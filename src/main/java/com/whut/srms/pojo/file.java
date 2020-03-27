package com.whut.srms.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "file")
@Data
public class file {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            //文件

    private String name;        //文件名

    private Long user_id;       //文件所属用户id

    private Long pid;           //文件所属目录id

    private String path;        //文件虚拟路径

    private String address;     //文件实际地址

    private Date update_time;   //文件最后更新时间

    private String type;        //文件扩展名
}
