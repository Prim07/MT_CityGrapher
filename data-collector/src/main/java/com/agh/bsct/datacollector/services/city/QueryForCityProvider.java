package com.agh.bsct.datacollector.services.city;

import com.agh.bsct.datacollector.library.query.OverpassQuery;
import com.agh.bsct.datacollector.library.union.Recurse;
import com.agh.bsct.datacollector.library.union.output.OutputFormat;
import com.agh.bsct.datacollector.library.union.output.OutputOrder;
import com.agh.bsct.datacollector.library.union.output.OutputVerbosity;
import org.springframework.stereotype.Service;

@Service
public class QueryForCityProvider {

    public String getQueryForCity(String cityName) {
        String wayTypes = getWayTypesForEveryCity();

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

    private String getWayTypesForEveryCity() {
        return new StringBuilder()
                .append("motorway").append("|")
                .append("trunk").append("|")
                .append("primary").append("|")
                .append("secondary").append("|")
                .append("tertiary").append("|")
                .append("motorway_link").append("|")
                .append("trunk_link").append("|")
                .append("primary_link").append("|")
                .append("secondary_link").append("|")
                .append("unclassified").append("|")
                .append("service").append("|")
                .append("bus_guideway")
                .toString();
    }

}
