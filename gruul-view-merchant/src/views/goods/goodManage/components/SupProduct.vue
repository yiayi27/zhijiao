<script setup lang="ts">
import { ShowSupPro } from '@/api/good/goods'
import PageManage from '@/components/PageManage.vue'

// 定义props
interface Props {
  supProduct?: any
}

const props = withDefaults(defineProps<Props>(), {
  supProduct: null,
})

// 响应式状态
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const shopList = ref<any[]>([])

// 生命周期钩子
onMounted(() => {
  getShowList()
})

/**
 * 获取供应商名下商品
 */
async function getShowList() {
  const param = {
    size: pageSize.value,
    current: pageNum.value,
    providerId: props.supProduct.id,
  }
  const res = await ShowSupPro(param)
  res.data.list.forEach((item: Obj) => {
    item.allSale = 0
    const skuStocks = item.skuStocks

    if (skuStocks.length === 1) {
      item.minPrice = skuStocks[0].price
    }

    if (skuStocks.length > 1) {
      const priceList = skuStocks.map((v: Obj) => v.price)
      item.minPrice = Math.min(...priceList)
      item.maxPrice = Math.max(...priceList)
    }

    skuStocks.forEach((v: Obj) => {
      item.allSale += v.sale
    })
  })

  shopList.value = res.data.list
  total.value = res.data.total
}

/**
 * 每页条数变化
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  getShowList()
}

/**
 * 当前页变化
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getShowList()
}
</script>

<template>
  <div>
    <div v-for="(item, index) in shopList" :key="index" class="shopBox">
      <img :src="item.pic" alt="" class="shopBox__img" />
      <div class="shopBox__content">
        <div class="shopBox__content--shopName">
          {{ item.name }}
        </div>
        <!-- <div class="shopBox__content--shopSku" v-if="item.skuStocks.length > 1">
          <div
            v-for="(n, i) in item.skuStocks"
            :key="i"
            class="shopBox__content--shopSku--item"
          >{{ n.specs }}</div>
        </div> -->

        <div class="shopBox__content--price">
          <div class="shopBox__content--price--left">
            <span>
              评分:
              <span style="color: #fa6465">{{ item.score ? item.score : 5 }}</span>
            </span>
            <span>
              销量:
              <span style="color: #fa6465">{{ item.allSale }}</span>
            </span>
          </div>
          <div style="color: #fa6465">
            ￥{{ item.minPrice }}
            <span v-if="item.skuStocks.length > 1">~ ￥{{ item.maxPrice }}</span>
          </div>
        </div>
        <div class="shopBox__content--shopName" style="margin-top: 10px">商品状态：{{ item.status === 1 ? '已上架' : '已下架' }}</div>
      </div>
    </div>
    <div v-if="shopList.length === 0" class="null">暂无商品~</div>
    <PageManage
      :page-size="pageSize"
      :page-num="pageNum"
      :total="total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </div>
</template>

<style lang="scss" scoped>
.shopBox {
  border-top: 1px solid #f2f2f2;
  padding: 10px;
  display: flex;

  &__img {
    width: 100px;
    height: 100px;
  }

  &__content {
    width: 100%;
    margin-left: 20px;

    &--shopName {
      padding-top: 10px;
      display: flex;
      justify-content: flex-start;
    }

    &--shopSku {
      padding-top: 10px;
      display: flex;
      justify-content: flex-start;

      &--item {
        width: 50px;
        height: 20px;
        background-color: #f2f2f2;
        margin-right: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }

    &--price {
      display: flex;
      justify-content: space-between;
      margin-top: 20px;

      &--left {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        width: 120px;
      }
    }
  }
}

.null {
  width: 100%;
  text-align: center;
}
</style>
