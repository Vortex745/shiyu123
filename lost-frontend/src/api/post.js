import request from '../utils/request'

// 1. 获取物品列表 (分页 + 搜索 + 筛选)
export function getPostList(params) {
    return request({
        url: '/post/list',
        method: 'get',
        params // 包含: pageNum, pageSize, type, keyword, category
    })
}

// 2. 文件上传 (图片)
export function uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/file/upload',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 3. 发布物品
export function publishPost(data) {
    return request({
        url: '/post/publish',
        method: 'post',
        data
    })
}
// 获取单条详情
export function getPostDetail(id) {
    return request({
        url: `/post/detail/${id}`,
        method: 'get'
    })
}