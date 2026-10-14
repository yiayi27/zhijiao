<script setup lang="ts">
import { getAllCategory } from '@/api/good/goods'

// 组件名称
defineOptions({
  name: 'MarketModalSearch',
})

// 定义props
const props = defineProps({
  changeId: {
    type: String,
    default: '',
  },
})

// 定义emit
const emit = defineEmits(['searchBy'])

// 表单引用
const form = ref(null)

// 日期选择快捷选项
const shortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    },
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    },
  },
]

// 响应式状态
const showCateList = ref<any[]>([])
const createTime = ref('')
const saleMode = ref('')

// 商品状态选项
const goodsType = [
  {
    value: '',
    label: '全部',
  },
  {
    value: '1',
    label: '上架',
  },
  {
    value: '0',
    label: '下架',
  },
]

// 搜索条件
const searchType = reactive({
  name: '', // 商品名称
  showCategoryId: '', // 商品展示分类
  status: '', // 商品上下架状态
  dominoState: '', // 商品是否参与接龙
  createBeginTime: '', // 创建开始时间
  createEndTime: '', // 创建结束时间
})

// 初始化函数
function init() {
  const param = {
    saleMode: saleMode.value,
  }

  getAllCategory(param).then((res) => {
    res.data.unshift({
      id: '0',
      name: '全部',
      parentId: '0',
      showCategoryId: '',
      showCategoryVos: [],
      sort: 0,
    })
    showCateList.value = res.data
    searchType.showCategoryId = ''
  })
}

// 选择时间处理
function chooseTime(data: string[]) {
  searchType.createBeginTime = data?.[0]
  searchType.createEndTime = data?.[1]
}

// 搜索方法
function search() {
  emit('searchBy', searchType)
}

// 监听changeId变化
watch(
  () => props.changeId,
  (newValue) => {
    saleMode.value = newValue
    init()
  },
  { immediate: true },
)

// 组件挂载时初始化
onMounted(() => {
  init()
})
</script>

<template>
  <m-card class="form" :need-toggle="true">
    <el-form ref="form" :model="searchType" label-width="90px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="商品名称">
            <el-input v-model="searchType.name" placeholder="请输入商品名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="展示分类">
            <el-select v-model="searchType.showCategoryId" placeholder="请选择分类">
              <el-option-group v-for="group in showCateList" :key="group.index">
                <el-option :label="group.name" :value="group.showCategoryId" />
                <el-option
                  v-for="item in group.showCategoryVos"
                  :key="item.showCategoryId"
                  :label="item.name"
                  :value="item.showCategoryId"
                  style="margin-left: 20px"
                />
              </el-option-group>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="商品状态">
            <el-select v-model="searchType.status" placeholder="请选择">
              <el-option v-for="item in goodsType" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="createTime"
              style="width: 224px"
              type="daterange"
              align="right"
              unlink-panels
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :shortcuts="shortcuts"
              value-format="YYYY-MM-DD"
              @change="chooseTime"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item>
        <el-button type="primary" @click="search"> 搜索 </el-button>
      </el-form-item>
    </el-form>
  </m-card>
  <!-- </div> -->
</template>

<style lang="scss" scoped>
.form {
  transform-origin: left top;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease 0s;

  &.show {
    height: 200px;
  }

  &.hide {
    height: 50px;

    .form__btn {
      width: 940px;
      height: 50px;
      background: #f9f9f9;
      line-height: 50px;
      // margin-top: 20px
    }
  }

  &__btn {
    width: 100%;
    position: absolute;
    bottom: 0;
    text-align: center;
    padding-bottom: 20px;

    span {
      cursor: pointer;
    }
  }
}

.page {
  // height: 270px;
  background-color: #f9f9f9;
  margin-bottom: 20px;
}

.search {
  display: flex;
  flex-wrap: wrap;

  &__item {
    padding: 20px 40px 10px 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    &--text {
      width: 60px;
    }
  }

  &__icon {
    width: 40px;
    text-align: center;
    border-left: 1px solid #dcdfe6;
    cursor: pointer;
    vertical-align: middle;
  }
}

.searchButton {
  margin: 20px 30px;
}
</style>
