package com.agh.bsct.algorithm.controllers.mapper;

import com.agh.bsct.algorithm.services.graph.Graph;
import com.agh.bsct.algorithm.services.graph.GraphEdge;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GraphDataMapper {

    public Graph mapToGraph(GraphDataDTO graphData) {
        Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = createIncidenceMap(graphData);
        return new Graph(nodeToEdgesIncidenceMap);
    }

    private Map<GraphNode, List<GraphEdge>> createIncidenceMap(GraphDataDTO graphData) {
        Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = new HashMap<>();

        var edges = graphData.getEdgeDTOS();
        var crossings = graphData.getNodeDTOS();

        for (var edge : edges) {
            var edgeNodeIds = edge.getStreetDTO().getNodesIds();
            var edgeWeight = edge.getWeight();

            var firstNodeId = edgeNodeIds.get(0);
            var firstNode = getNodeForId(nodeToEdgesIncidenceMap, firstNodeId);
            if (firstNode == null) {
                var firstCrossing = getCrossingWithId(crossings, firstNodeId);
                firstNode = new GraphNode(firstNodeId, firstCrossing.getWeight());
            }

            var lastNodeId = edgeNodeIds.get(edgeNodeIds.size() - 1);
            var lastNode = getNodeForId(nodeToEdgesIncidenceMap, lastNodeId);
            if (lastNode == null) {
                var lastCrossing = getCrossingWithId(crossings, lastNodeId);
                lastNode = new GraphNode(lastNodeId, lastCrossing.getWeight());
            }

            var firstNodeEdges = nodeToEdgesIncidenceMap.computeIfAbsent(firstNode, node -> new ArrayList<>());
            firstNodeEdges.add(new GraphEdge(lastNode, edgeWeight));

            var lastNodeEdges = nodeToEdgesIncidenceMap.computeIfAbsent(lastNode, node -> new ArrayList<>());
            lastNodeEdges.add(new GraphEdge(firstNode, edgeWeight));
        }
        return nodeToEdgesIncidenceMap;
    }


    private GraphNode getNodeForId(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap, Long nodeId) {
        return nodeToEdgesIncidenceMap.keySet().stream()
                .filter(graphNode -> graphNode.getId().equals(nodeId))
                .findAny()
                .orElse(null);
    }

    private NodeDTO getCrossingWithId(List<NodeDTO> crossings, Long nodeId) {
        return crossings.stream()
                .filter(crossing -> crossing.getGeographicalNodeDTO().getId().equals(nodeId))
                .findAny()
                .orElseThrow(() ->
                        new IllegalStateException("Cannot find GeographicalNodeDTO for GraphNode with taskId: " + nodeId));
    }
}
