package me.csaba.csak.weatherservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@EnableFeignClients
@SpringBootApplication
public class WeatherServiceApplication {


    public static void main(final String[] args) {
        final ApplicationContext context = SpringApplication.run(WeatherServiceApplication.class, args);

        final WeatherService weatherService1 = context.getBean(WeatherService.class);

        weatherService1.getWeather( 27.88, 15.6);
    }

}
