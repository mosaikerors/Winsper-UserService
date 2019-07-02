package com.mosaiker.userservice.service;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.repository.UserRepository;
import com.mosaiker.userservice.utils.Utils;
import com.zhenzi.sms.ZhenziSmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserByPhone(String phone) {
        return userRepository.findUserByPhone(phone);
    }

    @Override
    public User findUserByUId(Long uId) {
        return userRepository.findUserByUId(uId);
    }

    @Override
    public String sendCode(String phone, String code) {
        try {
            ZhenziSmsClient client = new ZhenziSmsClient("http://sms_developer.zhenzikj.com", "101953", "1c843c7d-ddf2-4e32-b897-637b95639d32");
            String message = "欢迎注册风语^-^,您的验证码为：" + code + "，有效期为5分钟，逾期不候哦~";
            String result = client.send(phone, message);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getIntValue("code") != 0) {
                return "fail";
            }
            return "ok";
        } catch (Exception e) {
            return "fail";
        }
    }

    @Override
    public String addUser(String name, String phone, String password) {
        if (userRepository.existsUserByPhone(phone))
            return "该手机号已被注册";
        User user = new User(name, password, phone, 1);
        userRepository.save(user);
        return "ok";
    }


    @Override
    public User findUserByPhoneAndPassword(String phone, String password){
        return userRepository.findUserByPhoneAndPassword(phone, password);
    }

    @Override
    public User findUserByUIdAndPassword(Long uId, String password){
        return userRepository.findUserByUIdAndPassword(uId, password);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
