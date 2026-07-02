<template>
  <div class="article-page" v-if="article">
    <div class="article-inner">

      <!-- 左侧：正文 -->
      <main class="article-main">

        <!-- 面包屑 -->
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span> / </span>
          <router-link to="/posts">文章</router-link>
          <span> / </span>
          <span>{{ article.title }}</span>
        </div>

        <!-- 封面图片 -->
        <div class="article-cover" v-if="article.coverImage || article.cover">
          <img :src="getCoverUrl(article.coverImage || article.cover)" :alt="article.title" />
        </div>

        <!-- 文章头 -->
        <header class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span class="meta-item">BY {{ article.authorName }}</span>
            <span class="meta-sep">·</span>
            <span class="meta-item">{{ formatDate(article.createTime) }}</span>
            <span class="meta-sep">·</span>
            <span class="meta-item">{{ article.viewCount || 0 }} 阅读</span>
            <span class="meta-sep" v-if="article.categoryName">·</span>
            <router-link
              :to="`/category/${encodeURIComponent(article.categoryName)}`"
              class="meta-cat"
              v-if="article.categoryName"
            >{{ article.categoryName }}</router-link>
          </div>
          <div class="article-tags" v-if="article.tags?.length">
            <router-link
              v-for="tag in article.tags"
              :key="tag.id"
              :to="`/tag/${encodeURIComponent(tag.name)}`"
              class="tag-chip"
            ># {{ tag.name }}</router-link>
          </div>
        </header>

        <!-- 正文 -->
        <div class="article-body md-content" v-html="renderedContent"></div>

        <!-- 附件（修复下载） -->
        <div class="article-attachments" v-if="attachments.length > 0">
          <p class="attach-title">附件</p>
          <div class="attach-list">
            <a
              v-for="(att, i) in attachments"
              :key="i"
              :href="getAttachmentUrl(att.url)"
              target="_blank"
              download
              class="attach-item"
              @click.prevent="downloadFile(att)"
            >
              <span class="att-icon">DOC</span>
              <span class="att-name">{{ att.name || att.fileName }}</span>
              <span class="att-size" v-if="att.size">({{ formatFileSize(att.size) }})</span>
            </a>
          </div>
        </div>

        <!-- 分割线 -->
        <hr class="divider" />

        <!-- 评论区 -->
        <section class="comments-section">
          <h3 class="comments-title">评论 <span class="comments-count">({{ totalComments }})</span></h3>

          <!-- 发表评论 -->
          <div class="comment-form">
            <h4 class="form-title">发布评论</h4>
            <div class="form-row">
              <input v-model="commentNickname" type="text" placeholder="昵称 *" class="form-input" maxlength="50" />
              <input v-model="commentEmail" type="email" placeholder="邮箱 *" class="form-input" maxlength="100" />
            </div>
            <input v-model="commentWebsite" type="url" placeholder="网站地址（选填）" class="form-input full-row" maxlength="200" />
            <textarea v-model="commentContent" placeholder="说点什么...（需填写昵称和邮箱）" class="comment-textarea" rows="5"></textarea>
            <div class="reply-notice" v-if="replyingTo">
              回复 @{{ replyingTo }}
              <button class="cancel-reply" @click="cancelReply">取消</button>
            </div>
            <div class="form-footer">
              <button class="btn-submit" @click="submitComment" :disabled="!canSubmit || submitting">
                {{ submitting ? '发送中...' : '发布评论' }}
              </button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comment-list" v-if="comments.length > 0">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-avatar">
                <img v-if="comment.userAvatar" :src="comment.userAvatar" :alt="comment.nickname || comment.username" @error="handleAvatarError" />
                <span v-else class="avatar-letter">{{ getInitial(comment.nickname || comment.username) }}</span>
              </div>
              <div class="comment-body">
                <div class="comment-meta">
                  <a v-if="comment.website" :href="comment.website" target="_blank" rel="noopener" class="comment-author linked">{{ comment.nickname || comment.username || '匿名用户' }}</a>
                  <span v-else class="comment-author">{{ comment.nickname || comment.username || '匿名用户' }}</span>
                  <span class="comment-badge" v-if="comment.isAdmin">博主</span>
                  <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                  <span v-if="comment.email && isLoggedIn" class="comment-email">{{ comment.email }}</span>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
                <div class="comment-actions">
                  <button class="action-btn" @click="replyTo(comment)">回复</button>
                  <button v-if="canDeleteComment(comment)" class="action-btn del-btn" @click="deleteComment(comment.id)">删除</button>
                </div>
                
                <!-- 子评论 -->
                <div class="sub-comments" v-if="comment.children?.length">
                  <div
                    v-for="sub in comment.children"
                    :key="sub.id"
                    class="sub-comment-item"
                  >
                    <div class="sub-comment-avatar">
                      <img v-if="sub.userAvatar" :src="sub.userAvatar" :alt="sub.nickname || sub.username" @error="handleAvatarError" />
                      <span v-else class="avatar-letter">{{ getInitial(sub.nickname || sub.username) }}</span>
                    </div>
                    <div class="sub-comment-body">
                      <div class="sub-comment-meta">
                        <a v-if="sub.website" :href="sub.website" target="_blank" rel="noopener" class="sub-author linked">{{ sub.nickname || sub.username || '匿名用户' }}</a>
                        <span v-else class="sub-author">{{ sub.nickname || sub.username || '匿名用户' }}</span>
                        <span class="comment-badge" v-if="sub.isAdmin">博主</span>
                        <span class="sub-time">{{ formatDate(sub.createTime) }}</span>
                        <span v-if="sub.email && isLoggedIn" class="comment-email">{{ sub.email }}</span>
                      </div>
                      <div class="sub-text">{{ sub.content }}</div>
                      <div class="comment-actions">
                        <button class="action-btn" @click="replyTo(sub)">回复</button>
                        <button v-if="canDeleteComment(sub)" class="action-btn del-btn" @click="deleteComment(sub.id)">删除</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="no-comments" v-else>
            <p>还没有评论，来抢沙发吧~</p>
          </div>
        </section>
      </main>

      <!-- 右侧：侧边栏 -->
      <aside class="article-aside">

        <!-- 目录 -->
        <div class="aside-card toc-card" v-if="toc.length > 0">
          <p class="aside-card-title">目录</p>
          <nav class="toc-nav">
            <a
              v-for="item in toc"
              :key="item.id"
              :href="`#${item.id}`"
              class="toc-item"
              :class="`toc-h${item.level}`"
            >{{ item.text }}</a>
          </nav>
        </div>

        <!-- 关于 -->
        <div class="aside-card">
          <p class="aside-card-title">关于我</p>
          <div class="about-brief">
            <div class="about-avatar">
              <img :src="siteConfig.avatar" :alt="siteConfig.name" />
            </div>
            <p class="about-name">{{ siteConfig.name }}</p>
            <p class="about-bio">{{ siteConfig.subtitle }}</p>
          </div>
        </div>

        <!-- 最新 -->
        <div class="aside-card">
          <p class="aside-card-title">最新文章</p>
          <div class="recent-list">
            <router-link
              v-for="a in recentArticles"
              :key="a.id"
              :to="`/article/${a.id}`"
              class="recent-item"
            >
              <span class="recent-title">{{ a.title }}</span>
              <span class="recent-date">{{ formatDate(a.createTime) }}</span>
            </router-link>
          </div>
        </div>

      </aside>
    </div>
  </div>

  <!-- 加载中 -->
  <div class="loading-page" v-else>
    <span class="loading-text">加载中...</span>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail } from '../api/article'
import { getCommentList, addComment, deleteComment as apiDeleteComment } from '../api/comment'
import { getArticleList } from '../api/article'
import { useUserStore } from '../stores/user'
import siteConfig, { fallbackArticles, fallbackComments, isStaticMode, resolveAssetUrl } from '../config/site.config.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 文章数据
const article = ref(null)

// 渲染内容（Markdown→HTML）
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return renderMarkdown(article.value.content)
})

// 解析 TOC
const toc = computed(() => {
  if (!article.value?.content) return []
  const headings = []
  const regex = /^(#{1,6})\s+(.+)$/gm
  let match
  while ((match = regex.exec(article.value.content)) !== null) {
    const level = match[1].length
    const text = match[2].trim()
    const id = `heading-${headings.length}-${text.replace(/\s+/g, '-').replace(/[^\w\u4e00-\u9fa5-]/g, '')}`
    headings.push({ level, text, id })
  }
  return headings
})

// 附件
const attachments = computed(() => {
  if (!article.value) return []
  try {
    const att = article.value.attachments
    if (!att) return []
    if (typeof att === 'string') return JSON.parse(att)
    if (Array.isArray(att)) return att
    return []
  } catch { return [] }
})

// 评论区
const comments = ref([])
const totalComments = ref(0)
const commentContent = ref('')
const commentNickname = ref('')
const commentEmail = ref('')
const commentWebsite = ref('')
const submitting = ref(false)
const replyingTo = ref(null)
const replyingToId = ref(null)
const pendingParentId = ref(null) // 修复：临时保存 parentId，API返回后再清空

// 最新文章
const recentArticles = ref([])
const apiAssetBase = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/api\/?$/, '')

// 能否提交
const canSubmit = computed(() => {
  if (!commentContent.value.trim()) return false
  if (commentContent.value.length > 500) return false
  // 必须填写昵称和邮箱
  return commentNickname.value.trim() && commentEmail.value.trim()
})

const getFallbackArticle = () => fallbackArticles.find(a => String(a.id) === String(route.params.id))
const getLocalCommentKey = () => `ethan_blog_comments_${route.params.id}`

const getLocalComments = () => {
  try {
    return JSON.parse(localStorage.getItem(getLocalCommentKey()) || '[]')
  } catch {
    return []
  }
}

const setLocalComments = (list) => {
  localStorage.setItem(getLocalCommentKey(), JSON.stringify(list))
}

const useStaticComments = () => {
  comments.value = [...getLocalComments(), ...(fallbackComments[route.params.id] || [])]
  totalComments.value = countAll(comments.value)
}

const resolveContentUrl = (url) => {
  if (!url) return '#'
  if (/^(https?:|mailto:|tel:|#)/.test(url)) return url
  if (url.startsWith('/uploads') || url.startsWith('/upload')) {
    return `${apiAssetBase}${url}`
  }
  if (url.startsWith('/')) {
    return resolveAssetUrl(url)
  }
  return url
}

// 修复附件URL
const getAttachmentUrl = (url) => resolveContentUrl(url)

// 修复封面URL
const getCoverUrl = (url) => resolveContentUrl(url)

// 修复附件下载
const downloadFile = async (attachment) => {
  try {
    const url = attachment.url
    if (!url) {
      alert('附件链接无效')
      return
    }

    const downloadUrl = resolveContentUrl(url)
    
    console.log('下载附件:', downloadUrl)
    
    // 创建临时a标签下载
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = attachment.name || attachment.fileName || 'download'
    link.target = '_blank'
    link.rel = 'noopener noreferrer'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    console.error('下载失败:', error)
    // 如果下载失败，直接打开链接
    window.open(attachment.url, '_blank')
  }
}

const loadArticle = async () => {
  if (isStaticMode) {
    article.value = getFallbackArticle()
    await nextTick()
    renderHeadingIds()
    return
  }
  try {
    const res = await getArticleDetail(route.params.id)
    if (res.code === 200) {
      article.value = res.data
      await nextTick()
      renderHeadingIds()
    } else {
      article.value = getFallbackArticle()
      await nextTick()
      renderHeadingIds()
    }
  } catch (e) {
    article.value = getFallbackArticle()
    if (import.meta.env.DEV) console.info('使用静态文章详情', e?.message || e)
    await nextTick()
    renderHeadingIds()
  }
}

const loadComments = async () => {
  if (isStaticMode) {
    useStaticComments()
    return
  }
  try {
    const res = await getCommentList(route.params.id)
    if (res.code === 200) {
      const list = res.data || []
      // 后端已构建完整树，填充博主标记
      const markAdmin = (arr) => {
        arr.forEach(c => {
          c.isAdmin = c.isAdmin || (userStore.isAdmin && c.userId === userStore.userId)
          if (c.children?.length) markAdmin(c.children)
        })
      }
      markAdmin(list)
      comments.value = list
      totalComments.value = countAll(list)
    } else {
      comments.value = fallbackComments[route.params.id] || []
      totalComments.value = countAll(comments.value)
    }
  } catch (e) {
    comments.value = fallbackComments[route.params.id] || []
    totalComments.value = countAll(comments.value)
    if (import.meta.env.DEV) console.info('使用静态评论数据', e?.message || e)
  }
}

// 递归统计总评论数
const countAll = (arr) => {
  let n = 0
  arr.forEach(c => { n++; if (c.children?.length) n += countAll(c.children) })
  return n
}

const submitComment = async () => {
  if (!canSubmit.value) {
    alert('请填写昵称和邮箱')
    return
  }
  
  submitting.value = true
  if (isStaticMode) {
    const localComment = {
      id: Date.now(),
      nickname: commentNickname.value.trim(),
      userAvatar: '',
      content: commentContent.value.trim(),
      createTime: new Date().toISOString(),
      children: [],
    }
    const next = [localComment, ...getLocalComments()]
    setLocalComments(next)
    replyingTo.value = null
    replyingToId.value = null
    pendingParentId.value = null
    commentContent.value = ''
    useStaticComments()
    submitting.value = false
    alert('当前是静态部署模式，评论已保存在本机浏览器。')
    return
  }
  try {
    // 修复：先保存 parentId，再发请求，最后再清空
    const parentId = replyingToId.value
    const payload = {
      articleId: Number(route.params.id),
      content: commentContent.value.trim(),
      nickname: commentNickname.value.trim(),
      email: commentEmail.value.trim()
    }
    
    if (commentWebsite.value.trim()) {
      payload.website = commentWebsite.value.trim()
    }
    
    if (parentId) {
      payload.parentId = parentId
    }
    
    console.log('提交评论数据:', payload)
    const res = await addComment(payload)
    console.log('评论返回:', res)

    // 清空回复状态，保留昵称/邮箱/内容（支持连续回复）
    replyingTo.value = null
    replyingToId.value = null
    pendingParentId.value = null
    commentContent.value = '' // 提交后清空内容，避免重复发送

    await loadComments()
    alert(parentId ? '回复成功！' : '评论发表成功！')
  } catch (e) {
    console.error('发表评论失败', e)
    if (e.response && e.response.status === 401) {
      if (confirm('发表评论需要登录，是否前往登录？')) {
        router.push('/login')
      }
    } else {
      alert('发表评论失败：' + (e.message || '请重试'))
    }
  } finally {
    submitting.value = false
  }
}

const deleteComment = async (id) => {
  if (!confirm('确定要删除这条评论吗？')) return
  
  try {
    await apiDeleteComment(id)
    await loadComments()
  } catch (e) {
    console.error('删除评论失败', e)
    alert('删除评论失败')
  }
}

const replyTo = (comment) => {
  replyingTo.value = comment.nickname || comment.username
  replyingToId.value = comment.id
  commentContent.value = `@${comment.nickname || comment.username} `
  nextTick(() => {
    const textarea = document.querySelector('.comment-textarea')
    if (textarea) textarea.focus()
  })
}

const cancelReply = () => {
  replyingTo.value = null
  replyingToId.value = null
  commentContent.value = ''
}

// 只有博主能删除评论
const canDeleteComment = (comment) => {
  if (!isLoggedIn.value) return false
  return userStore.isAdmin
}

const getInitial = (name) => {
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}

const handleAvatarError = (e) => {
  e.target.style.display = 'none'
}

const formatFileSize = (bytes) => {
  if (!bytes) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const loadRecent = async () => {
  if (isStaticMode) {
    recentArticles.value = fallbackArticles
      .filter(a => a.id !== Number(route.params.id))
      .slice(0, 5)
    return
  }
  try {
    const res = await getArticleList(1, 5)
    const list = res.data?.records?.length ? res.data.records : fallbackArticles
    recentArticles.value = list
      .filter(a => a.id !== Number(route.params.id))
      .slice(0, 5)
  } catch (e) {
    recentArticles.value = fallbackArticles
      .filter(a => a.id !== Number(route.params.id))
      .slice(0, 5)
  }
}

const renderHeadingIds = () => {
  nextTick(() => {
    const el = document.querySelector('.article-body')
    if (!el) return
    toc.value.forEach(item => {
      const nodes = el.querySelectorAll(`h${item.level}`)
      let idx = 0
      const regex = new RegExp(`^${item.text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}$`)
      nodes.forEach(node => {
        if (node.textContent.trim().match(regex) && idx === 0) {
          node.id = item.id
          idx++
        }
      })
    })
  })
}

const formatDate = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

const renderMarkdown = (text) => {
  if (!text) return ''
  let html = text
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/!\[(.*?)\]\((.*?)\)/g,
      (_, alt, src) => {
        const url = resolveContentUrl(src)
        return `<img src="${url}" alt="${alt}" style="max-width:100%;border-radius:4px;margin:8px 0" loading="lazy" />`
      })
    .replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>')
    .replace(/^###### (.+)$/gm, '<h6 id="h_$1">$1</h6>')
    .replace(/^##### (.+)$/gm, '<h5 id="h_$1">$1</h5>')
    .replace(/^#### (.+)$/gm, '<h4 id="h_$1">$1</h4>')
    .replace(/^### (.+)$/gm, '<h3 id="h_$1">$1</h3>')
    .replace(/^## (.+)$/gm, '<h2 id="h_$1">$1</h2>')
    .replace(/^# (.+)$/gm, '<h1 id="h_$1">$1</h1>')
    .replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/^> (.+)$/gm, '<blockquote>$1</blockquote>')
    .replace(/^---$/gm, '<hr>')
    .replace(/^- (.+)$/gm, '<li>$1</li>')
    .replace(/^(\d+)\. (.+)$/gm, '<li>$2</li>')

  html = html
    .split('\n\n')
    .map(p => p.trim())
    .filter(p => p)
    .map(p => {
      if (/^<(h[1-6]|pre|blockquote|ul|ol|li|img|hr)/.test(p)) return p
      return `<p>${p.replace(/\n/g, '<br>')}</p>`
    })
    .join('\n')

  return html
}

// 防重载：合并 watch 和 onMounted 的初始化逻辑
let initialized = false

const init = () => {
  if (initialized) return
  initialized = true
  article.value = null
  comments.value = []
  loadArticle()
  loadComments()
  loadRecent()
}

onMounted(() => {
  init()
})

watch(() => route.params.id, () => {
  // 重置已读状态，强制重新加载
  initialized = false
  nextTick(init)
})
</script>

<style scoped>
/* CSS 变量 — 继承全局，仅补充文章页特有变量 */
.article-page {
  --radius-sm: 4px;
  
  min-height: calc(100vh - 72px);
  padding: 40px 0;
}

.article-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 32px;
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 48px;
  align-items: start;
}

.article-main {
  min-width: 0;
  padding: 28px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.72);
  box-shadow: var(--shadow);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
}

.article-aside {
  position: sticky;
  top: 100px;
  height: fit-content;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.breadcrumb {
  font-size: 13px;
  color: var(--text-lighter);
  margin-bottom: 20px;
}
.breadcrumb a { color: var(--text-lighter); }
.breadcrumb a:hover { color: var(--accent); }
.breadcrumb span { margin: 0 4px; }

.article-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border);
}
.article-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--text);
  line-height: 1.3;
  margin-bottom: 12px;
  font-family: var(--font-serif);
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
.meta-item { font-size: 13px; color: var(--text-lighter); }
.meta-sep { color: var(--border); }
.meta-cat {
  font-size: 12px;
  color: var(--accent-dark);
  font-weight: 500;
  background: var(--accent-dim);
  padding: 2px 10px;
  border-radius: 20px;
}
.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.tag-chip {
  font-size: 12px;
  color: var(--text-light);
  background: var(--bg);
  border: 1px solid var(--border);
  padding: 3px 10px;
  border-radius: 20px;
  transition: all 0.2s;
}
.tag-chip:hover {
  background: var(--text);
  border-color: var(--text);
  color: #fff;
}

.article-body {
  font-size: 15px;
  line-height: 2;
  color: var(--text);
  font-family: var(--font-serif);
}
:deep(.article-body h1),
:deep(.article-body h2),
:deep(.article-body h3),
:deep(.article-body h4) {
  font-family: var(--font-display);
  color: var(--text);
  margin: 1.5em 0 0.6em;
  line-height: 1.4;
}
:deep(.article-body h2) {
  font-size: 1.35rem;
  border-bottom: 1px solid var(--border);
  padding-bottom: 0.4em;
}
:deep(.article-body h3) { font-size: 1.15rem; }
:deep(.article-body p) { margin: 0.8em 0; }
:deep(.article-body a) { color: var(--accent-dark); text-decoration: underline; text-underline-offset: 3px; }
:deep(.article-body strong) { font-weight: 600; }
:deep(.article-body em) { font-style: italic; }
:deep(.article-body code) {
  font-family: 'Fira Code', monospace;
  background: rgba(56, 248, 255, 0.09);
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.88em;
  color: var(--accent-soft);
}
:deep(.article-body pre) {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 16px 20px;
  border-radius: var(--radius);
  overflow-x: auto;
  margin: 16px 0;
}
:deep(.article-body pre code) {
  background: none;
  padding: 0;
  color: inherit;
}
:deep(.article-body blockquote) {
  border-left: 3px solid var(--accent);
  margin: 16px 0;
  padding: 10px 20px;
  background: var(--accent-dim);
  color: var(--text-light);
  border-radius: 0 4px 4px 0;
}
:deep(.article-body ul),
:deep(.article-body ol) { margin: 12px 0 16px 24px; }
:deep(.article-body li) { margin-bottom: 6px; }
:deep(.article-body hr) { border: none; border-top: 1px solid var(--border); margin: 28px 0; }
:deep(.article-body img) { max-width: 100%; border-radius: var(--radius-sm); margin: 8px 0; }

/* 封面图片样式 */
.article-cover {
  width: 100%;
  max-height: 400px;
  border-radius: var(--radius);
  overflow: hidden;
  margin-bottom: 24px;
}
.article-cover img {
  width: 100%;
  height: auto;
  max-height: 400px;
  object-fit: cover;
}

.article-attachments {
  margin-top: 32px;
  padding: 20px;
  background: rgba(8, 14, 27, 0.62);
  border: 1px solid var(--border);
  border-radius: var(--radius);
}
.attach-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text);
}
.attach-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.attach-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text);
  background: var(--card);
  border: 1px solid var(--border);
  padding: 8px 14px;
  border-radius: 6px;
  transition: all 0.2s;
  text-decoration: none;
  cursor: pointer;
}
.attach-item:hover {
  background: var(--accent-dim);
  border-color: var(--accent);
  transform: translateY(-1px);
}
.att-size {
  font-size: 11px;
  color: var(--text-lighter);
  margin-left: 4px;
}


/* 评论区 */
.comments-section {
  margin-top: 40px;
}
.comments-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 24px;
}
.comments-count {
  font-size: 14px;
  color: var(--text-lighter);
  font-weight: 400;
}

/* 评论表单 - 与留言板一致 */
.comment-form {
  background: rgba(8, 14, 27, 0.72);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 24px;
  margin-bottom: 32px;
}
.form-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 12px;
}
.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  color: var(--text);
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.form-input:focus {
  outline: none;
  border-color: var(--accent);
}
.form-input::placeholder {
  color: var(--text-lighter);
}
.form-input.full-row {
  width: 100%;
  margin-bottom: 12px;
}
.comment-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  color: var(--text);
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.comment-textarea:focus {
  outline: none;
  border-color: var(--accent);
}
.reply-notice {
  margin-top: 8px;
  padding: 6px 12px;
  background: rgba(56, 248, 255, 0.08);
  border-radius: 6px;
  font-size: 13px;
  color: var(--accent);
}
.cancel-reply {
  margin-left: 8px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 12px;
}
.cancel-reply:hover { color: #f56c6c; }
.form-footer {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
.btn-submit {
  padding: 8px 20px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.btn-submit:hover:not(:disabled) {
  background: var(--accent-dark);
  transform: translateY(-1px);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 评论列表 - 与留言板一致 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.comment-item {
  display: flex;
  gap: 14px;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(8, 14, 27, 0.56);
  animation: fadeUp 0.35s both;
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
.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
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
  background: rgba(56, 248, 255, 0.1);
  color: var(--accent);
  border-radius: 10px;
  font-weight: 600;
}
.comment-time {
  font-size: 12px;
  color: var(--text-lighter);
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
  margin-left: 54px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.sub-comment-item {
  display: flex;
  gap: 10px;
}
.sub-comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
  overflow: hidden;
}
.sub-comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.sub-comment-body {
  flex: 1;
  min-width: 0;
}
.sub-comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}
.sub-author {
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
}
.sub-author.linked {
  color: var(--accent-dark);
  text-decoration: none;
}
.sub-author.linked:hover {
  text-decoration: underline;
}
.sub-time {
  font-size: 11px;
  color: var(--text-lighter);
}
.sub-text {
  font-size: 13px;
  color: var(--text);
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}
.no-comments {
  text-align: center;
  padding: 40px 0;
  color: var(--text-lighter);
  font-size: 14px;
}
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}
.page-btn {
  padding: 6px 14px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: transparent;
  color: var(--text);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}
.page-btn:hover:not(:disabled) {
  background: rgba(56, 248, 255, 0.16);
  color: var(--accent-soft);
}
.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.page-info {
  font-size: 13px;
  color: var(--text-lighter);
}

.aside-card {
  background: rgba(8, 14, 27, 0.72);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 18px;
}
.aside-card-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 14px;
  letter-spacing: 0.5px;
}
.toc-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.toc-item {
  font-size: 13px;
  color: var(--text-light);
  padding: 4px 8px;
  border-radius: 4px;
  border-left: 2px solid transparent;
  transition: all 0.2s;
  line-height: 1.5;
}
.toc-item:hover {
  color: var(--accent-dark);
  border-left-color: var(--accent);
  background: var(--accent-dim);
}
.toc-h1 { font-size: 14px; font-weight: 600; color: var(--text); }
.toc-h2 { padding-left: 8px; }
.toc-h3 { padding-left: 16px; font-size: 12px; }
.about-brief {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}
.about-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 10px;
  border: 2px solid var(--border);
}
.about-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.about-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}
.about-bio {
  font-size: 12px;
  color: var(--text-lighter);
}
.recent-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.recent-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border);
  transition: all 0.2s;
}
.recent-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.recent-item:hover .recent-title {
  color: var(--accent-dark);
}
.recent-title {
  font-size: 13px;
  color: var(--text);
  line-height: 1.4;
  transition: color 0.2s;
}
.recent-date {
  font-size: 11px;
  color: var(--text-lighter);
}

.loading-page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.loading-text {
  font-size: 14px;
  color: var(--text-lighter);
  letter-spacing: 2px;
}

@media (max-width: 900px) {
  .article-inner {
    grid-template-columns: 1fr;
    padding: 0 20px;
  }
  .article-aside {
    position: static;
    order: -1;
    margin-top: 40px;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
  .sub-comments {
    margin-left: 20px;
  }
  .article-title {
    font-size: 1.4rem;
  }
}
</style>
