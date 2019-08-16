package com.security.everywhere.response;

public class FestivalDTO {
    private FestivalHeader header;
    private FestivalBody body = null;

    public FestivalHeader getHeader() {
        return header;
    }

    public void setHeader(FestivalHeader header) {
        this.header = header;
    }

    public FestivalBody getBody() {
        return body;
    }

    public void setBody(FestivalBody body) {
        this.body = body;
    }
}
