<template>
  <div class="write-page">
    <div class="page-header">
      <div>
        <span class="page-kicker">{{ isEdit ? t('editBadge') : t('writeBadge') }}</span>
        <h2 class="page-title">{{ isEdit ? t('editTitle') : t('writeTitle') }}</h2>
      </div>
      <div class="write-status">
        <span>{{ draftState }}</span>
        <span>{{ contentStats.words }} {{ t('words') }}</span>
        <span>{{ contentStats.readingMinutes }} {{ t('readingUnit') }}</span>
      </div>
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
                {{ a.isPublished ? t('unpublish') : t('publish') }}
              </button>
              <button class="item-btn item-btn-danger" :title="t('delete')" @click.stop="handleDelete(a.id)">
                {{ t('delete') }}
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
          <input ref="coverInput" type="file" accept="image/*" hidden @change="(e) => onFilePicked(e, 16/9)" />
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
              <input
                v-if="showNewTagInput"
                v-model="newTagName"
                type="text"
                class="new-tag-input"
                placeholder="输入新标签"
                maxlength="20"
                @keydown.enter.prevent="confirmNewTag"
                @blur="confirmNewTag"
                ref="newTagInputRef"
              />
              <button v-else class="tag-btn add-tag" @click="startNewTag">+ 新建</button>
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
            URL
          </button>
          <button class="tool-btn" @click="triggerImageUpload" title="Image">
            IMG
          </button>
          <button class="tool-btn" @click="insertFormat('\n```\n', '\n```\n')" title="Code Block">
            { }
          </button>
          <input ref="imageInput" type="file" accept="image/*" hidden @change="(e) => onFilePicked(e, 0)" />
        </div>

        <div class="writing-console" aria-label="writing console">
          <div class="writing-metrics">
            <span><strong>{{ contentStats.characters }}</strong>{{ t('characters') }}</span>
            <span><strong>{{ contentStats.headings }}</strong>{{ t('headings') }}</span>
            <span><strong>{{ contentStats.images }}</strong>{{ t('images') }}</span>
          </div>
          <div class="template-strip">
            <span>{{ t('templateLabel') }}</span>
            <button
              v-for="template in writingTemplates"
              :key="template.key"
              type="button"
              @click="insertTemplate(template)"
            >
              {{ template.name }}
            </button>
          </div>
        </div>

        <!-- 内容编辑：左编辑右预览 -->
        <div class="editor-container">
          <div class="editor-pane">
            <div class="pane-label">{{ t('markdownLabel') }}</div>
            <textarea
              ref="contentTextarea"
              v-model="article.content"
              :placeholder="t('contentPlaceholder')"
              class="content-textarea"
            ></textarea>
          </div>
          <div class="preview-pane">
            <div class="pane-label">{{ t('previewLabel') }}</div>
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
              <span class="file-icon">DOC</span>
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
    <!-- 裁剪对话框 -->
    <CropDialog
      v-if="showCrop"
      :imageFile="cropFile"
      :aspectRatio="cropAspect || undefined"
      @crop="onCropDone"
      @cancel="showCrop = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, inject, onMounted, watch, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getArticleList, getArticleDetail, createArticle, updateArticle, deleteArticle, getMyArticles } from '../api/article'
import { getCategoryList } from '../api/category'
import { getTagList, addTag } from '../api/tag'
import { uploadImage, uploadAttachment } from '../api/upload'
import CropDialog from '../components/CropDialog.vue'
import 'highlight.js/styles/github-dark.css'

const route = useRoute()
const userStore = useUserStore()

const lang = inject('siteLanguage', ref(localStorage.getItem('ethan-language') || 'zh'))

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
    ,
    writeBadge: 'WRITE LAB',
    editBadge: 'EDIT LAB',
    autosave: '自动草稿',
    splitView: '双栏预览',
    markdownLabel: 'Markdown',
    previewLabel: '预览',
    words: '词',
    readingUnit: '分钟阅读',
    characters: '字',
    headings: '标题',
    images: '图片',
    draftStandby: '草稿待命',
    draftSaved: '草稿已保存',
    templateLabel: '内容模板',
    templateTech: '技术笔记',
    templateProject: '项目复盘',
    templateAi: 'AI 学习日志',
    previewEmpty: '预览区域',
    previewLoading: '正在加载 Markdown 引擎...',
    previewFailed: '预览渲染失败，请检查 Markdown 内容。'
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
    delete: 'Delete',
    writeBadge: 'WRITE LAB',
    editBadge: 'EDIT LAB',
    autosave: 'Auto Draft',
    splitView: 'Split Preview',
    markdownLabel: 'Markdown',
    previewLabel: 'Preview',
    words: 'words',
    readingUnit: 'min read',
    characters: 'chars',
    headings: 'headings',
    images: 'images',
    draftStandby: 'Draft ready',
    draftSaved: 'Draft saved',
    templateLabel: 'Templates',
    templateTech: 'Tech Note',
    templateProject: 'Project Review',
    templateAi: 'AI Learning Log',
    previewEmpty: 'Preview area',
    previewLoading: 'Loading Markdown engine...',
    previewFailed: 'Preview render failed. Check the Markdown content.'
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
const showNewTagInput = ref(false)
const newTagName = ref('')
const renderedContent = ref('')
const draftSavedAt = ref(null)

const coverInput = ref(null)
const imageInput = ref(null)
const attachmentInput = ref(null)
const contentTextarea = ref(null)
const newTagInputRef = ref(null)

// 裁剪对话框
const showCrop = ref(false)
const cropFile = ref(null)
const cropAspect = ref(16/9) // cover=16:9, inline=free

const onFilePicked = (e, aspect = 16/9) => {
  const file = e.target.files[0]
  if (!file) return
  cropFile.value = file
  cropAspect.value = aspect
  showCrop.value = true
  e.target.value = '' // reset input
}

const onCropDone = async (croppedFile) => {
  showCrop.value = false
  try {
    const res = await uploadImage(croppedFile)
    if (res.code === 200 && res.data) {
      if (cropAspect.value === 16/9) {
        // 封面
        article.value.cover = resolveUploadUrl(res.data)
      } else {
        // 正文图片
        const markdown = `![${croppedFile.name}](${resolveUploadUrl(res.data)})`
        insertAtCursor(markdown)
      }
    } else {
      alert('上传失败: ' + (res.message || '未知错误'))
    }
  } catch (err) {
    console.error('上传失败:', err)
    alert('上传失败，请重试')
  }
}

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/api\/?$/, '')

const resolveUploadUrl = (url) => {
  if (!url) return ''
  if (/^https?:\/\//.test(url)) return url
  if (url.startsWith('/upload/') || url.startsWith('/uploads/')) {
    return `${apiBaseUrl}${url}`
  }
  return url
}

const insertTemplate = (template) => {
  const templateContent = template.content.trim()
  if (!article.value.title.trim()) article.value.title = template.title
  if (!article.value.summary.trim()) article.value.summary = template.summary

  if (!article.value.content.trim()) {
    article.value.content = templateContent
    setTimeout(() => contentTextarea.value?.focus(), 0)
    return
  }

  insertAtCursor(`\n\n${templateContent}`)
}

const isEdit = computed(() => !!editingId.value)

const emptyPreviewHtml = computed(() => `<p class="preview-empty">${t('previewEmpty')}</p>`)

const contentStats = computed(() => {
  const content = article.value.content || ''
  const plain = content
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/!\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/[#>*_`~\-[\]()!|]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
  const chineseChars = (plain.match(/[\u4e00-\u9fa5]/g) || []).length
  const latinWords = (plain.replace(/[\u4e00-\u9fa5]/g, ' ').match(/[A-Za-z0-9]+(?:[-_][A-Za-z0-9]+)*/g) || []).length
  const words = chineseChars + latinWords

  return {
    characters: content.replace(/\s/g, '').length,
    words,
    readingMinutes: Math.max(1, Math.ceil(words / 350)),
    headings: (content.match(/^#{1,6}\s+/gm) || []).length,
    images: (content.match(/!\[[^\]]*]\(/g) || []).length
  }
})

const draftState = computed(() => {
  if (!draftSavedAt.value) return t('draftStandby')
  return `${t('draftSaved')} ${new Date(draftSavedAt.value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`
})

const writingTemplates = computed(() => {
  if (lang.value === 'en') {
    return [
      {
        key: 'tech',
        name: t('templateTech'),
        title: 'Technical Note',
        summary: 'A focused note about one technical problem, implementation path, and final takeaway.',
        content: '## Background\n\nWhat problem did I run into, and why does it matter?\n\n## Approach\n\n- Key idea:\n- Implementation detail:\n- Trade-off:\n\n## Result\n\nWhat changed after this solution?\n\n## Reflection\n\nWhat should I remember next time?'
      },
      {
        key: 'project',
        name: t('templateProject'),
        title: 'Project Review',
        summary: 'A project review covering motivation, architecture, features, issues, and next steps.',
        content: '## Project Goal\n\nWhat is this project trying to solve?\n\n## Architecture\n\n- Frontend:\n- Backend:\n- Data:\n\n## Core Features\n\n1. \n2. \n3. \n\n## Problems And Fixes\n\nWhat was difficult, and how did I handle it?\n\n## Next Step\n\nWhat will be improved next?'
      },
      {
        key: 'ai',
        name: t('templateAi'),
        title: 'AI Learning Log',
        summary: 'A learning log about AI concepts, experiments, observations, and future questions.',
        content: '## Learning Topic\n\nToday I focused on...\n\n## Key Concepts\n\n- Concept 1:\n- Concept 2:\n- Concept 3:\n\n## Experiment\n\nWhat did I try, and what did I observe?\n\n## Notes\n\nWhat is still unclear?\n\n## Next Question\n\nWhat should I explore next?'
      }
    ]
  }

  return [
    {
      key: 'tech',
      name: t('templateTech'),
      title: '技术笔记',
      summary: '围绕一个技术问题，记录背景、实现路径、取舍和复盘结论。',
      content: '## 背景\n\n这次遇到的问题是什么？为什么值得记录？\n\n## 解决思路\n\n- 核心思路：\n- 实现细节：\n- 技术取舍：\n\n## 最终效果\n\n这个方案解决了什么？还有哪些限制？\n\n## 复盘\n\n下次遇到类似问题，我应该注意什么？'
    },
    {
      key: 'project',
      name: t('templateProject'),
      title: '项目复盘',
      summary: '记录项目目标、架构、核心功能、问题处理和下一步优化方向。',
      content: '## 项目目标\n\n这个项目要解决什么问题？面向谁使用？\n\n## 技术架构\n\n- 前端：\n- 后端：\n- 数据：\n\n## 核心功能\n\n1. \n2. \n3. \n\n## 问题与解决\n\n开发中遇到了什么问题？最后怎么处理？\n\n## 下一步\n\n后续准备如何优化？'
    },
    {
      key: 'ai',
      name: t('templateAi'),
      title: 'AI 学习日志',
      summary: '记录 AI 方向的概念理解、实验过程、观察结果和下一步问题。',
      content: '## 学习主题\n\n今天主要学习了什么？\n\n## 关键概念\n\n- 概念 1：\n- 概念 2：\n- 概念 3：\n\n## 实验记录\n\n我尝试了什么？观察到了什么现象？\n\n## 思考\n\n哪些地方还没有完全理解？\n\n## 下一步问题\n\n接下来要继续验证什么？'
    }
  ]
})

let markdownEnginePromise = null
let renderTimer = null
let renderToken = 0

const loadMarkdownEngine = async () => {
  if (!markdownEnginePromise) {
    markdownEnginePromise = (async () => {
      const [
        markedModule,
        highlightCore,
        javascript,
        typescript,
        xml,
        css,
        bash,
        python,
        java,
        cpp,
        json,
        markdown,
        sql
      ] = await Promise.all([
        import('marked'),
        import('highlight.js/lib/core'),
        import('highlight.js/lib/languages/javascript'),
        import('highlight.js/lib/languages/typescript'),
        import('highlight.js/lib/languages/xml'),
        import('highlight.js/lib/languages/css'),
        import('highlight.js/lib/languages/bash'),
        import('highlight.js/lib/languages/python'),
        import('highlight.js/lib/languages/java'),
        import('highlight.js/lib/languages/cpp'),
        import('highlight.js/lib/languages/json'),
        import('highlight.js/lib/languages/markdown'),
        import('highlight.js/lib/languages/sql')
      ])

      const hljs = highlightCore.default || highlightCore
      const languages = {
        javascript,
        js: javascript,
        typescript,
        ts: typescript,
        html: xml,
        xml,
        css,
        bash,
        shell: bash,
        sh: bash,
        python,
        py: python,
        java,
        cpp,
        c: cpp,
        json,
        markdown,
        md: markdown,
        sql
      }

      Object.entries(languages).forEach(([name, mod]) => {
        try {
          hljs.registerLanguage(name, mod.default || mod)
        } catch {}
      })

      const marked = markedModule.marked
      marked.setOptions({
        breaks: true,
        gfm: true,
        highlight: (code, language) => {
          const langName = language && hljs.getLanguage(language) ? language : ''
          try {
            return langName
              ? hljs.highlight(code, { language: langName }).value
              : hljs.highlightAuto(code).value
          } catch {
            return code
          }
        }
      })

      return { marked }
    })()
  }

  return markdownEnginePromise
}

const renderMarkdownNow = async (content = article.value.content) => {
  const currentToken = ++renderToken
  if (!content.trim()) {
    renderedContent.value = emptyPreviewHtml.value
    return
  }

  renderedContent.value = `<p class="preview-loading">${t('previewLoading')}</p>`
  try {
    const { marked } = await loadMarkdownEngine()
    if (currentToken !== renderToken) return
    renderedContent.value = marked.parse(content)
  } catch (error) {
    console.error('Markdown preview failed:', error)
    if (currentToken === renderToken) {
      renderedContent.value = `<p class="preview-error">${t('previewFailed')}</p>`
    }
  }
}

const scheduleRender = () => {
  clearTimeout(renderTimer)
  renderTimer = setTimeout(() => {
    renderMarkdownNow()
  }, 180)
}

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
      article.value.cover = resolveUploadUrl(res.data)
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
      const markdown = `![${file.name}](${resolveUploadUrl(res.data)})`
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
          url: resolveUploadUrl(res.data)
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

const startNewTag = () => {
  showNewTagInput.value = true
  newTagName.value = ''
  setTimeout(() => newTagInputRef.value?.focus(), 0)
}

const confirmNewTag = async () => {
  const name = newTagName.value.trim()
  showNewTagInput.value = false
  if (!name) return
  const exist = tags.value.find(t => t.name === name)
  if (exist) {
    if (!selectedTagIds.value.includes(exist.id)) {
      selectedTagIds.value.push(exist.id)
    }
    return
  }
  try {
    const res = await addTag({ name })
    if (res.code === 200 || res.code === 0) {
      const newTag = res.data || { id: Date.now(), name }
      tags.value.push(newTag)
      selectedTagIds.value.push(newTag.id)
    } else {
      alert('创建标签失败：' + (res.message || '未知错误'))
    }
  } catch (e) {
    console.error('创建标签失败:', e)
    alert('创建标签失败：' + (e?.message || '网络错误'))
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
  const token = localStorage.getItem('token')
  if (!token) return // 没登录就不调接口，避免控制台报错
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
      clearDraft()
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

// Ctrl+S 快捷键
const handleKeydown = (e) => {
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault()
    saveDraft()
  }
}

// 自动保存草稿到 localStorage
const AUTO_DRAFT_KEY = 'blog_write_draft'
let draftTimer = null

const autoSaveDraft = () => {
  clearTimeout(draftTimer)
  draftTimer = setTimeout(() => {
    const draft = {
      title: article.value.title,
      content: article.value.content,
      summary: article.value.summary,
      cover: article.value.cover,
      categoryId: article.value.categoryId,
      selectedTagIds: selectedTagIds.value,
      timestamp: Date.now()
    }
    localStorage.setItem(AUTO_DRAFT_KEY, JSON.stringify(draft))
    draftSavedAt.value = Date.now()
  }, 1500)
}

watch(() => article.value.content, () => {
  autoSaveDraft()
  scheduleRender()
})
watch(() => article.value.title, autoSaveDraft)
watch(lang, () => {
  renderMarkdownNow()
})

const clearDraft = () => localStorage.removeItem(AUTO_DRAFT_KEY)

onMounted(async () => {
  renderedContent.value = emptyPreviewHtml.value
  await Promise.all([loadCategories(), loadTags(), loadMyArticles()])
  const id = route.params.id
  if (id) {
    editingId.value = Number(id)
    await loadArticleDetail(Number(id))
  } else {
    // 尝试恢复草稿
    try {
      const saved = localStorage.getItem(AUTO_DRAFT_KEY)
      if (saved) {
        const draft = JSON.parse(saved)
        if (draft.timestamp && Date.now() - draft.timestamp < 86400000) {
          if (draft.title || draft.content) {
            if (confirm('检测到未保存的草稿（' + new Date(draft.timestamp).toLocaleString() + '），是否恢复？')) {
              article.value.title = draft.title || ''
              article.value.content = draft.content || ''
              article.value.summary = draft.summary || ''
              article.value.cover = draft.cover || ''
              article.value.categoryId = draft.categoryId || null
              selectedTagIds.value = draft.selectedTagIds || []
            } else {
              clearDraft()
            }
          }
        }
      }
    } catch {}
  }
  renderMarkdownNow()
  document.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown)
  clearTimeout(draftTimer)
  clearTimeout(renderTimer)
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
}

.item-btn {
  background: var(--bg);
  border: 1px solid var(--border);
  cursor: pointer;
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 4px;
  color: var(--text);
  transition: all 0.15s;
  white-space: nowrap;
}

.item-btn:hover {
  background: var(--accent);
  color: #fff;
  border-color: var(--accent);
}

.item-btn-danger {
  color: #e74c3c;
  border-color: #e74c3c33;
  background: #e74c3c0a;
}

.item-btn-danger:hover {
  background: #e74c3c;
  color: #fff;
  border-color: #e74c3c;
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

.tag-btn.add-tag {
  border-style: dashed;
  opacity: 0.8;
}

.new-tag-input {
  padding: 6px 10px;
  background: var(--bg);
  border: 1px solid var(--accent);
  border-radius: 20px;
  color: var(--text);
  font-size: 12px;
  outline: none;
  width: 100px;
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
  background: #0d1117;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 12px 0;
  border: 1px solid rgba(255,255,255,0.06);
}

.preview-content :deep(code) {
  font-family: 'JetBrains Mono', 'Consolas', monospace;
  font-size: 13px;
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
  background: rgba(147, 197, 253, 0.14);
  color: var(--ink-blue);
}
.item-status.draft {
  background: rgba(255, 189, 102, 0.14);
  color: var(--amber);
}

/* Future writing console polish */
.write-page {
  max-width: 1380px;
  padding: 18px clamp(14px, 3vw, 34px) 40px;
}

.page-header {
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
  padding: 18px 0 22px;
  border-bottom: 1px solid rgba(147, 197, 253, 0.18);
}

.page-header::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 1px;
  background: linear-gradient(90deg, rgba(96, 165, 250, 0.72), rgba(167, 139, 250, 0.34), transparent);
  box-shadow: 0 0 18px rgba(96, 165, 250, 0.22);
}

.page-kicker,
.write-status span {
  color: rgba(147, 197, 253, 0.72);
  font: 800 11px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.16em;
}

.page-title {
  margin: 8px 0 0;
  color: #f8fbff;
  font-size: clamp(2rem, 4vw, 4.4rem);
  font-weight: 950;
  line-height: 0.95;
  letter-spacing: 0;
  text-shadow: 0 0 30px rgba(96, 165, 250, 0.18);
}

.write-status {
  display: inline-flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.write-status span {
  padding: 8px 10px;
  border: 1px solid rgba(147, 197, 253, 0.2);
  border-radius: 7px;
  background: rgba(6, 12, 28, 0.56);
  color: rgba(226, 239, 255, 0.62);
}

.editor-layout {
  display: grid;
  grid-template-columns: minmax(230px, 280px) minmax(0, 1fr);
  gap: clamp(16px, 2vw, 24px);
  align-items: start;
}

.article-sidebar,
.editor-main {
  border: 1px solid rgba(147, 197, 253, 0.18);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(11, 20, 40, 0.86), rgba(5, 9, 22, 0.82)),
    repeating-linear-gradient(90deg, rgba(147, 197, 253, 0.04) 0 1px, transparent 1px 84px);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.26), inset 0 0 70px rgba(96, 165, 250, 0.035);
}

.article-sidebar {
  width: auto;
  position: sticky;
  top: 112px;
  padding: 18px;
}

.sidebar-heading {
  color: rgba(226, 239, 255, 0.66);
  border-bottom-color: rgba(147, 197, 253, 0.16);
}

.article-item {
  align-items: flex-start;
  gap: 8px;
  padding: 12px 10px;
  border: 1px solid transparent;
  border-radius: 7px;
}

.article-item:hover,
.article-item.active {
  background: rgba(96, 165, 250, 0.06);
  border-color: rgba(96, 165, 250, 0.18);
}

.item-title {
  white-space: normal;
  line-height: 1.5;
}

.item-actions {
  flex-direction: column;
}

.item-btn,
.btn-new,
.attach-btn,
.btn,
.tag-btn {
  border-radius: 7px;
}

.editor-main {
  padding: clamp(20px, 3vw, 34px);
}

.cover-upload,
.cover-preview {
  max-width: none;
  height: 150px;
  border-radius: 8px;
}

.cover-upload {
  border: 1px dashed rgba(147, 197, 253, 0.28);
  background:
    radial-gradient(circle at 20% 30%, rgba(96, 165, 250, 0.12), transparent 34%),
    rgba(6, 12, 28, 0.54);
}

.cover-upload:hover {
  border-color: rgba(96, 165, 250, 0.58);
  color: #eaf7ff;
}

.title-input {
  padding: 18px 0;
  font-size: clamp(1.55rem, 3vw, 2.6rem);
  border-bottom-color: rgba(147, 197, 253, 0.18);
}

.meta-field select,
.new-tag-input,
.content-textarea,
.preview-content,
.summary-textarea,
.attachment-item {
  border-color: rgba(147, 197, 253, 0.16);
  background: rgba(3, 8, 20, 0.56);
}

.toolbar {
  gap: 6px;
  padding: 10px;
  border-color: rgba(147, 197, 253, 0.18);
  border-radius: 8px;
  background: rgba(5, 11, 25, 0.74);
}

.tool-btn {
  width: 34px;
  height: 34px;
  border: 1px solid rgba(147, 197, 253, 0.12);
  border-radius: 7px;
  color: rgba(226, 239, 255, 0.66);
}

.tool-btn:hover {
  color: #ffffff;
  border-color: rgba(96, 165, 250, 0.42);
  background: rgba(96, 165, 250, 0.08);
}

.writing-console {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin: 0 0 16px;
  padding: 12px;
  border: 1px solid rgba(147, 197, 253, 0.14);
  border-radius: 8px;
  background:
    linear-gradient(90deg, rgba(96, 165, 250, 0.055), transparent 54%),
    rgba(5, 10, 23, 0.42);
}

.writing-metrics,
.template-strip {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.writing-metrics span,
.template-strip span,
.template-strip button {
  min-height: 30px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 0 10px;
  border: 1px solid rgba(147, 197, 253, 0.14);
  border-radius: 7px;
  background: rgba(3, 8, 20, 0.42);
  color: rgba(226, 239, 255, 0.64);
  font: 800 10px/1 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.08em;
  white-space: nowrap;
}

.writing-metrics strong {
  color: #bfdbfe;
  font-size: 13px;
}

.template-strip span {
  border-color: transparent;
  background: transparent;
  color: rgba(147, 197, 253, 0.64);
}

.template-strip button {
  cursor: pointer;
  transition: transform 0.18s var(--ease-out), border-color 0.18s, color 0.18s, background 0.18s;
}

.template-strip button:hover {
  transform: translateY(-1px);
  border-color: rgba(96, 165, 250, 0.42);
  color: #f8fbff;
  background: rgba(96, 165, 250, 0.08);
}

.editor-container {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 16px;
}

.pane-label {
  color: rgba(147, 197, 253, 0.74);
  font-weight: 800;
}

.content-textarea,
.preview-content {
  min-height: 470px;
  border-radius: 8px;
  box-shadow: inset 0 0 42px rgba(96, 165, 250, 0.025);
}

.preview-content :deep(.preview-empty),
.preview-content :deep(.preview-loading),
.preview-content :deep(.preview-error) {
  min-height: 220px;
  display: grid;
  place-items: center;
  margin: 0;
  border: 1px dashed rgba(147, 197, 253, 0.16);
  border-radius: 8px;
  color: rgba(226, 239, 255, 0.48);
  font: 800 12px/1.6 'SF Mono', 'Consolas', monospace;
  letter-spacing: 0.1em;
  background:
    linear-gradient(90deg, transparent, rgba(96, 165, 250, 0.045), transparent),
    rgba(3, 8, 20, 0.32);
}

.preview-content :deep(.preview-loading) {
  color: rgba(191, 219, 254, 0.68);
}

.preview-content :deep(.preview-error) {
  color: rgba(248, 113, 113, 0.86);
  border-color: rgba(248, 113, 113, 0.24);
}

.content-textarea:focus,
.summary-textarea:focus,
.meta-field select:focus {
  border-color: rgba(96, 165, 250, 0.56);
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.08);
}

.action-bar {
  border-top-color: rgba(147, 197, 253, 0.16);
}

.btn-primary {
  border-color: rgba(96, 165, 250, 0.72);
  background: linear-gradient(135deg, #60a5fa, #8b5cf6);
  color: #06101f;
  font-weight: 850;
}

.btn-primary:hover {
  border-color: #60a5fa;
  background: linear-gradient(135deg, #6ffaff, #a78bfa);
}

.btn-ghost:hover,
.attach-btn:hover,
.btn-new:hover,
.tag-btn:hover {
  border-color: rgba(96, 165, 250, 0.44);
  color: #eaf7ff;
}

@media (max-width: 980px) {
  .editor-layout,
  .editor-container {
    grid-template-columns: 1fr;
  }

  .article-sidebar {
    position: relative;
    top: auto;
  }
}

@media (max-width: 640px) {
  .write-page {
    padding: 8px 12px 36px;
  }

  .page-header,
  .meta-row,
  .action-bar {
    align-items: stretch;
    flex-direction: column;
  }

  .write-status {
    justify-content: flex-start;
  }

  .editor-main {
    padding: 16px;
  }

  .toolbar {
    overflow-x: auto;
  }

  .writing-console {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
