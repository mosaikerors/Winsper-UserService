package com.mosaiker.userservice.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenServiceImplTest {
    private TokenService tokenService;
    @Before
    public void setUp() {
        tokenService = new TokenServiceImpl();
    }

    @Test
    public void createToken() throws InterruptedException{
        String token = tokenService.createToken(10086L, "USER");
        System.out.println(token);
//        Thread.sleep(2000);
        System.out.println(tokenService.parseToken(token+"1"));
    }

    @Deprecated
    @Test
    public void parseToken() {
    }

    @Deprecated
    @Test
    public void verifyToken() {
    }
}