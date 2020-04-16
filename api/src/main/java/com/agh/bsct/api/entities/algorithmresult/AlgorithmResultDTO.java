package com.agh.bsct.api.entities.algorithmresult;

import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
public class AlgorithmResultDTO {

    @NotNull
    private String taskId;

    @NotNull
    private String status;

    @NotNull
    private GraphDataDTO graphData;

    @NotNull
    private Integer calculatingShortestPathsProgress;

    @NotNull
    private List<GeographicalNodeDTO> hospitals;

    public AlgorithmResultDTO(String taskId, String status, GraphDataDTO graphData,
                              Integer calculatingShortestPathsProgress, List<GeographicalNodeDTO> hospitals) {
        this.taskId = taskId;
        this.status = status;
        this.graphData = graphData;
        this.calculatingShortestPathsProgress = calculatingShortestPathsProgress;
        this.hospitals = hospitals;
    }

    public AlgorithmResultDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmResultDTO that = (AlgorithmResultDTO) o;
        return Objects.equals(taskId, that.taskId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(graphData, that.graphData) &&
                Objects.equals(hospitals, that.hospitals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, status, graphData, hospitals);
    }
}
