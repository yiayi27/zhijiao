<template>
  <view>
    <scroll-view class="left__mode-page" scroll-y="true" :scroll-top="scrollTop" :style="styleType.style2">
      <view
        v-for="(item, index) in HeadList"
        :key="index"
        class="tab__item"
        :style="item.id === activeId ? styleType.style1 : styleType.style2"
        :data-item="item"
        @tap="changeActiveItem"
      >
        <text :style="item.id === activeId ? 'background-color: #FC425A' : styleType.style2" />
        {{ item.name }}
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义props和emit
const props = defineProps<{
  propData?: Obj
}>()

const emit = defineEmits(['changeCategoryId'])

// 响应式变量
const formData = ref<Obj>({})
const activeId = ref('')
const HeadList = ref<any[]>([])
const styleType = ref({
  style1: '',
  style2: '',
})
const scrollTop = ref(0)
const isFirst = ref(true)

// 方法定义
const getFormData = () => {
  if (props.propData) {
    formData.value = props.propData
    getHeadList()
    getstyleType()
  }
}

const getHeadList = () => {
  let activeIdValue = formData.value.currentFirstCategory ? formData.value.currentFirstCategory.category.id : ''

  const headList = formData.value.firstCatList.map((i: any, d: number) => {
    if (d === 0 && isFirst.value) activeIdValue = i.category.id
    return {
      id: i.category.id,
      name: (i.category.name || '').substr(0, 4),
    }
  })

  let scrollTopValue = scrollTop.value
  const index = headList.findIndex((i: any) => i.id === activeIdValue)

  if (index > 10) {
    scrollTopValue = (index - 10) * 50
  } else {
    scrollTopValue = 0
  }

  activeId.value = activeIdValue
  HeadList.value = headList
  scrollTop.value = scrollTopValue
  isFirst.value = false
}

const changeActiveItem = (e: { currentTarget: { dataset: { item: any } } }) => {
  const item = e.currentTarget.dataset.item
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
.left__mode-page {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: rgba(242, 242, 242, 1);

  .tab__item {
    display: block;
    height: 80rpx;
    line-height: 80rpx;
    font-size: 28rpx;
    color: #333;
    background-color: rgba(242, 242, 242, 1);
    cursor: pointer;
    user-select: none;
    box-sizing: border-box;
    padding-left: 20rpx;
    position: relative;
    overflow: hidden;
    box-sizing: border-box;
    padding-right: 12rpx;

    text {
      width: 8rpx;
      height: 30rpx;
      background-color: rgba(242, 242, 242, 1);
      top: 34rpx;
      position: absolute;
      left: 0;
    }
  }

  .tab__active {
    background-color: #fff;

    text {
      background-color: rgba(252, 66, 90, 1);
    }
  }
}
</style>
