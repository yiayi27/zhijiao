<script lang="ts" setup>
import { toPostGetAdminPage, toPostShopAdminDelete } from '@/api/perm'
import BaseTable from '@/components/BaseTable/index.vue'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import AdminEdit from '@/views/perm/components/AdminEdit.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const adminPage = reactive<any>({
  queryUsers: [],
  selectedId: null,
  showDialog: false,
  records: [],
  total: 0,
  page: { size: 10, current: 1 },
  currentData: null,
})
const loading = ref(false)

onMounted(() => {
  reload()
})

async function reload() {
  loading.value = true
  const res = await toPostGetAdminPage({ ...adminPage.page }).finally(() => {
    loading.value = false
  })
  const data = res.data
  adminPage.records = data.list
  adminPage.total = data.total
}

function onEditAdminShow(row: any = null) {
  adminPage.currentData = row
  adminPage.showDialog = true
}

async function onDeleteData(row: any) {
  ElMessageBox.confirm('确定删除这个账号吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await toPostShopAdminDelete({
      mobile: row.mobile,
    })
    ElMessage.success('已删除')
    await reload()
  })
}

function handleCurrentChange(current: number) {
  adminPage.page.current = current
  reload()
}

function handleSizeChange(size: number) {
  adminPage.page.size = size
  reload()
}
</script>

<template>
  <MContainer v-loading="loading" class="perm__content">
    <template #section>
      <div class="handle_container">
        <el-button round type="primary" @click="onEditAdminShow()"> 新增管理员 </el-button>
      </div>
    </template>

    <template #table>
      <div class="table_container">
        <BaseTable
          :cell-style="{ fontSize: '14px', color: '#333333' }"
          :data="adminPage.records"
          :header-cell-style="{ background: '#F7F8FA' }"
          :header-row-style="{ fontSize: '14px', color: '#333', height: '48px' }"
        >
          <el-table-column align="left" label="昵称" prop="nickname" />
          <el-table-column align="left" label="电话" prop="mobile" />
          <el-table-column align="right" fixed="right" label="操作">
            <template #default="scope">
              <el-space>
                <el-button link type="primary" :disabled="!scope.row.editable" @click="onEditAdminShow(scope.row)"> 编辑 </el-button>
                <el-button link type="danger" :disabled="!scope.row.editable" @click="onDeleteData(scope.row)"> 删除 </el-button>
              </el-space>
            </template>
          </el-table-column>
        </BaseTable>
      </div>
    </template>

    <template #pagination>
      <PageManage
        :page-num="adminPage.page.current"
        :page-size="adminPage.page.size"
        :total="adminPage.total"
        @handle-current-change="handleCurrentChange"
        @handle-size-change="handleSizeChange"
      />
    </template>

    <AdminEdit v-model:visible="adminPage.showDialog" :data="adminPage.currentData" @success="reload" @close="adminPage.currentData = null" />
  </MContainer>
</template>

<style lang="scss" scoped>
.select-user-tips {
  font-size: 12px !important;
}

.select-user-tips .new-user {
  color: #b67c03;
}
</style>
