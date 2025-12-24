package com.li.lostbackend.controller;

import com.li.lostbackend.common.api.Result;
import com.li.lostbackend.entity.User;
import com.li.lostbackend.entity.dto.UserLoginDTO;
import com.li.lostbackend.entity.dto.UserRegisterDTO;
import com.li.lostbackend.service.IUserService;
import org.springframework.beans.BeanUtils; // 👈 关键工具：用于数据复制
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth") // 基础路径，这样访问就是 /api/auth/register
public class AuthController {

    @Autowired
    private IUserService userService;

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        // 1. 准备一个空的 User 实体对象
        User user = new User();

        // 2. 关键步骤：把前端传来的 DTO 数据复制到 User 实体里
        // 前提：DTO 和 User 里的字段名（username, password）必须一样
        BeanUtils.copyProperties(registerDTO, user);

        // 3. 现在可以把 user 传给 Service 了
        boolean success = userService.register(user);

        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，用户名可能已存在");
        }
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid UserLoginDTO loginDTO) {
        // 1. 同样的套路，先转换数据
        User user = new User();
        BeanUtils.copyProperties(loginDTO, user);

        // 2. 调用登录逻辑，获取 Token
        // Service 里会负责校验密码，如果不通过会直接抛异常
        String token = userService.login(user);

        // 3. 返回 Token 给前端
        return Result.success(token);
    }
}