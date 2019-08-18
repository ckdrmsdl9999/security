package com.security.everywhere.response.festivalCommonInformation;

public class FestivalComInfoDTO {
    private FestivalComInfoHeader header;
    private FestivalComInfoBody body;

    public FestivalComInfoHeader getHeader() {
        return header;
    }

    public void setHeader(FestivalComInfoHeader header) {
        this.header = header;
    }

    public FestivalComInfoBody getBody() {
        return body;
    }

    public void setBody(FestivalComInfoBody body) {
        this.body = body;
    }
}
