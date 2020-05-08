package com.whut.srms.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class ShareListNode {

    private Long id;
    private String name;
    private String type;
    private Integer share_type;
    private Date share_time;

}
