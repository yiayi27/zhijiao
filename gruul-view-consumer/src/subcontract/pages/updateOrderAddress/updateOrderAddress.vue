<template>
  <view>
    <view class="address--add">
      <view class="address--add__item border-bottom">
        <text class="address--cells__title">收货人</text>
        <view class="address--cell">
          <uv-input
            v-model="addressForm.receiverName"
            class="address-input"
            maxlength="10"
            data-input="addressForm.receiverName"
            placeholder="请输入收货人姓名"
            placeholder-class="placeholder"
          />
        </view>
      </view>
      <view class="address--add__item border-bottom">
        <text class="address--cells__title">手机号</text>
        <view class="address--cell">
          <uv-input
            v-model="addressForm.receiverPhone"
            class="address-input"
            data-input="addressForm.receiverPhone"
            placeholder="请输入收货人手机号"
            placeholder-class="placeholder"
          />
        </view>
      </view>
      <view class="address--add__item" @tap="selectLocation">
        <text class="address--cells__title">所在地区</text>
        <view class="address--cell">
          <text class="location__province location__item">{{ addressForm.receiverProvince }}</text>
          <text class="location__city location__item">{{ addressForm.receiverCity }}</text>
          <text class="location__county location__item">{{ addressForm.receiverRegion }}</text>
        </view>
        <uv-icon class="address--cells__icon" name="map" />
      </view>
      <view class="address--add__item" style="margin-top: 30rpx">
        <text class="address--cells__title">详细地址</text>
        <view class="address--cell">
          <uv-textarea
            v-model="addressForm.receiverDetailAddress"
            class="address-textarea"
            maxlength="48"
            data-input="addressForm.receiverDetailAddress"
            placeholder="街道、楼牌号等"
            placeholder-class="placeholder"
          />
        </view>
      </view>
      <uv-button type="warn" @tap="updateAddress">保存</uv-button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getAddressList, getArea } from '@/api/modules/address'
import { updateAddress as updateAddressAPI } from '@/api/modules/order'
import { $Popup } from '@/utils/helper'

// 定义接口
interface AddressForm {
  orderId: string | null
  receiverCity: string
  receiverDetailAddress: string
  receiverName: string
  receiverPhone: string
  receiverPostCode: string
  receiverProvince: string
  receiverRegion: string
}

interface AreaList {
  province_list: Record<string, string>
  city_list?: Record<string, string>
  county_list?: Record<string, string>
}

// 响应式数据
const addressList = ref<any[]>([])
const addressForm = ref<AddressForm>({
  orderId: null,
  receiverCity: '',
  receiverDetailAddress: '',
  receiverName: '',
  receiverPhone: '',
  receiverPostCode: '',
  receiverProvince: '',
  receiverRegion: '',
})
const areaList = ref<AreaList>({
  province_list: {},
  city_list: {},
  county_list: {},
})
const defaultArea = ref('')
const showSelectLocation = ref(false)

// 方法
const updateAddress = () => {
  updateAddressAPI(addressForm.value)
    .then(() => {
      $Popup.toast('更新成功')
      uni.navigateBack({
        delta: -1,
      })
    })
    .catch((err: any) => {
      $Popup.toast(err)
    })
}

const getDataList = async () => {
  const list = await getAddressList({
    type: 1,
  })
  addressList.value = list
}

const selectLocation = async () => {
  showSelectLocation.value = true
  for (const provinceCode in areaList.value.province_list) {
    if (areaList.value.province_list[provinceCode] === addressForm.value.receiverProvince) {
      const city_list: Record<string, string> = {}
      const county_list: Record<string, string> = {}
      let cityCode = ''
      /* 获取城市数据 */
      const resCity = await getArea({
        id: provinceCode,
        type: 2,
      })

      /* 转换城市数据格式 */
      resCity.forEach((element: any) => {
        city_list[element.value] = element.label
      })
      /* 获取城市代码 */
      resCity.forEach((element: any) => {
        if (element.label === addressForm.value.receiverCity) {
          cityCode = element.value
        }
      })
      /* 获取区数据 */
      const resCounty = await getArea({
        id: cityCode,
        type: 3,
      })

      /* 转换区数据格式 */
      let areaDefaultValue = ''
      resCounty.forEach((element: any) => {
        county_list[element.value] = element.label
        if (element.label === addressForm.value.receiverRegion) {
          areaDefaultValue = element.value
        }
      })
      areaList.value = {
        province_list: areaList.value.province_list,
        city_list,
        county_list,
      }
      defaultArea.value = areaDefaultValue
    }
  }
}

// 生命周期
onLoad(async (options: any = {}) => {
  if (options && options.form) {
    addressForm.value = JSON.parse(options.form)
  }
  getDataList()
  /** 初始化地址选择器数据（初始化省） */
  const area = await getArea({
    id: '',
    type: 1,
  })
  const province_list: Record<string, string> = {}
  area.forEach((element: any) => {
    province_list[element.value] = element.label
  })
  areaList.value = {
    province_list,
    city_list: {},
    county_list: {},
  }
})
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
    width: 100%;
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
