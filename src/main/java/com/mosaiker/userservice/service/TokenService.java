package com.mosaiker.userservice.service;

import com.alibaba.fastjson.JSONObject;

public interface TokenService {

    String createToken(Long uId, String role);

    JSONObject parseToken(String token, Long uId);

    boolean verifyToken(String token, Long uId, String role);

}