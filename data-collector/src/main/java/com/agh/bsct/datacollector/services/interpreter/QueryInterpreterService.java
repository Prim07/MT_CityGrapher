package com.agh.bsct.datacollector.services.interpreter;

import com.agh.bsct.datacollector.library.adapter.OverpassServiceProvider;
import com.agh.bsct.datacollector.library.adapter.queryresult.OverpassQueryResult;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;

@Service
public class QueryInterpreterService {

    private Integer exceptionCounter = 3;

    public OverpassQueryResult interpret(String query) {
        try {
            return OverpassServiceProvider.get().interpreter(query).execute().body();

        } catch (SocketTimeoutException e) {
            exceptionCounter--;
            return (exceptionCounter > 0)
                    ? interpret(query)
                    : new OverpassQueryResult();
        } catch (Exception e) {
            e.printStackTrace();

            return new OverpassQueryResult();
        }
    }

}
