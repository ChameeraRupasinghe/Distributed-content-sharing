package com.distributed;

import java.time.LocalTime;

public class LoggerX {

    public static void log(String s) {
        System.out.println(LocalTime.now() + "(LOGGER): " + s);
    }
}
