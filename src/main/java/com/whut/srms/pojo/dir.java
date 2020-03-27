package com.whut.srms.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "dir")
@Data
public class dir {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            //目录id

    private String name;        //目录名

    private Long user_id;       //目录所属用户id

    private Long pid;           //目录所属目录id

    private Date create_time;   //目录创建时间

    private String path;        //目录虚拟路径
}
