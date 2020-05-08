package com.whut.srms.security;

import com.whut.srms.mapper.UserMapper;
import com.whut.srms.pojo.User;
import com.whut.srms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyCustomUserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登陆验证时，通过username获取用户的所有权限信息
     * 并返回UserDetails放到spring的全局缓存SecurityContextHolder中，以供授权器使用
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //在这里可以自己调用数据库，对username进行查询，看看在数据库中是否存在
        User user = userMapper.findByUsername(username);
        MyUserDetails myUserDetail = new MyUserDetails();
        myUserDetail.setUsername(user.getUsername());
        myUserDetail.setPassword(user.getPassword());
        myUserDetail.setType(user.getType());
        return myUserDetail;
    }
}