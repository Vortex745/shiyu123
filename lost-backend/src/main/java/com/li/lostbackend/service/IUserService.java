package com.li.lostbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li.lostbackend.entity.User;

/**
 * 用户服务接口
 */
public interface IUserService extends IService<User> {

    /**
     * 用户登录
     * @param user 用户实体（包含账号密码）
     * @return Token 字符串
     */
    String login(User user);

    /**
     * 用户注册
     * @param user 用户实体
     * @return 是否成功
     */
    boolean register(User user);
}