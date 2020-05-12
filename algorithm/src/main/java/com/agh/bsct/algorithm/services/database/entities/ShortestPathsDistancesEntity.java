package com.agh.bsct.algorithm.services.database.entities;

import com.agh.bsct.algorithm.services.graph.ShortestPathsDistances;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "shortest_paths_distances")
@NoArgsConstructor
@Getter
@Setter
public class ShortestPathsDistancesEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "shortest_paths_distances")
    private ShortestPathsDistances shortestPathsDistances;

}
