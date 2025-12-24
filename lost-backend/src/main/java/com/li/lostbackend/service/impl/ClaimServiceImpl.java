package com.li.lostbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.lostbackend.entity.ClaimRecord;
import com.li.lostbackend.entity.PostItem;
import com.li.lostbackend.mapper.ClaimRecordMapper;
import com.li.lostbackend.mapper.PostItemMapper;
import com.li.lostbackend.service.IClaimService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ClaimServiceImpl extends ServiceImpl<ClaimRecordMapper, ClaimRecord> implements IClaimService {

    @Resource
    private PostItemMapper postItemMapper; // 需要查询物品信息

    @Override
    public void apply(Long postId, String proofInfo, Long userId) {
        // 1. 检查物品是否存在
        var post = postItemMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("物品不存在");
        }

        // 2. 不能认领自己发布的
        if (post.getUserId().equals(userId)) {
            throw new RuntimeException("不能认领自己发布的物品");
        }

        // 3. 检查是否重复申请
        var count = this.count(new LambdaQueryWrapper<ClaimRecord>()
                .eq(ClaimRecord::getPostId, postId)
                .eq(ClaimRecord::getApplicantId, userId)
                .eq(ClaimRecord::getStatus, 0)); // 0-待审核
        if (count > 0) {
            throw new RuntimeException("您已提交过申请，请等待审核");
        }

        // 4. 保存申请记录
        var record = new ClaimRecord();
        record.setPostId(postId);
        record.setApplicantId(userId);
        record.setProofInfo(proofInfo);
        record.setStatus(0); // 待审核
        record.setClaimTime(LocalDateTime.now());

        this.save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证数据一致性
    public void audit(Long claimId, boolean pass, String reply, Long currentUserId) {
        // 1. 查询申请记录
        var record = this.getById(claimId);
        if (record == null) throw new RuntimeException("申请记录不存在");

        // 2. 只有发布者本人才能审核 (安全校验)
        var post = postItemMapper.selectById(record.getPostId());
        if (!post.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权审核此申请");
        }

        // 3. 更新审核状态
        record.setStatus(pass ? 1 : 2); // 1-通过, 2-驳回
        record.setAuditReply(reply);
        this.updateById(record);

        // 4. 如果审核通过，需要把物品状态改为 "2-已解决"
        if (pass) {
            post.setStatus(2);
            postItemMapper.updateById(post);
        }
    }
}