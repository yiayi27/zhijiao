<script setup lang="ts">
import { getControl } from '@/api/decoration/decoration'

interface ControlItem {
  pluginProperties: string
  pluginNameCn: string
  value: string
  [key: string]: any
}

// 定义事件
const emit = defineEmits(['change', 'saveControlId'])

// 状态定义
const controlList = ref<ControlItem[]>([])

/**
 * 处理添加组件
 */
function handleAddComponent(item: ControlItem) {
  const obj = {
    icon: JSON.parse(item.pluginProperties)[0].icon,
    value: JSON.parse(item.pluginProperties)[0].value,
    // label: JSON.parse(item.pluginProperties)[0].label,
    label: item.pluginNameCn,
  }
  emit('change', {
    ...obj,
    id: Date.now(),
    formData: JSON.parse(item.pluginProperties)[0].formData,
  })
}

/**
 * 保存控件 ID
 */
function saveControlId(item: ControlItem, index: number) {
  emit('saveControlId', {
    item,
    index,
  })
}

/**
 * 初始获取控件数据
 */
async function getControlList() {
  const res = await getControl({})
  if (res.code === 200) {
    controlList.value = res.data
    if (!res.data.length) {
      ElMessage.warning(`获取数据为空`)
    }
  }
}

// 生命周期钩子 - 初始获取控件数据
onMounted(() => {
  getControlList()
})
</script>

<template>
  <!-- 控件管理页面 -->
  <div class="editor_control_wrap">
    <div class="editor_control_wrap_main">
      <el-scrollbar style="height: 100%">
        <div v-for="(item, index) of controlList" :key="item.value" class="checklist_item">
          <div>
            {{ JSON.parse(item.pluginProperties) && JSON.parse(item.pluginProperties).length > 0 ? item.pluginNameCn : '未命名' }}
          </div>
          <img class="edit_icon" src="@/assets/images/moduleIcon/u1141.png" @click="(saveControlId(item, index), handleAddComponent(item))" />
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/editorControl';
@import '@/assets/styles/decoration/editorPage';
.editor_control_wrap {
  width: 100%;
}
</style>
