package com.whut.srms.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class versionListNode {
    private Long id;           //版本id
    private String name;       //文件名
    private Date update_time;  //更新时间
    private String url;         //文件url
}
