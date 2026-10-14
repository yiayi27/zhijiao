<script setup lang="ts">
import { cloneDeep } from 'lodash-es'

interface FormData {
  goodsName?: string
  userName?: string
  receiverName?: string
  orderId?: string
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

const form = ref<FormData>({
  goodsName: '',
  userName: '',
  receiverName: '',
  orderId: '',
})

// 日期范围
const dateRange = ref([])

/** 触发父级查询 */
function emitSeach() {
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

  &.show {
    height: 264px;
  }

  &.hide {
    height: 50px;

    .form__btn {
      width: 940px;
      height: 50px;
      background: #f9f9f9;
      line-height: 50px;
      // margin-top: 20px
    }
  }

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
