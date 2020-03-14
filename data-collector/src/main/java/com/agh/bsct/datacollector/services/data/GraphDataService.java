package com.agh.bsct.datacollector.services.data;

import com.agh.bsct.api.entities.citydata.CityDataDTO;
import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.citydata.StreetDTO;
import com.agh.bsct.api.entities.graphdata.EdgeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Service
public class GraphDataService {

    private static final int EARTH_RADIUS = 6372800;

    public GraphDataDTO getGraphDataDTO(CityDataDTO cityData) {
        List<EdgeDTO> edges = calculateEdgeWeights(cityData.getStreets(), cityData.getGeographicalNodes());
        List<NodeDTO> crossings = calculateNodeWeights(cityData.getGeographicalNodes());
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

            edges.add(new EdgeDTO(street, weight));
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

    private List<NodeDTO> calculateNodeWeights(List<GeographicalNodeDTO> nodes) {
        return nodes.stream()
                .map(this::getCrossingWithNodeWeight)
                .collect(Collectors.toList());
    }

    private NodeDTO getCrossingWithNodeWeight(GeographicalNodeDTO node) {
        return new NodeDTO(node, 1);
    }
}
