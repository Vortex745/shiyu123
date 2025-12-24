package com.li.lostbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user") // 对应数据库表名
public class User {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;

    private String username; // 学号/工号
    private String password; // 密码
    private String nickname; // 姓名
    private String phone;    // 电话
    private String role;     // 角色 (USER/ADMIN)
    private String avatar;   // 头像
    private LocalDateTime createTime; // 注册时间
    private String avatarUrl;
}