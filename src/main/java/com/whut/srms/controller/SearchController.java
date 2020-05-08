package com.whut.srms.controller;

import com.whut.srms.pojo.fileListNode;
import com.whut.srms.pojo.searchRequest;
import com.whut.srms.pojo.searchResult;
import com.whut.srms.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 模糊查询目录和文件
     *
     * @param key
     * @param user_id
     * @return
     */
    @PostMapping("userfile")
    public ResponseEntity<List<fileListNode>> searchFile(@RequestParam("key") String key, @RequestParam("user_id") Long user_id) {
        return ResponseEntity.ok(this.searchService.searchFile(key, user_id));
    }

    /**
     * 搜索公开分享
     *
     * @param request
     * @return
     */
    @PostMapping("sharefile")
    public ResponseEntity<searchResult> searchShare(@Valid searchRequest request) {
        searchResult result = this.searchService.searchShare(request);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result);
    }
}
