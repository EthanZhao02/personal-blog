# 个人博客系统

基于 Vue3 + SpringBoot + MySQL 构建的个人博客系统，适合考研复试展示。

## 技术栈

### 前端
- Vue 3 - 渐进式 JavaScript 框架
- Vite - 下一代前端构建工具
- Vue Router - 路由管理
- Pinia - 状态管理
- Element Plus - UI 组件库
- Axios - HTTP 请求库
- md-editor-v3 - Markdown 编辑器

### 后端
- Spring Boot 2.7 - 应用框架
- MyBatis-Plus - ORM 框架
- MySQL 8.0 - 关系型数据库
- JWT - 身份认证

## 功能特性

### 用户模块
- ✅ 用户注册
- ✅ 用户登录（JWT 认证）
- ✅ 个人信息修改
- ✅ 头像上传

### 文章模块
- ✅ 文章列表（分页查询）
- ✅ 文章详情查看
- ✅ 发布文章（Markdown 编辑器）
- ✅ 编辑文章
- ✅ 删除文章
- ✅ 按分类筛选
- ✅ 按标签筛选
- ✅ 关键词搜索

### 分类模块
- ✅ 分类列表
- ✅ 添加分类
- ✅ 编辑分类
- ✅ 删除分类

### 标签模块
- ✅ 标签列表
- ✅ 添加标签
- ✅ 编辑标签
- ✅ 删除标签

### 评论模块
- ✅ 发表评论
- ✅ 评论列表
- ✅ 删除评论

## 项目结构

```
personal-blog/
├── client/                 # 前端项目
│   ├── src/
│   │   ├── api/           # API 接口
│   │   ├── assets/        # 静态资源
│   │   ├── components/    # 组件
│   │   ├── router/        # 路由
│   │   ├── stores/        # 状态管理
│   │   ├── utils/         # 工具函数
│   │   ├── views/         # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── server/                 # 后端项目
│   ├── src/main/java/com/blog/
│   │   ├── controller/    # 控制器
│   │   ├── entity/        # 实体类
│   │   ├── mapper/        # Mapper 接口
│   │   ├── service/       # 服务层
│   │   ├── dto/           # 数据传输对象
│   │   ├── vo/            # 视图对象
│   │   ├── common/        # 通用类
│   │   ├── config/        # 配置类
│   │   ├── utils/         # 工具类
│   │   └── BlogApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── database/               # 数据库
│   └── init.sql           # 初始化脚本
│
└── README.md
```

## 快速开始

### 环境要求

- Node.js 16+
- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+

### 1. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql
```

### 2. 启动后端

```bash
cd server

# 安装依赖
mvn clean install

# 启动项目
mvn spring-boot:run
```

后端运行在 http://localhost:8080

### 3. 启动前端

```bash
cd client

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端运行在 http://localhost:3000

### 4. 访问系统

浏览器打开 http://localhost:3000


## 数据库设计

### 用户表 (user)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(100) | 密码 |
| nickname | VARCHAR(50) | 昵称 |
| email | VARCHAR(100) | 邮箱 |
| avatar | VARCHAR(255) | 头像 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 文章表 (article)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR(200) | 标题 |
| content | TEXT | 内容 |
| summary | VARCHAR(500) | 摘要 |
| category_id | BIGINT | 分类ID |
| user_id | BIGINT | 用户ID |
| view_count | INT | 浏览量 |
| create_time | DATETIME | 创建时间 |

### 分类表 (category)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 分类名称 |
| sort | INT | 排序 |
| create_time | DATETIME | 创建时间 |

### 标签表 (tag)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 标签名称 |
| create_time | DATETIME | 创建时间 |

### 评论表 (comment)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| content | VARCHAR(500) | 评论内容 |
| article_id | BIGINT | 文章ID |
| user_id | BIGINT | 用户ID |
| parent_id | BIGINT | 父评论ID |
| create_time | DATETIME | 创建时间 |

## API 接口

### 用户接口
- POST /user/register - 用户注册
- POST /user/login - 用户登录
- GET /user/info - 获取用户信息
- PUT /user/info - 更新用户信息
- POST /user/avatar - 上传头像

### 文章接口
- GET /article/list - 获取文章列表
- GET /article/detail/{id} - 获取文章详情
- POST /article/publish - 发布文章
- PUT /article/update - 更新文章
- DELETE /article/delete/{id} - 删除文章

### 分类接口
- GET /category/list - 获取分类列表
- POST /category/add - 添加分类
- PUT /category/update - 更新分类
- DELETE /category/delete/{id} - 删除分类

### 标签接口
- GET /tag/list - 获取标签列表
- POST /tag/add - 添加标签
- PUT /tag/update - 更新标签
- DELETE /tag/delete/{id} - 删除标签

### 评论接口
- GET /comment/list/{articleId} - 获取评论列表
- POST /comment/add - 发表评论
- DELETE /comment/delete/{id} - 删除评论

## 复试要点

### 1. 技术选型理由
- **Vue3**：前端主流框架，组合式 API 更灵活
- **SpringBoot**：Java 后端标准框架，企业级应用首选
- **MySQL**：关系型数据库，考研必考，面试必问

### 2. 核心功能实现
- **登录认证**：JWT Token 机制，无状态认证
- **权限控制**：路由守卫 + 后端拦截器
- **数据操作**：完整的 CRUD 操作
- **分页查询**：MyBatis-Plus 分页插件

### 3. 面试可能问题
- JWT 认证流程是什么？
- 如何防止 SQL 注入？
- 前后端如何交互？
- 数据库如何设计？
- 分页如何实现？

## 常见问题

### 1. 后端启动失败
- 检查 MySQL 是否启动
- 检查数据库连接配置
- 检查 JDK 版本

### 2. 前端启动失败
- 检查 Node.js 版本
- 删除 node_modules 重新安装

### 3. 登录失败
- 检查数据库是否有用户数据
- 检查密码是否正确（默认 123456）

## 上传到 GitHub

### 方法一：命令行（推荐）

#### 1. 在 GitHub 创建仓库
1. 打开 https://github.com/new
2. 填写仓库名称：`personal-blog`
3. 选择 Private（私有）或 Public（公开）
4. **不要勾选** "Add a README file"（项目已有）
5. 点击 "Create repository"

#### 2. 本地初始化并推送
```bash
# 进入项目目录
cd personal-blog

# 初始化 Git 仓库（如果还没有）
git init

# 添加所有文件
git add .

# 提交
git commit -m "first commit: 个人博客系统"

# 关联远程仓库（把下面这段复制到 GitHub 创建仓库后显示的指令）
git remote add origin https://github.com/YOUR_USERNAME/personal-blog.git

# 推送到 GitHub
git push -u origin master
```

#### 3. 如果遇到 Token 失效问题
GitHub 已于 2021 年取消密码认证，需使用 Personal Access Token：

1. 打开 https://github.com/settings/tokens/new
2. 输入 Token 名称（如：my-pc）
3. 勾选 `repo` 全部权限
4. 点击 "Generate token"，**复制保存好 Token**

推送时，URL 格式改为：
```
https://ghp_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX@github.com/YOUR_USERNAME/personal-blog.git
```
把 `ghp_xxx...` 换成你的 Token。

或者用 GitHub CLI 登录：
```bash
# 安装 GitHub CLI（Windows）
winget install GitHub.cli

# 登录
gh auth login

# 之后 push 就不需要手动输入密码了
git push origin master
```

### 方法二：可视化工具

#### GitHub Desktop
1. 下载 https://desktop.github.com
2. File → Add Local Repository → 选择 personal-blog 文件夹
3. Publish repository 到 GitHub

#### VS Code
1. 用 VS Code 打开 personal-blog 文件夹
2. 侧边栏 Source Control 点 ✓
3. 输入提交信息，点击提交
4. 点击 Publish to GitHub

---

## 作者

个人博客系统 - Vue3 + SpringBoot + MySQL
