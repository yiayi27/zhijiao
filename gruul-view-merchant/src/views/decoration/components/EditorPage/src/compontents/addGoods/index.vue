<script setup lang="ts">
import { getCouponsGoods } from '@/api/couponActivity/coupon'

import { getAllCategory, getAllRegionList } from '@/api/good/goods'
import PageManage from '@/components/PageManage.vue'
import { Check, Search } from '@element-plus/icons-vue'

interface GoodsItem {
  id: string | number
  pic?: string
  name?: string
  limitType?: number
  minPrice?: number
  maxPrice?: number
  isCheck?: boolean
  saleMode: string
  [key: string]: any
}

interface ModelItem {
  id?: string | number
  value?: string
  label: string
  modeName?: string
  showCategoryVos?: any[]
  [key: string]: any
}

interface CategoryItem {
  id?: string | number
  name: string
  showCategoryId?: string
  showCategoryVos?: CategoryItem[]
  [key: string]: any
}

interface SearchParams {
  maxPrice: string
  minPrice: string
  name: string
  current: number
  size: number
  saleMode: number | string
  showCategoryId: string
}

// 定义props
const props = defineProps({
  pointGoodsList: {
    type: Array,
    default: () => [],
  },
  goodsVisible: {
    type: Boolean,
    default: false,
  },
})

// 响应式数据
const goodsList = ref<GoodsItem[]>([])
const tempGoods = ref<GoodsItem[]>([])
const modelList = ref<ModelItem[]>([
  {
    value: '3',
    label: '全部专区',
  },
  {
    value: '0',
    label: '商超',
  },
  {
    value: '2',
    label: '秒杀',
  },
])
const showList = ref<CategoryItem[]>([])
const saleMode = ref<string | number>('')
const showVal = ref('')
const showCategory = ref('')
const allChecked = ref(false)
const total = ref(0)

const search = reactive<SearchParams>({
  maxPrice: '',
  minPrice: '',
  name: '',
  current: 1,
  size: 10,
  saleMode: 0,
  showCategoryId: '',
})

// 样式定义
const borderStyle = {
  borderGet: '2px solid #2D8CF0',
  borderNoGet: '2px solid #f2f2f2',
}

// 监听goodsVisible变化
watch(
  () => props.goodsVisible,
  (newVal) => {
    if (newVal) {
      resetDate()
      tempGoods.value = JSON.parse(JSON.stringify(props.pointGoodsList))
      dealPointList()
      getGoods()
    }
  },
)

// 生命周期钩子
onMounted(() => {
  getGoods()

  getAllRegionList({}).then((res) => {
    modelList.value = res.data
    if (props.goodsVisible) {
      tempGoods.value = JSON.parse(JSON.stringify(props.pointGoodsList))
      dealPointList()
    }
    getCategoryList()
  })
})

// 方法定义
function getCategoryList(): void {
  const param = {
    saleMode: saleMode.value,
  }
  getAllCategory(param).then((res) => {
    showList.value = res.data
  })
}

/**
 * 重置数据
 */
function resetDate(): void {
  search.showCategoryId = ''
  search.saleMode = 0
  search.name = ''
  search.maxPrice = ''
  search.minPrice = ''
  allChecked.value = false
  showVal.value = ''
}

/**
 * 编辑是获取已选择过的数据
 */
function dealPointList(): void {
  let checkAllFlag = true
  goodsList.value.forEach((item) => {
    const flag = checkIsSelected(item.id)
    item.isCheck = flag
    checkAllFlag = checkAllFlag ? flag : checkAllFlag
  })
  allChecked.value = checkAllFlag
}

/**
 * 获取商品
 */
function getGoods(): void {
  search.saleMode = saleMode.value
  getCouponsGoods(search).then((res) => {
    let checkAllFlag = true
    res.data.list.forEach((item: GoodsItem) => {
      const flag = checkIsSelected(item.id)
      item.isCheck = flag
      checkAllFlag = checkAllFlag ? flag : checkAllFlag
    })
    allChecked.value = checkAllFlag
    goodsList.value = res.data.list
    total.value = res.data.total
  })
}

/**
 * 选择分类
 */
function selectTem(item: ModelItem): void {
  saleMode.value = item.id || ''
  getCategoryList()
}

/**
 * 选择专区后的分类
 */
function selectItem(item: CategoryItem): void {
  search.showCategoryId = item.showCategoryId || ''
  getGoods()
}

/**
 * 选择输入框
 */
function searchByInput(): void {
  getGoods()
}

/** 选择商品 */
function chooseGood(item: GoodsItem): void {
  item.isCheck = !item.isCheck
  if (tempGoods.value.length >= 16) {
    ElMessage.error('最多选择16个商品')
    return
  } else if (item.isCheck) {
    tempGoods.value.push(item)
  } else {
    const idx = tempGoods.value.findIndex((i) => i.id === item.id)
    if (idx !== -1) {
      tempGoods.value.splice(idx, 1)
    }
  }
}

/**
 * 检测当前商品是否已经在选择过的列表中
 */
function checkIsSelected(id: string | number): boolean {
  const idx = tempGoods.value.findIndex((i) => i.id === id)
  return idx !== -1
}

/** 全选操作 */
function getAll(): void {
  let flag = false
  goodsList.value.forEach((item) => {
    if (allChecked.value) {
      if (tempGoods.value.length >= 16) {
        flag = true
      }
      if (!tempGoods.value.find((t) => t.id === item.id)) {
        tempGoods.value.push(item)
      }
    }
    item.isCheck = allChecked.value
  })
  if (flag) {
    ElMessage.error('最多选择16个商品')
    return
  }

  if (!allChecked.value) {
    goodsList.value.forEach((t) => {
      const idx = tempGoods.value.findIndex((i) => i.id === t.id)
      if (idx !== -1) {
        tempGoods.value.splice(idx, 1)
      }
    })
  }
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number): void {
  search.size = val
  getGoods()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number): void {
  search.current = val
  allChecked.value = false
  getGoods()
}

defineExpose({
  search,
  goodsList,
  tempGoods,
})
</script>

<template>
  <div class="mt-[20px]">
    <div class="title">选择商品</div>
    <div class="digGoods">
      <div class="digGoods__box">
        <div class="digGoods__box--top">
          <el-select v-model="showVal" style="width: 120px" placeholder="全部专区">
            <el-option-group v-for="(group, index) in modelList" :key="group.id || index">
              <el-option :label="group.modeName || group.label" :value="group.modeName || group.label" @click="selectTem(group)" />
              <!-- <el-option
                v-for="item in group.showCategoryVos"
                @click="selectTem(item)"
                :key="item.name"
                :label="item.name"
                :value="item.name"
                style="margin-left:20px"
              ></el-option> -->
            </el-option-group>
          </el-select>
          <el-select v-model="showCategory" style="width: 120px" placeholder="全部分类">
            <el-option-group v-for="(group, index) in showList" :key="group.id || index">
              <el-option :label="group.name" :value="group.name" @click="selectItem(group)" />
              <el-option
                v-for="item in group.showCategoryVos || []"
                :key="item.name"
                :label="item.name"
                :value="item.name"
                style="margin-left: 20px"
                @click="selectItem(item)"
              />
            </el-option-group>
          </el-select>
          <span style="margin: 0px 0px 0px 65px; color: #a1a1a1; width: 60px; line-height: 32px">价格</span>
          <el-input v-model="search.minPrice" style="width: 60px" />
          <span style="margin: 0px 5px; line-height: 32px">-</span>
          <el-input v-model="search.maxPrice" style="width: 60px" />
          <el-input v-model="search.name" style="width: 140px; margin-left: 10px" placeholder="请输入关键词" />
          <span class="serachBtn" @click="searchByInput">
            <el-icon><Search /></el-icon>
          </span>
        </div>
        <div v-if="goodsList.length > 0" class="digGoods__box--content">
          <div
            v-for="(item, index) in goodsList"
            :key="index"
            class="digGoods__box--content--good"
            :style="{
              border: item.isCheck ? borderStyle.borderGet : borderStyle.borderNoGet,
            }"
            @click="chooseGood(item)"
          >
            <img :src="item.pic" alt="" class="digGoods__box--content--good--img" />
            <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
              <el-icon color="white" size="40">
                <Check />
              </el-icon>
            </div>
            <div class="digGoods__box--content--good--shopName">
              <div>{{ item.name }}</div>
              <div v-if="item.limitType === 0">
                <span v-if="item.minPrice && item.minPrice > 0">￥{{ item.minPrice }}</span>
                <span v-else>￥{{ item.maxPrice }}</span>
              </div>
              <div v-else>￥{{ item.minPrice }}~￥{{ item.maxPrice }}</div>
            </div>
          </div>
        </div>
        <div
          v-if="goodsList.length === 0"
          class="digGoods__box--content"
          style="display: flex; justify-content: center; align-items: center; height: 250px"
        >
          暂无相关商品信息，请选择其他分类
        </div>
        <div class="digGoods__box--bottom">
          <el-checkbox v-model="allChecked" style="margin-top: 40px" @change="getAll"> 全选 </el-checkbox>
          <PageManage
            :page-size="search.size"
            :page-num="search.current"
            :total="total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.title {
  font-size: 15px;
  font-weight: bold;
  display: flex;
  margin-bottom: 20px;
  margin-top: -40px;
}

.digGoods {
  border-top: 1px solid #d7d7d7;
  padding-top: 10px;
  &__box {
    background-color: #f2f2f2;
    padding: 10px;
    &--top {
      display: flex;
      justify-content: space-between;
    }
    &--content {
      margin-top: 10px;
      background-color: white;
      border-radius: 5px;
      display: flex;
      flex-wrap: wrap;
      padding: 5px;
      &--good {
        width: 33%;
        margin-left: 2px;
        margin-bottom: 4px;
        height: 80px;

        border-radius: 5px;
        padding: 5px;
        display: flex;
        &--img {
          width: 65px;
          height: 65px;
          position: relative;
        }
        &--imgShadow {
          width: 65px;
          height: 65px;
          position: absolute;
          background-color: rgba(0, 0, 0, 0.6);
          display: flex;
          justify-content: center;
          align-items: center;
        }
        &--shopName {
          margin-left: 10px;
          padding-top: 5px;
          font-size: 12px;
          align-content: space-between;
        }
      }
    }
    &--bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}

.serachBtn {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #dcdfe6;
  background-color: white;
  cursor: pointer;
  border-left: none;
  border-radius: 4px;
}
</style>
