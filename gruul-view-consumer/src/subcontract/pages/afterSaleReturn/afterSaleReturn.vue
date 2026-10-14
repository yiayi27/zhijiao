<template>
  <view class="return">
    <view class="return__goods">
      <Goods :goodsData="goods" />
    </view>
    <view class="return__dr">
      <uv-cell-group>
        <uv-cell title="物流公司" :required="true" :value="form.deliveryCompany || '请选择'" isLink @click="toggleSheetVisible" />

        <uv-cell title="物流单号" :required="true">
          <template #value>
            <uv-input v-model="form.deliverySn" placeholder="请输入物流单号" maxlength="24" />
          </template>
        </uv-cell>
      </uv-cell-group>
    </view>
    <view class="return__dr">
      <uv-cell-group>
        <uv-cell title="联系电话" :required="true">
          <template #value>
            <uv-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
          </template>
        </uv-cell>
        <uv-cell title="退货说明" :required="true">
          <template #value>
            <uv-input v-model="form.reason" placeholder="请输入退货说明" />
          </template>
        </uv-cell>
      </uv-cell-group>
    </view>
    <view class="return__submit" @tap="handleReturn">提交</view>
    <uv-action-sheet ref="actionSheetRef" title="选择物流" :actions="actions" @select="onSelect" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { returnOfGoods, getLogisticsCompany } from '@/api/modules/afterSale'
import Goods from '@/components/Goods/Goods.vue'
import { isPhone } from '@/utils/helper'
import { $Popup } from '@/utils/helper'

// 类型定义
interface Form {
  deliveryCompany: string
  deliverySn: string
  deliveryCode: string
  phone: string
  reason: string
}

// 响应式变量
const actionSheetRef = ref<any>(null)
const goods = ref<object>({})
const form = ref<Form>({
  deliveryCompany: '',
  deliverySn: '',
  deliveryCode: '',
  phone: '',
  reason: '',
})
const actions = ref<any[]>([])
const afsId = ref('')

// 方法定义
const handleReturn = () => {
  if (!isPhone(form.value.phone)) {
    return $Popup.toast('请填写正确手机号')
  }
  if (!form.value.deliveryCompany) {
    return $Popup.toast('请选择物流公司')
  }
  if (!form.value.deliverySn) {
    return $Popup.toast('请填写物流单号')
  }
  if (!form.value.reason) {
    return $Popup.toast('请填写退货说明')
  }

  returnOfGoods({ afsId: afsId.value, ...form.value })
    .then(() => {
      uni.navigateBack({ delta: 2 })
      $Popup.toast('退货成功')
    })
    .catch(() => {
      $Popup.toast('退货失败')
    })
}

const onSelect = ({ name, id }: { name: string; id: string }) => {
  form.value.deliveryCompany = name
  form.value.deliveryCode = id
  toggleSheetVisible(false)
}

const toggleSheetVisible = (bool: boolean = true) => {
  if (!bool) {
    actionSheetRef.value.close()
  } else {
    actionSheetRef.value.open()
  }
}

// 生命周期钩子
onLoad((options: any) => {
  if (options) {
    const { goods: goodsData, afsId: afsIdData } = options
    goods.value = JSON.parse(goodsData || '{}')
    afsId.value = afsIdData || ''

    getLogisticsCompany({})
      .then((res) => {
        const list = res.logisticsCompanyVos.map((item: any) => {
          return {
            name: item.name,
            id: item.logisticsCompanyId,
          }
        })
        actions.value = list
      })
      .catch(() => {
        $Popup.toast('获取失败')
      })
  }
})
</script>

<style lang="scss" scoped>
@include b(return) {
  width: 100vw;
  height: 100vh;
  background: #f4f4f4;

  @include e(goods) {
    background: #fff;
    margin-bottom: 20rpx;
    padding: 10rpx;
  }

  @include e(dr) {
    background: #fff;
    margin-bottom: 20rpx;
  }

  @include e(submit) {
    width: 100%;
    padding: 30rpx 20rpx;
    background: $main-light-color;
    position: fixed;
    bottom: 0;
    left: 0;
    text-align: center;
    color: #fff;
    font-size: 32rpx;
  }
}
</style>
