<template>
  <div class="giscus-wrapper" ref="giscusContainer"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
  term: {
    type: String,
    default: ''
  },
  mapping: {
    type: String,
    default: 'specific'
  }
})

const giscusContainer = ref(null)

const loadGiscus = () => {
  if (!giscusContainer.value) return

  const existingScript = giscusContainer.value.querySelector('script')
  if (existingScript) existingScript.remove()
  const existingFrame = giscusContainer.value.querySelector('iframe')
  if (existingFrame) existingFrame.remove()

  const script = document.createElement('script')
  script.src = 'https://giscus.app/client.js'
  script.setAttribute('data-repo', 'EthanZhao02/personal-blog')
  script.setAttribute('data-repo-id', 'R_kgDOTLEN0A')
  script.setAttribute('data-category', 'General')
  script.setAttribute('data-category-id', 'DIC_kwDOTLEN0M4DAaa4')
  script.setAttribute('data-mapping', props.mapping)
  if (props.mapping === 'specific' && props.term) {
    script.setAttribute('data-term', props.term)
  }
  script.setAttribute('data-strict', '0')
  script.setAttribute('data-reactions-enabled', '1')
  script.setAttribute('data-emit-metadata', '0')
  script.setAttribute('data-input-position', 'top')
  script.setAttribute('data-theme', 'dark')
  script.setAttribute('data-lang', 'zh-CN')
  script.setAttribute('data-loading', 'lazy')
  script.async = true
  script.crossOrigin = 'anonymous'

  giscusContainer.value.appendChild(script)
}

onMounted(loadGiscus)
watch(() => props.term, loadGiscus)
</script>

<style scoped>
.giscus-wrapper {
  width: 100%;
  min-height: 300px;
}
</style>
