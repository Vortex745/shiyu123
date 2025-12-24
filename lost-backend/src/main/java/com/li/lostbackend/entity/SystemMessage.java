package com.li.lostbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_message")
public class SystemMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;        // 接收消息的人
    private String content;     // 消息内容
    private Long relatedPostId; // 关联的那个相似物品ID
    private Integer status;     // 0-未读, 1-已读
    private LocalDateTime createTime;
}