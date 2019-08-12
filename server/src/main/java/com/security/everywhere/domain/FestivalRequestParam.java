package com.security.everywhere.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FestivalRequestParam {
    private String numOfRows="";
    private String pageNo="";
    private String MobileOS="";
    private String MobileApp="";
    private String arrange="";
    private String listYN="";
    private String areaCode="";
    private String sigunguCode="";
    private String eventStartDate="";
    private String eventEndDate="";

    public FestivalRequestParam(String numOfRows, String pageNo, String mobileOS, String mobileApp, String arrange, String listYN, String areaCode, String sigunguCode, String eventStartDate, String eventEndDate) {
        this.numOfRows = numOfRows;
        this.pageNo = pageNo;
        MobileOS = mobileOS;
        MobileApp = mobileApp;
        this.arrange = arrange;
        this.listYN = listYN;
        this.areaCode = areaCode;
        this.sigunguCode = sigunguCode;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    @Override
    public String toString() {
        return "FestivalRequestParam{" +
                "numOfRows='" + numOfRows + '\'' +
                ", pageNo='" + pageNo + '\'' +
                ", MobileOS='" + MobileOS + '\'' +
                ", MobileApp='" + MobileApp + '\'' +
                ", arrange='" + arrange + '\'' +
                ", listYN='" + listYN + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", sigunguCode='" + sigunguCode + '\'' +
                ", eventStartDate='" + eventStartDate + '\'' +
                ", eventEndDate='" + eventEndDate + '\'' +
                '}';
    }

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

    public String getMobileOS() {
        return MobileOS;
    }

    public void setMobileOS(String mobileOS) {
        MobileOS = mobileOS;
    }

    public String getMobileApp() {
        return MobileApp;
    }

    public void setMobileApp(String mobileApp) {
        MobileApp = mobileApp;
    }

    public String getArrange() {
        return arrange;
    }

    public void setArrange(String arrange) {
        this.arrange = arrange;
    }

    public String getListYN() {
        return listYN;
    }

    public void setListYN(String listYN) {
        this.listYN = listYN;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getSigunguCode() {
        return sigunguCode;
    }

    public void setSigunguCode(String sigunguCode) {
        this.sigunguCode = sigunguCode;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }
}
