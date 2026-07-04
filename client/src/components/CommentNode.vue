<template>
  <div class="comment-item" :class="{ 'sub-comment-item': depth > 0 }">
    <div class="comment-avatar" :style="avatarSize">
      <img v-if="comment.userAvatar" :src="comment.userAvatar" :alt="displayName" @error="handleAvatarError" />
      <span v-else class="avatar-letter">{{ getInitial(displayName) }}</span>
    </div>
    <div class="comment-body">
      <div class="comment-meta" :class="{ 'sub-comment-meta': depth > 0 }">
        <a v-if="comment.website" :href="comment.website" target="_blank" rel="noopener" class="comment-author linked" :class="{ 'sub-author': depth > 0 }">{{ displayName }}</a>
        <span v-else class="comment-author" :class="{ 'sub-author': depth > 0 }">{{ displayName }}</span>
        <span class="comment-badge" v-if="comment.isAdmin">博主</span>
        <span class="comment-time" :class="{ 'sub-time': depth > 0 }">{{ formatDate(comment.createTime) }}</span>
        <span v-if="comment.email && isLoggedIn" class="comment-email">{{ comment.email }}</span>
      </div>
      <div class="comment-text" :class="{ 'sub-text': depth > 0 }">{{ comment.content }}</div>
      <div class="comment-actions">
        <button class="action-btn" @click="$emit('reply', comment)">回复</button>
        <button v-if="canDelete" class="action-btn del-btn" @click="$emit('delete', comment.id)">删除</button>
      </div>

      <!-- 递归渲染子评论 -->
      <div class="sub-comments" v-if="comment.children?.length">
        <CommentNode
          v-for="sub in comment.children"
          :key="sub.id"
          :comment="sub"
          :depth="depth + 1"
          @reply="$emit('reply', $event)"
          @delete="$emit('delete', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '../stores/user'

const props = defineProps({
  comment: { type: Object, required: true },
  depth: { type: Number, default: 0 }
})

defineEmits(['reply', 'delete'])

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const displayName = computed(() => props.comment.nickname || props.comment.username || '匿名用户')

const avatarSize = computed(() => {
  if (props.depth === 0) return {}
  const size = Math.max(28, 40 - props.depth * 4)
  return { width: size + 'px', height: size + 'px', fontSize: (size / 2.5) + 'px' }
})

const canDelete = computed(() => {
  if (!isLoggedIn.value) return false
  return userStore.isAdmin
})

const getInitial = (name) => {
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}

const handleAvatarError = (e) => {
  e.target.style.display = 'none'
}

const formatDate = (s) => {
  if (!s) return ''
  const d = new Date(s + '+08:00')
  if (isNaN(d.getTime())) return s
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}
</script>

<style scoped>
.comment-item {
  display: flex;
  gap: 14px;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.56);
  animation: fadeUp 0.35s both;
}

.sub-comment-item {
  padding: 12px 0;
  border: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 0;
  background: transparent;
  animation: none;
}

.sub-comment-item:last-child {
  border-bottom: none;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-letter {
  font-size: 14px;
  font-weight: 700;
  color: var(--accent);
  font-family: 'Noto Serif SC', serif;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.sub-comment-meta {
  margin-bottom: 4px;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.sub-author {
  font-size: 13px;
}

.comment-author.linked {
  color: var(--accent-dark);
  text-decoration: none;
}

.comment-author.linked:hover {
  text-decoration: underline;
}

.comment-badge {
  font-size: 11px;
  padding: 1px 8px;
  background: rgba(96, 165, 250, 0.1);
  color: var(--accent);
  border-radius: 10px;
  font-weight: 600;
}

.comment-time {
  font-size: 12px;
  color: var(--text-lighter);
}

.sub-time {
  font-size: 11px;
}

.comment-email {
  font-size: 11px;
  color: var(--text-lighter);
}

.comment-text {
  font-size: 14px;
  color: var(--text);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.sub-text {
  font-size: 13px;
  line-height: 1.6;
}

.comment-actions {
  margin-top: 6px;
}

.action-btn {
  font-size: 12px;
  color: #c0c0c0;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin-right: 10px;
  transition: color 0.2s;
}

.action-btn:hover { color: var(--accent); }
.action-btn.del-btn:hover { color: #f56c6c; }

.sub-comments {
  margin-top: 12px;
  margin-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 0;
}
</style>
