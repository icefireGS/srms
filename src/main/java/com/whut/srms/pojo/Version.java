package com.whut.srms.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "version")
@Data
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;           //版本id

    @JsonIgnore
    private Long file_id;      //文件id

    @JsonIgnore
    private Long user_id;      //用户id

    private String name;       //文件名

    private Date update_time;  //更新时间

    @JsonIgnore
    private String address;    //文件地址
}
