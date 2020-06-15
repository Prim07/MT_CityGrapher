package com.agh.bsct.api.models.taskinput;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrioritizedNodeDTO {

    @NotNull
    private Long geographicalNodeId;

    @NotNull
    private Double manualWeight;

    @NotNull
    private Double voronoiWeight;

}
