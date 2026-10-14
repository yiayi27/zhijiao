<script setup lang="ts">
import { getCouponsGoods } from '@/api/couponActivity/coupon'
import PageManage from '@/components/PageManage.vue'
import { Check, Search as SearchIcon } from '@element-plus/icons-vue'

// 定义类型
interface GoodsItem {
  id: string
  name: string
  pic: string
  isCheck: boolean
  existId?: boolean
  limitType?: number
  minPrice?: number
  maxPrice?: number
  [key: string]: any
}

interface SearchParams {
  maxPrice: string
  minPrice: string
  name: string
  current: number
  size: number
  saleMode: string
  showCategoryId: string
}

const props = defineProps({
  actType: {
    type: String,
    default: '',
  },
  pointGoodsList: {
    type: Array,
    default: () => [],
  },
  dialogVisible: {
    type: Boolean,
    default: false,
  },
  allIds: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['onSureGoods', 'update:dialogVisible'])

// 本地变量，用于跟踪对话框状态
const localVisible = ref(false)

// 监听props中的dialogVisible变化，同步到本地变量
watch(
  () => props.dialogVisible,
  (val) => {
    localVisible.value = val
    if (val) {
      dealPointList()
    }
  },
)

// 监听本地变量变化，通知父组件更新
watch(
  () => localVisible.value,
  (val) => {
    emit('update:dialogVisible', val)
  },
)

/** 弹框商品 */
const goodsList = ref<GoodsItem[]>([])
const selectpointGoodsList = ref<GoodsItem[]>([])

/** 全选 */
const allChecked = ref(false)

/** 分页信息 */
const total = ref(0)

const search = reactive<SearchParams>({
  maxPrice: '',
  minPrice: '',
  name: '',
  current: 1,
  size: 10,
  saleMode: '',
  showCategoryId: '',
})

/** 样式选择 */
const borderStyle = reactive({
  borderGet: '2px solid #2D8CF0',
  borderNoGet: '2px solid #f2f2f2',
})

function cancelOption() {
  localVisible.value = false
  search.maxPrice = ''
  search.minPrice = ''
  search.name = ''
  search.current = 1
  search.size = 10
  search.saleMode = ''
  search.showCategoryId = ''
  goodsList.value.forEach((item: GoodsItem) => {
    item.isCheck = false
  })
}

function sureDig() {
  localVisible.value = false
  emit(
    'onSureGoods',
    selectpointGoodsList.value.map((v: GoodsItem) => v.id),
  )
}

function init() {
  getGoods()
  localVisible.value = true
  selectpointGoodsList.value = props.pointGoodsList as GoodsItem[]
  allChecked.value = false
}

/**
 * 编辑是获取已选择过的数据
 */
function dealPointList() {
  goodsList.value.forEach((item: GoodsItem) => {
    item.isCheck = false
    selectpointGoodsList.value.forEach((pointItem: GoodsItem) => {
      if (item.id === pointItem.id) {
        item.isCheck = true
      }
    })
  })
}

function getGoods() {
  getCouponsGoods(search).then((res) => {
    res.data.list.forEach((item: any) => {
      item.isCheck = false
    })
    const allIdsSet = new Set(props.allIds)
    goodsList.value = res.data.list.map((item: any) => ({ ...item, existId: allIdsSet.has(item.id) }))
    total.value = res.data.total
    dealPointList()
  })
}

// /**
//  * 选择商品状态
//  */
// function selectStatus(e) {
//   search.saleMode = e !== '3' ? e : ''
//   getGoods()
// }
//
// /**
//  * 选择分类
//  */
// function selectTem(item) {
//   search.showCategoryId = item.showCategoryId
//   getGoods()
// }

/**
 * 选择输入框
 */
function searchByInput() {
  getGoods()
}

/** 选择商品 */
function chooseGood(item: GoodsItem) {
  if (item.existId) return

  item.isCheck = !item.isCheck
  if (item.isCheck === true) {
    selectpointGoodsList.value.push(item)
  } else {
    selectpointGoodsList.value = selectpointGoodsList.value.filter((v: GoodsItem) => v.id !== item.id)
  }
}

/** 全选操作 */
function getAll() {
  goodsList.value.forEach((item: GoodsItem) => {
    if (allChecked.value) {
      if (!selectpointGoodsList.value.some((v: GoodsItem) => v.id === item.id)) {
        selectpointGoodsList.value.push(item)
      }
    } else {
      const index = selectpointGoodsList.value.findIndex((v: GoodsItem) => v.id === item.id)
      if (index >= 0) {
        selectpointGoodsList.value.splice(index, 1)
      }
    }
    item.isCheck = allChecked.value
  })
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  search.size = val
  getGoods()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  search.current = val
  getGoods()
}

defineExpose({
  init,
})
</script>

<template>
  <el-dialog v-model="localVisible" width="50%" append-to-body :before-close="cancelOption">
    <div>
      <div>选择商品</div>
      <div class="digGoods">
        <div class="digGoods__box">
          <div class="digGoods__box--top">
            <span style="line-height: 32px; margin: 0 0 0 65px; color: #a1a1a1; width: 60px">价格</span>
            <el-input v-model="search.minPrice" style="width: 60px" />
            <span style="line-height: 32px; margin: 0 5px">-</span>
            <el-input v-model="search.maxPrice" style="width: 60px" />
            <el-input v-model="search.name" style="width: 140px; margin-left: 10px" placeholder="请输入关键词" />
            <span class="serachBtn">
              <el-icon @click="searchByInput"><SearchIcon /></el-icon>
            </span>
          </div>
          <div class="digGoods__box--content">
            <template v-for="item in goodsList" :key="item.id">
              <div
                :class="`digGoods__box--content--good ${item.existId && 'not-select-good'}`"
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
                    <span v-if="item.minPrice && item.minPrice > 0">￥{{ item.minPrice }}</span>
                    <span v-else>￥{{ item.maxPrice }}</span>
                  </div>
                  <div v-else>￥{{ item.minPrice }}~￥{{ item.maxPrice }}</div>
                </div>
              </div>
            </template>
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
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancelOption"> 取 消 </el-button>
        <el-button type="primary" @click="sureDig"> 确 定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.digGoods {
  border-top: 1px solid #d7d7d7;
  padding-top: 10px;

  &__box {
    background-color: #f2f2f2;
    padding: 10px;

    &--top {
      display: flex;
      justify-content: flex-end;
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

.not-select-good {
  position: relative;

  &::after {
    content: '';
    position: absolute;
    display: block;
    width: 100%;
    height: 100%;
    cursor: not-allowed;
    background: rgba(0, 0, 0, 0.1);
    top: 0;
    left: 0;
  }
}
</style>
