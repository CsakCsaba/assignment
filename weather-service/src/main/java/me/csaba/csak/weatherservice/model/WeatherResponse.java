package me.csaba.csak.weatherservice.model;

public record WeatherResponse(WeatherReport weatherReport, String expires) {}
