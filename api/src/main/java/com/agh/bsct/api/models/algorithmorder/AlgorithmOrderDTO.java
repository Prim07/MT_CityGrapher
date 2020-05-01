package com.agh.bsct.api.models.algorithmorder;

import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class AlgorithmOrderDTO {

    @NotNull
    private Integer numberOfResults;

    @NotNull
    private GraphDataDTO graphDataDTO;

    @NotNull
    private String algorithmType;

    @NotNull
    private String cityName;

    public AlgorithmOrderDTO(Integer numberOfResults, GraphDataDTO graphDataDTO, String algorithmType,
                             String cityName) {
        this.numberOfResults = numberOfResults;
        this.graphDataDTO = graphDataDTO;
        this.algorithmType = algorithmType;
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmOrderDTO that = (AlgorithmOrderDTO) o;
        return Objects.equals(numberOfResults, that.numberOfResults) &&
                Objects.equals(graphDataDTO, that.graphDataDTO) &&
                Objects.equals(algorithmType, that.algorithmType) &&
                Objects.equals(cityName, that.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfResults, graphDataDTO, algorithmType, cityName);
    }
}
