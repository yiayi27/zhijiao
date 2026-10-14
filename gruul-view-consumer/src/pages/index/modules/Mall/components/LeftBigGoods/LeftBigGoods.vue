<template>
  <view>
    <view v-if="loading" class="left__biggoods-page">
      <view v-for="(item, index) in dataList" :key="index" class="con__box">
        <view class="i__title">{{ item.title }}</view>
        <LeftSecondGoods v-if="formData" v-model:dataList="item.list" :propData="formData" />
      </view>
    </view>
    <uv-loading-page v-else loading-text="loading..." bg-color="rgba(0,0,0,0.4)" />
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getEffectCategoryGoods } from '@/api/modules/goods'
import LeftSecondGoods from '../LeftSecondGoods/LeftSecondGoods.vue'
import { useSettingStore } from '@/store/modules/setting'

// 定义props和emit
const props = defineProps<{
  propData?: any
}>()

const emit = defineEmits(['getNoList'])

// 响应式变量
const formData = ref<Obj>({})
const dataList = ref<any[]>([])
const showCategoryId = ref('')
const loading = ref(true)
const isFirst = ref(true)

// 方法定义
const setFromData = (newValue: any) => {
  if (newValue) {
    loading.value = false
    uni.showLoading({
      title: '',
      mask: true,
    })

    if (!newValue.firstCatList.length) return
    const categoryId = isFirst.value ? newValue.firstCatList[0].category.id : newValue.currentFirstCategory.category.id
    newValue.currentFirstCategory = isFirst.value ? newValue.firstCatList[0] : newValue.currentFirstCategory
    formData.value = newValue
    showCategoryId.value = categoryId
    getAllList()
  }
}

const getAllList = async () => {
  const dataListValue: { title: string; list: any[] }[] = []
  const param = {
    saleMode: useSettingStore().saleMode,
  }

  const res = await getEffectCategoryGoods(showCategoryId.value, param)
  emit('getNoList', res.length ? 1 : 0)
  if (res.length === 0) {
    loading.value = true
    uni.hideLoading()
    return
  }

  res.forEach((sd: any) => {
    dataListValue.push({
      title: sd.name,
      list: sd.apiAliveProductVos,
    })
  })

  dataList.value = dataListValue
  loading.value = true
  uni.hideLoading()
  isFirst.value = false
}

// 监听propData变化
watch(
  () => props.propData,
  (newValue) => {
    if (newValue) {
      setFromData(newValue)
    }
  },
  {
    immediate: true,
    deep: true,
  },
)
</script>

<style lang="scss" scoped>
.left__biggoods-page {
  .i__title {
    box-sizing: border-box;
    color: #999999;
    background-color: #ffffff;
    padding-top: 40rpx;
    font-size: 24rpx;
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
