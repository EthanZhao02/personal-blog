<template>
  <div class="write-page">
    <div class="page-header">
      <h2 class="page-title">{{ isEdit ? t('editTitle') : t('writeTitle') }}</h2>
    </div>

    <div class="editor-layout">
      <!-- 左侧：文章列表 -->
      <aside class="article-sidebar">
        <h4 class="sidebar-heading">{{ t('myArticles') }}</h4>
        <div class="article-list">
          <div
            v-for="a in myArticles"
            :key="a.id"
            class="article-item"
            :class="{ active: editingId === a.id }"
            @click="editArticle(a)"
          >
            <span class="item-title">
              {{ a.title || t('untitled') }}
              <span class="item-status" :class="a.isPublished ? 'published' : 'draft'">
                {{ a.isPublished ? t('published') : t('draft') }}
              </span>
            </span>
            <div class="item-actions">
              <button class="item-btn" :title="a.isPublished ? t('unpublish') : t('publish')" @click.stop="togglePublish(a)">
                {{ a.isPublished ? '📢' : '📝' }}
              </button>
              <button class="item-btn item-btn-danger" :title="t('delete')" @click.stop="handleDelete(a.id)">
                🗑
              </button>
            </div>
          </div>
        </div>
        <button class="btn-new" @click="newArticle">+ {{ t('newArticle') }}</button>
      </aside>

      <!-- 右侧：编辑器 -->
      <div class="editor-main">
        <!-- 封面上传 -->
        <div class="cover-section">
          <div class="cover-preview" v-if="article.cover" @click="triggerCoverUpload">
            <img :src="article.cover" alt="cover" />
            <div class="cover-overlay">
              <span>{{ t('changeCover') }}</span>
            </div>
          </div>
          <div class="cover-upload" v-else @click="triggerCoverUpload">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <path d="M21 15l-5-5L5 21"/>
            </svg>
            <span>{{ t('addCover') }}</span>
          </div>
          <input ref="coverInput" type="file" accept="image/*" hidden @change="handleCoverUpload" />
        </div>

        <!-- 标题 -->
        <input
          v-model="article.title"
          type="text"
          :placeholder="t('titlePlaceholder')"
          class="title-input"
        />

        <!-- 分类标签 -->
        <div class="meta-row">
          <div class="meta-field">
            <label>{{ t('category') }}</label>
            <select v-model="article.categoryId">
              <option :value="null">{{ t('noCategory') }}</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div class="meta-field">
            <label>{{ t('tags') }}</label>
            <div class="tag-selector">
              <button
                v-for="tag in tags"
                :key="tag.id"
                class="tag-btn"
                :class="{ active: selectedTagIds.includes(tag.id) }"
                @click="toggleTag(tag.id)"
              >{{ tag.name }}</button>
            </div>
          </div>
        </div>

        <!-- 工具栏 -->
        <div class="toolbar">
          <button class="tool-btn" @click="insertFormat('**', '**')" title="Bold">
            <strong>B</strong>
          </button>
          <button class="tool-btn" @click="insertFormat('*', '*')" title="Italic">
            <em>I</em>
          </button>
          <span class="tool-sep"></span>
          <button class="tool-btn" @click="insertFormat('## ', '')" title="Heading">
            H
          </button>
          <button class="tool-btn" @click="insertFormat('- ', '')" title="List">
            •
          </button>
          <button class="tool-btn" @click="insertFormat('`', '`')" title="Code">
            &lt;/&gt;
          </button>
          <span class="tool-sep"></span>
          <button class="tool-btn" @click="insertFormat('[', '](url)')" title="Link">
            🔗
          </button>
          <button class="tool-btn" @click="triggerImageUpload" title="Image">
            🖼️
          </button>
          <button class="tool-btn" @click="insertFormat('\n```\n', '\n```\n')" title="Code Block">
            { }
          </button>
          <input ref="imageInput" type="file" accept="image/*" hidden @change="handleImageUpload" />
        </div>

        <!-- 内容编辑：左编辑右预览 -->
        <div class="editor-container">
          <div class="editor-pane">
            <div class="pane-label">Markdown</div>
            <textarea
              ref="contentTextarea"
              v-model="article.content"
              :placeholder="t('contentPlaceholder')"
              class="content-textarea"
            ></textarea>
          </div>
          <div class="preview-pane">
            <div class="pane-label">预览</div>
            <div class="preview-content" v-html="renderedContent"></div>
          </div>
        </div>

        <!-- 摘要 -->
        <div class="summary-section">
          <label>{{ t('summary') }}</label>
          <textarea
            v-model="article.summary"
            :placeholder="t('summaryPlaceholder')"
            class="summary-textarea"
          ></textarea>
        </div>

        <!-- 附件上传 -->
        <div class="attachment-section">
          <label>{{ t('attachments') }}</label>
          <div class="attachment-list" v-if="attachments.length > 0">
            <div v-for="(file, idx) in attachments" :key="idx" class="attachment-item">
              <span class="file-icon">📄</span>
              <a :href="file.url || '#'" target="_blank" class="file-name" :title="file.name">{{ file.name }}</a>
              <span class="file-size">{{ formatSize(file.size) }}</span>
              <button class="file-remove" @click="removeAttachment(idx)">×</button>
            </div>
          </div>
          <button class="attach-btn" @click="triggerAttachmentUpload">
            + {{ t('addAttachment') }}
          </button>
          <input ref="attachmentInput" type="file" multiple hidden @change="handleAttachmentUpload" />
        </div>

        <!-- 操作按钮 -->
        <div class="action-bar">
          <button class="btn btn-ghost" @click="saveDraft" :disabled="saving">
            {{ saving ? t('saving') : t('saveDraft') }}
          </button>
          <button class="btn btn-primary" @click="publish" :disabled="saving">
            {{ saving ? t('publishing') : t('publish') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getArticleList, getArticleDetail, createArticle, updateArticle, deleteArticle, getMyArticles } from '../api/article'
import { getCategoryList } from '../api/category'
import { getTagList } from '../api/tag'
import { uploadImage, uploadAttachment } from '../api/upload'

const route = useRoute()
const userStore = useUserStore()

const lang = ref(localStorage.getItem('lang') || 'zh')

const translations = {
  zh: {
    editTitle: '编辑文章',
    writeTitle: '撰写文章',
    myArticles: '我的文章',
    untitled: '无标题',
    published: '已发布',
    draft: '草稿',
    newArticle: '新建文章',
    changeCover: '更换封面',
    addCover: '添加封面图片',
    titlePlaceholder: '输入文章标题...',
    category: '分类',
    noCategory: '无分类',
    tags: '标签',
    contentPlaceholder: '支持 Markdown 格式，开始写作...',
    summary: '摘要',
    summaryPlaceholder: '文章摘要（可选，不填则自动截取）',
    attachments: '附件',
    addAttachment: '添加附件',
    saveDraft: '保存草稿',
    saving: '保存中...',
    publish: '发布文章',
    publishing: '发布中...',
    unpublish: '撤回',
    delete: '删除'
  },
  en: {
    editTitle: 'Edit Article',
    writeTitle: 'Write Article',
    myArticles: 'My Articles',
    untitled: 'Untitled',
    published: 'Published',
    draft: 'Draft',
    newArticle: 'New Article',
    changeCover: 'Change Cover',
    addCover: 'Add Cover Image',
    titlePlaceholder: 'Enter article title...',
    category: 'Category',
    noCategory: 'No Category',
    tags: 'Tags',
    contentPlaceholder: 'Markdown supported, start writing...',
    summary: 'Summary',
    summaryPlaceholder: 'Article summary (optional)',
    attachments: 'Attachments',
    addAttachment: 'Add Attachment',
    saveDraft: 'Save Draft',
    saving: 'Saving...',
    publish: 'Publish',
    publishing: 'Publishing...',
    unpublish: 'Unpublish',
    delete: 'Delete'
  }
}

const t = (key) => translations[lang.value][key] || key

const article = ref({
  title: '',
  content: '',
  summary: '',
  cover: '',
  categoryId: null
})
const selectedTagIds = ref([])
const attachments = ref([])
const myArticles = ref([])
const categories = ref([])
const tags = ref([])
const saving = ref(false)
const editingId = ref(null)

const coverInput = ref(null)
const imageInput = ref(null)
const attachmentInput = ref(null)
const contentTextarea = ref(null)

const isEdit = computed(() => !!editingId.value)

// 简单的 Markdown → HTML 渲染（支持标题、粗斜体、图片、代码块、列表、链接）
const renderedContent = computed(() => {
  if (!article.value.content) return '<p style="color:var(--text-lighter);font-style:italic">预览区域</p>'
  let html = article.value.content
    // 转义 HTML
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    // 代码块
    .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
    // 行内代码
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    // 图片（![]... 的 src 用 /uploads 路径，直接显示）
    .replace(/!\[(.*?)\]\((.*?)\)/g, '<img src="$2" alt="$1" style="max-width:100%;border-radius:4px;margin:8px 0" />')
    // 链接
    .replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" target="_blank">$1</a>')
    // 标题
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    // 粗体斜体
    .replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    // 引用
    .replace(/^&gt; (.+)$/gm, '<blockquote>$1</blockquote>')
    // 分割线
    .replace(/^---$/gm, '<hr>')
    // 列表
    .replace(/^- (.+)$/gm, '<li>$1</li>')
    .replace(/^(\d+)\. (.+)$/gm, '<li>$2</li>')
    // 段落
    .split('\n\n')
    .map(p => p.trim())
    .filter(p => p)
    .map(p => {
      if (/^<(h[1-6]|pre|blockquote|ul|ol|li|img|hr)/.test(p)) return p
      return `<p>${p.replace(/\n/g, '<br>')}</p>`
    })
    .join('\n')
  return html
})

// 上传相关
const triggerCoverUpload = () => coverInput.value?.click()
const triggerImageUpload = () => imageInput.value?.click()
const triggerAttachmentUpload = () => attachmentInput.value?.click()

const handleCoverUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadImage(file)
    if (res.code === 200 && res.data) {
      article.value.cover = res.data
    } else {
      alert('封面上传失败: ' + (res.message || '未知错误'))
    }
  } catch (err) {
    console.error('封面上传失败:', err)
    alert('封面上传失败，请重试')
  }
}

const handleImageUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadImage(file)
    if (res.code === 200 && res.data) {
      const markdown = `![${file.name}](${res.data})`
      insertAtCursor(markdown)
    } else {
      alert('图片上传失败: ' + (res.message || '未知错误'))
    }
  } catch (err) {
    console.error('图片上传失败:', err)
    alert('图片上传失败，请重试')
  }
}

const handleAttachmentUpload = async (e) => {
  const files = Array.from(e.target.files)
  for (const file of files) {
    try {
      const res = await uploadAttachment(file)
      if (res.code === 200 && res.data) {
        attachments.value.push({
          name: file.name,
          size: file.size,
          url: res.data
        })
      } else {
        alert(`附件「${file.name}」上传失败: ` + (res.message || '未知错误'))
      }
    } catch (err) {
      console.error('附件上传失败:', err)
      alert(`附件「${file.name}」上传失败，请重试`)
    }
  }
}

const removeAttachment = (idx) => {
  attachments.value.splice(idx, 1)
}

// 编辑器工具
const insertFormat = (before, after) => {
  const textarea = contentTextarea.value
  if (!textarea) return
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = article.value.content || ''
  const selected = text.substring(start, end)
  const newText = text.substring(0, start) + before + selected + after + text.substring(end)
  article.value.content = newText
  // 恢复光标位置
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + before.length, end + before.length)
  }, 0)
}

const insertAtCursor = (text) => {
  const textarea = contentTextarea.value
  if (!textarea) return
  const start = textarea.selectionStart
  const content = article.value.content || ''
  article.value.content = content.substring(0, start) + text + content.substring(start)
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + text.length, start + text.length)
  }, 0)
}

const formatSize = (bytes) => {
  if (!bytes) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

const toggleTag = (id) => {
  const idx = selectedTagIds.value.indexOf(id)
  if (idx > -1) {
    selectedTagIds.value.splice(idx, 1)
  } else {
    selectedTagIds.value.push(id)
  }
}

// 数据加载
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (e) {
    console.error('加载分类失败:', e)
  }
}

const loadTags = async () => {
  try {
    const res = await getTagList()
    if (res.code === 200) {
      tags.value = res.data || []
    }
  } catch (e) {
    console.error('加载标签失败:', e)
  }
}

const loadMyArticles = async () => {
  try {
    const res = await getMyArticles(1, 50)
    if (res.code === 200) {
      myArticles.value = res.data?.records || []
    }
  } catch (e) {
    console.error('加载文章列表失败:', e)
  }
}

const editArticle = async (a) => {
  if (a.id === editingId.value) return
  editingId.value = a.id
  await loadArticleDetail(a.id)
}

const loadArticleDetail = async (id) => {
  try {
    const res = await getArticleDetail(id)
    if (res.code === 200 && res.data) {
      article.value = {
        title: res.data.title || '',
        content: res.data.content || '',
        summary: res.data.summary || '',
        cover: res.data.coverImage || res.data.cover || '',
        categoryId: res.data.categoryId
      }
      // 标签：从后端返回的 tags 数组提取 ID
      selectedTagIds.value = res.data.tags?.map(t => t.id) || []
      // 附件：从后端 attachmentsList(JSON字符串) 解析还原
      attachments.value = []
      const raw = res.data.attachments || res.data.attachmentsList
      if (raw) {
        try {
          const list = JSON.parse(raw)
          if (Array.isArray(list)) {
            attachments.value = list.map(url => ({ name: url.split('/').pop(), size: 0, url }))
          }
        } catch {}
      }
    }
  } catch (e) {
    console.error('加载文章详情失败:', e)
  }
}

const togglePublish = async (a) => {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    return
  }
  
  try {
    const res = await updateArticle({ id: a.id, isPublished: a.isPublished ? 0 : 1 })
    if (res.code === 200 || res.code === 0) {
      await loadMyArticles()
    } else {
      alert('操作失败: ' + (res.message || '未知错误'))
    }
  } catch (e) {
    console.error('切换发布状态失败:', e)
    if (e?.response?.status === 401) {
      alert('登录已过期，请重新登录')
    } else {
      alert('操作失败')
    }
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除这篇文章吗？')) return
  
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    return
  }
  
  try {
    const res = await deleteArticle(id)
    if (res.code === 200 || res.code === 0) {
      alert('删除成功')
      if (editingId.value === id) newArticle()
      await loadMyArticles()
    } else {
      alert('删除失败: ' + (res.message || '未知错误'))
    }
  } catch (e) {
    console.error('删除失败:', e)
    const msg = e?.response?.data?.message || e?.message || '删除失败'
    if (e?.response?.status === 401) {
      alert('登录已过期，请重新登录')
    } else {
      alert(msg)
    }
  }
}

const newArticle = () => {
  editingId.value = null
  article.value = { title: '', content: '', summary: '', cover: '', categoryId: null }
  selectedTagIds.value = []
  attachments.value = []
}

const saveDraft = async () => {
  await doSave(false)
}

const publish = async () => {
  await doSave(true)
}

const doSave = async (isPublish) => {
  if (!article.value.title.trim()) {
    alert('请输入标题')
    return
  }
  if (!article.value.content.trim()) {
    alert('请输入文章内容')
    return
  }
  saving.value = true
  try {
    // 按后端 ArticleDTO 格式构造参数
    // tagIds: List<Long> — 直接用前端已有的选中 ID 数组
    // attachments: String — 附件 URL 列表转 JSON 字符串
    const attachmentUrls = attachments.value.map(a => a.url).filter(Boolean)
    const summary = article.value.summary ||
      article.value.content.replace(/[#*`>\-\[\]()!]/g, '').substring(0, 150)

    const payload = {
      title: article.value.title.trim(),
      content: article.value.content,
      summary: summary,
      coverImage: article.value.cover || null,
      categoryId: article.value.categoryId || null,
      tagIds: selectedTagIds.value,          // List<Long>，直接用 ID 数组
      attachments: attachmentUrls.length > 0 ? JSON.stringify(attachmentUrls) : null,
      isPublished: isPublish ? 1 : 0
    }

    let res
    if (editingId.value) {
      payload.id = editingId.value
      res = await updateArticle(payload)
    } else {
      res = await createArticle(payload)
    }

    // 严格检查后端返回 code
    if (res.code === 200 || res.code === 0) {
      // 发布成功后取回文章 ID
      if (!editingId.value && res.data?.id) {
        editingId.value = res.data.id
      }
      await loadMyArticles()
      alert(editingId.value ? '保存成功' : '发布成功')
    } else {
      alert('操作失败：' + (res.message || `错误码 ${res.code}`))
    }
  } catch (e) {
    console.error('保存失败:', e)
    const msg = e?.response?.data?.message || e?.message || '保存失败'
    alert(msg)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadTags(), loadMyArticles()])
  const id = route.params.id
  if (id) {
    editingId.value = Number(id)
    await loadArticleDetail(Number(id))
  }
})
</script>

<style scoped>
.write-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 1.3rem;
  font-weight: 400;
  color: var(--text);
  letter-spacing: 2px;
}

.editor-layout {
  display: flex;
  gap: 24px;
}

/* 侧边栏 */
.article-sidebar {
  width: 240px;
  flex-shrink: 0;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 20px;
}

.sidebar-heading {
  font-size: 12px;
  color: var(--text-lighter);
  letter-spacing: 2px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border);
}

.article-list {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.article-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.article-item:hover {
  background: var(--bg);
}

.article-item.active {
  background: rgba(200, 169, 126, 0.12);
}

.item-title {
  font-size: 13px;
  color: var(--text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.item-actions {
  display: flex;
  gap: 2px;
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.2s;
}

.article-item:hover .item-actions {
  opacity: 1;
}

.item-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  padding: 2px 4px;
  border-radius: 3px;
  transition: background 0.15s;
}

.item-btn:hover {
  background: var(--border);
}

.item-btn-danger:hover {
  background: #fdecea;
}

.item-status {
  font-size: 10px;
  padding: 2px 6px;
  background: var(--bg);
  color: var(--text-lighter);
  border-radius: 10px;
}

.item-status.published {
  color: var(--accent-dark);
}

.btn-new {
  width: 100%;
  padding: 10px;
  background: transparent;
  border: 1px dashed var(--border);
  color: var(--text-light);
  font-size: 13px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-new:hover {
  border-color: var(--accent);
  color: var(--accent-dark);
}

/* 编辑器主体 */
.editor-main {
  flex: 1;
  min-width: 0;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 32px;
}

/* 封面 */
.cover-section {
  margin-bottom: 20px;
}

.cover-preview {
  position: relative;
  width: 100%;
  max-width: 400px;
  height: 120px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.cover-preview:hover .cover-overlay {
  opacity: 1;
}

.cover-overlay span {
  color: white;
  font-size: 14px;
}

.cover-upload {
  width: 100%;
  height: 120px;
  border: 2px dashed var(--border);
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--text-lighter);
  cursor: pointer;
  transition: all 0.2s;
}

.cover-upload:hover {
  border-color: var(--accent);
  color: var(--accent-dark);
}

.cover-upload span {
  font-size: 13px;
}

/* 标题 */
.title-input {
  width: 100%;
  padding: 16px 0;
  border: none;
  border-bottom: 1px solid var(--border);
  background: transparent;
  font-family: var(--font-display);
  font-size: 1.5rem;
  color: var(--text);
  outline: none;
  letter-spacing: 1px;
  margin-bottom: 20px;
}

.title-input:focus {
  border-bottom-color: var(--accent);
}

/* 分类标签 */
.meta-row {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
}

.meta-field {
  flex: 1;
}

.meta-field label {
  display: block;
  font-size: 12px;
  color: var(--text-lighter);
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.meta-field select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  font-size: 14px;
  border-radius: 4px;
  outline: none;
}

.tag-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-btn {
  padding: 6px 14px;
  background: var(--bg);
  border: 1px solid var(--border);
  color: var(--text-light);
  font-size: 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-btn:hover {
  border-color: var(--accent);
}

.tag-btn.active {
  background: var(--accent);
  border-color: var(--accent);
  color: var(--bg);
}

/* 工具栏 */
.toolbar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 4px;
  margin-bottom: 16px;
}

.tool-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: var(--text-light);
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.tool-btn:hover {
  background: var(--card);
  color: var(--text);
}

.tool-sep {
  width: 1px;
  height: 20px;
  background: var(--border);
  margin: 0 8px;
}

/* 内容区：左右分栏 */
.editor-container {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.editor-pane,
.preview-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.pane-label {
  font-size: 11px;
  color: var(--text-lighter);
  letter-spacing: 2px;
  text-transform: uppercase;
  margin-bottom: 8px;
}

.content-textarea {
  flex: 1;
  min-height: 400px;
  padding: 16px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 14px;
  line-height: 1.8;
  resize: none;
  outline: none;
  border-radius: 4px;
  box-sizing: border-box;
}

.content-textarea:focus {
  border-color: var(--accent);
}

/* 预览面板 */
.preview-content {
  flex: 1;
  min-height: 400px;
  padding: 16px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  font-family: var(--font-serif);
  font-size: 15px;
  line-height: 1.8;
  overflow-y: auto;
  border-radius: 4px;
  box-sizing: border-box;
}

.preview-content :deep(h1),
.preview-content :deep(h2),
.preview-content :deep(h3) {
  font-family: var(--font-display);
  color: var(--text);
  margin: 16px 0 8px;
  line-height: 1.3;
}

.preview-content :deep(pre) {
  background: rgba(0,0,0,0.06);
  padding: 12px 16px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 12px 0;
}

.preview-content :deep(code) {
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
  background: rgba(0,0,0,0.06);
  padding: 2px 6px;
  border-radius: 3px;
}

.preview-content :deep(pre code) {
  background: none;
  padding: 0;
}

.preview-content :deep(blockquote) {
  border-left: 3px solid var(--accent);
  margin: 12px 0;
  padding: 8px 16px;
  color: var(--text-light);
  background: rgba(200,169,126,0.06);
  border-radius: 0 4px 4px 0;
}

.preview-content :deep(p) {
  margin: 8px 0;
}

.preview-content :deep(a) {
  color: var(--accent-dark);
  text-decoration: underline;
}

/* 摘要 */
.summary-section {
  margin-bottom: 20px;
}

.summary-section label {
  display: block;
  font-size: 12px;
  color: var(--text-lighter);
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.summary-textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  font-family: var(--font-serif);
  font-size: 14px;
  line-height: 1.7;
  resize: vertical;
  outline: none;
  border-radius: 4px;
}

/* 附件 */
.attachment-section {
  margin-bottom: 24px;
}

.attachment-section label {
  display: block;
  font-size: 12px;
  color: var(--text-lighter);
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 4px;
}

.file-icon {
  font-size: 16px;
}

.file-name {
  flex: 1;
  font-size: 13px;
  color: var(--text);
}

.file-remove {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: var(--text-lighter);
  font-size: 18px;
  cursor: pointer;
  border-radius: 4px;
}

.file-remove:hover {
  background: var(--card);
  color: #e74c3c;
}

.attach-btn {
  padding: 10px 16px;
  background: transparent;
  border: 1px dashed var(--border);
  color: var(--text-light);
  font-size: 13px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.attach-btn:hover {
  border-color: var(--accent);
  color: var(--accent-dark);
}

/* 操作按钮 */
.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid var(--border);
}

.btn {
  padding: 10px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-ghost {
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text-light);
}

.btn-ghost:hover {
  border-color: var(--accent);
  color: var(--accent-dark);
}

.btn-primary {
  background: var(--text);
  border: 1px solid var(--text);
  color: var(--bg);
}

.btn-primary:hover {
  background: var(--accent-dark);
  border-color: var(--accent-dark);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 900px) {
  .editor-layout {
    flex-direction: column;
  }
  .article-sidebar {
    width: 100%;
  }
}

.item-status {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 8px;
  margin-left: 6px;
  vertical-align: middle;
}
.item-status.published {
  background: #d1fae5;
  color: #065f46;
}
.item-status.draft {
  background: #fef3c7;
  color: #92400e;
}
</style>
