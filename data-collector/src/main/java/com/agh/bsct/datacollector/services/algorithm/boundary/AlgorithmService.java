package com.agh.bsct.datacollector.services.algorithm.boundary;

import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.entities.algorithmresult.AlgorithmResultDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static com.agh.bsct.datacollector.controllers.config.PathsConstants.ALGORITHM_ROOT_PATH;

@Service
public class AlgorithmService {

    private static final String ALGORITHM_PATH = ALGORITHM_ROOT_PATH + "/algorithm/";

    private final RestTemplate restTemplate;

    public AlgorithmService() {
        this.restTemplate = new RestTemplateBuilder().build();
    }

    public ObjectNode run(AlgorithmOrderDTO algorithmOrderDTO) {
        var uri = new DefaultUriBuilderFactory(ALGORITHM_PATH).builder()
                .build()
                .toString();
        return restTemplate.postForObject(uri, algorithmOrderDTO, ObjectNode.class);
    }

    public AlgorithmResultDTO getResult(String taskId) {
        var uri = new DefaultUriBuilderFactory(ALGORITHM_PATH).builder()
                .path(taskId)
                .build()
                .toString();
        return restTemplate.getForObject(uri, AlgorithmResultDTO.class);
    }
}
