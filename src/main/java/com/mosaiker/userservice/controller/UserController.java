package com.mosaiker.userservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.service.TokenService;
import com.mosaiker.userservice.service.UserService;
import com.mosaiker.userservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    public JSONObject sendCode(@RequestBody JSONObject request) {
        String phone = request.getString("phone");
        JSONObject result = new JSONObject();
        if (userService.findUserByPhone(phone) != null) {
            result.put("message", "该手机号已被注册！");
            return result;
        }
        String code = Utils.randomNumber(6);
        if (userService.sendCode(phone, code).equals("fail")) {
            result.put("message", "发送验证码失败，请稍后重试");
            return result;
        }
        String token = tokenService.createCodeToken(phone, code, 5 * 60 * 1000L);
        result.put("message", "ok");
        result.put("token", token);
        return result;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public JSONObject signup(@RequestBody JSONObject request) {
        String token = request.getString("token");
        String phone = request.getString("phone");
        JSONObject result = new JSONObject();
        String msg = tokenService.verifyCodeToken(token, phone, request.getString("code"));
        if (msg.equals("ok")) {
            msg = userService.addUser(request.getString("username"), request.getString("phone"), request.getString("password"));
            result.put("message", msg);
            return result;
        }
        else{
            result.put("message", msg);
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
            User user = userService.findUserByPhoneAndPassword(request.getString("phone"), request.getString("password"));
            if (user != null) {
                String role = Utils.statusToRole(user.getStatus());
                if (role.equals("BANNED")) {
                    result.put("message", "该用户已被禁用");
                    return result;
                }
                Long uId = user.getuId();
                String newToken = tokenService.createToken(uId, role);
                result.put("message", "ok");
                result.put("token", newToken);
                result.put("uId", uId);
                result.put("username",user.getUsername());
                result.put("status", user.getStatus());
                return result;
            }
            result.put("message", "手机号或密码不正确");
            return result;
        } else {
            //后续登录，只含token字段和uId字段
            //解析并验证token
            JSONObject userInfo = tokenService.parseToken(token, request.getLong("uId"));
            if (!userInfo.getString("message").equals("ok")) {
                result.put("message", userInfo.getString("message"));
                return result;
            }
            //该token有效，获取token对应用户
            User user = userService.findUserByUId(request.getLong("uId"));
            //根据该用户当前最新状态返回禁用信息或更新token
            String role = Utils.statusToRole(user.getStatus());
            if (role.equals("BANNED")) {
                result.put("message", "当前用户已被禁用");
                return result;
            }
            String newToken = tokenService.createToken(userInfo.getLong("uId"), role);
            result.put("message", "ok");
            result.put("token", newToken);
            result.put("uId", request.getLong("uId"));
            result.put("username",user.getUsername());
            result.put("status", user.getStatus());
            return result;
        }
    }

    @RequestMapping(value = "/updateInfo", method = RequestMethod.PUT)
    public JSONObject updateInfo(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();
        User user = userService.findUserByUId(request.getLong("uId"));
        if (user == null) {
            result.put("message", "该用户id不存在");
            return result;
        }
        user.setUsername(request.getString("username"));
        userService.updateUser(user);
        result.put("message", "ok");
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
        if (token==null||!tokenService.verifyTokenRoleHave(token, request.getLong("uId"), roleArray)) {
            result.put("message", "抱歉，你没有这个权限");
            return result;
        }
        User user = userService.findUserByUId(request.getLong("uId"));
        if (user == null) {
            result.put("message", "该用户id不存在");
            return result;
        }
        result.put("message", "ok");
        return result;
    }
}
