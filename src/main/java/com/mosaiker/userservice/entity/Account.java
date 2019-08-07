package com.mosaiker.userservice.entity;

import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;

@Entity
public class Account {
  @Id
  private Long uId;
  private String avatarUrl;
  private long feather;
  @ElementCollection
  private List<Long> follower;
  @ElementCollection
  private List<Long> following;
  private Long lastCheckIn;
  private Boolean isMessagePublic;
  private Boolean isHeanPublic;
  private Boolean isCollectionPublic;
  private Boolean isDiaryPublic;
  private Boolean isJournalPublic;
  private Boolean isSubmissionPublic;
  private Boolean isMoodReportPublic;
  private Boolean isCommentPublic;

  public Account() {
  }

  public Account(long uId) {
    this.uId = uId;
    this.avatarUrl = "";
    this.feather = 0;
    this.follower = new ArrayList<>();
    this.following = new ArrayList<>();
    this.lastCheckIn = 0L;
    this.isMessagePublic = true;
    this.isHeanPublic = true;
    this.isCollectionPublic = true;
    this.isDiaryPublic = true;
    this.isJournalPublic = true;
    this.isSubmissionPublic = true;
    this.isMoodReportPublic = true;
    this.isCommentPublic = true;
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


  public List<Long> getFollower() {
    return follower;
  }

  public void setFollower(List<Long> follower) {
    this.follower = follower;
  }

  public List<Long> getFollowing() {
    return following;
  }

  public void setFollowing(List<Long> following) {
    this.following = following;
  }

  public Long getLastCheckIn() {
    return lastCheckIn;
  }

  public void setLastCheckIn(Long lastCheckIn) {
    this.lastCheckIn = lastCheckIn;
  }

  public Boolean getMessagePublic() {
    return isMessagePublic;
  }

  public void setMessagePublic(Boolean messagePublic) {
    isMessagePublic = messagePublic;
  }

  public Boolean getHeanPublic() {
    return isHeanPublic;
  }

  public void setHeanPublic(Boolean heanPublic) {
    isHeanPublic = heanPublic;
  }

  public Boolean getCollectionPublic() {
    return isCollectionPublic;
  }

  public void setCollectionPublic(Boolean collectionPublic) {
    isCollectionPublic = collectionPublic;
  }

  public Boolean getDiaryPublic() {
    return isDiaryPublic;
  }

  public void setDiaryPublic(Boolean diaryPublic) {
    isDiaryPublic = diaryPublic;
  }

  public Boolean getJournalPublic() {
    return isJournalPublic;
  }

  public void setJournalPublic(Boolean journalPublic) {
    isJournalPublic = journalPublic;
  }

  public Boolean getSubmissionPublic() {
    return isSubmissionPublic;
  }

  public void setSubmissionPublic(Boolean submissionPublic) {
    isSubmissionPublic = submissionPublic;
  }

  public Boolean getMoodReportPublic() {
    return isMoodReportPublic;
  }

  public void setMoodReportPublic(Boolean moodReportPublic) {
    isMoodReportPublic = moodReportPublic;
  }

  public Boolean getCommentPublic() {
    return isCommentPublic;
  }

  public void setCommentPublic(Boolean commentPublic) {
    isCommentPublic = commentPublic;
  }

  public JSONObject toJSONObject() {
    JSONObject info = new JSONObject();
    info.put("uId",this.uId);
    info.put("avater", this.avatarUrl);
    info.put("feather", this.feather);
    List<Long> mutual = this.following;
    mutual.retainAll(this.follower);
    info.put("mutualFollow", mutual.size());
    info.put("following", this.following.size());
    info.put("followers", this.follower.size());

    Date t = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    if (df.format(new Date(this.lastCheckIn)).equals(df.format(new Date()))) {
      info.put("hasChecked", true);
    } else {
      info.put("hasChecked", false);
    }
    return info;
  }

  public JSONObject toViewedJSONObject() {
    JSONObject info = this.toJSONObject();

    info.put("isMessagePublic", this.isMessagePublic);
    info.put("isHeanPublic", this.isHeanPublic);
    info.put("isCollectionPublic", this.isCollectionPublic);
    info.put("isDiaryPublic", this.isDiaryPublic);
    info.put("isJournalPublic", this.isJournalPublic);
    info.put("isSubmissionPublic", this.isSubmissionPublic);
    info.put("isMoodReportPublic", this.isMoodReportPublic);
    info.put("isCommentPublic", this.isCommentPublic);
    return info;
  }
}
