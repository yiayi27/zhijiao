<script setup lang="ts">
import { getFullActivity } from '@/api/fullReduce/fullActive'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'

import { useRouter } from 'vue-router'
import Activies from './Activies.vue'

const router = useRouter()

/** 搜索活动状态 */
const activeTypeList = [
  {
    id: '3',
    label: '全部状态',
  },
  {
    id: '0',
    label: '未开始',
  },
  {
    id: '1',
    label: '进行中',
  },
  {
    id: '2',
    label: '已结束',
  },
]

/** 当前选中状态 */
const getTypeValue = ref('')

/** 固定时间 */
const useDateValue = ref('')

const total = ref(0)

/** 搜索条件 */
const search = reactive({
  current: 1,
  size: 10,
  startTime: '',
  endTime: '',
  status: '',
})

/** 满减数组 */
const fullList = ref([])

onMounted(() => {
  getFullList()
})

/**
 * 获取满减列表
 */
function getFullList() {
  getFullActivity(search).then((res) => {
    fullList.value = res.data.list
    total.value = res.data.total
  })
}

/**
 * 选择优惠券类型
 */
function selecType(e: string) {
  search.status = e !== '3' ? e : ''
  search.current = 1
  getFullList()
}

/**
 * 根据时间搜索
 */
function searchByInput(e: string[]) {
  search.current = 1
  search.startTime = e?.[0]
  search.endTime = e?.[1]

  getFullList()
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  search.size = val
  search.current = 1
  getFullList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  search.current = val
  getFullList()
}

// 导出方法供子组件调用
defineExpose({
  getFullList,
})
</script>

<template>
  <MContent>
    <template #section>
      <div class="activeTop">
        <div>
          <el-button type="primary" @click="router.push({ name: 'FullRedAdd' })"> 新增满减活动 </el-button>
          <el-select v-model="getTypeValue" placeholder="全部状态" class="activeTop__sel" @change="selecType">
            <el-option v-for="item in activeTypeList" :key="item.id" :label="item.label" :value="item.id" />
          </el-select>
        </div>
        <div style="display: flex">
          <el-date-picker
            v-model="useDateValue"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="margin-left: 40px"
            value-format="YYYY-MM-DD"
            @change="searchByInput"
          />
        </div>
      </div>
    </template>
    <template #table>
      <Activies :full-list="fullList" @refresh="getFullList" />
    </template>
    <template #pagination>
      <PageManage
        :page-size="search.size"
        :page-num="search.current"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.activeTop {
  display: flex;
  justify-content: space-between;
  align-items: center;
  &__sel {
    margin-left: 20px;
    width: 120px;
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
</style>
