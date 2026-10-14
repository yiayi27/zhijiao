<script setup lang="ts">
import { getImgCode, getLiveList, reFreshList as refreshLiveList, updateLanuch } from '@/api/live/index'

import BaseTable from '@/components/BaseTable/index.vue'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import { Search } from '@element-plus/icons-vue'

defineOptions({
  name: 'LiveIndex',
})

// 定义数据类型
interface LiveItem {
  id: string
  liveName: string
  startTime: string
  endTime: string
  anchor: string
  viewCount: number
  likeCount: number
  firstFlag: number
  [key: string]: any
}

interface FilterParams {
  current: number
  size: number
  liveName?: string
  anchor?: string
  startTime?: string
  endTime?: string
  [key: string]: any
}

/** 筛选字段 */
const dataRange = ref<[string, string] | []>([])
const selectValue = ref('liveName')
const inputValue = ref('')

/* 表格字段 */
const liveTable = ref<LiveItem[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

/* 弹窗字段 */
const qrCodeVisible = ref(false)

/* 过滤参数 */
const filter = reactive<FilterParams>({
  current: 1,
  size: 10,
})

/* 分享二维码 */
const codeImg = ref('')

onMounted(() => {
  fetchLiveList()
  fetchImgCode()
})

/* 文本框输入搜索 */
function handelSearch(): void {
  fetchLiveList({
    current: 1,
    liveName: undefined,
    anchor: undefined,
    [selectValue.value]: inputValue.value,
  })
}

function fetchImgCode(): void {
  const wxaGetwxacode = {
    path: `subcontract/pages/live/live`,
    width: 0,
  }
  getImgCode(wxaGetwxacode).then((res) => {
    codeImg.value = res.data
  })
}

/* 获取直播列表 */
function fetchLiveList(data: Partial<FilterParams> = {}): void {
  Object.assign(filter, data)
  getLiveList(filter).then((res) => {
    if (res.code === 200) {
      liveTable.value = (res.data.list || []).map((v: LiveItem) => {
        v.likeCount = +v.likeCount
        v.viewCount = +v.viewCount
        return v
      })
      currentPage.value = res.data.current
      total.value = res.data.total
    }
  })
}

/**
 * 设为首页/取消首页
 */
function changeStatus(item: LiveItem): void {
  updateLanuch({
    id: item.id,
    firstFlag: item.firstFlag === 1 ? 0 : 1,
  }).then(() => {
    ElMessage.success('操作成功')
    fetchLiveList()
  })
}

/* 刷新后台数据库列表 */
function reFreshList(): void {
  refreshLiveList({}).then(() => {
    fetchLiveList({
      current: 1,
      liveName: undefined,
      anchor: undefined,
      [selectValue.value]: inputValue.value,
    })
  })
}

function handleSizeChange(val: number): void {
  pageSize.value = val
  fetchLiveList({
    size: val,
  })
}

function dateChange(date: [string, string] | null): void {
  fetchLiveList({
    current: 1,
    startTime: date?.[0],
    endTime: date?.[1],
  })
}

/* 列表翻页操作 */
function handleCurrentChange(val: number): void {
  fetchLiveList({
    current: val,
  })
}
</script>

<template>
  <MContainer class="live-container">
    <template #form>
      <div class="filter-block">
        <div class="filter-field">
          <el-date-picker
            v-model="dataRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="dateChange"
          />
        </div>
        <div class="filter-field">
          <el-popover
            placement="top-start"
            width="200"
            trigger="hover"
            content="将从微信后台重新获取直播列表，限额一天500次，若只查询使用，请点击查询即可"
          >
            <template #reference>
              <el-button @click="reFreshList"> 刷新列表数据 </el-button>
            </template>
          </el-popover>
          <el-input v-model="inputValue" placeholder="请输入内容" class="input-with-select">
            <template #prepend>
              <el-select v-model="selectValue" placeholder="请选择" class="w-[100px]">
                <el-option label="直播名称" value="liveName" />
                <el-option label="主播名称" value="anchor" />
              </el-select>
            </template>
            <template #append>
              <el-button :icon="Search" @click="handelSearch" />
            </template>
          </el-input>
        </div>
      </div>
    </template>

    <template #table>
      <BaseTable :data="liveTable" style="width: 100%">
        <el-table-column prop="liveName" label="直播名称" min-width="150" />
        <el-table-column prop="startTime" label="开播时间" sortable width="200">
          <template #default="scope"> {{ scope.row.startTime }}-{{ scope.row.endTime }} </template>
        </el-table-column>
        <template #empty>
          <div class="emptyLine">暂无数据~</div>
        </template>
        <el-table-column prop="anchor" label="主播信息" min-width="120" />
        <el-table-column prop="viewCount" min-width="100" sortable label="观看量" />
        <el-table-column prop="likeCount" min-width="100" sortable label="获赞量" />
        <el-table-column width="130" label="操作">
          <template #default="scope">
            <a @click="qrCodeVisible = true">分享</a>
            <a style="margin-left: 8px" @click="changeStatus(scope.row)">
              {{ scope.row.firstFlag === 1 ? '取消首页' : '设为首页' }}
            </a>
          </template>
        </el-table-column>
      </BaseTable>
    </template>

    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="currentPage"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>

    <el-dialog v-model="qrCodeVisible" width="30%">
      <div style="text-align: center">
        <img :src="codeImg" style="width: 200px; height: 200px" /><br /><br />
        <a :href="codeImg" download="wxcode">保存小程序码</a>
      </div>
    </el-dialog>
  </MContainer>
</template>

<style lang="scss" scoped>
.live-container {
  a {
    color: #409eff !important;
    cursor: pointer !important;
  }

  :deep(.el-table--small th) {
    padding: 4px 0;
  }
}

.nright {
  width: 100%;
  height: 100%;
  text-align: center;
  padding-top: 200px;

  &__tip {
    font-size: 16px;
    margin-bottom: 12px;
  }
}

.refresh {
  color: rgba(0, 0, 0, 0.25);
  margin-top: 24px;
}

.wblock {
  text-align: left;
  font-size: 16px;
  color: rgba(0, 0, 0, 0.25);
  margin-top: 150px;

  &__left {
    float: left;
    margin-left: 50px;
  }

  &__right {
    float: left;
    margin-left: 50px;

    li {
      margin-bottom: 12px;
    }
  }
}

.filter-block {
  display: flex;
  justify-content: space-between;

  :deep(.el-input-group__prepend input) {
    width: 100px;
  }

  .filter-field:last-child {
    :deep(.input-with-select) {
      width: 310px;
      margin-left: 6px;
    }
  }
}

.live-table {
  margin-top: 16px;

  :deep(th) {
    background: #f5f7fa;
    color: rgba(0, 0, 0, 0.65);
  }

  :deep(.el-pagination) {
    text-align: right;
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
  margin-left: -15px;
  font-size: 14px;
  color: #b3b3b3;
}
</style>
