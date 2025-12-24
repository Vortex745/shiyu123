import request from '../utils/request'

// 获取我发布的物品
export function getMyPosts() {
    return request({
        url: '/post/my',
        method: 'get'
    })
}

// 获取我的消息
export function getMyMessages() {
    return request({
        url: '/message/my',
        method: 'get'
    })
}
// ✅ 新增：获取当前用户信息
export function getUserInfo() {
    return request({ url: '/user/info', method: 'get' })
}

// ✅ 新增：更新用户信息
export function updateUserInfo(data) {
    return request({ url: '/user/update', method: 'post', data })
}