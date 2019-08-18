package com.security.everywhere.response.tour;

import com.security.everywhere.response.festival.FestivalItem;

import java.util.List;

public class TourList {
    private List<TourItem> item;

    public List<TourItem> getItem() {
        return item;
    }

    public void setItem(List<TourItem> item) {
        this.item = item;
    }
}
