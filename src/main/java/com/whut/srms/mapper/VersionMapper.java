package com.whut.srms.mapper;

import com.whut.srms.pojo.Version;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface VersionMapper extends Mapper<Version> {

    @Select("SELECT * FROM version WHERE file_id = #{file_id} ORDER BY update_time DESC")
    List<Version> selectByFileIdDESC(Long file_id);
}
