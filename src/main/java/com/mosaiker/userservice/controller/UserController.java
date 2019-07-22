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
        if (msg==0) {
            msg = userService.addUser(request.getString("username"), request.getString("phone"), request.getString("password"));
            //  新增Account
            long realUId = userService.findUserByPhone(request.getString("phone")).getuId();
            Account newAccount = new Account(realUId);
            accountService.addAccount(newAccount);
            result.put("rescode", msg);
            return result;
        }
        else{
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
        JSONObject result=new JSONObject() ;
        //第一次登录，不含token字段
        String token = request.getString("token");
        if (token == null) {
            User user = userService.findUserByPhoneAndPassword(request.getString("phone"), request.getString("password"));
            if (user != null) {
                String role = Utils.statusToRole(user.getStatus());
                if (role.equals("BANNED")) {
                    result.put("rescode", 3);
                    return result;
                }
                Long uId = user.getuId();
                Account account = accountService.findAccountByUId(uId);
                result = account.toJSONObject();
                String newToken = tokenService.createToken(uId, role);
                result.put("rescode", 0);
                result.put("token", newToken);
                result.put("username",user.getUsername());
                result.put("status", user.getStatus());
                return result;
            }
            result.put("rescode", 4);
            return result;
        } else {
            //后续登录，只含token字段和uId字段
            //解析并验证token，检查token是否过期，密码改变和状态被禁用都会使token失效
            JSONObject userInfo = tokenService.parseToken(token, request.getLong("uId"));
            if (!userInfo.getInteger("message").equals(0)) {
                result.put("rescode", userInfo.getInteger("message"));
                return result;
            }
            //该token有效，获取token对应用户，该用户状态正常，密码没变
            User user = userService.findUserByUId(request.getLong("uId"));
            //根据该用户当前最新状态更新token
            String role = Utils.statusToRole(user.getStatus());
            if (role.equals("BANNED")) {
                result.put("rescode",3);
                return result;
            }else {
                Account account = accountService.findAccountByUId(request.getLong("uId"));
                result = account.toJSONObject();
                String newToken = tokenService.createToken(userInfo.getLong("uId"), role);
                result.put("rescode", 0);
                result.put("token", newToken);
                result.put("username",user.getUsername());
                result.put("status", user.getStatus());
                return result;
            }
        }
    }

    @RequestMapping(value = "/update/username", method = RequestMethod.PUT)
    public JSONObject updateInfo(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        User user = userService.findUserByUId(request.getLong("uId"));
        if (user == null) {
            result.put("rescode", 1);//该uId不存在
            return result;
        }
        user.setUsername(request.getString("username"));
        userService.updateUser(user);
        result.put("rescode", 0);
        return result;
    }

    /*
    * {"uId":10000,"token":"efwfsef.fefesf.efsefsef","roles":["USER","SUPERUSER"]}
    * the roles can be empty:
    * {"uId":10000,"token":"efwfsef.fefesf.efsefsef","roles":[]}
    * */
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public JSONObject anthenticate(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        String token = request.getString("token");
        List<String> roleArray = request.getJSONArray("roles").toJavaList(String.class);
        System.out.println(token);
        System.out.println(request.getLong("uId"));
        System.out.println(roleArray);
        if (token==null||!tokenService.verifyTokenRoleHave(token, request.getLong("uId"), roleArray)) {
            result.put("message", "uauth1");  //抱歉，你没有这个权限
            return result;
        }
        User user = userService.findUserByUId(request.getLong("uId"));
        if (user == null) {
            result.put("message", "uauth2");  //该用户id不存在
            return result;
        }
        result.put("message", "ok");
        return result;
    }

    @RequestMapping(value = "/getSimpleInfo", method = RequestMethod.GET)
    public JSONObject getSimpleInfo(@RequestParam Long uId) {
        JSONObject result = new JSONObject();
        User user = userService.findUserByUId(uId);
        Account account = accountService.findAccountByUId(uId);
        if (user == null || account == null) {
            result.put("message", "ainfo1");
            return result;
        }
        result.put("message", "ok");
        result.put("avatarUrl", account.getAvatarUrl());
        result.put("username", user.getUsername());
        result.put("isHeanPublic",account.getHeanPublic());
        result.put("isCollectionPublic",account.getCollectionPublic());
        return result;
    }
}
