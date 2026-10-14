<script setup lang="ts">
import { addLogistModel, delLogistModel, getLogistModel } from '@/api/logistics/logistics'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import address from '@/views/goods/common/city.js'
import { ElMessage, ElMessageBox } from 'element-plus'

import { useRoute, useRouter } from 'vue-router'
import AddModel from './compont/AddModel.vue'

// Define interface for AddressItem from address module
interface AddressItem {
  value: string | number
  label: string
  [key: string]: any
}

interface RegionItem {
  fatherid: string | number
  list: Array<{ value: string | number }>
}

interface ShippingModelItem {
  region: string
  area?: string
  [key: string]: any
}

interface LogisticsModelItem {
  id: string | number
  name: string
  logisticsShippingModelVos: ShippingModelItem[]
  logisticsIncludePostageVos: ShippingModelItem[]
  choiceConditionPostage?: number
  valuationModel?: string | number
  [key: string]: any
}

interface ModelCheckItem {
  name: string
  choiceConditionPostage: number
  valuationModel: string
  logisticsShippingModelDtos: {
    area?: string
    valuationModel?: string
    [key: string]: any
  }[]
  logisticsIncludePostageDtos: {
    area?: string
    value?: string
    type?: number
    weight?: number
    pieceNum?: number
    [key: string]: any
  }[]
  id?: string | number
}

const router = useRouter()
const route = useRoute()

// refs
const addModel = ref<any>(null)

// 响应式数据
const logDialog = ref(false)
const editCurrent = ref<Partial<LogisticsModelItem>>({})
const titleType = ref(true)
const logisModelList = ref<LogisticsModelItem[]>([])
const hasList = ref(false)
const pageType = ref('')
const query = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 生命周期钩子
onMounted(() => {
  pageType.value = route.params.options as string
  getList()
})

/**
 * 返回上一页
 */
function back() {
  router.go(-1)
}

/**
 * 获取运费模板
 */
function getList() {
  getLogistModel(query).then((res) => {
    query.total = res.data.total
    res.data.list.forEach((item: LogisticsModelItem) => {
      item.logisticsShippingModelVos.forEach((seItem) => {
        seItem.area = dealArea(JSON.parse(seItem.region))
      })
      item.logisticsIncludePostageVos.forEach((poItem) => {
        poItem.area = dealArea(JSON.parse(poItem.region))
      })
    })
    logisModelList.value = res.data.list
    hasList.value = res.data.list.length === 0
  })
}

/**
 * 根据返回的地域信息显示对应城市
 */
function dealArea(region: RegionItem[]) {
  const addressProv = address.getProvince() as AddressItem[]
  let addressCity: AddressItem[] = []
  const area: string[] = []
  region.forEach((item) => {
    addressProv.forEach((proItem: AddressItem) => {
      if (item.fatherid === proItem.value) {
        addressCity = address.getCity(proItem.value) as AddressItem[]
        if (item.list.length > 0 && item.list.length !== addressCity.length) {
          area.push(`${proItem.label}(${item.list.length}/${addressCity.length})`)
        } else {
          area.push(`${proItem.label}`)
        }
      }
    })
  })
  return area.join(',')
}

/**
 * 编辑模板
 */
function editModel(item: LogisticsModelItem) {
  titleType.value = false
  logDialog.value = true
  editCurrent.value = item
}

/**
 * 删除选中模板
 */
function delGetItem(item: LogisticsModelItem) {
  ElMessageBox.confirm(`确定删除选中模板吗?删除后模板信息不可恢复?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delLogistModel(item.id.toString(), {}).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

/**
 * 新增物流模板
 */
function addNewModel() {
  logDialog.value = true
  titleType.value = true
  editCurrent.value = {}
}

function handleClose() {
  ElMessageBox.confirm('确定退出物流模板编辑？退出后不会保留选择操作！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    logDialog.value = false
    if (addModel.value && typeof addModel.value.clearValidate === 'function') {
      addModel.value.clearValidate()
    }
  })
}

/**
 * 运费模板输入验证
 */
function checkModel(checkItem: ModelCheckItem) {
  let passType = true
  let errMsg = ''
  if (!checkItem.name) {
    errMsg = '请输入模板名称'
    passType = false
    ElMessage.error(errMsg)
    return passType
  }
  checkItem.logisticsShippingModelDtos.forEach((item) => {
    if (!item.area && passType) {
      errMsg = '请选择配送区域'
      passType = false
      ElMessage.error(errMsg)
      return passType
    }
  })

  return passType
}

/**
 * 包邮模板输入验证
 */
function checkFreeModel(checkItem: ModelCheckItem) {
  let passType = true
  checkItem.logisticsIncludePostageDtos.forEach((item) => {
    if (!item.area && passType) {
      passType = false
      ElMessage.error('请选择包邮区域')
      return passType
    }
  })

  return passType
}

/**
 * 获取类型
 */
function getType(arr: [string, string]) {
  const types: Array<[[string, string], number]> = [
    [['1', '1'], 0],
    [['2', '1'], 2],
    [['3', '1'], 3],
    [['1', '2'], 1],
    [['2', '2'], 2],
    [['3', '2'], 4],
  ]
  const logType = new Map()
  types.forEach(([key, value]) => {
    logType.set(JSON.stringify(key), value)
  })
  return logType.get(JSON.stringify(arr))
}

/**
 * 获取物流模型
 */
async function getLogisticsModel() {
  try {
    if (addModel.value && typeof addModel.value.getFormModel === 'function') {
      await addModel.value.getFormModel()
    }
  } catch (error) {
    ElMessage.warning('请填写正确运费信息')
    return Promise.reject(error)
  }
}

/**
 * 获取免费物流模型
 */
async function getFreeLogisticsModel() {
  try {
    if (addModel.value && typeof addModel.value.getFreeFormModel === 'function') {
      await addModel.value.getFreeFormModel()
    }
  } catch (error) {
    ElMessage.warning('请填写正确包邮地区信息')
    return Promise.reject(error)
  }
}

/**
 * 点击确定新增物流模板
 */
async function addLogModel() {
  if (!addModel.value || !addModel.value.modelValidateForm) {
    return
  }

  const logisticsTemplateDto: ModelCheckItem = {
    ...addModel.value.modelValidateForm,
  }

  logisticsTemplateDto.logisticsIncludePostageDtos.forEach((item) => {
    item.type = getType([item.value as string, logisticsTemplateDto.valuationModel])
  })
  logisticsTemplateDto.logisticsShippingModelDtos.forEach((item) => {
    item.valuationModel = logisticsTemplateDto.valuationModel
  })
  logisticsTemplateDto.choiceConditionPostage = Number(logisticsTemplateDto.choiceConditionPostage)
  const checkFlag = await checkModel(logisticsTemplateDto)
  let freeCheck = true
  if (checkFlag) {
    await getLogisticsModel()
    if (Number(logisticsTemplateDto.choiceConditionPostage) === 1) {
      freeCheck = await checkFreeModel(logisticsTemplateDto)
      if (freeCheck) {
        await getFreeLogisticsModel()
      }
    }
    if (checkFlag && freeCheck) {
      logisticsTemplateDto.logisticsIncludePostageDtos.forEach((item) => {
        if (item.value === '1' && logisticsTemplateDto.valuationModel === '2') {
          item.weight = item.pieceNum
        }
      })
      await addLogistModel(logisticsTemplateDto).then(() => {
        logDialog.value = false
        ElMessage.success(`${logisticsTemplateDto.id ? '编辑成功' : '新增成功'}`)
        getList()
      })
    }
  }
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  query.size = val
  getList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  query.current = val
  getList()
}
</script>

<template>
  <MContent>
    <template #section>
      <el-button v-if="pageType" @click="back"> 返回发布商品 </el-button>
      <el-button type="primary" @click="addNewModel"> 新增运费模板 </el-button>
    </template>

    <template #table>
      <div v-for="(item, index) in logisModelList" :key="index" class="list">
        <div v-if="logisModelList.length > 0">
          <div class="list__top">
            <div class="list__top--name">
              {{ item.name }}
              {{ item.choiceConditionPostage === 1 ? '(已指定包邮区域)' : '' }}
            </div>
            <div class="list__top--deal">
              <span style="color: #409eff" @click="editModel(item)">编辑</span>
              <span style="color: rgb(250, 100, 101)" @click="delGetItem(item)">删除</span>
            </div>
          </div>
          <div class="list__content">
            <div class="list__content--name">可配送区域</div>
            <div class="list__content--status">
              <span>
                {{ item.valuationModel === 1 ? '首件数(件)' : '首重量(kg)' }}
              </span>
              <span>首费(元)</span>
              <span>
                {{ item.valuationModel === 1 ? '续件数(件)' : '续重量(kg)' }}
              </span>
              <span>{{ item.firstAmount }}</span>
            </div>
          </div>
          <div v-for="(setItem, setIndex) in item.logisticsShippingModelVos" :key="setIndex" class="list__content">
            <div class="list__content--name">
              {{ setItem.area }}
            </div>
            <div class="list__content--status">
              <span>
                {{ setItem.valuationModel === 1 ? setItem.firstPiece : setItem.firstWeight }}
              </span>
              <span>{{ setItem.firstAmount }}</span>
              <span>
                {{ setItem.valuationModel === 1 ? setItem.secondPiece : setItem.secondWeight }}
              </span>
              <span>{{ setItem.secondAmount }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="hasList" class="emptyLine">暂无数据~</div>
    </template>

    <template #pagination>
      <PageManage
        :page-size="query.size"
        :page-num="query.current"
        :total="query.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>

    <el-dialog v-model="logDialog" width="850px" :before-close="handleClose">
      <template #header>
        <div class="digTitle">{{ titleType ? '新增' : '编辑' }}物流配送模板</div>
      </template>
      <AddModel ref="addModel" :edit-current="editCurrent" :log-dialog="logDialog" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="addLogModel">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </MContent>
</template>

<style lang="scss" scoped>
.list {
  background-color: white;
  padding: 0px;
  color: #595961;
  &__top {
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    background-color: #f6f8fa;
    &--name {
      font-size: 13px;
      font-weight: bold;
    }
    &--deal {
      font-size: 13px;
      width: 70px;
      display: flex;
      justify-content: space-between;
      cursor: pointer;
    }
  }

  &__content {
    border: 1px solid #ededed;
    border-top: none;
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    &--name {
      width: 550px;
    }
    &--status {
      width: 350px;
      display: flex;
      justify-content: space-around;
    }
  }
}

.digTitle {
  font-size: 17px;
  font-weight: bold;
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
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}
</style>
