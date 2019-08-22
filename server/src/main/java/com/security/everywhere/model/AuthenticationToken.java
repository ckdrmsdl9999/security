package com.security.everywhere.model;

import java.util.Collection;

public class AuthenticationToken {
    private String nickName;
    private Collection authorities;
    private String token;

    public AuthenticationToken(String nickName, Collection authorities, String token) {
        this.nickName = nickName;
        this.authorities = authorities;
        this.token = token;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Collection getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection authorities) {
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
