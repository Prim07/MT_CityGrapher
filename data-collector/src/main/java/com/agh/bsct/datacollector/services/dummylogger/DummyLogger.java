package com.agh.bsct.datacollector.services.dummylogger;

import java.util.Calendar;

public class DummyLogger {

    public static void printMessage(String message) {
        Calendar now = Calendar.getInstance();
        System.out.println(now.get(Calendar.HOUR_OF_DAY)
                + ":" + now.get(Calendar.MINUTE)
                + ":" + now.get(Calendar.SECOND)
                + ":" + now.get(Calendar.MILLISECOND)
                + " - " + message);
    }

}
