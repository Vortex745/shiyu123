package com.li.lostbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li.lostbackend.entity.ClaimRecord;

public interface IClaimService extends IService<ClaimRecord> {

    // 提交认领申请
    void apply(Long postId, String proofInfo, Long userId);

    // 审核认领 (pass: true通过, false驳回)
    void audit(Long claimId, boolean pass, String reply, Long currentUserId);
}