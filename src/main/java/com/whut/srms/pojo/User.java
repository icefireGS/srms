package com.whut.srms.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 3, max = 10, message = "用户名只能在3~10位之间")
    private String username;

    @JsonIgnore
    @Length(min = 6, max = 15, message = "密码只能在6~15位之间")
    private String password;

    private Integer type;

    private Date create_time;

    private String intro;

    private String img;

    @Pattern(regexp = "^1[35678]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private Long size;   //当前空间大小

    private Long max_size; //最大空间大小

}
