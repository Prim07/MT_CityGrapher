package com.agh.bsct.datacollector.services.database.entities;

import com.agh.bsct.api.entities.citydata.CityDataDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "city_data")
@NoArgsConstructor
@Getter
@Setter
public class CityDataEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "city_data")
    private CityDataDTO cityDataDTO;

}
