<script lang="ts" setup>
import type { OptionType, OrderItem } from '@/views/decoration/interfaces/UserCenter'
import UploadFile from '@/views/decoration/components/UserCenter/UploadFile.vue'

import useDialogMixin from './formModel/DialogMixin'

const props = defineProps({
  orderVisible: {
    type: Boolean,
    default: false,
  },
  orderProp: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:orderVisible', 'updateOrder'])

// 使用转换后的DialogMixin
const { beforeCloseHandle, closeHandle } = useDialogMixin()

// 使用计算属性实现双向绑定
const dialogVisible = ref(props.orderVisible)
watch(
  () => props.orderVisible,
  (newVal) => {
    dialogVisible.value = newVal
  },
)
watch(dialogVisible, (newVal) => {
  emit('update:orderVisible', newVal)
})

// 订单项数据
const orderItem = ref<OrderItem & OptionType>({
  key: '',
  name: '',
  url: '',
})

// 监听对话框显示状态
watch(dialogVisible, (v) => {
  if (v === true) {
    orderItem.value.key = props.orderProp.key
    orderItem.value.name = props.orderProp.name
    orderItem.value.url = props.orderProp.url
  }
})

// 确认按钮处理
function comfirmClickHandle() {
  emitUpdateMenu()
  dialogVisible.value = false
}

/**
 * 触发更新菜单事件
 */
function emitUpdateMenu() {
  emit('updateOrder', orderItem.value)
}
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :before-close="beforeCloseHandle"
    append-to-body
    :modal-append-to-body="false"
    width="450px"
    title="订单栏图标编辑"
  >
    <el-form :model="orderItem" label-width="120px">
      <el-form-item label="订单名称">
        {{ orderItem.name }}
      </el-form-item>
      <el-form-item label="订单图标">
        <UploadFile v-model:img-url="orderItem.url" />
        <div class="el-upload__tip" style="color: #9797a1">(图片格式支持尺寸15×15px,格式 png、jpg、jpeg，gif。)</div>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog--footer">
        <el-button @click="closeHandle">取 消</el-button>
        <el-button type="primary" @click="comfirmClickHandle">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.form--item {
  display: flex;
  justify-content: flex-start;
  align-content: flex-end;
  padding: 10px;

  .form-item__label {
    width: 120px;
    text-align: left;
    vertical-align: middle;
    float: left;
    font-size: 14px;
    color: #606266;
    line-height: 14px;
    box-sizing: border-box;
  }
}

.el-upload__tip {
  color: #9797a1;
}
</style>
