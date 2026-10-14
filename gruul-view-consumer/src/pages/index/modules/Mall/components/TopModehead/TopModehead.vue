<template>
  <view class="top__mode-page">
    <view
      v-for="(item, index) in HeadList"
      :key="index"
      class="tab__item"
      :style="item.id === activeId ? styleType.style1 : styleType.style2"
      :data-item="item"
      @tap="changeActiveItem"
    >
      {{ item.name }}
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义props和emits
const props = defineProps<{
  propData?: Obj
}>()

const emit = defineEmits(['changeCategoryId'])

// 响应式变量
const formData = ref<Obj>({})
const activeId = ref('-1')
const HeadList = ref<any[]>([])
const styleType = ref({
  style1: {},
  style2: {},
})
const isFirst = ref(true)

// 方法定义
const getHeadList = () => {
  const curId = formData.value.currentFirstCategory?.category ? formData.value.currentFirstCategory.category.id : '-1'
  const activeIdValue = isFirst.value ? '-1' : curId
  const list = formData.value.firstCatList.map((i: any) => {
    return {
      id: i.category.id,
      name: i.category.name,
    }
  })

  activeId.value = activeIdValue
  HeadList.value = [
    {
      id: '-1',
      name: '全部',
    },
    ...list,
  ]

  setTimeout(() => {
    isFirst.value = false
  }, 0)
}

const changeActiveItem = (e: { currentTarget: { dataset: { item: any } } }) => {
  const item = e.currentTarget.dataset.item
  formData.value.firstCatList.forEach((i: any) => {
    i.isSelected = i.category.id === item.id
  })
  activeId.value = item.id
  emit('changeCategoryId', item.id)
}

const getstyleType = () => {
  const { fontColor, fontBg, fontNotColor, fontNotBg } = formData.value
  const style1 = `color: ${fontColor}; background-color: ${fontBg}`
  const style2 = `color: ${fontNotColor}; background-color: ${fontNotBg}`
  styleType.value = {
    style1,
    style2,
  }
}

// 监听propData变化
watch(
  () => props.propData,
  (newValue) => {
    if (newValue) {
      formData.value = newValue
      getHeadList()
      getstyleType()
    }
  },
  {
    deep: true,
    immediate: true,
  },
)
</script>

<style lang="scss" scoped>
.top__mode-page {
  width: 100%;
  height: 100rpx;
  overflow-x: scroll;
  white-space: nowrap;
  box-sizing: border-box;
  padding: 0 10rpx;
  background-color: #fff;

  .tab__item {
    display: inline-block;
    padding: 12rpx 32rpx;
    border-radius: 80rpx;
    font-size: 30rpx;
    color: #333;
    background-color: #fff;
    cursor: pointer;
    user-select: none;
    margin-top: 20rpx;
  }

  .tab__active {
    background-color: rgba(253, 78, 100, 1);
    color: #fff;
  }
}
</style>
