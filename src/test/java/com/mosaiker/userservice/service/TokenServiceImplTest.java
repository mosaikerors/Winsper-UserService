package com.mosaiker.userservice.service;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TokenServiceImplTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        User superUser = new User("yjy", "123", "15201996738", 2);
        superUser.setuId(10000L);
        User user = new User("yjy2", "12345", "15201996739", 1);
        user.setuId(10001L);
        when(userRepository.findUserByUId(10000L)).thenReturn(superUser);
        when(userRepository.findUserByUId(10001L)).thenReturn(user);
    }

    @Test
    public void createToken() throws InterruptedException{
        //正常
        String token = tokenService.createToken(10001L, "USER");
        System.out.println(token);
        token = tokenService.createToken(10000L, "SUPERUSER", 1000L);
        System.out.println(token);
    }

    @Test
    public void parseToken() {
        //验证正常token
        String token = tokenService.createToken(10000L, "SUPERUSER");
        JSONObject expected = new JSONObject();
        expected.put("message", 0);
        expected.put("role", "SUPERUSER");
        expected.put("uId", 10000L);
        Assert.assertEquals(expected, tokenService.parseToken(token, 10000L));

        //验证第二个参数uId不存在
        expected.clear();
        expected.put("message", 1);
        Assert.assertEquals(expected, tokenService.parseToken(token, 9999L));
        //验证uId不正确
        expected.clear();
        expected.put("message", 1);
        Assert.assertEquals(expected, tokenService.parseToken(token, 10001L));

        //验证token过期
        token = tokenService.createToken(10000L, "SUPERUSER", 1000L);
        try {
            Thread.sleep(2000);
            expected.clear();
            expected.put("message", 1);
            Assert.assertEquals(expected, tokenService.parseToken(token, 10000L));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void verifyTokenRoleIs() {
        String token = tokenService.createToken(10000L, "SUPERUSER", 100000L);
        List<String> roles = new ArrayList<>();

        token = tokenService.createToken(10000L, "SUPERUSER",5000L);
        roles.clear();
        Assert.assertTrue(tokenService.verifyTokenRoleHave(token, 10000L, roles));
        roles.add("SUPERUSER");
        roles.add("USER");
        Assert.assertTrue(tokenService.verifyTokenRoleHave(token, 10000L, roles));
        roles.clear();
        roles.add("haha");
        Assert.assertFalse(tokenService.verifyTokenRoleHave(token, 10000L, roles));
      try {
        Thread.sleep(5000);
        roles.add("SUPERUSER");
        Assert.assertFalse(tokenService.verifyTokenRoleHave(token, 10000L, roles));
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
    }

    @Test
    public void verifyCodeToken() {
        String token = tokenService.createCodeToken("123", "456456", 10000L);
      assertTrue( tokenService.verifyCodeToken(token, "123", "456456").equals(0));

      assertTrue( tokenService.verifyCodeToken(token, "123", "789789").equals(4));
        assertTrue(tokenService.verifyCodeToken(token, "789", "456456").equals(4));

        try {
            Thread.sleep(10000L);
            assertTrue( tokenService.verifyCodeToken(token, "123", "456456").equals(1));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
