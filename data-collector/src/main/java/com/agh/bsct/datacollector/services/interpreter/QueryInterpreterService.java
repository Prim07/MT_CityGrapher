package com.agh.bsct.datacollector.services.interpreter;

import com.agh.bsct.datacollector.library.adapter.OverpassServiceProvider;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;

@Service
public class QueryInterpreterService {

    private static final int INIT_EXCEPTION_COUNTER_VALUE = 2;

    private Integer exceptionCounter = INIT_EXCEPTION_COUNTER_VALUE;

    public OverpassQueryResult interpret(String query) {
        try {
            OverpassQueryResult result = OverpassServiceProvider.get().interpreter(query).execute().body();
            exceptionCounter = INIT_EXCEPTION_COUNTER_VALUE;
            return result;
        } catch (SocketTimeoutException e) {
            exceptionCounter--;
            if (exceptionCounter > 0) {
                return interpret(query);
            }
            exceptionCounter = INIT_EXCEPTION_COUNTER_VALUE;
            return new OverpassQueryResult();
        } catch (Exception e) {
            e.printStackTrace();
            exceptionCounter = INIT_EXCEPTION_COUNTER_VALUE;
            return new OverpassQueryResult();
        }
    }

}
