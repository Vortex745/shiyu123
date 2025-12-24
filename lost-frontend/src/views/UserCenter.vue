<template>
  <div class="center-container animate__animated animate__fadeIn">

    <div class="profile-header card-base">
      <div class="profile-left">
        <el-avatar :size="90" :src="userInfo.avatarUrl || defaultAvatar" class="user-avatar" />
        <div class="user-info">
          <h2 class="nickname">{{ userInfo.nickname || '这里是昵称' }}</h2>
          <p class="username">学工号：{{ userInfo.username }}</p>
        </div>
      </div>
      <div class="profile-right">
        <el-button type="primary" round icon="Edit" @click="openEditDialog">编辑资料</el-button>
        <el-button round icon="Back" @click="$router.push('/home')">返回首页</el-button>
      </div>
    </div>

    <div class="content-tabs card-base">
      <el-tabs v-model="activeTab" class="daisy-tabs">
        <el-tab-pane label="📮 我发布的" name="posts">
          <el-table :data="myPosts" style="width: 100%" :header-cell-style="{background:'#fffdf5',color:'#5d4037'}">
            <el-table-column prop="title" label="物品标题" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="100" align="center">
              <template #default="scope">
                <span class="type-badge" :class="scope.row.type===0?'lost':'found'">
                  {{ scope.row.type === 0 ? '捡到' : '丢失' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="info" effect="dark" round>进行中</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="warning" effect="dark" round>审核中</el-tag>
                <el-tag v-else type="success" effect="dark" round>已解决</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="160">
              <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="scope">
                <el-button size="small" type="primary" plain round @click="openAuditDialog(scope.row)">
                  查看申请
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="🔔 消息通知" name="messages">
          <el-empty v-if="messages.length === 0" description="暂无新消息，去广场看看吧~" image-size="120" />
          <div v-else class="msg-list">
            <div v-for="msg in messages" :key="msg.id" class="msg-card" @click="$router.push(`/post/${msg.relatedPostId}`)">
              <div class="msg-icon">✨</div>
              <div class="msg-content">
                <p class="msg-text">{{ msg.content }}</p>
                <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
              </div>
              <el-icon class="msg-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="editVisible" title="编辑个人资料" width="480px" custom-class="daisy-dialog">
      <el-form label-position="top">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="给自己起个好听的名字吧" />
        </el-form-item>
        <el-form-item label="选择头像">
          <div class="avatar-selection">
            <div
                v-for="(img, index) in defaultAvatars" :key="index"
                class="avatar-option"
                :class="{ selected: editForm.selectedAvatarIndex === index && !editForm.uploadedAvatarUrl }"
                @click="selectDefaultAvatar(index)"
            >
              <img :src="img" />
            </div>
            <el-upload
                class="avatar-uploader-option"
                action="/api/file/upload"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
            >
              <div class="upload-placeholder" :class="{ hasImage: editForm.uploadedAvatarUrl }">
                <img v-if="editForm.uploadedAvatarUrl" :src="editForm.uploadedAvatarUrl" class="uploaded-img" />
                <el-icon v-else><Plus /></el-icon>
              </div>
            </el-upload>
          </div>
          <div class="tips">点击选择默认头像，或点击加号上传自定义头像</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button round @click="editVisible = false">取消</el-button>
        <el-button type="primary" round @click="submitEdit" :loading="editLoading">保存修改</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="auditVisible" title="处理认领申请" width="600px" custom-class="daisy-dialog">
      <el-table :data="claimList" border :header-cell-style="{background:'#fdfdfd'}">
        <el-table-column prop="proofInfo" label="申请人证明/留言" show-overflow-tooltip />
        <el-table-column prop="claimTime" label="申请时间" width="150">
          <template #default="scope">{{ formatTime(scope.row.claimTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" align="center">
          <template #default="scope">
            <div v-if="scope.row.status === 0" class="audit-btns">
              <el-button type="success" size="small" circle icon="Check" @click="handleAudit(scope.row, true)"></el-button>
              <el-button type="danger" size="small" circle icon="Close" @click="handleAudit(scope.row, false)"></el-button>
            </div>
            <div v-else>
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" round>
                {{ scope.row.status === 1 ? '已通过' : '已驳回' }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getMyPosts, getMyMessages, getUserInfo, updateUserInfo } from '../api/user'
import { getClaimList, auditClaim } from '../api/claim'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Back, ArrowRight, Plus, Check, Close } from '@element-plus/icons-vue'

// 默认兜底头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
// 5个可爱的默认头像选项
const defaultAvatars = [
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Felix',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Aneka',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Coco',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Milo',
  'https://api.dicebear.com/7.x/adventurer/svg?seed=Loki'
]

const userInfo = ref({})
const activeTab = ref('posts')
const myPosts = ref([])
const messages = ref([])

// 编辑相关
const editVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  nickname: '',
  selectedAvatarIndex: -1,
  uploadedAvatarUrl: ''
})
const uploadHeaders = computed(() => ({ Authorization: localStorage.getItem('token') }))

// 审核相关
const auditVisible = ref(false)
const claimList = ref([])

// 加载所有数据
const loadData = async () => {
  try {
    const [resUser, resPosts, resMsgs] = await Promise.all([
      getUserInfo(), getMyPosts(), getMyMessages()
    ])
    userInfo.value = resUser
    myPosts.value = resPosts
    messages.value = resMsgs
  } catch (error) { console.error(error) }
}

// === 编辑资料逻辑 ===
const openEditDialog = () => {
  editForm.nickname = userInfo.value.nickname
  editForm.selectedAvatarIndex = -1
  editForm.uploadedAvatarUrl = ''
  editVisible.value = true
}

// 选择默认头像
const selectDefaultAvatar = (index) => {
  editForm.selectedAvatarIndex = index
  editForm.uploadedAvatarUrl = ''
}

// 上传成功回调
const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    editForm.uploadedAvatarUrl = res.data
    editForm.selectedAvatarIndex = -1
    ElMessage.success('头像上传成功')
  }
}

// 提交修改
const submitEdit = async () => {
  if (!editForm.nickname) return ElMessage.warning('请输入昵称')

  let finalAvatarUrl = userInfo.value.avatarUrl
  if (editForm.uploadedAvatarUrl) {
    finalAvatarUrl = editForm.uploadedAvatarUrl
  } else if (editForm.selectedAvatarIndex !== -1) {
    finalAvatarUrl = defaultAvatars[editForm.selectedAvatarIndex]
  }

  editLoading.value = true
  try {
    await updateUserInfo({
      nickname: editForm.nickname,
      avatarUrl: finalAvatarUrl
    })
    ElMessage.success('修改成功')
    editVisible.value = false
    loadData() // 刷新本地数据
  } catch (error) { console.error(error) }
  finally { editLoading.value = false }
}

// === 审核逻辑 ===
const openAuditDialog = async (row) => {
  try {
    const res = await getClaimList(row.id)
    claimList.value = res
    auditVisible.value = true
  } catch (error) { console.error(error) }
}
const handleAudit = (row, pass) => {
  ElMessageBox.prompt('请输入给对方的回复', pass ? '确认通过' : '确认驳回', {
    confirmButtonText: '确定', cancelButtonText: '取消', inputPattern: /.+/, inputErrorMessage: '回复不能为空'
  }).then(async ({ value }) => {
    try {
      await auditClaim({ claimId: row.id, pass, reply: value })
      ElMessage.success('操作成功')
      openAuditDialog({ id: row.postId })
    } catch (error) { console.error(error) }
  })
}

const formatTime = (t) => t ? t.substring(5, 16).replace('T', ' ') : ''
onMounted(() => loadData())
</script>

<style scoped>
.center-container { max-width: 1000px; margin: 30px auto; padding: 0 20px; }

/* 通用卡片底座 */
.card-base { background: white; border-radius: var(--radius-lg); box-shadow: var(--shadow-soft); padding: 30px; margin-bottom: 30px; }

/* 1. 头部卡片 */
.profile-header { display: flex; justify-content: space-between; align-items: center; background: linear-gradient(to right, #fffdf5, #fff); }
.profile-left { display: flex; align-items: center; gap: 25px; }
.user-avatar { border: 4px solid white; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
.nickname { margin: 0 0 10px; font-size: 28px; color: var(--warm-text); }
.username { margin: 0; color: var(--soft-gray); font-size: 14px; }
.profile-right { display: flex; gap: 15px; }

/* 2. 内容标签页 */
.daisy-tabs :deep(.el-tabs__nav-wrap::after) { height: 1px; background-color: #f0f0f0; }
.daisy-tabs :deep(.el-tabs__item) { font-size: 16px; padding: 0 25px; }

/* 表格样式 */
.type-badge { padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: bold; color: white; }
.lost { background: #ff6b6b; } .found { background: #1dd1a1; }
.audit-btns { display: flex; justify-content: center; gap: 10px; }

/* 消息列表 */
.msg-list { display: flex; flex-direction: column; gap: 15px; }
.msg-card {
  display: flex; align-items: center; padding: 20px;
  background: #fffcf2; border-radius: var(--radius-md); cursor: pointer;
  transition: all 0.3s; border: 1px solid transparent;
}
.msg-card:hover { background: white; box-shadow: 0 4px 15px rgba(255, 193, 7, 0.15); border-color: var(--daisy-yellow); transform: translateX(5px); }
.msg-icon { font-size: 24px; margin-right: 20px; }
.msg-content { flex: 1; }
.msg-text { margin: 0 0 8px; font-weight: 500; color: var(--warm-text); }
.msg-time { font-size: 12px; color: #a89f91; }
.msg-arrow { color: #ccc; }

/* 编辑弹窗：头像选择区 */
.avatar-selection { display: flex; gap: 15px; flex-wrap: wrap; margin-top: 10px; padding: 5px; }

/* 头像选项 & 上传框 (统一大圆角) */
.avatar-option, .upload-placeholder {
  width: 64px; height: 64px;
  border-radius: 24px; /* 大圆角 */
  border: 3px solid #f0f2f5;
  cursor: pointer; overflow: hidden;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  padding: 3px; background: white;
  display: flex; justify-content: center; align-items: center;
}

.avatar-option img { width: 100%; height: 100%; border-radius: 20px; object-fit: cover; }
.uploaded-img { width: 100%; height: 100%; border-radius: 20px; object-fit: cover; display: block; }

.avatar-option:hover, .upload-placeholder:hover {
  border-color: var(--daisy-orange); transform: translateY(-3px) scale(1.05); box-shadow: 0 6px 15px rgba(255, 159, 67, 0.2);
}
.avatar-option.selected, .upload-placeholder.hasImage {
  border-color: var(--daisy-yellow); box-shadow: 0 0 0 4px rgba(255, 193, 7, 0.3); transform: scale(1.05);
}
.upload-placeholder.hasImage { background: white; padding: 3px; }

/* 加号图标颜色 */
.upload-placeholder .el-icon { font-size: 24px; color: #ccc; }

.tips { font-size: 12px; color: #999; margin-top: 10px; }
</style>