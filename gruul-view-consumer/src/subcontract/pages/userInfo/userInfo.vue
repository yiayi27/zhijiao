<template>
  <view>
    <view class="edit">
      <view class="edit__avator">
        <uv-upload previewFullImage :fileList="[]" :maxCount="1" @afterRead="afterRead">
          <uv-image width="170rpx" height="170rpx" :src="userInfo.avatarUrl" shape="circle" />
        </uv-upload>
      </view>
      <view class="edit__row--gray" />
      <uv-cell-group :border="false">
        <uv-cell size="large" title="昵称">
          <template #value>
            <uv-input v-model="userInfo.nikeName" maxlength="16" inputAlign="right" border="none" />
          </template>
        </uv-cell>
        <uv-cell size="large" title="退出登录" @click="logout">
          <template #value>
            <uv-icon name="arrow-right" />
          </template>
        </uv-cell>
      </uv-cell-group>
      <view class="avatar__list--confirm" @click="saveUserInfo">保存</view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { saveUserInfo as saveUserInfoAPI } from '@/api/modules/user'
import { useUserStore } from '@/store/modules/user'
import { useSettingStore } from '@/store/modules/setting'
import { $Popup } from '@/utils/helper'
import { doPostLogout } from '@/api/modules/sign'
import { uploadFile } from '@/utils/upload'

// 定义接口
interface UserInfo {
  avatarUrl: string
  nikeName: string
}

// 响应式数据
const userInfo = ref<UserInfo>({
  avatarUrl: '',
  nikeName: '',
})

// 方法
const afterRead = async (file: any) => {
  const res = await uploadFile(file.file.url)
  userInfo.value.avatarUrl = res as string
}

const saveUserInfo = async () => {
  const { nikeName, avatarUrl } = userInfo.value
  if (!avatarUrl) {
    $Popup.toast('请选择头像')
    return
  }
  await saveUserInfoAPI({
    nickname: nikeName,
    avatar: avatarUrl,
  })
  $Popup.toast('保存成功')
  uni.navigateBack({
    delta: 2,
  })
}

const logout = () => {
  $Popup
    .modal({
      title: '提示',
      content: '确定需要退出？',
    })
    .then(() => {
      doPostLogout()
      useSettingStore().backHome()
    })
}

// 生命周期
onMounted(() => {
  userInfo.value = { ...useUserStore().userInfo }
})
</script>
<style lang="scss" scoped>
page {
  background: #fff;
}
@include b(edit) {
  @include e(avator) {
    width: 100%;
    height: 250rpx;
    @include flex();
    :deep(.uv-upload) {
      flex: unset;
    }
  }
  @include e(info) {
    //   height:calc(100vh - 450rpx);
    @include m(flex) {
      @include flex(space-between);
      font-size: 28rpx;
      height: 120rpx;
    }
  }
  @include e(row) {
    @include m(gray) {
      background: rgba($color: #ccc, $alpha: 0.2);
      height: 20rpx;
      width: 100%;
    }
  }
}
@include b(avatar) {
  @include e(list) {
    box-sizing: border-box;
    width: 100%;
    height: 600rpx;
    padding: 20rpx;
    @include flex(flex-start);
    flex-wrap: wrap;
    @include m(confirm) {
      margin: 20rpx auto;
      width: 500rpx;
      height: 80rpx;
      line-height: 80rpx;
      text-align: center;
      font-size: 26rpx;
      background: #fe4e63;
      color: #fff;
      font-weight: bold;
      border-radius: 40rpx;
    }
  }
}
</style>
