package com.whut.srms.controller;

import com.whut.srms.pojo.Share;
import com.whut.srms.pojo.ShareInfo;
import com.whut.srms.pojo.ShareListNode;
import com.whut.srms.service.ShareService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("share")
public class ShareController {

    @Autowired
    private ShareService shareService;

    /**
     * 查询分享列表
     *
     * @param id;
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<List<ShareListNode>> queryShare(@RequestParam("id") Long id) {
        return ResponseEntity.ok(this.shareService.queryShare(id));
    }

    /**
     * 创建公开分享
     *
     * @param pubShare
     * @return
     */
    @PostMapping("create/public")
    public ResponseEntity<Void> createPublicShare(@Valid Share pubShare) {
        try {
            Boolean boo = this.shareService.createPublicShare(pubShare);
            if (boo == null || !boo) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 创建私密分享
     *
     * @param priShare
     * @return
     */
    @PostMapping("create/private")
    public ResponseEntity<String> createPrivateShare(@Valid Share priShare) {
        String code = this.shareService.createPrivateShare(priShare);
        if (StringUtils.isBlank(code)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(code);
    }

    /**
     * 获取分享详细信息
     *
     * @param id
     * @param user_id
     * @return
     */
    @PostMapping("getinfo")
    public ResponseEntity<ShareInfo> getShareInfo(@RequestParam("id") Long id, @RequestParam("user_id") Long user_id) {
        ShareInfo info = this.shareService.getShareInfo(id, user_id);
        if (info == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(info);
    }

    /**
     * 根据分享码提取分享文件
     *
     * @param code
     * @return
     */
    @GetMapping("extract")
    public ResponseEntity<ShareInfo> extractShare(@RequestParam("code") String code) {
        ShareInfo info = this.shareService.extractShare(code);
        if (info == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("{id}/{user_id}")
    public ResponseEntity<Void> deleteShare(@PathVariable("id") Long id, @PathVariable("user_id") Long user_id) {
        try {
            Boolean boo = this.shareService.deleteShare(id, user_id);
            if (boo == null || !boo) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
