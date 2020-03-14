package com.agh.bsct.datacollector.services.result.filter;

import com.agh.bsct.datacollector.library.adapter.queryresult.Element;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

@Service
public class ResultFilterService {

    private static final String IS_AREA_TAG_VALUE = "yes";
    private static final String AREA_TYPE = "area";

    public OverpassQueryResult removeAreaTags(OverpassQueryResult queryResult) {
        queryResult.getElements().removeIf(this::shouldElementBeRemoved);
        return queryResult;
    }

    private boolean shouldElementBeRemoved(Element element) {
        return IS_AREA_TAG_VALUE.equals(element.getTags().getArea())
                || AREA_TYPE.equals(element.getType());
    }
}
