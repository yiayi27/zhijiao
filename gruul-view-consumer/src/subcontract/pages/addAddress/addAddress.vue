<template>
  <view>
    <view class="address--add">
      <view class="address--add__item border-bottom">
        <text class="address--cells__title">收货人</text>
        <view class="address--cell">
          <uv-input v-model="addressForm.userName" class="address-input" maxlength="10" placeholder="请输入收货人姓名" />
        </view>
      </view>
      <view class="address--add__item border-bottom">
        <text class="address--cells__title">手机号</text>
        <view class="address--cell">
          <uv-input v-model="addressForm.phone" class="address-input" maxlength="11" placeholder="请输入收货人手机号" />
        </view>
      </view>
      <view class="address--add__item" @click="selectLocation">
        <text class="address--cells__title">所在地区</text>
        <view class="address--cell">
          <text class="location__province location__item">{{ addressForm.province }}</text>
          <text class="location__city location__item">{{ addressForm.city }}</text>
          <text class="location__county location__item">{{ addressForm.county }}</text>
        </view>
        <uv-icon class="address--cells__icon" name="map" />
      </view>
      <view class="address--add__item" style="margin-top: 30rpx">
        <text class="address--cells__title">详细地址</text>
        <view class="address--cell">
          <uv-textarea v-model="addressForm.detailInfo" class="address-textarea" maxlength="48" placeholder="街道、楼牌号等" />
        </view>
      </view>
      <uv-button type="primary" @click="addOrUpdateAddress">保存</uv-button>
    </view>
    <WPicker
      v-model:visible="showSelectLocation"
      mode="region"
      default-type="value"
      :hide-area="false"
      @confirm="onSelectConfirm"
      @cancel="onSelectClose"
    />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import WPicker from '@/components/WPicker/WPicker.vue'
import { addAddress, updateAddress, getLatAndLon } from '@/api/modules/address'
import { isPhone } from '@/utils/helper'

export interface AddressForm {
  id: number
  city: string
  county: string
  detailInfo: string
  location: string
  phone: string
  province: string
  userName: string
  defaultStatus: Long
  postCode: string
}

const addressForm = ref<AddressForm>({
  id: 0,
  city: '',
  county: '',
  detailInfo: '',
  location: '',
  phone: '',
  province: '',
  userName: '',
  defaultStatus: '',
  postCode: '',
})

const showSelectLocation = ref(false)

// uniapp 生命周期
onLoad((options?: any) => {
  if (options?.addressForm) {
    addressForm.value = JSON.parse(options.addressForm)
  }
})

const selectLocation = async () => {
  showSelectLocation.value = true
}

const onSelectClose = () => {
  showSelectLocation.value = false
}

const onSelectConfirm = (picker: any) => {
  const { area, city, province } = picker.obj
  addressForm.value.province = province.label
  addressForm.value.city = city.label
  addressForm.value.county = area.label
  showSelectLocation.value = false
}

const beforeValuate = () => {
  if (!addressForm.value.userName) {
    uni.showToast({
      title: '请输入收货人姓名',
      icon: 'none',
    })
    return false
  }
  if (!(addressForm.value.phone && isPhone(addressForm.value.phone))) {
    uni.showToast({
      title: '手机号不正确',
      icon: 'none',
    })
    return false
  }
  return true
}

const addOrUpdateAddress = async () => {
  if (!beforeValuate()) {
    return
  }
  const province = addressForm.value.province
  const city = addressForm.value.city
  const county = addressForm.value.county
  const provinceStrList = ['北京市', '重庆市', '上海市', '天津市']
  if (provinceStrList.find((p) => addressForm.value.province === p)) {
    addressForm.value.city = '市辖区'
  }
  if (!(province && city && county)) {
    uni.showToast({
      title: '存在地址信息为空',
      icon: 'none',
    })
    return
  }
  if (addressForm.value.id !== 0) {
    /* 更新地址 */
    await getLatAndLonByAddress()
    // 如果不修改直接保存，后端会报重复错误，不进行处理，认为修改成功
    try {
      await updateAddress(JSON.stringify(addressForm.value))
      uni.showToast({
        title: '修改地址成功',
        icon: 'none',
      })
      uni.navigateBack({
        delta: 1,
      })
    } catch (err) {
      // 后端会做重复判断
      if (err === '请勿添加相同地址') {
        uni.showToast({
          title: '修改地址成功',
          icon: 'none',
        })
        uni.navigateBack({
          delta: 1,
        })
      } else {
        uni.showToast({
          title: String(err),
          icon: 'none',
        })
      }
    }
  } else {
    try {
      await getLatAndLonByAddress()
      await addAddress(JSON.stringify(addressForm.value))
      uni.showToast({
        title: '添加地址成功',
        icon: 'none',
      })
      uni.navigateBack({
        delta: 1,
      })
    } catch (err) {
      uni.showToast({
        title: String(err),
        icon: 'none',
      })
    }
  }
}

const getLatAndLonByAddress = async () => {
  const { province, city, county, detailInfo } = addressForm.value
  const site = province + city + county + detailInfo
  const latAndLon = await getLatAndLon({
    site,
  })
  addressForm.value.location = latAndLon.location
  addressForm.value.postCode = latAndLon.andlongitudeVo.adcode
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

page {
  height: 100%;
}

.address--add {
  background-color: #f5f5f5;
  height: 100%;
  .address--add__item {
    position: relative;
    display: flex;
    width: 750rpx;
    font-size: 28rpx;
    line-height: 48rpx;
    color: #333;
    background-color: #fff;
    box-sizing: border-box;
    padding: 20rpx 32rpx;

    .address--cells__title {
      max-width: 180rpx;
      min-width: 180rpx;
    }
    .address--cell {
      width: 500rpx;
    }

    .address--cells__icon {
      position: absolute;
      right: 50rpx;
      top: 25rpx;
    }
  }

  .icon {
    position: absolute;
    top: 0rpx;
    right: 0rpx;
  }
  button {
    padding: 30rpx;
    height: 102rpx;
    border-radius: 45rpx;
    margin: 350rpx 50rpx 0 50rpx;
    background-color: #fc425a;
  }
  .border-bottom {
    border-bottom: 1rpx solid #f8f5f9;
  }
  .placeholder {
    color: #cacaca;
  }
}

.address-textarea {
  height: 150rpx;
  width: 500rpx;
}

.van-picker-column__item {
  color: #888888;
}
</style>
