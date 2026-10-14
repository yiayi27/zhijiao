<script setup lang="ts">
import { useRouter } from 'vue-router'

interface Props {
  currentGood?: any
  rebatesFlag?: any
}

const props = defineProps<Props>()
const router = useRouter()

const reList = ref<any>({})
const chooseRadio = ref('0')
const setValue = ref(false)
/** 批量设置返利金额 */
const rebatePrice = ref(0)

watch(
  () => props.rebatesFlag,
  () => {
    if (props.rebatesFlag) {
      setValue.value = false
      rebatePrice.value = 0
      init()
    }
  },
)

onMounted(() => {
  init()
})

function init() {
  reList.value = JSON.parse(JSON.stringify(props.currentGood))
  const list = reList.value
  list.skuStocks.forEach((skuItem: any) => {
    if (skuItem.skuRebates.length === 0) {
      chooseRadio.value = String(skuItem.rebateType)
      skuItem.skuRebates = []
      skuItem.skuRebates.push({
        rebatePrice: null,
        productId: list.id,
        skuId: skuItem.id,
      })
    } else {
      chooseRadio.value = String(list.skuStocks[0].rebateType)
    }
  })
}

/**
 * 批量设置开关
 */
function control() {
  reList.value.skuStocks.forEach((item: any) => {
    item.skuRebates[0].rebatePrice = null
  })
}

/**
 * 批量设置
 */
function setByAll(e: any) {
  if (!setValue.value) {
    return
  }
  reList.value.skuStocks.forEach((skuItem: any) => {
    skuItem.skuRebates[0].rebatePrice = e.target.value
  })
}

function jumpLocation() {
  router.push({ name: 'SettingIndex', params: { options: '1' } })
}

/**
 * 顶部切换使用方式
 */
function selectRadio() {
  reList.value.skuStocks.forEach((item: any) => {
    item.skuRebates.forEach((skuItem: any) => {
      skuItem.rebatePrice = null
    })
  })
}

defineExpose({
  reList,
  chooseRadio,
})
</script>

<template>
  <div class="fxBox">
    <div class="fxBox__top">
      <el-radio-group v-model="chooseRadio" @change="selectRadio">
        <el-radio value="0"> 不使用返利 </el-radio>
        <el-radio value="1"> 固定金额 </el-radio>
        <el-radio value="2"> 百分比 </el-radio>
        <el-radio value="3"> 统一设置 </el-radio>
      </el-radio-group>
      <div class="fxBox__top--tip">
        不使用返利即该商品不参与，设置后启用单品返利，前往
        <el-button link @click="jumpLocation"> 返利设置 </el-button>配置
      </div>
    </div>
    <div
      v-if="chooseRadio === '1' || chooseRadio === '2'"
      class="fxBox__list"
      :style="{
        height: reList.skuStocks.length > 1 ? '400px' : '140px',
      }"
    >
      <div class="fxBox__list--firLine">
        <div v-if="reList.skuStocks.length > 1" class="vipItem">规格值</div>
        <div class="vipItem">实售价</div>
        <div class="vipItem">指导价</div>
        <div class="vipItem">返利金额</div>
      </div>
      <div class="fxBox__list--skuLine" :style="{ height: reList.skuStocks.length > 4 ? '273px' : '' }">
        <div v-for="(v, i) in reList.skuStocks" :key="i">
          <div
            v-for="(item, index) in v.skuRebates"
            :key="index"
            class="skuLine"
            :style="{
              borderBottom: reList.skuStocks.length > 1 ? '1px solid #ebedf1' : 'none',
            }"
          >
            <div v-if="reList.skuStocks.length > 1" class="skuLine__name">
              {{ v.specs }}
            </div>
            <div class="skuLine__center" style="width: 70px">￥{{ v.price }}</div>
            <div class="skuLine__center" style="width: 180px">￥{{ v.originalPrice }}</div>
            <el-input-number v-model="item.rebatePrice" :min="0" :controls="false" style="width: 125px; margin: 0px 13px">
              <template #append>
                {{ chooseRadio === '1' ? '元' : '%' }}
              </template>
            </el-input-number>
          </div>
        </div>
      </div>
      <div
        v-if="reList.skuStocks.length > 1"
        class="fxBox__list--firLine"
        style="padding-right: 20px; margin-top: 20px; position: absolute; left: 0px; bottom: 0px"
      >
        <div style="display: flex; width: 353px; align-items: center">
          <div style="margin-right: 10px">批量设置</div>
          <el-switch v-model="setValue" active-color="#006EFF" inactive-color="#D7D7D7" @change="control" />
        </div>
        <el-input-number v-model="rebatePrice" :min="0" :controls="false" style="width: 125px; margin: 0px 13px" @blur="setByAll($event)">
          <template #append>
            {{ chooseRadio === '1' ? '元' : '%' }}
          </template>
        </el-input-number>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.fxBox {
  margin-top: 0px;

  &__top {
    display: flex;
    align-items: center;

    &--tip {
      font-size: 12px;
      margin-left: 40px;
      color: #aeaeae;
    }
  }

  &__list {
    margin-top: 20px;
    height: 400px;
    border: 1px solid #ebedf1;
    width: 834px;
    overflow-x: auto;
    display: -webkit-box;
    position: relative;

    &--firLine {
      height: 50px;
      width: 100%;
      display: flex;
      align-items: center;
      padding-left: 25px;
      padding-right: 30px;
      background-color: #f2f4f7;

      &--vip {
        width: 450px;
        display: flex;
      }

      .vipItem {
        margin-right: 46px;
        width: 80px;
        display: flex;
      }
    }

    &--skuLine {
      position: absolute;
      top: 55px;
      left: 0px;
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      // width: 100%;
      overflow: auto;

      .skuLine {
        display: flex;
        align-items: center;
        // margin-bottom: 20px;
        // padding-bottom: 20px;
        border-bottom: 1px solid #ebedf1;
        padding-right: 33px;
        height: 70px;
        width: 100%;

        &__name {
          width: 130px;
          display: flex;
          justify-content: flex-start;
          padding-left: 25px;
        }

        &__center {
          display: flex;
          justify-content: center;
        }
      }

      .vipLine {
        display: flex;
        width: 150px;
        justify-content: center;
      }
    }
  }
}
.fxBox__list--skuLine::-webkit-scrollbar {
  display: none;
}
// .fxBox__list--firLine--vip::-webkit-scrollbar {
//   // display: none;
//   height: 2px;
//   padding-top: 5px;
// }
</style>
