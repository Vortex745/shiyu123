package com.li.lostbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.li.lostbackend.entity.PostItem;
import com.li.lostbackend.entity.SystemMessage;
import com.li.lostbackend.mapper.PostItemMapper;
import com.li.lostbackend.mapper.SystemMessageMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MatchingStrategy {

    @Resource
    private PostItemMapper postItemMapper;

    @Resource
    private SystemMessageMapper systemMessageMapper;

    /**
     * 执行智能匹配 (异步执行，不卡顿主线程)
     * @param newItem 刚刚发布的物品
     */
    @Async
    public void processMatch(PostItem newItem) {
        // 1. 确定目标类型 (如果是丢了东西 1，就去找捡到东西 0；反之亦然)
        int targetType = (newItem.getType() == 1) ? 0 : 1;

        // 2. 第一层筛选：必须是同一种类 (比如都是 "卡证")
        List<PostItem> candidates = postItemMapper.selectList(new LambdaQueryWrapper<PostItem>()
                .eq(PostItem::getType, targetType)
                .eq(PostItem::getCategory, newItem.getCategory())
                .eq(PostItem::getStatus, 0)); // 只找未解决的

        // 3. 第二层筛选：关键词匹配 (核心算法)
        for (PostItem candidate : candidates) {
            if (isSimilar(newItem, candidate)) {
                // 4. 匹配成功！发送消息
                sendMessage(newItem, candidate);
            }
        }
    }

    /**
     * 判断两个物品是否相似
     * 逻辑：标题或描述中包含对方的关键词
     */
    private boolean isSimilar(PostItem item1, PostItem item2) {
        // 简单算法：只要标题互相包含，或者描述互相包含，就算匹配
        // 比如：item1="丢失饭卡", item2="捡到一张饭卡" -> 包含 "饭卡" -> 匹配
        boolean titleMatch = item1.getTitle().contains(item2.getTitle()) || item2.getTitle().contains(item1.getTitle());
        boolean descMatch = item1.getDescription().contains(item2.getDescription()) || item2.getDescription().contains(item1.getDescription());

        return titleMatch || descMatch;
    }

    /**
     * 发送系统通知
     */
    private void sendMessage(PostItem newItem, PostItem matchedItem) {
        SystemMessage msg = new SystemMessage();
        msg.setUserId(newItem.getUserId()); // 通知刚刚发布的人
        msg.setContent("系统检测到有一条相似物品：[" + matchedItem.getTitle() + "]，快去看看吧！");
        msg.setRelatedPostId(matchedItem.getId());
        msg.setStatus(0);
        msg.setCreateTime(LocalDateTime.now());

        systemMessageMapper.insert(msg);
    }
}