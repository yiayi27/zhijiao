<script setup lang="ts">
import { getMemberRecord } from '@/api/customer/member'
import BaseTable from '@/components/BaseTable/index.vue'
import PageManage from '@/components/PageManage.vue'
import { ArrowDown, Search } from '@element-plus/icons-vue'

const dialogVisible = ref(false)

const userId = ref('')

/** 筛选字段 */
const dataRange = ref<string[]>([])

const selectValue = ref('memberNumber')

const inputValue = ref('')

// 列表数据
const lists = ref([])

// 分页数据
const pageMap = reactive({
  total: 0,
  current: 1,
  size: 10,
})

// 开卡方式MAP
const radioMap = {
  1: '付费购买',
  2: '积分兑换',
  3: '累计消费金额',
  4: '购买指定商品',
}

function init(memberInfo: Obj) {
  userId.value = memberInfo.userId
  fetchList()
  dialogVisible.value = true
}

function handelSearch() {
  fetchList({
    page: 1,
    size: 10,
    [selectValue.value]: inputValue.value ? inputValue.value : undefined,
  })
}

function timeChange(value: string[]) {
  dataRange.value = value || []
  fetchList({
    page: 1,
    size: 10,
    becomeMemberStartTime: dataRange.value?.[0],
    becomeMemberEndTime: dataRange.value?.[1],
  })
}

function getDataList() {
  fetchList({
    page: 1,
    size: 10,
    [selectValue.value]: inputValue.value ? inputValue.value : undefined,
    becomeMemberStartTime: dataRange.value?.[0],
    becomeMemberEndTime: dataRange.value?.[1],
  })
}

async function fetchList(params?: Obj) {
  if (!params) {
    params = {
      page: 1,
      size: 10,
    }
  }
  const res = await getMemberRecord({
    ...params,
    shopUserId: userId.value,
  })
  lists.value = res.data.list
  pageMap.total = res.data.total
  pageMap.current = res.data.current
  pageMap.size = 10
}

/* 列表翻页操作 */
function handleCurrentChange(val: number) {
  fetchList({ page: val, size: 10 })
}

const handleCommand = (command: string) => {
  selectValue.value = command
}

const selectList = [
  {
    label: '会员卡号',
    value: 'memberNumber',
  },
  {
    label: '会员卡名称',
    value: 'title',
  },
  {
    label: '会员等级名称',
    value: 'rankName',
  },
]
const selectLabel = computed(() => {
  return selectList.find((item) => item.value === selectValue.value)?.label
})

defineExpose({
  init,
})
</script>

<template>
  <el-dialog v-model="dialogVisible" width="1040px" class="dialog" title="会员记录">
    <div>
      <div class="filter-block">
        <div class="filter-field">
          <el-input v-model="inputValue" placeholder="请输入内容" class="input-with-select">
            <template #prepend>
              <el-dropdown placement="bottom" trigger="click" @command="handleCommand">
                <span>
                  {{ selectLabel }}
                  <el-icon>
                    <ArrowDown />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="memberNumber">会员卡号</el-dropdown-item>
                    <el-dropdown-item command="title">会员卡名称</el-dropdown-item>
                    <el-dropdown-item command="rankName">会员等级名称</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>

            <template #append>
              <el-button :icon="Search" @click="handelSearch" />
            </template>
          </el-input>
        </div>
        <div class="filter-field">
          <el-date-picker
            v-model="dataRange"
            type="datetimerange"
            range-separator="-"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="timeChange"
          />
          <el-button class="search" @click="getDataList"> 查询 </el-button>
        </div>
      </div>
      <div class="live-table">
        <BaseTable :data="lists" style="width: 100%">
          <el-table-column prop="title" label="会员卡名称" min-width="140" />
          <el-table-column prop="rankName" label="会员等级" min-width="120" />
          <el-table-column prop="openType" label="开卡方式" min-width="120">
            <template #default="scope">
              {{ radioMap[scope.row.openType as keyof typeof radioMap] }}
            </template>
          </el-table-column>
          <el-table-column prop="openValue" label="开卡条件">
            <template #default="scope"> {{ scope.row.openValue }}{{ scope.row.openType === 1 ? '元' : '积分' }} </template>
          </el-table-column>
          <el-table-column prop="memberNumber" label="会员卡号" min-width="200" />
          <el-table-column prop="memberRegisterTime" label="购买时间" min-width="180" />
          <el-table-column prop="memberExpireTime" label="有效期" min-width="180" />
        </BaseTable>
        <br /><br />

        <PageManage
          v-model:page-num="pageMap.current"
          :page-size="10"
          layout="total, prev, pager, next"
          :total="pageMap.total"
          @handle-current-change="handleCurrentChange"
        />
      </div>
    </div>
    <template #footer>
      <div class="dialog--footer">
        <el-button @click="dialogVisible = false"> 取 消 </el-button>
        <!-- <el-button type="primary" @click="onOk">确 定</el-button> -->
      </div>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.filter-block {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  :deep(.el-input-group__prepend input) {
    width: 150px;
  }
  .filter-field:last-child {
    width: 430px;
    display: flex;
    :deep(.input-with-select) {
      width: 320px;
      margin: 0 6px;
    }

    .search {
      margin-left: 5px;
    }
  }
}
</style>
