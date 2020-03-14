package com.agh.bsct.api.entities.graphdata;

import com.agh.bsct.api.entities.citydata.GeographicalNodeDTO;
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
    private Integer weight;

    public NodeDTO(GeographicalNodeDTO geographicalNodeDTO, Integer weight) {
        this.geographicalNodeDTO = geographicalNodeDTO;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDTO nodeDTO = (NodeDTO) o;
        return Objects.equals(geographicalNodeDTO, nodeDTO.geographicalNodeDTO) &&
                Objects.equals(weight, nodeDTO.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geographicalNodeDTO, weight);
    }
}
