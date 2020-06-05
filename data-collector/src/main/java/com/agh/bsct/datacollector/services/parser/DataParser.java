package com.agh.bsct.datacollector.services.parser;

import com.agh.bsct.api.models.algorithmresult.*;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.graphdata.Colour;
import com.agh.bsct.api.models.graphdata.EdgeDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import com.agh.bsct.api.models.graphdata.NodeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DataParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FinalAlgorithmResultDTO parseToFinalAlgorithmResultDTO(AlgorithmResultDTO algorithmResultDTO) {
        var streetVisualizationDTOs =
                getStreetVisualizationDTOs(algorithmResultDTO.getGraphData(), algorithmResultDTO.getHospitals());

        return FinalAlgorithmResultDTO.builder()
                .visualizationDataDTO(getVisualizationDataDTO(streetVisualizationDTOs))
                .algorithmResultDTO(algorithmResultDTO)
                .algorithmResultInfoDTO(createAlgorithmResultInfoDTO(algorithmResultDTO, streetVisualizationDTOs))
                .build();
    }

    public VisualizationDataDTO getVisualizationDataDTOWithoutHospitals(GraphDataDTO graphDataDTO) {
        var streetVisualizationDTOs = getStreetVisualizationDTOs(graphDataDTO, Collections.emptyList());
        return getVisualizationDataDTO(streetVisualizationDTOs);
    }

    private VisualizationDataDTO getVisualizationDataDTO(List<StreetVisualizationDTO> jsonStreets) {
        return VisualizationDataDTO.builder()
                .edges(jsonStreets)
                .build();
    }

    private List<StreetVisualizationDTO> getStreetVisualizationDTOs(GraphDataDTO graphDataDTO,
                                                                    List<GeographicalNodeDTO> hospitals) {
        var streetVisualizationDTOs = new ArrayList<StreetVisualizationDTO>();
        var edgeDTOS = graphDataDTO.getEdgeDTOS();

        for (EdgeDTO edgeDTO : edgeDTOS) {
            var streetNodesIds = edgeDTO.getStreetDTO().getNodesIds();
            var crossingDTOS = graphDataDTO.getNodeDTOS();

            streetVisualizationDTOs.add(StreetVisualizationDTO.builder()
                    .id(edgeDTOS.indexOf(edgeDTO))
                    .weight(edgeDTO.getWeight())
                    .colour(getColourAsJson(edgeDTO.getEdgeColour()))
                    .nodes(getNodeVisualizationDTOs(streetNodesIds, crossingDTOS, hospitals))
                    .build());
        }

        return streetVisualizationDTOs;
    }

    private ArrayList<NodeVisualizationDTO> getNodeVisualizationDTOs(List<Long> streetNodesIds,
                                                                     List<NodeDTO> nodeDTOS,
                                                                     List<GeographicalNodeDTO> hospitals) {
        ArrayList<NodeVisualizationDTO> nodeVisualizationDTOs = new ArrayList<>();

        for (Long nodeId : streetNodesIds) {
            var crossing = getCrossingWithGivenId(nodeId, nodeDTOS);

            if (!crossing.isPresent()) {
                continue;
            }

            var crossingGeographicalNodeDTO = crossing.get().getGeographicalNodeDTO();

            nodeVisualizationDTOs.add(NodeVisualizationDTO.builder()
                    .id(crossingGeographicalNodeDTO.getId())
                    .lat(crossingGeographicalNodeDTO.getLat())
                    .lon(crossingGeographicalNodeDTO.getLon())
                    .isCrossing(crossingGeographicalNodeDTO.isCrossing())
                    .isHospital(hospitals.contains(crossingGeographicalNodeDTO))
                    .colour(getColourAsJson(crossing.get().getNodeColour()))
                    .build());
        }

        return nodeVisualizationDTOs;
    }

    private String getColourAsJson(Colour colour) {
        if (isColourIncorrect(colour)) {
            colour = Colour.createDefaultColour();
        }

        ObjectNode colourObjectNode = objectMapper.createObjectNode();
        colourObjectNode.put("R", colour.getR());
        colourObjectNode.put("G", colour.getG());
        colourObjectNode.put("B", colour.getB());
        return colourObjectNode.toString();
    }

    private boolean isColourIncorrect(Colour colour) {
        return colour == null || colour.getR() == null || colour.getG() == null || colour.getB() == null;
    }

    private Optional<NodeDTO> getCrossingWithGivenId(Long nodeId, List<NodeDTO> nodeDTOS) {
        return nodeDTOS.stream()
                .filter(nodeDTO -> nodeDTO.getGeographicalNodeDTO().getId().equals(nodeId))
                .findAny();
    }

    private AlgorithmResultInfoDTO createAlgorithmResultInfoDTO(AlgorithmResultDTO algorithmResultDTO,
                                                                List<StreetVisualizationDTO> streetVisualizationDTOs) {
        var graphDataDTO = algorithmResultDTO.getGraphData();

        return AlgorithmResultInfoDTO.builder()
                .numberOfNodes(getNumberOfNodes(streetVisualizationDTOs))
                .numberOfEdges(graphDataDTO.getEdgeDTOS().size())
                .fitnessScore(algorithmResultDTO.getFitnessScore())
                .build();
    }

    private long getNumberOfNodes(List<StreetVisualizationDTO> streetVisualizationDTOs) {
        return streetVisualizationDTOs.stream()
                .flatMap(streetVisualizationDTO -> streetVisualizationDTO.getNodes().stream())
                .filter(NodeVisualizationDTO::isCrossing)
                .map(NodeVisualizationDTO::getId)
                .distinct()
                .count();
    }
}
