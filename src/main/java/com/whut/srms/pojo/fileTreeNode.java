package com.whut.srms.pojo;

import lombok.Data;

import java.util.List;

@Data
public class fileTreeNode {
    private String name;
    private Long pid;
    private Long id;
    private Integer isfile;
    private List<fileTreeNode> children;
}
