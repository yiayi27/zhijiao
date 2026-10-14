<script setup lang="ts">
import address from '@/views/goods/common/city.js'
import { ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 类型定义
interface AddressItem {
  value: string | number
  label: string
  [key: string]: any
}

interface CityItem extends AddressItem {
  isCheck: boolean
  disabled: boolean
}

interface ProvinceItem extends AddressItem {
  isCheck: boolean
  disabled: boolean
  cityList: Array<{ value: string | number }>
  isIndeterminate: boolean
  ownCity: CityItem[]
  hasNum: number
}

interface RegionItem {
  fatherid: string | number
  list: Array<{ value: string | number }>
}

interface LogisticsModelDto {
  region?: string | RegionItem[]
  logisticsShippingModelDtos?: any[]
  logisticsIncludePostageDtos?: any[]
  [key: string]: any
}

// Props定义
const props = defineProps({
  modelIndex: {
    type: Number,
    default: 0,
  },
  getRegion: {
    type: Array as () => RegionItem[],
    default: () => [],
  },
  modelType: {
    type: Number,
    default: 0,
  },
  visible: {
    type: Boolean,
    default: false,
  },
  freeArea: {
    type: Boolean,
    default: false,
  },
  modelValidateForm: {
    type: Object as () => LogisticsModelDto,
    default: () => ({}),
  },
})

// Emits定义
const emit = defineEmits(['update:visible', 'getConList'])

// 响应式数据
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value),
})

const addressProv = ref<ProvinceItem[]>([])
const hasCheckPro = ref({
  pro: [] as Array<string | number>,
  city: [] as Array<string | number>,
})
const isIndeterminate = ref(false)
const checkAll = ref(false)
const lengthList = ref<number[]>([])

const instance = getCurrentInstance()

// 监听对话框显示状态
watch(
  () => dialogVisible.value,
  (newVal) => {
    if (newVal) {
      initArea()
      dealArea()
      if (instance && instance.vnode.el) {
        document.body.appendChild(instance.vnode.el as Node)
      }
    }
  },
)

// 生命周期钩子
onMounted(() => {
  initArea()
  dealArea()
})

// 初始化省市信息
function initArea() {
  const provinces = address.getProvince() as AddressItem[]

  // Convert Address items to ProvinceItems
  addressProv.value = provinces
    .map((item) => {
      // Skip specific provinces
      if (item.value === '710000' || item.value === '810000' || item.value === '820000') {
        return null
      }

      const addressCityData = address.getCity(item.value) as AddressItem[]
      // Convert to CityItems
      const ownCity = addressCityData.map((city) => ({
        ...city,
        disabled: false,
        isCheck: false,
      }))

      return {
        ...item,
        disabled: false,
        isCheck: false,
        cityList: [],
        isIndeterminate: false,
        ownCity,
        hasNum: ownCity.length,
      } as ProvinceItem
    })
    .filter(Boolean) as ProvinceItem[]

  checkAll.value = false
  getCheckAll()
}

// 关闭对话框
function handleClose() {
  ElMessageBox.confirm('退出后不会保留选择操作！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    dialogVisible.value = false
    initArea()
  })
}

// 全选
function handleCheckAllChange(e: any) {
  addressProv.value.forEach((item: ProvinceItem) => {
    item.isCheck = e
    item.cityList = []
    item.ownCity.forEach((ownItem: CityItem) => {
      ownItem.isCheck = e
      if (ownItem.isCheck) {
        item.cityList.push({
          value: ownItem.value,
        })
      }
    })
    item.isIndeterminate = item.cityList.length !== item.hasNum
    if (!e) {
      item.isIndeterminate = false
    }
  })
  getCheckAll()
}

// 选择省
function selectPro(item: ProvinceItem) {
  item.cityList = []
  item.ownCity.forEach((cityItem: CityItem) => {
    cityItem.isCheck = item.isCheck
    if (cityItem.isCheck) {
      item.cityList.push({
        value: cityItem.value,
      })
    }
  })
  item.hasNum = item.ownCity.length
  item.isIndeterminate = item.cityList.length > 0 && item.cityList.length < item.ownCity.length
  getCheckAll()
}

// 选择市
function selectCity(pro: ProvinceItem, city?: CityItem) {
  pro.cityList = []
  pro.ownCity.forEach((item: CityItem) => {
    if (item.isCheck) {
      pro.cityList.push({
        value: item.value,
      })
    }
  })
  pro.isIndeterminate = pro.cityList.length > 0 && pro.cityList.length < pro.ownCity.length
  pro.isCheck = pro.cityList.length === pro.ownCity.length
  pro.hasNum = pro.ownCity.length
}

// 新增时设置已选区域不可再选
function dealArea() {
  getNewCity()
  const getRegionData = JSON.parse(JSON.stringify(props.getRegion))
  hasCheckPro.value.pro.forEach((proItem) => {
    addressProv.value.forEach((addItem: ProvinceItem) => {
      if (proItem === addItem.value) {
        addItem.cityList = []
        addItem.ownCity.forEach((ownItem: CityItem) => {
          hasCheckPro.value.city.forEach((cityItem) => {
            if (cityItem === ownItem.value) {
              ownItem.isCheck = true
              ownItem.disabled = true
              addItem.cityList.push({
                value: ownItem.value,
              })
            }
          })
        })
        addItem.isCheck = addItem.hasNum > 0 && addItem.hasNum === addItem.cityList.length
        addItem.isIndeterminate = addItem.cityList.length > 0 && addItem.cityList.length < addItem.hasNum
        addItem.disabled = true
      }
    })
    if (getRegionData.length > 0) {
      getRegionData.forEach((gtItem: RegionItem) => {
        addressProv.value.forEach((addItem: ProvinceItem) => {
          addItem.cityList = []
          if (gtItem.fatherid === addItem.value) {
            addItem.ownCity.forEach((ownItem: CityItem) => {
              gtItem.list.forEach((cityItem: { value: string | number }) => {
                if (cityItem.value === ownItem.value) {
                  ownItem.isCheck = true
                  ownItem.disabled = false
                  if (ownItem.isCheck) {
                    addItem.cityList.push({
                      value: ownItem.value,
                    })
                  }
                }
              })
            })
            addItem.isCheck = addItem.hasNum > 0 && addItem.hasNum === addItem.cityList.length
            addItem.isIndeterminate = addItem.cityList.length > 0 && addItem.cityList.length < addItem.hasNum
            addItem.disabled = false
          }
        })
      })
    }
  })
  getCheckAll()
}

// 编辑时获取所有数据
function getNewCity() {
  const pros: (string | number)[] = []
  const citys: (string | number)[] = []

  const logisModelHasList = !props.freeArea ? props.modelValidateForm.logisticsShippingModelDtos : props.modelValidateForm.logisticsIncludePostageDtos

  logisModelHasList?.forEach((hsItem: any) => {
    hsItem.region = typeof hsItem.region === 'object' ? hsItem.region : JSON.parse(hsItem.region)
    hsItem.region.forEach((laItem: any) => {
      pros.push(laItem.fatherid)
      laItem.list.forEach((listItem: any) => {
        citys.push(listItem.value)
      })
    })
  })
  hasCheckPro.value.pro = [...new Set(pros)]
  hasCheckPro.value.city = [...new Set(citys)]
}

// 获取全选状态
function getCheckAll() {
  lengthList.value = []
  addressProv.value.forEach((item: ProvinceItem) => {
    if (item.isCheck) {
      lengthList.value.push(Number(item.value))
    }
  })
  checkAll.value = addressProv.value.length === lengthList.value.length
  isIndeterminate.value = lengthList.value.length > 0 && addressProv.value.length > lengthList.value.length
}

// 确认选择地区
function selectArea() {
  const region: RegionItem[] = []
  const conList: ProvinceItem[] = []
  addressProv.value.forEach((item: ProvinceItem) => {
    item.cityList = []
    item.ownCity.forEach((ownItem: CityItem) => {
      if (ownItem.isCheck && !ownItem.disabled) {
        item.cityList.push({
          value: ownItem.value,
        })
      }
    })
    if (item.cityList.length > 0) {
      conList.push(item)
      region.push({
        fatherid: item.value,
        list: item.cityList,
      })
    }
  })
  if (region.length === 0) {
    ElMessage.error('请至少选择一个区域')
    return
  }
  dialogVisible.value = false

  emit('getConList', region, conList, props.modelIndex, props.freeArea)
  initArea()
}
</script>

<template>
  <el-dialog v-model="dialogVisible" width="690px" style="margin-top: 60px" :before-close="handleClose">
    <template #header>
      <div class="digTitle">选择区域</div>
    </template>
    <div class="areaStyle">
      <div v-for="(item, index) in addressProv" :key="index">
        <div class="areaStyle__item">
          <el-checkbox v-model="item.isCheck" :indeterminate="item.isIndeterminate" :disabled="item.disabled" @change="selectPro(item)" />
          <div style="margin-left: 10px">
            {{ item.label }}
          </div>
          <el-popover trigger="hover" placement="right">
            <template #default>
              <div style="height: 60px; overflow: auto">
                <div v-for="(v, i) in item.ownCity" :key="i">
                  <div class="areaStyle__item--city">
                    <el-checkbox v-model="v.isCheck" :disabled="v.disabled" @change="selectCity(item, v)" />
                    {{ v.label }}
                  </div>
                </div>
              </div>
              <div class="areaStyle__item--close">关闭</div>
            </template>
            <template #reference>
              <span>
                <el-icon><ArrowRight /></el-icon>
              </span>
            </template>
          </el-popover>
        </div>
      </div>
    </div>
    <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate" @change="handleCheckAllChange"> 全选 </el-checkbox>
    <!-- <span style="margin-left:20px;">ps：港澳台暂不支持物流配送</span> -->
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="selectArea">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.digTitle {
  font-size: 17px;
}

.areaStyle {
  display: flex;
  flex-wrap: wrap;
  font-size: 14px;

  &__item {
    width: 142px;
    margin-right: 20px;
    margin-bottom: 20px;
    font-size: 13px;
    display: flex;

    &--city {
      margin-bottom: 3px;
      font-size: 13px;
    }

    &--close {
      margin-top: 10px;
      color: rgb(45, 140, 240);
      font-size: 12px;
      cursor: pointer;
    }
  }
}
</style>
