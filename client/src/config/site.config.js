/**
 * 站点配置文件
 */
export const siteConfig = {
  // ── 基础信息 ──
  name: 'Ethan',
  avatar: '/photos/avatar.png',        // 替换为真实头像路径
  subtitle: '菜就多练，练就必赚',

  // ── 首页右侧照片墙（留空则使用占位图）──
  photos: [
    '/photos/photo1.png',
    '/photos/photo2.png',
    '/photos/photo3.png',
  ],

  // ── 社交链接 ──
  socials: [
    { name: 'GitHub',     url: 'https://github.com/your-id',     color: '#1F2328', icon: 'github' },
    { name: '知乎',        url: 'https://www.zhihu.com/people/your-id', color: '#0084FF', icon: 'zhihu' },
    { name: 'Bilibili',    url: 'https://space.bilibili.com/your-id', color: '#00AEEC', icon: 'bilibili' },
    { name: '微博',        url: 'https://weibo.com/u/your-id',     color: '#E6162D', icon: 'weibo' },
    { name: '抖音',        url: 'https://www.douyin.com/user/your-id', color: '#161823', icon: 'douyin' },
    { name: '小红书',      url: 'https://www.xiaohongshu.com/user/profile/your-id', color: '#FF2442', icon: 'xiaohongshu' },
    { name: 'X',           url: 'https://x.com/your-id',           color: '#000000', icon: 'twitter' },
    { name: 'Telegram',    url: 'https://t.me/your-id',            color: '#26A5E4', icon: 'telegram' },
    { name: '邮箱',        url: 'mailto:your@email.com',           color: '#F5B83D', icon: 'email' },
  ],

  // ── 项目展示 ──
  projects: [
    {
      name: '博客系统',
      description: 'Vue3 + Spring Boot 个人博客，支持 Markdown 编辑与评论',
      icon: '📝',
      tag: 'Web',
      badge: null,
      url: '#',
      stats: '⭐ 10',
    },
    {
      name: '工具集',
      description: '日常开发中积累的实用工具和脚本',
      icon: '🛠️',
      tag: 'Tools',
      url: '#',
      stats: '⭐ 5',
    },
  ],

  // ── 友链 ──
  friends: [
    {
      name: 'Souta',
      description: '可能是一个笨蛋',
      avatar: 'https://api.dicebear.com/7.x/identicon/svg?seed=souta',
      url: 'https://souta.cc',
    },
    {
      name: '理想之 clover',
      description: '关于 AI 与技术的思考',
      avatar: 'https://api.dicebear.com/7.x/identicon/svg?seed=clover',
      url: 'https://idealclover.top',
    },
    {
      name: 'qwqwq',
      description: '有趣的项目与想法',
      avatar: 'https://api.dicebear.com/7.x/identicon/svg?seed=qwqwq',
      url: 'https://qwqwq.com',
    },
  ],

  // ── 关于页 ──
  about: {
    bio: '我是 Ethan，一个热爱技术也偶尔摸鱼的后端开发。',
    skills: ['Java', 'Spring Boot', 'Vue3', 'MySQL', 'Redis'],
    interests: ['编程', '游戏', '新技术'],
    location: '地球',
  },

  // ── 赞助 ──
  donateIntro: '如果我的博客对你有帮助，欢迎请我喝杯咖啡 ☕',
  wechatPay: '',   // 微信收款码，留空不显示
  alipay: '',      // 支付宝收款码，留空不显示
}

export default siteConfig
