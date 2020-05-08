package com.whut.srms.mapper;

import com.whut.srms.pojo.User;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
}
