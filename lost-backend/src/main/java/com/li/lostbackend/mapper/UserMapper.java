package com.li.lostbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li.lostbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 已经帮你写好了 insert, delete, update, selectById 等常用方法
    // 所以这里暂时什么都不用写，是不是很爽？
}