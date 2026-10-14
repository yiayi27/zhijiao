<script setup lang="ts">
import { addSup as apiAddSup, dealApplySup, delSup, getSupList, updateSup } from '@/api/good/goods'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import address from '../common/city.js'
import DropDown from './components/DropDown.vue'

defineOptions({
  name: 'SupplierManage',
})

// 定义类型接口
interface Supplier {
  id: string
  supplierSn?: string
  name: string
  mobile: string
  address: string
  productInfo: string
  province: string
  city: string
  country: string
  area: string
  status: number | string
  provinceId?: string
  cityId?: string
  countryId?: string
  createTime?: string
  score?: number | string
  version?: string
}

interface AddressItem {
  value: string | number
  label: string
}

interface CommandItem {
  type: string
  item: Supplier
}

// 路由相关
const route = useRoute()
const router = useRouter()

// 基础状态
const showType = ref(false)
const searchType = ref('1')
const list = ref<Supplier[]>([])
const hasList = ref(false)
const seletAll = ref(false)
const selelctList = ref<(string | number)[]>([])
const selectName = ref<string[]>([])
const isModelAdd = ref(false)
const editOrAdd = ref(false)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const isDeal = ref(false)
const selelctData = ref<Supplier[]>([])
const loading = ref(false)

// 搜索参数
const searchMsg = reactive({
  /** 姓名输入框 */
  name: '',
  /** 手机号输入框 */
  mobile: '',
  /** id输入框 */
  supplierSn: '',
})

// 地址相关
const addressProv = ref<AddressItem[]>(address.getProvince())
const addressCity = ref<AddressItem[]>([])
const addressArea = ref<AddressItem[]>([])

// 当前编辑项
const currItem = reactive<Supplier>({
  provinceId: '',
  cityId: '',
  countryId: '',
  name: '',
  mobile: '',
  address: '',
  productInfo: '',
  province: '',
  city: '',
  country: '',
  area: '',
  id: '0',
  status: 1,
})

// 监听选中数据变化
watch(selelctData, (val) => {
  selelctList.value = val.map((item: Supplier) => item.id)
  selectName.value = val.map((item: Supplier) => item.name)
})

/**
 * 批量删除
 */
function batchGoods() {
  ElMessageBox.confirm('确定要删除所有选中供应商吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delSup(selelctList.value.join(','), {}).then((res: any) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
      }
      getList()
      seletAll.value = false
    })
  })
}

/**
 * 新增供应商
 */
function addSup() {
  isModelAdd.value = true
  editOrAdd.value = false
  Object.assign(currItem, {
    provinceId: '',
    cityId: '',
    countryId: '',
    name: '',
    mobile: '',
    address: '',
    productInfo: '',
    province: '',
    city: '',
    country: '',
    area: '',
    id: '0',
    status: 1,
  })
}

/**
 * 选择省市区
 */
function selectChang(value: string | number, type: 'prov' | 'city' | 'area') {
  const valueStr = String(value)
  if (type === 'prov' && value) {
    addressCity.value = address.getCity(valueStr)
    addressArea.value = []
    currItem.cityId = ''
    currItem.countryId = ''
  }
  if (type === 'city' && value) {
    addressArea.value = address.getArea(currItem.provinceId || '', valueStr)
    currItem.countryId = ''
  }

  currItem.province = getAddressName(addressProv.value, currItem.provinceId || '')
  currItem.city = getAddressName(addressCity.value, currItem.cityId || '')
  currItem.country = getAddressName(addressArea.value, currItem.countryId || '')

  currItem.area = currItem.province + currItem.city + currItem.country
}

/**
 * 获取省市区所有数据
 */
function getAddressName(list: AddressItem[], value: string | number) {
  let name = ''
  if (list.length > 0) {
    for (const item of list) {
      if (String(item.value) === String(value)) {
        name = item.label
        break
      }
    }
  }
  return name
}

/**
 * 获取供应商数组
 */
async function getList() {
  const param = {
    current: pageNum.value,
    size: pageSize.value,
    ...searchMsg,
    status: searchType.value,
  }
  loading.value = true
  const res = await getSupList(param).finally(() => {
    loading.value = false
  })
  list.value = res.data.list.length > 0 ? res.data.list : []
  hasList.value = res.data.list.length === 0
  total.value = res.data.total
}

/** 搜索供应商 */
function search() {
  getList()
}

/**
 * 编辑供应商
 */
function editor(item: Supplier) {
  editOrAdd.value = true
  isModelAdd.value = true

  addressProv.value = address.getProvince()
  addressCity.value = address.getCity(item.province)
  addressArea.value = address.getArea(item.province, item.city)

  const province = getAddressName(addressProv.value, item.province)
  const city = getAddressName(addressCity.value, item.city)
  const country = getAddressName(addressArea.value, item.country)

  Object.assign(currItem, {
    provinceId: item.province,
    cityId: item.city,
    countryId: item.country,
    name: item.name,
    mobile: item.mobile,
    address: item.address,
    productInfo: item.productInfo,
    province,
    city,
    country,
    area: item.area,
    id: item.id,
    status: item.status,
  })
}

/**
 * 取消编辑
 */
function editPickCancel() {
  ElMessageBox.confirm(`确定要退出${editOrAdd.value ? '编辑' : '新增'}供应商页面?退出后，未保存的信息将不会保留!`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    isModelAdd.value = false
  })
}

/**
 * 处理供应商审核
 */
function dealAudit(command: CommandItem) {
  if (command.type === '1') {
    command.item.status = '1'
    isDeal.value = true
    editor(command.item)
  } else {
    command.item.status = '0'
    dealApplySup(command.item).then((res: any) => {
      if (res.code === 200) {
        ElMessage.success('关闭成功')
        getList()
        isModelAdd.value = false
      }
    })
  }
}

/**
 * 保存取消供应商信息
 */
function editPickOk() {
  currItem.city = currItem.cityId || ''
  currItem.country = currItem.countryId || ''
  currItem.province = currItem.provinceId || ''

  // 表单验证
  if (!currItem.name) {
    ElMessage.error('供应商姓名不能为空')
    return
  }

  if (!currItem.mobile) {
    ElMessage.error('供应商号码不能为空')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(currItem.mobile)) {
    ElMessage.error('手机号码有误，请重填')
    return
  }

  if (!currItem.area || !currItem.country || !currItem.city) {
    ElMessage.error('请输入供应商地区')
    return
  }

  if (!currItem.address) {
    ElMessage.error('请输入供应商地址')
    return
  }

  if (!currItem.productInfo) {
    ElMessage.error('请输入产品信息')
    return
  }

  if (currItem.id === '0') {
    // 新增供应商
    apiAddSup(currItem).then((res: any) => {
      if (res.code === 200) {
        ElMessage.success('添加成功')
        getList()
        isModelAdd.value = false
      }
    })
  } else if (isDeal.value) {
    // 编辑供应商
    dealApplySup(currItem).then(() => {
      ElMessage.success('保存成功')
      getList()
      isModelAdd.value = false
    })
  } else {
    updateSup(currItem).then(() => {
      ElMessage.success('编辑成功')
      getList()
      isModelAdd.value = false
    })
  }
}

/**
 * 删除供应商
 */
function delSupplier(status: number | string, item: Supplier) {
  ElMessageBox.confirm('删除该供应商，确认删除？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delSup(item.id, {}).then((res: any) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    })
  })
}

/** 启用/禁用供应商 */
function useSup(status: number, item: Supplier) {
  ElMessageBox.confirm(`确定要${status === 1 ? '禁用' : '启用'}选中供应商吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    item.status = status === 1 ? 3 : 1
    updateSup(item).then((res: any) => {
      if (res.code === 200) {
        ElMessage.success(`${status === 1 ? '禁用' : '启用'}成功`)
        getList()
      }
    })
  })
}

/**
 * 每页条数变化
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  getList()
}

/**
 * 当前页变化
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getList()
}

/**
 * 返回上一页
 */
function back() {
  router.go(-1)
}

// 组件挂载时执行
onMounted(() => {
  showType.value = false
  if (route.query.options === 'sup') {
    showType.value = true
  }
  getList()
})
</script>

<template>
  <MContainer>
    <template #tabs>
      <el-tabs v-model="searchType" class="serch--type" @tab-change="getList">
        <el-tab-pane label="供货商管理" name="1" />
        <el-tab-pane label="禁用中" name="3" />
      </el-tabs>
    </template>

    <template #form>
      <m-card class="form" :need-toggle="true">
        <el-form :model="searchMsg" label-width="90px">
          <el-row>
            <el-col :span="8">
              <el-form-item label="供应商名称">
                <el-input v-model="searchMsg.name" placeholder="请输入供应商名称" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机号">
                <el-input v-model="searchMsg.mobile" placeholder="请输入供应商手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="ID">
                <el-input v-model="searchMsg.supplierSn" placeholder="请输入供应商ID" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" @click="search"> 搜索 </el-button>
          </el-form-item>
        </el-form>
      </m-card>
    </template>

    <template #section>
      <el-button v-if="showType" @click="back"> 返回发布商品 </el-button>
      <el-button v-show="searchType === '1'" class="mr-[20px]" type="primary" @click="addSup"> 新 增 </el-button>
      <el-button :disabled="selelctList.length === 0" style="margin-left: 0px" @click="batchGoods"> 批量删除 </el-button>
    </template>

    <template #table>
      <m-table :key="searchType" v-model:checked-item="selelctData" v-loading="loading" :data="list" :selection="true">
        <template #header="{ row }">
          <div style="width: 100%">
            <span style="margin-right: 50px">ID：{{ row.supplierSn }}</span>
            <span style="margin-right: 50px">申请时间：{{ row.createTime }}</span>
          </div>
        </template>
        <m-table-column label="供应商名称">
          <template #default="{ row }">
            <div class="name-and-phone" style="height: 50px; display: flex; align-items: center">
              <span> {{ row.name }}</span>
            </div>
          </template>
        </m-table-column>
        <m-table-column label="手机号">
          <template #default="{ row }">
            <div class="name-and-phone">
              <span> {{ row.mobile }}</span>
            </div>
          </template>
        </m-table-column>
        <m-table-column label="地区">
          <template #default="{ row }">
            <div class="name-and-phone">
              <span> {{ row.area ? row.area : '无' }}</span>
            </div>
          </template>
        </m-table-column>
        <m-table-column label="产品信息">
          <template #default="{ row }">
            <div class="name-and-phone">
              <span> {{ row.productInfo }}</span>
            </div>
          </template>
        </m-table-column>
        <m-table-column v-if="['1', '3'].includes(searchType)" label="评分">
          <template #default="{ row }">
            <div class="name-and-phone">
              <span> {{ row.score }}</span>
            </div>
          </template>
        </m-table-column>
        <m-table-column label="供应商地址">
          <template #default="{ row }">
            <div class="name-and-phone">
              <span> {{ row.address ? row.address : '无' }}</span>
            </div>
          </template>
        </m-table-column>
        <m-table-column label="操作">
          <template #default="{ row }">
            <div>
              <span v-if="searchType === '1'" style="display: flex; align-items: center">
                <span style="cursor: pointer; color: #2d8cf0" @click="editor(row)">编辑</span>
                <DropDown :status="row.status" :sup="row" @del-supplier="delSupplier" @use-sup="useSup" />
              </span>
              <span v-if="searchType === '2'" style="display: flex; align-items: center">
                <el-dropdown trigger="click" placement="bottom-end" @command="dealAudit">
                  <span class="el-dropdown-link">
                    审核<el-icon class="ml-[5px]"><ArrowDown /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :command="{ item: row, type: '1' }">同意</el-dropdown-item>
                      <el-dropdown-item :command="{ item: row, type: '2' }">拒绝</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </span>
              <span v-if="searchType === '0'" style="display: flex; align-items: center">
                <span style="cursor: pointer; color: red" @click="delSupplier('3', row)">删除</span>
              </span>
              <span v-if="searchType === '3'" style="display: flex; align-items: center">
                <span style="cursor: pointer; color: #2d8cf0" @click="editor(row)">编辑</span>
                <DropDown :status="row.status" :sup="row" @del-supplier="delSupplier" @use-sup="useSup" />
              </span>
            </div>
          </template>
        </m-table-column>
      </m-table>
    </template>

    <template #pagination>
      <div class="listBottom">
        <PageManage
          :page-size="pageSize"
          :page-num="pageNum"
          :total="total"
          @handle-size-change="handleSizeChange"
          @handle-current-change="handleCurrentChange"
        />
      </div>
    </template>

    <el-dialog v-model="isModelAdd" width="35%" :before-close="editPickCancel">
      <template #header>
        <div class="dialog__title">{{ editOrAdd ? '编辑' : '新增' }}供应商</div>
      </template>
      <section class="dialog__title__line">
        <div class="dialog__title__line--item">
          <div class="dialog__title__line--item--lefts"><span style="color: #f76c84">*</span> 供应商名称：</div>
          <div class="dialog__title__line--item--rights">
            <el-input v-model="currItem.name" :maxlength="15" placeholder="输入供应商名称" />
          </div>
        </div>
        <div class="dialog__title__line--item">
          <div class="dialog__title__line--item--lefts"><span style="color: #f76c84">*</span> 手机号码：</div>
          <div class="dialog__title__line--item--rights">
            <el-input v-model="currItem.mobile" :maxlength="11" style="margin-left: 14px" placeholder="输入手机号码" />
          </div>
        </div>
        <div class="dialog__title__line--item">
          <div class="dialog__title__line--item--lefts"><span style="color: #f76c84">*</span> 地区选择：</div>
          <div class="dialog__title__line--item--rights">
            <el-select
              v-model="currItem.provinceId"
              placeholder="省份选择"
              style="margin-left: 14px; margin-right: 5px"
              @change="(value: string | number) => selectChang(value, 'prov')"
            >
              <el-option v-for="item in addressProv" :key="item.value" :value="item.value" :label="item.label">
                {{ item.label }}
              </el-option>
            </el-select>
            <el-select
              v-model="currItem.cityId"
              placeholder="城市选择"
              not-found-text="请先选择省份"
              style="margin-right: 5px"
              @change="(value: string | number) => selectChang(value, 'city')"
            >
              <el-option v-for="item in addressCity" :key="item.value" :value="item.value" :label="item.label">
                {{ item.label }}
              </el-option>
            </el-select>
            <el-select
              v-model="currItem.countryId"
              placeholder="区县选择"
              not-found-text="请先选择城市"
              @change="(value: string | number) => selectChang(value, 'area')"
            >
              <el-option v-for="item in addressArea" :key="item.value" :value="item.value" :label="item.label">
                {{ item.label }}
              </el-option>
            </el-select>
          </div>
        </div>
        <div class="dialog__title__line--item">
          <div class="dialog__title__line--item--lefts"><span style="color: #f76c84">*</span> 供应商地址：</div>
          <div class="dialog__title__line--item--rights">
            <el-input v-model="currItem.address" placeholder="输入供应商地址" />
          </div>
        </div>
        <div class="dialog__title__line--item">
          <div class="dialog__title__line--item--lefts"><span style="color: #f76c84">*</span> 产品信息：</div>
          <div class="dialog__title__line--item--rights">
            <el-input v-model="currItem.productInfo" type="textarea" placeholder="输入产品标题信息" :rows="5" style="margin-left: 14px" />
          </div>
        </div>
      </section>
      <template #footer>
        <span class="dialog--footer">
          <el-button @click="editPickCancel">取 消</el-button>
          <el-button type="primary" @click="editPickOk">保 存</el-button>
        </span>
      </template>
    </el-dialog>
  </MContainer>
</template>

<style lang="scss" scoped>
@import '../../../assets/styles/goods/index.scss';

.search__top {
  width: 100%;
  height: 150px;
  background-color: #f8f8f8;
  padding-top: 30px;

  &__line {
    display: flex;
    justify-content: space-around;
  }

  &__btn {
    margin-top: 10px;
    margin-left: 40px;
  }
}

.user--list {
  border-collapse: collapse;

  thead tr td {
    border-bottom: 1px solid #eee;
    border-top: 1px solid #eee;
    padding: 10px 5px;
    background-color: #f6f8fa;
    text-align: center;

    &:first-child {
      width: 1%;
    }

    &:nth-child(2) {
      width: 50px;
      text-align: left !important;
    }

    &:last-child {
      width: 90px;
    }
  }

  tbody tr:nth-child(2) td {
    background-color: #e9f3fb;
    height: 40px;
    padding: 5px 5px;
    border-right: 1px solid #e9f3fb;
    border-left: 1px solid #e9f3fb;
    font-size: 12px !important;

    .checkItem {
      margin-left: 15px;
    }
  }

  tbody tr:nth-child(3) td {
    border-bottom: 1px solid #e9f3fb;
    padding: 10px 5px;
    text-align: center;
    height: 75px;
    border-right: 1px solid #e9f3fb;

    &:first-child {
      border-left: 1px solid #e9f3fb;
    }
  }
}

.list__cell {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.dialog__title {
  display: flex;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
}

.dialog__title__line {
  padding: 0 30px;

  &--item {
    display: flex;
    margin-bottom: 15px;
    align-items: flex-start;

    &--lefts {
      text-align: right;
      line-height: 30px;
    }

    &--rights {
      display: flex;
      flex: 1;
      justify-content: space-between;
      margin-left: 10px;
    }
  }
}

.dialog--footer {
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-tabs__nav-wrap::after) {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 2px;
  background-color: #f5f5f5;
  z-index: 1;
}

:deep(.m__table--empty) {
  width: 790px;
  margin-left: 75px;
}

:deep(.m__table--empty .empty__td) {
  width: 920px;
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
}
</style>
