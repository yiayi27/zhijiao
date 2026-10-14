<template>
  <view class="address">
    <view v-if="addressList.length === 0" class="address--backGround">
      <MIcon name="icondizhi" class="icon" size="300rpx" />
      <view>暂时没有地址</view>
    </view>
    <uv-radio-group v-model="chooseAddressName">
      <view class="address--info">
        <view v-for="(item, index) in addressList" :key="item.id" class="address--info__item">
          <view class="location" @click="chooseBack(item)">
            <view class="location--user">
              <text class="location--user__userName">{{ item.userName }}</text>
              <text class="location--user__phone">{{ item.phone }}</text>
            </view>
            <view class="location--info">
              <text v-if="item.isDefault === 1" class="location--info__default">默认</text>
              <text class="location--info__province location--info__text">{{ item.province }}</text>
              <text class="location--info__city location--info__text">{{ item.city }}</text>
              <text class="location--info__county location--info__text">{{ item.county }}</text>
              <text class="location--info__detailInfo location--info__text">{{ item.detailInfo }}</text>
            </view>
          </view>
          <view class="address--item__edit">
            <uv-radio :key="index" shape="circle" activeColor="black" :name="item.id" label="设为默认" @change="onCheckBoxChange(index)">
              <text>设为默认</text>
            </uv-radio>
            <view class="item__edit">
              <view class="edit--item edit" :data-address="item" @click="handleItemClick">
                <MIcon name="iconbianji icon" class="icon" />
                <text>编辑</text>
              </view>
              <view class="edit--item delete" :data-id="item.id" @click="deleteAddress">
                <MIcon name="icondelete" class="icon" />
                <text>删除</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </uv-radio-group>
    <view class="address--buttons">
      <uv-button
        v-if="authLocationStatus === '首次授权' || authLocationStatus === '已授权'"
        class="button--item warn"
        type="error"
        shape="circle"
        @click="addManualLocation"
        ><MIcon name="icongouwuche-jia" class="icon" style="margin-right: 5rpx" />手动添加</uv-button
      >
    </view>
    <Auth></Auth>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getAddressList, addAddress, getArea, deleteAddress as deleteAddressApi, updateAddress, getLatAndLon } from '@/api/modules/address'
import type { AddressForm } from '../addAddress/addAddress.vue'
import MIcon from '@/components/MIcon/MIcon.vue'
import { isPhone } from '@/utils/helper'
import { $Popup } from '@/utils/helper'
import { CHOOSE_ADDRESS } from '@/utils/emitNameConfig'

const addressList = ref<any[]>([])
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
const areaList = ref({
  province_list: {},
  city_list: {},
  county_list: {},
})
const authLocationStatus = ref('首次授权')
const redirect = ref('')
const chooseAddressName = ref(0)

// uniapp 生命周期
onLoad((options?: any) => {
  if (options?.redirect) {
    redirect.value = options.redirect
  }
  getDataList()
  /** 初始化地址选择器数据（初始化省） */
  initAreaData()
})

onShow(() => {
  getDataList()
})

const initAreaData = async () => {
  const area = await getArea({
    id: '',
    type: 1,
  })

  const province_list: Record<string, string> = {}
  area.forEach((element: any) => {
    province_list[element.value] = element.label
  })
  areaList.value.province_list = province_list
}

const chooseBack = (addressOption: AddressForm) => {
  console.log(addressOption)
  if (redirect.value) {
    uni.$emit(CHOOSE_ADDRESS, addressOption)
    uni.navigateBack({
      delta: 1,
    })
  }
}

const getDataList = async () => {
  const addressList_data = await getAddressList({
    type: 1,
  })

  chooseAddressName.value = addressList_data
    .map((item: any) => {
      return item.isDefault && item.id
    })
    .filter(Boolean)[0]
  addressList.value = addressList_data
}

const onCheckBoxChange = async (e: number) => {
  const address = addressList.value[e]
  const addressFormData = addressForm.value
  const addressForEachList = addressList.value
  addressFormData.id = address.id
  addressFormData.defaultStatus = 1
  addressFormData.phone = address.phone
  addressFormData.location = address.location
  addressFormData.city = address.city
  addressFormData.county = address.county
  addressFormData.detailInfo = address.detailInfo
  addressFormData.userName = address.userName
  addressFormData.province = address.province
  addressFormData.postCode = address.postCode
  addressForEachList.forEach((item) => {
    if (item.id === addressFormData.id) {
      item.defaultStatus = 1
    }
  })
  addressForm.value = addressFormData
  addressList.value = addressForEachList
  addOrUpdateAddress('直接修改')
}

const addManualLocation = async () => {
  uni.navigateTo({
    url: '/subcontract/pages/addAddress/addAddress?addressForm=',
  })
}

const addOrUpdateAddress = async (type?: string) => {
  if (!addressForm.value.userName) {
    uni.showToast({
      title: '请输入收货人姓名',
      icon: 'none',
    })

    return
  }
  if (!(addressForm.value.phone && isPhone(addressForm.value.phone))) {
    uni.showToast({
      title: '手机号不正确',
      icon: 'none',
    })

    return
  }
  if (!(addressForm.value.province && addressForm.value.city && addressForm.value.county)) {
    uni.showToast({
      title: '存在空的地址信息',
      icon: 'none',
    })

    return
  }
  if (addressForm.value.id !== 0) {
    // 如果不修改直接保存，后端会报重复错误，不进行处理，认为修改成功
    try {
      /* 更新地址 */
      if (type !== '直接修改') {
        await getLatAndLonByAddress()
      }
      await updateAddress(JSON.stringify(addressForm.value))
      uni.showToast({
        title: '操作成功',
        icon: 'none',
      })

      getDataList()
    } catch (err) {
      uni.showToast({
        title: String(err),
        icon: 'none',
      })
    }
  } else {
    try {
      await getLatAndLonByAddress()
      await addAddress(JSON.stringify(addressForm.value))
      uni.showToast({
        title: '添加地址成功',
        icon: 'none',
      })

      getDataList()
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

const deleteAddress = (e: any) => {
  $Popup
    .modal({
      title: '提示',
      content: '确定删除该地址',
    })
    .then(async () => {
      await deleteAddressApi(e.currentTarget.dataset.id)
      $Popup.toast('删除成功')
      getDataList()
    })
    .catch(() => {
      getDataList()
    })
}

const handleItemClick = (e: any) => {
  const address = e.currentTarget.dataset.address
  addressForm.value.id = address.id
  addressForm.value.defaultStatus = address.isDefault
  addressForm.value.phone = address.phone
  addressForm.value.city = address.city
  addressForm.value.county = address.county
  addressForm.value.detailInfo = address.detailInfo
  addressForm.value.userName = address.userName
  addressForm.value.province = address.province
  addressForm.value.postCode = address.postCode
  uni.navigateTo({
    url: '/subcontract/pages/addAddress/addAddress?addressForm=' + JSON.stringify(addressForm.value),
  })
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

page {
  height: 100%;
}

.address {
  @mixin b-padding {
    padding: 40rpx;
  }

  background: #f5f5f5;
  min-height: 100%;

  .address--backGround {
    position: absolute;
    width: 100%;
    height: 100%;
    @include flex;
    flex-direction: column;
    color: #cacaca;
  }
  .address--info {
    width: 750rpx;
    color: #5c5c5c;
    overflow: scroll;
    height: 100%;
    margin-bottom: 123rpx;

    .address--info__item {
      margin: 0 20rpx 20rpx 20rpx;
      background-color: #fff;
      font-size: 28rpx;

      &:first-child {
        margin-top: 20rpx;
      }

      .address--item__edit {
        @include flex(space-between, center);
        background-color: #fff;
        padding: 0rpx 20rpx 20rpx 20rpx;

        .icon,
        .edit {
          padding-right: 12rpx;
        }

        .edit--item {
          @include flex();
        }

        .item__edit {
          @include flex(space-between, center);
        }
      }
    }
  }

  .address--buttons {
    @include flex;
    position: fixed;
    bottom: env(safe-area-inset-bottom);
    bottom: constant(safe-area-inset-bottom);
    width: 100%;
    padding-top: 30rpx;
    background: #fff;
    padding-bottom: 30rpx;

    .button--item {
      @include flex;
      width: 320rpx;
      height: 64rpx;
      border-radius: 32rpx;
      text-align: center;
    }
  }

  .location {
    color: #333;
    padding: 20rpx;

    .location--user {
      @include flex(space-between, center);
      padding: 15rpx 0rpx;
      font-size: 32rpx;
    }

    .location--info {
      font-size: 24rpx;
      padding: 20rpx 0rpx;
      border-bottom: 1rpx solid #f8f5f9;

      .location--info__default {
        box-sizing: border-box;
        font-size: 22rpx;
        padding: 1rpx 10rpx;
        color: #fff;
        font-weight: 300;
        background-color: red;
        border-radius: 20rpx;
      }

      .location--info__text {
        padding: 0 5rpx;
        color: #909399;
      }
    }
  }
}
</style>
