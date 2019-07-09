package com.mosaiker.userservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.service.TokenService;
import com.mosaiker.userservice.service.UserService;
import com.mosaiker.userservice.utils.MyJSONUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private MockMvc mockMvc;
    private MyJSONUtil myJSONUtil;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

    }

    @Test
    public void sendCode() {
        JSONObject mockParam = new JSONObject();
        mockParam.put("phone","13111333777");
        when(userService.findUserByPhone("13111333777")).thenReturn(new User());
        JSONObject expected1=new JSONObject();
        expected1.put("message","u1");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/sendCode")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        }catch (Exception e){
            System.out.print("Something wrong with test sendCode1!");
        }
        Boolean compare1 = myJSONUtil.compareTwoJSONObject(expected1,userController.sendCode(mockParam));
        assertTrue(compare1);

        mockParam.clear();
        mockParam.put("phone","13111333999");
        when(userService.findUserByPhone("13111333999")).thenReturn(null);
        when(userService.sendCode(eq("13111333999"),anyString())).thenReturn("fail");
        JSONObject expected2=new JSONObject();
        expected2.put("message","u2");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/sendCode")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        }catch (Exception e){
            System.out.print("Something wrong with test sendCode2!");
        }

        assertEquals(true,myJSONUtil.compareTwoJSONObject(expected2,userController.sendCode(mockParam)));

        mockParam.clear();
        mockParam.put("phone","13111333555");
        when(userService.findUserByPhone("13111333555")).thenReturn(null);
        when(userService.sendCode(eq("13111333555"), anyString())).thenReturn("ok");
        JSONObject expected3=new JSONObject();
        when(tokenService.createCodeToken(eq("13111333555"), anyString(),anyLong())).thenReturn("token");
        expected3.put("message", "ok");
        expected3.put("token", "token");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/sendCode")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        }catch (Exception e){
            System.out.print("Something wrong with test sendCode3!");
        }
        assertEquals(true, myJSONUtil.compareTwoJSONObject(expected3,userController.sendCode(mockParam)));

    }

    @Test
    public void signup() {
        JSONObject mockParam = new JSONObject();
        mockParam.put("phone","13111333777");
        mockParam.put("token","token1");
        mockParam.put("code","0000");
        mockParam.put("username","test1");
        mockParam.put("password","test1");
        when(tokenService.verifyCodeToken("token1", "13111333777", "0000")).thenReturn("ok");
        when(userService.addUser("test1","13111333777","test1")).thenReturn("ok");
        JSONObject expected1=new JSONObject();
        expected1.put("message","ok");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
            assertTrue(myJSONUtil.compareTwoJSONObject(expected1,userController.signup(mockParam)));
        }catch (Exception e){
            System.out.print("Something wrong with test signup1!");
        }

        mockParam.clear();
        mockParam = new JSONObject();
        mockParam.put("phone","13111333999");
        mockParam.put("token","token2");
        mockParam.put("code","0000");
        mockParam.put("username","test2");
        mockParam.put("password","test2");
        when(tokenService.verifyCodeToken("token2", "13111333999", "0000")).thenReturn("ucode1");
        JSONObject expected2=new JSONObject();
        expected2.put("message","ucode1");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
            assertTrue(myJSONUtil.compareTwoJSONObject(expected2,userController.signup(mockParam)));
        }catch (Exception e){
            System.out.print("Something wrong with test signup1!");
        }

    }

    @Test
    public void updateInfo() {
        JSONObject mockParam = new JSONObject();
        mockParam.put("username","test1");
        mockParam.put("uID",1234L);
        when(userService.findUserByUId(1234L)).thenReturn(new User());
        JSONObject expected1 = new JSONObject();
        expected1.put("message","ok");
        try {
            mockMvc.perform(MockMvcRequestBuilders.put("/user/updateInfo")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
            assertTrue(
                myJSONUtil.compareTwoJSONObject(expected1, userController.signup(mockParam)));
        }catch(Exception e){
            System.out.print("Something wrong with test updateInfo1!");
        }
        mockParam.clear();
        mockParam.put("username","test2");
        mockParam.put("uID",1235L);
        when(userService.findUserByUId(1235L)).thenReturn(null);
        JSONObject expected2 = new JSONObject();
        expected2.put("message","uinfo1");
        try {
            mockMvc.perform(MockMvcRequestBuilders.put("/user/updateInfo")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
            assertTrue(
                myJSONUtil.compareTwoJSONObject(expected2, userController.signup(mockParam)));
        }catch(Exception e){
            System.out.print("Something wrong with test updateInfo2!");
        }
    }
    @Test
    public void authenticate() {
        JSONObject mockParam = new JSONObject();
        mockParam.put("token","test1.test1.test1");
        List<String> role1 =new ArrayList<>();
        role1.add("USER");
        JSONArray roles1 = JSONArray.parseArray(JSON.toJSONString(role1));
        mockParam.put("roles",roles1);
        mockParam.put("uID",1234L);
        when(tokenService.verifyTokenRoleHave("test1.test1.test1", 1234L, role1)).thenReturn(true);
        when(userService.findUserByUId(1234L)).thenReturn(new User());
        JSONObject expected1=new JSONObject();
        expected1.put("message","ok");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
            assertTrue(myJSONUtil.compareTwoJSONObject(expected1,userController.signup(mockParam)));
        }catch (Exception e){
            System.out.print("Something wrong with test authenciation1!");
        }

        mockParam.clear();
        mockParam.put("token","");
        List<String> role2 =new ArrayList<>();
        JSONArray roles2 = JSONArray.parseArray(JSON.toJSONString(role2));
        mockParam.put("roles",roles2);
        mockParam.put("uID",1234L);
        when(tokenService.verifyTokenRoleHave("test1.test1.test1", 1234L, role2)).thenReturn(false);
        JSONObject expected2=new JSONObject();
        expected1.put("message","uauth1");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
            assertTrue(myJSONUtil.compareTwoJSONObject(expected2,userController.signup(mockParam)));
        }catch (Exception e){
            System.out.print("Something wrong with test authenciation2!");
        }

        mockParam.clear();
        mockParam.put("token","");
        List<String> role3 =new ArrayList<>();
        role3.add("USER");
        JSONArray roles3 = JSONArray.parseArray(JSON.toJSONString(role2));
        mockParam.put("roles",roles3);
        mockParam.put("uID",1235L);
        when(tokenService.verifyTokenRoleHave("test3.test3.test3", 1235L, role3)).thenReturn(true);
        when(userService.findUserByUId(1234L)).thenReturn(null);
        JSONObject expected3=new JSONObject();
        expected3.put("message","uauth2");
        try{
            mockMvc.perform(MockMvcRequestBuilders.post("/user/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
            assertTrue(myJSONUtil.compareTwoJSONObject(expected3,userController.signup(mockParam)));
        }catch (Exception e){
            System.out.print("Something wrong with test authenciation3!");
        }
    }
}
