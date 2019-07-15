package com.mosaiker.userservice.service;

import com.mosaiker.userservice.entity.Account;

public interface AccountService {
    void addAccount(Account account);

    Account findAccountByUId(Long uId);

    Account updateAccount(Account account);

    String checkIn(Long uId);

    String changeAvatar(String newAvatarUrl);
}
