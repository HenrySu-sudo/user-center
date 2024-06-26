package org.gatech.usercenter.service;
import java.util.Date;

import jakarta.annotation.Resource;
import org.gatech.usercenter.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testUserService(){
        User user=new User();
        user.setUsername("123");
        user.setUserAccount("123");
        user.setAvatarUrl("123");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setIsDelete(0);
        user.setUserRole(0);
        user.setPlanetCode("1234");
        userService.save(user);
        System.out.println(user.getId());

    }

    @Test
    public void testRegister(){
        String userAccount="henry";
        String userPassword="12345678";

        System.out.println(userService.userRegister(userAccount,userPassword,userPassword));
    }
}