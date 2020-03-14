package com.agh.bsct.api.entities.citydata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class GeographicalNodeDTO {

    @NotNull
    private Long id;

    @NotNull
    private Double lon;

    @NotNull
    private Double lat;

    @NotNull
    private boolean isCrossing;

    public GeographicalNodeDTO(Long id, Double lon, Double lat) {
        this(id, lon, lat, false);
    }

    private GeographicalNodeDTO(Long id, Double lon, Double lat, boolean isCrossing) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.isCrossing = isCrossing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeographicalNodeDTO that = (GeographicalNodeDTO) o;
        return isCrossing == that.isCrossing &&
                Objects.equals(id, that.id) &&
                Objects.equals(lon, that.lon) &&
                Objects.equals(lat, that.lat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lon, lat, isCrossing);
    }
}
