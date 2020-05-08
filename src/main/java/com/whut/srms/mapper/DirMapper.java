package com.whut.srms.mapper;

import com.whut.srms.pojo.Dir;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DirMapper extends Mapper<Dir> {

    @Select("SELECT * FROM dir WHERE name LIKE CONCAT('%',#{key},'%') AND user_id = #{user_id}")
    List<Dir> fuzzyByNameUser(String key, Long user_id);
}
