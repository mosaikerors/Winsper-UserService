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
}
