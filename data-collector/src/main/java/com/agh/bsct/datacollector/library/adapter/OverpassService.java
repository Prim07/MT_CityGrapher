package com.agh.bsct.datacollector.library.adapter;

import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OverpassService {
    @GET("/api/interpreter")
    Call<OverpassQueryResult> interpreter(@Query(value = "data", encoded = true) String data);
}
