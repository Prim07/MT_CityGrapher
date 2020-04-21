package com.agh.bsct.algorithm.services.colours;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
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
        setHospitalColours(hospitalToClosestNodes.keySet());

        hospitalToClosestNodes.forEach((hospital, closestNodes) ->
                closestNodes.forEach(closestNode -> closestNode.setNodeColour(hospital.getNodeColour())));
    }

    /*public void updateColoursInEdges(AlgorithmTask algorithmTask) {
        if (!algorithmTask.getHospitals().isPresent()) {
            return;
        }

        algorithmTask.getGraph().getIncidenceMap().forEach((graphNode, graphEdges) ->
                graphEdges.forEach(edge -> edge.setEdgeColour(graphNode.getNodeColour())));
    }*/

    private HashMap<GraphNode, Set<GraphNode>> buildHospitalToClosestNodes(AlgorithmTask algorithmTask,
                                                                           Map<Long, Map<Long, Double>> shortestPathsDistances,
                                                                           List<GraphNode> hospitals) {
        var hospitalToClosestNodes = new HashMap<GraphNode, Set<GraphNode>>();

        for (GraphNode currentNode : algorithmTask.getGraph().getIncidenceMap().keySet()) {
            Map<Long, Double> distancesFromCurrentNode = shortestPathsDistances.get(currentNode.getId());
            GraphNode closestHospital = getClosestHospital(hospitals, distancesFromCurrentNode);
            updateHospitalToClosestNodes(hospitalToClosestNodes, currentNode, closestHospital);
        }
        return hospitalToClosestNodes;
    }

    private GraphNode getClosestHospital(List<GraphNode> hospitals,
                                         Map<Long, Double> distancesFromCurrentNode) {
        AtomicReference<GraphNode> closestHospital = new AtomicReference<>(hospitals.get(0));
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
                                      GraphNode closestHospital,
                                      GraphNode currentHospital) {
        Double shortestDistance = distancesFromCurrentNode.get(closestHospital.getId());
        Double currentDistance = distancesFromCurrentNode.get(currentHospital.getId());

        return currentDistance < shortestDistance;
    }

    private void updateHospitalToClosestNodes(Map<GraphNode, Set<GraphNode>> hospitalToClosestNodes,
                                              GraphNode currentNode,
                                              GraphNode closestHospital) {
        if (!hospitalToClosestNodes.containsKey(closestHospital)) {
            hospitalToClosestNodes.put(closestHospital, new HashSet<>());
        }

        hospitalToClosestNodes.get(closestHospital).add(currentNode);
    }

    private void setHospitalColours(Set<GraphNode> hospitalsSet) {
        List<GraphNode> hospitals = new ArrayList<>(hospitalsSet);
        int numberOfAvailableColours = Colours.COLOURS.length;

        IntStream.range(0, hospitals.size()).forEach(i ->
                hospitals.get(i).setNodeColour(Colours.COLOURS[i % numberOfAvailableColours]));
    }
}
