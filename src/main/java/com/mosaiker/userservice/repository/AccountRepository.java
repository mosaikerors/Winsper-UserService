package com.mosaiker.userservice.repository;

import com.mosaiker.userservice.entity.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findAccountByUId(Long uId);

    @Modifying
    @Transactional
    void deleteAccountByUId(Long uId);
}
