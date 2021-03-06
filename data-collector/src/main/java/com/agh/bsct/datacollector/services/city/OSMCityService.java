package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.api.models.algorithmcreated.AlgorithmTaskIdDTO;
import com.agh.bsct.api.models.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.models.algorithmresult.FinalAlgorithmResultDTO;
import com.agh.bsct.api.models.algorithmresult.VisualizationDataDTO;
import com.agh.bsct.api.models.taskinput.NodesPrioritiesDTO;
import com.agh.bsct.api.models.taskinput.TaskInputDTO;
import com.agh.bsct.datacollector.services.algorithm.boundary.AlgorithmService;
import com.agh.bsct.datacollector.services.data.CityDataService;
import com.agh.bsct.datacollector.services.data.GraphDataService;
import com.agh.bsct.datacollector.services.parser.DataParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.agh.bsct.datacollector.services.dummylogger.DummyLogger.printMessage;

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

    public AlgorithmTaskIdDTO createAlgorithmTask(TaskInputDTO taskInputDTO) {
        var cityDataDTO = cityDataService.getCityDataDTO(taskInputDTO.getCityName());
        var graphDataDTO = graphService.getGraphDataDTO(cityDataDTO, taskInputDTO.getNodesPriorities());
        var algorithmType = taskInputDTO.getAlgorithmType();
        var numberOfResults = taskInputDTO.getNumberOfResults();
        var cityName = taskInputDTO.getCityName();

        var algorithmOrderDTO = algorithmType
                .map(typeValue -> new AlgorithmOrderDTO(numberOfResults, graphDataDTO, typeValue, cityName))
                .orElseGet(() -> new AlgorithmOrderDTO(numberOfResults, graphDataDTO, SA_ALGORITHM_SYMBOL, cityName));

        printMessage(taskInputDTO.getCityName() + ": Sending POST request to Algorithm");
        return algorithmService.run(algorithmOrderDTO);
    }

    public VisualizationDataDTO getVisualizationDataDTOData(String cityName) {
        var cityDataDTO = cityDataService.getCityDataDTO(cityName);
        var graphDataDTO = graphService.getGraphDataDTO(cityDataDTO, getEmptyNodesPrioritiesDTO());
        var largestConnectedComponentGraphDataDTO = algorithmService.getLargestConnectedComponent(graphDataDTO);
        return dataParser.getVisualizationDataDTOWithoutHospitals(largestConnectedComponentGraphDataDTO);
    }

    private NodesPrioritiesDTO getEmptyNodesPrioritiesDTO() {
        return new NodesPrioritiesDTO(false, false, Collections.emptyList());
    }

    public FinalAlgorithmResultDTO getMappedAlgorithmResult(String taskId) {
        return dataParser.parseToFinalAlgorithmResultDTO(algorithmService.getResult(taskId));
    }
}
