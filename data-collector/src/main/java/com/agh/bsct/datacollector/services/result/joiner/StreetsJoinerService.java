package com.agh.bsct.datacollector.services.result.joiner;

import com.agh.bsct.api.entities.citydata.StreetDTO;
import com.agh.bsct.datacollector.library.adapter.queryresult.Element;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StreetsJoinerService {

    private static final String WAY_TYPE = "way";
    private static final String NEXT_PART_OF_STREET_POSTFIX = "_part";
    private static final String UNNAMED_STREET_NAME = "unnamed";

    public Set<StreetDTO> joinStreets(OverpassQueryResult queryResult) {
        Map<String, Set<StreetDTO>> streetNameToStreets = getStreetNameToStreets(queryResult);
        Set<StreetDTO> streets = getStreets(streetNameToStreets, new HashSet<>());
        return getStreetsWithoutDuplicateNodes(streets);
    }

    private HashMap<String, Set<StreetDTO>> getStreetNameToStreets(OverpassQueryResult queryResult) {
        var iterator = queryResult.getElements().iterator();
        var streetNameToStreets = new HashMap<String, Set<StreetDTO>>();

        while (iterator.hasNext()) {
            Element currentElement = iterator.next();
            if (isCorrectWay(currentElement)) {
                String name = getStreetName(currentElement);
                if (!streetNameToStreets.containsKey(name)) {
                    streetNameToStreets.put(name, new HashSet<>());
                }
                var nodes = currentElement.getNodesAsArrayList();
                if (nodes != null) {
                    Set<StreetDTO> streets = streetNameToStreets.get(name);
                    streets.add(new StreetDTO(name, new ArrayList<>(nodes)));
                }
            }
        }

        return streetNameToStreets;
    }

    private String getStreetName(Element currentElement) {
        var name = currentElement.getTags().getName();
        return (name == null) ? UNNAMED_STREET_NAME : name;
    }

    private boolean isCorrectWay(Element currentElement) {
        return WAY_TYPE.equals(currentElement.getType());
    }

    private Set<StreetDTO> getStreets(Map<String, Set<StreetDTO>> streetNameToStreets, Set<StreetDTO> streets) {
        for (Map.Entry<String, Set<StreetDTO>> currentStreetNameToStreetsPart : streetNameToStreets.entrySet()) {
            Set<StreetDTO> currentEntryStreets = currentStreetNameToStreetsPart.getValue();

            List<Long> orderedJoinedNodes = initializeOrderedJoinedNodesWithFirstPart(currentEntryStreets);

            var maxNumberOfIterations = currentEntryStreets.size();
            var iterationsCounter = 0;

            while (isNextIterationAvailable(currentEntryStreets, maxNumberOfIterations, iterationsCounter)) {
                iterationsCounter++;
                orderedJoinedNodes = joinNewNodesPartToOrderedJoinedNodes(currentEntryStreets, orderedJoinedNodes);
            }

            String streetName = currentStreetNameToStreetsPart.getKey();

            if (isStreetBuiltOfManyParts(currentEntryStreets)) {
                streets = getMapForRemainingStreets(currentEntryStreets, streets, streetName);
            }

            streets.add(new StreetDTO(streetName, new ArrayList<>(orderedJoinedNodes)));
        }

        return streets;
    }

    private List<Long> initializeOrderedJoinedNodesWithFirstPart(Set<StreetDTO> currentEntryStreets) {
        Iterator<StreetDTO> currentEntryStreetsIterator = currentEntryStreets.iterator();
        var orderedJoinedNodes = new ArrayList<>(currentEntryStreetsIterator.next().getNodesIds());
        currentEntryStreetsIterator.remove();
        return orderedJoinedNodes;
    }

    private boolean isNextIterationAvailable(Set<StreetDTO> streets, int maxNumberOfIterations, int iterationsCounter) {
        return isStreetBuiltOfManyParts(streets) && iterationsCounter < maxNumberOfIterations;
    }

    private boolean isStreetBuiltOfManyParts(Set<StreetDTO> streets) {
        return !streets.isEmpty();
    }

    private List<Long> joinNewNodesPartToOrderedJoinedNodes(Set<StreetDTO> currentEntryStreets,
                                                            List<Long> orderedNodes) {
        var currentEntryListOfNodesIterator = currentEntryStreets.iterator();

        while (currentEntryListOfNodesIterator.hasNext()) {
            Long firstJoinedNodeId = orderedNodes.get(0);
            Long lastJoinedNodeId = orderedNodes.get(orderedNodes.size() - 1);

            StreetDTO street = currentEntryListOfNodesIterator.next();
            List<Long> streetNodesIds = street.getNodesIds();
            Long firstCandidateNodeToJoinId = streetNodesIds.get(0);
            Long lastCandidateNodeToJoinId = streetNodesIds.get(streetNodesIds.size() - 1);

            if (firstCandidateNodeToJoinId.equals(firstJoinedNodeId)) {
                orderedNodes = joinNodesConnectedInFirstPositions(orderedNodes, streetNodesIds);
            } else if (firstCandidateNodeToJoinId.equals(lastJoinedNodeId)) {
                orderedNodes = joinNodesConnectedByFirstCandidateAndLastOrdered(orderedNodes, streetNodesIds);
            } else if (lastCandidateNodeToJoinId.equals(firstJoinedNodeId)) {
                orderedNodes = joinNodesConnectedByLastCandidateAndFirstOrdered(orderedNodes, streetNodesIds);
            } else if (lastCandidateNodeToJoinId.equals(lastJoinedNodeId)) {
                orderedNodes = joinNodesConnectedInLastPositions(orderedNodes, streetNodesIds);
            } else {
                continue;
            }

            currentEntryListOfNodesIterator.remove();
        }

        return orderedNodes;
    }

    private List<Long> joinNodesConnectedInFirstPositions(List<Long> orderedJoinedNodes,
                                                          List<Long> candidateNodesToJoin) {
        Collections.reverse(orderedJoinedNodes);
        orderedJoinedNodes.addAll(candidateNodesToJoin);
        return orderedJoinedNodes;
    }

    private List<Long> joinNodesConnectedByFirstCandidateAndLastOrdered(List<Long> orderedJoinedNodes,
                                                                        List<Long> candidateNodesToJoin) {
        orderedJoinedNodes.addAll(candidateNodesToJoin);
        return orderedJoinedNodes;
    }

    private List<Long> joinNodesConnectedByLastCandidateAndFirstOrdered(List<Long> orderedJoinedNodes,
                                                                        List<Long> candidateNodesToJoin) {
        var orderedJoinedNodesCopy = new ArrayList<>(orderedJoinedNodes);
        orderedJoinedNodes = new ArrayList<>(candidateNodesToJoin);
        orderedJoinedNodes.addAll(orderedJoinedNodesCopy);
        return orderedJoinedNodes;
    }

    private List<Long> joinNodesConnectedInLastPositions(List<Long> orderedJoinedNodes,
                                                         List<Long> candidateNodesToJoin) {
        Collections.reverse(candidateNodesToJoin);
        orderedJoinedNodes.addAll(candidateNodesToJoin);
        return orderedJoinedNodes;
    }

    private Set<StreetDTO> getMapForRemainingStreets(Set<StreetDTO> currentEntryStreets, Set<StreetDTO> streets,
                                                     String streetName) {
        HashMap<String, Set<StreetDTO>> remainingStreetNameToStreets = new HashMap<>();
        remainingStreetNameToStreets.put(streetName + NEXT_PART_OF_STREET_POSTFIX, currentEntryStreets);
        streets = getStreets(remainingStreetNameToStreets, streets);
        return streets;
    }

    private Set<StreetDTO> getStreetsWithoutDuplicateNodes(Set<StreetDTO> streets) {
        return streets.stream()
                .map(street -> new StreetDTO(street.getName(), mapToNodesWithoutDuplicates(street.getNodesIds())))
                .collect(Collectors.toSet());
    }

    private List<Long> mapToNodesWithoutDuplicates(List<Long> nodesIds) {
        return new ArrayList<>(new LinkedHashSet<>(nodesIds));
    }
}
