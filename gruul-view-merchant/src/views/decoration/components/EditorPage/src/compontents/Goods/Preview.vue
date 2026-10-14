<script setup lang="ts">
import Goods from './Goods'
import { useVModel } from '@vueuse/core'

// 定义接口
interface CategoryItem {
  id: string
  name: string
  productNumber: number | string
  [key: string]: any
}

interface IShowContent {
  nameShow: boolean
  priceShow: boolean
  buttonShow: boolean
  buttonStyle: number
  buttonText: string
  tagShow: boolean
  tagStyle: number
  pointShow?: boolean
  [key: string]: any
}

// 定义props
const props = defineProps({
  formData: {
    type: Object as () => typeof Goods,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:formData'])

const formData = useVModel(props, 'formData', emit)

// 商品列表
const goodsList = ref<any>([])

// 计算页面样式
const pageStyle = computed(() => {
  return {
    padding: `0px ${formData.value.pagePadding}px`,
    minHeight: '261px',
  }
})

// 动态显示商品边框样式
const goodStyle = computed(() => {
  let margin = ''
  let width = ''
  const { listStyle, goodPadding = 0 } = formData.value
  switch (listStyle) {
    case 'goods-style--four':
      margin = `0px ${goodPadding}px 0px 0px`
      break
    case 'goods-style--two':
      margin = `0px 0px ${goodPadding}px 0px`
      width = `calc(50% - ${goodPadding / 2}px)`
      break
    default:
      margin = `0px 0px ${goodPadding}px 0px`
      break
  }

  return { margin, width }
})

// 如果是模式2调整边距
const goodStyle2 = computed(() => {
  let margin = ''
  const { listStyle } = formData.value
  switch (listStyle) {
    case 'goods-style--two':
      margin = `0px 0px ${formData.value.goodPadding}px 0px`
      break
  }
  return { margin }
})

// 获取商品角标地址及样式
const getGoodsCornerMark = computed(() => {
  const { tagStyle } = formData.value.showContent
  // 商品角标 1新品 2热卖 3抢购
  const srcs = [
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/37a28e49acb448fc8618d5e72851b027.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/1622a28ef72040f9a2f367a2efa7c32d.png',
    'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20201026/0f33444422b14a8e980cc50d0011e2d0.png',
  ]
  const styles = ['goods-item__coner1', 'goods-item__coner2', 'goods-item__coner3']
  return {
    url: srcs[+tagStyle - 1],
    class: styles[+tagStyle - 1],
  }
})

// 监听formData变化
watch(
  () => props.formData,
  () => {
    formData.value = props.formData
    getGoodsList()
  },
  { deep: true },
)

// 显示的商品数量
function getGoodsList() {
  const { ponentType = 1, currentCategoryId = '', firstCatList = [], goods = [] } = formData.value

  // 选择商品数量时对应显示预览效果
  if (ponentType === 1) {
    const typedList = firstCatList as CategoryItem[]
    const item = typedList.find((i) => i.id === currentCategoryId) || { productNumber: 0 }
    goodsList.value = Number(item.productNumber || 0)
  } else if (ponentType === 2) {
    goodsList.value = goods
  }
}

// 切换一级分类
function activeClass(id: string) {
  formData.value.currentCategoryId = id
  emit('update:formData', formData.value)
}

// 组件挂载时执行
onMounted(() => {
  getGoodsList()
})
</script>

<template>
  <!-- 商品 -->
  <div class="goods__ponent-page">
    <div
      v-if="
        (formData.ponentType === 1 && !(formData.firstCatList && formData.firstCatList.length)) ||
        (formData.ponentType === 2 && !formData.goods.length)
      "
      class="no__goods-item"
    >
      <img src="https://qiniu-app.qtshe.com/u391.png" />
    </div>
    <div v-if="formData.ponentType === 1 && formData.firstCatList && formData.firstCatList.length" class="tab__bar-box">
      <div class="con">
        <div
          v-for="(item, idx) in formData.firstCatList"
          :key="idx"
          class="class__item"
          :class="[(item as CategoryItem).id === formData.currentCategoryId ? ['', 'class__active', 'class__new-active'][formData.titleStyle] : '']"
          @click="activeClass((item as CategoryItem).id)"
        >
          <span class="item__name">{{ (item as CategoryItem).name }}</span>
        </div>
      </div>
    </div>
    <div
      v-if="
        (formData.ponentType === 1 && formData.firstCatList && formData.firstCatList.length) || (formData.ponentType === 2 && formData.goods.length)
      "
      class="goods"
      :class="formData.listStyle"
      :style="pageStyle"
    >
      <div
        v-for="(item, idx) in goodsList"
        :key="idx"
        class="goods-item"
        :class="[formData.angle, formData.goodsStyle]"
        :style="idx === formData.goods.length - 1 ? goodStyle2 : goodStyle"
      >
        <div v-if="formData.showContent.tagShow" class="goods-item__coner" :class="[getGoodsCornerMark.class]">
          <img :src="getGoodsCornerMark.url" />
        </div>
        <div class="goods-item__icon">
          <div class="ipic">
            <img :class="[formData.ponentType === 1 ? 'show__mall' : 'show__big']" src="https://qiniu-app.qtshe.com/u391.png" />
          </div>
        </div>

        <div class="goods-item__foot" style="padding: 0 7px">
          <div v-if="formData.showContent.nameShow" :class="formData.textStyle" class="goods-item__name">
            {{ item.name || '商品名称' }}
          </div>
          <div v-if="formData.showContent?.pointShow" class="spellpre__goods--delivery">
            <span class="i_box">次日达</span>
          </div>
          <div v-if="formData.showContent.priceShow || formData.showContent.buttonShow" class="goods-item__bottom">
            <div class="goods-item__price">
              <span v-if="formData.showContent.priceShow">￥{{ item.price || 99 }}</span>
            </div>
            <div v-if="formData.showContent.buttonShow && formData.showContent.buttonStyle === 1" class="goods-item__cart goods-item__cart1">
              <img src="https://oss-tencent.bgniao.cn//gruul/20200710/810700b67e264a7b82bba347528b9eb0.png" />
            </div>
            <div v-if="formData.showContent.buttonShow && formData.showContent.buttonStyle === 2" class="goods-item__cart goods-item__cart2">
              <img src="https://oss-tencent.bgniao.cn//gruul/20200710/3de00f3d8099459c83771b1f4decb197.png" />
            </div>
            <div
              v-if="formData.showContent.buttonShow && (formData.showContent.buttonStyle === 3 || formData.showContent.buttonStyle === 4)"
              class="goods-item__cart"
              :class="[`goods-item__cart${formData.showContent.buttonStyle}`]"
            >
              {{ formData.showContent.buttonText || '立即购买' }}
            </div>
          </div>
          <div
            v-if="formData.listStyle === 'goods-style--one'"
            class="goods-item__delivery"
            :class="[(formData.listStyle as string) === 'goods-style--four' ? 'goods-item__delivery3' : '']"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.goods__ponent-page {
  .tab__bar-box {
    height: 50px;
    width: 100%;
    overflow: hidden;

    .con {
      width: auto;
      height: 60px;
    }

    .class__item {
      display: inline-block;
      height: 50px;
      position: relative;
      color: #6b6b6b;
      font-size: 14px;
      padding: 0px 12px;
      padding-top: 15px;
      cursor: pointer;
    }

    .class__active {
      color: #000000;
      font-size: 17px;
      padding-top: 8px;
      background: linear-gradient(#e5382e, #fd4e26) no-repeat;
      background-size: calc(100% - 22px) 8px;
      background-position: 11px 1.16em;
    }

    .class__new-active {
      color: #000000;
      font-size: 17px;
      padding-top: 8px;

      span {
        display: inline-block;
        padding-bottom: 4px;
        border-bottom: 2px solid red;
      }
    }

    .first__class {
      padding-left: 0;
      background-size: calc(100% - 11px) 8px;
      background-position: 0px 1.16em;
    }
  }

  .goods-item__icon {
    width: 100%;
    height: 180px;

    .ipic {
      display: inline-block;
      width: 100%;
      height: 180px;
      background-color: rgba(233, 247, 253, 1);
      display: flex;
      justify-content: center;
      align-items: center;

      .show__mall {
        display: inline-block;
        width: 44px;
        height: 46px;
      }

      .show__big {
        display: inline-block;
        width: 100%;
        height: 100%;
      }
    }
  }

  .goods-style--three {
    .goods-item__icon {
      height: 128px;
      width: 128px;
      margin-right: 10px;
      flex: none;

      .ipic {
        height: 128px;
      }
    }
  }

  .spellpre__goods--delivery {
    color: #a3a3a3;
    font-size: 12px;
    font-weight: 400;
    padding-top: 10px;
  }
}
</style>
