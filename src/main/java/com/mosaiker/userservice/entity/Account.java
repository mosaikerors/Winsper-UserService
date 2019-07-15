package com.mosaiker.userservice.entity;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {
    private long uId;
    private String avatarUrl;
    private long feather;
    private List<Account> follower;
    private List<Account> following;
    private Long lastCheckIn;

    public Account() {
    }

    public Account(long uId) {
        this.uId = uId;
        this.avatarUrl = "";
        this.feather = 0;
        this.follower = new ArrayList<>();
        this.following = new ArrayList<>();
        this.lastCheckIn = 0L;
    }

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getFeather() {
        return feather;
    }

    public void setFeather(long feather) {
        this.feather = feather;
    }

    public List<Account> getFollower() {
        return follower;
    }

    public void setFollower(List<Account> follower) {
        this.follower = follower;
    }

    public List<Account> getFollowing() {
        return following;
    }

    public void setFollowing(List<Account> following) {
        this.following = following;
    }

    public Long getLastCheckIn() {
        return lastCheckIn;
    }

    public void setLastCheckIn(Long lastCheckIn) {
        this.lastCheckIn = lastCheckIn;
    }
}
