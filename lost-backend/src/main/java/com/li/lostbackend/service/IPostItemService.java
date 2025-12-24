package com.li.lostbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.li.lostbackend.entity.PostItem;
import com.li.lostbackend.entity.dto.PostItemPublishDTO;

public interface IPostItemService extends IService<PostItem> {

    /**
     * 发布物品
     */
    void publish(PostItemPublishDTO dto, Long userId);

    /**
     * 分页搜索查询
     */
    Page<PostItem> pageQuery(Page<PostItem> page, Integer type, String keyword, String category);
}