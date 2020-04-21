package com.agh.bsct.datacollector.services.parser;

import com.agh.bsct.api.models.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.models.algorithmresult.AlgorithmResultWithVisualizationDataDTO;
import com.agh.bsct.api.models.algorithmresult.VisualizationDataDTO;
import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.graphdata.Colour;
import com.agh.bsct.api.models.graphdata.EdgeDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import com.agh.bsct.api.models.graphdata.NodeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataParser {

    private static final String ID_KEY = "id";
    private static final String IS_HOSPITAL_KEY = "isHospital";
    private static final String IS_CROSSING_KEY = "isCrossing";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String NODES_KEY = "nodes";
    private static final String WEIGHT_KEY = "weight";
    private static final String COLOUR_KEY = "colour";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AlgorithmResultWithVisualizationDataDTO parseToVisualizationDataDTO(AlgorithmResultDTO algorithmResultDTO) {
        ArrayList<ObjectNode> jsonStreets =
                getEdgesParsedToObjectNodes(algorithmResultDTO.getGraphData(), algorithmResultDTO.getHospitals());

        return AlgorithmResultWithVisualizationDataDTO.builder()
                .visualizationDataDTO(getVisualizationDataDTO(jsonStreets))
                .algorithmResultDTO(algorithmResultDTO)
                .build();
    }

    private VisualizationDataDTO getVisualizationDataDTO(ArrayList<ObjectNode> jsonStreets) {
        return VisualizationDataDTO.builder()
                .edges(jsonStreets)
                .build();
    }

    private ArrayList<ObjectNode> getEdgesParsedToObjectNodes(GraphDataDTO graphDataDTO,
                                                              List<GeographicalNodeDTO> hospitals) {
        var jsonStreets = new ArrayList<ObjectNode>();
        List<EdgeDTO> edgeDTOS = graphDataDTO.getEdgeDTOS();

        for (EdgeDTO edgeDTO : edgeDTOS) {
            ObjectNode jsonStreet = objectMapper.createObjectNode();
            jsonStreet.put(ID_KEY, edgeDTOS.indexOf(edgeDTO));
            jsonStreet.put(WEIGHT_KEY, edgeDTO.getWeight());
            jsonStreet.put(COLOUR_KEY, getNodeColourAsObjectNode(edgeDTO.getEdgeColour()));
            var streetNodesIds = edgeDTO.getStreetDTO().getNodesIds();
            var crossingDTOS = graphDataDTO.getNodeDTOS();
            ArrayList<ObjectNode> jsonNodes = getCrossingsParsedToObjectNodes(streetNodesIds, crossingDTOS, hospitals);
            jsonStreet.putArray(NODES_KEY).addAll(jsonNodes);
            jsonStreets.add(jsonStreet);
        }

        return jsonStreets;
    }

    private ArrayList<ObjectNode> getCrossingsParsedToObjectNodes(List<Long> streetNodesIds,
                                                                  List<NodeDTO> nodeDTOS,
                                                                  List<GeographicalNodeDTO> hospitals) {
        ArrayList<ObjectNode> jsonNodes = new ArrayList<>();

        for (Long nodeId : streetNodesIds) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            NodeDTO crossing = getCrossingWithGivenId(nodeId, nodeDTOS);
            jsonNode.put(ID_KEY, crossing.getGeographicalNodeDTO().getId());
            jsonNode.put(LATITUDE_KEY, crossing.getGeographicalNodeDTO().getLat());
            jsonNode.put(LONGITUDE_KEY, crossing.getGeographicalNodeDTO().getLon());
            jsonNode.put(IS_CROSSING_KEY, crossing.getGeographicalNodeDTO().isCrossing());
            jsonNode.put(IS_HOSPITAL_KEY, hospitals.contains(crossing.getGeographicalNodeDTO()));
            if (crossing.getNodeColour() != null) {
                jsonNode.put(COLOUR_KEY, getNodeColourAsObjectNode(crossing.getNodeColour()));
            }
            jsonNodes.add(jsonNode);
        }

        return jsonNodes;
    }

    private String getNodeColourAsObjectNode(Colour colour) {
        ObjectNode colourObjectNode = objectMapper.createObjectNode();
        colourObjectNode.put("R", colour.getR());
        colourObjectNode.put("G", colour.getG());
        colourObjectNode.put("B", colour.getB());
        return colourObjectNode.toString();
    }

    private NodeDTO getCrossingWithGivenId(Long nodeId, List<NodeDTO> nodeDTOS) {
        return nodeDTOS.stream()
                .filter(nodeDTO -> nodeDTO.getGeographicalNodeDTO().getId().equals(nodeId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find GraphNode with given taskId"));
    }

}
