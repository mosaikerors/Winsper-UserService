package com.mosaiker.userservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.service.TokenService;
import com.mosaiker.userservice.service.UserService;
import com.mosaiker.userservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping("/sendCode")
    public JSONObject sendCode(HttpServletRequest request, String phone) {
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
        HttpSession session = request.getSession();
        JSONObject codeDetails = new JSONObject();
        codeDetails.put("verifyCode", code);
        codeDetails.put("phone", phone);
        codeDetails.put("createTime", System.currentTimeMillis());
        session.setAttribute("codeDetails", codeDetails);
        result.put("message", "ok");
        return result;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public JSONObject signup(HttpSession session ,@RequestBody JSONObject request) {
        JSONObject codeDetails = (JSONObject) session.getAttribute("codeDetails");
        JSONObject result = new JSONObject();
        if (!codeDetails.getString("verifyCode").equals(request.getString("code"))) {
            result.put("message","验证码错误");
            return result;
        }
        if (!codeDetails.getString("phone").equals(request.getString("phone"))) {
            result.put("message", "前后手机号不一致");
            return result;
        }
        if ((System.currentTimeMillis() - codeDetails.getLong("createTime")) > 1000 * 60 * 5) {
            result.put("message", "验证码过期啦");
            return result;
        }
        String msg = userService.addUser(request.getString("username"), request.getString("phone"), request.getString("password"));
        result.put("message", msg);
        return result;
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
        if (request.getString("token") == null) {
            User user = userService.findUserByPhoneAndPassword(request.getString("phone"), request.getString("password"));
            if (user != null) {
                String role = Utils.statusToRole(user.getStatus());
                if (role.equals("BANNED")) {
                    result.put("message", "该用户已被禁用");
                    return result;
                }
                Long uId = user.getuId();
                String token = tokenService.createToken(uId, role);
                result.put("message", "ok");
                result.put("token", token);
                result.put("uId", uId);
                return result;
            }
            result.put("message", "手机号或密码不正确");
            return result;
        } else {
            //后续登录，只含token字段和uId字段
            //解析并验证token
            JSONObject userInfo = tokenService.parseToken(request.getString("token"), request.getLong("uId"));
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
            return result;
        }
    }
}
