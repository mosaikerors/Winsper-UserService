package com.mosaiker.userservice.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({AccountServiceImplTest.class, TokenServiceImplTest.class,
    UserServiceImplTest.class})
public class ServiceTests {

}
