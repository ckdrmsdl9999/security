package com.security.everywhere.request;

/*
 * 2개의 값 모두 필수
 * */
public class ObservatoryParam {
    private String mapx = "";
    private String mapy = "";

    public String getMapx() {
        return mapx;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public String getMapy() {
        return mapy;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }
}
