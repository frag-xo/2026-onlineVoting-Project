<template>
  <div class="personal-center">
    <!-- 头像与个人信息卡片 -->
    <div class="profile-card">
      <div class="avatar-wrapper">
        <img :src="client.cimg" alt="头像" class="avatar" @click="triggerUpload" />
        <div class="avatar-edit-icon" @click="triggerUpload">
          <i class="fas fa-camera"></i>
        </div>
        <input
            type="file"
            ref="fileInput"
            accept="image/*"
            style="display: none"
            @change="handleAvatarChange"
        />
      </div>
      <div class="info-details">
        <div class="info-row">
          <span class="info-label">昵称</span>
          <div class="info-value">
            <span class="nickname">{{ client.cname }}</span>
            <button class="edit-btn" @click="editNickname"><i class="fas fa-pen">修改昵称</i></button>
          </div>
        </div>
        <div class="info-row">
          <span class="info-label">手机</span>
          <div class="info-value">
            <span>{{ client.cphone }}</span>
            <button class="edit-btn" @click="editPhone"><i class="fas fa-pen">修改手机号</i></button>
          </div>
        </div>
        <div class="info-row">
          <span class="info-label">邮箱</span>
          <div class="info-value">
            <span>{{ client.cemail }}</span>
            <button class="edit-btn" @click="editEmail"><i class="fas fa-pen">修改邮箱</i></button>
            
          </div>
        </div>
      </div>
    </div>

    <!-- 我的订单 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-title"><i class="fas fa-truck-fast"></i> 我的订单</span>
        <span class="section-link" @click="viewAllOrders">查看全部 <i class="fas fa-chevron-right"></i></span>
      </div>
      <div class="order-list">
        <div v-for="order in orderrecords" :key="order.goodsLists[0].oid" class="order-item" @click="viewOrderDetail(order)">
          <div class="order-info">
            <div class="order-no">{{ order.goodsLists[0].oid }}</div>
            <div class="order-amount">¥{{ order.oprices }}</div>
          </div>
          <div class="order-status">{{ order.shop.sname }}</div>
          <el-button class="editBtn" type="success" @click="showEdit(order)">查询</el-button>
        </div>
        <div v-if="orders.length === 0" class="empty-state">
          <i class="fas fa-receipt"></i>
          <p>暂无订单，去逛逛吧~</p>
        </div>
      </div>
    </div>

    <!-- 我的收藏 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-title"><i class="fas fa-heart"></i> 我的收藏</span>
        <span class="section-link" @click="viewAllFavorites">更多 <i class="fas fa-chevron-right"></i></span>
      </div>
      <div v-if="favorites.length > 0" class="grid-list">
        <div v-for="item in shoppingcar" :key="item.scid" class="favorite-item">
          <div>
            123
          </div>
          <img :src="getUploadUrl(item.plant.pimg)" class="item-img" :alt="item.plant.pname" />
          <div class="item-info">
            <div class="item-title">{{ item.plant.pname }}</div>
            <div class="item-price">¥{{ item.plant.pprice }}</div>
            <button class="action-btn remove-fav" @click.stop="removeFromFav(item.plant.cid)">
              <i class="fas fa-trash-can"></i> 取消收藏
            </button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <i class="far fa-heart"></i>
        <p>暂无收藏，去猜你喜欢添加吧~</p>
      </div>
    </div>

    <!-- 猜你喜欢 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-title"><i class="fas fa-magic"></i> 猜你喜欢</span>
        <span class="section-link" style="color:#94a3b8;">为你推荐</span>
      </div>
      <div v-if="guessLikes.length > 0" class="grid-list">
        <div v-for="item in guessLikes" :key="item.id" class="guess-item">
          <img :src="item.image" class="item-img" :alt="item.title" />
          <div class="item-info">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-price">¥{{ item.price }}</div>
            <button class="action-btn" @click="addToFavorites(item)">
              <i class="fas fa-heart"></i> 收藏
            </button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <i class="fas fa-store"></i>
        <p>暂无推荐商品~</p>
      </div>
    </div>

    <!-- Toast 提示 -->
    <transition name="fade">
      <div v-if="toastVisible" class="toast-msg">{{ toastMessage }}</div>
    </transition>

    <myorderrecord   v-model:visible="flagEditVisiable" :order="currOrder"/>
  </div>
</template>

<script>
// 按照用户示例引入api（可根据实际路径调整）
// import api from "../../../axios/api.js"

import {ref, reactive, onMounted} from 'vue'
import { orderRecordsWithShopAndOrderdescsByCId_page } from "@/api/orderrecord.ts" //数据
import myorderrecord from '@/views/front/client/myorderrecord.vue'

import { client_id } from "@/api/client.ts"
import { shopcarWithPlantAndShop_id } from "@/api/shopcar.ts"

import editHorticultirist from "@/views/back/horticultirist/editHorticultirist.vue";



export default {
  components:{editHorticultirist, myorderrecord},

  name: 'PersonalCenter',
  setup() {
    const client = ref({
      cid: '',
      cname: '',
      cimg: '',
      cphone: '',
      cemail: '',
      caddress: '',
    });
    const flagEditVisiable = ref(false)
    const orderrecords = ref([]);
    const shoppingcar = ref([]);

    const   get_client_id = (id) =>  {
      client_id(id).then((dto) => {

        client.value = dto.t

        console.log(client)
        console.log('__________')

      });
    };

    const   get_shopcarWithPlantAndShop_id = (id) =>  {
      shopcarWithPlantAndShop_id(id).then((dto) => {

        shoppingcar.value = dto.tList


      });
    };
    const currOrder = ref()
    const showEdit = (order) => {
      flagEditVisiable.value = true
      console.log("_____oooo_______")
      console.log(order)
      currOrder.value = order
      console.log(currOrder)

    };


    // private String cid;
    // private String cname;
    // private String cphone;
    // private String cemail;
    // private String caddress;
    const   get_orderRecordsWithShopAndOrderdescsByCId_page = (pageInfo) =>  {
      orderRecordsWithShopAndOrderdescsByCId_page(pageInfo).then((dto) => {
        orderrecords.value = dto.obj.records
        console.log(orderrecords)
        console.log('____zz______')
        pageInfo = dto.obj
        pageInfo = pageInfo
      });
    };
    const getUploadUrl=(pimg)=>{
      return      "http://localhost:5173/target/upload/plant/"+pimg;
    }
    // ---------- 用户信息 ----------
    // const client = reactive({
    //   avatar: 'https://picsum.photos/id/64/200/200',   // 默认头像
    //   nickname: '美食探险家',
    //   phone: '138****5678',
    //   email: 'foodie@example.com'
    // })

    // 头像上传相关
    const fileInput = ref(null)
    const triggerUpload = () => {
      fileInput.value.click()
    }
    const handleAvatarChange = (event) => {
      const file = event.target.files[0]
      if (file && (file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg')) {
        const reader = new FileReader()
        reader.onload = (e) => {
          client.cimg = e.target.result
          showToast('头像更新成功 ✨')
          // 如需上传服务器，可调用API：
          // const formData = new FormData()
          // formData.append('avatar', file)
          // api.uploadAvatar(formData).then(...)
        }
        reader.readAsDataURL(file)
      } else {
        showToast('请选择图片格式 (jpg/png)')
      }
      event.target.value = ''
    }

    // 编辑个人信息（简易弹窗）
    const editNickname = () => {
      let newName = prompt('请输入新昵称', client.cname)
      if (newName && newName.trim()) {
        client.cname = newName.trim()
        showToast('昵称已更新')
        // api.updateUserInfo({ nickname: client.nickname })
      } else if (newName !== null && newName.trim() === '') {
        showToast('昵称不能为空')
      }
    }
    const editPhone = () => {
      let newPhone = prompt('请输入手机号', client.cphone)
      if (newPhone && newPhone.trim()) {
        client.cphone = newPhone.trim()
        showToast('手机号已更新')
      } else if (newPhone !== null && newPhone.trim() === '') {
        showToast('手机号不能为空')
      }
    }
    const editEmail = () => {
      let newEmail = prompt('请输入邮箱地址', client.cemail)
      if (newEmail && newEmail.trim() && newEmail.includes('@')) {
        client.cemail = newEmail.trim()
        showToast('邮箱已更新')
      } else if (newEmail !== null && newEmail.trim() !== '' && !newEmail.includes('@')) {
        showToast('请输入有效的邮箱地址')
      } else if (newEmail !== null && newEmail.trim() === '') {
        showToast('邮箱不能为空')
      }
    }

    // ---------- 订单数据（模拟） ----------
    const orders = ref([
      { id: 1, orderNo: 'ORD100239482', amount: '129.00', status: '已签收', goods: '智能台灯 x1' },
      { id: 2, orderNo: 'ORD100239483', amount: '59.90', status: '待发货', goods: '无线鼠标 x1' },
      { id: 3, orderNo: 'ORD100239484', amount: '299.00', status: '待付款', goods: '运动手环 x1' }
    ])

    const viewAllOrders = () => {
      showToast('跳转全部订单页面 (演示)')
      // 实际调用: router.push('/orders')
    }
    const viewOrderDetail = (order) => {
      showToast(`订单 ${order.orderNo} : ${order.status}`)
      // router.push(`/order/${order.id}`)
    }

    // ---------- 收藏数据 ----------
    const favorites = ref([
      { id: 101, title: '降噪无线耳机', price: '199', image: 'https://picsum.photos/id/1/200/200' },
      { id: 102, title: '便携快充充电宝', price: '89', image: 'https://picsum.photos/id/20/200/200' },
      { id: 103, title: '简约双肩包', price: '159', image: 'https://picsum.photos/id/26/200/200' }
    ])

    const removeFromFav = (id) => {
      const index = favorites.value.findIndex(item => item.id === id)
      if (index !== -1) {
        favorites.value.splice(index, 1)
        showToast('已取消收藏')
        // api.deleteFavorite(id)
      }
    }

    const viewAllFavorites = () => {
      showToast(`共 ${favorites.value.length} 件收藏商品`)
    }

    // ---------- 猜你喜欢数据 ----------
    const guessLikes = ref([
      { id: 201, title: '复古休闲运动鞋', price: '239', image: 'https://picsum.photos/id/0/200/200' },
      { id: 202, title: '极简双肩包', price: '179', image: 'https://picsum.photos/id/96/200/200' },
      { id: 203, title: '智能运动手环8', price: '269', image: 'https://picsum.photos/id/91/200/200' },
      { id: 204, title: '磁吸充电宝', price: '129', image: 'https://picsum.photos/id/77/200/200' }
    ])

    const addToFavorites = (item) => {
      const exists = favorites.value.some(fav => fav.id === item.id)
      if (exists) {
        showToast('商品已经在收藏夹里啦 ❤️')
        return
      }
      const newFav = {
        id: item.id,
        title: item.title,
        price: item.price,
        image: item.image
      }
      favorites.value.unshift(newFav)
      showToast(`✨ "${item.title}" 已添加到收藏`)
      // api.addFavorite(item.id)
    }

    // ---------- Toast ----------
    const toastVisible = ref(false)
    const toastMessage = ref('')
    let toastTimer = null
    const showToast = (msg, duration = 1800) => {
      if (toastTimer) clearTimeout(toastTimer)
      toastMessage.value = msg
      toastVisible.value = true
      toastTimer = setTimeout(() => {
        toastVisible.value = false
      }, duration)
    }

    onMounted(() => {
      console.log('__________'),
      get_orderRecordsWithShopAndOrderdescsByCId_page({"current": 1,"size":12,"cid":"C0000001"}),
          console.log('_____zyx____'),
          get_client_id("C0000001"),
          get_shopcarWithPlantAndShop_id("C0000001"),

          console.log(client)


    })

    return {
      getUploadUrl,
      shoppingcar,
      get_shopcarWithPlantAndShop_id,
      currOrder,
      showEdit,
      flagEditVisiable,
      orderrecords,
      client,
      fileInput,
      triggerUpload,
      handleAvatarChange,
      editNickname,
      editPhone,
      editEmail,
      orders,
      viewAllOrders,
      viewOrderDetail,
      favorites,
      removeFromFav,
      viewAllFavorites,
      guessLikes,
      addToFavorites,
      toastVisible,
      toastMessage
    }
  }

}
</script>

<style scoped>
/* 通过 @import 引入 Font Awesome，避免使用 link 标签 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.personal-center {
  max-width: 1360px;
  margin: 0 auto;
  padding: 24px 32px 48px;
  background-color: #f5f7fb;
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', sans-serif;
  color: #1e293b;
}

/* 头像卡片 - PC端优化 */
.profile-card {
  background: white;
  border-radius: 28px;
  padding: 28px 32px;
  margin-bottom: 28px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  gap: 32px;
  flex-wrap: wrap;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: #eef2ff;
  cursor: pointer;
  transition: transform 0.2s;
}

.avatar:hover {
  transform: scale(1.02);
}

.avatar-edit-icon {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background: #3b82f6;
  color: white;
  border-radius: 30px;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  border: 2px solid white;
  cursor: pointer;
}

.info-details {
  flex: 1;
}

.info-row {
  display: flex;
  gap: 16px;
  margin-bottom: 14px;
  align-items: baseline;
}

.info-label {
  font-size: 15px;
  color: #64748b;
  width: 56px;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  font-weight: 500;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.edit-btn {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
  font-size: 14px;
}

.edit-btn:hover {
  color: #3b82f6;
}

.nickname {
  font-weight: 700;
  font-size: 20px;
}

/* 通用卡片 */
.section-card {
  background: white;
  border-radius: 24px;
  padding: 24px 28px;
  margin-bottom: 28px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  transition: box-shadow 0.2s;
}

.section-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 20px;
  border-left: 4px solid #3b82f6;
  padding-left: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
}

.section-title i {
  margin-right: 10px;
}

.section-link {
  font-size: 14px;
  color: #3b82f6;
  cursor: pointer;
  transition: opacity 0.2s;
}

.section-link:hover {
  opacity: 0.8;
}

/* 订单列表 - PC端保持纵向但优化间距 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background: #f8fafc;
  border-radius: 20px;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: background 0.2s;
}

.order-item:hover {
  background: #f1f5f9;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-no {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.order-amount {
  font-size: 18px;
  font-weight: 700;
}

.order-status {
  font-size: 13px;
  background: #e6f0ff;
  padding: 5px 12px;
  border-radius: 30px;
  color: #2563eb;
  font-weight: 500;
}

.order-more {
  color: #3b82f6;
  font-size: 16px;
}

/* 收藏 & 猜你喜欢 网格布局 - PC端适配多列 */
.grid-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

/* 平板及以上显示3列 */
@media (min-width: 768px) {
  .grid-list {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
}

/* 桌面端（≥1024px）显示4列，充分利用1360px宽度 */
@media (min-width: 1024px) {
  .grid-list {
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
  }
}

.favorite-item, .guess-item {
  background: #f9f9fc;
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #edf2f7;
  transition: transform 0.2s, box-shadow 0.2s;
}

.favorite-item:hover, .guess-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.item-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
}

.item-info {
  padding: 14px 12px 16px;
}

.item-title {
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 8px;
}

.item-price {
  font-size: 18px;
  font-weight: 700;
  color: #e11d48;
  margin-bottom: 12px;
}

.action-btn {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 40px;
  padding: 8px 0;
  width: 100%;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.action-btn:hover {
  background-color: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.remove-fav {
  background: #fff1f0;
  border-color: #ffe4e2;
  color: #e11d48;
}

.remove-fav:hover {
  background: #e11d48;
  color: white;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 48px 20px;
  color: #94a3b8;
  background: #fafcff;
  border-radius: 24px;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

/* Toast 提示 */
.toast-msg {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 10px 24px;
  border-radius: 40px;
  font-size: 14px;
  z-index: 999;
  backdrop-filter: blur(4px);
  pointer-events: none;
  white-space: nowrap;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 移动端覆盖（宽度≤768px时恢复紧凑样式） */
@media (max-width: 767px) {
  .personal-center {
    padding: 20px 16px 40px;
  }

  .profile-card {
    padding: 20px;
    gap: 18px;
  }

  .avatar {
    width: 80px;
    height: 80px;
  }

  .avatar-edit-icon {
    width: 26px;
    height: 26px;
    font-size: 12px;
  }

  .nickname {
    font-size: 18px;
  }

  .section-card {
    padding: 18px 16px;
  }

  .section-title {
    font-size: 18px;
  }

  .grid-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 14px;
  }

  .item-info {
    padding: 10px 8px 12px;
  }

  .item-title {
    font-size: 14px;
  }

  .item-price {
    font-size: 16px;
  }

  .action-btn {
    padding: 6px 0;
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .avatar {
    width: 70px;
    height: 70px;
  }

  .grid-list {
    gap: 12px;
  }
}
</style>