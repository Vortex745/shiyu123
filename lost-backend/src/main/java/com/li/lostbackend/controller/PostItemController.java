package com.li.lostbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.li.lostbackend.common.api.Result;
import com.li.lostbackend.entity.PostItem;
import com.li.lostbackend.entity.User;
import com.li.lostbackend.entity.dto.PostQueryDTO;
import com.li.lostbackend.service.IPostItemService;
import com.li.lostbackend.service.IUserService;
import com.li.lostbackend.util.JwtUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostItemController {

    @Resource
    private IPostItemService postItemService;

    @Resource
    private IUserService userService;

    @Resource
    private JwtUtils jwtUtils;

    /**
     * 发布物品
     * POST /api/post/publish
     */
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody PostItem postItem, HttpServletRequest request) {
        // 1. 获取当前登录用户
        String token = request.getHeader("Authorization");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 2. 补全信息
        postItem.setUserId(user.getId());
        postItem.setCreateTime(new java.util.Date());
        postItem.setStatus(0); // 默认状态：0-进行中

        // 3. 保存到数据库
        boolean success = postItemService.save(postItem);

        // (可选) 这里其实可以加入智能匹配的逻辑，但为了代码清晰，先不展开

        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 分页获取物品列表 (公共区域)
     * GET /api/post/list
     */
    @GetMapping("/list")
    public Result<IPage<PostItem>> list(PostQueryDTO query) {
        Page<PostItem> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PostItem> wrapper = new LambdaQueryWrapper<>();

        // 1. 关键词搜索 (标题、描述、地点)
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(PostItem::getTitle, query.getKeyword())
                    .or().like(PostItem::getDescription, query.getKeyword())
                    .or().like(PostItem::getLocation, query.getKeyword()));
        }

        // 2. 类型筛选 (失物/寻物)
        if (query.getType() != null) {
            wrapper.eq(PostItem::getType, query.getType());
        }

        // ✅✅✅ 核心修改：公共列表不显示“已解决” (status=2) 的物品
        wrapper.ne(PostItem::getStatus, 2);

        // 3. 按时间倒序 (最新的在前面)
        wrapper.orderByDesc(PostItem::getCreateTime);

        IPage<PostItem> res = postItemService.page(page, wrapper);
        return Result.success(res);
    }

    /**
     * 获取物品详情
     * GET /api/post/detail/{id}
     */
    @GetMapping("/detail/{id}")
    public Result<PostItem> detail(@PathVariable Long id) {
        PostItem postItem = postItemService.getById(id);
        return Result.success(postItem);
    }

    /**
     * 查询我发布的物品 (个人中心用)
     * GET /api/post/my
     * 注意：这里不需要过滤状态，自己发布的所有状态都应该能看到
     */
    @GetMapping("/my")
    public Result<List<PostItem>> getMyPosts(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 查询该用户的所有发布记录，按时间倒序
        List<PostItem> list = postItemService.list(new LambdaQueryWrapper<PostItem>()
                .eq(PostItem::getUserId, user.getId())
                .orderByDesc(PostItem::getCreateTime));

        return Result.success(list);
    }
}