package com.security.everywhere.response;

public class WeatherForecastDTO {
    private WeatherForecastHeader header;
    private WeatherForecastBody body;

    public WeatherForecastHeader getHeader() {
        return header;
    }

    public void setHeader(WeatherForecastHeader header) {
        this.header = header;
    }

    public WeatherForecastBody getBody() {
        return body;
    }

    public void setBody(WeatherForecastBody body) {
        this.body = body;
    }
}
