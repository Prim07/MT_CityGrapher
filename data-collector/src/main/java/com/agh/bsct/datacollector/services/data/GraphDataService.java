package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.api.models.citydata.CityDataDTO;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.citydata.StreetDTO;
import com.agh.bsct.api.models.graphdata.EdgeDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import com.agh.bsct.api.models.graphdata.NodeDTO;
import com.agh.bsct.api.models.taskinput.NodesPrioritiesDTO;
import com.agh.bsct.api.models.taskinput.PrioritizedNodeDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Service
public class GraphDataService {

    private static final int EARTH_RADIUS = 6372800;
    private static final double DEFAULT_NOT_CROSSING_NODE_WEIGHT = 0;
    private static final double DEFAULT_NODE_WEIGHT = 1;

    public GraphDataDTO getGraphDataDTO(CityDataDTO cityData, NodesPrioritiesDTO nodesPriorities) {
        List<EdgeDTO> edges = calculateEdgeWeights(cityData.getStreets(), cityData.getGeographicalNodes());
        List<NodeDTO> crossings = calculateNodeWeights(cityData.getGeographicalNodes(), nodesPriorities);
        return new GraphDataDTO(edges, crossings);
    }

    private List<EdgeDTO> calculateEdgeWeights(List<StreetDTO> streets, List<GeographicalNodeDTO> nodes) {
        List<EdgeDTO> edges = new ArrayList<>();

        for (StreetDTO street : streets) {
            double weight = 0;
            List<Long> nodesIds = street.getNodesIds();

            for (int i = 0; i < nodesIds.size() - 1; i++) {
                Long startNodeId = nodesIds.get(i);
                Long endNodeId = nodesIds.get(i + 1);

                GeographicalNodeDTO startNode = getNodeWithId(startNodeId, nodes);
                GeographicalNodeDTO endNode = getNodeWithId(endNodeId, nodes);

                weight += calculateDistance(startNode, endNode);
            }

            edges.add(new EdgeDTO(street, weight, getNodeWithId(nodesIds.get(0), nodes)));
        }

        return edges;
    }

    private double calculateDistance(GeographicalNodeDTO startNode, GeographicalNodeDTO endNode) {
        double startLat = startNode.getLat();
        double endLat = endNode.getLat();
        double startLon = startNode.getLon();
        double endLon = endNode.getLon();

        double startLatRad = toRadians(startLat);
        double endLatRad = toRadians(endLat);
        double latRad = toRadians(endLat - startLat);
        double lonRad = toRadians(endLon - startLon);

        return 2.0 * EARTH_RADIUS
                * asin(sqrt(pow(sin(latRad / 2), 2) + cos(startLatRad) * cos(endLatRad) * pow(sin(lonRad / 2), 2)));
    }

    private GeographicalNodeDTO getNodeWithId(Long nodeId, List<GeographicalNodeDTO> nodes) {
        return nodes.stream()
                .filter(node -> node.getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given id: " + nodeId));
    }

    private List<NodeDTO> calculateNodeWeights(List<GeographicalNodeDTO> nodes,
                                               NodesPrioritiesDTO nodesPrioritiesDTO) {
        if (areNoneOfWeightsUsed(nodesPrioritiesDTO)) {
            return getEquallyWeightedNodes(nodes);
        }

        removeNotCrossingNodes(nodes, nodesPrioritiesDTO);
        double normalizedValue = getNormalizedValue(nodesPrioritiesDTO);

        return nodes.stream()
                .map(node -> getNodeWithCalculatedWeight(node, nodesPrioritiesDTO, normalizedValue))
                .collect(Collectors.toList());
    }

    private List<NodeDTO> getEquallyWeightedNodes(List<GeographicalNodeDTO> nodes) {
        return nodes.stream()
                .map(geographicalNodeDTO -> new NodeDTO(geographicalNodeDTO, DEFAULT_NODE_WEIGHT))
                .collect(Collectors.toList());
    }

    private void removeNotCrossingNodes(List<GeographicalNodeDTO> nodes, NodesPrioritiesDTO nodesPrioritiesDTO) {
        List<PrioritizedNodeDTO> prioritizedCrossingNodes = nodesPrioritiesDTO.getPrioritizedNodes().stream()
                .filter(prioritizedNodeDTO -> isPrioritizedNodeCrossing(nodes, prioritizedNodeDTO))
                .collect(Collectors.toList());
        nodesPrioritiesDTO.setPrioritizedNodes(prioritizedCrossingNodes);
    }

    private boolean isPrioritizedNodeCrossing(List<GeographicalNodeDTO> nodes, PrioritizedNodeDTO prioritizedNodeDTO) {
        return nodes.stream()
                .filter(node -> node.getId().equals(prioritizedNodeDTO.getGeographicalNodeId()))
                .findAny()
                .map(GeographicalNodeDTO::isCrossing)
                .orElse(false);
    }

    private double getNormalizedValue(NodesPrioritiesDTO nodesPrioritiesDTO) {
        if (areNoneOfWeightsUsed(nodesPrioritiesDTO)) {
            return 1;
        }

        List<PrioritizedNodeDTO> prioritizedNodes = nodesPrioritiesDTO.getPrioritizedNodes();

        if (areOnlyManualWeightsUsed(nodesPrioritiesDTO)) {
            return calculateSumOfManualWeights(prioritizedNodes);
        }

        if (areOnlyVoronoiWeightsUsed(nodesPrioritiesDTO)) {
            return calculateSumOfVoronoiWeights(prioritizedNodes);
        }

        return calculateSumOfBothWeights(prioritizedNodes);
    }

    private NodeDTO getNodeWithCalculatedWeight(GeographicalNodeDTO node, NodesPrioritiesDTO nodesPrioritiesDTO,
                                                double normalizedValue) {
        List<PrioritizedNodeDTO> prioritizedNodes = nodesPrioritiesDTO.getPrioritizedNodes();

        if (!node.isCrossing()) {
            return new NodeDTO(node, DEFAULT_NOT_CROSSING_NODE_WEIGHT);
        }

        var prioritizedNode = getPrioritizedNode(node, prioritizedNodes);
        double weight = calculateWeight(nodesPrioritiesDTO, prioritizedNode, normalizedValue);

        return new NodeDTO(node, weight);
    }

    private double calculateWeight(NodesPrioritiesDTO nodesPrioritiesDTO,
                                   PrioritizedNodeDTO prioritizedNode,
                                   double normalizedValue) {
        if (areNoneOfWeightsUsed(nodesPrioritiesDTO)) {
            return DEFAULT_NODE_WEIGHT;
        }

        if (areOnlyManualWeightsUsed(nodesPrioritiesDTO)) {
            return prioritizedNode.getManualWeight() / normalizedValue;
        }

        if (areOnlyVoronoiWeightsUsed(nodesPrioritiesDTO)) {
            return prioritizedNode.getVoronoiWeight() / normalizedValue;
        }

        return prioritizedNode.getManualWeight() * prioritizedNode.getVoronoiWeight() / normalizedValue;
    }

    private boolean areNoneOfWeightsUsed(NodesPrioritiesDTO nodesPrioritiesDTO) {
        return !nodesPrioritiesDTO.areManualWeightsUsed() && !nodesPrioritiesDTO.areVoronoiWeightsUsed();
    }

    private boolean areOnlyManualWeightsUsed(NodesPrioritiesDTO nodesPrioritiesDTO) {
        return nodesPrioritiesDTO.areManualWeightsUsed() && !nodesPrioritiesDTO.areVoronoiWeightsUsed();
    }

    private double calculateSumOfManualWeights(List<PrioritizedNodeDTO> prioritizedNodes) {
        return prioritizedNodes.stream()
                .mapToDouble(PrioritizedNodeDTO::getManualWeight)
                .sum();
    }

    private boolean areOnlyVoronoiWeightsUsed(NodesPrioritiesDTO nodesPrioritiesDTO) {
        return !nodesPrioritiesDTO.areManualWeightsUsed() && nodesPrioritiesDTO.areVoronoiWeightsUsed();
    }

    private double calculateSumOfVoronoiWeights(List<PrioritizedNodeDTO> prioritizedNodes) {
        return prioritizedNodes.stream()
                .mapToDouble(PrioritizedNodeDTO::getVoronoiWeight)
                .filter(this::isPositive)
                .sum();
    }

    private double calculateSumOfBothWeights(List<PrioritizedNodeDTO> prioritizedNodes) {
        return prioritizedNodes.stream()
                .map(this::multiplyBothWeights)
                .mapToDouble(Double::doubleValue)
                .filter(this::isPositive)
                .sum();
    }

    private boolean isPositive(double v) {
        return v > 0;
    }

    private double multiplyBothWeights(PrioritizedNodeDTO prioritizedNodeDTO) {
        return prioritizedNodeDTO.getManualWeight() * prioritizedNodeDTO.getVoronoiWeight();
    }

    private PrioritizedNodeDTO getPrioritizedNode(GeographicalNodeDTO node, List<PrioritizedNodeDTO> prioritizedNodes) {
        return prioritizedNodes.stream()
                .filter(prioritizedNodeDTO -> prioritizedNodeDTO.getGeographicalNodeId().equals(node.getId()))
                .findAny()
                .orElse(getPrioritizedNodeFromOutsideLargestConnectedComponent(node.getId()));
    }

    private PrioritizedNodeDTO getPrioritizedNodeFromOutsideLargestConnectedComponent(Long id) {
        return new PrioritizedNodeDTO(id, 0D, 0D);
    }
}
