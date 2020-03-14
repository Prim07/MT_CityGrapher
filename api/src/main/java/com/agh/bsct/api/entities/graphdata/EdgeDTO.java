package com.agh.bsct.api.entities.graphdata;

import com.agh.bsct.api.entities.citydata.StreetDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class EdgeDTO {

    @NotNull
    private StreetDTO streetDTO;

    @NotNull
    private double weight;

    public EdgeDTO(StreetDTO streetDTO, double weight) {
        this.streetDTO = streetDTO;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeDTO edgeDTO = (EdgeDTO) o;
        return Double.compare(edgeDTO.weight, weight) == 0 &&
                Objects.equals(streetDTO, edgeDTO.streetDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetDTO, weight);
    }
}
