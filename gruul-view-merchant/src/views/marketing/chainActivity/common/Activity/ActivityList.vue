<script setup lang="ts">
import { transFormActivityById } from '@/api/chainActivity/chainActivity'
import { useUserStore } from '@/store/modules/user'

// 定义活动列表项接口
interface ActivityItem {
  id: string
  name: string
  status: number
  startTime: string
  endTime: string
  coverPoster: string
  productNumber: number
  userNumber: number
  orderNumber: number
  totalAmount: number
  [key: string]: any
}

defineProps({
  list: {
    type: Array as PropType<ActivityItem[]>,
    default: () => [],
  },
})

// 定义emits
const emit = defineEmits(['edit', 'close', 'del', 'setLoading'])

/**
 * 编辑活动
 */
function edit(item: ActivityItem) {
  emit('edit', item.id)
}

/**
 * 关闭活动
 */
function close(item: ActivityItem) {
  emit('close', item.id)
}

/**
 * 删除活动
 */
function del(item: ActivityItem) {
  emit('del', item.id)
}

const userStore = useUserStore()

/**
 * 复制活动链接
 */
function copyActivityLink(item: ActivityItem) {
  emit('setLoading', true)

  const shopId = userStore?.shopInfo?.platformShopId
  const url = `https://${window.location.host}/h5/${import.meta.env.VUE_APP_APP_VERSION || ''}?&shopId=${shopId}&actId=${item.id}`
  const param = {
    path: url,
  }

  transFormActivityById(item.id, param)
    .then((res) => {
      // 使用第三方复制文本功能，需要检查对应插件是否已引入
      if (typeof navigator.clipboard?.writeText === 'function') {
        navigator.clipboard.writeText(res.data)
        ElMessage.success('复制成功')
      } else {
        // 创建文本域
        const textarea = document.createElement('textarea')
        textarea.value = res.data
        textarea.style.position = 'fixed'
        textarea.style.opacity = '0'
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
        ElMessage.success('复制成功')
      }
    })
    .finally(() => {
      emit('setLoading', false)
    })
}
</script>

<template>
  <div>
    <div v-for="(item, index) in list" :key="index" class="list">
      <div class="list__content">
        <div class="list__content--left">
          <div class="leftTop">
            <div style="width: 90px; height: 90px; position: relative">
              <img :src="item.coverPoster" style="width: 90px; height: 90px" />
              <div v-if="item.status === 1" class="tip" style="background-color: #00cc33">进行中</div>
              <div v-if="item.status === 0" class="tip" style="background-color: #2d8cf0">未开始</div>
              <div v-if="item.status === 2" class="tip" style="background-color: #c9c9c9">已结束</div>
            </div>
            <div class="leftTop__content">
              <div class="leftTop__content--name">
                {{ item.name }}
              </div>
              <div class="leftTop__content--tip">活动时间：{{ item.startTime }} - {{ item.endTime }}</div>
              <div class="leftTop__content--tip">活动商品：{{ item.productNumber }}件</div>
            </div>
          </div>
          <div class="leftBottom">
            <span v-if="item.status === 2" class="leftBottom__btn" @click="edit(item)">复制活动</span>
            <span v-else class="leftBottom__btn" @click="edit(item)">编辑活动</span>
            <span v-if="item.status === 1" class="leftBottom__btn" @click="close(item)">关闭活动</span>
            <span v-else class="leftBottom__btn" @click="del(item)">删除活动</span>
            <span class="leftBottom__btn" @click="copyActivityLink(item)">复制链接</span>
          </div>
        </div>
        <div class="list__content--right">
          <div class="listItem">参加人数 {{ item.userNumber }}</div>
          <div class="listItem">支付单数 {{ item.orderNumber }}</div>
          <div class="listItem">应收金额 {{ item.totalAmount.toFixed(2) }}</div>
        </div>
      </div>
    </div>
    <div v-if="list.length === 0" class="emptyLine">暂无数据~</div>
  </div>
</template>

<style lang="scss" scoped>
.list {
  display: flex;
  &__content {
    width: 100%;
    height: 170px;
    padding: 20px;
    background-color: white;
    margin-top: 20px;
    display: flex;

    &--left {
      display: flex;
      flex-wrap: wrap;
      width: 660px;
      position: relative;

      .leftTop {
        display: flex;

        &__content {
          display: flex;
          flex-wrap: wrap;
          margin-left: 20px;
          padding-top: 5px;

          &--name {
            font-weight: bold;
            font-size: 15px;
            width: 380px;
            margin-bottom: 15px;
          }

          &--tip {
            font-size: 14px;
            color: #666666;
            width: 380px;
          }
        }
      }

      .leftBottom {
        display: flex;
        margin-top: 10px;

        &__btn {
          width: 100px;
          height: 34px;
          display: flex;
          justify-content: center;
          align-items: center;
          border: 1px solid #d4d4d4;
          color: #333333;
          font-size: 12px;
          border-radius: 5px;
          margin-right: 20px;
          cursor: pointer;
        }
      }
    }

    &--right {
      width: 150px;
      border-left: 1px solid #e4e4e4;
      padding-left: 20px;
      display: flex;
      align-content: space-between;
      flex-wrap: wrap;
      .listItem {
        width: 180px;
        margin: 5px 0px 5px 0px;
        color: #666666;
      }
    }
  }
}

.tip {
  position: absolute;
  width: 55px;
  height: 20px;
  color: white;
  top: 0px;
  left: 35px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.emptyLine {
  width: 100%;
  height: 80px;
  background-color: white;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  margin-top: 15px;
  font-size: 14px;
  color: #b3b3b3;
  border-bottom: 1px solid #ebeef5;
  border-top: 1px solid #ebeef5;
}
</style>
