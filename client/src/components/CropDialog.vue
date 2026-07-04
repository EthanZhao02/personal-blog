<template>
  <div class="crop-overlay" @click.self="$emit('cancel')">
    <div class="crop-panel">
      <h3 class="crop-title">裁剪图片</h3>
      <p class="crop-hint">拖拽选区调整范围，确认后上传</p>
      
      <div class="crop-stage" ref="stageRef">
        <img :src="imageUrl" ref="imgRef" class="crop-image" @load="onImageLoad" />
        
        <!-- 暗色遮罩四块 -->
        <div class="crop-mask mask-top" :style="maskTopStyle" />
        <div class="crop-mask mask-bottom" :style="maskBottomStyle" />
        <div class="crop-mask mask-left" :style="maskLeftStyle" />
        <div class="crop-mask mask-right" :style="maskRightStyle" />
        
        <!-- 裁剪框 -->
        <div
          class="crop-box"
          :style="boxStyle"
          @mousedown="startDrag"
          @touchstart.prevent="startTouchDrag"
        >
          <div class="crop-grid"></div>
          <!-- 四个角手柄 -->
          <span class="crop-handle handle-tl" @mousedown.stop="startResize('tl', $event)" />
          <span class="crop-handle handle-tr" @mousedown.stop="startResize('tr', $event)" />
          <span class="crop-handle handle-bl" @mousedown.stop="startResize('bl', $event)" />
          <span class="crop-handle handle-br" @mousedown.stop="startResize('br', $event)" />
        </div>
      </div>

      <div class="crop-actions">
        <button class="crop-btn cancel" @click="$emit('cancel')">取消</button>
        <button class="crop-btn confirm" @click="doCrop">确认裁剪</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  imageFile: { type: File, required: true },
  aspectRatio: { type: Number, default: 16 / 9 }, // 16:9 for cover, 1:1 for icon/avatar
  outputFormat: { type: String, default: 'image/jpeg' },
  outputQuality: { type: Number, default: 0.85 }
})

const emit = defineEmits(['crop', 'cancel'])

const imgRef = ref(null)
const stageRef = ref(null)
const imageUrl = ref('')
const imageLoaded = ref(false)
const naturalWidth = ref(0)
const naturalHeight = ref(0)

// 裁剪区域 (在显示尺寸上的坐标)
const cropX = ref(0)
const cropY = ref(0)
const cropW = ref(200)
const cropH = ref(112)

// 拖拽状态
const dragging = ref(false)
const resizing = ref('')
let dragStartX = 0, dragStartY = 0, dragStartCropX = 0, dragStartCropY = 0, dragStartCropW = 0, dragStartCropH = 0

onMounted(() => {
  imageUrl.value = URL.createObjectURL(props.imageFile)
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
  document.addEventListener('touchmove', onTouchMove, { passive: false })
  document.addEventListener('touchend', onTouchUp)
})

onUnmounted(() => {
  URL.revokeObjectURL(imageUrl.value)
  document.removeEventListener('mousemove', onMouseMove)
  document.removeEventListener('mouseup', onMouseUp)
  document.removeEventListener('touchmove', onTouchMove)
  document.removeEventListener('touchend', onTouchUp)
})

const onImageLoad = () => {
  const img = imgRef.value
  naturalWidth.value = img.naturalWidth
  naturalHeight.value = img.naturalHeight
  
  // 计算显示尺寸 (最大 500px 宽, 300px 高)
  const maxW = 480, maxH = 280
  let dw = img.naturalWidth, dh = img.naturalHeight
  if (dw > maxW || dh > maxH) {
    const scale = Math.min(maxW / dw, maxH / dh)
    dw *= scale; dh *= scale
  }
  
  // 初始裁剪区域 - 居中，保持长宽比
  const targetRatio = props.aspectRatio
  let cw, ch
  if (dw / dh > targetRatio) {
    ch = dh * 0.7
    cw = ch * targetRatio
  } else {
    cw = dw * 0.7
    ch = cw / targetRatio
  }
  
  cropW.value = Math.round(cw)
  cropH.value = Math.round(ch)
  cropX.value = Math.round((dw - cw) / 2)
  cropY.value = Math.round((dh - ch) / 2)
  
  imageLoaded.value = true
}

// 遮罩样式
const maskTopStyle = computed(() => ({ height: cropY.value + 'px', left: 0, right: 0, top: 0 }))
const maskBottomStyle = computed(() => ({ top: (cropY.value + cropH.value) + 'px', left: 0, right: 0, bottom: 0 }))
const maskLeftStyle = computed(() => ({ top: cropY.value + 'px', height: cropH.value + 'px', left: 0, width: cropX.value + 'px' }))
const maskRightStyle = computed(() => ({ top: cropY.value + 'px', height: cropH.value + 'px', left: (cropX.value + cropW.value) + 'px', right: 0 }))

const boxStyle = computed(() => ({
  left: cropX.value + 'px',
  top: cropY.value + 'px',
  width: cropW.value + 'px',
  height: cropH.value + 'px'
}))

const startDrag = (e) => {
  if (resizing.value) return
  dragging.value = true
  dragStartX = e.clientX; dragStartY = e.clientY
  dragStartCropX = cropX.value; dragStartCropY = cropY.value
}

const startResize = (corner, e) => {
  resizing.value = corner
  dragStartX = e.clientX; dragStartY = e.clientY
  dragStartCropX = cropX.value; dragStartCropY = cropY.value
  dragStartCropW = cropW.value; dragStartCropH = cropH.value
}

const startTouchDrag = (e) => {
  if (resizing.value) return
  dragging.value = true
  dragStartX = e.touches[0].clientX; dragStartY = e.touches[0].clientY
  dragStartCropX = cropX.value; dragStartCropY = cropY.value
}

const clamp = (v, min, max) => Math.max(min, Math.min(max, v))

const onMouseMove = (e) => {
  if (!dragging.value && !resizing.value) return
  const dx = e.clientX - dragStartX, dy = e.clientY - dragStartY
  const ratio = props.aspectRatio
  const imgW = imgRef.value?.clientWidth || 500
  const imgH = imgRef.value?.clientHeight || 300
  
  if (dragging.value) {
    cropX.value = clamp(dragStartCropX + dx, 0, imgW - cropW.value)
    cropY.value = clamp(dragStartCropY + dy, 0, imgH - cropH.value)
  } else if (resizing.value) {
    let nw = dragStartCropW, nh = dragStartCropH, nx = dragStartCropX, ny = dragStartCropY
    const minW = 60, minH = minW / ratio
    
    switch (resizing.value) {
      case 'br': nw = clamp(dragStartCropW + dx, minW, imgW - dragStartCropX); nh = nw / ratio; break
      case 'bl': nw = clamp(dragStartCropW - dx, minW, dragStartCropX + dragStartCropW); nx = dragStartCropX + dragStartCropW - nw; nh = nw / ratio; break
      case 'tr': nw = clamp(dragStartCropW + dx, minW, imgW - dragStartCropX); nh = nw / ratio; ny = dragStartCropY + dragStartCropH - nh; break
      case 'tl': nw = clamp(dragStartCropW - dx, minW, dragStartCropX + dragStartCropW); nx = dragStartCropX + dragStartCropW - nw; nh = nw / ratio; ny = dragStartCropY + dragStartCropH - nh; break
    }
    
    cropW.value = Math.round(nw); cropH.value = Math.round(nh)
    cropX.value = clamp(Math.round(nx), 0, imgW); cropY.value = clamp(Math.round(ny), 0, imgH)
  }
}

const onMouseUp = () => { dragging.value = false; resizing.value = '' }
const onTouchMove = (e) => {
  e.preventDefault()
  if (!dragging.value && !resizing.value) return
  onMouseMove({ clientX: e.touches[0].clientX, clientY: e.touches[0].clientY })
}
const onTouchUp = () => { dragging.value = false; resizing.value = '' }

const doCrop = () => {
  const img = imgRef.value
  if (!img) return
  
  const displayW = img.clientWidth
  const displayH = img.clientHeight
  const scaleX = naturalWidth.value / displayW
  const scaleY = naturalHeight.value / displayH
  
  const sx = cropX.value * scaleX
  const sy = cropY.value * scaleY
  const sw = cropW.value * scaleX
  const sh = cropH.value * scaleX
  
  const canvas = document.createElement('canvas')
  canvas.width = Math.round(sw)
  canvas.height = Math.round(sh)
  const ctx = canvas.getContext('2d')
  ctx.drawImage(img, sx, sy, sw, sh, 0, 0, canvas.width, canvas.height)
  
  canvas.toBlob((blob) => {
    if (blob) {
      const ext = props.outputFormat === 'image/png' ? 'png' : 'jpg'
      const file = new File([blob], `cropped.${ext}`, { type: props.outputFormat })
      emit('crop', file)
    }
  }, props.outputFormat, props.outputQuality)
}
</script>

<style scoped>
.crop-overlay {
  position: fixed; inset: 0; z-index: 2000;
  background: rgba(0,0,0,0.85);
  display: flex; align-items: center; justify-content: center;
  backdrop-filter: blur(4px);
}
.crop-panel {
  background: rgba(12,20,35,0.98);
  border: 1px solid rgba(56,248,255,0.2);
  border-radius: 16px;
  padding: 20px 24px;
  max-width: 560px;
  width: 95%;
}
.crop-title { font-size: 16px; color: #fff; margin: 0 0 4px; }
.crop-hint { font-size: 12px; color: rgba(255,255,255,0.4); margin: 0 0 16px; }
.crop-stage {
  position: relative;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  display: inline-block;
  max-width: 100%;
  cursor: crosshair;
  user-select: none;
}
.crop-image { display: block; max-width: 480px; max-height: 280px; pointer-events: none; }
.crop-mask { position: absolute; background: rgba(0,0,0,0.55); pointer-events: none; }
.crop-box {
  position: absolute;
  outline: 2px solid #38f8ff;
  outline-offset: -2px;
  cursor: move;
}
.crop-grid::before,
.crop-grid::after {
  content: ''; position: absolute;
  background: rgba(56,248,255,0.3);
}
.crop-grid::before {
  left: 33.33%; top: 0; width: 1px; height: 100%;
  box-shadow: 1px 0 0 0 rgba(56,248,255,0.3);
}
.crop-grid::after {
  top: 50%; left: 0; height: 1px; width: 100%;
}
.crop-handle {
  position: absolute;
  width: 12px; height: 12px;
  border: 2px solid #38f8ff;
  background: rgba(12,20,35,0.9);
  z-index: 2;
}
.handle-tl { top: -6px; left: -6px; cursor: nw-resize; }
.handle-tr { top: -6px; right: -6px; cursor: ne-resize; }
.handle-bl { bottom: -6px; left: -6px; cursor: sw-resize; }
.handle-br { bottom: -6px; right: -6px; cursor: se-resize; }
.crop-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 16px; }
.crop-btn {
  padding: 8px 20px; border-radius: 6px;
  font-size: 13px; font-weight: 600; cursor: pointer;
  transition: all 0.2s;
}
.crop-btn.cancel {
  background: none; border: 1px solid rgba(255,255,255,0.2);
  color: #888;
}
.crop-btn.confirm {
  background: #38f8ff; border: none;
  color: #0c1423;
}
.crop-btn.confirm:hover {
  background: #5cfaff;
  transform: translateY(-1px);
}
</style>
