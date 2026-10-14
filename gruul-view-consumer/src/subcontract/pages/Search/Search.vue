<template>
  <view class="page">
    <uv-search
      v-model="queryName"
      :show-action="true"
      :placeholder="keyWord"
      actionText="搜索"
      :animation="true"
      @custom="onClick"
      @search="onClick"
    />
    <uv-cell v-if="hotWord.length" title="热门推荐" />
    <view v-if="hotWord.length" class="page__content">
      <view v-for="(item, index) in hotWord" :key="index" class="page__item">
        <text class="page__item--word" :data-variable="item" @tap="choose">{{ item }}</text>
      </view>
    </view>
    <uv-cell title="历史搜索">
      <template #right-icon>
        <uv-icon name="close-circle" class="custom-icon" @tap="closeChoose" />
      </template>
    </uv-cell>
    <view class="history__box">
      <view v-for="(item, index) in historyWords" :key="index" class="page__item">
        <text class="page__item--word" :data-variable="item" @tap="choose">{{ item }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import storage from '@/utils/storage'

const historyWords = ref<any[]>([])
const hotWord = ref<any[]>([])
const queryName = ref('')
const chanel = ref(2)
const keyWord = ref('')

onLoad((option) => {
  const historyWordsValue = JSON.parse(storage.get('historyWords')) ?? []
  let hotWordValue = []
  let keyWordValue = ''
  const chanelValue = Number(option?.chanel)
  // 如果来自首页搜索组件，需要带上formate
  if (chanelValue === 4) {
    const params = JSON.parse(option?.formData || '{}')
    hotWordValue = params.hotWord || []
    keyWordValue = params.keyWord
  }
  historyWords.value = historyWordsValue
  hotWord.value = hotWordValue.filter((i: string) => i !== '')
  keyWord.value = keyWordValue
  chanel.value = chanelValue
})

const onClick = () => {
  addStoreage()
  queryName.value = queryName.value === '' ? keyWord.value : queryName.value
  uni.navigateTo({
    url: `/subcontract/pages/spellPage/second-catgoods/second-catgoods?chanel=${chanel.value}&queryName=${queryName.value}`,
  })
}

const choose = (event: any) => {
  const queryNameValue = event.currentTarget.dataset.variable
  queryName.value = queryNameValue
  nextTick(() => {
    onClick()
  })
}

const closeChoose = () => {
  let historyWordsValue = storage.get('historyWords') ?? []
  if (historyWordsValue.length > 0) {
    historyWordsValue = []
    storage.remove('historyWords')
    historyWords.value = historyWordsValue
    uni.showToast({
      title: '删除成功',
      icon: 'none',
      duration: 800,
    })
  }
}

const addStoreage = () => {
  const value = queryName.value
  const historyWordsValue = JSON.parse(storage.get('historyWords') ?? '[]')
  if (historyWordsValue.findIndex((i: string) => i === value) !== -1) {
    historyWordsValue.splice(
      historyWordsValue.findIndex((i: string) => i === value),
      1,
    )
  }
  // 点击热门搜索的关键字也要放入历史搜索记录中
  // && !(this.data.hotWord as any).includes(value)
  if (!historyWordsValue.includes(value) && value) {
    historyWordsValue.unshift(value)
  }
  if (historyWordsValue.length > 6) historyWordsValue.pop()
  historyWords.value = historyWordsValue
  storage.set('historyWords', JSON.stringify(historyWordsValue))
}
</script>

<style lang="scss" scoped>
.page {
  background: #fff;
  height: 100vh;
  .page__pop--search {
    color: #fff;
    font-weight: 800;
    margin-right: 20rpx;
    background: #fe5468;
    padding: 0 40rpx;
    border-radius: 40rpx;
  }
  .page__content {
    display: flex;
    flex-wrap: wrap;
  }
  .page__item {
    padding: 14rpx 20rpx;
    text-align: center;
    &--word {
      display: block;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      border-radius: 50rpx;
      background: #f5f5f5;
      font-size: 28rpx;
      padding: 20rpx;
      text-align: center;
    }
  }
}

.custom-icon {
  color: #bbbbbb;
  font-size: large;
}
.history__box {
  @include flex(flex-start);
  flex-wrap: wrap;
}
</style>
