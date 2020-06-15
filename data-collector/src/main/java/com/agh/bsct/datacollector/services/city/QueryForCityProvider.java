package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.datacollector.library.query.OverpassQuery;
import com.agh.bsct.datacollector.library.union.Recurse;
import com.agh.bsct.datacollector.library.union.output.OutputFormat;
import com.agh.bsct.datacollector.library.union.output.OutputOrder;
import com.agh.bsct.datacollector.library.union.output.OutputVerbosity;
import org.springframework.stereotype.Service;

@Service
public class QueryForCityProvider {

    public static final String TAGS_IN_QUERY_SEPARATOR = "|";

    public String getQueryForCity(String cityName, WaysDataThresholdImportanceLevel importanceLevel) {
        String wayTypes = getWayTypes(importanceLevel);

        return new OverpassQuery()
                .format(OutputFormat.JSON)
                .filterQuery()
                .area().tag("name", cityName)
                .prepareNext()
                .way().tagLike("highway", wayTypes).forKey("area")
                .end()
                .output(OutputVerbosity.META)
                .output(Recurse.DOWN)
                .output(OutputVerbosity.SKEL, OutputOrder.QT)
                .build();
    }

    public String getQueryForCityCount(String cityName, WaysDataThresholdImportanceLevel importanceLevel) {
        String wayTypes = getWayTypes(importanceLevel);

        return new OverpassQuery()
                .format(OutputFormat.JSON)
                .filterQuery()
                .area().tag("name", cityName)
                .prepareNext()
                .way().tagLike("highway", wayTypes).forKey("area")
                .end()
                .output(OutputVerbosity.COUNT)
                .build();
    }

    private String getWayTypes(WaysDataThresholdImportanceLevel importanceLevel) {
        return String.join(TAGS_IN_QUERY_SEPARATOR, importanceLevel.getTags());
    }

}
