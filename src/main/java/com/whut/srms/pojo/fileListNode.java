package com.whut.srms.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class fileListNode {

    private String name;
    private String type;    //文件类型(扩展名)
    private Long id;
    private Integer isfile;       //0为目录,1为文件
    private Long size;           //大小
    private Date time;          //修改日期
    private String url;        //文件实际地址
}
