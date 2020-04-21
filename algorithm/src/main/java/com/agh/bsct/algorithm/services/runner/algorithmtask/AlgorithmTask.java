package com.agh.bsct.algorithm.services.runner.algorithmtask;

import com.agh.bsct.algorithm.services.graph.Graph;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.api.models.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;

import java.util.List;
import java.util.Optional;

public class AlgorithmTask {

    private final String taskId;
    private final GraphDataDTO graphDataDTO;
    private final Integer numberOfResults;
    private final String algorithmType;
    private final Graph graph;
    private Integer calculatingShortestPathsProgress;
    private AlgorithmCalculationStatus status;
    private List<GraphNode> hospitals;

    public AlgorithmTask(String taskId, AlgorithmOrderDTO algorithmOrderDTO, Graph graph) {
        this.taskId = taskId;
        this.graphDataDTO = algorithmOrderDTO.getGraphDataDTO();
        this.numberOfResults = algorithmOrderDTO.getNumberOfResults();
        this.algorithmType = algorithmOrderDTO.getAlgorithmType();
        this.graph = graph;
        this.calculatingShortestPathsProgress = 0;
        this.status = AlgorithmCalculationStatus.NOT_STARTED;
    }

    public String getTaskId() {
        return taskId;
    }

    public GraphDataDTO getGraphDataDTO() {
        return graphDataDTO;
    }

    public Integer getNumberOfResults() {
        return numberOfResults;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public Graph getGraph() {
        return graph;
    }

    public AlgorithmCalculationStatus getStatus() {
        return status;
    }

    public void setStatus(AlgorithmCalculationStatus status) {
        this.status = status;
    }

    public Integer getCalculatingShortestPathsProgress() {
        return calculatingShortestPathsProgress;
    }

    public void setCalculatingShortestPathsProgress(Integer progress) {
        this.calculatingShortestPathsProgress = progress;
    }

    public Optional<List<GraphNode>> getHospitals() {
        return Optional.ofNullable(hospitals);
    }

    public void setHospitals(List<GraphNode> hospitals) {
        this.hospitals = hospitals;
    }

}
