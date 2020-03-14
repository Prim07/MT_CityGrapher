package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.entities.taskinput.TaskInputDTO;
import com.agh.bsct.datacollector.services.algorithm.boundary.AlgorithmService;
import com.agh.bsct.datacollector.services.data.CityDataService;
import com.agh.bsct.datacollector.services.data.GraphDataService;
import com.agh.bsct.datacollector.services.parser.DataParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OSMCityService {

    private static final String SA_ALGORITHM_SYMBOL = "sa";

    private GraphDataService graphService;
    private CityDataService cityDataService;
    private AlgorithmService algorithmService;
    private DataParser dataParser;

    @Autowired
    public OSMCityService(GraphDataService graphService,
                          CityDataService cityDataService,
                          AlgorithmService algorithmService,
                          DataParser dataParser) {
        this.graphService = graphService;
        this.cityDataService = cityDataService;
        this.algorithmService = algorithmService;
        this.dataParser = dataParser;
    }

    public ObjectNode getCityGraph(TaskInputDTO taskInputDTO) {
        var cityDataDTO = cityDataService.getCityDataDTO(taskInputDTO.getCityName());
        var graphDataDTO = graphService.getGraphDataDTO(cityDataDTO);
        var algorithmType = taskInputDTO.getAlgorithmType();
        var numberOfResults = taskInputDTO.getNumberOfResults();
        AlgorithmOrderDTO algorithmOrderDTO = algorithmType
                .map(typeValue -> new AlgorithmOrderDTO(numberOfResults, graphDataDTO, typeValue))
                .orElseGet(() -> new AlgorithmOrderDTO(numberOfResults, graphDataDTO, SA_ALGORITHM_SYMBOL));
        return algorithmService.run(algorithmOrderDTO);
    }

    public ObjectNode getMappedAlgorithmResult(String taskId) {
        var result = algorithmService.getResult(taskId);
        return dataParser.parseToJson(result.getGraphData(), result.getHospitals());
    }
}
