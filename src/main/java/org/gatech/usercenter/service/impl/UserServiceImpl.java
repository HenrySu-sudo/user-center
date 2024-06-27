package org.gatech.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gatech.usercenter.common.errorCode;
import org.gatech.usercenter.entity.User;
import org.gatech.usercenter.exception.businessException;
import org.gatech.usercenter.mapper.UserMapper;
import org.gatech.usercenter.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.gatech.usercenter.constant.userConstant.USER_LOGIN_STATE;

/**
* @author hsu
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-06-24 17:02:18
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    final static private String  SALT="henry";
    public long userRegister(String userAccount,String userPassword,String checkedPassword){

        if(StringUtils.isAnyBlank(userAccount,userPassword,checkedPassword)){
            throw new businessException(errorCode.NULL_ERROR);
        }
        if(userAccount.length()<4) {
            throw new businessException(errorCode.PARAMS_ERROR,"用户名长度过短");
        };
        if(!userPassword.equals(checkedPassword)){
            throw new businessException(errorCode.PARAMS_ERROR,"两次密码不等");
        };
        if(userPassword.length()<8) {
            throw new businessException(errorCode.PARAMS_ERROR,"密码过短");
        };

        String accountPattern="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher=Pattern.compile(accountPattern).matcher(userAccount);
        if(matcher.find()) {
            throw new businessException(errorCode.PARAMS_ERROR,"用户名包含特殊符号");
        };

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count>0) {
            throw new businessException(errorCode.PARAMS_ERROR,"该用户名已经注册");
        };

        String encryptPassword= DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());

        User user=new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(userAccount);

        userMapper.insert(user);

        if(user.getId()==null){
            throw new businessException(errorCode.SYSTEM_ERROR,"请重试");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        if(StringUtils.isAnyBlank(userAccount,userPassword))  {
            throw new businessException(errorCode.NULL_ERROR);
        };
        if(userAccount.length()<4) {
            throw new businessException(errorCode.PARAMS_ERROR,"用户名过短");
        };
        if(userPassword.length()<8) {
            throw new businessException(errorCode.NULL_ERROR,"密码错误");
        };
        String accountPattern="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher=Pattern.compile(accountPattern).matcher(userAccount);
        if(matcher.find()) {
            throw new businessException(errorCode.PARAMS_ERROR);
        };

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq("userAccount",userAccount);
        userQueryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);

        if(user==null) {
            throw new businessException(errorCode.PARAMS_ERROR,"密码或账户错误");
        };

        user=hideUserInfo(user);

        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,user);
        return user;
    }

    public List<User> userSearch(String userName,HttpServletRequest httpServletRequest){
        if(!isAdmin(httpServletRequest)) {
            throw new businessException(errorCode.NO_AUTH);
        };

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();

        if(StringUtils.isNotBlank(userName)){
            queryWrapper.like("username",userName);
        }
        List<User> res=list(queryWrapper);
        List<User> users = res.stream().map(user -> hideUserInfo(user)).collect(Collectors.toList());
        return users;
    }

    public boolean deleteUser(long id, HttpServletRequest httpServletRequest){
        if(!isAdmin(httpServletRequest)) {
            throw new businessException(errorCode.NO_AUTH);
        };
        boolean res=this.removeById(id);
        return res;
    }

    @Override
    public User hideUserInfo(User usedUser) {
        if(usedUser==null) return null;
        User safeUser=new User();
        safeUser.setId(usedUser.getId());
        safeUser.setUsername(usedUser.getUsername());
        safeUser.setUserAccount(usedUser.getUserAccount());
        safeUser.setAvatarUrl(usedUser.getAvatarUrl());
        safeUser.setGender(usedUser.getGender());
        safeUser.setUserPassword("");
        safeUser.setPhone(usedUser.getPhone());
        safeUser.setEmail(usedUser.getEmail());
        safeUser.setUserStatus(usedUser.getUserStatus());
        safeUser.setCreateTime(usedUser.getCreateTime());
        safeUser.setUserRole(usedUser.getUserRole());
        safeUser.setPlanetCode(usedUser.getPlanetCode());
        return safeUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest httpServletRequest) {
        User user= (User) httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        return user!=null && user.getUserRole()==1;
    }


}




