package com.whut.srms.controller;

import com.whut.srms.pojo.fileTreeNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {

    /**
    * 获取文件树
    * @param id
    * @return
    */
    @GetMapping("query")
    public ResponseEntity<List<fileTreeNode>> queryTree(@RequestParam("id") Long id){
        return null;
    }
}
