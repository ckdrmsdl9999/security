package com.security.everywhere.response.middleLandWeather;

public class MiddleLandWeatherDTO {
    private MiddleLandWeatherHeader header;
    private MiddleLandWeatherBody body;

    public MiddleLandWeatherHeader getHeader() {
        return header;
    }

    public void setHeader(MiddleLandWeatherHeader header) {
        this.header = header;
    }

    public MiddleLandWeatherBody getBody() {
        return body;
    }

    public void setBody(MiddleLandWeatherBody body) {
        this.body = body;
    }
}
