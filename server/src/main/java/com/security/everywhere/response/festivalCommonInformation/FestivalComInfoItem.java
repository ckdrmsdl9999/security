package com.security.everywhere.response.festivalCommonInformation;

public class FestivalComInfoItem {
    private String contentid;       // 콘텐츠ID
    private String contenttypeid;   // 콘텐츠타입ID
    private String createdtime;     // 콘텐츠 최초 등록일
    private String homepage;        // 홈페이지 주소
    private String modifiedtime;    // 콘텐츠 수정일
    private String overview;        // 콘텐츠 개요 조회
    private String tel;             // 전화번호
    private String telname;         // 전화번호명
    private String title;           // 콘텐츠명(제목)

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getContenttypeid() {
        return contenttypeid;
    }

    public void setContenttypeid(String contenttypeid) {
        this.contenttypeid = contenttypeid;
    }

    public String getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(String createdtime) {
        this.createdtime = createdtime;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getModifiedtime() {
        return modifiedtime;
    }

    public void setModifiedtime(String modifiedtime) {
        this.modifiedtime = modifiedtime;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTelname() {
        return telname;
    }

    public void setTelname(String telname) {
        this.telname = telname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
