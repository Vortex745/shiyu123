package com.li.lostbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.li.lostbackend.common.api.Result;
import com.li.lostbackend.entity.ClaimRecord;
import com.li.lostbackend.entity.User;
import com.li.lostbackend.service.IClaimService;
import com.li.lostbackend.service.IUserService;
import com.li.lostbackend.util.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claim")
public class ClaimController {

    @Resource
    private IClaimService claimService;
    @Resource
    private IUserService userService;
    @Resource
    private JwtUtils jwtUtils;

    // 辅助方法：获取当前登录用户ID
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = jwtUtils.getUsernameFromToken(token);
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user.getId();
    }

    /**
     * 1. 提交认领申请
     * POST /api/claim/apply
     * 参数: {"postId": 1, "proofInfo": "这是我的包"}
     */
    @PostMapping("/apply")
    public Result<String> apply(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);

        Long postId = Long.valueOf(params.get("postId").toString());
        String proofInfo = params.get("proofInfo").toString();

        claimService.apply(postId, proofInfo, userId);
        return Result.success("申请提交成功，请等待审核");
    }

    /**
     * 2. 审核申请 (发布者操作)
     * POST /api/claim/audit
     * 参数: {"claimId": 5, "pass": true, "reply": "好的，来一食堂拿"}
     */
    @PostMapping("/audit")
    public Result<String> audit(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);

        Long claimId = Long.valueOf(params.get("claimId").toString());
        boolean pass = (boolean) params.get("pass");
        String reply = (String) params.get("reply");

        claimService.audit(claimId, pass, reply, userId);
        return Result.success("审核完成");
    }

    /**
     * 3. 查看某物品的所有申请记录 (发布者看)
     * GET /api/claim/list?postId=1
     */
    @GetMapping("/list")
    public Result<List<ClaimRecord>> list(@RequestParam Long postId) {
        // JDK 18 var 写法
        var list = claimService.list(new LambdaQueryWrapper<ClaimRecord>()
                .eq(ClaimRecord::getPostId, postId)
                .orderByDesc(ClaimRecord::getClaimTime));
        return Result.success(list);
    }
}