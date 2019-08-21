package com.security.everywhere.request;

public class UserParam {
    private String nickName = "";
    private String pw = "";

    public String getNickName() { return nickName; }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
