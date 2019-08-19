package com.security.everywhere.request;

public class TourDetailIntroParam {
    private String numOfRows;       // 한 페이지 결과수
    private String pageNo;          // 현재 페이지 번호
    private String contentId;       // 콘텐츠 ID
    private String contentTypeId;   // 관광타입(관광지, 숙박 등) ID

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

    @Override
    public String toString() {
        return "TourDetailIntroParam{" +
                "numOfRows='" + numOfRows + '\'' +
                ", pageNo='" + pageNo + '\'' +
                ", contentId='" + contentId + '\'' +
                ", contentTypeId='" + contentTypeId + '\'' +
                '}';
    }
}