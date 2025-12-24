package com.li.lostbackend.entity.dto;

import lombok.Data;

@Data
public class PostQueryDTO {
    // 当前页码 (默认第1页)
    private Integer pageNum = 1;

    // 每页条数 (默认10条)
    private Integer pageSize = 10;

    // 搜索关键词
    private String keyword;

    // 物品类型 (0-失物, 1-寻物, null-全部)
    private Integer type;
}