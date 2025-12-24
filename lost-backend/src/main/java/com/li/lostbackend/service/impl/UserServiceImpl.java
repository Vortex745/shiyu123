package com.li.lostbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.lostbackend.entity.User;
import com.li.lostbackend.mapper.UserMapper;
import com.li.lostbackend.service.IUserService;
import com.li.lostbackend.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // 👈 必须导入
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    @Lazy // 👈【关键修复】加上 @Lazy 解决循环依赖死锁
    private PasswordEncoder passwordEncoder;

    @Override
    public String login(User user) {
        User dbUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (dbUser == null || !passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        return jwtUtils.generateToken(dbUser.getUsername());
    }

    @Override
    public boolean register(User user) {
        User existUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (existUser != null) return false;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.save(user);
    }
}