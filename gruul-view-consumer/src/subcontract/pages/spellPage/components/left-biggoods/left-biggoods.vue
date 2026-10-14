<template>
  <view class="left__biggoods-page">
    <view v-for="(item, index) in dataList" :key="index" class="con__box">
      <view class="i__title">{{ item.title }}</view>
      <LeftSecondgoodsPt v-if="modeType === 0" :propData="formData" :dataList="item.list" />
      <LeftSecondgoodssc v-else :propData="formData" :dataList="item.list" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { getEffectCategoryGoods } from '@/api/modules/goods'
import LeftSecondgoodsPt from '../left-secondgoods/left-secondgoods.vue'
import LeftSecondgoodssc from '@/pages/index/modules/Mall/components/LeftSecondGoods/LeftSecondGoods.vue'

// 定义props
const props = defineProps<{
  propData?: Obj
}>()

// 响应式变量
const formData = ref<Obj>({})
const dataList = ref<any[]>([])
const showCategoryId = ref('')
const modeType = ref(1)
const isFirst = ref(true)

// 方法定义
const getFormData = () => {
  if (props.propData) {
    dataList.value = []
    formData.value = props.propData

    if (!formData.value.firstCatList.length) return

    const categoryId = isFirst.value ? formData.value.firstCatList[0].category.id : formData.value.currentFirstCategory.category.id

    formData.value.currentFirstCategory = isFirst.value ? formData.value.firstCatList[0] : formData.value.currentFirstCategory

    showCategoryId.value = categoryId
    modeType.value = 1

    nextTick(() => {
      getAllList()
      isFirst.value = false
    })
  }
}

const getAllList = async () => {
  const param = {
    saleMode: formData.value.saleMode,
  }

  try {
    const res = await getEffectCategoryGoods(showCategoryId.value, param)
    const newDataList: any[] = []

    res.forEach((sd: any) => {
      newDataList.push({
        title: sd.name,
        list: sd.apiAliveProductVos,
      })
    })

    dataList.value = newDataList
  } catch (error) {
    console.error('Failed to get category goods:', error)
  }
}

// 监听props变化
watch(
  () => props.propData,
  () => {
    getFormData()
  },
  { deep: true, immediate: true },
)
</script>

<style lang="scss" scoped>
.left__biggoods-page {
  .i__title {
    height: 66rpx;
    box-sizing: border-box;
    color: #999999;
    background-color: #ffffff;
    padding-top: 40rpx;
  }

  .spellpre__goodsB--icon {
    height: 260rpx;

    .ipic {
      height: 260rpx;
      border-radius: 16rpx;
    }
  }

  .spellpre__goodsL--icon {
    height: 180rpx;
    width: 180rpx;

    .ipic {
      height: 180rpx;
      width: 180rpx;
      border-radius: 16rpx;
    }
  }

  .spellpre__goodsL--info {
    overflow-x: hidden;
  }

  .spellpre__goodsL--name {
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
