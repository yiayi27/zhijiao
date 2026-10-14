<template>
  <view>
    <uv-form>
      <uv-form-item v-for="(item, index) in forms" :key="index" :label="item.key" :required="item.required">
        <uv-input
          v-if="isInputItem(item.type)"
          v-model="item.value"
          :placeholder="item.placeholder"
          border="none"
          :maxlength="item.type === 'phone' ? 11 : undefined"
        />
        <view v-else-if="isDateItem(item.type)" @click="setDatePopVisible(true)">
          <uv-input v-model="item.value" disabled disabledColor="#ffffff" :placeholder="item.placeholder" border="none" />
          <uv-datetime-picker
            ref="dateSelect"
            v-model="item.value"
            closeOnClickOverlay
            :mode="item.type || 'date'"
            @confirm="onDateConfirm($event, item.type)"
          />
        </view>
        <uv-upload
          v-else
          :fileList="item.value"
          name="1"
          :maxCount="1"
          previewFullImage
          @afterRead="afterRead($event, index)"
          @delete="deletePic($event, index)"
        />
        <template #right>
          <uv-icon v-if="isDateItem(item.type)" name="arrow-right"></uv-icon>
        </template>
      </uv-form-item>
    </uv-form>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { IFileItem, IFromItem } from './form.schema'
import DateUtil from '@/mixins/date'
import { uploadFile } from '@/utils/upload'
import { useCustomForm } from '@/composables/helper/useCustomForm'
import { isIDnumber, isPhone } from '@/utils/helper'
import { isNumber } from 'lodash-es'
import { $Popup } from '@/utils/helper'

type FileList = {
  file: {
    url: string
    status: 'uploading' | 'success' | 'fail'
    message?: string
  }
  name: string
}

const forms = defineModel<IFromItem[]>('forms', { default: () => [] })
const { isInputItem, isDateItem } = useCustomForm()

// 初始化
onMounted(() => {
  forms.value.forEach((item) => {
    if (!isInputItem(item.type) || !isDateItem(item.type)) {
      item.value = ''
    } else {
      // 文件类型
      item.value = []
    }
  })
})

const afterRead = async (e: FileList, index: number) => {
  try {
    const res = await uploadFile(e.file.url)
    forms.value[index].value = res as IFileItem[]
  } catch (error) {
    console.log(error)
    $Popup.toast('上传失败')
  }
}

const deletePic = (e: { index: number }, index: number) => {
  if (Array.isArray(forms.value[index].value)) {
    forms.value[index].value.splice(e.index, 1)
  }
}

// 状态变量
const dateSelect = ref()

// 基础工具函数
// 设置日期选择器的可见性
const setDatePopVisible = (value: boolean) => {
  if (value) {
    dateSelect.value.open()
  } else {
    dateSelect.value.close()
  }
}

// 字段验证函数 - 验证表单字段的有效性
const verificationField = (form: IFromItem) => {
  switch (form.type) {
    case 'id':
      return isIDnumber(form.value as string)
    case 'phone':
      return isPhone(form.value as string)
    case 'number':
      return isNumber(+form.value)
    default:
      return true
  }
}

// 处理日期确认选择
const onDateConfirm = (e: { value: number | string }, type?: string) => {
  let time = ''
  const dateUtil = new DateUtil(e.value)
  switch (type) {
    case 'date':
      time = dateUtil.getYMDs()
      break
    case 'time':
      time = e.value as string
      break
    case 'datetime':
      time = dateUtil.getYMDHMSs()
      break
    default:
      break
  }
  console.log(time)
}

// 表单验证 - 暴露给父组件使用
const verificationForm = () => {
  const emptyItem = forms.value.filter((item) => item.required).find((item) => !item.value || item.value.length === 0)
  if (emptyItem) {
    $Popup.toast(`${emptyItem.key}不可为空`)
    return false
  }

  const errorItem = forms.value.find((item) => !!item.value && !verificationField(item))
  if (errorItem) {
    $Popup.toast(`请填写正确的${errorItem.key}`)
    return false
  }

  return true
}

// 暴露给父组件的方法
defineExpose({
  verificationForm,
})
</script>

<style scoped></style>
