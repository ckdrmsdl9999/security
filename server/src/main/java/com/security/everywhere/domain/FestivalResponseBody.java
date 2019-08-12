package com.security.everywhere.domain;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "body")
@XmlAccessorType(XmlAccessType.FIELD)
public class FestivalResponseBody {
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<FestivalResponseItem> items;
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

    public List<FestivalResponseItem> getItems() {
        return items;
    }

    public void setItems(List<FestivalResponseItem> items) {
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
