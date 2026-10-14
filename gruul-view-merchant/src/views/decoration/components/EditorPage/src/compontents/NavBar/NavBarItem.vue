<script setup lang="ts">
import type { LinkSelectItem } from '@/components/LinkSelect/type'

import type NavBarItem from './NavBarItem'
import LinkSelect from '@/components/LinkSelect'
import { getDefaultLinkData } from '@/components/LinkSelect/constants'
import UploadFile from '@/views/decoration/components/EditorPage/src/compontents/NavBar/UploadFile.vue'
import { useVModel } from '@vueuse/core'

interface FormDataType {
  linkUrl: string
  linkName: string
  name: string
  type: Long
  id: string | number | null
  isAdd?: boolean
  iconType: number
  isHome?: boolean
  [key: string]: any
}

const props = defineProps({
  formData: {
    type: Object as PropType<FormDataType>,
    required: true,
  },
  itemIndex: {
    type: Number,
    required: true,
  },
})

const emit = defineEmits(['onSetHomeTab', 'onDelect', 'setDefIcon', 'update:formData'])
const formData = useVModel(props, 'formData', emit)
const link = ref<LinkSelectItem>(getDefaultLinkData())

/** 点击链接选择 */
watch(
  link,
  (newVal) => {
    /** 链接地址 */
    formData.value.linkUrl = newVal.url
    /** 链接名称 */
    formData.value.linkName = newVal.name
    formData.value.name = newVal.append as string
    formData.value.type = newVal.type as string
    formData.value.id = newVal.id || null
    if (!formData.value.isAdd) {
      formData.value.iconType = 2
    }
  },
  { deep: true },
)

function changeIcon(index: number, formData: FormDataType) {
  if (formData.iconType === 1) {
    emit('setDefIcon', index)
  }
}

/** 删除底部导航tab */
function delect(formData: FormDataType, itemIndex: number) {
  if (formData.isHome) {
    ElMessage.error('该导航已设为首页,不允许删除')
  } else {
    emit('onDelect', itemIndex)
  }
}

onMounted(() => {
  if (formData.value.iconType === 1) {
    changeIcon(props.itemIndex, formData.value)
  }

  link.value = {
    id: formData.value.id,
    type: formData.value.type,
    name: formData.value.linkName,
    url: formData.value.linkUrl,
    append: formData.value.name,
  }
})
</script>

<template>
  <!-- 底部导航 -->
  <div class="bar_item navBar__setting">
    <div class="bar_item_top">
      <span v-if="formData.isHome">
        <img class="bar_home_icon" src="@/assets/images/decoration/u325.png" />
        <span style="color: #7f7f7f; fontsize: 16px; marginleft: 5px">首页</span>
      </span>
    </div>
    <el-form-item label="名称" prop="name">
      <el-input v-model="formData.text" :maxlength="3" />
    </el-form-item>
    <el-form-item v-if="itemIndex !== 0" label="链接" prop="link">
      <LinkSelect get-from="bottomNav" :inner="true" :link="link" :no-pro-tab="true" :limit-pro-tab="false" />
      <div>
        <span v-if="link.type !== 6" style="color: #9797a1">{{ formData.linkName }}</span>
      </div>
    </el-form-item>
    <el-form-item label="图标" prop="icon">
      <el-radio-group v-model="formData.iconType" @change="changeIcon(itemIndex, formData)">
        <el-radio v-if="formData.isAdd" :value="1"> 系统图标 </el-radio>
        <el-radio :value="2"> 自定义图标 </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="formData.iconType === 2" label="图片" prop="pic">
      <div style="display: flex">
        <div>
          <UploadFile v-model:img-url="formData.iconPath" />
          <span style="color: #9797a1; marginleft: 12px">未选中</span>
        </div>
        <div style="margin-left: 10px">
          <UploadFile v-model:img-url="formData.selectedIconPath" />
          <span style="color: #9797a1; marginleft: 17px">选中</span>
        </div>
      </div>
      <div>
        <span style="color: #9797a1">建议尺寸为50*50，支持png格式</span>
      </div>
    </el-form-item>
    <el-form-item v-if="formData.iconType === 1" label="图片" prop="pic">
      <div style="display: flex">
        <div>
          <img v-if="formData.defIcon" :src="formData.defIcon" class="avatar" />
          <span style="color: #9797a1; marginleft: 12px">未选中</span>
        </div>
        <div style="margin-left: 10px">
          <img v-if="formData.actIcon" :src="formData.actIcon" class="avatar" />
          <span style="color: #9797a1; marginleft: 17px">选中</span>
        </div>
      </div>
      <div>
        <span style="color: #9797a1">建议尺寸为50*50，支持png格式</span>
      </div>
    </el-form-item>

    <img class="bar_item_del_icon" src="@/assets/images/del.png" @click="delect(formData, itemIndex)" />
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/navBar';
.navBar__setting {
  .avatar {
    height: 50px !important;
    width: 50px !important;
  }
}
</style>
