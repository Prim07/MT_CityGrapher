package com.agh.bsct.datacollector.services.database;

import com.agh.bsct.api.entities.citydata.CityDataDTO;
import com.agh.bsct.datacollector.services.database.entities.CityDataEntity;
import com.agh.bsct.datacollector.services.database.repositories.CityDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final CityDataRepository cityDataRepository;

    @Autowired
    public DatabaseService(CityDataRepository cityDataRepository) {
        this.cityDataRepository = cityDataRepository;
    }

    public void save(CityDataDTO cityDataDTO, String cityName) {
        CityDataEntity cityDataEntity = new CityDataEntity();
        cityDataEntity.setCityName(cityName);
        cityDataEntity.setCityDataDTO(cityDataDTO);
        cityDataRepository.save(cityDataEntity);
    }
}
