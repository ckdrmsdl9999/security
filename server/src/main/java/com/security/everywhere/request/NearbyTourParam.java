package com.security.everywhere.request;

public class NearbyTourParam {
    private String numOfRows = "10";    // 한 페이지 결과 수
    private String pageNo = "1";        // 현재 페이지 번호
    private String arrange = "E";       // (A=제목순, B=조회순, C=수정일순, D=생성일순, E=거리순)
    private String mapX;                // GPS X좌표(WGS84 경도 좌표)
    private String mapY;                // GPS Y좌표(WGS84 위도 좌표)
    private String radius = "2000";              // 거리 반경(단위m), Max값 20000m=20Km

    public String getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(String numOfRows) {
        this.numOfRows = numOfRows;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getArrange() {
        return arrange;
    }

    public void setArrange(String arrange) {
        this.arrange = arrange;
    }

    public String getMapX() {
        return mapX;
    }

    public void setMapX(String mapX) {
        this.mapX = mapX;
    }

    public String getMapY() {
        return mapY;
    }

    public void setMapY(String mapY) {
        this.mapY = mapY;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }
}