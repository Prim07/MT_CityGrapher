package com.agh.bsct.algorithm.services.algorithms;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import com.agh.bsct.api.entities.graphdata.NodeDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrossingsService {


    public List<GeographicalNodeDTO> getGeographicalNodesForBestState(List<GraphNode> bestState,
                                                                      GraphDataDTO graphDataDTO) {
        List<Long> bestStateNodesIds = bestState.stream()
                .map(GraphNode::getId)
                .collect(Collectors.toList());
        return graphDataDTO.getNodeDTOS().stream()
                .map(NodeDTO::getGeographicalNodeDTO)
                .filter(geographicalNodeDTO -> bestStateNodesIds.contains(geographicalNodeDTO.getId()))
                .collect(Collectors.toList());
    }

}
