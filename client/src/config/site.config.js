/**
 * Site configuration and static fallback content.
 *
 * GitHub Pages can only serve static files, so the public-facing pages use
 * this data whenever the Spring Boot API is not available.
 */
export const resolveAssetUrl = (path = '') => {
  if (!path) return ''
  if (/^[a-z][a-z\d+.-]*:|#/i.test(path)) return path
  const base = (import.meta.env.BASE_URL || '/').replace(/\/$/, '')
  return `${base}/${path.replace(/^\//, '')}`
}

export const isStaticMode = import.meta.env.VITE_STATIC_MODE === 'true'

const avatar = resolveAssetUrl('/photos/avatar-profile.jpg')
const photos = [
  resolveAssetUrl('/photos/hero-tech-01.png'),
  resolveAssetUrl('/photos/hero-tech-02.png'),
  resolveAssetUrl('/photos/hero-tech-03.png'),
  resolveAssetUrl('/photos/hero-tech-04.png'),
]

const socials = [
  {
    name: 'GitHub',
    handle: 'EthanZhao02',
    url: 'https://github.com/EthanZhao02',
    color: '#38bdf8',
    icon: 'github',
  },
  {
    name: 'B站',
    handle: '1671173639',
    url: 'https://space.bilibili.com/1671173639?spm_id_from=333.1007.0.0',
    color: '#00a1d6',
    icon: 'bilibili',
  },
  {
    name: '抖音',
    handle: '扫码关注',
    url: '#douyin-qr',
    color: '#fe2c55',
    icon: 'douyin',
    isQRCode: true,
    qrCodeUrl: '/photos/douyin-qr.png',
  },
  {
    name: '小红书',
    handle: '扫码关注',
    url: '#xiaohongshu-qr',
    color: '#ff2442',
    icon: 'xiaohongshu',
    isQRCode: true,
    qrCodeUrl: '/photos/xiaohongshu-qr.png',
  },
  {
    name: '稀土掘金',
    handle: '2355568709733891',
    url: 'https://juejin.cn/user/2355568709733891',
    color: '#1e80ff',
    icon: 'juejin',
  },
  {
    name: '力扣',
    handle: 'ethan-517',
    url: 'https://leetcode.cn/u/ethan-517/',
    color: '#ffa116',
    icon: 'leetcode',
  },
  {
    name: '语雀',
    handle: 'Ethan的知识库',
    url: 'https://www.yuque.com/ethan-nohpo/ak5789',
    color: '#00b96b',
    icon: 'yuque',
  },
  {
    name: '微信公众号',
    handle: '扫码关注',
    url: '#wechat-qr',
    color: '#22c55e',
    icon: 'wechat',
    isQRCode: true,
    qrCodeUrl: '/photos/wechat-qr.png',
  },
  {
    name: 'QQ',
    handle: '2317733506',
    url: '#qq-qr',
    color: '#22d3ee',
    icon: 'qq',
    isQRCode: true,
    qrCodeUrl: '/photos/qq-qr.png',
  },
  {
    name: '邮箱',
    handle: 'ethan912yydegree@gmail.com',
    url: 'mailto:ethan912yydegree@gmail.com',
    color: '#7dd3fc',
    icon: 'email',
  },
  {
    name: 'CSDN',
    handle: 'm0_60650009',
    url: 'https://blog.csdn.net/m0_60650009?type=blog',
    color: '#ff5a33',
    icon: 'csdn',
  },
  {
    name: '知乎',
    handle: '84-27-88-27-17',
    url: 'https://www.zhihu.com/people/84-27-88-27-17',
    color: '#1772f6',
    icon: 'zhihu',
  },
]

const categories = [
  { id: 1, name: 'Tech 技术', sort: 1 },
  { id: 2, name: 'Project 项目', sort: 2 },
  { id: 3, name: 'AI 人工智能', sort: 3 },
  { id: 4, name: 'Learning 学习', sort: 4 },
  { id: 5, name: 'Essay 随笔', sort: 5 },
]

const tags = [
  { id: 1, name: 'Vue3' },
  { id: 2, name: 'Spring Boot' },
  { id: 3, name: 'MySQL' },
  { id: 4, name: 'GitHub Pages' },
  { id: 5, name: 'AI' },
  { id: 6, name: 'Portfolio' },
  { id: 7, name: '考研复试' },
  { id: 8, name: '知识管理' },
]

const articles = [
  {
    id: 1,
    title: 'Ethan Nexus：把个人博客升级成知识与项目档案',
    summary: '这次改版把首页从普通文章入口升级为面向复试、求职和长期成长记录的个人技术枢纽。',
    content: `# Ethan Nexus：把个人博客升级成知识与项目档案

这个站点不只承担生活记录，更重要的是成为一个长期可访问的个人数字空间：展示项目、技术文章、学习路线、研究兴趣和成长时间线。

## 为什么要这样改

复试老师或面试官进入网站时，最需要快速看到三个问题：我是谁、我做过什么、我正在往哪个方向积累。首页因此采用作品集优先的信息架构，把文章列表放到后面，把项目和学习轨迹提前。

## 当前定位

- Web Developer / AI Learner / Knowledge System Builder
- 关注 Web 全栈实践、智能学习工具和知识管理
- 用项目和笔记记录持续成长

## 后续计划

继续补充真实项目复盘、专业课学习笔记、英文表达训练、算法题记录和研究兴趣整理，让这个站点既有个人表达，也能作为正式展示入口。`,
    categoryId: 2,
    categoryName: 'Project 项目',
    authorName: 'Ethan',
    tags: [
      { id: 4, name: 'GitHub Pages' },
      { id: 6, name: 'Portfolio' },
      { id: 7, name: '考研复试' },
    ],
    viewCount: 168,
    createTime: '2026-07-03T10:20:00',
  },
  {
    id: 2,
    title: 'Vue3 + Spring Boot 个人博客项目结构说明',
    summary: '梳理前端、后端、数据库和静态部署模式的职责边界，以及登录功能真正可用的前提。',
    content: `# Vue3 + Spring Boot 个人博客项目结构说明

项目主要分为三个部分：client、server 和 database。

## client

前端使用 Vue3、Vue Router、Pinia 和 Vite。首页、文章、项目、友链、留言板、关于页和写文章页面都在这里实现。

## server

后端使用 Spring Boot，负责文章、评论、留言、友链、用户登录、上传等接口。登录功能依赖后端在线运行，并且数据库中要存在用户数据。

## database

database/init.sql 是初始化 MySQL 数据的脚本，包含默认管理员账号 admin / 123456。线上如果只部署 GitHub Pages，站内账号登录不会生效；如果要启用账号登录，需要单独部署后端和 MySQL，并设置 VITE_API_BASE_URL。`,
    categoryId: 1,
    categoryName: 'Tech 技术',
    authorName: 'Ethan',
    tags: [
      { id: 1, name: 'Vue3' },
      { id: 2, name: 'Spring Boot' },
      { id: 3, name: 'MySQL' },
    ],
    viewCount: 132,
    createTime: '2026-07-02T20:15:00',
  },
  {
    id: 3,
    title: '学习档案：数学、英语、计算机与 AI 的长期路线',
    summary: '把复试准备、专业基础和 AI/Web 项目积累拆成可持续更新的学习轨道。',
    content: `# 学习档案：数学、英语、计算机与 AI 的长期路线

这个栏目会记录我对数学、英语、计算机基础、算法训练和 AI 实践的阶段性总结。

## 记录方式

- 数学：概念、题型、错题和复盘
- 英语：阅读、听说表达和专业英文
- 计算机：数据结构、操作系统、网络、数据库
- AI/Web：模型工具、知识管理、工程实践

目标不是堆砌碎片，而是形成能被回看、复用和展示的知识档案。`,
    categoryId: 4,
    categoryName: 'Learning 学习',
    authorName: 'Ethan',
    tags: [
      { id: 5, name: 'AI' },
      { id: 7, name: '考研复试' },
      { id: 8, name: '知识管理' },
    ],
    viewCount: 96,
    createTime: '2026-06-28T18:00:00',
  },
]

const projects = [
  {
    id: 1,
    name: 'Ethan Nexus',
    description: '面向复试、求职和长期成长记录的个人技术博客，整合项目、文章、学习路线、研究兴趣和联系入口。',
    icon: '◇',
    tag: 'Portfolio',
    techStack: 'Vue3, Vite, Spring Boot, MySQL, GitHub Pages',
    category: 'web',
    status: '已上线',
    isOpenSource: 1,
    isActive: 1,
    url: 'https://ethanzhao02.github.io/personal-blog/',
    githubUrl: 'https://github.com/EthanZhao02/personal-blog',
    stats: 'Portfolio Blog',
    features: ['未来科技风首页', '静态内容兜底', 'Giscus 留言', 'JWT 登录后端'],
    createTime: '2026-07-03T10:00:00',
  },
  {
    id: 2,
    name: 'NLP Code Assistant',
    description: '围绕代码审查、可信验证、记忆增强和多模型协作展开的 NLP/AI 工程实践项目。',
    icon: '⌘',
    tag: 'AI 工程',
    techStack: 'Python, FastAPI, Vue3, JavaScript, Markdown',
    category: 'ai',
    status: '持续完善',
    isOpenSource: 1,
    isActive: 1,
    url: '#',
    githubUrl: 'https://github.com/EthanZhao02/nlp-code-assistant',
    stats: 'AI + Code Review',
    features: ['代码审查流程', '实验记录', '项目验收材料', '知识库沉淀'],
    createTime: '2026-06-29T10:00:00',
  },
  {
    id: 3,
    name: 'Knowledge Learning Archive',
    description: '把英语、数学、计算机和 AI 学习记录整理为可复盘、可展示、可长期维护的知识档案。',
    icon: '◎',
    tag: 'Knowledge',
    techStack: 'Markdown, GitHub Pages, Obsidian, AI Workflow',
    category: 'knowledge',
    status: '规划中',
    isOpenSource: 0,
    isActive: 1,
    url: '#',
    stats: 'Learning System',
    features: ['学习路线', '阶段复盘', '专业课笔记', '复试材料'],
    createTime: '2026-06-20T10:00:00',
  },
]

const learningTracks = [
  {
    title: 'Computer Science',
    description: '数据结构、操作系统、网络、数据库和工程化基础。',
    progress: 68,
  },
  {
    title: 'AI Engineering',
    description: 'NLP、代码智能、模型工具链和可信评测。',
    progress: 62,
  },
  {
    title: 'English & Interview',
    description: '英语听说表达、项目介绍和复试问答。',
    progress: 55,
  },
]

const researchInterests = [
  'AI-assisted code review',
  'Knowledge management',
  'Learning system design',
  'Web full-stack engineering',
]

const timeline = [
  {
    time: '2026',
    title: 'Ethan Nexus 改版',
    text: '把博客重构为作品集、学习档案与工程实践入口。',
  },
  {
    time: '2026',
    title: 'NLP 代码审查项目',
    text: '沉淀毕业设计、实验记录、验收材料和工程复盘。',
  },
  {
    time: 'Next',
    title: '复试与求职材料',
    text: '补齐项目 README、在线 Demo、简历 PDF 和研究兴趣页。',
  },
]

const friends = [
  {
    id: 1,
    name: 'Souta',
    description: '技术博客与个人站点参考',
    avatar: 'https://api.dicebear.com/7.x/identicon/svg?seed=souta',
    url: 'https://souta.cc',
    category: 'tech',
    isActive: 1,
  },
  {
    id: 2,
    name: '理想之 clover',
    description: '关于 AI 与技术的思考',
    avatar: 'https://api.dicebear.com/7.x/identicon/svg?seed=clover',
    url: 'https://idealclover.top',
    category: 'tech',
    isActive: 1,
  },
  {
    id: 3,
    name: 'qwqwq',
    description: '有趣的项目与想法',
    avatar: 'https://api.dicebear.com/7.x/identicon/svg?seed=qwqwq',
    url: 'https://qwqwq.com',
    category: 'other',
    isActive: 1,
  },
]

const messages = [
  {
    id: 1,
    nickname: 'Ethan',
    avatar,
    content: '欢迎来到 Ethan Nexus。这里是一面便签墙，可以留下建议、问候或项目反馈。',
    createTime: '2026-07-03T10:00:00',
  },
  {
    id: 2,
    nickname: 'Future Visitor',
    avatar: '',
    content: '便签会像一张张小纸片贴在这里；后端在线时会进入真实留言数据。',
    createTime: '2026-07-04T21:20:00',
  },
  {
    id: 3,
    nickname: 'AI Channel',
    avatar: '',
    content: '友链申请现在可以直接去友链页提交，不用再绕到留言板。',
    createTime: '2026-07-05T09:30:00',
  },
]

const comments = {
  1: [
    {
      id: 1,
      nickname: 'Ethan',
      userAvatar: avatar,
      content: '这一版先把首页定位和静态展示做好；后端部署后，站内登录与写文章会切回真实接口。',
      createTime: '2026-07-03T10:20:00',
      children: [],
    },
  ],
  2: [],
  3: [],
}

export const siteConfig = {
  name: 'Ethan Zhao',
  shortName: 'Ethan',
  avatar,
  role: 'Web Developer / AI Learner / Knowledge System Builder',
  subtitle: 'Recording projects, notes, and learning routes in one personal archive.',
  positioning: '一个记录 Web、AI 学习与长期成长的个人技术博客',
  photos,
  socials,
  projects,
  friends,
  learningTracks,
  researchInterests,
  timeline,
  about: {
    tagline: 'Web Developer · AI Learner · Knowledge Builder',
    bio: '我是 Ethan Zhao，正在把 Web 开发、AI 学习、知识管理和学习系统结合起来，持续沉淀项目、文章、复盘和成长记录。这个站点既是个人表达空间，也是面向复试和求职展示的作品集入口。',
    skills: [
      { name: 'Java / Spring Boot', level: 78 },
      { name: 'Vue3 / Vite', level: 76 },
      { name: 'MySQL / Data Modeling', level: 70 },
      { name: 'Python / NLP Practice', level: 66 },
      { name: 'Git / Deployment', level: 72 },
      { name: 'AI Workflow', level: 68 },
    ],
    interests: ['AI 工程', '知识管理', 'Web 全栈', '考研复试', '英文表达', '长期主义'],
    status: 'Building in public',
    location: 'China',
    footer: '独立博客是家，GitHub 是证据，项目是最好的自我介绍',
  },
  auth: {
    staticNotice: '当前 GitHub Pages 只能展示静态页面；站内账号登录需要单独部署 Spring Boot 后端与 MySQL。',
    localNotice: '管理员登录入口。如有账号问题请联系站长。',
  },
  donateIntro: '如果我的博客对你有帮助，欢迎随手支持一下。',
  wechatPay: '',
  alipay: '',
  content: {
    articles,
    categories,
    tags,
    messages,
    comments,
  },
}

export const visibleSocials = socials.filter(s => s.url && !s.url.includes('your-id') && !s.url.includes('your@email.com'))
export const fallbackArticles = articles
export const fallbackCategories = categories
export const fallbackTags = tags
export const fallbackProjects = projects
export const fallbackFriends = friends
export const fallbackMessages = messages
export const fallbackComments = comments

export default siteConfig
