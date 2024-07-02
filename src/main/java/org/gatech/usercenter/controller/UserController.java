package org.gatech.usercenter.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gatech.usercenter.common.errorCode;
import org.gatech.usercenter.common.generalRespon;
import org.gatech.usercenter.entity.User;
import org.gatech.usercenter.entity.request.userLoginRequest;
import org.gatech.usercenter.entity.request.userRegisterRequest;
import org.gatech.usercenter.exception.businessException;
import org.gatech.usercenter.service.UserService;
import org.gatech.usercenter.utils.resultutils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gatech.usercenter.constant.userConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = "http://223.4.215.83")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public generalRespon<Long> userRegister(@RequestBody userRegisterRequest userRegisterRequest){

        if(userRegisterRequest == null) throw new businessException(errorCode.NOT_LOGIN);
        String userName=userRegisterRequest.getUserName();
        String userPassword=userRegisterRequest.getUserPassword();
        String checkedPassword=userRegisterRequest.getCheckedPassword();

        if(StringUtils.isAnyBlank(userName,userPassword,checkedPassword)) throw new businessException(errorCode.NULL_ERROR);

        long res=userService.userRegister(userName,userPassword,checkedPassword);

        return resultutils.success(res);
    }

    @PostMapping("/login")
    public generalRespon<User> userdoLogin(@RequestBody userLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){

        if(userLoginRequest==null) throw new businessException(errorCode.NULL_ERROR);
        String userName=userLoginRequest.getUserName();

        String userPassword=userLoginRequest.getUserPassword();


        if(StringUtils.isAnyBlank(userName,userPassword)) throw new businessException(errorCode.NULL_ERROR);

        User user = userService.userLogin(userName, userPassword, httpServletRequest);

        return resultutils.success(user);
    }

    @GetMapping("/search")
    public generalRespon<List<User>> userSearch(String userName, HttpServletRequest httpServletRequest){
        return resultutils.success(userService.userSearch(userName,httpServletRequest));
    }

    @GetMapping("/delete")
    public generalRespon<Boolean> deleteUser(long id, HttpServletRequest httpServletRequest){
        return resultutils.success(userService.deleteUser(id,httpServletRequest));
    }

    @RequestMapping("/current")
    public generalRespon<User> currentUser(HttpServletRequest httpServletRequest){
        User currentUser = (User) httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        long id=currentUser.getId();
        User user=userService.getById(id);
        return resultutils.success(userService.hideUserInfo(user));
    }

    @PostMapping("/logout")
    public generalRespon<Integer> userLogout(HttpServletRequest httpServletRequest){
        if(httpServletRequest==null){
            throw new businessException(errorCode.PARAMS_ERROR);
        }
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,null);
        return resultutils.success(1);
    }
}
