package com.whut.srms.controller;

import com.whut.srms.pojo.Dir;
import com.whut.srms.pojo.File;
import com.whut.srms.pojo.fileListNode;
import com.whut.srms.pojo.fileTreeNode;
import com.whut.srms.property.CustomInfoProperties;
import com.whut.srms.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private CustomInfoProperties customInfoProperties;

    /**
     * 获取文件树
     * @param id;
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

    /**
     * 获取目录文件列表
     *
     * @param id;
     * @param user_id;
     * @return
     */
    @PostMapping("getlist")
    public ResponseEntity<List<fileListNode>> getfileList(@RequestParam("id") Long id, @RequestParam("user_id") Long user_id) {
        List<fileListNode> result = this.fileService.getfileList(id, user_id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 创建目录
     * @param dir;
     * @return
     */
    @PostMapping("mkdir")
    public ResponseEntity<Void> makeDir(@Valid Dir dir) {
        Boolean boo = this.fileService.mkdir(dir);
        if (boo == null | !boo) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);   //201
    }

    /**
     * 上传文件
     * @param file_info;
     * @param file;
     * @return
     */
    @PostMapping("upload")
    public ResponseEntity<String> uploadFile(@Valid File file_info, @RequestParam("file") MultipartFile file) {
        try {
            String url = this.fileService.uploadFile(file_info, file);
            if(StringUtils.isBlank(url)) {
                return ResponseEntity.badRequest().body("未知错误");
            }
            if (url.startsWith(customInfoProperties.getErrorHeader())) {
                return ResponseEntity.badRequest().body(url);
            }
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除文件
     *
     * @param id;
     * @param user_id;
     * @return
     */
    @DeleteMapping("isfile/{id}/{user_id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") Long id, @PathVariable("user_id") Long user_id) {
        try {
            Boolean boo = this.fileService.deleteFile(id, user_id);
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
     * 删除目录
     *
     * @param id;
     * @param user_id;
     * @return
     */
    @DeleteMapping("isdir/{id}/{user_id}")
    public ResponseEntity<Void> deleteDir(@PathVariable("id") Long id, @PathVariable("user_id") Long user_id) {
        try {
            Boolean boo = this.fileService.deleteDir(id, user_id);
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
     * 更新文件
     *
     * @param id;
     * @param file;
     * @param user_id;
     * @return
     */
    @PostMapping("update")
    public ResponseEntity<String> updateFile(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file, @RequestParam("user_id") Long user_id) {
        try {
            String url = this.fileService.updateFile(id, file, user_id);
            if (StringUtils.isBlank(url)) {
                return ResponseEntity.badRequest().body("未知错误");
            }
            if (url.startsWith(customInfoProperties.getErrorHeader())) {
                return ResponseEntity.badRequest().body(url);
            }
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 复制文件
     *
     * @param id;
     * @param pid_new;
     * @param user_id;
     * @return
     */
    @PostMapping("copy/isfile")
    public ResponseEntity<String> copyFile(@RequestParam("id") Long id, @RequestParam("pid_new") Long pid_new, @RequestParam("user_id") Long user_id) {
        try {
            String url = this.fileService.copyFile(id, pid_new, user_id);
            if (StringUtils.isBlank(url)) {
                return ResponseEntity.badRequest().body("未知错误");
            }
            if (url.startsWith(customInfoProperties.getErrorHeader())) {
                return ResponseEntity.badRequest().body(url);
            }
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 重命名文件
     *
     * @param id;
     * @param name_new;
     * @param user_id;
     * @return
     */
    @PostMapping("rename/isfile")
    public ResponseEntity<Void> renameFile(@RequestParam("id") Long id, @RequestParam("name_new") String name_new, @RequestParam("user_id") Long user_id) {
        try {
            Boolean boo = this.fileService.renameFile(id, name_new.trim(), user_id);
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
     * 移动文件
     *
     * @param id;
     * @param pid_new;
     * @param user_id;
     * @return
     */
    @PostMapping("move/isfile")
    public ResponseEntity<Void> moveFile(@RequestParam("id") Long id, @RequestParam("pid_new") Long pid_new, @RequestParam("user_id") Long user_id) {
        try {
            Boolean boo = this.fileService.moveFile(id, pid_new, user_id);
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
     * 重命名目录
     *
     * @param id;
     * @param name_new;
     * @param user_id;
     * @return
     */
    @PostMapping("rename/isdir")
    public ResponseEntity<Void> renameDir(@RequestParam("id") Long id, @RequestParam("name_new") String name_new, @RequestParam("user_id") Long user_id) {
        try {
            Boolean boo = this.fileService.renameDir(id, name_new.trim(), user_id);
            if (boo == null || !boo) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 移动目录
     *
     * @param id;
     * @param pid_new;
     * @param user_id;
     * @return
     */
    @PostMapping("move/isdir")
    public ResponseEntity<Void> moveDir(@RequestParam("id") Long id, @RequestParam("pid_new") Long pid_new, @RequestParam("user_id") Long user_id) {
        try {
            Boolean boo = this.fileService.moveDir(id, pid_new, user_id);
            if (boo == null || !boo) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 复制目录
     *
     * @param id;
     * @param pid_new;
     * @param user_id;
     * @return
     */
    @PostMapping("copy/isdir")
    public ResponseEntity<List<String>> copyDir(@RequestParam("id") Long id, @RequestParam("pid_new") Long pid_new, @RequestParam("user_id") Long user_id) {
        try {
            List<String> urls = this.fileService.copyDir(id, pid_new, user_id);
            if (urls == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取文件地址
     *
     * @param id;
     * @param user_id;
     * @return
     */
    @GetMapping("getAddress/{id}/{user_id}")
    public ResponseEntity<String> getAddress(@PathVariable("id") Long id, @PathVariable("user_id") Long user_id) {
        String address = this.fileService.getAddress(id, user_id);
        if (address == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(address);
    }
}
