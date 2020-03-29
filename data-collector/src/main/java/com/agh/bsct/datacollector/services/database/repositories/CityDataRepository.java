package com.agh.bsct.datacollector.services.database.repositories;

import com.agh.bsct.datacollector.services.database.entities.CityDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityDataRepository extends JpaRepository<CityDataEntity, String> {

    boolean existsByCityName(String cityName);

    Optional<CityDataEntity> findTopByCityName(String cityName);
}
