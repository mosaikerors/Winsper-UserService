package com.mosaiker.userservice.service;

import com.mosaiker.userservice.entity.Account;

public interface AccountService {
    void addAccount(Account account);

    Account findAccountByUId(Long uId);

    Account updateAccount(Account account);

//    String checkIn(Long uId);

    Integer toggleMessage(Boolean isPublic,Long uId);

    Integer toggleHean(Boolean isPublic,Long uId);

    Integer toggleCollection(Boolean isPublic,Long uId);

    Integer toggleDiary(Boolean isPublic,Long uId);

    Integer toggleJournal(Boolean isPublic,Long uId);

    Integer toggleSubmission(Boolean isPublic,Long uId);

    Integer toggleMood(Boolean isPublic,Long uId);

    Integer toggleComment(Boolean isPublic,Long uId);
}
