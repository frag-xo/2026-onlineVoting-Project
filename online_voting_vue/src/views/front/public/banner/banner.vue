<template>
  <div class="carousel-container">
    <!-- 主轮播区域 -->
    <div
        class="carousel-viewport"
        ref="viewportRef"
        @mouseenter="pauseAutoPlay"
        @mouseleave="resumeAutoPlay"
        @touchstart="handleTouchStart"
        @touchmove="handleTouchMove"
        @touchend="handleTouchEnd"
    >
      <div
          class="carousel-track"
          :style="{
          transform: `translateX(-${currentTranslateX}px)`,
          transition: isTransitioning ? `transform ${transitionDuration}ms cubic-bezier(0.25, 0.46, 0.45, 0.94)` : 'none'
        }"
          @transitionend="onTransitionEnd"
      >
        <div
            v-for="(item, idx) in extendedSlides"
            :key="`carousel-item-${idx}`"
            class="carousel-slide"
        >
          <div class="slide-card" :class="{ 'active': isActiveSlide(idx) }">
            <div class="card-media">
              <img :src="getUploadUrl(item.bimg)" :alt="item.btitle" />
              <div class="card-overlay"></div>
            </div>
            <div class="card-content">
              <h3>{{ item.btitle }}</h3>
              <p>{{ item.bdes }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 左箭头 -->
    <button
        class="carousel-arrow prev"
        @click="prev"
        :disabled="isTransitioning"
    >
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path fill="currentColor" d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
      </svg>
    </button>

    <!-- 右箭头 -->
    <button
        class="carousel-arrow next"
        @click="next"
        :disabled="isTransitioning"
    >
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path fill="currentColor" d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/>
      </svg>
    </button>

    <!-- 指示器 -->
    <div class="carousel-indicators">
      <button
          v-for="(_, idx) in banners"
          :key="idx"
          class="indicator-dot"
          :class="{ active: idx === currentRealIndex }"
          @click="goToSlide(idx)"
          :disabled="isTransitioning"
      >
        <span class="dot-inner"></span>
      </button>
    </div>

    <!-- 装饰光晕效果 -->
    <div class="glow-effect"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'

import {bannersTop4} from "@/api/banner.ts";

// 定义轮播数据（酷炫示例内容）
const banners = ref([

])
const getUploadUrl=(imgsrc)=>{
  return      "http://localhost:5173/target/upload/banner/"+imgsrc;
}
const   get_bannersTop4 = (pageInfo) =>  {
  bannersTop4(pageInfo).then((dto) => {
    console.log(dto)
    banners.value = dto.tList
  });
};


// 配置参数
const autoPlayInterval = 4000      // 自动播放间隔(ms)
const transitionDuration = 500      // 过渡动画时长(ms)
const swipeThreshold = 50           // 滑动切换阈值(px)

// 扩展数据（首尾克隆实现无限循环）
const extendedSlides = computed(() => {
  if (!banners.value.length) return []
  const last = banners.value[banners.value.length - 1]
  const first = banners.value[0]
  return [last, ...banners.value, first]
})

// 真实索引（原始数据中的索引，0-based）
const currentRealIndex = ref(0)
// 克隆序列中的索引（0 ~ extendedSlides.length-1）
const currentCloneIndex = ref(1)
// 当前translateX偏移量(px)
const currentTranslateX = ref(0)
// 是否正在过渡动画中
const isTransitioning = ref(false)
// 容器宽度
const viewportWidth = ref(0)
// 定时器id
let autoTimer = null
// 触摸相关
let touchStartX = 0
let touchMoveX = 0
let isDragging = false

// DOM 引用
const viewportRef = ref(null)

// 计算单个slide的宽度（等于视口宽度）
const slideWidth = computed(() => viewportWidth.value)

// 监听当前clone索引变化，更新translateX
watch([currentCloneIndex, slideWidth], ([newIndex, width]) => {
  if (width > 0) {
    currentTranslateX.value = newIndex * width
  }
}, { immediate: true })

// 监听真实索引变化，更新指示器样式（额外的动画触发）
watch(currentRealIndex, (newVal) => {
  // 可选：添加额外的音效或视觉反馈，这里只是确保UI同步
})

// 重置位置（用于无限循环边界修正）
const resetToRealIndex = () => {
  // 检查是否在克隆边界
  if (currentCloneIndex.value === 0) {
    // 在第一个克隆项（实际是最后一个真实项），应跳转到真实最后一项的clone索引位置
    const newCloneIndex = banners.value.length
    currentCloneIndex.value = newCloneIndex
    // 更新真实索引
    currentRealIndex.value = banners.value.length - 1
    // 无过渡更新偏移量
    updateTranslateWithoutTransition()
  }
  else if (currentCloneIndex.value === extendedSlides.value.length - 1) {
    // 在最后一个克隆项（实际是第一个真实项），应跳转到真实第一项的clone索引位置
    const newCloneIndex = 1
    currentCloneIndex.value = newCloneIndex
    currentRealIndex.value = 0
    updateTranslateWithoutTransition()
  }
}

// 无过渡更新偏移量
const updateTranslateWithoutTransition = () => {
  isTransitioning.value = false
  if (slideWidth.value > 0) {
    currentTranslateX.value = currentCloneIndex.value * slideWidth.value
  }
}

// 过渡结束处理
const onTransitionEnd = () => {
  if (!isTransitioning.value) return
  isTransitioning.value = false
  // 修正边界（实现无限循环）
  resetToRealIndex()
}

// 切换到指定真实索引（带动画）
const goToSlide = (index, withAnimation = true) => {
  if (isTransitioning.value) return
  if (index < 0 || index >= banners.value.length) return

  const targetRealIndex = index
  // 计算目标克隆索引
  let targetCloneIndex = targetRealIndex + 1

  // 如果目标索引与当前真实索引相同，不需要切换
  if (targetRealIndex === currentRealIndex.value && !withAnimation) return

  // 执行切换
  if (withAnimation) {
    isTransitioning.value = true
  }
  currentRealIndex.value = targetRealIndex
  currentCloneIndex.value = targetCloneIndex
}

// 下一张
const next = () => {
  if (isTransitioning.value) return
  let nextRealIndex = currentRealIndex.value + 1
  if (nextRealIndex >= banners.value.length) {
    nextRealIndex = 0
  }
  goToSlide(nextRealIndex, true)
}

// 上一张
const prev = () => {
  if (isTransitioning.value) return
  let prevRealIndex = currentRealIndex.value - 1
  if (prevRealIndex < 0) {
    prevRealIndex = banners.value.length - 1
  }
  goToSlide(prevRealIndex, true)
}

// 判断当前slide是否为激活状态（用于样式增强）
const isActiveSlide = (cloneIdx) => {
  // 实际展示的卡片为克隆索引对应的真实索引判断
  const activeCloneIndex = currentRealIndex.value + 1
  return cloneIdx === activeCloneIndex
}

// 自动播放控制
const startAutoPlay = () => {
  if (autoTimer) clearInterval(autoTimer)
  autoTimer = setInterval(() => {
    if (!isTransitioning.value) {
      next()
    }
  }, autoPlayInterval)
}

const pauseAutoPlay = () => {
  if (autoTimer) {
    clearInterval(autoTimer)
    autoTimer = null
  }
}

const resumeAutoPlay = () => {
  if (autoTimer) return
  startAutoPlay()
}

// 更新视口宽度
const updateViewportWidth = () => {
  if (viewportRef.value) {
    viewportWidth.value = viewportRef.value.clientWidth
  }
}

// 触摸滑动处理
const handleTouchStart = (e) => {
  if (isTransitioning.value) return
  pauseAutoPlay()
  const touch = e.touches[0]
  touchStartX = touch.clientX
  touchMoveX = 0
  isDragging = true
  // 临时禁用过渡以便拖动时跟随手指
  isTransitioning.value = false
}

const handleTouchMove = (e) => {
  if (!isDragging) return
  const touch = e.touches[0]
  const deltaX = touch.clientX - touchStartX
  touchMoveX = deltaX
  // 实时更新位置（限制最大偏移避免过度拉扯）
  let newTranslateX = currentCloneIndex.value * slideWidth.value - deltaX
  // 边界阻尼效果
  const minTranslate = 0
  const maxTranslate = (extendedSlides.value.length - 1) * slideWidth.value
  if (newTranslateX < minTranslate) {
    newTranslateX = minTranslate + (newTranslateX - minTranslate) * 0.3
  }
  if (newTranslateX > maxTranslate) {
    newTranslateX = maxTranslate + (newTranslateX - maxTranslate) * 0.3
  }
  currentTranslateX.value = newTranslateX
  e.preventDefault()
}

const handleTouchEnd = () => {
  if (!isDragging) {
    resumeAutoPlay()
    return
  }
  isDragging = false
  // 判断滑动距离是否超过阈值
  const deltaX = touchMoveX
  if (Math.abs(deltaX) > swipeThreshold) {
    if (deltaX > 0) {
      // 向右滑动，显示上一张
      prev()
    } else {
      // 向左滑动，显示下一张
      next()
    }
  } else {
    // 未超过阈值，回弹到当前卡片
    isTransitioning.value = true
    currentTranslateX.value = currentCloneIndex.value * slideWidth.value
    // 过渡结束后重置标志
    const onFinish = () => {
      isTransitioning.value = false
      viewportRef.value?.removeEventListener('transitionend', onFinish)
    }
    viewportRef.value?.addEventListener('transitionend', onFinish, { once: true })
    // 确保超时恢复
    setTimeout(() => {
      if (isTransitioning.value) {
        isTransitioning.value = false
      }
    }, transitionDuration + 50)
  }
  resumeAutoPlay()
  touchStartX = 0
  touchMoveX = 0
}

// 监听窗口大小变化
const handleResize = () => {
  updateViewportWidth()
  nextTick(() => {
    updateTranslateWithoutTransition()
  })
}

// 生命周期
onMounted(() => {
  get_bannersTop4()
  updateViewportWidth()
  startAutoPlay()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  pauseAutoPlay()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.carousel-container {
  position: relative;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 25px 45px -12px rgba(0, 0, 0, 0.5);
  background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
}

.carousel-viewport {
  position: relative;
  width: 100%;
  overflow: hidden;
  cursor: grab;
  aspect-ratio: 16 / 9;
}

.carousel-viewport:active {
  cursor: grabbing;
}

.carousel-track {
  display: flex;
  height: 100%;
  will-change: transform;
}

.carousel-slide {
  flex: 0 0 100%;
  height: 100%;
  position: relative;
}

.slide-card {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 0;
  overflow: hidden;
  transition: transform 0.4s cubic-bezier(0.2, 0.9, 0.4, 1.1), filter 0.3s ease;
  background: rgba(10, 10, 20, 0.6);
  backdrop-filter: blur(2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.slide-card.active {
  transform: scale(1.02);
  filter: drop-shadow(0 0 12px rgba(0, 255, 255, 0.6));
  transition: all 0.3s ease;
}

.card-media {
  position: relative;
  width: 100%;
  height: 100%;
}

.card-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.6s ease;
}

.slide-card:hover .card-media img {
  transform: scale(1.05);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(0deg, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.2) 50%, transparent 100%);
  pointer-events: none;
}

.card-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1.5rem 2rem;
  color: white;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  transform: translateY(20px);
  opacity: 0;
  animation: slideUpFade 0.6s ease forwards;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  backdrop-filter: blur(4px);
}

@keyframes slideUpFade {
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.card-content h3 {
  font-size: 1.8rem;
  margin: 0 0 0.5rem;
  font-weight: 700;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #fff, #aaccff);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.card-content p {
  font-size: 1rem;
  margin: 0;
  opacity: 0.9;
  font-weight: 400;
}

/* 箭头按钮 */
.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(20, 20, 40, 0.7);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  color: white;
}

.carousel-arrow:hover:not(:disabled) {
  background: rgba(0, 255, 255, 0.8);
  box-shadow: 0 0 15px cyan;
  transform: translateY(-50%) scale(1.1);
  border-color: white;
}

.carousel-arrow:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.prev {
  left: 20px;
}

.next {
  right: 20px;
}

/* 指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 12px;
  z-index: 10;
}

.indicator-dot {
  background: transparent;
  border: none;
  padding: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.dot-inner {
  display: block;
  width: 10px;
  height: 10px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.indicator-dot.active .dot-inner {
  width: 28px;
  background: cyan;
  box-shadow: 0 0 8px cyan;
}

.indicator-dot:hover .dot-inner {
  background: white;
  transform: scale(1.2);
}

/* 装饰光晕 */
.glow-effect {
  position: absolute;
  top: -20%;
  left: -20%;
  width: 140%;
  height: 140%;
  background: radial-gradient(circle at 30% 40%, rgba(0, 255, 255, 0.15), transparent 70%);
  pointer-events: none;
  z-index: 0;
  animation: rotateGlow 12s linear infinite;
}

@keyframes rotateGlow {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .card-content h3 {
    font-size: 1.2rem;
  }
  .card-content p {
    font-size: 0.8rem;
  }
  .carousel-arrow {
    width: 36px;
    height: 36px;
  }
  .indicator-dot {
    padding: 4px;
  }
  .dot-inner {
    width: 6px;
    height: 6px;
  }
  .indicator-dot.active .dot-inner {
    width: 20px;
  }
}
</style>