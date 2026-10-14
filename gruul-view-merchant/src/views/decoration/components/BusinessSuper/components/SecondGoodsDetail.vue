<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'

defineProps({
  formData: {
    type: Object as () => typeof BusinessSuper,
    required: true,
  },
})

/**
 * 设置商品class值
 */
const getGoodsClass = computed(() => {
  const { listStyle = 1 } = {}
  // 1列表模式 2大图模式
  const styles = ['spellpre__goodsL', 'spellpre__goodsB']
  return styles[+listStyle - 1]
})

/**
 * 动态设置商品style
 */
const getGoodsStyle = computed(() => {
  const padding = `${+16 / 2}px 10px`
  return { padding }
})

/**
 * 获取商品边框样式
 */
const getGoodsBoxClass = computed(() => {
  const { goodsStyle = 1, doodsAngle = 1 } = {}
  // 商品样式 1无底白边 2卡片投影 3描边白底
  const gstyles = ['spellpre__goods--boxWB', 'spellpre__goods--boxCP', 'spellpre__goods--boxSW']
  // 图片倒角 1直角 2圆角
  const astyles = ['spellpre__goods--corners', 'spellpre__goods--angle']
  const gs = gstyles[+goodsStyle - 1]
  const as = astyles[+doodsAngle - 1]
  return `${gs} ${as}`
})

/**
 * 获取商品名称文本样式
 */
const getGoodsNameClass = computed(() => {
  const { textStyle = 1 } = {}
  // 1常规体 2加粗体
  const styles = ['spellpre__goods--nameF', 'spellpre__goods--nameB']
  return styles[+textStyle - 1]
})
</script>

<template>
  <div class="second__goodsDetail-page top__biggoods-page">
    <div v-for="(item, index) in +5" :key="`F${index}`" class="spellpre__goods" :class="[getGoodsClass]" :style="getGoodsStyle">
      <div class="spellpre__goods--box" :class="[getGoodsBoxClass]">
        <div class="spellpre__goodsL--content">
          <div class="spellpre__goodsL--icon">
            <div class="ipic">
              <img :src="formData.goods.img" alt="" />
            </div>
          </div>
          <div class="spellpre__goodsL--info">
            <div class="spellpre__goodsL--name" :class="[getGoodsNameClass]">
              {{ formData.goods.name }}
            </div>
            <div class="spellpre__goods--delivery">提货时间：06月24日 14:00</div>
            <div class="spellpre__goodsL--buy">
              <span class="spellpre__goodsL--prinum">{{ `¥${formData.goods.price}` }}</span>
              <div class="spellpre__goods--cart" :class="[`spellpre__goods--cart${formData.cartButton}`]">加购物车</div>
              <div>
                <s class="spellpre__goodsL--guaid">{{ `¥${formData.goods.guide}` }}</s>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
@import '@/assets/styles/decoration/businessGoods.scss';
.second__goodsDetail-page {
  background-color: #f5f5f5;
  height: 100%;
  overflow-y: scroll;
  box-sizing: border-box;
  padding: 5px 0px;

  .spellpre__goods {
    background-color: #fff;
    border-bottom: 1px solid rgb(247, 247, 247);
  }

  .spellpre__goods--cart {
    margin-top: 7px;
  }
}
</style>
