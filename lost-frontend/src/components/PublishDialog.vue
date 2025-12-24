<template>
  <el-dialog
      v-model="visible"
      title="发布信息"
      width="500px"
      :close-on-click-modal="false"
      @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">

      <el-form-item label="类型" prop="type">
        <el-radio-group v-model="form.type">
          <el-radio :label="0">👋 失物招领 (我捡到了)</el-radio>
          <el-radio :label="1">🔍 寻物启事 (我丢了)</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="图片" prop="imageUrl">
        <el-upload
            class="avatar-uploader"
            action="/api/file/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
        >
          <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="tips">支持 jpg/png，不超过 5MB</div>
      </el-form-item>

      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="例如：在二食堂捡到一个黑色钱包" />
      </el-form-item>

      <el-form-item label="分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择物品分类" style="width: 100%">
          <el-option label="卡证 (身份证/饭卡)" value="卡证" />
          <el-option label="电子数码" value="电子数码" />
          <el-option label="书籍资料" value="书籍资料" />
          <el-option label="日用衣物" value="日用衣物" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>

      <el-form-item label="地点" prop="location">
        <el-input v-model="form.location" placeholder="例如：一号教学楼302教室" />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请详细描述物品特征、拾取/丢失具体时间等..."
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">发 布</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { publishPost } from '../api/post'

// 定义父组件传过来的事件
const emit = defineEmits(['success'])

const visible = ref(false)
const loading = ref(false)
const formRef = ref(null)

// 上传需要携带 Token
const uploadHeaders = computed(() => ({
  Authorization: localStorage.getItem('token')
}))

const form = reactive({
  type: 0,
  title: '',
  category: '',
  location: '',
  description: '',
  imageUrl: ''
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }]
}

// === 暴露给父组件的方法 ===
const open = () => {
  visible.value = true
  // 重置表单
  if (formRef.value) formRef.value.resetFields()
  form.imageUrl = ''
}
// 导出 open 方法，让 Home.vue 可以调用
defineExpose({ open })

// === 图片上传逻辑 ===
const handleAvatarSuccess = (response) => {
  // 后端返回 Result结构: {code: 200, data: "http://...", msg: "..."}
  if (response.code === 200) {
    form.imageUrl = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// === 提交表单 ===
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await publishPost(form)
        ElMessage.success('发布成功！')
        visible.value = false
        // 通知父组件刷新列表
        emit('success')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleClose = () => {
  // 关闭时的清理工作
}
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px; /* 垂直居中 */
}

.tips {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>