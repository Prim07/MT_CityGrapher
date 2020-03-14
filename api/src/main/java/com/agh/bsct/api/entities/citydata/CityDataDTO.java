package com.agh.bsct.api.entities.citydata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CityDataDTO {

    @NotNull
    private List<GeographicalNodeDTO> geographicalNodes;

    @NotNull
    private List<StreetDTO> streets;

    public CityDataDTO(List<GeographicalNodeDTO> geographicalNodes, List<StreetDTO> streets) {
        this.geographicalNodes = geographicalNodes;
        this.streets = streets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityDataDTO that = (CityDataDTO) o;
        return Objects.equals(geographicalNodes, that.geographicalNodes) &&
                Objects.equals(streets, that.streets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geographicalNodes, streets);
    }
}
