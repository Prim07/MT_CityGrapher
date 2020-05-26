package com.agh.bsct.datacollector.services.city;

import java.util.Set;

public enum WaysDataThresholdImportanceLevel {

    LOW(Set.of("motorway",
            "motorway_link",
            "trunk",
            "trunk_link",
            "primary",
            "primary_link",
            "secondary",
            "secondary_link",
            "tertiary",
            "unclassified",
            "service",
            "bus_guideway")),

    MEDIUM(Set.of("motorway",
            "motorway_link",
            "trunk",
            "trunk_link",
            "primary",
            "primary_link",
            "secondary",
            "secondary_link")),

    HIGH(Set.of("motorway",
            "motorway_link",
            "trunk",
            "trunk_link",
            "primary",
            "primary_link")),

    VERY_HIGH(Set.of("motorway",
            "motorway_link",
            "trunk",
            "trunk_link")),
    ;

    private final Set<String> tags;

    WaysDataThresholdImportanceLevel(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getTags() {
        return tags;
    }
}
