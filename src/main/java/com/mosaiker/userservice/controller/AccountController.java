package com.mosaiker.userservice.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.repository.UserRepository;
import com.mosaiker.userservice.service.AccountService;
import com.mosaiker.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class AccountController {

  @Autowired
  private AccountService accountService;
  @Autowired
  private UserService userService;

  @RequestMapping(value = "/update/avatar", method = RequestMethod.PUT)
  public JSONObject updateAvatar(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    JSONObject result = new JSONObject();
    Account account = accountService.findAccountByUId(uId);
    if (account == null) {
      result.put("rescode", 1);//该uid不存在
      return result;
    }
    account.setAvatarUrl(request.getString("avatar"));
    accountService.updateAccount(account);
    result.put("rescode", 0);
    return result;
  }

  @RequestMapping(value = "/check", method = RequestMethod.PUT)
  public JSONObject checkIn(@RequestHeader("uId") Long uId) {
    JSONObject result = new JSONObject(true);
    Account account = accountService.findAccountByUId(uId);
    if (account == null) {
      result.put("rescode", 1);//该uid不存在
      return result;
    }
    Date now = new Date();
    int day = now.getDate();
    if (account.getLastCheckIn() != 0) {
      if (new Date(account.getLastCheckIn()).getDate() == day) {
        result.put("rescode", 3);  //  你今天已经签过到了
        return result;
      }
    }
    account.setLastCheckIn(now.getTime());
    account.setFeather(account.getFeather() + 1);  //  羽毛+1
    accountService.updateAccount(account);
    result.put("rescode", 0);
    result.put("newFeather", account.getFeather());
    return result;
  }

  @RequestMapping(value = "/follow", method = RequestMethod.POST)
  public JSONObject follow(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    JSONObject result = new JSONObject();
    Account account = accountService.findAccountByUId(uId);
    Account targetAccount = accountService.findAccountByUId(request.getLong("targetUId"));
    if (account == null || targetAccount == null) {
      result.put("rescode", 1);//双方至少有一方uid不存在
      return result;
    }
    List<Long> following = account.getFollowing();
    List<Long> targetFollower = targetAccount.getFollower();
    following.add(targetAccount.getuId());
    targetFollower.add(account.getuId());
    account.setFollowing(following);
    targetAccount.setFollower(targetFollower);
    accountService.updateAccount(account);
    accountService.updateAccount(targetAccount);
    result.put("rescode", 0);
    return result;
  }

  @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
  public JSONObject unfollow(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    JSONObject result = new JSONObject();
    Account account = accountService.findAccountByUId(uId);
    Account targetAccount = accountService.findAccountByUId(request.getLong("targetUId"));
    if (account == null || targetAccount == null) {
      result.put("rescode", 1);//双方至少有一方uid不存在
      return result;
    }
    List<Long> following = account.getFollowing();
    List<Long> targetFollower = targetAccount.getFollower();
    if (!following.contains(targetAccount.getuId())) {
      result.put("rescode", 3);//未关注此人
      return result;
    }
    following.remove(targetAccount.getuId());
    targetFollower.remove(account.getuId());
    account.setFollowing(following);
    targetAccount.setFollower(targetFollower);
    accountService.updateAccount(account);
    accountService.updateAccount(targetAccount);
    result.put("rescode", 0);
    return result;
  }

  @RequestMapping(value = "/info/me", method = RequestMethod.GET)
  public JSONObject getMyInfo(@RequestHeader("uId") Long uId) {
    JSONObject result;
    Account account = accountService.findAccountByUId(uId);
    if (account == null) {
      result = new JSONObject();
      result.put("rescode", 1);//该uid不存在
      return result;
    }
    User me = userService.findUserByUId(uId);
    result = account.toJSONObject();
    result.put("username", me.getUsername());
    result.put("status", me.getStatus());
    result.put("rescode", 0);
    return result;
  }

  @RequestMapping(value = "/info/{uId}", method = RequestMethod.GET)
  public JSONObject getAccountInfo(@PathVariable Long uId) {
    JSONObject result;
    Account account = accountService.findAccountByUId(uId);
    if (account == null) {
      result = new JSONObject();
      result.put("rescode", 1);//该uid不存在
      return result;
    }
    User me = userService.findUserByUId(uId);
    result = account.toViewedJSONObject();
    result.put("username", me.getUsername());
    result.put("rescode", 0);
    return result;
  }

  @RequestMapping(value = "/privacy/message", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isMessage(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleMessage(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/privacy/hean", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isHean(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleHean(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/privacy/collection", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isCollection(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleCollection(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/privacy/diary", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isDiary(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleDiary(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/privacy/journal", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isJournal(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleJournal(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/privacy/submission", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isSubmission(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleSubmission(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/privacy/moodReport", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isMood(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleMood(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/privacy/comment", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isComment(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    Boolean isPublic = request.getBoolean("toBePublic");
    Integer msg = accountService.toggleComment(isPublic, uId);
    JSONObject result = new JSONObject();
    result.put("rescode", msg);
    return result;
  }

  @RequestMapping(value = "/followlist/followings", method = RequestMethod.GET)
  @ResponseBody
  public JSONObject getFollowings(@RequestHeader("uId") Long uId) {
    Account account = accountService.findAccountByUId(uId);
    JSONObject result = new JSONObject();
    result.put("rescode", 0);
    result.put("followlist", toFollowlist(account.getFollowing(), uId));
    return result;
  }

  @RequestMapping(value = "/followlist/followers", method = RequestMethod.GET)
  @ResponseBody
  public JSONObject getFollowers(@RequestHeader("uId") Long uId) {
    Account account = accountService.findAccountByUId(uId);
    JSONObject result = new JSONObject();
    result.put("rescode", 0);
    result.put("followlist", toFollowlist(account.getFollower(), uId));
    return result;
  }

  @RequestMapping(value = "/followlist/mutual", method = RequestMethod.GET)
  @ResponseBody
  public JSONObject getMutualFollows(@RequestHeader("uId") Long uId) {
    Account account = accountService.findAccountByUId(uId);
    JSONObject result = new JSONObject();
    result.put("rescode", 0);
    result.put("followlist", toFollowlist(account.getMutualFollows(), uId));
    return result;
  }

  private JSONArray toFollowlist(List<Long> list, Long uId) {
    JSONArray jsonList = new JSONArray();
    for (Long one : list) {
      JSONObject oneFollow = new JSONObject();
      oneFollow.put("uId", one);
      Account found = accountService.findAccountByUId(one);
      oneFollow.put("username", userService.findUserByUId(one).getUsername());
      oneFollow.put("avatar", found.getAvatarUrl());
      oneFollow.put("isMutualFollow", found.getMutualFollows().contains(uId));
      jsonList.add(oneFollow);
    }
    return jsonList;
  }


}
