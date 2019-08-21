package com.security.everywhere.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(length = 100)
    private String nickName;
    @Column(length = 100)
    private String pw;

    public User() {
    }

    public User(String nickName, String pw) {
        this.nickName = nickName;
        this.pw = pw;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

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
