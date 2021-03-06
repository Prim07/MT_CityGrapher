package com.agh.bsct.api.models.algorithmresult;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class FinalAlgorithmResultDTO {

    @NotNull
    private AlgorithmResultDTO algorithmResultDTO;

    @NotNull
    private VisualizationDataDTO visualizationDataDTO;

    @NotNull
    private AlgorithmResultInfoDTO algorithmResultInfoDTO;

}
