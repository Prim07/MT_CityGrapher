package com.agh.bsct.algorithm.services.colours;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.graphdata.NodeColour;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Service
public class ColoursService {

    public void updateColoursInNodes(AlgorithmTask algorithmTask, Map<Long, Map<Long, Double>> shortestPathsDistances) {
        if (!algorithmTask.getHospitals().isPresent()) {
            return;
        }

        var hospitals = algorithmTask.getHospitals().get();
        var hospitalToClosestNodes = buildHospitalToClosestNodes(algorithmTask, shortestPathsDistances, hospitals);
        var hospitalToColour = buildHospitalToColour(hospitalToClosestNodes.keySet());

        hospitalToClosestNodes.forEach((hospital, closestNodes) -> closestNodes
                .forEach(graphNode -> graphNode.setNodeColour(hospitalToColour.get(hospital))));
    }

    private HashMap<GeographicalNodeDTO, Set<GraphNode>> buildHospitalToClosestNodes(AlgorithmTask algorithmTask,
                                                                                     Map<Long, Map<Long, Double>> shortestPathsDistances, List<GeographicalNodeDTO> hospitals) {
        var hospitalToClosestNodes = new HashMap<GeographicalNodeDTO, Set<GraphNode>>();

        for (GraphNode currentNode : algorithmTask.getGraph().getIncidenceMap().keySet()) {
            Map<Long, Double> distancesFromCurrentNode = shortestPathsDistances.get(currentNode.getId());
            GeographicalNodeDTO closestHospital = getClosestHospital(hospitals, distancesFromCurrentNode);
            updateHospitalToClosestNodes(hospitalToClosestNodes, currentNode, closestHospital);
        }
        return hospitalToClosestNodes;
    }

    private void updateHospitalToClosestNodes(Map<GeographicalNodeDTO, Set<GraphNode>> hospitalToClosestNodes,
                                              GraphNode currentNode,
                                              GeographicalNodeDTO closestHospital) {
        if (!hospitalToClosestNodes.containsKey(closestHospital)) {
            hospitalToClosestNodes.put(closestHospital, new HashSet<>());
        }

        hospitalToClosestNodes.get(closestHospital).add(currentNode);
    }

    private GeographicalNodeDTO getClosestHospital(List<GeographicalNodeDTO> hospitals,
                                                   Map<Long, Double> distancesFromCurrentNode) {
        AtomicReference<GeographicalNodeDTO> closestHospital = new AtomicReference<>(hospitals.get(0));
        hospitals.stream()
                .skip(0)
                .forEach(currentHospital -> {
                    if (isDistanceShorter(distancesFromCurrentNode, closestHospital.get(), currentHospital)) {
                        closestHospital.set(currentHospital);
                    }
                });

        return closestHospital.get();
    }

    private boolean isDistanceShorter(Map<Long, Double> distancesFromCurrentNode,
                                      GeographicalNodeDTO closestHospital,
                                      GeographicalNodeDTO currentHospital) {
        Double shortestDistance = distancesFromCurrentNode.get(closestHospital.getId());
        Double currentDistance = distancesFromCurrentNode.get(currentHospital.getId());

        return currentDistance < shortestDistance;
    }

    private Map<GeographicalNodeDTO, NodeColour> buildHospitalToColour(Set<GeographicalNodeDTO> hospitalsSet) {
        List<GeographicalNodeDTO> hospitals = new ArrayList<>(hospitalsSet);
        int numberOfAvailableColours = Colours.COLOURS.length;
        Map<GeographicalNodeDTO, NodeColour> hospitalToColour = new HashMap<>();

        IntStream.range(0, hospitals.size()).forEach(i ->
                hospitalToColour.put(hospitals.get(i), Colours.COLOURS[i % numberOfAvailableColours]));

        return hospitalToColour;

    }
}
