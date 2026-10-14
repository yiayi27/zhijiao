<template>
  <view>
    <view class="shopNav">
      <view v-for="(item, index) in navList || []" :key="index">
        <view
          class="shopNav-item"
          :data-index="index"
          :style="'width:' + ((navList && navList.length % 5 === 0) || (navList && navList.length > 8) ? '140.5rpx' : '175.5rpx')"
          @tap.stop="handleTap"
        >
          <image v-if="item.navIcon" class="shopNav-item__image" :src="item.navIcon" :lazy-load="true" />
          <view class="shopNav-item__text" :style="'color:' + item.fontColor">{{ item.navName }}</view>
          <uv-button v-if="item.type === 0 && item.id === 8" open-type="contact" class="contact-style" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { navtofun } from '@/utils/navtofun'
import { getAggregate } from '@/api/modules/userCenter'
import { useUserStore } from '@/store/modules/user'
import { useSettingStore } from '@/store/modules/setting'
import storage from '@/utils/storage'
import { LOGIN_OPEN } from '@/utils/emitNameConfig'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { isLogin } = storeToRefs(userStore)
// 定义导航项接口
interface NavItem {
  navIcon?: string
  navName?: string
  fontColor?: string
  type?: number
  id?: number
  linkUrl?: string
  append?: string
  linkName?: string
}

// 导入ItemLink接口或重新定义
interface ItemLink {
  type: number
  url: string
  append: string
  id: number
  name?: string
}

// 定义props和emit
const props = defineProps<{
  navList?: NavItem[]
}>()

const emit = defineEmits(['AssembleReload'])

// 响应式变量
const getAllUserCenterData = ref<Obj>({})

// 方法定义
const handleTap = async (e: { currentTarget: { dataset: { index: number } } }) => {
  getAllUserCenterData.value = await getAggregate()
  const index = e.currentTarget.dataset.index

  if (!props.navList || !props.navList[index]) return

  const { linkUrl, append, id, type, linkName } = props.navList[index]

  // 确保所有必要属性都有值
  if (type === undefined || !linkUrl || append === undefined || id === undefined) {
    return
  }

  const itemLink: ItemLink = {
    type,
    url: linkUrl,
    append,
    id,
    name: linkName,
  }

  // 积分商城开通
  if (itemLink.url === '/pages/integralShop/integralShop') {
    const shopOpen = getAllUserCenterData.value.shopFunctionInfoVo?.shop
    if (!shopOpen) {
      uni.showToast({
        title: '积分商城暂未开放',
        icon: 'none',
      })
      return
    }
  }

  if (!isLogin.value) {
    uni.$emit(LOGIN_OPEN, true)
    return
  }

  if (itemLink && itemLink.append === 'mall' && itemLink.type === 2) {
    storage.set('goMallItemLink', itemLink)
  }
  navtofun(itemLink)

  const buttomList = useSettingStore().tabBar.list
  const jumpToButtom = buttomList.filter((item) => {
    return item.linkName === itemLink.name
  })

  if (jumpToButtom.length > 0) {
    emit('AssembleReload')
  }
}
</script>

<style lang="scss" scoped>
@include b(shopNav) {
  margin: 20rpx 0;
  background: #fff;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: flex-start;
  padding: 6rpx 20rpx;
}

@include b(shopNav-item) {
  position: relative;
  text-align: center;
  // width: 167.5rpx;
  // height: 187.5rpx;
  width: 140.5rpx;
  height: 185rpx;
  padding-top: 22rpx;

  @include e(image) {
    // margin-top: 4rpx;
    // width: 75%;
    // height: 75%;
    width: 100rpx;
    height: 100rpx;
  }

  @include e(text) {
    margin-top: 8rpx;
    font-size: 24rpx;
    color: #000000;
  }
}

@include b(contact-style) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}
</style>
