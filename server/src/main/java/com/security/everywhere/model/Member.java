package com.security.everywhere.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Member {
    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nickName;

    @Column(nullable = false, length = 200)
    private String pw;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @CreationTimestamp
    private Date regdate;

    @UpdateTimestamp
    private Date updateDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="nickName")
    private List<MemberRole> roles;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<MemberRole> getRoles() {
        return roles;
    }

    public void setRoles(List<MemberRole> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(nickName, member.nickName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickName);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", pw='" + pw + '\'' +
                ", email='" + email + '\'' +
                ", regdate=" + regdate +
                ", updateDate=" + updateDate +
                ", roles=" + roles +
                '}';
    }
}
