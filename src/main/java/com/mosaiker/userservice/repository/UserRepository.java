package com.mosaiker.userservice.repository;

import com.mosaiker.userservice.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserByUId(Long uId);

    User findUserByPhone(String phone);

    boolean existsUserByPhone(String phone);

    User findUserByPhoneAndPassword(String phone, String password);

    User findUserByUIdAndPassword(Long uId, String password);

    @Modifying
    @Transactional
    void deleteUserByPhone(Long phone);
}
