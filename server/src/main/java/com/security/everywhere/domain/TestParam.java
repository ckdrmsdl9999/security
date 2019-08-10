package com.security.everywhere.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public class TestParam {
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
