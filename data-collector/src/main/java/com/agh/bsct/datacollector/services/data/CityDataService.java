package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.api.entities.citydata.CityDataDTO;
import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.citydata.StreetDTO;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import com.agh.bsct.datacollector.services.city.QueryForCityProvider;
import com.agh.bsct.datacollector.services.interpreter.QueryInterpreterService;
import com.agh.bsct.datacollector.services.result.filter.ResultFilterService;
import com.agh.bsct.datacollector.services.result.joiner.StreetsJoinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityDataService {

    private static final String NODE_TYPE = "node";

    private QueryForCityProvider queryForCityProvider;
    private QueryInterpreterService queryInterpreterService;
    private ResultFilterService resultFilterService;
    private StreetsJoinerService streetsJoinerService;

    @Autowired
    public CityDataService(QueryForCityProvider queryForCityProvider,
                           QueryInterpreterService queryInterpreterService,
                           ResultFilterService resultFilterService,
                           StreetsJoinerService streetsJoinerService) {
        this.queryForCityProvider = queryForCityProvider;
        this.queryInterpreterService = queryInterpreterService;
        this.resultFilterService = resultFilterService;
        this.streetsJoinerService = streetsJoinerService;
    }

    public CityDataDTO getCityDataDTO(String cityName) {
        String query = queryForCityProvider.getQueryForCity(cityName);

        OverpassQueryResult interpretedQuery = queryInterpreterService.interpret(query);
        OverpassQueryResult removedAreaTagsQueryResult = resultFilterService.removeAreaTags(interpretedQuery);

        Set<StreetDTO> streets = streetsJoinerService.joinStreets(removedAreaTagsQueryResult);

        return updateCrossings(streets, removedAreaTagsQueryResult);
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
                .forEach(nodeId -> nodeIdsToOccurrencesInStreets.merge(nodeId, 1, (a, b) -> a + b));

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
