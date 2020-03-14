package com.agh.bsct.api.entities.algorithmresult;

import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
public class AlgorithmResultDTO {

    @NotNull
    private String taskId;

    @NotNull
    private String status;

    @NotNull
    private GraphDataDTO graphData;

    @NotNull
    private List<GeographicalNodeDTO> hospitals;

    public AlgorithmResultDTO(String taskId, String status, GraphDataDTO graphData,
                              List<GeographicalNodeDTO> hospitals) {
        this.taskId = taskId;
        this.status = status;
        this.graphData = graphData;
        this.hospitals = hospitals;
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
