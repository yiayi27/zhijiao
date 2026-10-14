<script setup lang="ts">
import { cloneDeep } from 'lodash-es'

interface FormData {
  goodsName?: string
  userName?: string
  receiverName?: string
  orderId?: string
  deliverySn?: string
  remarkType?: string
  area?: string
  [key: string]: any
}

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({}),
  },
  lines: {
    type: [Number, String],
    default: '',
  },
})

const emit = defineEmits(['update:modelValue'])

const form = ref<FormData>({})

// 日期范围
const dateRange = ref([])

/** 备注选项 */
const remarkOption = ref([
  {
    label: '全部状态',
    value: '0',
  },
  {
    label: '有备注',
    value: '1',
  },
  {
    label: '无备注',
    value: '2',
  },
])

/** 销售专区 */
const areas = computed(() => {
  return [
    {
      label: '商超',
      value: '2',
    },
  ]
})

/** 触发父级查询 */
function emitSeach(): void {
  emit(
    'update:modelValue',
    cloneDeep({
      ...props.modelValue,
      ...form.value,
      current: 1,
      startTime: dateRange.value?.[0],
      endTime: dateRange.value?.[1],
    }),
  )
}
</script>

<template>
  <m-card class="form" hide-text="展开搜索条件" show-text="收起搜索条件" :need-toggle="true">
    <el-form :model="form" label-width="90px">
      <el-row>
        <el-col :span="8">
          <el-form-item label="商品名称">
            <el-input v-model="form.goodsName" placeholder="请输入商品名称" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="买家昵称">
            <el-input v-model="form.userName" placeholder="请输入买家昵称" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="收货人姓名">
            <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8">
          <el-form-item label="订单号">
            <el-input v-model="form.orderId" placeholder="请输入订单号" />
          </el-form-item>
        </el-col>

        <el-col :span="8">
          <el-form-item label="物流单号">
            <el-input v-model="form.deliverySn" placeholder="请输入物流单号" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="备注状态">
            <el-select v-model="form.remarkType" placeholder="请选择备注状态" style="width: 223px">
              <el-option v-for="item in remarkOption" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8">
          <el-form-item label="销售专区">
            <el-select v-model="form.area" placeholder="请选择销售专区" style="width: 223px">
              <el-option v-for="item in areas" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="8">
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
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="emitSeach"> 搜索 </el-button>
      </el-form-item>
    </el-form>
  </m-card>
</template>

<style lang="scss" scoped>
.form {
  transform-origin: left top;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease 0s;

  &__btn {
    width: 100%;
    position: absolute;
    bottom: 0;
    text-align: center;
    padding-bottom: 20px;

    span {
      cursor: pointer;
    }
  }
}
</style>
