<template>
  <el-dialog
      v-model="visible"
      title="🎡 幸运大转盘"
      width="520px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="handleClose"
  >
    <div class="wheel-container">
      <!-- 转盘 -->
      <div class="wheel-wrapper">
        <canvas ref="wheelCanvas" width="400" height="400"></canvas>
        <div class="pointer" @click="startDraw">🎯</div>
        <div v-if="isSpinning" class="spinning-mask">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>抽奖中...</span>
        </div>
      </div>

      <!-- 中奖结果 -->
      <div v-if="result" class="result-box">
        <el-alert
            :title="result.message"
            :type="result.points > 0 ? 'success' : 'info'"
            :closable="false"
            show-icon
        >
          <template v-if="result.points > 0">
            <span>🎉 恭喜获得 <strong>{{ result.points }}</strong> 积分！</span>
          </template>
          <template v-else>
            <span>{{ result.message || '😅 谢谢参与，下次好运！' }}</span>
          </template>
        </el-alert>
      </div>

      <!-- 底部按钮 -->
      <div class="wheel-actions">
        <el-button
            v-if="!result && !isSpinning"
            type="primary"
            @click="startDraw"
        >
          🎰 开始抽奖
        </el-button>
        <el-button v-if="result" type="success" @click="handleClose">
          太好了！
        </el-button>
        <el-button v-if="result" type="info" plain @click="skipDraw">
          下次再玩
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { wheelDraw } from '@/api/vote'

const props = defineProps<{
  modelValue: boolean
  voteId: number
}>()

const emit = defineEmits(['update:modelValue', 'success', 'skip', 'close'])

const visible = ref(false)
const isSpinning = ref(false)
const result = ref<{ points: number; message: string } | null>(null)

const wheelCanvas = ref<HTMLCanvasElement | null>(null)
let currentAngle = 0

// 奖品配置
const prizes = [
  { label: '10积分', points: 10, color: '#FF6B6B' },
  { label: '5积分', points: 5, color: '#4ECDC4' },
  { label: '谢谢参与', points: 0, color: '#45B7D1' },
  { label: '20积分', points: 20, color: '#FFA07A' },
  { label: '2积分', points: 2, color: '#98D8C8' },
  { label: '50积分', points: 50, color: '#F7DC6F' },
  { label: '谢谢参与', points: 0, color: '#BB8FCE' },
  { label: '8积分', points: 8, color: '#85C1E9' }
]

const SEGMENT = (2 * Math.PI) / prizes.length

// 绘制转盘
const drawWheel = (rotation = 0) => {
  const canvas = wheelCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const centerX = canvas.width / 2
  const centerY = canvas.height / 2
  const radius = Math.min(centerX, centerY) - 10

  ctx.clearRect(0, 0, canvas.width, canvas.height)

  prizes.forEach((prize, index) => {
    const startAngle = index * SEGMENT + rotation
    const endAngle = startAngle + SEGMENT

    ctx.beginPath()
    ctx.moveTo(centerX, centerY)
    ctx.arc(centerX, centerY, radius, startAngle, endAngle)
    ctx.closePath()
    ctx.fillStyle = prize.color
    ctx.fill()
    ctx.strokeStyle = '#fff'
    ctx.lineWidth = 2
    ctx.stroke()

    ctx.save()
    ctx.translate(centerX, centerY)
    ctx.rotate(startAngle + SEGMENT / 2)
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillStyle = '#fff'
    ctx.font = 'bold 14px Arial'
    ctx.shadowColor = 'rgba(0,0,0,0.3)'
    ctx.shadowBlur = 4

    const textRadius = radius * 0.7
    const label = prize.label.length > 6 ? prize.label.slice(0, 6) : prize.label
    ctx.fillText(label, textRadius, 0)
    ctx.restore()
  })

  // 中心装饰
  ctx.beginPath()
  ctx.arc(centerX, centerY, 30, 0, 2 * Math.PI)
  ctx.fillStyle = '#FFD700'
  ctx.fill()
  ctx.strokeStyle = '#DAA520'
  ctx.lineWidth = 3
  ctx.stroke()

  ctx.fillStyle = '#8B4513'
  ctx.font = 'bold 16px Arial'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText('GO', centerX, centerY)
}

// 开始抽奖
const startDraw = async () => {
  if (isSpinning.value) return

  isSpinning.value = true
  result.value = null

  try {
    const data: any = await wheelDraw(voteId)
    console.log('🎰 抽奖结果:', data)

    // 检查是否已抽奖
    if (data.alreadyDrawn) {
      ElMessage.info('您已抽过奖')
      result.value = {
        points: 0,
        message: '😅 您已抽过奖，不能再抽了'
      }
      isSpinning.value = false
      return
    }

    // 获取积分
    const points = data.points || 0
    const prizeName = data.prizeName || '谢谢参与'

    // 找到对应的奖品索引（用于转盘动画）
    let targetIndex = prizes.findIndex(p => p.points === points)
    if (targetIndex < 0) targetIndex = 0

    const targetAngle = targetIndex * SEGMENT + SEGMENT / 2
    const spins = 5 + Math.random() * 3
    const finalAngle = targetAngle + spins * 2 * Math.PI

    // 动画
    const duration = 3000
    const startTime = performance.now()
    const startAngle = currentAngle

    await new Promise<void>((resolve) => {
      const animate = (time: number) => {
        const progress = Math.min((time - startTime) / duration, 1)
        const ease = 1 - Math.pow(1 - progress, 3)
        const currentAngleNew = startAngle + (finalAngle - startAngle) * ease

        drawWheel(currentAngleNew)

        if (progress < 1) {
          requestAnimationFrame(animate)
        } else {
          currentAngle = currentAngleNew
          isSpinning.value = false

          const prize = prizes[targetIndex] || { points: 0, label: '谢谢参与' }
          const finalPoints = points || 0

          result.value = {
            points: finalPoints,
            message: finalPoints > 0 ? `🎉 恭喜获得 ${finalPoints} 积分！` : '😅 谢谢参与，下次好运！'
          }

          if (finalPoints > 0) {
            emit('success', { points: finalPoints, prize: prizeName })
            ElMessage.success(`🎉 获得 ${finalPoints} 积分！`)
          } else {
            ElMessage.info('谢谢参与，下次好运！')
          }

          resolve()
        }
      }

      requestAnimationFrame(animate)
    })
  } catch (error: any) {
    console.error('抽奖失败:', error)
    ElMessage.error(error.message || '抽奖失败')
    isSpinning.value = false
  }
}

const skipDraw = () => {
  emit('skip')
  visible.value = false
  emit('update:modelValue', false)
  emit('close')
}

const handleClose = () => {
  visible.value = false
  emit('update:modelValue', false)
  if (result.value) {
    emit('close')
  }
}

watch(
    () => props.modelValue,
    (val) => {
      visible.value = val
      if (val) {
        nextTick(() => {
          drawWheel(currentAngle)
          result.value = null
          isSpinning.value = false
        })
      }
    },
    { immediate: true }
)

onMounted(() => {
  drawWheel(currentAngle)
})
</script>

<style scoped>
.wheel-container {
  text-align: center;
  padding: 10px 0;
}

.wheel-wrapper {
  position: relative;
  display: inline-block;
}

.wheel-wrapper canvas {
  display: block;
  margin: 0 auto;
  border-radius: 50%;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
  max-width: 100%;
  height: auto;
}

.pointer {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 40px;
  cursor: pointer;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
  transition: transform 0.2s;
  z-index: 10;
}

.pointer:hover {
  transform: translateX(-50%) scale(1.1);
}

.pointer:active {
  transform: translateX(-50%) scale(0.9);
}

.spinning-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
}

.spinning-mask .el-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.result-box {
  margin-top: 16px;
  padding: 0 10px;
}

.wheel-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}
</style>