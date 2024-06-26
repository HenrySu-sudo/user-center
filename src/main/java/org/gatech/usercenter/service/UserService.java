package org.gatech.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.gatech.usercenter.entity.User;

import java.util.List;


/**
* @author hsu
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-06-24 17:02:18
*/

public interface UserService extends IService<User> {
    public long userRegister(String userAccount,String userPassword,String checkedPassword);


    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    public User hideUserInfo(User usedUser);

    public List<User> userSearch(String userName,HttpServletRequest httpServletRequest);

    public boolean deleteUser(long id,HttpServletRequest httpServletRequest);

    public boolean isAdmin(HttpServletRequest httpServletRequest);
}
