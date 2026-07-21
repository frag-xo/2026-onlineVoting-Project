<template>
  <div class="hot-horticultirists">
    <!-- 头部标题 -->
    <div class="section-header">
      <h2 class="title">
        <span class="icon-hot">🔥</span> 园艺师推荐
      </h2>
      <div class="subtitle">今日爆款 · 限时特惠</div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载热门园艺师中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button @click="fetchProducts" class="retry-btn">重新加载</button>
    </div>

    <!-- 商品列表 -->
    <div v-else class="horticultirist-grid">
      <div
          v-for="horticultirist in horticultirists"
          :key="horticultirist.hid"
          class="horticultirist-card"
          @click="goToDetail(horticultirist.hid)"
      >
        <div class="card-media">
          <img :src="getUploadUrl(horticultirist.himg)" :alt="horticultirist.hname" loading="lazy" />
          <!-- 热卖标签 -->
          <div v-if="horticultirist.hotBadge" class="hot-badge">
            {{ horticultirist.h_hotBadge }}
          </div>
          <!-- 折扣标签 -->
          <div v-if="horticultirist.discount" class="discount-tag">
            -{{ horticultirist.discount }}%
          </div>
        </div>
        <div class="card-info">
          <h3 class="horticultirist-name">{{ horticultirist.hname }}</h3>
          <div class="price-row">

          </div>
          <div class="meta-row">
            <span class="sales">月销 {{ horticultirist.countItem.itemVal}}+</span>
            <span class="rating" v-if="horticultirist.itemVal2">
              ⭐ {{ horticultirist.itemVal2 }}
            </span>
          </div>
          <button class="buy-btn">立即咨询</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { horticultiristsWithCountItem_page } from "@/api/horticultirist" //数据


const   get_horticultiristsWithCountItem_page = (pageInfo) =>  {
  horticultiristsWithCountItem_page(pageInfo).then((dto) => {
    horticultirists.value = dto.obj.records
    pageInfo = dto.obj
    data.pageInfo = pageInfo
  });
};
const getUploadUrl=(pimg)=>{
  return      "http://localhost:5173/target/upload/horticultirist/"+pimg;
}
// 商品数据类型定义 (TS 风格，但实际 JS 环境也适用)
const horticultirists = ref([])
const loading = ref(true)
const error = ref('')

// 模拟 API 请求函数
const fetchProducts = async () => {
  loading.value = true
  error.value = ''
  try {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 800))

    // 模拟返回的热门商品数据
    // const mockData = [
    //   {
    //     id: 1,
    //     name: '极简运动跑鞋',
    //     price: 199,
    //     oldPrice: 399,
    //     sales: 2350,
    //     rating: 4.9,
    //     image: 'https://picsum.photos/id/0/300/300',
    //     hotBadge: '爆款',
    //     discount: 50
    //   },
    //   {
    //     id: 2,
    //     name: '无线降噪耳机',
    //     price: 459,
    //     oldPrice: 899,
    //     sales: 1872,
    //     rating: 4.8,
    //     image: 'https://picsum.photos/id/1/300/300',
    //     hotBadge: '新品',
    //     discount: 48
    //   },
    //   {
    //     id: 3,
    //     name: '便携快充充电宝',
    //     price: 89,
    //     oldPrice: 149,
    //     sales: 5430,
    //     rating: 4.7,
    //     image: 'https://picsum.photos/id/20/300/300',
    //     hotBadge: '爆款',
    //     discount: 40
    //   },
    //   {
    //     id: 4,
    //     name: '智能运动手环',
    //     price: 169,
    //     oldPrice: 299,
    //     sales: 3218,
    //     rating: 4.8,
    //     image: 'https://picsum.photos/id/22/300/300',
    //     hotBadge: '限时抢',
    //     discount: 43
    //   },
    //   {
    //     id: 5,
    //     name: '简约双肩包',
    //     price: 129,
    //     oldPrice: 259,
    //     sales: 976,
    //     rating: 4.6,
    //     image: 'https://picsum.photos/id/26/300/300',
    //     hotBadge: '热门',
    //     discount: 50
    //   },
    //   {
    //     id: 6,
    //     name: '4K运动相机',
    //     price: 899,
    //     oldPrice: 1699,
    //     sales: 432,
    //     rating: 4.9,
    //     image: 'https://picsum.photos/id/29/300/300',
    //     hotBadge: '爆款',
    //     discount: 47
    //   }
    // ]

    // horticultirists.value = mockData
    get_horticultiristsWithCountItem_page({"current": 1,"size":12})
  } catch (err) {
    error.value = '加载失败，请稍后重试'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 商品详情跳转 (示例)
const goToDetail = (id) => {
  console.log(`跳转到商品详情页，ID: ${id}`)
  // 实际项目中可使用 router.push(`/horticultirist/${id}`)
}

// 页面加载时获取数据
onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.hot-horticultirists {
  max-width: 1280px;
  margin: 0 auto;
  padding: 40px 24px;
  background: linear-gradient(145deg, #f8faff 0%, #f0f2f8 100%);
  border-radius: 32px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

/* 头部样式 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 2.2rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #e65c00, #f9d423);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.icon-hot {
  font-size: 1.8rem;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

.subtitle {
  color: #666;
  font-size: 1rem;
  letter-spacing: 1px;
}

/* 网格布局 */
.horticultirist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 28px;
}

/* 商品卡片 */
.horticultirist-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  cursor: pointer;
  position: relative;
}

.horticultirist-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.12);
}

.card-media {
  position: relative;
  padding-top: 100%; /* 1:1 比例 */
  overflow: hidden;
  background: #f5f5f5;
}

.card-media img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.horticultirist-card:hover .card-media img {
  transform: scale(1.05);
}

/* 标签 */
.hot-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(120deg, #ff416c, #ff4b2b);
  color: white;
  font-size: 0.75rem;
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 30px;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.discount-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  color: #ffd966;
  font-size: 0.75rem;
  font-weight: bold;
  padding: 4px 10px;
  border-radius: 30px;
  z-index: 2;
  border: 1px solid rgba(255,215,0,0.5);
}

/* 卡片信息 */
.card-info {
  padding: 16px;
}

.horticultirist-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #1a1a2e;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 8px;
}

.current-price {
  font-size: 1.5rem;
  font-weight: 800;
  color: #e63946;
}

.current-price::before {
  content: '¥';
  font-size: 1rem;
  font-weight: 500;
  margin-right: 2px;
}

.old-price {
  font-size: 0.85rem;
  color: #888;
  text-decoration: line-through;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 16px;
}

.sales, .rating {
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 20px;
}

.buy-btn {
  width: 100%;
  background: linear-gradient(90deg, #1e1e2f, #2a2a40);
  border: none;
  padding: 10px 0;
  border-radius: 40px;
  color: white;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: 1px;
}

.buy-btn:hover {
  background: linear-gradient(90deg, #e63946, #ff6b6b);
  transform: scale(0.98);
  box-shadow: 0 4px 12px rgba(230,57,70,0.3);
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
}

.spinner {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  border: 4px solid rgba(0,0,0,0.1);
  border-top-color: #e63946;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 40px;
  color: #e63946;
}

.retry-btn {
  background: #1e1e2f;
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: 30px;
  margin-top: 16px;
  cursor: pointer;
  transition: 0.2s;
}

.retry-btn:hover {
  background: #e63946;
}

/* 响应式 */
@media (max-width: 768px) {
  .hot-horticultirists {
    padding: 24px 16px;
  }
  .title {
    font-size: 1.6rem;
  }
  .horticultirist-grid {
    gap: 16px;
  }
  .current-price {
    font-size: 1.2rem;
  }
}
</style>