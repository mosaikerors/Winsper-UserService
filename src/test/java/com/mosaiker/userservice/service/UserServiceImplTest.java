package com.mosaiker.userservice.service;

import static org.mockito.Mockito.when;

import com.mosaiker.userservice.entity.User;
import com.mosaiker.userservice.repository.UserRepository;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class UserServiceImplTest {


  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserServiceImpl userService;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    User superUser = new User("yjy", "123", "15201996738", 2);
    superUser.setuId(10000L);
    User user = new User("yjy2", "12345", "15201996739", 1);
    user.setuId(10001L);
    User addUserTrue = new User("yjy3", "123456", "15201996737", 1);
    addUserTrue.setuId(10002L);
    when(userRepository.findUserByUId(10000L)).thenReturn(superUser);
    when(userRepository.findUserByPhone("15201996738")).thenReturn(superUser);
    when(userRepository.findUserByUId(10001L)).thenReturn(user);
    when(userRepository.findUserByPhoneAndPassword("15201996738", "123")).thenReturn(superUser);
    when(userRepository.findUserByUIdAndPassword(10000L, "123")).thenReturn(superUser);
    when(userRepository.existsUserByPhone("15201996738")).thenReturn(Boolean.TRUE);
    when(userRepository.existsUserByPhone("15201996737")).thenReturn(Boolean.FALSE);
    when(userRepository.save(addUserTrue)).thenReturn(addUserTrue);
  }

  @Test
  public void findUserBy() {
    User superUser = new User("yjy", "123", "15201996738", 2);
    superUser.setuId(10000L);
    Assert.assertEquals(superUser, userService.findUserByUId(10000L));
    Assert.assertEquals(superUser, userService.findUserByPhone("15201996738"));
    Assert.assertEquals(superUser, userService.findUserByPhoneAndPassword("15201996738", "123"));
    Assert.assertEquals(superUser, userService.findUserByUIdAndPassword(10000L, "123"));
  }

  @Test
  public void sendCode() throws Exception {

    Assert.assertEquals(userService.sendCode("152019967", "123456"), "fail");
    Assert.assertEquals(userService.sendCode("15201996738", "123456"), "ok");
  }

  @Test
  public void addUser() throws Exception {

    TestCase.assertTrue(3 == userService.addUser("haha", "15201996738", "123"));
    TestCase.assertTrue(userService.addUser("haha", "15201996737", "123") == 0);
  }

  @Test
  public void updateUser() {
    User addUserTrue = new User("yjy3", "123456", "15201996737", 1);
    addUserTrue.setuId(10002L);
    Assert.assertEquals(addUserTrue, userService.updateUser(addUserTrue));
  }
}
