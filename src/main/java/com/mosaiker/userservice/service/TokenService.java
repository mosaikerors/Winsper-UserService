package com.mosaiker.userservice.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface TokenService {

    String createToken(Long uId, String role);

    String createToken(Long uId, String role, Long expiration_time);

    String createCodeToken(String phone, String code, Long expiration_time);

    Integer verifyCodeToken(String token, String phone, String code);

    JSONObject parseToken(String token, Long uId);

    /*
    * 验证role、uId和token匹不匹配
    * */
    boolean verifyTokenRoleHave(String token, Long uId, List<String> roleArray);
}
