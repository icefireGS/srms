package com.whut.srms.controller;

import com.whut.srms.pojo.User;
import com.whut.srms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据是否可用
     * @param data;
     * @param type;
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type") Integer type) {
        Boolean boo = this.userService.checkData(data, type);
        if (boo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(boo);
    }

    /**
     * 发送手机验证码
     * @param phone;
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(String phone) {
        Boolean boo = this.userService.sendVerifyCode(phone);
        if (boo == null || !boo) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 注册
     * @param user;
     * @param code;
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        Boolean boo = this.userService.register(user, code);
        if (boo == null || !boo) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 根据用户名查询用户
     * @param username;
     * @return
     */
    @PostMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username
    ) {
        User user = this.userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 验证请求
     */
    @GetMapping("verify")
    public ResponseEntity<Map<String, String>> Verify() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "验证成功!");
        return ResponseEntity.ok(result);
    }
}
