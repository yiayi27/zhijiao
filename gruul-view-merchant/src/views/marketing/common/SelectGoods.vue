<script setup lang="ts">
import { getCouponsGoods } from '@/api/couponActivity/coupon'
import { getAllRegionList, getList } from '@/api/good/goods'
import PageManage from '@/components/PageManage.vue'
import { Check, Search } from '@element-plus/icons-vue'

// 定义商品类型接口
interface GoodsItem {
  id: string | number
  pic: string
  name: string
  limitType: number
  minPrice: number
  maxPrice: number
  isCheck?: boolean
  [key: string]: any
}

// 定义分类接口
interface CategoryItem {
  id: string
  name: string
  modeName: string
  index?: number
  showCategoryVos?: CategoryItem[]
  [key: string]: any
}

// Props 定义
const props = defineProps({
  /** 获取活动类型 */
  actType: {
    type: String,
    default: '',
  },
  pointGoodsList: {
    type: Array as PropType<GoodsItem[]>,
    default: () => [],
  },
  goodsVisible: {
    type: Boolean,
    default: false,
  },
})

/** 弹框商品 */
const goodsList = ref<GoodsItem[]>([])

/** 已选未确认商品 */
const selectpointGoodsList = ref<GoodsItem[]>([])

/** 二级分类 */
const showSecList = ref<CategoryItem[]>([])
const showSecVal = ref('')

/** 分类选择 */
const showList = ref<CategoryItem[]>([])

/** 默认选择状态 */
const showVal = ref('')

const saleMode = ref('')

/** 全选 */
const allChecked = ref(false)

/** 分页信息 */
const total = ref(0)

const search = ref({
  maxPrice: '',
  minPrice: '',
  name: '',
  current: 1,
  size: 10,
  saleMode: '',
  showCategoryId: '',
})

/** 样式选择 */
const borderStyle = {
  borderGet: '2px solid #2D8CF0',
  borderNoGet: '2px solid #f2f2f2',
}

// 监听弹窗可见性
watch(
  () => props.goodsVisible,
  (newVal) => {
    if (newVal) {
      dealPointList()
    }
  },
)

onMounted(() => {
  selectpointGoodsList.value = [...(props.pointGoodsList as GoodsItem[])]
  getAllRegionList({}).then((res) => {
    showList.value = res.data
    saleMode.value = res.data[0].id
    getGoods()
  })
  onClear()
})

/**
 * 编辑是获取已选择过的数据
 */
function dealPointList() {
  goodsList.value.forEach((item) => {
    item.isCheck = false
    selectpointGoodsList.value.forEach((pointItem) => {
      if (item.id === pointItem.id) {
        item.isCheck = true
      }
    })
  })
}

function getGoods() {
  getCouponsGoods(search.value).then((res) => {
    res.data.list.forEach((item: any) => {
      item.isCheck = false
      props.pointGoodsList.forEach((pointItem: GoodsItem) => {
        if (item.id === pointItem.id) {
          item.isCheck = true
        }
      })
    })
    goodsList.value = res.data.list
    total.value = res.data.total
  })
}

/**
 * 选择分类
 */
function selectTem(item: CategoryItem) {
  saleMode.value = item.id
  search.value.saleMode = saleMode.value
  showSecList.value = []
  search.value.showCategoryId = ''
  showSecVal.value = ''
  getUseEffectiveCategory()
}

async function getUseEffectiveCategory() {
  const res = await getList({
    saleMode: saleMode.value,
  })
  showSecList.value = res.data.list
  getGoods()
}

/**
 * 选择二级分类后获取商品
 */
function selectGood(item: CategoryItem) {
  search.value.showCategoryId = item.id
  search.value.saleMode = saleMode.value
  allChecked.value = false
  getGoods()
}

function onClear() {
  search.value.saleMode = ''
  showSecList.value = []
  showSecVal.value = ''
  search.value.showCategoryId = ''
  getGoods()
}

/**
 * 选择输入框
 */
function searchByInput() {
  getGoods()
}

/** 选择商品 */
function chooseGood(item: GoodsItem) {
  item.isCheck = !item.isCheck
  if (item.isCheck) {
    selectpointGoodsList.value = [...selectpointGoodsList.value, item]
  } else {
    selectpointGoodsList.value = selectpointGoodsList.value.filter((i) => {
      return i.id !== item.id
    })
  }

  allChecked.value = selectpointGoodsList.value.length === goodsList.value.length
}

/** 全选操作 */
function getAll() {
  if (allChecked.value) {
    // 全选
    const newGoodsList = [...goodsList.value]
    newGoodsList.forEach((item) => (item.isCheck = true))

    // 合并已有的和新选择的商品
    const existingIds = selectpointGoodsList.value.map((item) => item.id)
    const newItems = newGoodsList.filter((item) => !existingIds.includes(item.id))
    selectpointGoodsList.value = [...selectpointGoodsList.value, ...newItems]
  } else {
    // 取消全选
    goodsList.value.forEach((item) => (item.isCheck = false))

    // 从已选列表中移除当前页面上的商品
    const currentIds = goodsList.value.map((item) => item.id)
    selectpointGoodsList.value = selectpointGoodsList.value.filter((item) => !currentIds.includes(item.id))
  }
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  search.value.size = val
  allChecked.value = false
  getGoods()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  search.value.current = val
  allChecked.value = false
  getGoods()
}

// 导出组件属性和方法
defineExpose({
  goodsList,
  selectpointGoodsList,
  search,
  getGoods,
})
</script>

<template>
  <div>
    <div class="title">选择商品</div>
    <div class="digGoods">
      <div class="digGoods__box">
        <div class="digGoods__box--top">
          <el-select v-model="showVal" style="width: 120px" placeholder="全部专区" clearable @clear="onClear">
            <el-option-group v-for="group in showList" :key="group.index">
              <el-option :label="group.modeName" :value="group.modeName" @click="selectTem(group)" />
            </el-option-group>
          </el-select>
          <el-select v-model="showSecVal" placeholder="请选择分类" style="width: 120px">
            <el-option-group v-for="group in showSecList" :key="group.index">
              <el-option-group :key="group.id" :label="group.name" :value="group.name" />
              <el-option
                v-for="item in group.showCategoryVos"
                :key="item.name"
                :label="item.name"
                :value="item.name"
                style="margin-left: 20px"
                @click="selectGood(item)"
              />
            </el-option-group>
          </el-select>
          <span style="margin: 0px 0px 0px 50px; color: #a1a1a1; width: 34px; line-height: 32px">价格</span>
          <el-input v-model="search.minPrice" style="width: 60px" />
          <span style="margin: 0px 5px; line-height: 32px">-</span>
          <el-input v-model="search.maxPrice" style="width: 60px" />
          <el-input
            v-model="search.name"
            placeholder="请输入关键词"
            class="search-wrap-input"
            clearable
            style="width: 180px; margin-left: 10px"
            @click="onClear"
          >
            <template #append>
              <el-button :icon="Search" @click="searchByInput" />
            </template>
          </el-input>
        </div>
        <div class="digGoods__box--content">
          <div
            v-for="(item, index) in goodsList"
            :key="index"
            class="digGoods__box--content--good"
            :style="{
              border: item.isCheck ? borderStyle.borderGet : borderStyle.borderNoGet,
            }"
            @click="chooseGood(item)"
          >
            <img :src="item.pic" class="digGoods__box--content--good--img" />
            <div v-if="item.isCheck" class="digGoods__box--content--good--imgShadow">
              <el-icon color="white" size="40">
                <Check />
              </el-icon>
            </div>
            <div class="digGoods__box--content--good--shopName">
              <div>{{ item.name }}</div>
              <div v-if="item.limitType === 0">
                <span v-if="item.minPrice > 0">￥{{ item.minPrice }}</span>
                <span v-else>￥{{ item.maxPrice }}</span>
              </div>
              <div v-else>￥{{ item.minPrice }}~￥{{ item.maxPrice }}</div>
            </div>
          </div>
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
  font-weight: bold;
  margin-top: -30px;
  margin-bottom: 20px;
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

:deep(.el-icon-loading) {
  display: none;
}
</style>
