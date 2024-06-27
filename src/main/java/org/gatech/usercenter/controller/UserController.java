package org.gatech.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gatech.usercenter.common.errorCode;
import org.gatech.usercenter.entity.User;
import org.gatech.usercenter.entity.request.userLoginRequest;
import org.gatech.usercenter.entity.request.userRegisterRequest;
import org.gatech.usercenter.exception.businessException;
import org.gatech.usercenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.gatech.usercenter.constant.userConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public long userRegister(@RequestBody userRegisterRequest userRegisterRequest){

        if(userRegisterRequest == null) throw new businessException(errorCode.NOT_LOGIN);
        String userName=userRegisterRequest.getUserName();
        String userPassword=userRegisterRequest.getUserPassword();
        String checkedPassword=userRegisterRequest.getCheckedPassword();

        if(StringUtils.isAnyBlank(userName,userPassword,checkedPassword)) throw new businessException(errorCode.NULL_ERROR);

        long res=userService.userRegister(userName,userPassword,checkedPassword);

        return res;
    }

    @PostMapping("/login")
    public User userdoLogin(@RequestBody userLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){

        if(userLoginRequest==null) throw new businessException(errorCode.NULL_ERROR);
        String userName=userLoginRequest.getUserName();

        String userPassword=userLoginRequest.getUserPassword();


        if(StringUtils.isAnyBlank(userName,userPassword)) throw new businessException(errorCode.NULL_ERROR);

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

    @RequestMapping("/current")
    public User currentUser(HttpServletRequest httpServletRequest){
        User currentUser = (User) httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        long id=currentUser.getId();
        User user=userService.getById(id);
        return userService.hideUserInfo(user);
    }

    @PostMapping("/logout")
    public Integer userLogout(HttpServletRequest httpServletRequest){
        if(httpServletRequest==null){
            throw new businessException(errorCode.PARAMS_ERROR);
        }
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,null);
        return 1;
    }
}
