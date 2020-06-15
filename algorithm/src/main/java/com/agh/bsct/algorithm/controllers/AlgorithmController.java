package com.agh.bsct.algorithm.controllers;

import com.agh.bsct.algorithm.controllers.mapper.AlgorithmTaskMapper;
import com.agh.bsct.algorithm.services.lcc.LargestConnectedComponentService;
import com.agh.bsct.algorithm.services.runner.AlgorithmRunnerService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.models.algorithmcreated.AlgorithmTaskIdDTO;
import com.agh.bsct.api.models.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.models.algorithmresult.AlgorithmResultDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
public class AlgorithmController {

    private static final String ALGORITHM_PATH = "algorithm/";
    private static final String TASK_ID_URI_PARAM = "{taskId}";
    public static final String LARGEST_CONNECTED_COMPONENT_PATH = ALGORITHM_PATH + "lcc/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AlgorithmRunnerService algorithmRunnerService;
    private final AlgorithmTaskMapper algorithmTaskMapper;
    private final LargestConnectedComponentService largestConnectedComponentService;

    @Autowired
    public AlgorithmController(AlgorithmRunnerService algorithmRunnerService,
                               AlgorithmTaskMapper algorithmTaskMapper,
                               LargestConnectedComponentService largestConnectedComponentService) {
        this.algorithmRunnerService = algorithmRunnerService;
        this.algorithmTaskMapper = algorithmTaskMapper;
        this.largestConnectedComponentService = largestConnectedComponentService;
    }

    @RequestMapping(method = RequestMethod.GET, value = ALGORITHM_PATH + TASK_ID_URI_PARAM)
    public ResponseEntity<AlgorithmResultDTO> getResults(@PathVariable String taskId) {
        try {
            AlgorithmTask task = algorithmRunnerService.get(taskId);
            return (task.getStatus() == AlgorithmCalculationStatus.SUCCESS)
                    ? getSuccessfulResponseWithAlgorithmTask(task)
                    : getAcceptedResponseWithAlgorithmTask(task);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            e.printStackTrace();
            return getNotFoundResponse(e, taskId);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return getFailureResponseWithAlgorithmResultDTO(e, taskId);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = ALGORITHM_PATH + TASK_ID_URI_PARAM)
    public ResponseEntity<ObjectNode> cancelAlgorithmTask(@PathVariable String taskId) {
        algorithmRunnerService.cancel(taskId);
        return getSuccessfulResponseForCancelledAlgorithmTask(taskId);
    }

    @RequestMapping(method = RequestMethod.POST, value = ALGORITHM_PATH)
    @ResponseBody
    public ResponseEntity<AlgorithmTaskIdDTO> run(@RequestBody AlgorithmOrderDTO algorithmOrderDTO) {
        try {
            String taskId = algorithmRunnerService.run(algorithmOrderDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new AlgorithmTaskIdDTO(taskId));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = LARGEST_CONNECTED_COMPONENT_PATH)
    @ResponseBody
    public ResponseEntity<GraphDataDTO> createLargestConnectedComponent(@RequestBody GraphDataDTO graphDataDTO) {
        largestConnectedComponentService.replaceWithLargestConnectedComponent(graphDataDTO);
        return ResponseEntity.status(HttpStatus.OK).body(graphDataDTO);
    }

    @GetMapping(ALGORITHM_PATH + "test")
    @ResponseBody
    public String test() {
        return "Hello, world!";
    }

    private ResponseEntity<AlgorithmResultDTO> getSuccessfulResponseWithAlgorithmTask(AlgorithmTask task) {
        AlgorithmResultDTO algorithmResultDTO = algorithmTaskMapper.mapToAlgorithmResultDTO(task);
        return ResponseEntity.status(HttpStatus.OK).body(algorithmResultDTO);
    }

    private ResponseEntity<AlgorithmResultDTO> getAcceptedResponseWithAlgorithmTask(AlgorithmTask task) {
        AlgorithmResultDTO algorithmResultDTO = algorithmTaskMapper.mapToAlgorithmResultDTO(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(algorithmResultDTO);
    }

    private ResponseEntity<ObjectNode> getSuccessfulResponseForCancelledAlgorithmTask(String taskId) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("message", "IAlgorithm task with ID: " + taskId + " was successfully cancelled");
        return ResponseEntity.status(HttpStatus.OK).body(objectNode);
    }

    private ResponseEntity<AlgorithmResultDTO> getNotFoundResponse(CacheLoader.InvalidCacheLoadException e,
                                                                   String taskId) {
        AlgorithmResultDTO algorithmResultDTO = getAlgorithmResultWithErrorStatus(e.getMessage(), taskId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(algorithmResultDTO);
    }

    private ResponseEntity<AlgorithmResultDTO> getFailureResponseWithAlgorithmResultDTO(ExecutionException e,
                                                                                        String taskId) {
        AlgorithmResultDTO algorithmResultDTO = getAlgorithmResultWithErrorStatus(e.getMessage(), taskId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(algorithmResultDTO);
    }

    private AlgorithmResultDTO getAlgorithmResultWithErrorStatus(String message, String taskId) {
        return AlgorithmResultDTO.builder()
                .taskId(taskId)
                .status("Error: " + message)
                .graphData(null)
                .hospitals(Collections.emptyList())
                .build();
    }

}
