package com.mosaiker.userservice.service;

import com.mosaiker.userservice.entity.User;

public interface UserService {

    User findUserByPhone(String phone);

    User findUserByUId(Long uId);

    String sendCode(String phone, String code);

    Integer addUser(String username, String phone, String password);

    User findUserByPhoneAndPassword(String phone, String password);

    User findUserByUIdAndPassword(Long uId, String password);

    User updateUser(User user);
}
