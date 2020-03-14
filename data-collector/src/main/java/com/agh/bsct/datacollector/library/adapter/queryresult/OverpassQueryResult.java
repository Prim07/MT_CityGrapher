package com.agh.bsct.datacollector.library.adapter.queryresult;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OverpassQueryResult {

    @SerializedName("elements")
    private List<Element> elements;

    public List<Element> getElements() {
        return elements;
    }
}

