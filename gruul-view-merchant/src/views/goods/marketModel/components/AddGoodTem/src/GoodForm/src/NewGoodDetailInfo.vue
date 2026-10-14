<script setup lang="ts">
import exImg from '@/assets/images/ex_img.png'
import temModel from '@/assets/images/tem_model.png'
import RichEditor from '@/components/RichEditor/RichEditor.vue'
import { NEW_GOOD_SHOP_INFO_MODEL } from '@/libs/enum/storage'
import { storage } from '@/libs/storage'

// 定义props
const props = defineProps({
  goodDetail: {
    type: Object,
    default: () => ({}),
  },
})

// 响应式状态
const detailText = ref('')
const wEditor = ref()
const modelValue = ref(1)
const showTemModel = ref(false)

// 详情模板选项
const modelOptions = [
  {
    label: '规格折叠',
    value: 1,
  },
  {
    label: '规格展开',
    value: 0,
  },
]

// 选择模板值
function selectModelValue(e: number) {
  modelValue.value = e
}

// 获取HTML内容
function getDetailHtml() {
  return wEditor.value?.getHtml()
  // return (this.$refs.ue as any).getUEContent();
}

// 获取新表单数据
function getNewForm() {
  return {
    detail: getDetailHtml(),
    openSpecs: Boolean(modelValue.value),
  }
}

// 设置表单模型
function setFormModel(nVal: any) {
  if (!nVal || Object.keys(nVal).length === 0) return
  modelValue.value = nVal.openSpecs ? 1 : 0
  detailText.value = nVal.detail || ''
}

// 获取表单模型
async function getFormModel() {
  return {
    /** 商品详情 */
    detail: getDetailHtml(),
    /** 规格是否展开 */
    openSpecs: Boolean(modelValue.value),
  }
}

// 组件挂载时初始化
onMounted(() => {
  setFormModel(storage.get(NEW_GOOD_SHOP_INFO_MODEL) || props.goodDetail)
})

// 暴露方法供父组件调用
defineExpose({
  getFormModel,
  getNewForm,
})
</script>

<template>
  <div class="pageRoll">
    <div class="shopMsg">
      <img :src="exImg" alt="" style="width: 320px; height: 667px" />
      <div class="shopMsg__right">
        <div class="shopMsg__right--wed">
          <div class="shopMsg__right--wed--text">商品页模板</div>
          <el-select v-model="modelValue" placeholder="请选择" style="width: 300px" :popper-append-to-body="false" @change="selectModelValue">
            <el-option v-for="item in modelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button style="margin-left: 20px" link plain @click="showTemModel = true"> 示例 </el-button>
        </div>
        <div style="margin-top: 36px">
          <RichEditor ref="wEditor" :text="detailText" />
        </div>
      </div>
    </div>
    <el-dialog v-model="showTemModel" width="50%" title="查看示例">
      <img :src="temModel" style="width: 100%; height: auto" />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.shopMsg {
  display: flex;
}

.shopMsg__right {
  flex: 1;
  overflow: hidden;
}
</style>
