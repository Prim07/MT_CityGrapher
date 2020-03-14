package com.agh.bsct.algorithm.services.graphdata;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.api.entities.graphdata.EdgeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphDataService {

    public void replaceGraphWithItsLargestConnectedComponent(GraphDataDTO graphData,
                                                             List<GraphNode> largestConnectedComponentNodes) {
        //we don't have to remove anything from nodes collection because we want to draw only edges
        var edges = graphData.getEdgeDTOS();
        edges.removeIf(edge -> !shouldEdgeBeKept(edge, largestConnectedComponentNodes));
    }

    private boolean shouldEdgeBeKept(EdgeDTO edge, List<GraphNode> largestConnectedComponentNodes) {
        List<Long> nodesIds = edge.getStreetDTO().getNodesIds();
        return shouldNodeBeKept(nodesIds.get(0), largestConnectedComponentNodes)
                && shouldNodeBeKept(nodesIds.get(nodesIds.size() - 1), largestConnectedComponentNodes);
    }

    private boolean shouldNodeBeKept(Long id, List<GraphNode> largestConnectedComponentNodes) {
        return largestConnectedComponentNodes.stream().anyMatch(graphNode -> graphNode.getId().equals(id));
    }
}
