<template>
  <div class="shop-container">
    <!-- 顶部 -->
    <div class="shop-header">
      <h2>🏪 积分商城</h2>
      <div class="header-right">
        <span class="points-display">💰 积分：{{ userPoints }}</span>
        <el-button size="small" type="primary" plain @click="goToMyItems">
          📦 我的物品
        </el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div v-loading="loading" class="shop-grid">
      <div
          v-for="item in itemList"
          :key="item.id"
          class="shop-card"
          :style="{ animationDelay: (index * 0.05) + 's' }"
      >
        <div class="card-icon">{{ item.icon || '🎁' }}</div>
        <div class="card-info">
          <h3>{{ item.name }}</h3>
          <p class="desc">{{ item.description || '暂无描述' }}</p>
          <div class="card-footer">
            <span class="price">💰 {{ item.price }} 积分</span>
            <el-button
                size="small"
                type="primary"
                :loading="buyLoading === item.id"
                :disabled="userPoints < item.price"
                @click="handleBuy(item.id)"
            >
              {{ userPoints < item.price ? '积分不足' : '购买' }}
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-if="!loading && itemList.length === 0" description="暂无商品" />
    </div>

    <!-- 购买确认对话框 -->
    <el-dialog v-model="buyDialogVisible" title="确认购买" width="400px">
      <p>确认购买 <strong>{{ buyTarget?.name }}</strong>？</p>
      <p style="color:#909399;font-size:13px;">消耗 <strong>{{ buyTarget?.price }}</strong> 积分</p>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBuy" :loading="buyLoading">确认购买</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShopItems, buyShopItem } from '@/api/shop'
import { getMyPoints } from '@/api/vote'
import { getUserId } from '@/utils/auth'

const router = useRouter()
const loading = ref(false)
const buyLoading = ref<number | null>(null)
const buyDialogVisible = ref(false)
const buyTarget = ref<any>(null)
const userPoints = ref(0)
const itemList = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const [items, points] = await Promise.all([
      getShopItems(),
      getMyPoints()
    ])
    itemList.value = items || []
    userPoints.value = points?.points || 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handleBuy = (itemId: number) => {
  const target = itemList.value.find(i => i.id === itemId)
  if (!target) return
  if (userPoints.value < target.price) {
    ElMessage.warning('积分不足')
    return
  }
  buyTarget.value = target
  buyDialogVisible.value = true
}

const confirmBuy = async () => {
  if (!buyTarget.value) return
  buyLoading.value = buyTarget.value.id
  try {
    await buyShopItem(buyTarget.value.id)
    ElMessage.success(`成功购买「${buyTarget.value.name}」！`)
    buyDialogVisible.value = false
    await loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '购买失败')
  } finally {
    buyLoading.value = null
  }
}

const goToMyItems = () => {
  router.push('/shop/my-items')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.shop-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}
.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.shop-header h2 { margin: 0; }
.points-display {
  font-size: 16px;
  font-weight: 600;
  color: #e6a23c;
  margin-right: 16px;
}
.shop-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 18px;
}
.shop-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  animation: fadeUp 0.5s ease both;
}
.shop-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(0,0,0,0.08);
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.card-icon {
  font-size: 42px;
  width: 60px;
  text-align: center;
  flex-shrink: 0;
}
.card-info {
  flex: 1;
  min-width: 0;
}
.card-info h3 {
  margin: 0 0 4px;
  font-size: 16px;
  color: #303133;
}
.card-info .desc {
  margin: 0 0 12px;
  font-size: 13px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  font-size: 15px;
  font-weight: 600;
  color: #e6a23c;
}
</style>