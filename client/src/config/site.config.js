/**
 * Site configuration and static fallback content.
 *
 * GitHub Pages can only serve static files, so the public-facing pages use
 * this data whenever the Spring Boot API is not available.
 */
export const resolveAssetUrl = (path = '') => {
  if (!path) return ''
  if (/^(https?:|mailto:|tel:|#)/.test(path)) return path
  const base = (import.meta.env.BASE_URL || '/').replace(/\/$/, '')
  return `${base}/${path.replace(/^\//, '')}`
}

export const isStaticMode =
  import.meta.env.VITE_STATIC_MODE === 'true' ||
  (typeof window !== 'undefined' && window.location.hostname.endsWith('github.io'))

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
    url: 'https://github.com/EthanZhao02',
    color: '#38f8ff',
    icon: 'github',
  },
  {
    name: 'B站',
    url: 'https://space.bilibili.com/1671173639?spm_id_from=333.1007.0.0',
    color: '#00a1d6',
    icon: 'bilibili',
  },
  {
    name: '小红书',
    url: 'https://www.xiaohongshu.com/user/profile/66effb0c000000001d032156',
    color: '#ff2e4d',
    icon: 'xiaohongshu',
  },
  {
    name: '微博',
    url: 'https://weibo.com/u/6857384033',
    color: '#ff8200',
    icon: 'weibo',
  },
  {
    name: '知乎',
    url: 'https://www.zhihu.com/people/84-27-88-27-17',
    color: '#1772f6',
    icon: 'zhihu',
  },
  {
    name: '抖音',
    url: 'https://www.douyin.com/user/self?from_tab_name=main&showSubTab=video&showTab=post',
    color: '#38f8ff',
    icon: 'douyin',
  },
  {
    name: '邮箱',
    url: 'mailto:ethan912yydegree@gmail.com',
    color: '#8df8c7',
    icon: 'email',
  },
  {
    name: 'CSDN',
    url: 'https://blog.csdn.net/m0_60650009?type=blog',
    color: '#ff5a33',
    icon: 'csdn',
  },
  {
    name: 'GitLab',
    url: 'https://gitlab.com/dashboard/projects',
    color: '#fc6d26',
    icon: 'gitlab',
  },
  {
    name: 'Gitee',
    url: 'http://gitee.com/Ethan_36',
    color: '#c71d23',
    icon: 'gitee',
  },
  {
    name: 'X',
    url: 'https://x.com/Chason4436911',
    color: '#ffffff',
    icon: 'twitter',
  },
]

const categories = [
  { id: 1, name: '技术笔记', sort: 1 },
  { id: 2, name: '项目复盘', sort: 2 },
  { id: 3, name: '生活记录', sort: 3 },
]

const tags = [
  { id: 1, name: 'Vue3' },
  { id: 2, name: 'Spring Boot' },
  { id: 3, name: 'GitHub Pages' },
  { id: 4, name: '个人博客' },
]

const articles = [
  {
    id: 1,
    title: '把个人博客部署到 GitHub Pages',
    summary: '记录这个站点从本地项目到 GitHub Pages 静态发布的关键步骤。',
    content: `# 把个人博客部署到 GitHub Pages

这个博客原本是 Vue3 + Spring Boot + MySQL 的全栈项目。本次上线先把访客会看到的页面做成静态可访问版本，再通过 GitHub Actions 自动构建并部署。

## 为什么要做静态兜底

GitHub Pages 不能运行 Java 后端，也不能托管 MySQL。为了避免上线后文章、项目、友链和留言页空白，前端会在接口不可用时读取本地配置里的公开内容。

## 当前部署流程

每次推送到 master 分支后，GitHub Actions 会进入 client 目录，安装依赖，执行生产构建，然后把 dist 发布到 GitHub Pages。

## 下一步更适合怎么做

短期最适合做 GitHub 仓库驱动的 CMS/后台：后台页面编辑 Markdown 文章、首页配置、关于页、项目和友链，然后自动提交到仓库。仓库更新后 GitHub Actions 会重新部署，维护成本低，也不需要一直运行 Spring Boot + MySQL。

如果以后要真实评论、登录、写文章和后台管理，再把 Spring Boot 后端部署到支持 Java 的平台，把数据库单独托管，并把 VITE_API_BASE_URL 指到后端地址。免费额度经常变化，部署前以平台官网为准。`,
    categoryId: 1,
    categoryName: '技术笔记',
    authorName: 'Ethan',
    tags: [
      { id: 1, name: 'Vue3' },
      { id: 3, name: 'GitHub Pages' },
      { id: 4, name: '个人博客' },
    ],
    viewCount: 128,
    createTime: '2026-07-02T09:30:00',
  },
  {
    id: 2,
    title: '个人博客项目结构说明',
    summary: '前端、后端和数据库脚本各自承担的角色，以及上线时的取舍。',
    content: `# 个人博客项目结构说明

项目主要分为三个部分：client、server 和 database。

## client

前端使用 Vue3、Vue Router、Pinia 和 Vite。首页、归档、项目、友链、留言板和关于页都在这里实现。

## server

后端使用 Spring Boot，负责文章、评论、留言、友链、用户登录和上传等接口。它适合部署在支持 Java 的服务器上。

## database

database/init.sql 是初始化 MySQL 数据的脚本。线上如果要启用后台管理和动态内容，需要先准备数据库。`,
    categoryId: 2,
    categoryName: '项目复盘',
    authorName: 'Ethan',
    tags: [
      { id: 2, name: 'Spring Boot' },
      { id: 4, name: '个人博客' },
    ],
    viewCount: 86,
    createTime: '2026-06-28T20:15:00',
  },
  {
    id: 3,
    title: '关于这个站点',
    summary: '这里会放一些学习、实践和项目记录。',
    content: `# 关于这个站点

这里会记录我在编程、项目实践和技术学习中的一些笔记。站点会先保持轻量、好读、能稳定访问，再逐步补充真实文章和项目沉淀。

保持热爱，奔赴山海。`,
    categoryId: 3,
    categoryName: '生活记录',
    authorName: 'Ethan',
    tags: [
      { id: 4, name: '个人博客' },
    ],
    viewCount: 42,
    createTime: '2026-06-18T18:00:00',
  },
]

const projects = [
  {
    id: 1,
    name: '个人博客系统',
    description: 'Vue3 + Spring Boot 个人博客，支持 Markdown 文章、评论、留言和友链模块。',
    icon: '◇',
    tag: 'Vue',
    techStack: 'Vue3, Vite, Spring Boot, MySQL, GitHub Pages',
    category: 'web',
    status: '已上线',
    isOpenSource: 1,
    isActive: 1,
    url: 'https://ethanzhao02.github.io/personal-blog/',
    githubUrl: 'https://github.com/EthanZhao02/personal-blog',
    stats: 'Personal Blog',
    createTime: '2026-07-02T09:00:00',
  },
  {
    id: 2,
    name: '开发工具集',
    description: '日常开发中积累的脚本、模板和排错经验，后续会逐步整理成可复用工具。',
    icon: '⌘',
    tag: 'Tools',
    techStack: 'JavaScript, PowerShell, Markdown',
    category: 'tool',
    status: '开发中',
    isOpenSource: 0,
    isActive: 1,
    url: '#',
    stats: 'WIP',
    createTime: '2026-06-20T10:00:00',
  },
]

const friends = [
  {
    id: 1,
    name: 'Souta',
    description: '可能是一个笨蛋',
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
    content: '欢迎来到我的留言板。静态部署模式下，你的留言会先保存在当前浏览器本地。',
    createTime: '2026-07-02T09:00:00',
  },
]

const comments = {
  1: [
    {
      id: 1,
      nickname: 'Ethan',
      userAvatar: avatar,
      content: '这一版先保证博客能稳定上线；后端部署后，评论会切回真实接口。',
      createTime: '2026-07-02T10:00:00',
      children: [],
    },
  ],
  2: [],
  3: [],
}

export const siteConfig = {
  name: 'Ethan',
  avatar,
  subtitle: '代码、热爱与未来感的个人实验室',
  photos,
  socials,
  projects,
  friends,
  about: {
    bio: '我是 Ethan，喜欢后端与全栈实践，也在把学习、项目和生活碎片整理成长期可访问的作品。',
    skills: ['Java', 'Spring Boot', 'Vue3', 'MySQL', 'Redis'],
    interests: ['编程', '游戏', '新技术'],
    location: '地球',
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
