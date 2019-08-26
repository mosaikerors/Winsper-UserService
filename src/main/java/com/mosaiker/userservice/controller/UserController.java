package com.mosaiker.userservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.service.AccountService;
import com.mosaiker.userservice.service.TokenService;
import com.mosaiker.userservice.service.UserService;
import com.mosaiker.userservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private AccountService accountService;

  @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
  public JSONObject sendCode(@RequestBody JSONObject request) {
    String phone = request.getString("phone");
    JSONObject result = new JSONObject();
    if (userService.findUserByPhone(phone) != null) {
      result.put("rescode", 3);  //该手机号已被注册！
      return result;
    }
    String code = Utils.randomNumber(6);
    if (userService.sendCode(phone, code).equals("fail")) {
      result.put("rescode", 4);  //发送验证码失败，请稍后重试
      return result;
    }
    String token = tokenService.createCodeToken(phone, code, 5 * 60 * 1000L);
    result.put("rescode", 0);
    result.put("token", token);
    return result;
  }

  @RequestMapping(value = "/signup", method = RequestMethod.POST)
  public JSONObject signup(@RequestBody JSONObject request) {
    String token = request.getString("token");
    String phone = request.getString("phone");
    JSONObject result = new JSONObject();
    Integer msg = tokenService.verifyCodeToken(token, phone, request.getString("code"));
    if (msg == 0) {
      msg = userService.addUser(request.getString("username"), request.getString("phone"),
          request.getString("password"));
      //  新增Account
      long realUId = userService.findUserByPhone(request.getString("phone")).getuId();
      Account newAccount = new Account(realUId);
      accountService.addAccount(newAccount);
      result.put("rescode", msg);
      return result;
    } else {
      result.put("rescode", msg);
      return result;
    }
  }

  /*
   * 登录，根据requestBody中是否含有token字段来判定是否第一次登录
   * 第一次登录，需要提供手机号和密码，返回token
   * 后续登录，需要提供token和uId，若token没过期，就登录成功，并给token续命，然后返回新的token
   * 若token过期，就要求重新进行第一次登录
   * token是用uId构建的
   * */
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public JSONObject login(@RequestBody JSONObject request) {
    JSONObject result = new JSONObject();
    //第一次登录，不含token字段
    String token = request.getString("token");
    if (token == null) {
      User user = userService
          .findUserByPhoneAndPassword(request.getString("phone"), request.getString("password"));
      if (user != null) {
        String role = Utils.statusToRole(user.getStatus());
        if (role.equals("BANNED")) {
          result.put("rescode", 3);
          return result;
        }
        Long uIdgot = user.getuId();
        Account account = accountService.findAccountByUId(uIdgot);
        result = account.toJSONObject();
        String newToken = tokenService.createToken(uIdgot, role);
        result.put("rescode", 0);
        result.put("token", newToken);
        result.put("username", user.getUsername());
        result.put("status", user.getStatus());
        return result;
      }
      result.put("rescode", 4);
      return result;
    } else {
      //后续登录，只含token字段和uId字段
      //解析并验证token，检查token是否过期，密码改变和状态被禁用都会使token失效
      Long uId = request.getLong("uId");
      JSONObject userInfo = tokenService.parseToken(token, uId);
      if (!userInfo.getInteger("message").equals(0)) {
        result.put("rescode", userInfo.getInteger("message"));
        return result;
      }
      //该token有效，获取token对应用户，该用户状态正常，密码没变
      User user = userService.findUserByUId(uId);
      //根据该用户当前最新状态更新token
      String role = Utils.statusToRole(user.getStatus());
      Account account = accountService.findAccountByUId(uId);
      result = account.toJSONObject();
      String newToken = tokenService.createToken(uId, role);
      result.put("rescode", 0);
      result.put("token", newToken);
      result.put("username", user.getUsername());
      result.put("status", user.getStatus());
      return result;
    }
  }

  /*
   * 更新username
   * */
  @RequestMapping(value = "/username/update", method = RequestMethod.PUT)
  public JSONObject updateInfo(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    JSONObject result = new JSONObject();
    User user = userService.findUserByUId(uId);
    if (user == null) {
      result.put("rescode", 1);//该uId不存在
      return result;
    }
    user.setUsername(request.getString("username"));
    userService.updateUser(user);
    result.put("rescode", 0);
    return result;
  }

  @RequestMapping(value = "/getSimpleInfo", method = RequestMethod.GET)
  public JSONObject getSimpleInfo(@RequestHeader("uId") Long uId) {
    JSONObject result = new JSONObject();
    User user = userService.findUserByUId(uId);
    Account account = accountService.findAccountByUId(uId);
    if (user == null || account == null) {
      result.put("rescode", 1);
      return result;
    }
    result.put("rescode", 0);
    result.put("uId", uId);
    result.put("avatarUrl", account.getAvatarUrl());
    result.put("username", user.getUsername());
    result.put("isHeanPublic", account.getHeanPublic());
    result.put("isCollectionPublic", account.getCollectionPublic());
    return result;
  }

  @RequestMapping(value = "/forget/update", method = RequestMethod.POST)
  public JSONObject forgetPassword(@RequestBody JSONObject request) {
    String token = request.getString("token");
    String phone = request.getString("phone");
    JSONObject result = new JSONObject();
    Integer msg = tokenService.verifyCodeToken(token, phone, request.getString("code"));
    if (msg == 0) {
      User user = userService.findUserByPhone(phone);
      user.setPassword(request.getString("password"));
      userService.updateUser(user);
      result.put("rescode", 0);
      return result;
    } else {
      result.put("rescode", msg);
      return result;
    }
  }

  @RequestMapping(value = "/forget/sendCode", method = RequestMethod.POST)
  public JSONObject forgetSendCode(@RequestBody JSONObject request) {
    String phone = request.getString("phone");
    JSONObject result = new JSONObject();
    if (userService.findUserByPhone(phone) == null) {
      result.put("rescode", 3);  //该手机号不存在！
      return result;
    }
    String code = Utils.randomNumber(6);
    if (userService.sendCode(phone, code).equals("fail")) {
      result.put("rescode", 4);  //发送验证码失败，请稍后重试
      return result;
    }
    String token = tokenService.createCodeToken(phone, code, 5 * 60 * 1000L);
    result.put("rescode", 0);
    result.put("token", token);
    return result;
  }

  @RequestMapping(value = "/password/update", method = RequestMethod.PUT)
  public JSONObject updatePassword(@RequestBody JSONObject request, @RequestHeader("uId") Long uId) {
    String password = request.getString("password");
    JSONObject result = new JSONObject();
    User user = userService.findUserByUId(uId);
    user.setPassword(password);
    String newToken = tokenService.createToken(uId, Utils.statusToRole(user.getStatus()));
    userService.updateUser(user);
    result.put("rescode", 0);
    result.put("token", newToken);
    return result;

  }
}
