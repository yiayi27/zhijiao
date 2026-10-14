<script setup lang="ts">
const emit = defineEmits(['changeSel'])

// 选择状态
const selectVal = ref('')

// 选择列表
const selectList = [
  {
    value: '0',
    label: '全部订单',
  },
  {
    value: '1',
    label: '待发货',
  },
  {
    value: '2',
    label: '已发货',
  },
  {
    value: '3',
    label: '成功的订单',
  },
]

// 选择状态
function changeSel(value: string) {
  emit('changeSel', value)
}
</script>

<template>
  <div>
    <el-select v-model="selectVal" placeholder="全部订单" style="width: 120px" @change="changeSel">
      <el-option v-for="item in selectList" :key="item.value" :label="item.label" :value="item.value" />
    </el-select>
  </div>
</template>

<style lang="scss" scoped></style>
