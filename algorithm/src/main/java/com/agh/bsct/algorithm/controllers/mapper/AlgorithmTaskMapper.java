package com.agh.bsct.algorithm.controllers.mapper;

import com.agh.bsct.algorithm.services.graph.Graph;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.models.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import com.agh.bsct.api.models.graphdata.NodeColour;
import com.agh.bsct.api.models.graphdata.NodeDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AlgorithmTaskMapper {

    public AlgorithmResultDTO mapToAlgorithmResultDTO(AlgorithmTask algorithmTask) {
        return AlgorithmResultDTO.builder()
                .taskId(algorithmTask.getTaskId())
                .status(algorithmTask.getStatus().toString())
                .graphData(mapToGraphDataDTO(algorithmTask))
                .calculatingShortestPathsProgress(algorithmTask.getCalculatingShortestPathsProgress())
                .hospitals(algorithmTask.getHospitals().orElse(Collections.emptyList()))
                .build();
    }

    private GraphDataDTO mapToGraphDataDTO(AlgorithmTask algorithmTask) {
        updateNodeColours(algorithmTask.getGraph(), algorithmTask.getGraphDataDTO());
        return algorithmTask.getGraphDataDTO();
    }

    private void updateNodeColours(Graph graph, GraphDataDTO graphDataDTO) {
        Map<GeographicalNodeDTO, NodeColour> geographicalNodeToNodeColour = graph.getIncidenceMap().keySet().stream()
                .collect(Collectors.toMap(GraphNode::getGeographicalNodeDTO, GraphNode::getNodeColour));

        for (NodeDTO nodeDTO : graphDataDTO.getNodeDTOS()) {
            NodeColour nodeColour = geographicalNodeToNodeColour.get(nodeDTO.getGeographicalNodeDTO());
            nodeDTO.setNodeColour(nodeColour);
        }
    }

}
