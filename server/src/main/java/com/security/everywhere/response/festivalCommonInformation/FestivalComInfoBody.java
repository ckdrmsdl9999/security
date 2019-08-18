package com.security.everywhere.response.festivalCommonInformation;

public class FestivalComInfoBody {
    private FestivalComInfoItems items;
    private String numOfRows;
    private String pageNo;
    private String totalCount;

    public FestivalComInfoItems getItems() {
        return items;
    }

    public void setItems(FestivalComInfoItems items) {
        this.items = items;
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

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }
}
