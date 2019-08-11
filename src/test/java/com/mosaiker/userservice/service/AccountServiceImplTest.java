package com.mosaiker.userservice.service;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.repository.AccountRepository;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * AccountServiceImpl Tester.
 *
 * @author <DeeEll-X>
 * @version 1.0
 * @since <pre>Aug 1, 2019</pre>
 */
public class AccountServiceImplTest {

  @Mock
  private AccountRepository accountRepository;
  @InjectMocks
  private AccountServiceImpl accountService;
  private Account account1 = new Account(1L);

  @Before
  public void before() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: addAccount(Account account)
   */
  @Test
  public void testAddAccount() throws Exception {
    when(accountRepository.save(account1)).thenReturn(account1);
    accountService.addAccount(account1);
  }

  /**
   * Method: findAccountByUId(Long uId)
   */
  @Test
  public void testFindAccountByUId() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertEquals(account1, accountService.findAccountByUId(1L));
  }

  /**
   * Method: updateAccount(Account account)
   */
  @Test
  public void testUpdateAccount() throws Exception {
    when(accountRepository.save(account1)).thenReturn(account1);
    assertEquals(account1, accountService.updateAccount(account1));
//accountRepository.save(account);
  }

  /**
   * Method: toggleMessage(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleMessage() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleMessage(true, 1L) == 0);

  }

  /**
   * Method: toggleHean(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleHean() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleHean(true, 1L) == 0);
  }

  /**
   * Method: toggleCollection(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleCollection() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleCollection(true, 1L) == 0);
  }

  /**
   * Method: toggleDiary(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleDiary() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleDiary(true, 1L) == 0);
  }

  /**
   * Method: toggleJournal(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleJournal() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleJournal(true, 1L) == 0);
  }

  /**
   * Method: toggleSubmission(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleSubmission() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleSubmission(true, 1L) == 0);
  }

  /**
   * Method: toggleMood(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleMood() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleMood(true, 1L) == 0);
  }

  /**
   * Method: toggleComment(Boolean isPublic, Long uId)
   */
  @Test
  public void testToggleComment() throws Exception {
    when(accountRepository.findAccountByUId(1L)).thenReturn(account1);
    assertTrue(accountService.toggleComment(true, 1L) == 0);
  }


}
