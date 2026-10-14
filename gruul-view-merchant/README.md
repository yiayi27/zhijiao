# 🛍️ Gruul View Merchant - 商城后台管理系统

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.5-brightgreen.svg" alt="Vue Version" />
  <img src="https://img.shields.io/badge/Vite-6.2-blue.svg" alt="Vite Version" />
  <img src="https://img.shields.io/badge/TypeScript-5.8-blue.svg" alt="TypeScript Version" />
  <img src="https://img.shields.io/badge/Element Plus-2.8-blue.svg" alt="Element Plus Version" />
  <img src="https://img.shields.io/badge/Pinia-3.0-orange.svg" alt="Pinia Version" />
</p>

## 📖 项目介绍

Gruul View Merchant 是一个现代化的商城后台管理系统，基于 Vue 3 和 TypeScript 开发。该项目已从 Vue 2 迁移至 Vue 3，采用了组合式 API (Composition API) 进行重构，为开发者提供更灵活、更强大的编程体验。

## ✨ 主要特性

- 🚀 **现代化技术栈**: Vue 3 + TypeScript + Vite
- 🧩 **组合式 API**: 告别选项式 API，拥抱 Composition API
- 📦 **Pinia 状态管理**: 告别 Vuex，享受 Pinia 的简洁与强大
- 🎨 **Element Plus UI**: 美观易用的 UI 组件库
- 🛠️ **TypeScript 支持**: 提供完整的类型定义
- 📱 **响应式设计**: 适配多种屏幕尺寸
- 🔒 **权限管理**: 完善的权限控制系统
- 🌐 **国际化**: 支持多语言切换
- 🔌 **可扩展插件系统**: 便于功能扩展

## 💼 业务模块设计

本系统由多个功能模块组成，覆盖了商城运营的各个方面：

### 📊 经营概况

- 提供商城经营数据的可视化展示
- 包含销售统计、客户增长、订单量等关键指标
- 辅助商家进行业务分析和决策

### 🛒 商品管理

- **商品列表**: 管理所有在售商品，支持商品上架、下架、编辑等操作
- **专区设置**: 配置商品专区，提升特定商品的曝光率
- **商品分类**: 创建和管理商品分类体系，便于客户浏览
- **属性模板**: 管理商品属性模板，快速应用到同类商品
- **供货商管理**: 维护供货商信息，管理货源渠道

### 📦 订单管理

- **快递订单**: 查看和管理线上销售的快递订单，支持批量发货
- **评价管理**: 查看和回复客户评价，维护商品声誉
- **售后工单**: 处理客户的退换货申请和售后服务
- **储值订单**: 管理客户储值充值相关的订单记录

### 🎯 营销应用

- **优惠券**: 创建和发放各类优惠券，刺激消费
- **满减活动**: 设置满额减免活动，提高客单价
- **积分商城**: 运营积分兑换体系，增强客户黏性
- **直播带货**: 管理直播销售活动，拓展销售渠道
- **商品基金**: 设置商品营销基金，用于特定推广
- **回收返券**: 通过旧物回收促进二次消费
- **社群接龙**: 利用社群力量进行营销推广

### 💰 财务管理

- **对账单**: 查看和核对商城各类交易的账单记录
- **提现工单**: 处理商家资金提现申请，管理资金流转

### 👥 客户管理

- **普通会员**: 查看和管理普通会员信息及消费记录
- **会员管理**: 设置会员等级和权益，提升客户忠诚度
- **黑名单**: 管理被限制购买或使用的客户名单

### 🚚 配送方式

- **快递配送**: 设置快递配送规则、费用和区域限制

### ⚙️ 商城设置

- **装修**: 自定义设计商城前台页面，提升用户体验
- **通用设置**: 配置商城基础信息、支付方式等
- **权限管理**: 设置后台用户权限，保障系统安全

## 🔧 安装与使用

### 环境要求

- Node.js 18+ (推荐使用 Node.js 22.14.0)
- pnpm 8+ (推荐使用 pnpm 9.0.0+)

### 安装依赖

```bash
# 强制使用 pnpm
pnpm install
```

### 开发环境启动

```bash
pnpm dev
```

### 生产环境构建

```bash
# 开发环境构建
pnpm build:dev

# 生产环境构建
pnpm build
```

### 代码检查和格式化

```bash
# 代码检查
pnpm lint

# 代码修复
pnpm fix

# 代码格式化
pnpm format
```

## 🏗️ 项目结构

```
├── public/             # 静态资源
├── src/                # 源代码
│   ├── api/            # API 请求
│   ├── assets/         # 主题、样式和字体资源
│   ├── components/     # 全局组件
│   ├── config/         # 全局配置
│   ├── layout/         # 布局组件
│   ├── libs/           # 工具库
│   ├── router/         # 路由配置
│   ├── store/          # 状态管理
│   ├── views/          # 页面组件
│   │   ├── businessCenter/ # 业务中心
│   │   ├── console/        # 控制台
│   │   ├── customer/       # 客户管理
│   │   ├── decoration/     # 装修系统
│   │   ├── finance/        # 财务管理
│   │   ├── goods/          # 商品管理
│   │   ├── logistics/      # 物流管理
│   │   ├── manage/         # 系统管理
│   │   ├── marketing/      # 营销活动
│   │   ├── order/          # 订单管理
│   │   ├── perm/           # 权限管理
│   │   ├── setting/        # 设置中心
│   │   └── sign/           # 登录注册
│   ├── App.vue         # 根组件
│   └── main.ts         # 应用入口
├── types/              # 类型声明
├── .eslintrc.cjs       # ESLint 配置
├── .prettierrc.cjs     # Prettier 配置
├── tsconfig.json       # TypeScript 配置
├── vite.config.ts      # Vite 配置
└── package.json        # 项目依赖和脚本
```

## 🚀 Vue 2 到 Vue 3 迁移指南

本项目已经从Vue 2迁移到Vue 3，采用了组合式API（Composition API）。以下是迁移过程中的主要变化和最佳实践。

### 主要变化

#### 1. 组件结构变化

**Vue 2 (Options API或Class Component)**:

```vue
<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'

@Component
export default class MyComponent extends Vue {
  @Prop({ default: () => {} }) readonly propData!: object

  myData = 'Hello'

  get computedValue() {
    return this.myData.toUpperCase()
  }

  mounted() {
    this.fetchData()
  }

  fetchData() {
    // 逻辑代码
  }
}
</script>
```

**Vue 3 (Composition API)**:

```vue
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  propData: object
}>()

const myData = ref('Hello')
const computedValue = computed(() => myData.value.toUpperCase())

function fetchData() {
  // 逻辑代码
}

onMounted(() => {
  fetchData()
})
</script>
```

#### 2. 响应式系统变化

- **Vue 2**: 使用`this`访问数据
- **Vue 3**: 使用`ref()`和`reactive()`创建响应式数据，需要通过`.value`访问ref值

#### 3. 生命周期钩子

- **Vue 2**: `created`, `mounted`, `updated`, `destroyed`等
- **Vue 3**: `onMounted`, `onUpdated`, `onUnmounted`等

#### 4. Props定义

- **Vue 2**: 使用`@Prop`装饰器或`props`选项
- **Vue 3**: 使用`defineProps`函数，支持类型推断

#### 5. 事件处理

- **Vue 2**: 使用`this.$emit`
- **Vue 3**: 使用`defineEmits`函数

#### 6. 工具库更新

- **日期处理**: 从自定义`DateUtil`迁移到`dayjs`库
  - 日期格式化: `new DateUtil('').getYMDs(date)` → `dayjs(date).format('YYYY-MM-DD')`
  - 日期计算: 使用`dayjs`的`diff`, `add`, `subtract`等方法

### 最佳实践

#### 1. 类型定义

为所有数据结构定义接口，提高代码的可维护性：

```typescript
interface UserData {
  id: number
  name: string
  age: number
}

const userData = reactive<UserData>({
  id: 1,
  name: '张三',
  age: 30,
})
```

#### 2. 组合函数(Composables)

将逻辑代码提取到单独的组合函数中，实现代码复用：

```typescript
// useUser.ts
import { computed, ref } from 'vue'

export function useUser() {
  const user = ref({ name: '张三', age: 30 })

  const isAdult = computed(() => user.value.age >= 18)

  const updateUser = (newName: string) => {
    user.value.name = newName
  }

  return {
    user,
    isAdult,
    updateUser,
  }
}

// 在组件中使用
import { useUser } from './composables/useUser'
const { user, isAdult, updateUser } = useUser()
```

#### 3. 模板引用

```vue
<script setup>
import { onMounted, ref } from 'vue'

const divRef = ref(null)

onMounted(() => {
  console.log(divRef.value) // 访问DOM元素
})
</script>

<template>
  <div ref="divRef">内容</div>
</template>
```

#### 4. 状态管理

从Vuex迁移到Pinia：

```typescript
// Vue 2 + Vuex
this.$store.dispatch('user/login', { username, password })

// Vue 3 + Pinia
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
userStore.login({ username, password })
```

#### 5. 使用标准库替代自定义工具

```typescript
// 使用dayjs处理日期
import dayjs from 'dayjs'

// 格式化日期
const formattedDate = dayjs(date).format('YYYY-MM-DD')

// 计算日期差
const diffDays = dayjs(endDate).diff(dayjs(startDate), 'day')

// 日期加减
const nextWeek = dayjs().add(7, 'day')
```

## 🔄 迁移步骤

1. 更新项目依赖（Vue 3, Vue Router 4等）
2. 将Options API转换为Composition API
3. 更新组件引用和插件用法
4. 修复兼容性问题
5. 更新状态管理（从Vuex到Pinia）
6. 优化类型定义和代码结构
7. 用标准库替换自定义工具（如用dayjs替换DateUtil）

## ❓ 常见问题

1. **响应式丢失**: 确保正确使用`.value`访问ref值
2. **组件通信**: 使用props和emits替代事件总线
3. **类型错误**: 使用正确的泛型和接口定义

## 🧩 Git提交规范

本项目使用 commitlint 进行提交规范检查，提交类型包括：

- `feat`：新功能（feature）
- `fix`：修复bug
- `perf`：性能优化
- `docs`：文档（documentation）
- `style`：格式（不影响代码运行的变动）
- `refactor`：重构（即不是新增功能，也不是修改bug的代码变动）
- `test`：增加测试
- `revert`：回滚
- `config`：构建过程或辅助工具的变动
- `chore`：其他改动
- `build`：构建配置修改
- `ci`：CI配置修改

## 📚 相关资源

- [Vue 3官方文档](https://v3.cn.vuejs.org/)
- [Composition API文档](https://v3.cn.vuejs.org/guide/composition-api-introduction.html)
- [Vue 3迁移指南](https://v3.cn.vuejs.org/guide/migration/introduction.html)
- [Element Plus文档](https://element-plus.org/zh-CN/)
- [Pinia文档](https://pinia.vuejs.org/zh/)
- [dayjs文档](https://day.js.org/zh-CN/)
- [TypeScript文档](https://www.typescriptlang.org/zh/)

## 📝 License

[MIT](LICENSE)
