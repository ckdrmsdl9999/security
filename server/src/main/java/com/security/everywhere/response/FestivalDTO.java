package com.security.everywhere.response;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class FestivalDTO {
    private FestivalHeader header;
    private FestivalBody body;

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
