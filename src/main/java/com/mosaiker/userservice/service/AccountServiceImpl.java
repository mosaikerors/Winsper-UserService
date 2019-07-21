package com.mosaiker.userservice.service;

import com.mosaiker.userservice.entity.Account;
import com.mosaiker.userservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void addAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Account findAccountByUId(Long uId) {
        return null;
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public String checkIn(Long uId) {
        return null;
    }

    @Override
    public String changeAvatar(String newAvatarUrl) {
        return null;
    }
    @Override
    public Integer toggleMessage(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setMessagePublic(isPublic);
        return 0;
    }
    @Override
    public Integer toggleHean(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setHeanPublic(isPublic);
        return 0;
    }
    @Override
    public Integer toggleCollection(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setCollectionPublic(isPublic);
        accountRepository.save(user);
        return 0;
    }
    @Override
    public Integer toggleDiary(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setDiaryPublic(isPublic);
        accountRepository.save(user);
        return 0;
    }
    @Override
    public Integer toggleJournal(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setJournalPublic(isPublic);
        accountRepository.save(user);
        return 0;
    }
    @Override
    public Integer toggleSubmission(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setSubmissionPublic(isPublic);
        accountRepository.save(user);
        return 0;
    }
    @Override
    public Integer toggleMood(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setMoodReportPublic(isPublic);
        accountRepository.save(user);
        return 0;
    }
    @Override
    public Integer toggleComment(Boolean isPublic,Long uId){
        Account user = accountRepository.findAccountByUId(uId);
        user.setCommentPublic(isPublic);
        accountRepository.save(user);
        return 0;
    }
}
