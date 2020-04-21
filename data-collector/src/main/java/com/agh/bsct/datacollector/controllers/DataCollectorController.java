package com.agh.bsct.datacollector.controllers;


import com.agh.bsct.api.models.algorithmcreated.AlgorithmCreatedResponseDTO;
import com.agh.bsct.api.models.algorithmcreated.AlgorithmTaskIdDTO;
import com.agh.bsct.api.models.algorithmresult.AlgorithmResultWithVisualizationDataDTO;
import com.agh.bsct.api.models.taskinput.TaskInputDTO;
import com.agh.bsct.datacollector.controllers.config.PathsConstants;
import com.agh.bsct.datacollector.services.city.OSMCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
public class DataCollectorController {

    private static final String DATA_COLLECTOR_PATH = "dataCollector";
    private static final String CREATE_TASK_PATH = "/createTask";
    private static final String GET_ALGORITHM_RESULT_PATH = "/algorithmResult/";
    private static final String GET_TEMP_ALGORITHM_RESULT_PATH = "/tempAlgorithmResult/";
    private static final String TASK_ID_URI_PARAM = "{taskId}";

    private final OSMCityService osmCityService;

    @Autowired
    public DataCollectorController(OSMCityService osmCityService) {
        this.osmCityService = osmCityService;
    }

    @RequestMapping(method = RequestMethod.POST, value = DATA_COLLECTOR_PATH + CREATE_TASK_PATH)
    @ResponseBody
    public ResponseEntity<AlgorithmCreatedResponseDTO> getCityGraph(@RequestBody TaskInputDTO taskInputDTO) {
        AlgorithmTaskIdDTO algorithmTaskIdDTO = osmCityService.getCityGraph(taskInputDTO);
        AlgorithmCreatedResponseDTO algorithmCreatedResponseDTO = AlgorithmCreatedResponseDTO.builder()
                .taskId(algorithmTaskIdDTO)
                .uri(PathsConstants.DATA_COLLECTOR_ROOT_PATH + DATA_COLLECTOR_PATH
                        + GET_TEMP_ALGORITHM_RESULT_PATH + algorithmTaskIdDTO.getTaskId())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(algorithmCreatedResponseDTO);
    }

    @GetMapping(DATA_COLLECTOR_PATH + GET_ALGORITHM_RESULT_PATH + TASK_ID_URI_PARAM)
    @ResponseBody
    public AlgorithmResultWithVisualizationDataDTO getMappedAlgorithmResult(@PathVariable String taskId) {
        return osmCityService.getMappedAlgorithmResult(taskId);
    }

    @GetMapping(DATA_COLLECTOR_PATH + GET_TEMP_ALGORITHM_RESULT_PATH + TASK_ID_URI_PARAM)
    @ResponseBody
    public AlgorithmResultWithVisualizationDataDTO getTempAlgorithmResult(@PathVariable String taskId) {
        return getMappedAlgorithmResult(taskId);
    }
}
