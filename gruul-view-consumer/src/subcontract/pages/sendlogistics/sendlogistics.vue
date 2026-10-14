<template>
  <view class="return">
    <view class="return__dr">
      <uv-cell-group>
        <uv-cell title="物流公司" :value="form.deliveryCompany || '请选择'" isLink @click="toggleSheetVisible" />
        <!-- //TODO field组件 -->
        <!-- <uv-field :value="form.deliverySn" @change="setForm" data-key="deliverySn" label="物流单号" placeholder="请输入物流单号"
          maxlength="18" :border="false"></van-field> -->
        <uv-cell title="物流单号">
          <template #value>
            <uv-input v-model="form.deliverySn" placeholder="请输入物流单号" maxlength="18" border="none" inputAlign="right" @change="setForm" />
          </template>
        </uv-cell>
        <uv-cell title="上传凭证" value="" :border="false" />
        <view class="photo_list">
          <view v-for="(item, index) in originPhotos" :key="index" class="item">
            <image :src="item" mode="aspectFill" class="photo" />
            <view class="item_btn" :data-index="index" @click="handleDelPhoto">删除</view>
          </view>
          <view v-if="originPhotos.length < 3" class="item" @click="handleTakePhoto">
            <image
              src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/fd4ad3fe6e3548ab286614cb6591e4b.png"
              mode="aspectFill"
            />
          </view>
        </view>
      </uv-cell-group>
    </view>
    <view class="return__submit" @click="handleReturn">提交</view>
    <uv-action-sheet ref="actionSheetRef" title="选择物流" :actions="actions" @select="onSelect" />
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getLogisticsCompany } from '@/api/modules/afterSale'
import { uploadFile } from '@/utils/upload'
import { sendRecycling } from '@/api/modules/user'
import { isArray } from 'lodash-es'
import { $Popup } from '@/utils/helper'

// 响应式数据
const form = ref({
  deliveryCompany: '',
  deliverySn: '',
  deliveryCode: '',
})
const actions = ref<any[]>([])
const actionSheetRef = ref<any>(null)
const photos = ref<any[]>([])
const originPhotos = ref<any[]>([])
const id = ref('')
const uploading = ref(false)

// 方法

const onSelect = (item: any) => {
  form.value.deliveryCompany = item.name
  form.value.deliveryCode = item.id
  toggleSheetVisible(false)
}

const toggleSheetVisible = (bool: boolean = true) => {
  if (!bool) {
    actionSheetRef.value.close()
  } else {
    actionSheetRef.value.open()
  }
}

const handleReturn = () => {
  if (!form.value.deliveryCompany) {
    $Popup.toast('请选择物流公司')
    return
  }
  if (!form.value.deliverySn) {
    $Popup.toast('请填写物流单号')
    return
  }
  const data = {
    id: id.value,
    shipmentName: form.value.deliveryCompany,
    shipmentNumber: form.value.deliverySn,
    proofImgs: originPhotos.value.join(','),
  }
  sendRecycling(data)
    .then(() => {
      uni.showToast({
        icon: 'none',
        title: '提交成功',
        duration: 1000,
        success: () => {
          uni.navigateBack({
            delta: 2,
          })
        },
      })
    })
    .catch((err) => {
      console.log(err)
      $Popup.toast('提交失败')
    })
}

const setForm = (e: any) => {
  form.value.deliverySn = e.detail
}

const handleTakePhoto = () => {
  if (originPhotos.value.length >= 3) {
    return $Popup.toast('已达到上传上限')
  }
  uni.chooseImage({
    count: 3 - originPhotos.value.length,
    sizeType: ['original', 'compressed'],
    success: (res) => {
      const tempFilePaths = res.tempFilePaths
      if (isArray(tempFilePaths)) {
        for (const i in tempFilePaths) {
          const photosList = photos.value
          photosList.push(tempFilePaths[i])
          uploadPhoto(tempFilePaths[i])
        }
      }
    },
  })
}

const uploadPhoto = (tempFilePaths: any) => {
  uploading.value = true
  nextTick(() =>
    uni.showLoading({
      title: '上传中...',
    }),
  )
  uploadFile(tempFilePaths)
    .then((data: any) => {
      const originPhotosList = originPhotos.value.concat(data)
      originPhotos.value = originPhotosList
      uploading.value = false
      nextTick(() => uni.hideLoading())
    })
    .catch(() => {
      $Popup.toast('上传失败')
    })
}

const handleDelPhoto = (e: any) => {
  const index = e.currentTarget.dataset.index
  const photosList = photos.value
  const originPhotosList = originPhotos.value
  photosList.splice(index, 1)
  originPhotosList.splice(index, 1)
  photos.value = photosList
  originPhotos.value = originPhotosList
}

// 生命周期
onLoad((data: any) => {
  getLogisticsCompany({})
    .then((res: any) => {
      const list = res.logisticsCompanyVos.map((item: any) => {
        return {
          name: item.name,
          id: item.logisticsCompanyId,
        }
      })
      actions.value = list
      if (data && data.id) {
        id.value = data.id
      }
    })
    .catch(() => {
      $Popup.toast('获取失败')
    })
})
</script>

<style lang="scss" scoped>
@include b(return) {
  width: 100vw;
  height: 100vh;
  background: #ffffff;

  @include e(dr) {
    background: #fff;
  }

  @include e(submit) {
    width: 100%;
    height: 98rpx;
    background: #ff5028;
    position: fixed;
    bottom: 0;
    left: 0;
    text-align: center;
    color: #fff;
    font-size: 28rpx;
    text-align: center;
    line-height: 98rpx;
  }
}

.photo_list {
  width: 700rpx;
  margin: 0 auto;
  height: 293rpx;
  display: flex;
  justify-content: flex-start;
  align-items: center;

  .item {
    width: 160rpx;
    height: 233rpx;
    margin: 0 15rpx;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;

    image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 20rpx;
    }

    .item_btn {
      width: 110rpx;
      height: 50rpx;
      border-radius: 10rpx;
      background-color: #eeeeee;
      font-size: 26rpx;
      color: #9e9e9e;
      text-align: center;
      line-height: 50rpx;
      margin-top: 20rpx;
    }
  }
}
</style>
