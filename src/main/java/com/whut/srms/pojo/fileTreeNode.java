package com.whut.srms.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class fileTreeNode {
    private String name;
    private Long pid;
    private Long id;
    private Integer isfile;       //0为目录,1为文件
    private Long size;           //大小
    private Date time;          //修改日期
    private List<fileTreeNode> children;
    private String url;        //文件地址
}
