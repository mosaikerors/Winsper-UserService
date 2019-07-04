package com.mosaiker.userservice.service;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.repository.UserRepository;
import com.mosaiker.userservice.utils.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService{

    @Autowired
    private UserRepository userRepository;

    static final long DEFAULT_EXPIRATION_TIME = 15 * 24 * 60 * 60 * 1000;      //15天
    static final String COMMON_SECRET = "MosA1kER5738h";            //JWT密码
    static final String TOKEN_PREFIX = "Bearer ";        //Token前缀

    @Override
    public String createToken(Long uId, String role, Long expiration_time) {
        User user = userRepository.findUserByUId(uId);
        String secret = Utils.getFullSecret(user.getPassword(), user.getStatus(), COMMON_SECRET);
        String token = Jwts.builder()
                // 保存权限（角色）
                .claim("authorities", role)
                // uId写入标题
                .setSubject(uId.toString())
                // 有效期设置
                .setExpiration(new Date(System.currentTimeMillis() + expiration_time))
                // 签名设置
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return token;
    }

    @Override
    public String createCodeToken(String phone, String code, Long expiration_time) {
        return Jwts.builder()
                // 保存验证码
                .claim("code", code)
                // 有效期设置
                .setExpiration(new Date(System.currentTimeMillis() + expiration_time))
                // 签名设置,用phone作为密码来加密
                .signWith(SignatureAlgorithm.HS512, phone)
                .compact();
    }

    @Override
    public String verifyCodeToken(String token, String phone, String code) {
        try {
            Claims claims = Jwts.parser()
                    // 验签
                    .setSigningKey(phone)
                    // 去掉 Bearer
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            String expectCode = claims.get("code").toString();
            if (expectCode.equals(code)) {
                return "ok";
            } else {
                return "验证码不正确";
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return "验证码已过期";
        } catch (io.jsonwebtoken.SignatureException e) {
            return "前后手机号不一致";
        }
    }

    @Override
    public String createToken(Long uId, String role) {
        return createToken(uId, role, DEFAULT_EXPIRATION_TIME);
    }

    @Override
    public JSONObject parseToken(String token, Long uId) {
        JSONObject result = new JSONObject();
        User user = userRepository.findUserByUId(uId);
        if (user == null) {
            result.put("message", "用户id不存在");
            return result;
        }
        String secret = Utils.getFullSecret(user.getPassword(), user.getStatus(), COMMON_SECRET);
        // 解析 Token
        try {
            Claims claims = Jwts.parser()
                    // 验签
                    .setSigningKey(secret)
                    // 去掉 Bearer
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            String role = claims.get("authorities").toString();
            result.put("uId", uId);
            result.put("role", role);
            result.put("message", "ok");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            result.put("message", "token已过期");
        } catch (io.jsonwebtoken.SignatureException e) {
            result.put("message", "token无效");
        }
        return result;
    }

    @Override
    public boolean verifyTokenRoleIs(String token, Long uId, String role) {
        // 解析 Token
        JSONObject userInfo = parseToken(token, uId);
        if (!userInfo.getString("message").equals("ok")) {
            //token已过期
            return false;
        }
        // 要求的身份和 token 中含有的身份信息匹配，返回 true
        return role.equals(userInfo.get("role"));
    }

    @Override
    public boolean verifyTokenRoleHave(String token, Long uId, String... roleArray) {
        // 解析 Token
        JSONObject userInfo = parseToken(token, uId);
        if (!userInfo.getString("message").equals("ok")) {
            //token已过期
            return false;
        }
        // 要求的身份和 token 中含有的身份信息匹配，返回 true
        for (String role : roleArray) {
            if (role.equals(userInfo.get("role"))) {
                return true;
            }
        }
        return false;
    }
}