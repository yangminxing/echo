package org.echo.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        if ("admin".equals(username) && "123456".equals(password)) {
            StpUtil.login(username);
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", StpUtil.getTokenValue());
        } else {
            result.put("code", 401);
            result.put("msg", "用户名或密码错误");
        }

        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> result = new HashMap<>();
        StpUtil.logout();
        result.put("code", 200);
        result.put("msg", "登出成功");
        return result;
    }
}