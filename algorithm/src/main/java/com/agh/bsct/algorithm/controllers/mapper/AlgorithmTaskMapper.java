package com.agh.bsct.algorithm.controllers.mapper;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class AlgorithmTaskMapper {

    public AlgorithmResultDTO mapToAlgorithmResultDTO(AlgorithmTask algorithmTask) {
        return AlgorithmResultDTO.builder()
                .taskId(algorithmTask.getTaskId())
                .status(algorithmTask.getStatus().toString())
                .graphData(algorithmTask.getGraphDataDTO())
                .hospitals(algorithmTask.getHospitals().orElse(Collections.emptyList()))
                .build();
    }

}
