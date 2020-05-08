package com.whut.srms.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class searchResult {

    private Long total; //总条数
    private Integer totalpage;   //总页数
    private List<ShareListNode> items;   //数据

}
