package com.li.lostbackend.entity;

import lombok.Data;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.sub_erupt.Power;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.ViewType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.annotation.sub_field.sub_edit.ChoiceType;
import xyz.erupt.annotation.sub_field.sub_edit.VL;

import javax.persistence.*;
import java.util.Date;

@Erupt(
        name = "失物招领管理",
        power = @Power(export = true)
)
@Entity
@Table(name = "post_item")
@Data
public class PostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EruptField
    private Long id;

    // 1. 必须保留 userId (业务逻辑需要)
    private Long userId;

    // 2. 补全 category 字段 (智能匹配需要)
    @EruptField(
            views = @View(title = "物品分类"),
            edit = @Edit(title = "物品分类", notNull = true, search = @Search(vague = true))
    )
    private String category;

    @EruptField(
            views = @View(title = "标题"),
            edit = @Edit(title = "标题", notNull = true, search = @Search(vague = true))
    )
    private String title;

    // 3. 字段名必须叫 description (匹配你的 MatchingStrategy)
    @EruptField(
            views = @View(title = "详细描述"),
            edit = @Edit(title = "详细描述", type = EditType.TEXTAREA)
    )
    private String description;

    @EruptField(
            views = @View(title = "类型"),
            edit = @Edit(
                    title = "类型",
                    type = EditType.CHOICE,
                    choiceType = @ChoiceType(
                            vl = {
                                    @VL(value = "0", label = "失物招领"),
                                    @VL(value = "1", label = "寻物启事")
                            }
                    )
            )
    )
    private Integer type;

    @EruptField(
            views = @View(title = "状态"),
            edit = @Edit(
                    title = "状态",
                    type = EditType.CHOICE,
                    choiceType = @ChoiceType(
                            vl = {
                                    @VL(value = "0", label = "进行中"),
                                    @VL(value = "1", label = "审核中"),
                                    @VL(value = "2", label = "已解决")
                            }
                    )
            )
    )
    private Integer status;

    @EruptField(
            views = @View(title = "地点"),
            edit = @Edit(title = "地点", search = @Search(vague = true))
    )
    private String location;

    @EruptField(
            views = @View(title = "图片", type = ViewType.IMAGE),
            edit = @Edit(title = "图片", type = EditType.ATTACHMENT)
    )
    private String imageUrl;

    // 4. 注意这里是 java.util.Date
    @EruptField(
            views = @View(title = "发布时间"),
            edit = @Edit(title = "发布时间", type = EditType.DATE, show = false)
    )
    private Date createTime;

    // 5. 补全 updateTime
    private Date updateTime;
}