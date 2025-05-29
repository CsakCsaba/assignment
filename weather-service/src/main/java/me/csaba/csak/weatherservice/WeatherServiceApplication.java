package me.csaba.csak.weatherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class WeatherServiceApplication {


    public static void main(final String[] args) {
        SpringApplication.run(WeatherServiceApplication.class, args);
    }

}
