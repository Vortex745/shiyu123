package com.li.lostbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认领申请记录表
 */
@Data
@TableName("claim_record")
public class ClaimRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;        // 关联的物品ID
    private Long applicantId;   // 申请人ID (谁想认领)
    private String proofInfo;   // 证明信息 (比如："那个包里有我的身份证")
    private Integer status;     // 0-待审核, 1-通过, 2-驳回
    private String auditReply;  // 审核回复 (比如："请到一号楼门卫处领取")
    private LocalDateTime claimTime;
}