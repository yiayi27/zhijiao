<script setup lang="ts">
import { getAllTagsVipLevel } from '@/api/customer/customer'

// 定义props
const props = defineProps({
  currentGood: {
    type: Object,
    default: () => ({}),
  },
  memberFlag: {
    type: Boolean,
    default: false,
  },
})

// 响应式状态
const goodQuery = reactive<any>({})
const memberList = ref<any[]>([])
const chooseRadio = ref('0')
const setValue = ref(false)

// 监听memberFlag变化
watch(
  () => props.memberFlag,
  (newValue) => {
    if (newValue) {
      setValue.value = false
      init()
    }
  },
)

// 组件挂载时初始化
onMounted(() => {
  init()
})

// 初始化函数
function init() {
  Object.assign(goodQuery, JSON.parse(JSON.stringify(props.currentGood)))

  getAllTagsVipLevel().then((res) => {
    goodQuery.skuStocks.forEach((skuItem: any) => {
      if (skuItem.memberPrices.length === 0) {
        chooseRadio.value = String(skuItem.memberType)
        skuItem.memberPrices = []
        res.data.forEach((vipItem: any) => {
          vipItem.memberPrice = null
          // if (vipItem.isOpen) {
          skuItem.memberPrices.push({
            memberLevelId: vipItem.rankCode,
            memberLevelName: vipItem.rankName,
            memberPrice: null,
            productId: goodQuery.id,
            skuId: skuItem.id,
          })
          // }
        })
      } else {
        const newQuery = goodQuery
        const newList: any[] = []
        const vipList: any[] = []
        newQuery.skuStocks.forEach((skuItem: any) => {
          skuItem.memberPrices.forEach((memItem: any) => {
            newList.push(memItem.memberLevelId)
          })
        })

        res.data.forEach((vipItem: any) => {
          vipList.push(vipItem.rankCode)
        })
        const differenceList = vipList.filter((x) => newList.includes(x)).concat(newList.filter((x) => vipList.includes(x)))

        const addList: any[] = []
        res.data.forEach((vipItem: any) => {
          differenceList.forEach((difItem) => {
            if (vipItem.rankCode === difItem) {
              addList.push(vipItem)
            }
          })
        })
        goodQuery.skuStocks.forEach((skuItem: any) => {
          addList.forEach((vipItem) => {
            vipItem.memberPrice = null
            if (skuItem.memberPrices.length !== res.data.length && skuItem.memberPrices.length !== 0) {
              // if (vipItem.isOpen) {
              skuItem.memberPrices.push({
                memberLevelId: vipItem.rankCode,
                memberLevelName: vipItem.rankName,
                memberPrice: null,
                productId: goodQuery.id,
                skuId: skuItem.id,
              })
              // }
            }
          })
        })

        res.data.forEach((item: any) => {
          item.memberPrice = null
        })
        chooseRadio.value = String(goodQuery.skuStocks[0].memberType)
      }
    })
    memberList.value = res.data
  })
}

/**
 * 批量设置开关
 */
function control() {
  memberList.value.forEach((item) => {
    item.memberPrice = null
  })
}

/**
 * 批量设置
 */
function setByAll(e: any, index: number) {
  if (!setValue.value) {
    return
  }
  goodQuery.skuStocks.forEach((skuItem: any) => {
    skuItem.memberPrices.forEach((memItem: any, memIndex: number) => {
      if (memIndex === index) {
        memItem.memberPrice = e.target.value
      }
    })
  })
}

/**
 * 选择radio
 */
function selectRadio() {
  goodQuery.skuStocks.forEach((item: any) => {
    item.memberPrices.forEach((memItem: any) => {
      memItem.memberPrice = null
    })
  })
}

defineExpose({
  goodQuery,
  chooseRadio,
})
</script>

<template>
  <div class="fxBox">
    <div class="fxBox__top">
      <el-radio-group v-model="chooseRadio" @change="selectRadio">
        <el-radio value="0"> 不使用会员价 </el-radio>
        <el-radio value="1"> 固定金额 </el-radio>
        <el-radio value="2"> 百分比 </el-radio>
        <el-radio value="3"> 统一设置 </el-radio>
      </el-radio-group>
      <div class="fxBox__top--tip">不使用会员价即该商品不参与会员折扣，设置后启用单品折扣</div>
    </div>
    <div
      v-if="chooseRadio === '1' || chooseRadio === '2'"
      class="fxBox__list"
      :style="{
        height: goodQuery.skuStocks.length > 1 ? '400px' : '140px',
      }"
    >
      <div class="fxBox__list--firLine" :style="{ width: memberList.length > 3 ? '' : '100%' }">
        <div v-if="goodQuery.skuStocks.length > 1" class="vipItem">规格值</div>
        <div class="vipItem">实售价</div>
        <div class="vipItem">指导价</div>
        <div v-for="(item, index) in memberList" :key="index" style="width: 122px; margin: 0px 15px">
          {{ item.rankName }}
        </div>
      </div>
      <div
        class="fxBox__list--skuLine"
        :style="{
          height: goodQuery.skuStocks.length > 4 ? '273px' : '',
          width: memberList.length > 3 ? '' : '100%',
        }"
      >
        <div
          v-for="(item, index) in goodQuery.skuStocks"
          :key="index"
          class="skuLine"
          :style="{
            width: memberList.length > 3 ? '' : '100%',
            borderBottom: memberList.length > 1 ? '1px solid #ebedf1' : 'none',
          }"
        >
          <div v-if="goodQuery.skuStocks.length > 1" class="skuLine__name">
            {{ item.specs }}
          </div>
          <div class="skuLine__center" style="width: 70px">￥{{ item.price }}</div>
          <div class="skuLine__center" style="width: 180px">￥{{ item.originalPrice }}</div>
          <div v-for="(v, i) in item.memberPrices" :key="i" class="vipLine">
            <!-- @blur="validPrice($event, index, i)" -->
            <el-input-number v-model="v.memberPrice" :min="0" :controls="false" style="width: 120px; margin: 0px 10px">
              <template #append>
                {{ chooseRadio === '1' ? '元' : '%' }}
              </template>
            </el-input-number>
          </div>
        </div>
      </div>
      <div
        v-if="goodQuery.skuStocks.length > 1"
        class="fxBox__list--firLine"
        style="padding-right: 20px; margin-top: 20px; position: absolute; left: 0px; bottom: 0px"
        :style="{
          width: memberList.length > 3 ? '' : '100%',
          paddingRight: memberList.length > 3 ? '60px' : '20px',
        }"
      >
        <div style="display: flex; width: 353px; align-items: center">
          <div style="margin-right: 10px">批量设置</div>
          <el-switch v-model="setValue" active-color="#006EFF" inactive-color="#D7D7D7" @change="control" />
        </div>
        <div v-for="(item, index) in memberList" :key="index" class="vipLine">
          <el-input-number
            v-model="item.memberPrice"
            :min="0"
            :controls="false"
            style="width: 125px; margin: 0px 13px"
            @blur="setByAll($event, index)"
          >
            <template #append>
              {{ chooseRadio === '1' ? '元' : '%' }}
            </template>
          </el-input-number>
        </div>
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
