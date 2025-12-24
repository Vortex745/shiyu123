package com.li.lostbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.li.lostbackend.common.api.Result;
import com.li.lostbackend.entity.SystemMessage;
import com.li.lostbackend.entity.User;
import com.li.lostbackend.mapper.SystemMessageMapper;
import com.li.lostbackend.service.IUserService;
import com.li.lostbackend.util.JwtUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Resource
    private SystemMessageMapper systemMessageMapper;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private IUserService userService;

    @GetMapping("/my")
    public Result<List<SystemMessage>> getMyMessages(HttpServletRequest request) {
        // 1. 获取当前用户
        String token = request.getHeader("Authorization");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 2. 查询该用户的消息 (按时间倒序)
        var list = systemMessageMapper.selectList(new LambdaQueryWrapper<SystemMessage>()
                .eq(SystemMessage::getUserId, user.getId())
                .orderByDesc(SystemMessage::getCreateTime));

        return Result.success(list);
    }
}