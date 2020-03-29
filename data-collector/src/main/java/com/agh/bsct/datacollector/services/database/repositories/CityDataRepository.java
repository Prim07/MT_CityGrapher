package com.agh.bsct.datacollector.services.database.repositories;

import com.agh.bsct.datacollector.services.database.entities.CityDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityDataRepository extends JpaRepository<CityDataEntity, String> {

}
