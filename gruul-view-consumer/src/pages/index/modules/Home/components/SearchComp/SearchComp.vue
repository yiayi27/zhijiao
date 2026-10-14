<template>
  <!-- 搜索组件 -->
  <view class="page">
    <view class="page__box van-search">
      <view class="page__header" :style="style" @tap="toPop">
        <uv-icon name="search" class="page__header__icon" />
        {{ query.name ? query.name : formData.keyWord }}
      </view>
    </view>
    <UPopup v-model:show="show" mode="top" :customStyle="{ height: '100%' }">
      <view style="display: flex">
        <view style="line-height: 100rpx; padding-left: 40rpx">
          <uv-icon name="grid" @tap="close" />
        </view>
        <uv-search
          v-model="query.name"
          :show-action="true"
          actionText="搜索"
          :animation="true"
          :placeholder="formData.keyWord"
          @change="onChangePop"
        />
      </view>
      <uv-cell title="热门推荐" />
      <view>
        <view v-for="(item, index) in formData.hotWord" :key="index" class="page__item">
          <text class="page__item--word" :data-variable="item" @tap="choose">{{ item }}</text>
        </view>
      </view>
      <uv-cell title="历史搜索" />
      <view>
        <view v-for="(item, index) in historyWords" :key="index" class="page__item">
          <text class="page__item--word" :data-variable="item" @tap="choose">{{ item }}</text>
        </view>
      </view>
    </UPopup>
  </view>
</template>

<script setup lang="ts">
import storage from '@/utils/storage'
import { ref, onMounted } from 'vue'

// 定义接口
interface SearchFormData extends Obj {
  keyWord?: string
  hotWord?: string[]
  showStyle?: string
}

interface SearchQuery {
  name: string
}

// 定义props和emit
const props = defineProps<{
  decoretionProperties?: SearchFormData
}>()

// 响应式变量
const style = ref('')
const formData = ref<SearchFormData>({})
const historyWords = ref<string[]>([])
const show = ref(false)
const query = ref<SearchQuery>({
  name: '',
})
const scope = ref(2)

// 方法定义
const getListSytle = (v: string): string => {
  let styleStr = ''
  switch (v) {
    case 'is-style-one':
      styleStr = 'background-color: #eeeeee;border: 1px solid transparent;border-radius: 88rpx;'
      break
    case 'is-style-two':
      styleStr = 'background-color: #eeeeee;border: 1px solid transparent;border-radius: 8rpx;'
      break
    case 'is-style-three':
      styleStr = 'border: 1px solid #eeeeee;border-radius: 88rpx;'
      break
    default:
      break
  }
  return styleStr
}

const getFormData = () => {
  if (props.decoretionProperties) {
    const styleType = props.decoretionProperties.showStyle || ''
    formData.value = props.decoretionProperties
    style.value = getListSytle(styleType)
    scope.value = 2
  }
}

const toPop = () => {
  uni.navigateTo({
    url: `/subcontract/pages/Search/Search?chanel=4&formData=${JSON.stringify(formData.value)}`,
  })
}

const close = () => {
  show.value = false
}

const onChangePop = (e: string) => {
  query.value.name = e
}

const choose = (event: { currentTarget: { dataset: { variable: string } } }) => {
  const val = event.currentTarget.dataset.variable
  query.value.name = val
}

// 生命周期钩子
onMounted(() => {
  getFormData()
  const storedWords = storage.get('historyWords') ?? []
  historyWords.value = storedWords
})

// 初始化
getFormData()
const storedWords = storage.get('historyWords') ?? []
historyWords.value = storedWords
</script>

<style lang="scss" scoped>
.page {
  &__header {
    height: 72rpx;
    display: inline-block;
    display: flex;
    align-items: center;
    font-size: 28rpx;
    width: 100%;

    &__icon {
      font-size: 28rpx;
      margin: 0 20rpx;
    }
  }

  &__box {
    background: #fff;
    height: 92rpx;
    width: 100%;
    padding: 10rpx 40rpx;
  }

  .page__pop--search {
    color: #fff;
    font-weight: 800;
    margin-right: 20rpx;
    background: #fe5468;
    padding: 0 40rpx;
    border-radius: 40rpx;
  }

  .page__item {
    width: 33%;
    padding: 14rpx 20rpx;
    float: left;
    text-align: center;

    &--word {
      display: block;
      width: 80%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      border-radius: 40rpx;
      background: #f5f5f5;
      font-size: 28rpx;
      padding: 20rpx 20rpx;
      text-align: center;
    }
  }
}
</style>
