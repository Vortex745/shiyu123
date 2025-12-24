package com.li.lostbackend.entity.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PostItemPublishDTO {

    @NotNull(message = "类型不能为空")
    private Integer type;       // 0-失物招领, 1-寻物启事

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "分类不能为空")
    private String category;    // 书籍/证件/电子产品...

    @NotBlank(message = "描述不能为空")
    private String description;

    @NotBlank(message = "地点不能为空")
    private String location;

    private String imageUrl;    // 图片链接 (上传接口返回的那个 URL)
}