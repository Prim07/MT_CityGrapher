package com.agh.bsct.api.entities.algorithmresult;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class AlgorithmResultWithVisualizationDataDTO {

    @NotNull
    private AlgorithmResultDTO algorithmResultDTO;

    @NotNull
    private VisualizationDataDTO visualizationDataDTO;

}
