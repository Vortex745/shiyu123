<template>
  <div class="detail-container">

    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回列表</span>
      </div>
    </div>

    <div class="detail-card" v-loading="loading">

      <div class="image-section">
        <div class="image-wrapper">
          <el-image
              :src="post.imageUrl || 'https://via.placeholder.com/400x400?text=No+Image'"
              fit="cover"
              class="main-image"
              :preview-src-list="post.imageUrl ? [post.imageUrl] : []"
          >
            <template #error>
              <div class="image-slot"><el-icon><Picture /></el-icon></div>
            </template>
          </el-image>
          <div class="float-tag" :class="post.type === 0 ? 'tag-lost' : 'tag-found'">
            {{ post.type === 0 ? '👋 捡到了' : '🔍 丢失了' }}
          </div>
        </div>
      </div>

      <div class="info-section">
        <div class="info-header">
          <h1 class="post-title">{{ post.title }}</h1>
          <div class="status-badge" :class="getStatusClass(post.status)">
            {{ getStatusText(post.status) }}
          </div>
        </div>

        <div class="tags-group">
          <div class="icon-tag-box category-theme">
            <div class="icon-circle"><el-icon><PriceTag /></el-icon></div>
            <span class="tag-text">{{ post.category }}</span>
          </div>

          <div class="icon-tag-box time-theme">
            <div class="icon-circle"><el-icon><Timer /></el-icon></div>
            <span class="tag-text">{{ formatTime(post.createTime) }}</span>
          </div>
        </div>

        <div class="location-box">
          <el-icon class="loc-icon"><LocationFilled /></el-icon>
          <div>
            <div class="label">地点</div>
            <div class="value">{{ post.location }}</div>
          </div>
        </div>

        <el-divider border-style="dashed" />

        <div class="desc-box">
          <div class="label">详细描述</div>
          <p class="desc-text">{{ post.description }}</p>
        </div>

        <div class="action-footer">
          <el-button
              type="primary"
              class="main-action-btn"
              @click="openClaimDialog"
              :disabled="post.status !== 0"
              :icon="post.type === 0 ? 'Position' : 'Trophy'"
              round
          >
            {{ btnText }}
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog
        v-model="claimVisible"
        :title="post.type === 0 ? '我是失主，我要认领' : '我有线索，提供帮助'"
        width="450px"
        class="daisy-dialog"
        :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <el-alert
            :title="post.type === 0 ? '请详细描述物品特征，证明它是你的。' : '请留下物品的具体位置或联系方式。'"
            type="warning"
            :closable="false"
            show-icon
            style="margin-bottom: 15px; border-radius: 12px;"
        />
        <el-form :model="claimForm">
          <el-form-item>
            <el-input
                v-model="claimForm.proofInfo"
                type="textarea"
                :rows="5"
                placeholder="例如：钱包里有一张张三的身份证，卡号后四位是..."
                resize="none"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="claimVisible = false" round>再想想</el-button>
          <el-button type="primary" @click="submitClaim" round color="#ff9f43">
            🚀 立即提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostDetail } from '../api/post'
import { applyClaim } from '../api/claim'
import { ElMessage } from 'element-plus'
// ✅ 引入 PriceTag, LocationFilled, Timer 等图标
import { PriceTag, LocationFilled, Timer, ArrowLeft, Picture, Position, Trophy } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const post = ref({})
const claimVisible = ref(false)
const claimForm = ref({ proofInfo: '' })

const btnText = computed(() => {
  if (post.value.status === 1) return '⏳ 正在审核中...'
  if (post.value.status === 2) return '✅ 已成功解决'
  return post.value.type === 0 ? '这东西是我的 (认领)' : '我捡到了/有线索'
})

const getStatusText = (status) => {
  if (status === 0) return '进行中'
  if (status === 1) return '审核中'
  return '已完结'
}

const getStatusClass = (status) => {
  if (status === 0) return 'status-active'
  if (status === 1) return 'status-audit'
  return 'status-done'
}

const loadDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getPostDetail(id)
    post.value = res
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const openClaimDialog = () => {
  claimForm.value.proofInfo = ''
  claimVisible.value = true
}

const submitClaim = async () => {
  if (!claimForm.value.proofInfo) return ElMessage.warning('请填写内容')
  try {
    await applyClaim({
      postId: post.value.id,
      proofInfo: claimForm.value.proofInfo
    })
    ElMessage.success({
      message: '提交成功！请耐心等待发布者审核',
      type: 'success',
    })
    claimVisible.value = false
  } catch (error) { console.error(error) }
}

const formatTime = (t) => t ? t.substring(0, 16).replace('T', ' ') : ''

onMounted(() => loadDetail())
</script>

<style scoped>
.detail-container { max-width: 1000px; margin: 0 auto; padding: 40px 20px; min-height: 100vh; }

/* 导航 */
.nav-bar { margin-bottom: 20px; }
.back-btn {
  display: inline-flex; align-items: center; gap: 5px; cursor: pointer;
  padding: 8px 16px; background: white; border-radius: 20px; color: #666;
  font-weight: 600; transition: all 0.3s; box-shadow: 0 4px 10px rgba(0,0,0,0.03);
}
.back-btn:hover { background: #fff8e1; color: #ff9f43; transform: translateX(-3px); }

/* 卡片主体 */
.detail-card {
  background: white; border-radius: 32px; box-shadow: 0 10px 40px rgba(255, 193, 7, 0.1);
  display: flex; overflow: hidden; min-height: 500px;
}

/* 左侧图片 */
.image-section { flex: 5; background: #fdfbf7; padding: 30px; display: flex; align-items: center; justify-content: center; }
.image-wrapper { width: 100%; height: 400px; border-radius: 24px; overflow: hidden; position: relative; box-shadow: 0 8px 25px rgba(0,0,0,0.08); }
.main-image { width: 100%; height: 100%; }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #eee; color: #ccc; font-size: 40px; }
.float-tag { position: absolute; top: 20px; left: 20px; padding: 8px 18px; border-radius: 20px; color: white; font-weight: bold; backdrop-filter: blur(8px); box-shadow: 0 4px 15px rgba(0,0,0,0.15); }
.tag-lost { background: rgba(255, 107, 107, 0.9); }
.tag-found { background: rgba(29, 209, 161, 0.9); }

/* 右侧信息 */
.info-section { flex: 7; padding: 50px; display: flex; flex-direction: column; }
.info-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; }
.post-title { margin: 0; font-size: 28px; color: #5d4037; line-height: 1.4; flex: 1; margin-right: 20px; }
.status-badge { padding: 6px 14px; border-radius: 12px; font-size: 13px; font-weight: bold; }
.status-active { background: #e3f2fd; color: #2196f3; }
.status-audit { background: #fff3e0; color: #ff9800; }
.status-done { background: #e8f5e9; color: #4caf50; }

/* ✅ 优化：属性标签组 (带图标圆形背景) */
.tags-group { display: flex; gap: 20px; margin-bottom: 30px; }

.icon-tag-box {
  display: flex; align-items: center; gap: 10px;
  padding: 8px 16px 8px 8px; /* 左边距小，右边距大 */
  border-radius: 30px; font-size: 14px; font-weight: 600;
  transition: all 0.3s;
}
.icon-tag-box:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.05); }

.icon-circle {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; justify-content: center; align-items: center; font-size: 18px;
}

/* 分类主题 (橙色) */
.category-theme { background: #fff3e0; color: #e67e22; }
.category-theme .icon-circle { background: white; color: #e67e22; box-shadow: 0 2px 6px rgba(230, 126, 34, 0.2); }

/* 时间主题 (灰色) */
.time-theme { background: #f5f5f5; color: #7f8c8d; }
.time-theme .icon-circle { background: white; color: #95a5a6; box-shadow: 0 2px 6px rgba(0,0,0,0.05); }

/* 地点 */
.location-box {
  display: flex; align-items: center; gap: 15px;
  background: #fffbf2; padding: 15px 20px; border-radius: 16px;
  border: 1px solid #ffe0b2; margin-bottom: 20px;
}
.loc-icon {
  font-size: 24px; color: #ff9f43;
  background: linear-gradient(135deg, #fff, #fff3e0);
  padding: 10px; border-radius: 50%; box-shadow: 0 2px 8px rgba(255, 159, 67, 0.2);
}
.label { font-size: 12px; color: #a89f91; margin-bottom: 4px; }
.value { font-size: 16px; font-weight: bold; color: #5d4037; }

.desc-box { flex: 1; margin-top: 10px; }
.desc-text { color: #666; line-height: 1.8; font-size: 15px; background: #fafafa; padding: 15px; border-radius: 12px; }

.action-footer { margin-top: 30px; }
.main-action-btn {
  width: 100%; height: 54px; font-size: 18px; letter-spacing: 1px;
  background: linear-gradient(135deg, #ffc107, #ff9f43); border: none;
  box-shadow: 0 8px 20px rgba(255, 159, 67, 0.3); transition: all 0.3s;
}
.main-action-btn:hover { transform: translateY(-3px); box-shadow: 0 12px 25px rgba(255, 159, 67, 0.4); }
.main-action-btn:disabled { background: #eee; color: #999; box-shadow: none; transform: none; }
</style>