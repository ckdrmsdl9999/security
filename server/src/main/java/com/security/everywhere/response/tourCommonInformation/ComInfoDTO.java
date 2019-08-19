package com.security.everywhere.response.tourCommonInformation;

public class ComInfoDTO {
    private ComInfoHeader header;
    private ComInfoBody body;

    public ComInfoHeader getHeader() {
        return header;
    }

    public void setHeader(ComInfoHeader header) {
        this.header = header;
    }

    public ComInfoBody getBody() {
        return body;
    }

    public void setBody(ComInfoBody body) {
        this.body = body;
    }
}
