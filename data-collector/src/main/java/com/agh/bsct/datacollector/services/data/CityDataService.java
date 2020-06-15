package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.api.models.citydata.CityDataDTO;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.citydata.StreetDTO;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import com.agh.bsct.datacollector.services.city.QueryForCityProvider;
import com.agh.bsct.datacollector.services.city.WaysDataThresholdImportanceLevel;
import com.agh.bsct.datacollector.services.database.DatabaseService;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreterService;
import com.agh.bsct.datacollector.services.result.filter.ResultFilterService;
import com.agh.bsct.datacollector.services.result.joiner.StreetsJoinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.agh.bsct.datacollector.services.dummylogger.DummyLogger.printMessage;

@Service
public class CityDataService {

    private static final String NODE_TYPE = "node";
    private static final Integer MAX_NUMBER_OF_WAYS = 4000;

    private final QueryForCityProvider queryForCityProvider;
    private final QueryInterpreterService queryInterpreterService;
    private final ResultFilterService resultFilterService;
    private final StreetsJoinerService streetsJoinerService;
    private final DatabaseService databaseService;

    @Autowired
    public CityDataService(QueryForCityProvider queryForCityProvider,
                           QueryInterpreterService queryInterpreterService,
                           ResultFilterService resultFilterService,
                           StreetsJoinerService streetsJoinerService,
                           DatabaseService databaseService) {
        this.queryForCityProvider = queryForCityProvider;
        this.queryInterpreterService = queryInterpreterService;
        this.resultFilterService = resultFilterService;
        this.streetsJoinerService = streetsJoinerService;
        this.databaseService = databaseService;
    }

    public CityDataDTO getCityDataDTO(String cityName) {
        if (databaseService.doesDataExist(cityName)) {
            return databaseService.getCityData(cityName);
        }

        return createCityData(cityName);
    }

    private CityDataDTO createCityData(String cityName) {
        printMessage(cityName + ": start finding importance level");
        var importanceLevel = getImportanceLevelOfWaysInsideCity(cityName);
        printMessage(cityName + ": found importance level: " + importanceLevel + ". Starting collecting data");
        String query = queryForCityProvider.getQueryForCity(cityName, importanceLevel);
        OverpassQueryResult interpretedQuery = queryInterpreterService.interpret(query);
        OverpassQueryResult removedAreaTagsQueryResult = resultFilterService.removeAreaTags(interpretedQuery);
        Set<StreetDTO> streets = streetsJoinerService.joinStreets(removedAreaTagsQueryResult);
        CityDataDTO cityDataDTO = updateCrossings(streets, removedAreaTagsQueryResult);

        databaseService.save(cityDataDTO, cityName);

        return cityDataDTO;
    }

    private WaysDataThresholdImportanceLevel getImportanceLevelOfWaysInsideCity(String cityName) {
        int numberOfWaysWithLowLevelImportance = getNumberOfWays(cityName, WaysDataThresholdImportanceLevel.LOW);
        if (numberOfWaysWithLowLevelImportance < MAX_NUMBER_OF_WAYS) {
            return WaysDataThresholdImportanceLevel.LOW;
        }

        int numberOfWaysWithMediumLevelImportance = getNumberOfWays(cityName, WaysDataThresholdImportanceLevel.MEDIUM);
        if (numberOfWaysWithMediumLevelImportance < MAX_NUMBER_OF_WAYS) {
            return WaysDataThresholdImportanceLevel.MEDIUM;
        }

        int numberOfWaysWithHighLevelImportance = getNumberOfWays(cityName, WaysDataThresholdImportanceLevel.HIGH);
        if (numberOfWaysWithHighLevelImportance < MAX_NUMBER_OF_WAYS) {
            return WaysDataThresholdImportanceLevel.HIGH;
        }

        return WaysDataThresholdImportanceLevel.VERY_HIGH;
    }

    private int getNumberOfWays(String cityName, WaysDataThresholdImportanceLevel importanceLevel) {
        var queryForCityCount = queryForCityProvider.getQueryForCityCount(cityName, importanceLevel);
        var result = queryInterpreterService.interpret(queryForCityCount);
        var elements = result.getElements();

        if (elements == null) {
            printMessage(cityName + ": null elements for level: " + importanceLevel);
            return Integer.MAX_VALUE;
        }

        int numberOfWays = Integer.parseInt(elements.get(0).getTags().getWays());
        printMessage(cityName + ": " + numberOfWays + " ways for level: " + importanceLevel);
        return numberOfWays;
    }

    private CityDataDTO updateCrossings(Set<StreetDTO> streets, OverpassQueryResult overpassQueryResult) {
        Map<Long, Integer> nodeIdToOccurrencesInStreetCount = getNodeIdToOccurrencesInStreetCountMap(streets);
        Set<Long> crossingsIds = getCrossingIds(nodeIdToOccurrencesInStreetCount, streets);
        List<StreetDTO> streetsAfterSplitting = getStreetsSeparatedOnCrossings(streets, crossingsIds);
        List<GeographicalNodeDTO> nodes = mapToNodes(overpassQueryResult);
        updateWithCrossingInformation(nodes, crossingsIds);
        return new CityDataDTO(nodes, streetsAfterSplitting);
    }

    private Map<Long, Integer> getNodeIdToOccurrencesInStreetCountMap(Set<StreetDTO> streets) {
        Map<Long, Integer> nodeIdsToOccurrencesInStreets = new HashMap<>();
        streets.stream()
                .map(StreetDTO::getNodesIds)
                .flatMap(Collection::stream)
                .forEach(nodeId -> nodeIdsToOccurrencesInStreets.merge(nodeId, 1, Integer::sum));

        return nodeIdsToOccurrencesInStreets;
    }

    private Set<Long> getCrossingIds(Map<Long, Integer> nodeIdToOccurrencesInStreetCount, Set<StreetDTO> streets) {
        Set<Long> crossingsIds = nodeIdToOccurrencesInStreetCount.entrySet().stream()
                .filter(this::isNodesOccurrenceGreaterThanOne)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        addFirstAndLastNodeForEachStreet(streets, crossingsIds);

        return crossingsIds;
    }

    private void addFirstAndLastNodeForEachStreet(Set<StreetDTO> streets, Set<Long> crossingsIds) {
        for (StreetDTO street : streets) {
            var nodesIds = street.getNodesIds();

            var firstNodeId = nodesIds.get(0);
            var lastNodeId = nodesIds.get(nodesIds.size() - 1);

            crossingsIds.add(firstNodeId);
            crossingsIds.add(lastNodeId);
        }
    }

    private boolean isNodesOccurrenceGreaterThanOne(Map.Entry<Long, Integer> entry) {
        return entry.getValue() > 1;
    }

    private List<StreetDTO> getStreetsSeparatedOnCrossings(Set<StreetDTO> streets, Set<Long> crossingsIds) {
        var streetsSeparatedOnCrossings = new ArrayList<StreetDTO>();

        for (StreetDTO street : streets) {
            List<Long> nodesIds = street.getNodesIds();
            var crossingWithinStreet = new ArrayList<Long>();
            for (int i = 1; i < nodesIds.size() - 1; i++) {
                Long nodeId = nodesIds.get(i);
                if (crossingsIds.contains(nodeId)) {
                    crossingWithinStreet.add(nodeId);
                }
            }
            if (crossingWithinStreet.size() == 0) {
                streetsSeparatedOnCrossings.add(street);
            } else {
                streetsSeparatedOnCrossings.addAll(splitStreet(street, crossingWithinStreet));
            }
        }

        return streetsSeparatedOnCrossings;
    }

    private List<StreetDTO> splitStreet(StreetDTO street, List<Long> middleNodeIds) {
        ArrayList<List<Long>> listOfSplitStreetsNodes = getInitializedWithEmptyLists(middleNodeIds.size() + 1);
        splitNodesOfBaseStreet(street, middleNodeIds, listOfSplitStreetsNodes);
        return mapToStreets(street, listOfSplitStreetsNodes);
    }

    private ArrayList<List<Long>> getInitializedWithEmptyLists(int size) {
        var splitStreetsNodes = new ArrayList<List<Long>>(size);
        for (int i = 0; i < size; i++) {
            splitStreetsNodes.add(i, new ArrayList<>());
        }
        return splitStreetsNodes;
    }

    private void splitNodesOfBaseStreet(StreetDTO street, List<Long> middleNodeIds,
                                        ArrayList<List<Long>> listOfSplitStreetsNodes) {
        var streetPartId = 0;
        for (Long nodeId : street.getNodesIds()) {
            if (middleNodeIds.contains(nodeId)) {
                listOfSplitStreetsNodes.get(streetPartId).add(nodeId);
                listOfSplitStreetsNodes.get(streetPartId + 1).add(nodeId);
                streetPartId++;
            } else {
                listOfSplitStreetsNodes.get(streetPartId).add(nodeId);
            }
        }
    }

    private List<StreetDTO> mapToStreets(StreetDTO street, ArrayList<List<Long>> listOfSplitStreetsNodes) {
        return listOfSplitStreetsNodes
                .stream()
                .map(nodeIds -> new StreetDTO(street.getName(), nodeIds))
                .collect(Collectors.toList());
    }

    private List<GeographicalNodeDTO> mapToNodes(OverpassQueryResult overpassQueryResult) {
        return overpassQueryResult.getElements().stream()
                .filter(element -> NODE_TYPE.equals(element.getType()))
                .map(element -> new GeographicalNodeDTO(element.getId(), element.getLon(), element.getLat()))
                .collect(Collectors.toList());
    }

    private void updateWithCrossingInformation(List<GeographicalNodeDTO> nodes, Set<Long> crossingsIds) {
        nodes.forEach(node -> {
            if (crossingsIds.contains(node.getId())) {
                node.setCrossing(true);
            }
        });
    }

}
