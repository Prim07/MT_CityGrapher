package com.agh.bsct.api.models.graphdata;

import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.citydata.StreetDTO;
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

    @NotNull
    private GeographicalNodeDTO startGeographicalNode;

    @NotNull
    private Colour edgeColour;

    public EdgeDTO(StreetDTO streetDTO, double weight, GeographicalNodeDTO startGeographicalNode) {
        this.streetDTO = streetDTO;
        this.weight = weight;
        this.startGeographicalNode = startGeographicalNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeDTO edgeDTO = (EdgeDTO) o;
        return Double.compare(edgeDTO.weight, weight) == 0 &&
                Objects.equals(streetDTO, edgeDTO.streetDTO) &&
                Objects.equals(startGeographicalNode, edgeDTO.startGeographicalNode) &&
                Objects.equals(edgeColour, edgeDTO.edgeColour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetDTO, weight, startGeographicalNode, edgeColour);
    }
}
