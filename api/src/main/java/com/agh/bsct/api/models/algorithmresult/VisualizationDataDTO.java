package com.agh.bsct.api.models.algorithmresult;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
public class VisualizationDataDTO {

    @NotNull
    List<StreetVisualizationDTO> edges;

}
