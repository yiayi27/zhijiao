<script setup lang="ts">
import { cloneDeep } from 'lodash-es'

interface FormData {
  goodsName?: string
  orderId?: string
  rate?: number | string
  [key: string]: any
}

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:modelValue'])

const form = ref<FormData>({
  goodsName: '',
  orderId: '',
  rate: '',
})

interface StarOption {
  label: string
  value: number | string
}

const starOption = computed(() => {
  const list: StarOption[] = Array.from({ length: 5 })
    .fill('')
    .map((_, i) => {
      return {
        label: `${i + 1}星`,
        value: i + 1,
      }
    })
  list.unshift({
    label: '全部',
    value: '',
  })
  return list
})

// 日期范围
const dateRange = ref([])

function emitSearch() {
  emit(
    'update:modelValue',
    cloneDeep({
      ...props.modelValue,
      ...form.value,
      payTimeStart: dateRange.value?.[0],
      payTimeEnd: dateRange.value?.[1],
    }),
  )
}
</script>

<template>
  <m-card :need-toggle="true" hide-text="展开搜索条件" show-text="收起搜索条件">
    <el-form :model="form" label-width="90px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="商品名称">
            <el-input v-model="form.goodsName" placeholder="请输入商品名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订单号">
            <el-input v-model="form.orderId" placeholder="请输入订单号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="成交时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              value-format="yyyy-MM-dd"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="评价星级">
            <el-select v-model="form.rate" style="width: 100%" placeholder="请选择评价星级">
              <el-option v-for="(star, i) in starOption" :key="i" :label="star.label" :value="star.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="emitSearch"> 搜索 </el-button>
      </el-form-item>
    </el-form>
  </m-card>
</template>
