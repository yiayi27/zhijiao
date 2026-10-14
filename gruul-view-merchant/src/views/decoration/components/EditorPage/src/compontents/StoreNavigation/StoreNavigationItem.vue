<script setup lang="ts">
import type { LinkSelectItem } from '@/components/LinkSelect/type'
import type StoreNavigationItemTs from './StoreNavigationItemTs'
import LinkSelect from '@/components/LinkSelect'
import UploadFile from '@/views/decoration/components/EditorPage/src/compontents/NavBar/UploadFile.vue'
import { useVModel } from '@vueuse/core'

const props = defineProps<{
  formData: StoreNavigationItemTs
  itemIndex: number
}>()

const emit = defineEmits(['onDelect', 'update:formData'])
const formData = useVModel(props, 'formData', emit)

const link = ref<LinkSelectItem>({
  id: '',
  type: '',
  name: '',
  url: '',
  append: '',
})

/** 删除店铺导航tab */
function delect(_formData: StoreNavigationItemTs, itemIndex: number) {
  emit('onDelect', itemIndex)
}

// 监听link的变化
watch(
  link,
  (newVal) => {
    /** 链接地址 */
    formData.value.linkUrl = newVal.url
    /** 链接名称 */
    formData.value.linkName = newVal.name
    /** 首页 链接append */
    formData.value.append = newVal.append
    /** 类型 type5为自定义页面 */
    formData.value.type = newVal.type
    formData.value.id = newVal.id
  },
  { deep: true },
)

onMounted(() => {
  const formData = props.formData
  link.value = {
    id: formData.id,
    type: formData.type,
    name: formData.linkName,
    url: formData.linkUrl,
    append: formData.append,
  }
})
</script>

<template>
  <!-- 店铺导航 -->
  <div class="storeNavigation-item-form">
    <el-form :model="formData" label-width="85px" style="margin-top: 15px">
      <el-form-item label="导航名称">
        <el-input v-model="formData.navName" :maxlength="6" placeholder="请输入导航名称" />
      </el-form-item>
      <el-form-item label="字体颜色">
        <el-color-picker v-model="formData.fontColor" />
      </el-form-item>
      <el-form-item label="图标">
        <UploadFile v-model:img-url="formData.navIcon" />
      </el-form-item>

      <el-form-item label="跳转路径">
        <LinkSelect :link="link" />
        <div v-if="link.type !== 6">
          <span style="color: #9797a1">{{ formData.linkName }}</span>
        </div>
      </el-form-item>
    </el-form>
    <img
      style="width: 35px; height: 35px; cursor: pointer; position: absolute; top: -5px; right: -5px"
      class="bar_item_del_icon"
      src="@/assets/images/del.png"
      @click="delect(formData, itemIndex)"
    />
  </div>
</template>
