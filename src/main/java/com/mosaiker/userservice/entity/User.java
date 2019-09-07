package com.mosaiker.userservice.entity;

import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {
    @GeneratedValue
    private long uId;
    private String username;
    private String password;
    private String phone;
    private int status;  //status: 0 - 被禁用， 1 - 普通用户， 2 - 会员
    private Long firstin; //注册时间

    public User() {

    }

    public User(String username, String password, String phone, int status) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.status = status;
        this.firstin = 0L;
    }

    @Id
    @Column(name = "u_id", nullable = false)
    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }


    @Basic
    @Column(name = "username", nullable = false, length = 32)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "phone", nullable = false)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Basic
    @Column(name = "status", nullable = false)
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (uId != user.uId) return false;
        if (phone != null ? !phone.equals(user.phone) : user.phone != null) return false;
        if (status != user.status) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (uId ^ (uId >>> 32));
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + status;
        return result;
    }

    @Basic
    @Column(name = "firstin", nullable = false)
    public Long getFirstin() {
        return firstin;
    }

    public void setFirstin(Long firstin) {
        this.firstin = firstin;
    }
}
