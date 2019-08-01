package com.mosaiker.userservice.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.service.AccountService;
import com.mosaiker.userservice.service.TokenService;
import com.mosaiker.userservice.service.UserService;
import com.mosaiker.userservice.utils.MyJSONUtil;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * UserController Tester.
 *
 * @author <DeeEll-X>
 * @version 1.0
 * @since <pre>Aug 1, 2019</pre>
 */
public class UserControllerTest {

  private MockMvc mockMvc;
  @Mock
  private UserService userService;
  @Mock
  private TokenService tokenService;
  @Mock
  private AccountService accountService;
  @InjectMocks
  private UserController userController;
  private User user1 = new User("test", "testpwd", "13111333777", 1);

  @Before
  public void before() throws Exception {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: sendCode(@RequestBody JSONObject request)
   */
  @Test
  public void testSendCode() throws Exception {
    JSONObject mockParam = new JSONObject();
    mockParam.put("phone", "13111333777");
    when(userService.findUserByPhone("13111333777")).thenReturn(new User());
    JSONObject expected1 = new JSONObject();
    expected1.put("rescode", 3);
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/sendCode")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test sendCode1!");
    }

    assertTrue(MyJSONUtil.compareTwoJSONObject(expected1, userController.sendCode(mockParam)));

    mockParam.clear();
    mockParam.put("phone", "13111333999");
    when(userService.findUserByPhone("13111333999")).thenReturn(null);
    when(userService.sendCode(eq("13111333999"), anyString())).thenReturn("fail");
    JSONObject expected2 = new JSONObject();
    expected2.put("rescode", 4);
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/sendCode")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test sendCode2!");
    }

    assertTrue(MyJSONUtil.compareTwoJSONObject(expected2, userController.sendCode(mockParam)));

    mockParam.clear();
    mockParam.put("phone", "13111333555");
    when(userService.findUserByPhone("13111333555")).thenReturn(null);
    when(userService.sendCode(eq("13111333555"), anyString())).thenReturn("ok");
    JSONObject expected3 = new JSONObject();
    when(tokenService.createCodeToken(eq("13111333555"), anyString(), anyLong()))
        .thenReturn("token");
    expected3.put("rescode", 0);
    expected3.put("token", "token");
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/sendCode")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test sendCode3!");
    }
    assertTrue(MyJSONUtil.compareTwoJSONObject(expected3, userController.sendCode(mockParam)));
  }

  /**
   * Method: signup(@RequestBody JSONObject request)
   */
  @Test
  public void testSignup() throws Exception {
    JSONObject mockParam = new JSONObject();
    mockParam.put("phone", "13111333777");
    mockParam.put("token", "token1");
    mockParam.put("code", "0000");
    mockParam.put("username", "test1");
    mockParam.put("password", "test1");
    when(tokenService.verifyCodeToken("token1", "13111333777", "0000")).thenReturn(0);
    when(userService.addUser("test1", "13111333777", "test1")).thenReturn(0);
    user1.setuId(1L);
    when(userService.findUserByPhone("13111333777")).thenReturn(user1);

    JSONObject expected1 = new JSONObject();
    expected1.put("rescode", 0);
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/signup")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
      assertTrue(MyJSONUtil.compareTwoJSONObject(expected1, userController.signup(mockParam)));
    } catch (Exception e) {
      System.out.print("Something wrong with test signup1!");
    }

    mockParam.clear();
    mockParam = new JSONObject();
    mockParam.put("phone", "13111333999");
    mockParam.put("token", "token2");
    mockParam.put("code", "0000");
    mockParam.put("username", "test2");
    mockParam.put("password", "test2");
    when(tokenService.verifyCodeToken("token2", "13111333999", "0000")).thenReturn(4);
    JSONObject expected2 = new JSONObject();
    expected2.put("rescode", 4);
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/signup")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
      assertTrue(MyJSONUtil.compareTwoJSONObject(expected2, userController.signup(mockParam)));
    } catch (Exception e) {
      System.out.print("Something wrong with test signup1!");
    }
  }

  /**
   * Method: login(@RequestBody JSONObject request)
   */
  @Test
  public void testLogin() throws Exception {

    JSONObject successUserInfo = new JSONObject();
    successUserInfo.put("message", 0);
    successUserInfo.put("uId", 1);
    JSONObject failUserInfo = new JSONObject();
    failUserInfo.put("message", 3);
    when(tokenService.parseToken("1", 1L)).thenReturn(successUserInfo);
    when(tokenService.parseToken("3", 3L)).thenReturn(failUserInfo);
    when(tokenService.createToken(1L, "USER")).thenReturn("1+");
    User successUser = new User("ok", "ok", "1", 1);
    successUser.setuId(1L);
    User bannedUser = new User("banned", "banned", "3", -1);
    bannedUser.setuId(3L);
    when(userService.findUserByPhoneAndPassword("1", "ok")).thenReturn(successUser);
    when(userService.findUserByUId(1L)).thenReturn(successUser);
    when(userService.findUserByPhoneAndPassword("2", "fail")).thenReturn(null);
    when(userService.findUserByPhoneAndPassword("3", "banned")).thenReturn(bannedUser);
    when(accountService.findAccountByUId(1L)).thenReturn(new Account(1L));
    JSONObject expectResult = new JSONObject();
    JSONObject mockParam = new JSONObject();
    mockParam.put("phone", "1");
    mockParam.put("password", "ok");
    expectResult = new Account(1L).toJSONObject();
    expectResult.put("rescode", 0);
    expectResult.put("token", "1+");
    expectResult.put("uId", 1L);
    expectResult.put("username", "ok");
    expectResult.put("status", 1);
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test login!");
    }
    System.out.println(expectResult);
    System.out.println(userController.login(mockParam));
    assertTrue(MyJSONUtil.compareTwoJSONObject(expectResult, userController.login(mockParam)));

    mockParam.clear();
    mockParam.put("token", "1");
    mockParam.put("uId", 1L);
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test login!");
    }
    System.out.println(expectResult);
    System.out.println(userController.login(mockParam));
    assertTrue(MyJSONUtil.compareTwoJSONObject(expectResult, userController.login(mockParam)));

    expectResult.clear();
    expectResult.put("rescode", 4);
    mockParam.clear();
    mockParam.put("phone", "2");
    mockParam.put("password", "fail");
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test login!");
    }
    assertTrue(MyJSONUtil.compareTwoJSONObject(expectResult, userController.login(mockParam)));

    expectResult.clear();
    expectResult.put("rescode", 3);
    mockParam.clear();
    mockParam.put("phone", "3");
    mockParam.put("password", "banned");
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test login!");
    }
    assertTrue(MyJSONUtil.compareTwoJSONObject(expectResult, userController.login(mockParam)));

    mockParam.clear();
    mockParam.put("uId", 3);
    mockParam.put("token", "3");
    expectResult.clear();
    expectResult.put("rescode", 3);
    try {
      mockMvc.perform(MockMvcRequestBuilders.post("/login")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print())
          .andReturn();
    } catch (Exception e) {
      System.out.print("Something wrong with test login!");
    }
    assertTrue(MyJSONUtil.compareTwoJSONObject(expectResult, userController.login(mockParam)));
  }

  /**
   * Method: updateInfo(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testUpdateInfo() throws Exception {
    JSONObject mockParam = new JSONObject();
    mockParam.put("username", "test1");
    mockParam.put("uId", 1234L);
    User user = new User("test", "test", "13000444888", 1);
    user.setuId(1234L);
    when(userService.findUserByUId(1234L)).thenReturn(user);
    when(userService.updateUser(anyObject())).thenReturn(user);
    JSONObject expected1 = new JSONObject();
    expected1.put("rescode", 0);

    mockMvc.perform(MockMvcRequestBuilders.put("/update/username")
        .accept(MediaType.APPLICATION_JSON)
        .header("uId", 1234L)
        .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

    assertTrue(
        MyJSONUtil.compareTwoJSONObject(expected1, userController.updateInfo(mockParam, 1234L)));

    mockParam.clear();
    mockParam.put("username", "test2");
    mockParam.put("uId", 1235L);
    when(userService.findUserByUId(1235L)).thenReturn(null);
    JSONObject expected2 = new JSONObject();
    expected2.put("rescode", 1);
    mockMvc.perform(MockMvcRequestBuilders.put("/update/username")
        .accept(MediaType.APPLICATION_JSON)
        .header("uId", 1234L)
        .contentType(MediaType.APPLICATION_JSON).content(mockParam.toJSONString()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
    System.out.println(userController.updateInfo(mockParam, 1235L));
    assertTrue(
        MyJSONUtil.compareTwoJSONObject(expected2, userController.updateInfo(mockParam, 1235L)));

  }

  /**
   * Method: getSimpleInfo(@RequestHeader("uId") Long uId)
   */
  @Test
  public void testGetSimpleInfo() throws Exception {
    when(accountService.findAccountByUId(1L)).thenReturn(new Account(1L));
    when(accountService.findAccountByUId(3L)).thenReturn(null);
    when(userService.findUserByUId(1L)).thenReturn(user1);
    when(userService.findUserByUId(3L)).thenReturn(null);
    JSONObject expect_ok = new JSONObject() {{
      put("rescode", 0);
      put("avatarUrl", "");
      put("username", "test");
      put("isHeanPublic", true);
      put("isCollectionPublic", true);
    }};
    JSONObject expect_null = new JSONObject() {{
      put("rescode", 1);
    }};
    assertTrue(MyJSONUtil.compareTwoJSONObject(expect_ok, userController.getSimpleInfo(1L)));
    assertTrue(MyJSONUtil.compareTwoJSONObject(expect_null, userController.getSimpleInfo(3L)));

  }


}
