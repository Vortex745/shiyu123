package com.li.lostbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.lostbackend.entity.PostItem;
import com.li.lostbackend.entity.dto.PostItemPublishDTO;
import com.li.lostbackend.mapper.PostItemMapper;
import com.li.lostbackend.service.IPostItemService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.li.lostbackend.service.MatchingStrategy;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class PostItemServiceImpl extends ServiceImpl<PostItemMapper, PostItem> implements IPostItemService {

    @Resource
    private MatchingStrategy matchingStrategy; // 注入刚才写的策略
    @Override
    public void publish(PostItemPublishDTO dto, Long userId) {
        var postItem = new PostItem();
        BeanUtil.copyProperties(dto, postItem);

        postItem.setUserId(userId);
        postItem.setStatus(0); // 0-未解决
        // ✅ 正确代码 (使用 new Date())
        postItem.setCreateTime(new java.util.Date());

        this.save(postItem);
        matchingStrategy.processMatch(postItem);
    }

    @Override
    public Page<PostItem> pageQuery(Page<PostItem> page, Integer type, String keyword, String category) {
        // JDK 18 写法：使用 var 简化
        var wrapper = new LambdaQueryWrapper<PostItem>();

        // 1. 类型筛选
        wrapper.eq(type != null, PostItem::getType, type);

        // 2. 分类筛选
        wrapper.eq(StringUtils.hasLength(category), PostItem::getCategory, category);

        // 3. 关键词搜索 (标题 或 描述)
        if (StringUtils.hasLength(keyword)) {
            wrapper.and(w -> w.like(PostItem::getTitle, keyword)
                    .or()
                    .like(PostItem::getDescription, keyword));
        }

        // 4. 按时间倒序
        wrapper.orderByDesc(PostItem::getCreateTime);

        return this.page(page, wrapper);
    }
}