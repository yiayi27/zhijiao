<script setup lang="ts">
import { deleteMemberCardPrivilege, getMemberCardPrivilege, updateMemberCardPrivilegeStatus } from '@/api/customer/member'
import BaseTable from '@/components/BaseTable/index.vue'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessageBox } from 'element-plus'

import SetRule from '../dialog/SetRule.vue'

const setRuleRef = ref()

// 定义数据接口
interface PrivilegeItem {
  id: string
  open: number
  ptype: number
  privilegeName: string
  illustrate: string
  allowDel: number
  [key: string]: any
}

// 表格数据
const listData = ref<PrivilegeItem[]>([])

// 分页条数
const pageSize = ref(30)

// 页码
const pageNum = ref(1)

// 总数
const total = ref(0)

// 权益类型映射
const ptypeMap: Record<number, string> = {
  0: '自定义权益',
  1: '商品折扣',
  2: '物流优惠',
  3: '积分加倍',
  4: '专属客服',
  5: '优先发货',
  6: '急速售后',
}

// 获取表格数据
function getDataList() {
  getMemberCardPrivilege({} as any).then((res) => {
    listData.value = res.data
    total.value = res.data.length || 0
  })
}

// 更新权益状态
function onUpdateStatus(rowData: PrivilegeItem, type: number) {
  const openIndex = listData.value.filter((v) => v.open === 1)
  if (openIndex.length === 1 && type === 0) {
    ElMessage.error('至少有一项开启')
    return null
  }
  if (openIndex.length === 12 && type === 1) {
    ElMessage.error('最多开启12条权益')
    return null
  }
  ElMessageBox.confirm(
    type === 0 ? '关闭权益后小程序端将不会显示，不影响已有会员使用，确定关闭吗？' : '开启权益将对已有会员生效，等级绑定时将显示该权益，确定启用吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    },
  ).then(() => {
    updateMemberCardPrivilegeStatus({ id: rowData.id, open: type }).then(() => getDataList())
  })
}

// 表格分页变化
function handleCurrentChange() {
  getDataList()
}

// 设置规则
function onSetRule(row?: PrivilegeItem) {
  if (!row && listData.value && listData.value.length === 30) {
    ElMessage.error('最多增加30条权益!')
    return null
  }
  setRuleRef.value.init(row || null)
}

// 删除自定义权益
function onDelRow(row: PrivilegeItem) {
  ElMessageBox.confirm('删除权益后将删除无法恢复，不影响已有会员使用，确定删除吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteMemberCardPrivilege({ id: row.id, isDeleted: 1 }).then(() => getDataList())
  })
}

// Lifecycle hooks
onMounted(() => {
  getDataList()
})
</script>

<template>
  <MContainer class="rmember">
    <template #table>
      <BaseTable :data="listData" style="width: 100%">
        <el-table-column label="权益类型" width="120">
          <template #default="scope">
            <span>{{ ptypeMap[scope.row.ptype as keyof typeof ptypeMap] }}</span>
          </template>
        </el-table-column>
        <el-table-column label="权益名称" prop="privilegeName" width="120" />
        <el-table-column prop="illustrate" label="权益说明" />
        <el-table-column width="180" label="操作">
          <template #default="scope">
            <el-button v-if="scope.row.open === 1" link @click="onUpdateStatus(scope.row, 0)"> 关闭 </el-button>
            <el-button v-if="scope.row.open === 0" link @click="onUpdateStatus(scope.row, 1)"> 开启 </el-button>
            <el-button link @click="onSetRule(scope.row)"> 设置规则 </el-button>
            <el-button v-if="scope.row.allowDel === 1" link @click="onDelRow(scope.row)"> 删除 </el-button>
          </template>
        </el-table-column>
      </BaseTable>
    </template>

    <template #pagination>
      <PageManage
        v-model:page-num="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @handle-current-change="handleCurrentChange"
      >
        <template #pagination--left>
          <el-button class="setupcon__item3--addnew" @click="onSetRule()"> 添加自定义权益 </el-button>
        </template>
      </PageManage>
    </template>
    <SetRule ref="setRuleRef" @re-fresh="getDataList" />
  </MContainer>
</template>

<style lang="scss" scoped>
.rmember {
  .m__table--body .body--content td:first-child {
    border-left: 1px solid #ededed;
  }
  :deep(.m__table--head th) {
    text-align: left;
  }
  :deep(.el-pagination) {
    text-align: right;
  }
  :deep(.m__table--item) {
    border-left: none;
  }
}
</style>
