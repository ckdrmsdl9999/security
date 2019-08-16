package com.security.everywhere.model;

import com.security.everywhere.response.FestivalItem;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "festival")
public class Festival implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String addr1 = "";
    private String addr2 = "";
    private String areaCode = "";
    private String bookTour = "";
    private String cat1 = "";
    private String cat2 = "";
    private String cat3 = "";
    private String contentId = "";
    private String contentTypeId = "";
    private String createdTime = "";
    private String firstImage= "";
    private String firstImage2= "";
    private String mapX= "";
    private String mapY= "";
    private String mLevel= "";
    private String modifiedTime = "";
    private String readCount= "";
    private String sigunguCode= "";
    private String tel= "";
    private String title= "";
    private String eventStartDate = "";
    private String eventEndDate = "";

    protected Festival() {
    }

    public Festival(FestivalItem item) {
        this.addr1 = item.getAddr1();
        this.addr2 = item.getAddr2();
        this.areaCode = item.getAreacode();
        this.bookTour = item.getBooktour();
        this.cat1 = item.getCat1();
        this.cat2 = item.getCat2();
        this.cat3 = item.getCat3();
        this.contentId = item.getContentid();
        this.contentTypeId = item.getContenttypeid();
        this.createdTime = item.getCreatedtime();
        this.firstImage = item.getFirstimage();
        this.firstImage2 = item.getFirstimage2();
        this.mapX = item.getMapx();
        this.mapY = item.getMapy();
        this.mLevel = item.getMlevel();
        this.modifiedTime = item.getModifiedtime();
        this.readCount = item.getReadcount();
        this.sigunguCode = item.getSigungucode();
        this.tel = item.getTel();
        this.title = item.getTitle();
        this.eventStartDate = item.getEventstartdate();
        this.eventEndDate = item.getEventenddate();
    }

    public Festival(String addr1, String addr2, String areaCode, String bookTour, String cat1, String cat2, String cat3, String contentId, String contentTypeId, String createdTime, String firstImage, String firstImage2, String mapX, String mapY, String mLevel, String modifiedTime, String readCount, String sigunguCode, String tel, String title, String eventStartDate, String eventEndDate) {
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.areaCode = areaCode;
        this.bookTour = bookTour;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.contentId = contentId;
        this.contentTypeId = contentTypeId;
        this.createdTime = createdTime;
        this.firstImage = firstImage;
        this.firstImage2 = firstImage2;
        this.mapX = mapX;
        this.mapY = mapY;
        this.mLevel = mLevel;
        this.modifiedTime = modifiedTime;
        this.readCount = readCount;
        this.sigunguCode = sigunguCode;
        this.tel = tel;
        this.title = title;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getBookTour() {
        return bookTour;
    }

    public void setBookTour(String bookTour) {
        this.bookTour = bookTour;
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

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(String contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public String getFirstImage2() {
        return firstImage2;
    }

    public void setFirstImage2(String firstImage2) {
        this.firstImage2 = firstImage2;
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

    public String getmLevel() {
        return mLevel;
    }

    public void setmLevel(String mLevel) {
        this.mLevel = mLevel;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getReadCount() {
        return readCount;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    public String getSigunguCode() {
        return sigunguCode;
    }

    public void setSigunguCode(String sigunguCode) {
        this.sigunguCode = sigunguCode;
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

    @Override
    public String toString() {
        return "Festival{" +
                "id=" + id +
                ", addr1='" + addr1 + '\'' +
                ", addr2='" + addr2 + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", bookTour='" + bookTour + '\'' +
                ", cat1='" + cat1 + '\'' +
                ", cat2='" + cat2 + '\'' +
                ", cat3='" + cat3 + '\'' +
                ", contentId='" + contentId + '\'' +
                ", contentTypeId='" + contentTypeId + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", firstImage='" + firstImage + '\'' +
                ", firstImage2='" + firstImage2 + '\'' +
                ", mapX='" + mapX + '\'' +
                ", mapY='" + mapY + '\'' +
                ", mLevel='" + mLevel + '\'' +
                ", modifiedTime='" + modifiedTime + '\'' +
                ", readCount='" + readCount + '\'' +
                ", sigunguCode='" + sigunguCode + '\'' +
                ", tel='" + tel + '\'' +
                ", title='" + title + '\'' +
                ", eventStartDate='" + eventStartDate + '\'' +
                ", eventEndDate='" + eventEndDate + '\'' +
                '}';
    }
}
