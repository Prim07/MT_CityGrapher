package com.agh.bsct.api.models.algorithmresult;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class AlgorithmResultInfoDTO {

    @NotNull
    private long numberOfNodes;

    @NotNull
    private long numberOfEdges;

    @NotNull
    private double fitnessScore;

}
