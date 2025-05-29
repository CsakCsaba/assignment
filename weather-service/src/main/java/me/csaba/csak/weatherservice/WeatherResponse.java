package me.csaba.csak.weatherservice;

public record WeatherResponse(WeatherReport weatherReport, String expires) {}
