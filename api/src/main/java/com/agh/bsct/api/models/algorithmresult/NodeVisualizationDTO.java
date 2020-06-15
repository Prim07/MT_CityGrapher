package com.agh.bsct.api.models.algorithmresult;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class NodeVisualizationDTO {

    @NotNull
    private final Long id;

    @NotNull
    private final double lat;

    @NotNull
    private final double lon;

    @NotNull
    @JsonProperty(value = "isCrossing")
    private final boolean isCrossing;

    @NotNull
    @JsonProperty(value = "isHospital")
    private final boolean isHospital;

    @NotNull
    private final String colour;

}
