package com.agh.bsct.api.models.algorithmresult;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
public class StreetVisualizationDTO {

    @NotNull
    private final int id;

    @NotNull
    private final double weight;

    @NotNull
    private final String colour;

    @NotNull
    private final List<NodeVisualizationDTO> nodes;
}
