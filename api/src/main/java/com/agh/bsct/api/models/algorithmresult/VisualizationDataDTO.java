package com.agh.bsct.api.models.algorithmresult;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
public class VisualizationDataDTO {
    @NotNull
    List<ObjectNode> edges;
}
