<script lang="ts" setup>
defineProps({
  name: {
    type: String,
    default: '',
  },
})

/** 选择状态 */
const emit = defineEmits(['changeSel'])

const selectList = ref([
  {
    value: '0',
    label: '全部状态',
  },
  {
    value: '1',
    label: '上架',
  },
  {
    value: '2',
    label: '下架',
  },
])

/** 默认选择状态 */
const selectVal = ref('')

function changeSel(e: string) {
  emit('changeSel', e)
}
</script>

<template>
  <div>
    <el-select v-model="selectVal" placeholder="全部状态" style="width: 120px" @change="changeSel">
      <el-option v-for="item in selectList" :key="item.value" :label="item.label" :value="item.value" />
    </el-select>
  </div>
</template>

<style lang="scss" scoped></style>
