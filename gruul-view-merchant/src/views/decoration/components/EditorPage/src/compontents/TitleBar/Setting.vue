<script setup lang="ts">
import type { LinkSelectItem } from '@/components/LinkSelect/type'

import TitleBar from './TitleBar'
import LinkSelect from '@/components/LinkSelect'
import { getDefaultLinkData } from '@/components/LinkSelect/constants'
import { useVModel } from '@vueuse/core'

const props = defineProps({
  formData: {
    type: Object as PropType<typeof TitleBar>,
    required: true,
  },
})

const emit = defineEmits(['update:formData'])

const formData = useVModel(props, 'formData', emit)
const showStyles = [
  {
    label: '样式一',
    value: 'is-style-one',
  },
  {
    label: '样式二',
    value: 'is-style-two',
  },
]

const link = ref<LinkSelectItem>(getDefaultLinkData())

/**
 * 点击链接选择
 */
watch(
  () => link.value,
  (linkDataItem) => {
    formData.value.id = linkDataItem.id as string
    formData.value.type = linkDataItem.type as string
    formData.value.append = linkDataItem.append as string
    formData.value.pathLink = linkDataItem.url
    formData.value.linkName = linkDataItem.name
  },
  { deep: true },
)

onMounted(() => {
  const formData = props.formData
  link.value = {
    id: formData.id as string,
    type: formData.type as string,
    append: formData.append as string,
    name: formData.linkName,
    url: formData.pathLink,
  }
})
</script>

<template>
  <!-- 标题栏 -->
  <el-form :model="formData" label-width="100px">
    <el-form-item label="选择样式">
      <el-radio-group v-model="formData.showStyle">
        <el-radio v-for="item in showStyles" :key="item.value" :value="item.value">
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="标题名称">
      <el-input v-model="formData.titleName" :maxlength="10" placeholder="请输入标题名称" />
    </el-form-item>
    <el-form-item label="背景颜色">
      <el-color-picker v-model="formData.backgroundColor" />
    </el-form-item>
    <el-form-item label="文字颜色">
      <el-color-picker v-model="formData.color" />
    </el-form-item>
    <el-form-item v-if="formData.showStyle === 'is-style-two'" label="跳转路径">
      <LinkSelect v-model:link="link" :inner="true" />
      <div v-if="link.type !== 6">
        <span style="color: #9797a1">{{ formData.linkName }}</span>
      </div>
    </el-form-item>
  </el-form>
</template>
