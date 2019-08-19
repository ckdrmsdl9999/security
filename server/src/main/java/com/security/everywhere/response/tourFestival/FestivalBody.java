package com.security.everywhere.response.tourFestival;

public class FestivalBody {
    private FestivalItems items;
    private String numOfRows;
    private String pageNo;
    private String totalCount;

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

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public FestivalItems getItems() {
        return items;
    }

    public void setItems(FestivalItems items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "FestivalResponseBody{" +
                "numOfRows='" + numOfRows + '\'' +
                ", pageNo='" + pageNo + '\'' +
                ", totalCount='" + totalCount + '\'' +
                '}';
    }
}
