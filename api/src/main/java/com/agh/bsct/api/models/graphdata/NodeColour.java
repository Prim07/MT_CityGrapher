package com.agh.bsct.api.models.graphdata;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Builder
public class NodeColour {

    private Integer R;
    private Integer G;
    private Integer B;

    public NodeColour(Integer r, Integer g, Integer b) {
        R = r;
        G = g;
        B = b;
    }

    public static NodeColour defaultColour() {
        return new NodeColour(168, 48, 216);
    }
}
