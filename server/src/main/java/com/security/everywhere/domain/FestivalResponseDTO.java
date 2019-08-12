package com.security.everywhere.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class FestivalResponseDTO {
    private FestivalResponseHeader header;
    private FestivalResponseBody body;

    public FestivalResponseHeader getHeader() {
        return header;
    }

    public void setHeader(FestivalResponseHeader header) {
        this.header = header;
    }

    public FestivalResponseBody getBody() {
        return body;
    }

    public void setBody(FestivalResponseBody body) {
        this.body = body;
    }
}
