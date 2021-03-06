package com.agh.bsct.api.models.graphdata;

import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class NodeDTO {

    @NotNull
    private GeographicalNodeDTO geographicalNodeDTO;

    @NotNull
    private Colour nodeColour;

    @NotNull
    private Double weight;

    public NodeDTO(GeographicalNodeDTO geographicalNodeDTO, Double weight) {
        this.geographicalNodeDTO = geographicalNodeDTO;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDTO nodeDTO = (NodeDTO) o;
        return Objects.equals(geographicalNodeDTO, nodeDTO.geographicalNodeDTO) &&
                Objects.equals(nodeColour, nodeDTO.nodeColour) &&
                Objects.equals(weight, nodeDTO.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geographicalNodeDTO, nodeColour, weight);
    }
}
