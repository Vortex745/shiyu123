package com.li.lostbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.li.lostbackend.common.api.Result;
import com.li.lostbackend.entity.User;
import com.li.lostbackend.entity.dto.UserUpdateDTO;
import com.li.lostbackend.service.IUserService;
import com.li.lostbackend.util.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private JwtUtils jwtUtils;

    // 辅助方法：获取当前用户
    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = jwtUtils.getUsernameFromToken(token);
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        User user = getCurrentUser(request);
        // 脱敏处理，不返回密码
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户信息 (昵称、头像)
     */
    @PostMapping("/update")
    public Result<String> updateUserInfo(@RequestBody @Valid UserUpdateDTO dto, HttpServletRequest request) {
        User user = getCurrentUser(request);

        user.setNickname(dto.getNickname());
        user.setAvatarUrl(dto.getAvatarUrl());

        userService.updateById(user);
        return Result.success("修改成功");
    }
}