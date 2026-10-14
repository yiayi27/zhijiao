<template>
  <view class="w-picker-view">
    <picker-view class="d-picker-view" :indicator-style="itemHeight" :value="pickVal" @change="handlerChange">
      <picker-view-column>
        <view v-for="(item, index) in range.provinces" :key="index" class="w-picker-item">{{ item.label }}</view>
      </picker-view-column>
      <picker-view-column>
        <view v-for="(item, index) in range.citys" :key="index" class="w-picker-item">{{ item.label }}</view>
      </picker-view-column>
      <picker-view-column v-if="!hideArea">
        <view v-for="(item, index) in range.areas" :key="index" class="w-picker-item">{{ item.label }}</view>
      </picker-view-column>
    </picker-view>
  </view>
</template>

<script setup lang="ts">
import areaData from './areadata/areadata'
import { ref, reactive, watch, nextTick, onMounted, type PropType } from 'vue'

// 定义接口
interface RegionItem {
  label: string
  value: Long
  children?: RegionItem[]
  [key: string]: any
}

interface RegionObj {
  province: RegionItem
  city: RegionItem
  area?: RegionItem
  [key: string]: any
}

// const areaData: RegionItem[] = []

// 定义Props
const props = defineProps({
  itemHeight: {
    type: String,
    default: '88rpx',
  },
  value: {
    type: [Array, String] as PropType<Long[] | Long>,
    default: '',
  },
  defaultType: {
    type: String,
    default: 'label',
  },
  hideArea: {
    type: Boolean,
    default: false,
  },
})

// 定义emit
const emit = defineEmits(['change'])

// 响应式数据
const pickVal = ref<number[]>([])
const range = reactive<{
  provinces: RegionItem[]
  citys: RegionItem[]
  areas: RegionItem[]
}>({
  provinces: [],
  citys: [],
  areas: [],
})
const checkObj = reactive<RegionObj>({
  province: { label: '', value: '' },
  city: { label: '', value: '' },
})

// 方法
const getData = () => {
  //用来处理初始化数据
  let provinces = areaData
  let dVal: number[] = []
  let value = props.value
  let a1 = Array.isArray(value) ? value[0] : '' //默认值省
  let a2 = Array.isArray(value) ? value[1] : '' //默认值市
  let a3 = Array.isArray(value) ? value[2] : '' //默认值区、县
  let province, city, area
  let provinceIndex = provinces.findIndex((v: RegionItem) => {
    return v[props.defaultType] === a1
  })
  provinceIndex = value ? (provinceIndex !== -1 ? provinceIndex : 0) : 0

  let citys = provinces[provinceIndex]?.children || []
  let cityIndex = citys.findIndex((v: RegionItem) => {
    return v[props.defaultType] === a2
  })
  cityIndex = value ? (cityIndex !== -1 ? cityIndex : 0) : 0

  let areas = citys[cityIndex]?.children || []
  let areaIndex = areas.findIndex((v: RegionItem) => {
    return v[props.defaultType] === a3
  })
  areaIndex = value ? (areaIndex !== -1 ? areaIndex : 0) : 0

  dVal = props.hideArea ? [provinceIndex, cityIndex] : [provinceIndex, cityIndex, areaIndex]
  province = provinces[provinceIndex] || { label: '', value: '' }
  city = citys[cityIndex] || { label: '', value: '' }
  area = areas[areaIndex] || { label: '', value: '' }

  let obj: RegionObj = props.hideArea
    ? {
        province,
        city,
      }
    : {
        province,
        city,
        area,
      }

  return props.hideArea
    ? {
        provinces,
        citys,
        dVal,
        obj,
      }
    : {
        provinces,
        citys,
        areas,
        dVal,
        obj,
      }
}

const initData = () => {
  let dataData = getData()
  let provinces = dataData.provinces
  let citys = dataData.citys
  let areas = props.hideArea ? [] : dataData.areas
  let obj = dataData.obj
  let province = obj.province,
    city = obj.city,
    area = props.hideArea ? { label: '', value: '' } : obj.area || { label: '', value: '' }

  let value = props.hideArea ? [province.value, city.value] : [province.value, city.value, area.value]
  let result = props.hideArea ? `${province.label + city.label}` : `${province.label + city.label + area.label}`

  Object.assign(
    range,
    props.hideArea
      ? {
          provinces,
          citys,
        }
      : {
          provinces,
          citys,
          areas,
        },
  )

  Object.assign(checkObj, obj)

  nextTick(() => {
    pickVal.value = dataData.dVal
  })

  emit('change', {
    result: result,
    value: value,
    obj: obj,
  })
}

const handlerChange = (e: { detail: { value: number[] } }) => {
  let arr = [...e.detail.value]
  let provinceIndex = arr[0],
    cityIndex = arr[1],
    areaIndex = props.hideArea ? 0 : arr[2]

  let provinces = areaData
  let citys = provinces[provinceIndex]?.children || provinces[provinces.length - 1]?.children || []
  let areas = props.hideArea ? [] : citys[cityIndex]?.children || citys[citys.length - 1]?.children || []

  let province = provinces[provinceIndex] || provinces[provinces.length - 1] || { label: '', value: '' },
    city = citys[cityIndex] || { label: '', value: '' },
    area = props.hideArea ? { label: '', value: '' } : areas[areaIndex] || { label: '', value: '' }

  let obj: RegionObj = props.hideArea
    ? {
        province,
        city,
      }
    : {
        province,
        city,
        area,
      }

  if (checkObj.province.label !== province.label) {
    //当省更新的时候需要刷新市、区县的数据;
    range.citys = citys
    if (!props.hideArea) {
      range.areas = areas
    }
  }

  if (checkObj.city.label !== city.label) {
    //当市更新的时候需要刷新区县的数据;
    if (!props.hideArea) {
      range.areas = areas
    }
  }

  Object.assign(checkObj, obj)

  nextTick(() => {
    pickVal.value = arr
  })

  let result = props.hideArea ? `${province.label + city.label}` : `${province.label + city.label + (area?.label || '')}`
  let value = props.hideArea ? [province.value, city.value] : [province.value, city.value, area?.value || '']

  emit('change', {
    result: result,
    value: value,
    obj: obj,
  })
}

// 监听props变化
watch(
  () => props.value,
  () => {
    initData()
  },
)

// 生命周期
onMounted(() => {
  initData()
})
</script>

<style lang="scss">
@import './w-picker.css';
</style>
