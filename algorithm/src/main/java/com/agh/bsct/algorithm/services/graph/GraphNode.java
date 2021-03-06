package com.agh.bsct.algorithm.services.graph;

import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.graphdata.Colour;

import java.util.Objects;

public class GraphNode {

    private final long id;
    private final double weight;
    private GeographicalNodeDTO geographicalNodeDTO;
    private Colour nodeColour;


    public GraphNode(Long id, Double weight, GeographicalNodeDTO geographicalNodeDTO) {
        this.id = id;
        this.weight = weight;
        this.geographicalNodeDTO = geographicalNodeDTO;
        this.nodeColour = Colour.createDefaultColour();
    }

    public GraphNode(Long id, Integer weight) {
        this.id = id;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public Colour getNodeColour() {
        return nodeColour;
    }

    public void setNodeColour(Colour nodeColour) {
        this.nodeColour = nodeColour;
    }

    public GeographicalNodeDTO getGeographicalNodeDTO() {
        return geographicalNodeDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return id == graphNode.id &&
                Objects.equals(geographicalNodeDTO, graphNode.geographicalNodeDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, geographicalNodeDTO);
    }
}
