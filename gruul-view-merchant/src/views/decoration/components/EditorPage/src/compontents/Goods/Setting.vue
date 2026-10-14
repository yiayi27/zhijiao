<script setup lang="ts">
import { getAllCategory } from '@/api/decoration/decoration'
import { discountList } from '@/api/good/goods'
import { useGlobalStore } from '@/store/modules/global'
import { CircleClose, Delete, Plus } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import { ElMessageBox } from 'element-plus'
import { storeToRefs } from 'pinia'

import SelectGoods from './../addGoods/index.vue'
import Category from './Category.vue'
import Goods from './Goods'

interface Sytles {
  label: string
  value: string
}

interface CategoryItem {
  id: string
  name: string
  productNumber?: number
  showCategoryVos?: CategoryItem[]
  saleMode?: string
  [key: string]: any
}

interface GoodsItem {
  id: string
  img: string
  name: string
  price: string | number
  albumPics?: string
  saleMode?: string
  [key: string]: any
}

interface ShowContent {
  nameShow: boolean
  pointShow: boolean
  priceShow: boolean
  buttonShow: boolean
  buttonStyle: number
  buttonText: string
  tagShow: boolean
  tagStyle: number
  [key: string]: any
}

interface FormData {
  ponentType: number
  firstCatList: CategoryItem[]
  currentCategoryId: string
  goods: GoodsItem[]
  goodsCount: number
  titleStyle: number
  listStyle: string
  pagePadding: number
  goodPadding: number
  goodsStyle: string
  angle: string
  textStyle: string
  showContent: ShowContent
  [key: string]: any
}

// 定义props和emit
const props = defineProps({
  formData: {
    type: Object as () => typeof Goods,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:formData'])

// 引用原来的formModel
const subForm = useVModel(props, 'formData', emit)

// 获取store
const globalStore = useGlobalStore()
const { mode } = storeToRefs(globalStore)

// 使用ref获取组件实例
const selectGoodsRef = ref<InstanceType<typeof SelectGoods> | null>(null)
const CategoryRef = ref<InstanceType<typeof Category> | null>(null)

// 监听subForm的变化
watch(
  subForm,
  () => {
    handleFormDataChange()
  },
  { deep: true },
)

// 选择商品分类对话框
const dialogShow = ref(false)

// 类目拖动下标
const classDragIndex = ref(-1)

// 拖动商品的下标
const dragStarIndex = ref(-1)

// 列表样式
const listStyles = reactive<Sytles[]>([
  {
    label: '大图模式',
    value: 'goods-style--one',
  },
  {
    label: '一行两个',
    value: 'goods-style--two',
  },
  {
    label: '详细列表',
    value: 'goods-style--three',
  },
  {
    label: '横向滑动',
    value: 'goods-style--four',
  },
])

// 商品样式
const goodsStyles = reactive<Sytles[]>([
  {
    label: '无底白边',
    value: 'is-none',
  },
  {
    label: '卡片投影',
    value: 'is-shadow',
  },
  {
    label: '描底白边',
    value: 'is-border',
  },
])

// 图片倒角
const angleStyles = reactive<Sytles[]>([
  {
    label: '直角',
    value: 'is-straight',
  },
  {
    label: '圆角',
    value: 'is-circle',
  },
])

// 文本样式
const textStyles = reactive<Sytles[]>([
  {
    label: '常规',
    value: 'is-normal',
  },
  {
    label: '加粗',
    value: 'is-bold',
  },
])

// 购物按钮样式
const buttonStyles = reactive<Sytles[]>([
  {
    label: '样式一',
    value: '1',
  },
  {
    label: '样式二',
    value: 'button-style--two',
  },
  {
    label: '样式三',
    value: 'button-style--three',
  },
  {
    label: '样式四',
    value: 'button-style--four',
  },
])

// 商品角标样式
const tagStyles = reactive<Sytles[]>([
  {
    label: '新品',
    value: 'tag-style--one',
  },
  {
    label: '热卖',
    value: 'tag-style--two',
  },
  {
    label: '抢购',
    value: 'tag-style--three',
  },
])

// 选择商品弹窗
const goodsVisible = ref(false)

// 选择商品数组
const pointGoodsList = ref<any[]>([])

// 列表样式改变 只有大图和列表样式可以选择购买按钮3/4
const buttonTypeDisable = computed(() => {
  const list = ['goods-style--three', 'goods-style--four']
  const { listStyle } = subForm.value
  return list.includes(listStyle)
})

// 监听列表样式变化，更新按钮样式
watch(
  () => subForm.value.listStyle,
  (newListStyle) => {
    const list = ['goods-style--three', 'goods-style--four']
    if (list.includes(newListStyle)) {
      const value = subForm.value.showContent.buttonStyle
      subForm.value.showContent.buttonStyle = value > 2 ? 1 : value
    }
  },
  { immediate: true },
)

// 获取商品IDs
const goodsIds = computed(() => {
  return (subForm.value.goods || []).map((i) => i.id).join(',')
})

// 监听商品IDs变化
watch(goodsIds, (v = '', o = '') => {
  if (v && v.length !== o.length) {
    onDiscountList(v, o)
  }
})

// 组件挂载时执行
onMounted(() => {
  if (subForm.value.ponentType === 1) {
    filterCategory()
  } else if (subForm.value.ponentType === 2) {
    if (goodsIds.value) {
      onDiscountList(goodsIds.value, '')
    }
  }
})

// 处理formData变化
function handleFormDataChange(): void {
  if (!subForm.value.firstCatList) {
    subForm.value.firstCatList = []
  }
  if (subForm.value.currentCategoryId === undefined) {
    subForm.value.currentCategoryId = '-1'
  }
}

// 过滤不可用的商品
function onDiscountList(ids: string, old: string): void {
  // 将字符串转换为对象来满足API参数要求
  const params = ids as any
  discountList(params).then((res) => {
    if (res.code === 200) {
      const temp = res.data.map((item: any) => {
        return {
          id: item.id,
          img: item.pic || item.img,
          name: item.name,
          price: dealGoodsPrice(item),
          albumPics: item.albumPics,
          saleMode: item.saleMode,
        }
      })
      subForm.value.goods = old ? dealGoodsSort(temp, old.split(',')) : dealGoodsSort(temp, ids.split(','))
      emit('update:formData', subForm.value)
    }
  })
}

// 处理商品顺序
function dealGoodsSort(goods: GoodsItem[] = [], ids: string[] = []): GoodsItem[] {
  const temp: GoodsItem[] = []
  ids.forEach((i) => {
    const e = goods.find((t) => t.id === i)
    if (e) {
      temp.push(e)
    }
  })
  goods.forEach((i) => {
    if (!ids.includes(i.id)) {
      temp.push(i)
    }
  })
  return temp
}

// 处理商品价格
function dealGoodsPrice(item: { minPrice?: number | string; maxPrice?: number | string }): string {
  let price: any = 0
  if (item.minPrice === item.maxPrice || item.minPrice) {
    price = item.minPrice
  } else if (!item.minPrice && item.maxPrice) {
    price = item.maxPrice
  }
  return !Number.isNaN(Number(price)) ? Number(price).toFixed(2) : price
}

// 初始化数据，需要过滤掉不可用掉一级分类
async function filterCategory(): Promise<void> {
  const res = await getAllCategory({})
  const list = res.data || []
  list.forEach((i: CategoryItem) => {
    i.productNumber = (i.showCategoryVos || []).reduce((a, b) => {
      a += Number(b.productNumber || 0)
      return a
    }, 0)
    if (i.productNumber && i.productNumber > 50) i.productNumber = 50
  })
  const { firstCatList = [] } = subForm.value
  const value: CategoryItem[] = []
  firstCatList.forEach((i: CategoryItem) => {
    const category = list.find((k: CategoryItem) => k.id === i.id)
    if (category) {
      category.saleMode = i.saleMode
      value.push(category)
    }
  })
  // value.unshift({ id: "-1", name: "全部" });
  // this.$set(this.subForm, "firstCatList", value);
  subForm.value.currentCategoryId = value[0]?.id
  emit('update:formData', subForm.value)
}

// 选择商品分组
function chooseCategory(): void {
  dialogShow.value = true
}

// 移除商品
function removeGoods(index: number): void {
  subForm.value.goods.splice(index, 1)
  subForm.value.goodsCount--
  emit('update:formData', subForm.value)
}

// 增加商品
function addGoods(): void {
  pointGoodsList.value = subForm.value.goods.map((item) => {
    return {
      id: item.id,
      name: item.name,
      pic: item.img,
      minPrice: item.price,
      maxPrice: '',
      saleMode: item.saleMode,
    }
  })
  goodsVisible.value = true
}

// 取消操作
function cancelOption(): void {
  ElMessageBox.confirm('确定要退出选择商品页面? 退出后，未保存的信息将不会保留!', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    goodsVisible.value = false
    if (selectGoodsRef.value) {
      selectGoodsRef.value.search = {
        maxPrice: '',
        minPrice: '',
        name: '',
        current: 1,
        size: 10,
        saleMode: '',
        showCategoryId: '',
      }
      selectGoodsRef.value.goodsList.forEach((item: any) => {
        item.isCheck = false
        return item
      })
    }
  })
}

// 确认选择商品
function sureDig(): void {
  if (!selectGoodsRef.value) return

  const list = selectGoodsRef.value.tempGoods
  const temp: GoodsItem[] = []
  goodsVisible.value = false
  list.forEach((item: any) => {
    temp.push({
      id: item.id,
      img: item.pic || item.img,
      name: item.name,
      price: dealGoodsPrice(item),
      albumPics: item.albumPics,
    })
  })
  subForm.value.goods = temp
  subForm.value.goodsCount = temp.length
  emit('update:formData', subForm.value)
}

// 开始拖动，记录拖动的组件下角标
function handleDragstart(i: number): void {
  dragStarIndex.value = i
}

// 阻止默认行为，否则drop事件不会触发
function handleDragover(e: DragEvent): void {
  e.preventDefault()
}

// 被放置的容器触发事件，交换两个组件的位置
function handleDrop(i: number): false | void {
  if (dragStarIndex.value === i) {
    return false
  }
  const temp = subForm.value.goods.splice(dragStarIndex.value, 1, subForm.value.goods[i])
  subForm.value.goods.splice(i, 1, ...temp)
  emit('update:formData', subForm.value)
}

// 购买按钮文案
function bottonRadioChange(): void {
  switch (subForm.value.showContent.buttonStyle) {
    case 3:
      subForm.value.showContent.buttonText = '立即购买'
      break
    case 4:
      subForm.value.showContent.buttonText = '加购物车'
      break
    default:
      subForm.value.showContent.buttonText = ''
  }
  emit('update:formData', subForm.value)
}

// 被放置的容器触发事件，交换两个组件的位置
function handleDropClass(i: number): false | void {
  if (classDragIndex.value === i) {
    return false
  }
  const temp = subForm.value.firstCatList.splice(classDragIndex.value, 1, subForm.value.firstCatList[i])
  subForm.value.firstCatList.splice(i, 1, ...temp)
  emit('update:formData', subForm.value)
}

// 阻止默认行为，否则drop事件不会触发
function handleDragoverClass(e: DragEvent): void {
  e.preventDefault()
}

// 开始拖动，记录拖动的组件下角标
function handleDragClass(i: number): void {
  classDragIndex.value = i
}

// 选择一级分类
function selectedCategory(list: CategoryItem[] = []): void {
  const ls: CategoryItem[] = subForm.value.firstCatList || []
  list.forEach((i: CategoryItem) => {
    const temp = ls.find((t: CategoryItem) => t.id === i.id)
    if (!temp) {
      ls.push(i)
    }
  })
  subForm.value.firstCatList = JSON.parse(JSON.stringify(ls))
  subForm.value.currentCategoryId = list[0]?.id
  emit('update:formData', subForm.value)
}

// 删除一级分类
function handleGoodGroupDel(id: string): void {
  const firstCatList = subForm.value.firstCatList || []
  const index = firstCatList.findIndex((item: CategoryItem) => item.id === id)
  // const categoryId = this.subForm.currentCategoryId;
  firstCatList.splice(index, 1)
  // if (id === categoryId) {
  //   this.subForm.currentCategoryId = "-1";
  // }
  emit('update:formData', subForm.value)
}

// 切换显示类型
function changePonentType(): void {
  const { ponentType = 1 } = subForm.value
  if (ponentType === 1) {
    subForm.value.goods = []
  } else if (ponentType === 2) {
    // this.subForm.firstCatList = [{ id: "-1", name: "全部" }];
  }
  emit('update:formData', subForm.value)
}

// 商品数量数据切换
function productNumberChange(group: CategoryItem): void {
  if (!/^\d{1,2}$/.test(String(group.productNumber))) {
    group.productNumber = 5
  }
  emit('update:formData', subForm.value)
}
</script>

<template>
  <!-- 商品 -->
  <el-form :model="subForm" label-width="70px" class="goods__page--set">
    <el-form-item label="展示分类">
      <el-radio-group v-model="subForm.ponentType" @change="changePonentType">
        <el-radio :value="1"> 是 </el-radio>
        <el-radio :value="2"> 否 </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="subForm.ponentType === 1" label="商品分组">
      <el-button style="margin-left: 10px; border-radius: 7px; padding: 6px 9px" @click="chooseCategory"> 添加分类 </el-button>
      <span class="select__box-tips">最多添加30个商品分组</span>
    </el-form-item>
    <div v-show="subForm.ponentType === 1 && subForm.firstCatList && subForm.firstCatList.length > 0" class="class__box">
      <div class="info__tip-box">
        <span class="t_one">分类展示</span>
        <span class="t_two">商品数量</span>
        <span>操作</span>
      </div>
      <div style="height: 147px; overflow: scroll">
        <div v-for="(group, index) in subForm.firstCatList" :key="index">
          <div
            v-if="group.id !== '-1'"
            class="list__item"
            :draggable="true"
            @dragstart="handleDragClass(index)"
            @dragover="handleDragoverClass"
            @drop="handleDropClass(index)"
          >
            <span class="show__name">{{ group.name }}</span>
            <el-icon class="click__btn" @click="handleGoodGroupDel(group.id)">
              <Delete />
            </el-icon>
            <el-input-number
              v-model="group.productNumber"
              size="small"
              :min="1"
              :max="50"
              step-strictly
              :step="1"
              :controls="false"
              class="input__pro-num"
              @change="productNumberChange(group)"
            />
          </div>
        </div>
      </div>
    </div>
    <el-form-item v-if="subForm.ponentType === 2" label="添加商品">
      <span style="color: #999">鼠标拖拽调整顺序，小程序端商品按顺序显示</span>
    </el-form-item>
    <el-form-item v-if="subForm.ponentType === 2" label="商品" style="border: 1px dotted #999; padding: 12px 12px 12px 0px">
      <ul class="goods-view">
        <li
          v-for="(item, idx) in subForm.goods"
          :key="idx"
          :draggable="true"
          class="goods-view__item"
          @dragstart="handleDragstart(idx)"
          @dragover="handleDragover"
          @drop="handleDrop(idx)"
        >
          <img class="goods-view__img" :src="item.img" />
          <el-icon class="goods-view__del" @click="removeGoods(idx)">
            <CircleClose />
          </el-icon>
        </li>
        <li class="goods-view__item goods-view__add" @click="addGoods">
          <el-icon>
            <Plus />
          </el-icon>
        </li>
      </ul>
    </el-form-item>
    <el-form-item v-if="subForm.ponentType === 1" label="分类样式">
      <el-radio-group v-model="subForm.titleStyle">
        <el-radio :value="1"> 样式1 </el-radio>
        <el-radio :value="2"> 样式2 </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="列表样式">
      <el-radio-group v-model="subForm.listStyle">
        <el-radio v-for="(item, idx) in listStyles" :key="item.value" :class="[idx > 1 ? 'goods-view__addmart' : '']" :value="item.value">
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="页面边距">
      <el-slider v-model="subForm.pagePadding" :show-tooltip="false" :show-input="true" :max="30" />
    </el-form-item>
    <el-form-item label="商品间距">
      <el-slider v-model="subForm.goodPadding" :show-tooltip="false" :show-input="true" :max="30" />
    </el-form-item>
    <el-form-item label="商品样式">
      <el-radio-group v-model="subForm.goodsStyle">
        <el-radio v-for="item in goodsStyles" :key="item.value" :value="item.value">
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="图片倒角">
      <el-radio-group v-model="subForm.angle">
        <el-radio v-for="item in angleStyles" :key="item.value" :value="item.value">
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="文本样式">
      <el-radio-group v-model="subForm.textStyle">
        <el-radio v-for="item in textStyles" :key="item.value" :value="item.value">
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="显示内容">
      <div style="display: flex; flex-direction: column">
        <div>
          <el-checkbox v-model="subForm.showContent.nameShow" disabled> 商品名称 </el-checkbox>
        </div>
        <div v-if="mode === 'SQPT'">
          <el-checkbox v-model="subForm.showContent.pointShow"> 提货时间 </el-checkbox>
        </div>
        <div>
          <el-checkbox v-model="subForm.showContent.priceShow"> 商品价格 </el-checkbox>
        </div>
        <div>
          <el-checkbox v-model="subForm.showContent.buttonShow"> 购物按钮 </el-checkbox>
          <el-radio-group
            v-if="subForm.showContent.buttonShow"
            v-model="subForm.showContent.buttonStyle"
            style="margin-left: 24px"
            @change="bottonRadioChange"
          >
            <el-radio
              v-for="(item, idx) in buttonStyles"
              :key="item.value"
              :class="[idx > 2 ? 'goods-view__addmart' : '']"
              :value="idx + 1"
              :disabled="idx > 1 && buttonTypeDisable"
            >
              {{ item.label }}
            </el-radio>
            <el-input
              v-if="[3, 4].includes(subForm.showContent.buttonStyle)"
              v-model="subForm.showContent.buttonText"
              title="自定义购买按钮文案，最多输入五个字"
              maxlength="5"
              class="goods__button-text"
            />
          </el-radio-group>
        </div>
        <div>
          <el-checkbox v-model="subForm.showContent.tagShow"> 商品角标 </el-checkbox>
          <el-radio-group v-if="subForm.showContent.tagShow" v-model="subForm.showContent.tagStyle" style="margin-left: 24px">
            <el-radio v-for="(item, idx) in tagStyles" :key="item.value" :class="[idx > 2 ? 'goods-view__addmart' : '']" :value="idx + 1">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </div>
      </div>
    </el-form-item>

    <el-dialog v-model="goodsVisible" width="50%" append-to-body :before-close="cancelOption">
      <SelectGoods ref="selectGoodsRef" :point-goods-list="pointGoodsList" :goods-visible="goodsVisible" />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelOption">取 消</el-button>
          <el-button type="primary" @click="sureDig">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <Category v-if="dialogShow" ref="CategoryRef" v-model:dialog-show="dialogShow" :sub-form="subForm" @choose="selectedCategory" />
  </el-form>
</template>

<style lang="scss">
.goods__button-text {
  width: 100px;
  display: inline-block;
  height: 24px;
  line-height: 24px;

  .el-input__inner {
    height: 24px !important;
    line-height: 24px !important;
  }
}

.goods__page--set {
  .select__box-tips {
    font-size: 13px;
    color: #999;
    margin-left: 30px;
  }

  .class__box {
    background-color: #fff;
    border: 1px solid #ccc;
    max-height: 220px;
    overflow-y: scroll;
    border-radius: 4px;
    margin-bottom: 22px;
    padding-bottom: 10px;
  }

  .info__tip-box {
    height: 40px;
    background-color: #f5f5f5;
    margin-bottom: 3px;
    position: relative;
    z-index: 1000;

    span {
      display: inline-block;
      height: 40px;
      line-height: 40px;
    }

    .t_one {
      width: 170px;
      padding-left: 18px;
    }

    .t_two {
      width: 134px;
    }
  }

  .list__item {
    height: 35px;
    line-height: 35px;
    font-size: 14px;
    box-sizing: border-box;
    padding: 5px 10px;

    .show__name {
      display: inline-block;
      width: 135px;
      padding: 0px 10px;
      background-color: #f2f2f2;
      font-size: 12px;
      height: 27px;
      line-height: 27px;
      border-radius: 6px;
      white-space: nowrap;
      text-overflow: ellipsis;
      color: #333;
      border: 1px solid #ddd;
    }

    .click__btn {
      float: right;
      color: #333;
      height: 35px;
      line-height: 35px;
      cursor: pointer;
      float: right;
      margin-right: 28px;
    }

    .input__pro-num {
      width: 80px;
      float: right;
      margin-right: 70px;
    }
  }
}
</style>
