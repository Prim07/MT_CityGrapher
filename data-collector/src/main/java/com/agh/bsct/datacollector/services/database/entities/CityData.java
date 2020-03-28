package com.agh.bsct.datacollector.services.database.entities;

import com.agh.bsct.api.entities.citydata.CityDataDTO;

import javax.persistence.*;

@Entity
@Table(name = "city_data")
public class CityData {

    private long id;
    private String cityName;
    private CityDataDTO cityDataDTO;

    public CityData() {
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "city_name")
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Column(name = "city_data")
    public CityDataDTO getCityDataDTO() {
        return cityDataDTO;
    }

    public void setCityDataDTO(CityDataDTO cityDataDTO) {
        this.cityDataDTO = cityDataDTO;
    }
}
