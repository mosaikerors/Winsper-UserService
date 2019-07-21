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
    * 你可以大胆放心地使用这两个函数来认证
    * 并且你可以相信这两个函数不仅验证role，
    * 还验证uId和token匹不匹配
    * */
    boolean verifyTokenRoleIs(String token, Long uId, String role);

    boolean verifyTokenRoleHave(String token, Long uId, List<String> roleArray);
}
