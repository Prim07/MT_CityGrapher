package com.agh.bsct.algorithm.controllers.mapper;

import com.agh.bsct.algorithm.services.graph.Graph;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.models.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.graphdata.Colour;
import com.agh.bsct.api.models.graphdata.EdgeDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import com.agh.bsct.api.models.graphdata.NodeDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
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
                .hospitals(mapToHospitals(algorithmTask))
                .build();
    }

    private GraphDataDTO mapToGraphDataDTO(AlgorithmTask algorithmTask) {
        updateNodeColours(algorithmTask.getGraph(), algorithmTask.getGraphDataDTO());
        return algorithmTask.getGraphDataDTO();
    }

    private void updateNodeColours(Graph graph, GraphDataDTO graphDataDTO) {
        Map<GeographicalNodeDTO, Colour> geographicalNodeToNodeColour = graph.getIncidenceMap().keySet().stream()
                .collect(Collectors.toMap(GraphNode::getGeographicalNodeDTO, GraphNode::getNodeColour));

        for (NodeDTO nodeDTO : graphDataDTO.getNodeDTOS()) {
            Colour nodeColour = geographicalNodeToNodeColour.get(nodeDTO.getGeographicalNodeDTO());
            nodeDTO.setNodeColour(nodeColour);
        }

        for (EdgeDTO edgeDTO : graphDataDTO.getEdgeDTOS()) {
            Colour nodeColour = geographicalNodeToNodeColour.get(edgeDTO.getStartGeographicalNode());
            edgeDTO.setEdgeColour(nodeColour);
        }
    }

    private List<GeographicalNodeDTO> mapToHospitals(AlgorithmTask algorithmTask) {
        return algorithmTask.getHospitals()
                .orElse(Collections.emptyList())
                .stream()
                .map(GraphNode::getGeographicalNodeDTO)
                .collect(Collectors.toList());
    }

}
