import request from '../utils/request'

// 提交认领 (已有)
export function applyClaim(data) {
    return request({
        url: '/claim/apply',
        method: 'post',
        data
    })
}

// ✅ 新增：获取某物品的所有申请记录
export function getClaimList(postId) {
    return request({
        url: '/claim/list',
        method: 'get',
        params: { postId }
    })
}

// ✅ 新增：审核申请
export function auditClaim(data) {
    return request({
        url: '/claim/audit',
        method: 'post',
        data // { claimId, pass, reply }
    })
}