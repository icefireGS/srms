package com.whut.srms.controller;

import com.whut.srms.mapper.FileMapper;
import com.whut.srms.pojo.fileTreeNode;
import com.whut.srms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
    * 获取文件树
    * @param id
    * @return
    */
    @GetMapping("query")
    public ResponseEntity<List<fileTreeNode>> queryTree(@RequestParam("id") Long id){
        List<fileTreeNode> result = this.fileService.queryTree(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(result);
    }
}
