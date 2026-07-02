<template>
  <div class="home-page">
    <div class="home-layout">
      <div class="home-left">
        <div class="hero-kicker reveal-up">ETHAN FUTURE LAB / PERSONAL BLOG</div>
        <div class="hero-greeting reveal-up">
          <span>Hi, there </span>
          <span class="hero-wave" aria-hidden="true">/</span>
        </div>

        <div class="hero-name reveal-up" style="animation-delay: 120ms">
          <span>I'm </span>
          <span class="hero-name-underline">{{ siteConfig.name }}</span>
          <span> !</span>
        </div>

        <div class="hero-subtitle reveal-up" style="animation-delay: 240ms">
          {{ siteConfig.subtitle }}
        </div>

        <div class="hero-actions reveal-up" style="animation-delay: 300ms">
          <router-link to="/posts" class="hero-action primary">进入文章</router-link>
          <router-link to="/projects" class="hero-action">查看项目</router-link>
        </div>

        <div class="hero-socials reveal-up" style="animation-delay: 360ms">
          <a
            v-for="social in siteConfig.socials"
            :key="social.name"
            :href="social.url"
            target="_blank"
            rel="noopener noreferrer"
            class="social-btn"
            :style="{ '--social-color': social.color }"
            :aria-label="social.name"
          >
            <span v-html="getSocialIcon(social.icon)"></span>
          </a>
        </div>
      </div>

      <div class="home-right reveal-up" style="animation-delay: 180ms">
        <div class="photo-stage">
          <span class="stage-ring ring-one" aria-hidden="true"></span>
          <span class="stage-ring ring-two" aria-hidden="true"></span>
          <span class="stage-line line-one" aria-hidden="true"></span>
          <span class="stage-line line-two" aria-hidden="true"></span>

          <div
            class="photo-wrapper"
            @mouseenter="stopCarousel"
            @mouseleave="startCarousel"
            @click="handlePhotoClick"
          >
            <div ref="photoInnerRef" class="photo-inner">
              <img
                v-for="(photo, i) in siteConfig.photos"
                :key="i"
                :src="photo"
                :alt="`photo ${i + 1}`"
                class="photo-img"
                :class="{ active: i === currentPhoto }"
              />
            </div>
          </div>

          <div class="photo-dots" aria-label="照片切换">
            <button
              v-for="(_, i) in siteConfig.photos"
              :key="i"
              class="photo-dot"
              :class="{ active: i === currentPhoto }"
              :aria-label="`切换到第 ${i + 1} 张照片`"
              @click.stop="selectPhoto(i)"
            ></button>
          </div>
        </div>
      </div>
    </div>

    <router-link to="/posts" class="scroll-cue" aria-label="查看文章">
      <span></span>
      <span></span>
    </router-link>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import siteConfig from '../config/site.config.js'

const siteConfigData = siteConfig
const currentPhoto = ref(0)
const photoInnerRef = ref(null)
let carouselTimer = null
let waveTimer = null

// 触发自然缩放动画
const triggerPopBounce = () => {
  if (!photoInnerRef.value) return
  
  // 核心技巧：移除 class -> 强制浏览器重绘 (void offsetWidth) -> 重新添加 class
  // 这样才能每次都重新播放 CSS 动画
  photoInnerRef.value.classList.remove('pop-bounce')
  void photoInnerRef.value.offsetWidth 
  photoInnerRef.value.classList.add('pop-bounce')
}

// 自动轮播 (5秒)
const startCarousel = () => {
  if (siteConfigData.photos.length <= 1 || carouselTimer) return
  carouselTimer = setInterval(() => {
    currentPhoto.value = (currentPhoto.value + 1) % siteConfigData.photos.length
    triggerPopBounce()
  }, 5000)
}

const stopCarousel = () => {
  if (carouselTimer) {
    clearInterval(carouselTimer)
    carouselTimer = null
  }
}

// 点击切换照片
const handlePhotoClick = () => {
  currentPhoto.value = (currentPhoto.value + 1) % siteConfigData.photos.length
  triggerPopBounce()
}

const selectPhoto = (index) => {
  currentPhoto.value = index
  triggerPopBounce()
  stopCarousel()
  startCarousel()
}

// 社交图标
const getSocialIcon = (icon) => {
  const icons = {
    github: '<svg width="25" height="25" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M511.6 76.3C264.3 76.2 64 276.4 64 523.5 64 718.9 189.3 885 363.8 946c23.5 5.9 19.9-10.8 19.9-22.2v-77.5c-135.7 15.9-141.2-73.9-150.3-88.9C215 726 171.5 718 184.5 703c30.9-15.9 62.4 4 98.9 57.9 26.4 39.1 77.9 32.5 104 26 5.7-23.5 17.9-44.5 34.7-60.8-140.6-25.2-199.2-111-199.2-213 0-49.5 16.3-95 48.3-131.7-20.4-60.5 1.9-112.3 4.9-120 58.1-5.2 118.5 41.6 123.2 45.3 33-8.9 70.7-13.6 112.9-13.6 42.4 0 80.2 4.9 113.5 13.9 11.3-8.6 67.3-48.8 121.3-43.9 2.9 7.7 24.7 58.3 5.5 118 32.4 36.8 48.9 82.7 48.9 132.3 0 102.2-59 188.1-200 212.9 23.5 23.2 38.1 55.4 38.1 91v112.5c0.8 9 0 17.9 15 17.9 177.1-59.7 304.6-227 304.6-424.1 0-247.2-200.4-447.3-447.5-447.3z"/></svg>',
    bilibili: '<svg width="25" height="25" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M306.005333 117.632L444.330667 256h135.296l138.368-138.325333a42.666667 42.666667 0 0 1 60.373333 60.373333L700.330667 256H789.333333A149.333333 149.333333 0 0 1 938.666667 405.333333v341.333334a149.333333 149.333333 0 0 1-149.333334 149.333333h-554.666666A149.333333 149.333333 0 0 1 85.333333 746.666667v-341.333334A149.333333 149.333333 0 0 1 234.666667 256h88.96L245.632 177.962667a42.666667 42.666667 0 0 1 60.373333-60.373334zM789.333333 341.333333h-554.666666a64 64 0 0 0-63.701334 57.856L170.666667 405.333333v341.333334a64 64 0 0 0 57.856 63.701333L234.666667 810.666667h554.666666a64 64 0 0 0 63.701334-57.856L853.333333 746.666667v-341.333334A64 64 0 0 0 789.333333 341.333333zM341.333333 469.333333a42.666667 42.666667 0 0 1 42.666667 42.666667v85.333333a42.666667 42.666667 0 0 1-85.333333 0v-85.333333a42.666667 42.666667 0 0 1 42.666666-42.666667z m341.333334 0a42.666667 42.666667 0 0 1 42.666666 42.666667v85.333333a42.666667 42.666667 0 0 1-85.333333 0v-85.333333a42.666667 42.666667 0 0 1 42.666667-42.666667z"/></svg>',
    zhihu: '<svg width="25" height="25" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M576.8 807.52h57.28l20.8 72.48 100.8-72.48h141.92V229.28H576.8z m67.84-513.92H832v448h-66.24l-85.12 64.96-18.56-64.96h-17.44zM126.4 884.48a149.44 149.44 0 0 0 123.84-10.4c60.96-36 105.92-194.56 105.92-194.56l144 177.44s13.12-84.48-2.24-108.32-99.04-119.84-99.04-119.84l-36.64 32 26.08-104.96H544s0-61.76-30.56-65.28-125.44 0-125.44 0v-192H528s-1.6-64-28.8-64H270.56l35.52-104.64s-57.6 3.36-77.92 39.36-86.4 221.6-86.4 221.6 21.92 10.24 59.2-17.28a147.68 147.68 0 0 0 49.28-75.52l67.84-3.36L320 491.2s-116.96-1.76-140.64 0-37.28 65.28-37.28 65.28H320s-15.2 108.16-60.96 187.2-132.64 140.8-132.64 140.8z"/></svg>',
    weibo: '<svg width="25" height="25" viewBox="0 -102 1138 1024" fill="currentColor"><path d="M914.432 518.144q27.648 21.504 38.912 51.712t9.216 62.976-14.336 65.536-31.744 59.392q-34.816 48.128-78.848 81.92t-91.136 56.32-94.72 35.328-89.6 18.944-75.264 7.68-51.712 1.536-49.152-2.56-68.096-10.24-78.336-21.504-79.872-36.352-74.24-55.296-59.904-78.848q-16.384-29.696-22.016-63.488t-5.632-86.016q0-22.528 7.68-51.2t27.136-63.488 53.248-75.776 86.016-90.112q51.2-48.128 105.984-85.504t117.248-57.856q28.672-10.24 63.488-11.264t57.344 11.264q10.24 11.264 19.456 23.04t12.288 29.184q3.072 14.336 0.512 27.648t-5.632 26.624-5.12 25.6 2.048 22.528q17.408 2.048 33.792-1.536t31.744-9.216 31.232-11.776 33.28-9.216q27.648-5.12 54.784-4.608t49.152 7.68 36.352 22.016 17.408 38.4q2.048 14.336-2.048 26.624t-8.704 23.04-7.168 22.016 1.536 23.552q3.072 7.168 14.848 13.312t27.136 12.288 32.256 13.312 29.184 16.384zM656.384 836.608q26.624-16.384 53.76-45.056t44.032-64 18.944-75.776-20.48-81.408q-19.456-33.792-47.616-57.344t-62.976-37.376-74.24-19.968-80.384-6.144q-78.848 0-139.776 16.384t-105.472 43.008-72.192 60.416-38.912 68.608q-11.264 33.792-6.656 67.072t20.992 62.976 42.496 53.248 57.856 37.888q58.368 25.6 119.296 32.256t116.224 0.512 100.864-21.504 74.24-33.792zM522.24 513.024q20.48 8.192 38.912 18.432t32.768 27.648q10.24 12.288 17.92 30.72t10.752 39.424 1.536 42.496-9.728 38.912q-8.192 18.432-19.968 37.376t-28.672 35.328-40.448 29.184-57.344 18.944q-61.44 11.264-117.76-11.264t-88.064-74.752q-12.288-39.936-13.312-70.656t16.384-66.56q13.312-27.648 40.448-51.712t62.464-38.912 75.264-17.408 78.848 12.8zM359.424 764.928q37.888 3.072 57.856-18.432t21.504-48.128-15.36-47.616-52.736-16.896q-27.648 3.072-43.008 23.552t-17.408 43.52 9.728 42.496 39.424 21.504zM778.24 6.144q74.752 0 139.776 19.968t113.664 57.856 76.288 92.16 27.648 122.88q0 33.792-16.384 50.688t-35.328 17.408-35.328-14.336-16.384-45.568q0-40.96-22.528-77.824t-59.392-64.512-84.48-43.52-96.768-15.872q-31.744 0-47.104-15.36t-14.336-34.304 18.944-34.304 51.712-15.36zM778.24 169.984q95.232 0 144.384 48.64t49.152 146.944q0 30.72-10.24 43.52t-22.528 11.264-22.528-14.848-10.24-35.84q0-60.416-34.816-96.256t-93.184-35.84q-19.456 0-28.672-10.752t-9.216-23.04 9.728-23.04 28.16-10.752z"/></svg>',
    douyin: '<svg width="25" height="25" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M410.91072 427.58144c-158.8224 20.15232-284.44672 222.72-154.112 405.00224 120.40192 98.47808 373.68832 41.20576 380.70272-171.85792l-0.17408-324.1472a280.7296 280.7296 0 0 0 142.88896 38.62528V261.2224a144.98816 144.98816 0 0 1-72.8064-54.82496 135.23968 135.23968 0 0 1-54.70208-72.45824h-123.66848l-0.08192 561.41824c-0.11264 78.46912-130.9696 106.41408-164.18816 30.2592-83.18976-39.77216-64.37888-190.9248 46.31552-192.57344z"/></svg>',
    xiaohongshu: '<svg width="25" height="25" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M726.51776 457.45152c-6.70208-0.0768-13.39392 0-20.00384-0.0768-2.37056 0-3.0464 1.05984-3.0464 3.23072 0.0768 5.10976 0.0768 10.13248 0.0768 15.232v0.01024c0.07168 4.87936 0 9.7536 0.07168 14.56128 0 3.90656 0.68096 4.66944 4.45952 4.66944 7.1424 0.0768 14.27456 0 21.41696 0.0768 2.67776 0 3.72736-1.28 3.65056-3.75808-0.08704-9.1648-0.08704-18.31936-0.15872-27.48416a6.7584 6.7584 0 0 0-6.46656-6.46144z"/><path d="M849.92 51.2h-675.84c-67.8656 0-122.88 55.0144-122.88 122.88v675.84c0 67.8656 55.0144 122.88 122.88 122.88h675.84c67.8656 0 122.88-55.0144 122.88-122.88V174.08c0-67.8656-55.0144-122.88-122.88-122.88zM250.78784 505.73312c-0.73728 10.59328-1.41312 21.25312-2.60608 31.8464-2.08896 18.39104-6.24128 36.26496-14.6432 52.864-2.16064 4.12672-5.13536 7.79776-8.18176 12.45696-1.85344-3.90656-3.41504-6.97856-4.82816-10.13248a3203.59424 3203.59424 0 0 1-14.79168-33.56672c-0.52736-1.2032-0.896-2.92352-0.36864-3.97824 3.19488-6.83008 3.41504-14.12096 3.85536-21.40672 0.60416-9.15968 1.35168-18.24256 2.01728-27.39712 0.51712-7.00416 0.80896-13.9776 1.39776-20.96128 0.67584-8.10496 1.49504-16.21504 2.16064-24.24832 0.14848-1.96608 1.04448-2.56 2.82624-2.56 11.0848 0 22.07744 0 33.16224-0.07168 2.37056 0 3.0464 0.98304 2.89792 3.23072-0.96768 14.63296-1.86368 29.28128-2.89792 43.92448z m71.29088 87.32672c-0.73728 9.46176-5.13536 17.49504-12.5696 23.5008-5.43232 4.352-11.74528 6.15936-18.6624 6.08256-5.87264 0-11.66848-0.0768-17.54112 0-2.00192 0-3.27168-0.60416-4.09088-2.55488-3.41504-7.6544-6.90688-15.32416-10.32192-22.97344-0.52736-1.13152-0.67584-2.33472-1.13152-3.456-1.63328-4.12672-1.5616-4.28544 2.97472-4.36224h13.90592c5.94944 0 8.47872-2.46784 8.5504-8.56576 0.07168-4.57216 0.07168-9.14944 0.07168-13.73696V494.2336c0.14848 0.15872 0.22016 0.15872 0.29696 0.15872V408.63744c0-4.28544 0.14848-4.43392 4.38784-4.43392h29.21472c5.13536 0 5.20704 0.14848 5.20704 5.40672 0 27.1872 0 54.36416 0.0768 81.47968 0.0768 23.87456 0.29696 47.75936 0.29696 71.6288 0 10.14272 0.14848 20.26496-0.6656 30.34112z m75.58656-28.90752c-4.98688 11.56096-10.19904 22.97344-15.31904 34.4576-0.45568 1.13664-1.19296 2.25792-2.3808 4.42368v0.01024c-2.97472-4.5056-6.0928-8.18176-8.11008-12.39552-2.82624-6.13888-4.5312-12.83584-7.35744-18.9952-3.0464-6.6816-4.15744-13.88032-5.57568-20.94592-1.1776-6.02112-1.40288-12.25216-1.8432-18.3296-1.2032-15.39584-2.23744-30.78656-3.44064-46.09536a2449.95584 2449.95584 0 0 0-2.0736-25.1648c-0.14848-1.50016 0.2304-2.176 1.94048-2.176 11.52512 0 22.97344-0.14848 34.49856-0.22016 2.1504 0 3.0464 0.96768 3.11808 2.9952 0.29696 4.65408 0.51712 9.31328 0.88576 11.41248 0.29696 3.83488 0.73728 7.6544 1.04448 11.41248 0.51712 5.40672 1.04448 10.81344 1.41312 16.14336 0.51712 6.90688 0.51712 13.81888 1.4848 20.63872 1.34144 10.4448 0.29696 21.10464 3.93216 31.32928 0.89088 2.40128-0.96768 6.08768-2.21696 8.93952z m84.28032 22.016c-2.89792 6.6816-6.02112 13.21472-8.99072 19.82464-1.64352 3.74784-3.19488 7.49568-4.76672 11.25376-1.85344 4.51072-3.11808 5.40672-7.87456 5.40672h-22.2976c-7.52128 0-15.0272 0.23552-22.53312-0.0768-3.56352-0.14336-7.0656-1.27488-10.62912-2.02752-1.792-0.36864-2.16064-1.42336-1.41312-3.14368a3709.71648 3709.71648 0 0 0 13.45024-29.21472c1.04448-2.24768 1.85344-4.65408 3.0464-6.90688 0.29696-0.6144 1.41312-1.28 2.00192-1.13152 12.42112 3.15392 25.13408 2.77504 37.76512 2.63168a874.6496 874.6496 0 0 1 20.07552 0c3.19488 0.00512 3.50208 0.45568 2.16576 3.38432z m3.84-21.86752a4.48512 4.48512 0 0 1-2.74944 1.4336c-13.89568 0.0768-27.8784 0.14848-41.77408-0.0768-4.23936-0.08704-8.5504-1.05472-11.74528-4.28544-3.3536-3.3792-4.98688-7.36256-3.28192-11.93984a897.52576 897.52576 0 0 1 9.58464-24.10496c3.88096-9.15456 7.81312-18.31936 12.05248-28.2368-2.30912-0.14848-3.712-0.29696-5.04832-0.29696-4.09088-0.07168-8.18176 0.29696-12.27264-0.2304-4.45952-0.51712-8.99072-1.04448-12.48256-4.79232-3.42528-3.6864-3.94752-8.04352-2.60608-12.32384 2.1504-6.83008 4.97664-13.44 7.80288-20.04992 2.67776-6.15424 5.72416-12.16 8.47872-18.24256 2.97472-6.53824 5.86752-13.07136 8.77056-19.6096a1361.99168 1361.99168 0 0 0 7.6544-17.33632c0.73728-1.80736 1.8688-2.47808 3.87072-2.47808 10.93632 0.07168 21.92896 0 32.86528 0 3.6352 0 3.712 0.36864 2.29888 3.6864-6.31296 14.63296-12.71808 29.20448-18.95936 43.84768a11.52 11.52 0 0 0-1.19296 4.87936c0.22016 3.90656 1.04448 4.5056 5.06368 4.5056 8.17152 0.0768 16.35328 0 24.448 0 1.64864 0 3.3536 0.22016 4.98688 0.29696 2.30912 0.0768 2.60608 1.05984 1.63328 3.072a2455.21408 2455.21408 0 0 0-13.3888 29.21472c-3.03616 6.91712-5.93408 13.89568-8.9088 20.8128a1530.1632 1530.1632 0 0 1-6.1696 13.80864c-1.94048 4.20352-0.60416 6.31296 4.15232 6.38976 6.02112 0 12.04224 0.0768 18.05824 0 2.08896 0 3.13344 0.60416 2.08896 2.85184-3.6352 8.25344-7.21408 16.58368-10.84928 24.85248-0.67072 1.50016-1.408 3.072-2.3808 4.352z m134.81472 58.73664h-125.3376c-1.72032-0.22016-3.48672-0.22016-5.94432-0.22016v-0.01536c0.88064-2.61632 1.41312-4.41856 2.1504-6.0672 4.69504-10.29632 9.4464-20.5056 14.0544-30.79168 1.04448-2.33472 2.52928-2.92352 4.75648-2.92352h28.6976c4.54656 0 4.75648-0.2304 4.75648-4.74112V461.66016c0-3.97824-0.0768-4.05504-4.08064-4.05504-6.10304 0-12.26752-0.0768-18.36544 0-2.30912 0-3.27168-0.51712-3.27168-3.1488 0.14848-10.97216 0.0768-21.92896 0.0768-32.88576 0-3.90656 0.0768-3.90656 3.86048-3.90656h73.00096c4.23936 0 8.5504 0.0768 12.78976 0 2.01728 0 2.82624 0.82432 2.74944 2.85184-0.0768 11.41248-0.0768 22.82496-0.0768 34.31424 0 2.02752-0.73728 2.77504-2.82624 2.77504-6.6048-0.0768-13.14304 0.07168-19.77856 0.07168-2.29376 0-3.33824 1.05984-3.33824 3.46624 0.0768 18.39104 0.14336 36.7104 0.14336 55.11168 0 20.87424 0 41.74848 0.0768 62.6944 0 3.75808 0.36864 4.21376 4.17792 4.21376h31.4368c3.41504 0 3.87072 0.36864 3.93728 3.81952 0.08704 10.97216 0 21.92896 0.08704 32.89088-0.01024 2.8672-1.57184 3.16416-3.73248 3.16416z m198.69696-34.92864c-0.14848 16.37376-11.008 29.21472-26.38848 32.89088-4.31616 1.05472-8.78592 1.35168-13.24544 1.5104-6.83008 0.22016-13.7472 0.07168-20.58752 0.07168-4.23936 0-5.42208-0.83456-6.9888-4.66432-3.33824-7.95136-6.83008-15.90784-10.26048-23.87456l-0.66048-1.57184c-1.19296-3.072-0.81408-3.61472 2.45248-3.61472 9.43616-0.07168 18.95424 0.15872 28.3904-0.29184 5.65248-0.29696 8.03328-2.85696 8.18688-8.64256 0.22016-11.04384-0.29696-22.07744-0.14848-33.11104 0.0768-5.48864-6.84032-11.42272-11.74528-11.71968a32.8448 32.8448 0 0 0-2.74944-0.14336c-18.73408 0-37.54496 0-56.2688 0.07168-5.27872 0-5.65248 0.53248-5.65248 5.8624l0.20992 77.55776c0 4.14208-0.0768 4.21376-4.23936 4.21376h-31.22176c-4.01408 0-4.3008-0.3072-4.3008-4.28544v-39.94112c0.06144 0.14336 0.13312 0.14336 0.20992 0.14336v-40.99584c0-2.78016-1.85344-2.93888-3.78368-2.93888-10.19392 0.08704-20.44416 0.31232-30.62272 0.31232-6.92224 0-6.17984 0.8192-6.25664-6.38976-0.0768-9.90208 0-19.90144 0-29.80352 0-3.59936 0.36864-4.05504 3.94752-4.13184 10.7008-0.07168 21.33504 0 32.04096-0.07168 4.09088 0 4.31104-0.15872 4.38272-4.21376 0.0768-9.90208-0.0768-19.8144 0-29.73184 0-2.4832-1.04448-3.23072-3.41504-3.23072-6.84544 0.0768-13.76256-0.07168-20.60288 0-2.1504 0-2.89792-0.74752-2.89792-2.92352 0.09216-11.26912 0.09216-22.46144-0.06144-33.72544 0-2.70336 1.03424-3.29216 3.41504-3.29216 6.31296 0.0768 12.6464 0 18.95936 0 4.23424 0 4.45952-0.3072 4.5312-4.74112 0-2.61632 0.14848-5.24288 0-7.87456-0.07168-2.4832 1.04448-3.15904 3.34336-3.15904 9.07776 0.0768 18.22208 0.0768 27.28448 0.0768h4.97664c3.94752 0 4.0192 0 4.1728 4.05504 0.06656 2.4064-0.1536 4.87936-0.08704 7.28576 0.0768 3.3792 0.9728 4.2752 4.31616 4.36224 5.65248 0.0768 11.30496 0.0768 17.024 0.0768 14.6432 0.07168 27.3664 5.09952 37.0176 16.29184 5.35552 6.22592 8.69888 13.81888 9.216 22.14912 0.52736 8.47872 0.15872 17.03936 0.3072 25.52832 0 3.15904 0.22016 6.38976 0.36864 9.53344 0.14336 3.15904 0.896 3.97824 4.09088 3.90656a48.56832 48.56832 0 0 1 19.03104 3.15904c13.00992 5.03808 21.03296 14.18752 23.63904 28.01152a44.4416 44.4416 0 0 1 0.73728 8.33024c0.08192 17.88928 0.06656 35.78368-0.06656 53.6832zM810.14272 453.632c-5.94432 3.90656-12.1856 3.75808-19.4048 3.6864-2.23744 0-5.20192 0.07168-8.09984-0.0768-0.7424-0.07168-2.00704-0.98304-2.08896-1.5872-0.6656-8.84736-1.77152-17.792 1.35168-26.35264 2.75456-7.5776 9.58464-12.01664 17.61792-12.16a19.99872 19.99872 0 0 1 19.32288 14.336c2.30912 8.2688-1.55648 17.42336-8.69888 22.15424z"/></svg>',
    telegram: '<svg width="25" height="25" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M834.24 127.872a95.168 95.168 0 0 0-29.856 7.136h-0.128c-9.12 3.616-52.48 21.856-118.4 49.504l-236.224 99.488c-169.504 71.36-336.128 141.632-336.128 141.632l1.984-0.768s-11.488 3.776-23.488 12a64.96 64.96 0 0 0-18.752 18.144c-5.888 8.64-10.624 21.856-8.864 35.52 2.88 23.104 17.856 36.96 28.608 44.608 10.88 7.744 21.248 11.36 21.248 11.36h0.256l156.256 52.64c7.008 22.496 47.616 156 57.376 186.752 5.76 18.368 11.36 29.856 18.368 38.624 3.392 4.48 7.36 8.224 12.128 11.232a35.808 35.808 0 0 0 7.872 3.392l-1.6-0.384c0.48 0.128 0.864 0.512 1.216 0.64 1.28 0.352 2.144 0.48 3.776 0.736 24.736 7.488 44.608-7.872 44.608-7.872l1.12-0.896 92.256-84 154.624 118.624 3.52 1.504c32.224 14.144 64.864 6.272 82.112-7.616 17.376-13.984 24.128-31.872 24.128-31.872l1.12-2.88 119.488-612.128c3.392-15.104 4.256-29.248 0.512-42.976a57.824 57.824 0 0 0-24.992-33.504 59.904 59.904 0 0 0-34.144-8.64z m-3.232 65.6c-0.128 2.016 0.256 1.792-0.64 5.664v0.352l-118.368 605.76c-0.512 0.864-1.376 2.752-3.744 4.64-2.496 1.984-4.48 3.232-14.88-0.896l-189.12-144.992-114.24 104.128 24-153.28 308.992-288c12.736-11.84 8.48-14.336 8.48-14.336 0.896-14.528-19.232-4.256-19.232-4.256l-389.632 241.376-0.128-0.64-186.752-62.88v-0.128l-0.48-0.096a8.64 8.64 0 0 0 0.96-0.384l1.024-0.512 0.992-0.352s166.752-70.272 336.256-141.632c84.864-35.744 170.368-71.744 236.128-99.52 65.76-27.616 114.368-47.872 117.12-48.96 2.624-1.024 1.376-1.024 3.264-1.024z"/></svg>',
    email: '<svg width="25" height="25" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M838.954667 234.666667H170.666667c-3.626667 0-7.168 0.448-10.56 1.322666l323.690666 323.669334a21.333333 21.333333 0 0 0 30.165334 0L838.954667 234.666667z m46.144 14.186666l-260.693334 260.693334 262.933334 262.912c5.44-7.168 8.661333-16.106667 8.661333-25.792V277.333333c0-10.944-4.117333-20.906667-10.88-28.48zM843.861333 789.333333l-249.6-249.621333-50.133333 50.133333a64 64 0 0 1-90.517333 0l-50.112-50.133333L156.373333 786.88c4.48 1.578667 9.28 2.453333 14.314667 2.453333h673.194667zM128.661333 754.218667L373.333333 509.525333 129.578667 265.813333A42.709333 42.709333 0 0 0 128 277.333333v469.333334c0 2.56 0.213333 5.098667 0.661333 7.552zM170.666667 192h682.666666a85.333333 85.333333 0 0 1 85.333334 85.333333v469.333334a85.333333 85.333333 0 0 1-85.333334 85.333333H170.666667a85.333333 85.333333 0 0 1-85.333334-85.333333V277.333333a85.333333 85.333333 0 0 1 85.333334-85.333333z"/></svg>',
    twitter: '<svg width="16" height="16" viewBox="0 -102 1024 1024" fill="currentColor"><path d="M919.759462 318.994613c0-9.605475-0.184234-19.102577-0.608696-28.534655 41.060787-30.595551 76.68666-68.894629 104.863684-112.720738a403.247526 403.247526 0 0 1-120.68436 33.017692c43.401647-26.708567 76.68666-69.423851 92.415219-120.68436-40.638132 24.645865-85.589515 42.265535-133.383889 51.446549-38.295465-43.575045-92.944441-71.329413-153.358869-72.360764-116.027926-1.918205-210.084998 96.132418-210.084999 218.973404 0 17.460724 1.826088 34.394031 5.451171 50.745736-174.722834-11.243716-329.55016-102.190479-433.116978-239.293373-18.071226 32.847907-28.438925 71.237296-28.438925 112.377556 0 77.811934 37.090717 146.970271 93.44657 187.779994-34.500598-1.560574-66.819283-12.009553-95.166091-29.141545v2.85744c0 108.752474 72.440238 199.870827 168.560013 221.13003-17.621477 5.012259-36.207475 7.62044-55.322696 7.526516a191.542349 191.542349 0 0 1-39.500214-4.325895c26.708567 89.120675 104.332655 154.231274 196.231295 156.400543-71.923659 59.635948-162.525433 95.258208-260.892145 95.00895-16.960401 0-33.720311-1.137918-50.15149-3.200621 93.011271 63.80109 203.418242 100.960443 322.104923 100.960443 386.350343 0.092117 597.636477-336.191629 597.636477-627.962902z"/></svg>',
    gitlab: '<span class="social-letter">GL</span>',
    gitee: '<span class="social-letter">GE</span>',
    csdn: '<span class="social-letter">CN</span>',
  }
    return icons[icon] || '<span class="social-letter">--</span>'
}

onMounted(() => {
  startCarousel()
  waveTimer = setInterval(() => {
    const hand = document.querySelector('.hero-wave')
    if (hand) {
      hand.classList.remove('wave-pop')
      void hand.offsetWidth
      hand.classList.add('wave-pop')
    }
  }, 2400)
})

onUnmounted(() => {
  stopCarousel()
  if (waveTimer) clearInterval(waveTimer)
})
</script>

<style scoped>
.home-page {
  position: relative;
  min-height: calc(100vh - 78px);
  display: grid;
  place-items: center;
  padding: 24px 0 86px;
  overflow: hidden;
}

.home-layout {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1180px;
  width: 100%;
  margin: 0 auto;
  padding: 34px 32px;
  gap: clamp(32px, 6vw, 88px);
}

.home-left {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  max-width: 560px;
  position: relative;
  z-index: 2;
}

.hero-kicker {
  width: fit-content;
  padding: 7px 12px;
  border: 1px solid rgba(126, 238, 255, 0.24);
  border-radius: 999px;
  color: var(--accent-soft);
  background: rgba(8, 14, 27, 0.58);
  font: 600 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.08em;
  box-shadow: 0 0 24px rgba(56, 248, 255, 0.08);
}

.hero-greeting {
  font-size: clamp(2rem, 4.2vw, 4.6rem);
  display: flex;
  align-items: center;
  gap: 14px;
  color: var(--text);
  font-family: var(--font-serif);
  font-weight: 700;
  line-height: 1.05;
  letter-spacing: 0;
}

.hero-wave {
  font-size: 0.72em;
  cursor: pointer;
  display: inline-block;
  transform-origin: 70% 70%;
  animation: waveHello 2.8s ease-in-out infinite;
  color: var(--accent);
  text-shadow: 0 0 18px rgba(56, 248, 255, 0.4);
}

.hero-name {
  font-size: clamp(3.2rem, 7vw, 6.7rem);
  font-weight: 700;
  color: var(--text);
  line-height: 0.98;
  font-family: var(--font-serif);
  letter-spacing: 0;
}

.hero-name-underline {
  position: relative;
  display: inline-block;
  color: var(--accent);
  text-shadow: 0 12px 30px rgba(56, 248, 255, 0.16);
}

.hero-name-underline::after {
  content: '';
  position: absolute;
  left: -0.08em;
  right: -0.12em;
  bottom: -0.08em;
  height: 0.08em;
  border-radius: 999px;
  background: linear-gradient(90deg, var(--accent), var(--sage), transparent);
  transform: rotate(-1.4deg);
  box-shadow: 0 0 18px rgba(56, 248, 255, 0.22);
}

.hero-subtitle {
  width: fit-content;
  margin-top: 10px;
  padding: 8px 0 9px;
  font-size: clamp(1.2rem, 2.2vw, 1.85rem);
  color: var(--text-light);
  font-family: var(--font-serif);
  letter-spacing: 0.04em;
  border-bottom: 1px solid rgba(126, 238, 255, 0.32);
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 6px;
}

.hero-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 108px;
  height: 40px;
  padding: 0 18px;
  border: 1px solid rgba(126, 238, 255, 0.24);
  border-radius: 8px;
  color: var(--text-light);
  background: rgba(8, 14, 27, 0.62);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.16);
  transition: transform 0.24s var(--ease-out), border-color 0.24s, box-shadow 0.24s, color 0.24s;
}

.hero-action.primary {
  color: #07101c;
  border-color: rgba(56, 248, 255, 0.7);
  background: linear-gradient(135deg, var(--accent), var(--violet));
  box-shadow: 0 16px 36px rgba(56, 248, 255, 0.18);
}

.hero-action:hover {
  transform: translateY(-3px);
  color: var(--accent-soft);
  border-color: var(--accent);
  box-shadow: 0 18px 42px rgba(56, 248, 255, 0.14);
}

.hero-socials {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 18px;
}

.social-btn {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid rgba(126, 238, 255, 0.22);
  background: rgba(8, 14, 27, 0.72);
  color: var(--text-light);
  transition: box-shadow 0.25s var(--ease-out), transform 0.25s var(--ease-out), color 0.2s, border-color 0.2s;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}
.social-btn svg {
  width: 18px;
  height: 18px;
}

.social-btn:hover {
  border-color: color-mix(in srgb, var(--social-color) 46%, transparent);
  box-shadow: 0 16px 28px color-mix(in srgb, var(--social-color) 22%, transparent);
  transform: translateY(-4px) rotate(-2deg);
  color: var(--social-color);
}

.home-right {
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.photo-stage {
  position: relative;
  width: min(42vw, 470px);
  min-width: 340px;
  height: 560px;
  display: grid;
  place-items: center;
  isolation: isolate;
}

.stage-ring {
  position: absolute;
  border: 1px solid rgba(56, 248, 255, 0.26);
  border-radius: 50%;
  z-index: -2;
  box-shadow: inset 0 0 32px rgba(56, 248, 255, 0.08), 0 0 32px rgba(155, 92, 255, 0.12);
}

.ring-one {
  width: 420px;
  height: 420px;
  animation: slowSpin 24s linear infinite;
}

.ring-two {
  width: 320px;
  height: 320px;
  border-color: rgba(155, 92, 255, 0.26);
  animation: slowSpin 30s linear infinite reverse;
}

.stage-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(56, 248, 255, 0.5), transparent);
  z-index: -1;
  box-shadow: 0 0 16px rgba(56, 248, 255, 0.22);
}

.line-one {
  width: 280px;
  height: 1px;
  top: 116px;
  right: -22px;
}

.line-two {
  width: 1px;
  height: 260px;
  right: 42px;
  bottom: 86px;
}

.photo-wrapper {
  width: 380px;
  height: 500px;
  background:
    linear-gradient(180deg, rgba(22, 34, 61, 0.76), rgba(8, 13, 25, 0.74)),
    rgba(8, 14, 27, 0.62);
  border: 1px solid rgba(126, 238, 255, 0.28);
  border-radius: 8px;
  position: relative;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 30px 82px rgba(0, 0, 0, 0.34), 0 0 48px rgba(56, 248, 255, 0.1);
  transform: rotate(2.2deg);
  transition: transform 0.45s var(--ease-out), box-shadow 0.45s var(--ease-out);
  animation: photoFloat 6s ease-in-out infinite;
}

.photo-wrapper::before {
  content: '';
  position: absolute;
  inset: 12px;
  border: 1px solid rgba(56, 248, 255, 0.34);
  border-radius: 6px;
  pointer-events: none;
  z-index: 2;
}

.photo-wrapper::after {
  content: '';
  position: absolute;
  inset: auto 28px 20px;
  height: 18px;
  border-radius: 50%;
  background: rgba(56, 248, 255, 0.16);
  filter: blur(18px);
  z-index: 0;
}

.photo-wrapper:hover {
  transform: rotate(0deg) translateY(-8px) scale(1.015);
  box-shadow: 0 40px 94px rgba(0, 0, 0, 0.4), 0 0 64px rgba(56, 248, 255, 0.18);
}

.photo-inner {
  width: 100%;
  height: 100%;
  background: transparent;
  position: relative;
  overflow: hidden;
  z-index: 1;
  transform-origin: bottom center;
}

.photo-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: bottom center;
  opacity: 0;
  padding: 20px 18px 0;
  filter: drop-shadow(0 20px 26px rgba(56, 248, 255, 0.12));
  transition: opacity 0.42s ease, transform 0.55s var(--ease-out), filter 0.45s;
  transform: translateY(18px) scale(0.97);
}

.photo-img.active {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* 自然柔和的缩放动画 */
@keyframes popBounce {
  0%   { 
    opacity: 0; 
    transform: scale(0.95); /* 起始稍微小一点点，像吸气 */
  }
  100% { 
    opacity: 1; 
    transform: scale(1);    /* 恢复到正常大小 */
  }
}

/* 动画触发类 - 使用平滑的缓动曲线 */
.pop-bounce {
  animation: popBounce 0.45s cubic-bezier(0.22, 1, 0.36, 1);
}

.photo-dots {
  position: absolute;
  left: 50%;
  bottom: 12px;
  z-index: 4;
  display: flex;
  gap: 10px;
  transform: translateX(-50%);
}

.photo-dot {
  width: 8px;
  height: 8px;
  padding: 0;
  border: 0;
  border-radius: 999px;
  background: rgba(126, 238, 255, 0.22);
  cursor: pointer;
  transition: width 0.24s var(--ease-out), background 0.24s;
}

.photo-dot.active {
  width: 28px;
  background: var(--accent);
}

.scroll-cue {
  position: absolute;
  left: 50%;
  bottom: 30px;
  display: grid;
  gap: 7px;
  transform: translateX(-50%);
  color: var(--accent);
}

.scroll-cue span {
  display: block;
  width: 17px;
  height: 17px;
  border-right: 1.5px solid currentColor;
  border-bottom: 1.5px solid currentColor;
  transform: rotate(45deg);
  animation: cuePulse 1.6s ease-in-out infinite;
}

.scroll-cue span:nth-child(2) {
  animation-delay: 0.16s;
}

@keyframes waveHello {
  0%, 70%, 100% { transform: rotate(0deg); }
  76% { transform: rotate(15deg); }
  82% { transform: rotate(-9deg); }
  88% { transform: rotate(10deg); }
  94% { transform: rotate(-4deg); }
}

.wave-pop {
  animation: waveHello 0.8s ease-in-out;
}

@keyframes photoFloat {
  0%, 100% { translate: 0 0; }
  50% { translate: 0 -12px; }
}

@keyframes slowSpin {
  to { transform: rotate(360deg); }
}

@keyframes cuePulse {
  0%, 100% { opacity: 0.32; transform: translateY(0) rotate(45deg); }
  50% { opacity: 1; transform: translateY(7px) rotate(45deg); }
}

@media (max-width: 768px) {
  .home-page {
    padding: 16px 0 78px;
  }

  .home-layout {
    flex-direction: column-reverse;
    text-align: center;
    gap: 22px;
    padding: 18px 20px;
  }

  .home-left {
    align-items: center;
  }

  .hero-socials { justify-content: center; }
  .hero-subtitle { letter-spacing: 0.03em; }

  .photo-stage {
    width: min(88vw, 350px);
    min-width: 0;
    height: 380px;
  }
  
  .photo-wrapper { 
    width: min(78vw, 280px);
    height: 340px;
    transform: rotate(1.4deg);
  }

  .stage-line {
    display: none;
  }

  .ring-one {
    width: 310px;
    height: 310px;
  }

  .ring-two {
    width: 236px;
    height: 236px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-wave,
  .photo-wrapper,
  .stage-ring,
  .scroll-cue span {
    animation: none !important;
  }
}
</style>
