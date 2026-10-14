<script setup lang="ts">
import { getLogisticsContent } from '@/api/good/goods'
import BaseTable from '@/components/BaseTable/index.vue'
import address from '@/views/goods/common/city.js'

// 定义类型接口
interface RegionItem {
  fatherid: string | number
  list: any[]
  [key: string]: any
}

interface ShippingModelItem {
  area?: string
  region: string
  firstPiece?: string | number
  firstWeight?: string | number
  firstAmount?: string | number
  secondPiece?: string | number
  secondWeight?: string | number
  secondAmount?: string | number
  [key: string]: any
}

interface ModelOption {
  logisticsShippingModelVos?: ShippingModelItem[]
  valuationModel?: number
  [key: string]: any
}

const props = defineProps({
  templateId: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['getModelOption'])

/** 获取模板内容 */
const modelOption = ref<ModelOption>({})

function rowClass() {
  return { background: '#f6f8fa', 'font-weight': 'normal' }
}

watch(
  () => props.templateId,
  () => {
    getModelContent()
  },
)

onMounted(() => {
  getModelContent()
})

/**
 * 获取物流详情
 */
async function getModelContent() {
  if (Number(props.templateId) > 0) {
    const res = (await getLogisticsContent(props.templateId, {})) as any
    res.data.logisticsShippingModelVos.forEach((item: ShippingModelItem) => {
      item.area = dealArea(JSON.parse(item.region))
    })
    modelOption.value = res.data
    emit('getModelOption', modelOption.value)
  } else {
    modelOption.value = {}
  }
}

/**
 * 获取省市区所有数据
 */
function dealArea(region: RegionItem[]): string {
  const addressProv = address.getProvince()
  let addressCity = []
  const area: string[] = []
  region.forEach((item: RegionItem) => {
    addressProv.forEach((proItem: any) => {
      if (item.fatherid === proItem.value) {
        addressCity = address.getCity(proItem.value)
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
</script>

<template>
  <div v-if="templateId && modelOption.logisticsShippingModelVos">
    <BaseTable :data="modelOption.logisticsShippingModelVos" border class="my-[20px] w-full" :header-cell-style="rowClass">
      <el-table-column label="可配送区域" style="color: red">
        <template #default="scope">
          <div>{{ scope.row.area }}</div>
        </template>
      </el-table-column>
      <el-table-column v-if="modelOption.valuationModel === 1" label="首件(件)">
        <template #default="scope">
          <div class="noClick">
            {{ scope.row.firstPiece }}
          </div>
        </template>
      </el-table-column>
      <el-table-column v-else label="首重(kg)">
        <template #default="scope">
          <div class="noClick">
            {{ scope.row.firstWeight }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="首费(元)">
        <template #default="scope">
          <div class="noClick">
            {{ scope.row.firstAmount }}
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="modelOption.valuationModel === 1" label="续件(件)">
        <template #default="scope">
          <div class="noClick">
            {{ scope.row.secondPiece }}
          </div>
        </template>
      </el-table-column>
      <el-table-column v-else label="续重(kg)">
        <template #default="scope">
          <div class="noClick">
            {{ scope.row.secondWeight }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="续费(元)">
        <template #default="scope">
          <div class="noClick">
            {{ scope.row.secondAmount }}
          </div>
        </template>
      </el-table-column>
    </BaseTable>
  </div>
</template>

<style lang="scss" scoped>
.noClick {
  width: 80px;
  height: 30px;
  background-color: #f0f0f0;
  border: 1px solid #797979;
  display: flex;
  align-items: center;
  padding-left: 10px;
}
</style>
