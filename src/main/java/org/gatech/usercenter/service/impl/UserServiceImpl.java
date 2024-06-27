package org.gatech.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gatech.usercenter.entity.User;
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
            return -1;
        }
        if(userAccount.length()<4) return -1;
        if(!userPassword.equals(checkedPassword)) return -1;
        if(userPassword.length()<8) return -1;

        String accountPattern="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher=Pattern.compile(accountPattern).matcher(userAccount);
        if(matcher.find()) return -1;

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count>0) return -1;

        String encryptPassword= DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());

        User user=new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(userAccount);

        userMapper.insert(user);

        if(user.getId()==null){
            return -1;
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        if(StringUtils.isAnyBlank(userAccount,userPassword))    return null;
        if(userAccount.length()<4) return null;
        if(userPassword.length()<8) return null;
        String accountPattern="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher=Pattern.compile(accountPattern).matcher(userAccount);
        if(matcher.find()) return null;

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        userQueryWrapper.eq("userAccount",userAccount);
        userQueryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);

        if(user==null) return null;

        user=hideUserInfo(user);

        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,user);
        return user;
    }

    public List<User> userSearch(String userName,HttpServletRequest httpServletRequest){
        if(!isAdmin(httpServletRequest)) return null;

        QueryWrapper<User> queryWrapper=new QueryWrapper<>();

        if(StringUtils.isNotBlank(userName)){
            queryWrapper.like("username",userName);
        }
        List<User> res=list(queryWrapper);
        List<User> users = res.stream().map(user -> hideUserInfo(user)).collect(Collectors.toList());
        return users;
    }

    public boolean deleteUser(long id, HttpServletRequest httpServletRequest){
        if(!isAdmin(httpServletRequest)) return false;
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




