package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.api.models.algorithmcreated.AlgorithmTaskIdDTO;
import com.agh.bsct.api.models.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.models.algorithmresult.AlgorithmResultWithVisualizationDataDTO;
import com.agh.bsct.api.models.taskinput.TaskInputDTO;
import com.agh.bsct.datacollector.services.algorithm.boundary.AlgorithmService;
import com.agh.bsct.datacollector.services.data.CityDataService;
import com.agh.bsct.datacollector.services.data.GraphDataService;
import com.agh.bsct.datacollector.services.parser.DataParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OSMCityService {

    private static final String SA_ALGORITHM_SYMBOL = "sa";

    private final GraphDataService graphService;
    private final CityDataService cityDataService;
    private final AlgorithmService algorithmService;
    private final DataParser dataParser;

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

    public AlgorithmTaskIdDTO getCityGraph(TaskInputDTO taskInputDTO) {
        var cityDataDTO = cityDataService.getCityDataDTO(taskInputDTO.getCityName());
        var graphDataDTO = graphService.getGraphDataDTO(cityDataDTO);
        var algorithmType = taskInputDTO.getAlgorithmType();
        var numberOfResults = taskInputDTO.getNumberOfResults();
        var cityName = taskInputDTO.getCityName();

        var algorithmOrderDTO = algorithmType
                .map(typeValue -> new AlgorithmOrderDTO(numberOfResults, graphDataDTO, typeValue, cityName))
                .orElseGet(() -> new AlgorithmOrderDTO(numberOfResults, graphDataDTO, SA_ALGORITHM_SYMBOL, cityName));

        return algorithmService.run(algorithmOrderDTO);
    }

    public AlgorithmResultWithVisualizationDataDTO getMappedAlgorithmResult(String taskId) {
        return dataParser.parseToVisualizationDataDTO(algorithmService.getResult(taskId));
    }
}
