package com.security.everywhere.response.festivalImages;

import com.security.everywhere.model.Festival;

public class FestivalImagesDTO {
    private FestivalImagesHeader header;
    private FestivalImagesBody body;

    public FestivalImagesHeader getHeader() {
        return header;
    }

    public void setHeader(FestivalImagesHeader header) {
        this.header = header;
    }

    public FestivalImagesBody getBody() {
        return body;
    }

    public void setBody(FestivalImagesBody body) {
        this.body = body;
    }
}
