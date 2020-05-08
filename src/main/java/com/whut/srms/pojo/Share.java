package com.whut.srms.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "share")
@Data
public class Share {

    @Id
    private Long id;         //文件id

    private String name;     //文件名

    private Long user_id;    //用户id

    private String type;     //文件类型，检索条件

    private String address;   //文件地址

    private Integer share_type;  //分享类型，0：公开分享；1：私密分享

    private String code;     //分享码，公开分享为空

    private Date share_time;  //分享创建时间，检索条件

    private String subject;   //学科类别，检索条件

    private String period;   //学段类别，检索条件

    private String school;   //学校类别，检索条件

}
