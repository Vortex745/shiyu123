<template>
  <div class="home-container">
    <div class="navbar-wrapper">
      <div class="navbar">
        <div class="logo-area" @click="handleTabChange('all')">
          <span class="logo-icon">🌼</span>
          <span class="logo-text">失物招领</span>
        </div>

        <div class="search-box">
          <el-input
              v-model="queryParams.keyword"
              placeholder="搜索物品..."
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </div>

        <div class="user-actions">
          <el-button type="primary" class="publish-btn" :icon="Plus" @click="openPublishDialog">
            发布
          </el-button>

          <el-dropdown placement="bottom-end" popper-class="daisy-dropdown-popper">
            <div class="avatar-wrapper">
              <el-avatar :size="40" :src="userInfo.avatarUrl || defaultAvatar" class="nav-avatar" />
              <span class="username">{{ userInfo.nickname || '同学' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user')">个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="main-content">

      <div class="tabs-container">
        <div class="tabs-track" :style="tabStyle">
          <div class="tab-glider"></div>

          <div class="tab-item" :class="{ active: activeTab === 'all' }" @click="handleTabChange('all')">全部</div>
          <div class="tab-item" :class="{ active: activeTab === '0' }" @click="handleTabChange('0')">👋 失物招领</div>
          <div class="tab-item" :class="{ active: activeTab === '1' }" @click="handleTabChange('1')">🔍 寻物启事</div>
        </div>
      </div>

      <div
          class="post-list-wrapper"
          v-infinite-scroll="loadMore"
          :infinite-scroll-disabled="disabled"
          infinite-scroll-distance="100"
      >
        <TransitionGroup
            name="list-anim"
            tag="div"
            class="post-grid"
            v-loading="loading && postList.length === 0"
        >
          <div
              v-for="(item, index) in postList"
              :key="item.id"
              class="post-card"
              @click="goToDetail(item.id)"
              :style="{ '--i': index }"
          >
            <div class="card-image-box">
              <img :src="item.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'" class="card-img"/>
              <div class="glass-tag" :class="item.type === 0 ? 'tag-lost' : 'tag-found'">
                {{ item.type === 0 ? '捡到' : '丢失' }}
              </div>
            </div>

            <div class="card-body">
              <h3 class="title">{{ item.title }}</h3>

              <div class="tags-row">
                <span class="category-badge">
                  <el-icon class="badge-icon"><PriceTag /></el-icon>
                  {{ item.category }}
                </span>
                <span class="time-text">{{ formatTime(item.createTime) }}</span>
              </div>

              <div class="location-row">
                <el-icon class="location-icon-highlight"><LocationFilled /></el-icon>
                <span>{{ item.location }}</span>
              </div>
            </div>
          </div>
        </TransitionGroup>

        <el-empty v-if="postList.length === 0 && !loading" description="暂无相关物品" />

        <div class="loading-state">
          <p v-if="loading && postList.length > 0">🌱 正在努力加载中...</p>
          <p v-if="noMore && postList.length > 0">🌼 到底啦，没有更多内容了~</p>
        </div>
      </div>
    </div>

    <PublishDialog ref="publishDialogRef" @success="handlePublishSuccess" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Plus, LocationFilled, PriceTag } from '@element-plus/icons-vue'
import { getPostList } from '../api/post'
import { getUserInfo } from '../api/user'
import PublishDialog from '../components/PublishDialog.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const postList = ref([])
const total = ref(0)
const activeTab = ref('all')
const publishDialogRef = ref(null)
const userInfo = ref({})
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const queryParams = reactive({
  pageNum: 1, pageSize: 8, keyword: '', type: null
})

// 计算滑块位置
const tabStyle = computed(() => {
  let index = 0
  if (activeTab.value === '0') index = 1
  if (activeTab.value === '1') index = 2
  return { '--glider-index': index }
})

const noMore = computed(() => postList.value.length >= total.value && total.value > 0)
const disabled = computed(() => loading.value || noMore.value)

const loadData = async (isAppend = false) => {
  loading.value = true
  try {
    const res = await getPostList(queryParams)
    if (isAppend) {
      postList.value = [...postList.value, ...res.records]
    } else {
      // 覆盖模式：先清空，让 Vue 感知到变化以触发进场动画
      postList.value = []
      // 稍微延迟赋值，确保动画流畅执行
      setTimeout(() => {
        postList.value = res.records
      }, 50)
    }
    total.value = res.total
  } catch (error) { console.error(error) }
  finally { loading.value = false }
}

const loadMore = () => {
  if (!noMore.value) {
    queryParams.pageNum++
    loadData(true)
  }
}

const loadUser = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res
  } catch (error) { console.error(error) }
}

const handleSearch = () => { queryParams.pageNum = 1; loadData(false) }

const handleTabChange = (name) => {
  activeTab.value = name
  queryParams.type = name === 'all' ? null : parseInt(name)
  queryParams.pageNum = 1
  loadData(false)
}

const openPublishDialog = () => publishDialogRef.value.open()

const handlePublishSuccess = () => {
  queryParams.pageNum = 1; loadData(false); loadUser();
}

const logout = () => { localStorage.removeItem('token'); router.push('/login') }
const goToDetail = (id) => router.push(`/post/${id}`)
const formatTime = (t) => t ? t.substring(5, 16).replace('T', ' ') : ''

onMounted(() => {
  loadData(false)
  loadUser()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: var(--cream-bg);
  overflow-y: scroll;
}

/* 导航栏 */
.navbar-wrapper { position: sticky; top: 20px; z-index: 100; padding: 0 40px; display: flex; justify-content: center; }
.navbar {
  width: 100%; max-width: 1400px; height: 70px; background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px); border-radius: 50px; box-shadow: 0 8px 30px rgba(0,0,0,0.04);
  display: flex; align-items: center; justify-content: space-between; padding: 0 30px; border: 1px solid rgba(255,255,255,0.6);
}
.logo-area { display: flex; align-items: center; gap: 10px; font-size: 24px; font-weight: 800; color: var(--warm-text); cursor: pointer; }
.logo-icon { animation: spin 20s linear infinite; }
.search-box { width: 400px; }
.user-actions { display: flex; gap: 20px; align-items: center; }

/* 消除黑框 */
.el-dropdown { outline: none !important; }
.avatar-wrapper {
  display: flex; align-items: center; gap: 10px; cursor: pointer;
  padding: 5px 5px 5px 15px; border-radius: 30px; transition: 0.3s;
  outline: none !important; user-select: none;
}
.avatar-wrapper:hover { background: #fff0d1; }
.nav-avatar { border: 2px solid white; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.username { font-weight: 600; color: var(--warm-text); }

/* === 滑块式标签页 (自然缓动) === */
.tabs-container { display: flex; justify-content: center; margin: 40px 0; }
.tabs-track {
  position: relative;
  display: flex;
  background: white;
  padding: 5px;
  border-radius: 50px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03);
  --glider-index: 0;
}

.tab-glider {
  position: absolute;
  top: 5px; left: 5px; bottom: 5px;
  width: calc((100% - 10px) / 3);
  background: var(--daisy-yellow);
  border-radius: 40px;
  z-index: 0;
  /* 0.6s + 柔和曲线：像果冻一样自然滑行，不急促 */
  transition: transform 0.6s cubic-bezier(0.25, 1, 0.5, 1);
  transform: translateX(calc(100% * var(--glider-index)));
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.3);
}

.tab-item {
  position: relative;
  z-index: 1;
  padding: 10px 30px;
  min-width: 120px;
  text-align: center;
  border-radius: 40px;
  color: var(--soft-gray);
  font-weight: 600;
  cursor: pointer;
  transition: color 0.4s; /* 文字颜色变化也稍微慢一点 */
  user-select: none;
}
.tab-item.active { color: var(--warm-text); }

/* 内容布局 */
.main-content { max-width: 1400px; margin: 0 auto; padding: 0 40px 60px 40px; }

/* === 列表动画 (错落展开) === */
.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  min-height: 60vh;
}

/* 进场：时间拉长到0.8s，配合延迟实现波浪效果 */
.list-anim-enter-active {
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  /* 核心：根据 --i 计算延迟 */
  transition-delay: calc(var(--i) * 0.08s);
}

/* 离场：快速消失，避免占位 */
.list-anim-leave-active {
  transition: all 0.3s ease-in;
  position: absolute;
  opacity: 0;
  z-index: -1;
}

/* 进场前：下沉+透明 */
.list-anim-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.96);
}

/* 离场后 */
.list-anim-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

/* 卡片样式 */
.post-card {
  background: white; border-radius: 24px; overflow: hidden; cursor: pointer;
  border: 1px solid transparent; box-shadow: 0 4px 20px rgba(0,0,0,0.02);
  /* 独立定义 hover 动画，避免被 TransitionGroup 覆盖 */
  transition: transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1), box-shadow 0.4s ease;
}
.post-card:hover { transform: translateY(-10px); box-shadow: 0 20px 40px rgba(255, 193, 7, 0.15); border-color: rgba(255, 193, 7, 0.3); }

.card-image-box { height: 200px; overflow: hidden; position: relative; }
.card-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.6s; }
.post-card:hover .card-img { transform: scale(1.1); }
.glass-tag { position: absolute; top: 15px; left: 15px; padding: 6px 16px; border-radius: 20px; color: white; font-size: 12px; font-weight: bold; backdrop-filter: blur(4px); box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.tag-lost { background: rgba(255, 107, 107, 0.85); }
.tag-found { background: rgba(29, 209, 161, 0.85); }

.card-body { padding: 20px; }
.title { margin: 0 0 15px; font-size: 18px; color: var(--warm-text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.tags-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.category-badge {
  background: #fff8e1; color: #d35400;
  padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 700;
  display: flex; align-items: center; gap: 4px;
}
.badge-icon { font-size: 14px; }
.time-text { font-size: 12px; color: #ccc; }

.location-row {
  display: flex; align-items: center; gap: 8px;
  color: #7f8c8d; font-size: 13px; font-weight: 500;
}
.location-icon-highlight {
  color: var(--daisy-orange);
  font-size: 16px;
  filter: drop-shadow(0 2px 4px rgba(255, 159, 67, 0.3));
}

.post-grid :deep(.el-empty) { grid-column: 1 / -1; padding: 60px 0; }
.loading-state { text-align: center; padding: 40px 0; color: #999; font-size: 14px; }
</style>