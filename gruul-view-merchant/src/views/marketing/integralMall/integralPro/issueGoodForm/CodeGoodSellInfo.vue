<script setup lang="ts">
import exImg from '@/assets/images/ex_img.png'
import RichEditor from '@/components/RichEditor/RichEditor.vue'

interface FormModel {
  detail?: string
  [key: string]: any
}

interface RichEditorInstance {
  getHtml: () => string
}

interface FormData {
  detail: string
}

const props = defineProps({
  formModel: {
    type: Object as () => FormModel,
    default: () => ({}),
  },
})

/**
 * 编辑商品信息
 */
const wEditor = ref<RichEditorInstance>()
const detailText = ref('')

// 监听formModel变化
watch(
  () => props.formModel,
  (nVal: FormModel) => {
    if (nVal) {
      detailText.value = nVal.detail || ''
    }
  },
  { deep: true },
)

// 获取富文本编辑器HTML
function getDetailHtml(): string {
  return wEditor.value?.getHtml() || ''
}

onMounted(() => {
  setFormModel()
})

// 设置表单数据
function setFormModel(): void {
  const goodDetail = props.formModel
  if (goodDetail) {
    detailText.value = goodDetail.detail || ''
  }
}

// 获取表单数据
function getFormModel(): FormData {
  return {
    detail: getDetailHtml(),
  }
}

// 暴露方法给父组件
defineExpose({
  getFormModel,
})
</script>

<template>
  <div class="pageRoll">
    <div class="shopMsg">
      <img :src="exImg" alt="" style="width: 375px; height: 667px" />
      <div class="shopMsg__right">
        <div style="margin-top: 18px">
          <RichEditor ref="wEditor" :text="detailText" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.shopMsg {
  display: flex;
  padding-top: 40px;
  margin-left: 20px;
  &__right {
    margin-left: 20px;
    &--wed {
      display: flex;
      align-items: center;
      margin-top: 40px;
      height: 40px;
      &--text {
        margin-top: 10px;
        margin-right: 20px;
      }
    }
  }
}
</style>
