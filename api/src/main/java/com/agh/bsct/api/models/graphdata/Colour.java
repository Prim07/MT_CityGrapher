package com.agh.bsct.api.models.graphdata;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Builder
public class Colour {

    private Integer R;
    private Integer G;
    private Integer B;

    public Colour(Integer r, Integer g, Integer b) {
        R = r;
        G = g;
        B = b;
    }

    public static Colour createDefaultColour() {
        return new Colour(168, 48, 216);
    }
}
