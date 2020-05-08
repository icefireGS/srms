package com.whut.srms.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "dir")
@Data
public class Dir {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            //目录id

    @Pattern(regexp = "^[^\\\\/:*?\"<>|]{1,255}$",message = "目录命名格式不正确")
    private String name;        //目录名

    private Long user_id;       //目录所属用户id

    private Long pid;           //目录所属目录id

    private Date create_time;   //目录创建时间

}
