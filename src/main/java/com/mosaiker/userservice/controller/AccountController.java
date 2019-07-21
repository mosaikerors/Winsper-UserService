package com.mosaiker.userservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.entity.User;
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
    public JSONObject updateAvatar(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        Account account = accountService.findAccountByUId(request.getLong("uId"));
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
    public JSONObject checkIn(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        Account account = accountService.findAccountByUId(request.getLong("uId"));
        if (account == null) {
            result.put("rescode", 1);//该uid不存在
            return result;
        }
        Long todayTime = new Date().getTime();
        if (account.getLastCheckIn().equals(todayTime)) {
            result.put("rescode", 3);  //  你今天已经签过到了
            return result;
        }
        account.setLastCheckIn(todayTime);
        account.setFeather(account.getFeather()+1);  //  羽毛+1
        accountService.updateAccount(account);
        result.put("message", "ok");
        result.put("newFeather",account.getFeather());
        return result;
    }

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public JSONObject follow(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        Account account = accountService.findAccountByUId(request.getLong("uId"));
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

    @RequestMapping(value = "/info/me/{uId}", method = RequestMethod.GET)
    public JSONObject getMyInfo(@PathVariable Long uId) {
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
        result.put("status",me.getStatus());
        result.put("rescode",0);
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
    result.put("rescode",0);
    return result;
  }

  @RequestMapping(value = "/privacy/message", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isMessage(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleMessage(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }

  @RequestMapping(value = "/privacy/hean", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isHean(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleHean(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }

  @RequestMapping(value = "/privacy/collection", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isCollection(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleCollection(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }

  @RequestMapping(value = "/privacy/diary", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isDiary(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleDiary(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }

  @RequestMapping(value = "/privacy/journal", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isJournal(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleJournal(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }

  @RequestMapping(value = "/privacy/submission", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isSubmission(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleSubmission(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }

  @RequestMapping(value = "/privacy/moodReport", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isMood(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleMood(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }

  @RequestMapping(value = "/privacy/comment", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject isComment(@RequestBody JSONObject request) {
    Boolean isPublic=request.getBoolean("toBePublic");
    Long uId = request.getLong("uId");
    Integer msg=accountService.toggleComment(isPublic,uId);
    JSONObject result = new JSONObject();
    result.put("rescode",msg);
    return result;
  }
}
