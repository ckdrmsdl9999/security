package com.security.everywhere.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
public class FestivalResponseItem {
    @XmlElement(name = "addr1")
    private String addr1;
    @XmlElement(name = "addr2")
    private String addr2;
    @XmlElement(name = "areacode")
    private String areacode;
    @XmlElement(name = "booktour")
    private String booktour;
    @XmlElement(name = "cat1")
    private String cat1;
    @XmlElement(name = "cat2")
    private String cat2;
    @XmlElement(name = "cat3")
    private String cat3;
    @XmlElement(name = "contenid")
    private String contenid;
    @XmlElement(name = "contenttyprld")
    private String contenttyprld;
    @XmlElement(name = "createdtime")
    private String createdtime;
    @XmlElement(name = "firstimage")
    private String firstimage;
    @XmlElement(name = "firstimage2")
    private String firstimage2;
    @XmlElement(name = "mapX")
    private String mapX;
    @XmlElement(name = "mapY")
    private String mapY;
    @XmlElement(name = "mlevel")
    private String mlevel;
    @XmlElement(name = "modifiedtime")
    private String modifiedtime;
    @XmlElement(name = "readcount")
    private String readcount;
    @XmlElement(name = "sigungucode")
    private String sigungucode;
    @XmlElement(name = "tel")
    private String tel;
    @XmlElement(name = "title")
    private String title;
    @XmlElement(name = "eventstartdate")
    private String eventstartdate;
    @XmlElement(name = "eventenddate")
    private String eventenddate;
    @XmlElement(name = "totalCnt")
    private String totalCnt;

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public String getBooktour() {
        return booktour;
    }

    public void setBooktour(String booktour) {
        this.booktour = booktour;
    }

    public String getCat1() {
        return cat1;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public String getCat2() {
        return cat2;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public String getCat3() {
        return cat3;
    }

    public void setCat3(String cat3) {
        this.cat3 = cat3;
    }

    public String getContenid() {
        return contenid;
    }

    public void setContenid(String contenid) {
        this.contenid = contenid;
    }

    public String getContenttyprld() {
        return contenttyprld;
    }

    public void setContenttyprld(String contenttyprld) {
        this.contenttyprld = contenttyprld;
    }

    public String getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(String createdtime) {
        this.createdtime = createdtime;
    }

    public String getFirstimage() {
        return firstimage;
    }

    public void setFirstimage(String firstimage) {
        this.firstimage = firstimage;
    }

    public String getFirstimage2() {
        return firstimage2;
    }

    public void setFirstimage2(String firstimage2) {
        this.firstimage2 = firstimage2;
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

    public String getMlevel() {
        return mlevel;
    }

    public void setMlevel(String mlevel) {
        this.mlevel = mlevel;
    }

    public String getModifiedtime() {
        return modifiedtime;
    }

    public void setModifiedtime(String modifiedtime) {
        this.modifiedtime = modifiedtime;
    }

    public String getReadcount() {
        return readcount;
    }

    public void setReadcount(String readcount) {
        this.readcount = readcount;
    }

    public String getSigungucode() {
        return sigungucode;
    }

    public void setSigungucode(String sigungucode) {
        this.sigungucode = sigungucode;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventstartdate() {
        return eventstartdate;
    }

    public void setEventstartdate(String eventstartdate) {
        this.eventstartdate = eventstartdate;
    }

    public String getEventenddate() {
        return eventenddate;
    }

    public void setEventenddate(String eventenddate) {
        this.eventenddate = eventenddate;
    }

    public String getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(String totalCnt) {
        this.totalCnt = totalCnt;
    }
}
