package com.mosaiker.userservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/updateAvatar", method = RequestMethod.PUT)
    public JSONObject updateAvatar(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        Account account = accountService.findAccountByUId(request.getLong("uId"));
        if (account == null) {
            result.put("message", "ainfo1");
            return result;
        }
        account.setAvatarUrl(request.getString("avatarUrl"));
        accountService.updateAccount(account);
        result.put("message", "ok");
        return result;
    }

    @RequestMapping(value = "/checkIn", method = RequestMethod.PUT)
    public JSONObject checkIn(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        Account account = accountService.findAccountByUId(request.getLong("uId"));
        if (account == null) {
            result.put("message", "ainfo1");
            return result;
        }
        Long todayTime = new Date().getTime();
        if (account.getLastCheckIn().equals(todayTime)) {
            result.put("message", "ainfo2");  //  你今天已经签过到了
            return result;
        }
        account.setLastCheckIn(todayTime);
        account.setFeather(account.getFeather()+1);  //  羽毛+1
        accountService.updateAccount(account);
        result.put("message", "ok");
        return result;
    }

    @RequestMapping(value = "/follow", method = RequestMethod.PUT)
    public JSONObject follow(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        Account account = accountService.findAccountByUId(request.getLong("uId"));
        Account targetAccount = accountService.findAccountByUId(request.getLong("targetUId"));
        if (account == null || targetAccount == null) {
            result.put("message", "ainfo1");
            return result;
        }
        List<Account> following = account.getFollowing();
        List<Account> targetFollower = targetAccount.getFollower();
        following.add(targetAccount);
        targetFollower.add(account);
        account.setFollowing(following);
        targetAccount.setFollower(targetFollower);
        accountService.updateAccount(account);
        accountService.updateAccount(targetAccount);
        result.put("message", "ok");
        return result;
    }

    @RequestMapping(value = "/getAccountInfo/{uId}", method = RequestMethod.GET)
    public JSONObject getAccountInfo(@PathVariable Long uId) {
        JSONObject result = new JSONObject();
        Account account = accountService.findAccountByUId(uId);
        if (account == null) {
            result.put("message", "ainfo1");
            return result;
        }
        result.put("message", "ok");
        result.put("accountInfo", account);
        return result;
    }
}
