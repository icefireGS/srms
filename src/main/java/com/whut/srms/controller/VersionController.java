package com.whut.srms.controller;

import com.whut.srms.pojo.Version;
import com.whut.srms.pojo.versionListNode;
import com.whut.srms.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("version")
public class VersionController {

    @Autowired
    private VersionService versionService;

    /**
     * 查询历史文件
     *
     * @param id;
     * @param user_id;
     * @return
     */
    @PostMapping("query")
    public ResponseEntity<List<versionListNode>> queryVersions(@RequestParam("id") Long id, @RequestParam("user_id") Long user_id) {
        List<versionListNode> result = this.versionService.queryVersions(id, user_id);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 删除历史文件
     *
     * @param id;
     * @param user_id;
     * @return
     */
    @DeleteMapping("{id}/{user_id}")
    public ResponseEntity<Void> deleteVersion(@PathVariable("id") Long id, @PathVariable("user_id") Long user_id) {
        Boolean boo = this.versionService.deleteVersion(id, user_id);
        if (boo == null || !boo) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 获取历史文件地址
     *
     * @param id;
     * @param user_id;
     * @return
     */
    @GetMapping("getAddress/{id}/{user_id}")
    public ResponseEntity<String> getVerAddress(@PathVariable("id") Long id, @PathVariable("user_id") Long user_id) {
        String address = this.versionService.getVerAddress(id, user_id);
        if (address == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(address);
    }
}
