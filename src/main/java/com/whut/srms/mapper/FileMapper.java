package com.whut.srms.mapper;

import com.whut.srms.pojo.File;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FileMapper extends Mapper<File> {

    @Select("SELECT * FROM file WHERE name LIKE CONCAT('%',#{key},'%') AND user_id = #{user_id}")
    List<File> fuzzyByNameUser(String key, Long user_id);
}
