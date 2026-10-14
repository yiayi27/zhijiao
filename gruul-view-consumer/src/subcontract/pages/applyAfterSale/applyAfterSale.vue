<template>
  <view>
    <view class="apply--after">
      <view class="apply--after__goods">
        <Goods :goodsData="goods" />
      </view>
      <view class="apply--after__form">
        <block>
          <block v-if="form.type === 'EXCHANGE'">
            <uv-radio-group v-model="form.reason">
              <uv-cell-group :border="false">
                <uv-cell title="未收到货" :border="false" clickable data-name="未收到货" @click="onClick">
                  <uv-radio activeColor="#fe4e63" name="未收到货" />
                </uv-cell>
                <uv-cell title="货物损坏" :border="false" clickable data-name="货物损坏" @click="onClick">
                  <uv-radio activeColor="#fe4e63" name="货物损坏" />
                </uv-cell>
              </uv-cell-group>
            </uv-radio-group>
            <!-- 用户换货 -->
          </block>
          <!-- 用户退款 -->
          <block v-else-if="form.type === 'REFUND'">
            <view v-if="hasPicked" class="form--refund">
              退款金额：
              <text class="form--refund__amount">￥{{ form.refundAmount }}</text>
            </view>
            <view v-else class="form--refund border">
              <!-- // TODO 完善 field组件 -->
              <view class="max--refund">最多￥{{ maxRefund }}</view>
            </view>
          </block>
          <!-- 用户退货退款 -->
          <block v-else>
            <view class="form--refund border">
              <view class="form__flex">
                <text>退款金额</text>
                <uv-input v-model="form.refundAmount" border="none" placeholder="请输入退款金额" @change="onrefundAmountChange" />
              </view>
              <view class="max--refund">最多￥{{ maxRefund }}</view>
            </view>
            <!-- // TODO 完善field组件 -->
          </block>
        </block>
        <view class="apply--after__textarea">
          <!-- 说明
          （{{200 - form.description.length>0?200 - form.description.length:0}}个字符） -->
          <uv-textarea v-model="form.description" placeholder="请输入内容" count maxlength="200" border="none" />
        </view>
        <view class="apply--after__photo">
          拍照上传凭证（最多5张）
          <view class="photo--list">
            <view class="photo--item take--photo" @tap="handleTakePhoto">
              <image src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/camera-icon.png" mode="aspectFill" />
              <view>上传凭证</view>
              <view>(最多{{ 5 - photos.length }}张)</view>
            </view>
            <view v-for="(item, index) in photos" :key="index" class="photo--item">
              <image :src="item" mode="aspectFill" class="photo" />
              <view class="photo--item__del" :data-index="index" @tap="handleDelPhoto">
                <image src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/close.png" mode="aspectFill" />
              </view>
            </view>
          </view>
        </view>
      </view>
      <view class="apply--after__submit" @tap="handleApplyAfterSale">提交</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { uploadFile } from '@/utils/upload'
import { applyAfterSale } from '@/api/modules/afterSale'
import Goods from '@/components/Goods/Goods.vue'
import { isArray } from 'lodash-es'
import { useMessageStore } from '@/store/modules/message'
import { $Popup } from '@/utils/helper'

// 响应式状态
const photos = ref<any[]>([])
const originPhotos = ref<any[]>([])
const goods = ref<Obj>({})
const form = ref<any>({
  reason: '未收到货',
  description: '111',
})
const maxRefund = ref(0)
const hasPicked = ref(false)
const haSubscribed = ref(false)
const uploading = ref(false)
const orderStatus = ref<any>(null)

// 方法定义
const verification = () => {
  if (!form.value.description) {
    $Popup.toast('原因不可为空')
    return false
  }
  return true
}

const setForm = (key: string, value: any) => {
  form.value = Object.assign(form.value, {
    [key]: value,
  })
}

const onClick = (event: any) => {
  const name = event.currentTarget.dataset.name
  setForm('reason', name)
}

const onrefundAmountChange = (event: any) => {
  setForm('refundAmount', event.detail)
}

const handleDelPhoto = (event: any) => {
  const index = event.currentTarget.dataset.index
  const photosCopy = [...photos.value]
  const originPhotosCopy = [...originPhotos.value]
  photosCopy.splice(index, 1)
  originPhotosCopy.splice(index, 1)
  photos.value = photosCopy
  originPhotos.value = originPhotosCopy
}

const uploadPhoto = (tempFilePath: string) => {
  uploading.value = true
  uni.showLoading({
    title: '上传中...',
  })
  uploadFile(tempFilePath)
    .then((data) => {
      photos.value = originPhotos.value.concat(data)
      originPhotos.value = originPhotos.value.concat(data)
      uploading.value = false
      nextTick(() => uni.hideLoading())
    })
    .catch((err) => {
      console.log(err)
      $Popup.toast('上传失败')
    })
}

const handleTakePhoto = () => {
  if (photos.value.length >= 5) {
    return $Popup.toast('已达到上传上限')
  }
  uni.chooseImage({
    count: 5,
    sizeType: ['original', 'compressed'],
    sourceType: ['camera', 'album'],
    success: async ({ tempFilePaths }) => {
      if (isArray(tempFilePaths)) {
        tempFilePaths.forEach((url) => {
          uploadPhoto(url)
        })
      }
    },
  })
}

const handleApplyAfterSale = async () => {
  if (!verification()) return
  if (uploading.value) return $Popup.toast('图片上传中')

  const userType = 'user'
  const messageStore = useMessageStore()
  const orderRefundNotify = messageStore.type?.orderRefundNotify
  const orderRefund = messageStore.type?.orderRefund

  if (!haSubscribed.value && orderRefundNotify && orderRefund) {
    /** 模板订阅 */
    await messageStore.subscribe([orderRefund, orderRefundNotify]).then(() => {
      haSubscribed.value = true
    })
  }

  applyAfterSale({
    ...form.value,
    images: originPhotos.value.join(','),
    templateId: (await messageStore.getTemplateId(orderRefundNotify)) || null,
    refundTemplateld: (await messageStore.getTemplateId(orderRefund)) || null,
  })
    .then((res) => {
      uni.redirectTo({
        url: `/subcontract/pages/afterSaleDetail/afterSaleDetail?afsid=${res}&userType=${userType}&goods=${encodeURIComponent(JSON.stringify(goods.value))}`,
      })
    })
    .catch((err) => {
      $Popup.toast(err)
    })
}

// 生命周期钩子
onLoad((query: any) => {
  if (!query) return

  const { title, goods: goodsData, orders, hasPicked: hasPickedData, orderStatus: orderStatusData, ...formData } = query

  uni.setNavigationBarTitle({
    title: `申请${title}`,
  })

  if (orders) {
    formData.orders = JSON.parse(orders)
  }

  goods.value = JSON.parse(decodeURIComponent(goodsData))
  form.value = formData
  maxRefund.value = formData.refundAmount
  hasPicked.value = !!hasPickedData
  orderStatus.value = orderStatusData
})
</script>
<style lang="scss" scoped>
page {
  background: #f4f4f4;
}

.apply--after {
  width: 100%;
  height: auto;
  padding: 13rpx;
  box-sizing: border-box;
}

.apply--after__goods {
  padding: 20rpx 17rpx;
  background: #fff;
  box-sizing: border-box;
  border-radius: 10rpx;
  height: auto;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.apply--after__form {
  margin-top: 15rpx;
  padding: 20rpx 17rpx;
  background: #fff;
  box-sizing: border-box;
  border-radius: 10rpx;

  .van-radio {
    justify-content: flex-end;
  }
}

.apply--after__textarea,
.apply--after__photo {
  margin-top: 15rpx;
  padding: 20rpx 30rpx;
  font-size: 28rpx;
}

.van-field .van-cell {
  padding-left: 0 !important;
  padding-right: 0 !important;
}

.van-len {
  margin-top: 20rpx;
}

.photo--list {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  margin-top: 48rpx;
}

.photo--item {
  width: 177rpx;
  height: 177rpx;
  margin-right: 24rpx;
  margin-top: 24rpx;
  border: 1px dotted #979797;
  background-size: contain;
  background-repeat: no-repeat;
  position: relative;
}

.photo--item .photo {
  width: 100%;
  height: 100%;
}

.photo--item__del {
  width: 44rpx;
  height: 44rpx;
  /* border-radius: 50%; */
  /* background: rgba(0, 0, 0, 0.8); */
  position: absolute;
  top: -15rpx;
  right: -15rpx;
}

.photo--item__del image {
  width: 44rpx !important;
  height: 44rpx !important;
}

.apply--after__submit {
  width: 100%;
  height: 98rpx;
  background: #fe4e63;
  text-align: center;
  color: #fff;
  line-height: 98rpx;
  margin-top: 15rpx;
  position: fixed;
  bottom: 0;
  left: 0;
}

.form--refund {
  box-sizing: border-box;
  width: 100%;
  background: rgb(235, 235, 235);
  height: 84rpx;
  border-radius: 42.5rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  @include flex(space-between);
}

.form--refund__amount {
  color: #fb95a1;
}

.form--refund__input .van-cell {
  background: none;
}

.take--photo {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-size: 20rpx;
  color: #868686;
}

.take--photo image {
  width: 48rpx;
  height: 45rpx;
}

.form--refund__input {
  background: none;
  color: #fe4e63;
  height: 84rpx;
}

.border {
  background: none !important;
  border: 1px solid #d3d3d3;
  position: relative;
  overflow: hidden;
  padding: 20rpx 30rpx;
}

.max--refund {
  position: absolute;
  right: 20rpx;
  top: 20rpx;
  color: #c7c7c7;
}
.form__flex {
  @include flex();
  font-size: 28rpx;
}
</style>
