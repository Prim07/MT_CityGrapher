package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.api.models.citydata.CityDataDTO;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.citydata.StreetDTO;
import com.agh.bsct.api.models.graphdata.EdgeDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import com.agh.bsct.api.models.graphdata.NodeDTO;
import com.agh.bsct.api.models.taskinput.PrioritizedNodeDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Service
public class GraphDataService {

    private static final int EARTH_RADIUS = 6372800;
    private static final int DEFAULT_NODE_WEIGHT = 1;

    public GraphDataDTO getGraphDataDTO(CityDataDTO cityData, List<PrioritizedNodeDTO> prioritizedNodes) {
        List<EdgeDTO> edges = calculateEdgeWeights(cityData.getStreets(), cityData.getGeographicalNodes());
        List<NodeDTO> crossings = calculateNodeWeights(cityData.getGeographicalNodes(), prioritizedNodes);
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
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given taskId: " + nodeId));
    }

    private List<NodeDTO> calculateNodeWeights(List<GeographicalNodeDTO> nodes,
                                               List<PrioritizedNodeDTO> prioritizedNodes) {
        return nodes.stream()
                .map(node -> getCrossingWithNodeWeight(node, prioritizedNodes))
                .collect(Collectors.toList());
    }

    private NodeDTO getCrossingWithNodeWeight(GeographicalNodeDTO node, List<PrioritizedNodeDTO> prioritizedNodes) {
        AtomicInteger nodeWeight = new AtomicInteger(0);

        prioritizedNodes.stream()
                .filter(prioritizedNodeDTO -> isNodeInPrioritizedGroup(node, prioritizedNodeDTO))
                .forEach(prioritizedNodeDTO -> nodeWeight.addAndGet(prioritizedNodeDTO.getPriorityValue().intValue()));

        if (isNotNodePrioritized(nodeWeight)) {
            nodeWeight.set(DEFAULT_NODE_WEIGHT);
        }

        return new NodeDTO(node, nodeWeight.get());
    }

    private boolean isNotNodePrioritized(AtomicInteger nodeWeight) {
        return nodeWeight.get() == 0;
    }

    private boolean isNodeInPrioritizedGroup(GeographicalNodeDTO node, PrioritizedNodeDTO prioritizedNodeDTO) {
        return prioritizedNodeDTO.getGeographicalNodeDTOIds().stream()
                .anyMatch(id -> node.getId().equals(id));
    }
}
