<script setup lang="ts">
import { delFullActivity, fullActivityDet, offFullActivity } from '@/api/fullReduce/fullActive'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import EndContent from '../../common/EndContent.vue'

// 定义活动项类型
interface ActivityItem {
  id: string
  name: string
  status: number
  startTime: string
  endTime: string
  productNumber: number
  peopleNum?: number
  payOrder?: number
  gaapTotal?: string | number
  [key: string]: any
}

const props = defineProps({
  fullList: {
    type: Array as PropType<ActivityItem[]>,
    default: () => [],
  },
})
const emit = defineEmits(['refresh'])

const router = useRouter()
const hasList = ref(false)

/** 查看结束活动弹窗 */
const endVisible = ref(false)

/** 选中活动 */
const chooseItem = ref({})

// 监听fullList变化
watch(
  () => props.fullList,
  () => {
    hasList.value = props.fullList.length === 0
  },
  { immediate: true },
)

/**
 * 编辑/查看活动
 */
function editFullAct(item: ActivityItem) {
  if (item.status === 2) {
    endVisible.value = true
    fullActivityDet(item.id, {}).then((res) => {
      const discountProductVos = res.data.discountProductVos
      chooseItem.value = { ...res.data.fullScaleVo, discountProductVos }
    })
  } else {
    router.push({ name: 'FullRedAdd', query: { id: item.id } })
  }
}

/**
 * 提示语
 */
function overAct(item: ActivityItem) {
  ElMessageBox.confirm(`确认${item.status === 1 ? '关闭' : '删除'}选中活动吗？${item.status === 1 ? '关闭' : '删除'}后无法恢复`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    dealSure(item)
  })
}

/**
 * 关闭/删除活动
 */
async function dealSure(item: ActivityItem) {
  const ids: string[] = []
  ids.push(item.id)
  const res = item.status === 1 ? await offFullActivity(2, ids) : await delFullActivity(item.id, {})
  if (res.code === 200) {
    ElMessage.success(`${item.status === 1 ? '关闭成功' : '删除成功'}`)
    emit('refresh')
  }
}

// 导出方法供父组件使用
defineExpose({
  editFullAct,
  overAct,
  dealSure,
})
</script>

<template>
  <div>
    <div class="list">
      <m-card v-for="(item, index) in fullList" :key="index" class="list__item">
        <div v-if="fullList.length > 0" class="list__item--top">
          <div class="list__item--top--actName">
            <span>{{ item.name }}</span>
            <span v-if="item.status === 0" style="color: #499af2">未开始</span>
            <span v-if="item.status === 1" style="color: #ff0000">进行中</span>
            <span v-if="item.status === 2" style="color: #868686">已结束</span>
          </div>
          <span class="list__item--top--actTime">活动时间：{{ item.startTime }} 至 {{ item.endTime }}</span>
          <div class="list__item--top--actTime">活动商品：{{ item.productNumber }} 件</div>
          <div class="list__item--top--account">
            <span>参加人数：{{ item.peopleNum ? item.peopleNum : 0 }}</span>
            <span>支付单数：{{ item.payOrder ? item.payOrder : 0 }}</span>
            <span>应收金额：{{ item.gaapTotal ? item.gaapTotal : '0.00' }}</span>
          </div>
        </div>
        <div v-if="fullList.length > 0" class="list__item--right">
          <el-button-group>
            <el-button @click="editFullAct(item)">
              {{ item.status === 2 ? '查看活动' : '编辑活动' }}
            </el-button>
            <el-button @click="overAct(item)">
              {{ item.status === 1 ? '关闭活动' : '删除活动' }}
            </el-button>
          </el-button-group>
        </div>
      </m-card>
      <div v-if="hasList" class="emptyLine">暂无数据~</div>
    </div>
    <el-dialog v-model:visible="endVisible" width="65%">
      <EndContent :choose-item="chooseItem" />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.list {
  margin-top: 15px;
  background-color: white;
  &__item {
    margin-bottom: 10px;
    display: flex;
    justify-content: space-between;
    &--top {
      width: 500px;
      &--actName {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        font-weight: bold;
        margin-bottom: 10px;
      }
      &--actTime {
        display: flex;
        font-size: 12px;
        margin-bottom: 10px;
      }
      &--account {
        color: #8b8b8b;
        font-size: 12px;
        display: flex;
        justify-content: space-between;
        width: 380px;
      }
    }

    &--right {
      display: flex;
      align-items: center;
    }
  }
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
  font-size: 14px;
  color: #b3b3b3;
  border-bottom: 1px solid #ebeef5;
  border-top: 1px solid #ebeef5;
}
</style>
