<template>
  <div class="my-items-container">
    <div class="header">
      <h2>📦 我的物品</h2>
      <el-button size="small" @click="goBack">← 返回商城</el-button>
    </div>

    <!-- 分类筛选 -->
    <div class="filter-tabs">
      <el-tabs v-model="activeType" @tab-change="filterItems">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="装备" name="equip" />
        <el-tab-pane label="道具" name="item" />
        <el-tab-pane label="改名卡" name="name_card" />
      </el-tabs>
    </div>

    <!-- 物品列表 -->
    <div v-loading="loading" class="items-grid">
      <div
          v-for="item in filteredList"
          :key="item.id"
          class="item-card"
          :class="{ equipped: item.isEquipped }"
      >
        <div class="item-icon">{{ item.icon || '🎁' }}</div>
        <div class="item-info">
          <h4>{{ item.name }}</h4>
          <span class="item-type">{{ item.type }}</span>
          <span v-if="item.isEquipped" class="equip-badge">✅ 已装备</span>
        </div>
        <div class="item-actions">
          <el-button
              v-if="item.type === 'name_card'"
              size="small"
              type="primary"
              @click="showRenameDialog(item)"
          >
            使用
          </el-button>
          <el-button
              v-else-if="item.type === 'equip'"
              size="small"
              :type="item.isEquipped ? 'warning' : 'success'"
              @click="toggleEquip(item)"
              :loading="equipLoading === item.id"
          >
            {{ item.isEquipped ? '卸下' : '装备' }}
          </el-button>
          <el-button
              v-else
              size="small"
              type="info"
              plain
              disabled
          >
            已拥有
          </el-button>
        </div>
      </div>
      <el-empty v-if="!loading && filteredList.length === 0" description="暂无物品" />
    </div>

    <!-- 改名对话框 -->
    <el-dialog v-model="renameDialogVisible" title="改名" width="400px">
      <el-form>
        <el-form-item label="新昵称">
          <el-input v-model="newName" placeholder="请输入新昵称" maxlength="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRename" :loading="renameLoading">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyItems, equipItem, useNameCard } from '@/api/shop'
import { getUserId } from '@/utils/auth'

const router = useRouter()
const loading = ref(false)
const equipLoading = ref<number | null>(null)
const renameLoading = ref(false)
const renameDialogVisible = ref(false)
const activeType = ref('all')
const itemList = ref<any[]>([])
const renameTarget = ref<any>(null)
const newName = ref('')

const filteredList = computed(() => {
  if (activeType.value === 'all') return itemList.value
  return itemList.value.filter(item => item.type === activeType.value)
})

const loadItems = async () => {
  loading.value = true
  try {
    const data = await getMyItems()
    itemList.value = data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const toggleEquip = async (item: any) => {
  equipLoading.value = item.id
  try {
    await equipItem(item.itemId, !item.isEquipped)
    ElMessage.success(item.isEquipped ? '已卸下' : '已装备')
    await loadItems()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    equipLoading.value = null
  }
}

const showRenameDialog = (item: any) => {
  renameTarget.value = item
  newName.value = ''
  renameDialogVisible.value = true
}

const confirmRename = async () => {
  if (!newName.value.trim()) {
    ElMessage.warning('请输入新昵称')
    return
  }
  if (!renameTarget.value) return
  renameLoading.value = true
  try {
    await useNameCard(renameTarget.value.itemId, newName.value.trim())
    ElMessage.success('昵称修改成功！')
    renameDialogVisible.value = false
    await loadItems()
  } catch (error: any) {
    ElMessage.error(error.message || '修改失败')
  } finally {
    renameLoading.value = false
  }
}

const goBack = () => router.push('/shop')

onMounted(() => {
  loadItems()
})
</script>

<style scoped>
.my-items-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.filter-tabs {
  margin-bottom: 20px;
}
.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}
.item-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}
.item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.06);
}
.item-card.equipped {
  border-color: #67c23a;
  background: #f0f9f0;
}
.item-icon {
  font-size: 32px;
}
.item-info {
  flex: 1;
}
.item-info h4 {
  margin: 0;
  font-size: 14px;
  color: #303133;
}
.item-type {
  font-size: 12px;
  color: #909399;
}
.equip-badge {
  font-size: 12px;
  color: #67c23a;
  margin-left: 8px;
}
.item-actions {
  flex-shrink: 0;
}
</style>