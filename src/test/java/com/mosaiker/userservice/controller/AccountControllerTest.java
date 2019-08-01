package com.mosaiker.userservice.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.service.AccountService;
import com.mosaiker.userservice.service.UserService;
import com.mosaiker.userservice.utils.MyJSONUtil;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * AccountController Tester.
 *
 * @author <DeeEll-X>
 * @version 1.0
 * @since <pre>Aug 1, 2019</pre>
 */
public class AccountControllerTest {

  @Mock
  private AccountService accountService;
  @Mock
  private UserService userService;
  @InjectMocks
  private AccountController accountController;
  private User user1 = new User("test", "testpwd", "13111333777", 1);
  private User user2 = new User("test2", "testpwd", "13111333888", 1);


  @Before
  public void before() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: updateAvatar(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testUpdateAvatar() throws Exception {
    when(accountService.findAccountByUId(1L)).thenReturn(new Account(1L));
    when(accountService.findAccountByUId(2L)).thenReturn(null);
    when(accountService.updateAccount(anyObject())).thenReturn(new Account(1L) {{
      setAvatarUrl("pic");
    }});

    JSONObject expected_fail = new JSONObject() {{
      put("rescode", 1);
    }};
    JSONObject expected_ok = new JSONObject() {{
      put("rescode", 0);
    }};

    assertTrue(MyJSONUtil
        .compareTwoJSONObject(expected_fail, accountController.updateAvatar(new JSONObject() {{
          put("avatar", "pic1");
        }}, 2L)));
    assertTrue(MyJSONUtil
        .compareTwoJSONObject(expected_ok, accountController.updateAvatar(new JSONObject() {{
          put("avatar", "pic1");
        }}, 1L)));
  }

  /**
   * Method: checkIn(@RequestHeader("uId") Long uId)
   */
  @Test
  public void testCheckIn() throws Exception {
    when(accountService.findAccountByUId(2L)).thenReturn(null);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 1);
    }}, accountController.checkIn(2L)));

    Account account3 = new Account(3L) {{
      setLastCheckIn(new Date().getTime());
    }};
    ;
    when(accountService.findAccountByUId(3L)).thenReturn(account3);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 3);
    }}, accountController.checkIn(3L)));

    Account account1 = new Account(1L);
    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    assertEquals(new JSONObject(true) {{
      put("rescode", 0);
      put("newFeather", 1);
    }}.toJSONString(), accountController.checkIn(1L).toJSONString());
  }

  /**
   * Method: follow(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testFollow() throws Exception {
    Account account1 = new Account(1L);
    Account account3 = new Account(3L);
    when(accountService.findAccountByUId(2L)).thenReturn(null);
    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    when(accountService.findAccountByUId(3L)).thenReturn(account3);

    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 1);
    }}, accountController.follow(new JSONObject() {{
      put("targetUId", 2L);
    }}, 1L)));
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 1);
    }}, accountController.follow(new JSONObject() {{
      put("targetUId", 1L);
    }}, 2L)));

    List<Long> following = account1.getFollowing();
    List<Long> follower = account3.getFollower();
    following.add(3L);
    follower.add(1L);
    account1.setFollowing(following);
    account3.setFollower(follower);

    when(accountService.updateAccount(account1)).thenReturn(account1);
    when(accountService.updateAccount(account3)).thenReturn(account3);

    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.follow(new JSONObject() {{
      put("targetUId", 3L);
    }}, 1L)));

  }

  /**
   * Method: unfollow(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testUnfollow() throws Exception {
    Account account1 = new Account(1L);
    Account account3 = new Account(3L);
    when(accountService.findAccountByUId(2L)).thenReturn(null);
    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    when(accountService.findAccountByUId(3L)).thenReturn(account3);

    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 1);
    }}, accountController.unfollow(new JSONObject() {{
      put("targetUId", 2L);
    }}, 1L)));
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 1);
    }}, accountController.unfollow(new JSONObject() {{
      put("targetUId", 1L);
    }}, 2L)));
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 3);
    }}, accountController.unfollow(new JSONObject() {{
      put("targetUId", 1L);
    }}, 3L)));

    List<Long> following = account1.getFollowing();
    List<Long> follower = account3.getFollower();
    following.add(3L);
    follower.add(1L);
    account1.setFollowing(following);
    account3.setFollower(follower);
    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    when(accountService.findAccountByUId(3L)).thenReturn(account3);

    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.unfollow(new JSONObject() {{
      put("targetUId", 3L);
    }}, 1L)));

  }

  /**
   * Method: getMyInfo(@RequestHeader("uId") Long uId)
   */
  @Test
  public void testGetMyInfo() throws Exception {
    Account account1 = new Account(1L);
    when(accountService.findAccountByUId(1L)).thenReturn(account1);

    when(accountService.findAccountByUId(2L)).thenReturn(null);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 1);
    }}, accountController.getMyInfo(2L)));

    when(userService.findUserByUId(1L)).thenReturn(user1);
    JSONObject expect = account1.toJSONObject();
    expect.put("rescode", 0);
    expect.put("username", "test");
    expect.put("status", 1);
    assertTrue(MyJSONUtil.compareTwoJSONObject(expect, accountController.getMyInfo(1L)));
  }

  /**
   * Method: getAccountInfo(@PathVariable Long uId)
   */
  @Test
  public void testGetAccountInfo() throws Exception {
    when(accountService.findAccountByUId(2L)).thenReturn(null);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 1);
    }}, accountController.getAccountInfo(2L)));

    Account account1 = new Account(1L);
    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    when(userService.findUserByUId(1L)).thenReturn(user1);
    JSONObject expect = account1.toViewedJSONObject();
    expect.put("rescode", 0);
    expect.put("username", "test");
    assertTrue(MyJSONUtil.compareTwoJSONObject(expect, accountController.getAccountInfo(1L)));

//JSONObject result;
//    Account account = accountService.findAccountByUId(uId);
//    if (account == null) {
//      result = new JSONObject();
//      result.put("rescode", 1);//该uid不存在
//      return result;
//    }
//    User me = userService.findUserByUId(uId);
//    result = account.toViewedJSONObject();
//    result.put("username", me.getUsername());
//    result.put("rescode", 0);
//    return result;
  }

  /**
   * Method: isMessage(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsMessage() throws Exception {
    when(accountService.toggleMessage(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isMessage(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: isHean(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsHean() throws Exception {
    when(accountService.toggleHean(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isHean(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: isCollection(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsCollection() throws Exception {
    when(accountService.toggleCollection(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isCollection(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: isDiary(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsDiary() throws Exception {
    when(accountService.toggleDiary(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isDiary(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: isJournal(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsJournal() throws Exception {
    when(accountService.toggleJournal(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isJournal(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: isSubmission(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsSubmission() throws Exception {
    when(accountService.toggleSubmission(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isSubmission(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: isMood(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsMood() throws Exception {
    when(accountService.toggleMood(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isMood(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: isComment(@RequestBody JSONObject request, @RequestHeader("uId") Long uId)
   */
  @Test
  public void testIsComment() throws Exception {
    when(accountService.toggleComment(Boolean.TRUE, 1L)).thenReturn(0);
    assertTrue(MyJSONUtil.compareTwoJSONObject(new JSONObject() {{
      put("rescode", 0);
    }}, accountController.isComment(new JSONObject() {{
      put("toBePublic", true);
    }}, 1L)));
  }

  /**
   * Method: getFollowings(@RequestHeader("uId") Long uId)
   */
  @Test
  public void testGetFollowings() throws Exception {
    Account account1 = new Account(1L);
    Account account2 = new Account(2L);
    List<Long> followings = account1.getFollowing();
    List<Long> followers = account2.getFollower();
    followings.add(2L);
    followers.add(1L);
    account1.setFollowing(followings);
    account2.setFollower(followers);

    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    when(accountService.findAccountByUId(2L)).thenReturn(account2);
    when(userService.findUserByUId(2L)).thenReturn(user2);

    JSONArray expectedArray = new JSONArray() {{
      add(new JSONObject() {{
        put("uId", 2L);
        put("username", "test2");
        put("avatar", "");
        put("isMutualFollow", false);
      }});
    }};
    JSONObject expected = new JSONObject() {{
      put("rescode", 0);
      put("followlist", expectedArray);
    }};
    assertTrue(MyJSONUtil.compareTwoJSONObject(expected, accountController.getFollowings(1L)));

  }

  /**
   * Method: getFollowers(@RequestHeader("uId") Long uId)
   */
  @Test
  public void testGetFollowers() throws Exception {
    Account account1 = new Account(1L);
    Account account2 = new Account(2L);
    account1.setAvatarUrl("");
    List<Long> followings = account1.getFollowing();
    List<Long> followers = account2.getFollower();
    followings.add(2L);
    followers.add(1L);
    account1.setFollowing(followings);
    account2.setFollower(followers);

    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    when(accountService.findAccountByUId(2L)).thenReturn(account2);
    when(userService.findUserByUId(1L)).thenReturn(user1);

    JSONArray expectedArray = new JSONArray() {{
      add(new JSONObject() {{
        put("uId", 1L);
        put("username", "test");
        put("avatar", "");
        put("isMutualFollow", false);
      }});
    }};
    JSONObject expected = new JSONObject() {{
      put("rescode", 0);
      put("followlist", expectedArray);
    }};
    assertTrue(MyJSONUtil.compareTwoJSONObject(expected, accountController.getFollowers(2L)));

  }

  /**
   * Method: getMutualFollows(@RequestHeader("uId") Long uId)
   */
  @Test
  public void testGetMutualFollows() throws Exception {
    Account account1 = new Account(1L);
    Account account2 = new Account(2L);
    List<Long> followings1 = account1.getFollowing();
    List<Long> followings2 = account1.getFollowing();
    List<Long> followers1 = account1.getFollower();
    List<Long> followers2 = account2.getFollower();
    followings1.add(2L);
    followings2.add(1L);
    followers1.add(2L);
    followers2.add(1L);
    account1.setFollower(followers1);
    account1.setFollowing(followings1);
    account2.setFollowing(followings2);
    account2.setFollower(followers2);

    when(accountService.findAccountByUId(1L)).thenReturn(account1);
    when(accountService.findAccountByUId(2L)).thenReturn(account2);
    when(userService.findUserByUId(1L)).thenReturn(user1);
    when(userService.findUserByUId(2L)).thenReturn(user2);
    JSONArray expectedArray = new JSONArray() {{
      add(new JSONObject() {{
        put("uId", 1L);
        put("username", "test");
        put("avatar", "");
        put("isMutualFollow", true);
      }});
    }};
    JSONObject expected = new JSONObject() {{
      put("rescode", 0);
      put("followlist", expectedArray);
    }};
    assertTrue(MyJSONUtil.compareTwoJSONObject(expected, accountController.getMutualFollows(2L)));

//
  }


}
