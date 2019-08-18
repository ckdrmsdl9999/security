package com.security.everywhere.response.festivalImages;

public class FestivalImagesItem {
    private String contentid;       // 콘텐츠ID
    private String originimgurl;    // 원본 이미지, 약 500*333 size
    private String serialnum;       // 이미지 일련번호
    private String smallimageurl;   // 썸네일 이미지, 약 160*100 size

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getOriginimgurl() {
        return originimgurl;
    }

    public void setOriginimgurl(String originimgurl) {
        this.originimgurl = originimgurl;
    }

    public String getSerialnum() {
        return serialnum;
    }

    public void setSerialnum(String serialnum) {
        this.serialnum = serialnum;
    }

    public String getSmallimageurl() {
        return smallimageurl;
    }

    public void setSmallimageurl(String smallimageurl) {
        this.smallimageurl = smallimageurl;
    }
}
