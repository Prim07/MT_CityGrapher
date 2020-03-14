package com.agh.bsct.datacollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.agh.bsct.datacollector.controllers", "com.agh.bsct.datacollector.services"})
public class DataCollector {
    public static void main(String[] args) {
        SpringApplication.run(DataCollector.class, args);
    }
}

