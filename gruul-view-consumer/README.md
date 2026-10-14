# 🛒 智能商城消费者端 (Smart Shop Consumer)

基于 Vue 3 + TypeScript + Vite 开发的智能商城消费者端应用，支持H5和微信小程序多端部署。

## 🌟 项目特点

- 💪 基于 Vue 3.5.13 Composition API，代码逻辑更清晰
- 🔧 使用 TypeScript 5.8.3 提供完整类型安全和智能提示
- ⚡️ Vite 5.4.15 构建，开发热更新秒级响应
- 📱 一套代码多端运行，支持 H5 和微信小程序
- 🎨 统一的UI组件系统，基于uv-ui扩展的自定义组件
- 📦 使用 Pinia 3.0.1 进行状态管理，支持持久化存储
- 🔄 完善的API调用体系，统一处理请求响应
- 🛡️ 严格的代码规范和质量检测工具链
- 📂 基于分包设计，优化小程序加载性能

## 📋 功能概览

- 🏪 **商城核心**：首页、商品分类、商品详情、搜索
- 👤 **用户中心**：登录注册、个人信息、地址管理
- 🛒 **订单系统**：购物车、订单提交、订单管理、支付
- 🔄 **售后服务**：退款/退货申请、售后跟踪
- 💰 **会员系统**：会员等级、会员权益、积分商城
- 👥 **分销体系**：分销中心、客户管理、推广码
- 💳 **钱包功能**：余额查询、充值、提现

## 📦 安装与环境要求

> ⚠️ 本项目强制使用 pnpm 作为包管理工具，版本要求 >= 9.4.0
>
> Node.js 版本建议 >= 22.14.0

```bash
# 安装依赖
pnpm install
```

## 🚀 开发命令

```bash
# 默认开发模式（H5）
pnpm dev

# H5开发模式
pnpm dev:h5

# 微信小程序开发模式
pnpm dev:mp-weixin
```

## 🏗️ 构建命令

```bash
# 构建H5版本
pnpm build:h5

# 构建微信小程序版本
pnpm build:mp-weixin
```

## 🧪 代码质量工具

```bash
# 代码格式化
pnpm format

# Lint检查
pnpm lint

# 修复Lint错误（所有文件）
pnpm fix:all

# 修复Lint错误（已修改文件）
pnpm fix

# 修复Lint错误（最近一次提交的文件）
pnpm fix:last-commit

# 类型检查
pnpm type-check

# 移除直播相关代码
pnpm removeLive

# 未使用代码检测
pnpm knip
```

## 📁 项目结构

```
.
├── src/                    # 源代码
│   ├── api/                # API接口定义
│   │   ├── modules/        # 按模块划分的API
│   │   └── request.ts      # 请求和响应拦截器
│   ├── assets/             # 静态资源
│   │   ├── icons/          # 图标资源
│   │   ├── images/         # 图片资源
│   │   └── styles/         # 全局样式
│   ├── components/         # 公共组件
│   │   ├── business/       # 业务组件
│   │   └── common/         # 通用组件
│   ├── composables/        # 组合式函数（Hooks）
│   │   ├── useAuth.ts      # 认证相关
│   │   ├── useCart.ts      # 购物车相关
│   │   └── ...
│   ├── pages/              # 主包页面
│   │   ├── index/          # 首页
│   │   ├── sign/           # 登录注册
│   │   └── ...
│   ├── store/              # Pinia 状态
│   │   ├── modules/        # 状态模块
│   │   └── index.ts        # 状态入口
│   ├── subcontract/        # 子合约/分包页面
│   │   ├── user/           # 用户中心
│   │   ├── order/          # 订单系统
│   │   ├── afterSale/      # 售后服务
│   │   └── ...
│   ├── modules/            # 模块化业务代码
│   │   └── submit/         # 订单提交相关
│   ├── utils/              # 工具函数
│   │   ├── auth.ts         # 认证工具
│   │   ├── format.ts       # 格式化工具
│   │   ├── storage.ts      # 存储工具
│   │   └── ...
│   ├── uni_modules/        # uni-app 插件
│   ├── App.vue             # 应用入口组件
│   ├── main.ts             # 入口文件
│   ├── pages.json          # 页面和路由配置
│   └── manifest.json       # 应用配置
├── types/                  # 全局类型定义
├── scripts/                # 脚本工具
├── vite.config.ts          # Vite 配置
├── tsconfig.json           # TypeScript 配置
├── eslint.config.mjs       # ESLint 配置
└── prettier.config.mjs     # Prettier 配置
```

## 📦 主包与分包结构

### 主包 (pages)

主包包含应用的核心页面，保持轻量化以优化首次加载体验:

- `pages/index/index`: 首页，使用自定义导航栏
- `pages/sign/SmsAuth`: 登录页面，短信验证登录
- `pages/index/custom/custom`: 自定义页面

### 分包结构

项目采用多分包设计，优化加载速度和资源利用:

#### 1. modules 分包

包含订单提交相关功能:

- `pages/submit/integralSubmit/integralSubmit`: 积分订单提交
- `pages/submit/submit`: 标准订单提交

#### 2. subcontract 分包

包含大部分业务功能模块:

- **用户中心**:
  - 个人资料编辑 (`/subcontract/user/userInfo`)
  - 地址管理 (`/subcontract/user/address/list`)
  - 新增/编辑地址 (`/subcontract/user/address/edit`)
- **订单系统**:
  - 订单列表 (`/subcontract/order/list`)
  - 订单详情 (`/subcontract/order/detail`)
  - 物流跟踪 (`/subcontract/order/logistics`)
- **售后服务**:
  - 申请售后 (`/subcontract/afterSale/apply`)
  - 售后详情 (`/subcontract/afterSale/detail`)
- **商品相关**:
  - 商品详情 (`/subcontract/goods/detail`)
  - 商品评价 (`/subcontract/goods/comment`)
  - 收藏列表 (`/subcontract/goods/collection`)
- **会员系统**:
  - 会员中心 (`/subcontract/member/center`)
  - 会员升级 (`/subcontract/member/upgrade`)
- **积分商城**:
  - 积分兑换 (`/subcontract/points/mall`)
  - 积分规则 (`/subcontract/points/rule`)
- **钱包功能**:
  - 余额查询 (`/subcontract/wallet/balance`)
  - 充值 (`/subcontract/wallet/recharge`)
  - 提现 (`/subcontract/wallet/withdraw`)
- **分销体系**:
  - 分销中心 (`/subcontract/distribution/center`)
  - 客户管理 (`/subcontract/distribution/customer`)
  - 推广码 (`/subcontract/distribution/code`)

### 预加载规则

在首页(`pages/index/index`)加载时，预加载所有分包:

- 适用网络: 所有网络环境
- 预加载包: modules, subcontract

## 📄 环境配置

项目支持多环境配置：

- `.env.development` - 开发环境

  - API基础路径: 开发服务器
  - 调试模式: 开启
  - 日志级别: Debug

- `.env.test` - 测试环境

  - API基础路径: 测试服务器
  - 调试模式: 开启
  - 日志级别: Info

- `.env.production` - 生产环境
  - API基础路径: 生产服务器
  - 调试模式: 关闭
  - 日志级别: Error

## 📱 支持平台

- 🌐 H5 (Web)
- 💬 微信小程序

## 💾 数据管理

### Pinia 状态管理

项目使用 Pinia 进行状态管理，主要模块包括：

- `user` - 用户信息和登录状态
- `cart` - 购物车数据
- `app` - 应用全局配置
- `order` - 订单相关状态

### 本地存储

- `uni.setStorageSync` - 同步存储
- `uni.setStorage` - 异步存储
- 封装的`storage.ts`工具函数

## 📊 请求处理

- 基于`uni.request`封装的请求库
- 统一的请求拦截器：添加认证信息、处理通用参数
- 统一的响应拦截器：错误处理、状态码处理、数据转换
- 支持请求取消和超时控制

## 📝 开发规范

- 使用 ESLint 和 Prettier 保持代码风格一致
- 遵循 Vue 3 组合式 API 的最佳实践
- 组件和函数使用类型声明
- 使用rpx作为主要尺寸单位（1px = 2rpx, 0px和1px除外）
- 组件和页面使用BEM命名规范

### 命名规范

- 文件夹: 小驼峰命名（camelCase）
- Vue组件: 大驼峰命名（PascalCase）
- TypeScript类型: 大驼峰命名，以T或I开头（如TUser, IResponse）
- 常量: 大写下划线（UPPER_SNAKE_CASE）
- 函数和变量: 小驼峰命名（camelCase）

### 代码注释规范

- 函数注释使用JSDoc格式
- 关键业务逻辑需添加注释说明
- 复杂算法或特殊处理需详细注释

## 🛠️ 技术栈

- Vue 3.5.13
- Pinia 3.0.1
- TypeScript 5.8.3
- Vite 5.4.15
- uni-app 3.0.0
- Sass 1.77.8
- ESLint 9.24.0
- Prettier 3.5.3

## 🧩 组件库

项目使用自定义的UI组件库，基于uni-app生态:

- uv-ui: 基础UI组件库
- 自定义业务组件

## 🔐 安全

- 使用JSEncrypt进行RSA加密处理敏感数据
- MD5用于数据签名
- 完善的Token认证和刷新机制

## 📚 开发资源

- [uni-app官方文档](https://uniapp.dcloud.net.cn/)
- [Vue 3官方文档](https://cn.vuejs.org/)
- [TypeScript官方文档](https://www.typescriptlang.org/zh/)
- [Pinia官方文档](https://pinia.vuejs.org/zh/)
- [Vite官方文档](https://cn.vitejs.dev/)

## 📋 常见问题

### Q: 如何处理跨域问题?

A: 在开发环境下，可通过Vite的proxy配置解决跨域问题，生产环境建议在服务器端配置CORS。

### Q: 小程序包体积超出限制怎么办?

A: 检查并优化静态资源、合理使用分包、移除未使用代码、优化依赖库。

### Q: 如何调试小程序?

A: 使用微信开发者工具进行调试，同时可以利用uni-app提供的条件编译进行平台特定调试。
