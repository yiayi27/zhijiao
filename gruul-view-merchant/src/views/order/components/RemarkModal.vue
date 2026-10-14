<script setup lang="ts">
const props = defineProps({
  title: {
    type: String,
    default: '商家回复',
  },
  modelValue: {
    type: Boolean,
    default: false,
  },
  currentOrder: {
    type: Object,
    default: () => ({}),
  },
  orderIds: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['update:modelValue', 'onOk', 'onCancel'])

const form = ref({ note: '', over: false })

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (v) => {
    if (!v) {
      resetForm()
    }
    emit('update:modelValue', v)
  },
})

watch(
  () => props.currentOrder,
  (newVal) => {
    if (newVal) {
      form.value.note = newVal.note || ''
    }
  },
  { immediate: true },
)

function resetForm() {
  form.value = { note: '', over: false }
}

function onOk() {
  emit('onOk', form.value)
}

function onCancel() {
  emit('onCancel')
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :title="title" width="30%">
    <div v-if="orderIds.length > 1" style="margin-bottom: 15px">你选择了{{ orderIds.length }}记录</div>
    <el-input v-model="form.note" type="textarea" :rows="2" placeholder="请输入内容" />
    <div style="margin-top: 10px">
      <el-checkbox v-model="form.over"> 覆盖已有备注内容 </el-checkbox>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onCancel">取 消</el-button>
        <el-button type="primary" @click="onOk">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
