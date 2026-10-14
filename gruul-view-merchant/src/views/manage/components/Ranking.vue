<script setup lang="ts">
import { getAccountGVMRank, getAccountRank } from '@/api/manage/manage'

// 定义产品排行的接口
interface ProductRankItem {
  id: string
  url: string
  nick: string
  price: number | string
  tradingVolume: number | string
  tradeVolume?: number | string
  [key: string]: any
}

// 响应式状态
const topName = ref('0')
const rankActive = ref(0)

// 商品排行数据
const productVBRanking = ref<ProductRankItem[]>([])
const gvmSproductVBRanking = ref<ProductRankItem[]>([])

// 初始化数据
async function init() {
  const res = await getAccountRank()
  const gvmRes = await getAccountGVMRank()
  productVBRanking.value = res.data.productVBRanking
  gvmSproductVBRanking.value = gvmRes.data.productGMVRanking
}

// 点击排行榜切换
function clickRankTop(type: number) {
  rankActive.value = type
}

// 组件挂载时初始化
onMounted(() => {
  init()
  topName.value = '2'
})
</script>

<template>
  <div class="ranking">
    <div style="width: 645px">
      <div class="ranking__rank">
        <div style="width: 120px; display: flex; flex-wrap: wrap; justify-content: center">
          <div :class="[rankActive === 0 ? 'ranking__rank--noType' : 'ranking__rank--type']" @click="clickRankTop(0)">商品交易量排行</div>
          <div :class="[rankActive === 0 ? 'ranking__rank--Line' : '']" />
        </div>
        <div style="width: 120px; display: flex; flex-wrap: wrap; justify-content: center">
          <div :class="[rankActive === 1 ? 'ranking__rank--noType' : 'ranking__rank--type']" @click="clickRankTop(1)">商品交易额排行</div>
          <div :class="[rankActive === 1 ? 'ranking__rank--Line' : '']" />
        </div>
      </div>
    </div>
    <!-- 商品销量排行榜 商品交易量 -->
    <div v-if="topName === '2' && rankActive === 0" class="ranking__content">
      <div v-for="(item, index) in productVBRanking" :key="index" class="ranking__list">
        <div v-if="productVBRanking.length" class="ranking__list--line">
          <div class="ranking__list--line--left">
            <span class="indexStyle">{{ index + 1 }}</span>
            <img :src="item.url" style="width: 50px; height: 50px; border-radius: 50px" />
            <div class="userBox">
              <div class="userBox__name">
                {{ item.nick }}
              </div>
              <div class="userBox__community" style="color: #ff4d4d; font-weight: bold">￥{{ item.price }}</div>
            </div>
          </div>
          <div class="ranking__list--line--right">{{ item.tradingVolume }}</div>
        </div>
        <div v-else class="noRank">暂无排行数据~</div>
      </div>
    </div>
    <!-- 商品销量排行榜 商品交易额 -->
    <div v-if="topName === '2' && rankActive === 1" class="ranking__content">
      <div v-for="(item, index) in gvmSproductVBRanking" :key="index" class="ranking__list">
        <div v-if="gvmSproductVBRanking.length" class="ranking__list--line">
          <div class="ranking__list--line--left">
            <span class="indexStyle">{{ index + 1 }}</span>
            <img :src="item.url" style="width: 50px; height: 50px; border-radius: 50px" />
            <div class="userBox">
              <div class="userBox__name">
                {{ item.nick }}
              </div>
              <div class="userBox__community" style="color: #ff4d4d; font-weight: bold">￥{{ item.price }}</div>
            </div>
          </div>
          <div class="ranking__list--line--right">
            {{ item.tradeVolume }}
          </div>
        </div>
        <div v-else class="noRank">暂无排行数据~</div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.ranking {
  display: flex;
  flex-direction: column;
  width: 100%;
  justify-content: space-between;
  background-color: white;
  height: 100%;

  .ranking__content {
    width: 100%;
    flex: 1;
    overflow: auto;
  }

  .ranking__rank {
    display: flex;
    width: 100%;
    margin-top: 10px;
    align-items: center;

    .ranking__rank--type {
      width: 120px;
      height: 2px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #6c7485;
      font-size: 12px;
      font-weight: bold;
      cursor: pointer;
    }

    .ranking__rank--Line {
      width: 90px;
      height: 2px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 13px;
      background-color: #fd7c25;
    }

    .ranking__rank--noType {
      width: 120px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fd7c25;
      font-size: 12px;
      font-weight: bold;
      cursor: pointer;
    }
  }

  .ranking__list {
    margin-top: 10px;
    width: 100%;
    padding: 10px;
    padding-right: 40px;

    .ranking__list--line {
      display: flex;
      width: 100%;
      align-items: center;
      justify-content: space-between;

      .ranking__list--line--left {
        display: flex;
        width: 560px;
        align-items: center;
      }

      .ranking__list--line--right {
        color: #515a6e;
        margin-left: 20px;
        font-weight: bold;
      }
    }
  }
}

.noRank {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 200px;
  color: #adaeb0;
  font-size: 15px;
}

.indexStyle {
  width: 25px;
  height: 25px;
  color: #fd9047;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  margin-right: 20px;
}

.userBox {
  display: flex;
  margin-left: 20px;
  flex-wrap: wrap;
  width: 370px;

  .userBox__name {
    width: 300px;
    color: #515a6e;
  }

  .userBox__community {
    width: 340px;
    margin-top: 10px;
    color: #868c9a;
  }
}

:deep(.el-tabs--border-card) {
  border: none;
  width: 100%;
  box-shadow: none;
  -webkit-box-shadow: none;
}

:deep(.el-tabs--border-card > .el-tabs__content) {
  padding: 0px;
}
</style>
