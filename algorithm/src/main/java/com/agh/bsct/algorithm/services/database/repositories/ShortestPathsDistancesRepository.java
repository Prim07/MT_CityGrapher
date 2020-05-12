package com.agh.bsct.algorithm.services.database.repositories;

import com.agh.bsct.algorithm.services.database.entities.ShortestPathsDistancesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortestPathsDistancesRepository extends JpaRepository<ShortestPathsDistancesEntity, String> {

    boolean existsByCityName(String cityName);

    Optional<ShortestPathsDistancesEntity> findTopByCityName(String cityName);
}
