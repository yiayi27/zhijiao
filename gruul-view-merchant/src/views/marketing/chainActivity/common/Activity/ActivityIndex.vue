<script setup lang="ts">
import { closeActivityById, delActivityById, getActivityList } from '@/api/chainActivity/chainActivity'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

import { useRouter } from 'vue-router'
import ActivityList from './ActivityList.vue'

const router = useRouter()

const statusList = [
  {
    value: '全部状态',
    id: '',
  },
  {
    value: '未开始',
    id: '0',
  },
  {
    value: '进行中',
    id: '1',
  },
  {
    value: '已结束',
    id: '2',
  },
]

// 接龙活动商品
const list = ref([])
const selectValue = ref('')
const useDateValue = ref([])
const activityName = ref('')
const dateList = reactive({
  startTime: '',
  endTime: '',
})
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const fullscreenLoading = ref(false)

/**
 * 新增活动
 */
function addActivity() {
  router.push({
    name: 'AddChainActivity',
  })
}

/**
 * 搜索列表
 */
function searchGetList() {
  pageNum.value = 1
  getList()
}

/**
 * 获取接龙活动列表
 */
function getList() {
  const param = {
    current: pageNum.value,
    size: pageSize.value,
    startTime: dateList.startTime,
    endTime: dateList.endTime,
    status: selectValue.value,
    name: activityName.value,
  }
  getActivityList(param).then((res) => {
    list.value = res.data.list
    total.value = res.data.total
  })
}

/**
 * 选择活动时间
 */
function timeSure(e: string[] | null) {
  dateList.startTime = e?.[0] || ''
  dateList.endTime = e?.[1] || ''
  pageNum.value = 1
  getList()
}

/**
 * 编辑活动
 */
function edit(id: string) {
  router.push({
    name: 'AddChainActivity',
    query: { activityId: id },
  })
}

/**
 * 关闭活动
 */
function close(id: string) {
  ElMessageBox.confirm(`确认关闭选中活动吗？关闭后接龙活动无法恢复`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    closeActivityById(id, {}).then(() => {
      ElMessage.success('关闭成功')
      getList()
    })
  })
}

/**
 * 删除活动
 */
function del(id: string) {
  ElMessageBox.confirm(`确认删除选中活动吗？删除后接龙活动无法恢复`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delActivityById(id, {}).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  pageNum.value = 1
  getList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getList()
}

function setLoading(val: boolean) {
  fullscreenLoading.value = val
}

onMounted(() => {
  getList()
})
</script>

<template>
  <MContent>
    <template #form>
      <div class="chainAct__top">
        <div class="chainAct__left">
          <div class="chainAct__top--tip" @click="addActivity">新增活动</div>
          <el-select v-model="selectValue" placeholder="全部状态" class="chainAct__top--select" @change="searchGetList">
            <el-option v-for="item in statusList" :key="item.id" :label="item.value" :value="item.id" />
          </el-select>
          <el-date-picker
            v-model="useDateValue"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            class="w-[250px] grow-[0]"
            value-format="YYYY-MM-DD"
            @change="timeSure"
          />
        </div>
        <el-input v-model="activityName" style="width: 170px" placeholder="请输入关键词">
          <template #append>
            <el-button :icon="Search" @click="searchGetList" />
          </template>
        </el-input>
      </div>
    </template>

    <template #table>
      <div v-loading="fullscreenLoading" class="chainAct">
        <ActivityList :list="list" @close="close" @del="del" @edit="edit" @set-loading="setLoading" />
      </div>
    </template>

    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>
  </MContent>
</template>

<style lang="scss" scoped>
.chainAct {
  width: 100%;
  background-color: #f9f9f9;
  &__top {
    width: 100%;
    display: flex;
    align-items: center;
    background-color: white;
    justify-content: space-between;

    .chainAct__left {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 500px;
    }

    &--tip {
      width: 100px;
      height: 32px;
      display: flex;
      align-items: center;
      border: 1px solid #2d8cf0;
      color: #2d8cf0;
      justify-content: center;
      border-radius: 5px;
      cursor: pointer;
    }
    &--select {
      width: 100px;
    }
  }
}

:deep(.el-input--small .el-input__inner) {
  height: 38px;
}

:deep(.el-range-editor--small.el-input__inner) {
  height: 38px;
}

:deep(.el-loading-spinner) {
  top: 6%;
}
</style>
