package org.gatech.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gatech.usercenter.entity.User;
import org.gatech.usercenter.entity.request.userLoginRequest;
import org.gatech.usercenter.entity.request.userRegisterRequest;
import org.gatech.usercenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public long userRegister(@RequestBody userRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null) return -1;
        String userName=userRegisterRequest.getUserName();
        String userPassword=userRegisterRequest.getUserPassword();
        String checkedPassword=userRegisterRequest.getCheckedPassword();

        if(StringUtils.isAnyBlank(userName,userPassword,checkedPassword)) return -1;

        long res=userService.userRegister(userName,userPassword,checkedPassword);

        return res;
    }

    @PostMapping("/login")
    public User userdoLogin(@RequestBody userLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){
        log.info("Called");
        if(userLoginRequest==null) return null;
        String userName=userLoginRequest.getUserName();
        log.info(userName);
        String userPassword=userLoginRequest.getUserPassword();
        log.info(userPassword);

        if(StringUtils.isAnyBlank(userName,userPassword)) return null;

        User user = userService.userLogin(userName, userPassword, httpServletRequest);

        return user;
    }

    @GetMapping("/search")
    public List<User> userSearch(String userName,HttpServletRequest httpServletRequest){
        return userService.userSearch(userName,httpServletRequest);
    }

    @GetMapping("/delete")
    public boolean deleteUser(long id,HttpServletRequest httpServletRequest){
        return userService.deleteUser(id,httpServletRequest);
    }
}
