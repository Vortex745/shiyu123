package com.li.lostbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.lostbackend.entity.User;
import com.li.lostbackend.mapper.UserMapper;
import com.li.lostbackend.service.IUserService;
import com.li.lostbackend.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // 👈 必须导入这个
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    @Lazy // 👈 加上 @Lazy，告诉 Spring：这个 Bean 晚点再加载，不要卡住启动
    private PasswordEncoder passwordEncoder;

    @Override
    public String login(User user) {
        // 1. 直接查询数据库
        User dbUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));

        if (dbUser == null) {
            throw new BadCredentialsException("用户不存在");
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }

        // 3. 生成 Token
        return jwtUtils.generateToken(dbUser.getUsername());
    }

    @Override
    public boolean register(User user) {
        // 1. 检查用户名
        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (existUser != null) {
            return false;
        }

        // 2. 密码加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        // 3. 保存
        return this.save(user);
    }
}