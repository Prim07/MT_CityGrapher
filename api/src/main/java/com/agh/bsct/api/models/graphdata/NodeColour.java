package com.agh.bsct.api.models.graphdata;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@NoArgsConstructor
@Builder
public class NodeColour {

    @NotNull
    private Integer R;

    @NotNull
    private Integer G;

    @NotNull
    private Integer B;

}
