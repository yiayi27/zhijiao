<template>
  <uv-form labelPosition="left" :model="submitForm" style="padding: 30rpx" labelWidth="160rpx">
    <view class="title">
      <text class="title__text">个人资料</text>
    </view>
    <uv-form-item label="姓名" placeholder="请输入你的姓名" borderBottom>
      <uv-input v-model="submitForm.name" border="none" placeholder="请输入你的姓名" />
    </uv-form-item>
    <uv-form-item label="手机号" placeholder="请输入你的手机号" borderBottom>
      <uv-input v-model="submitForm.mobile" border="none" maxlength="11" placeholder="请输入你的手机号" />
    </uv-form-item>
    <uv-form-item label="地区" borderBottom @click="areaShow = true">
      <uv-input v-model="submitForm.area" border="none" placeholder="请选择地区" />
    </uv-form-item>
    <uv-form-item label="地址" borderBottom>
      <uv-input v-model="submitForm.address" border="none" placeholder="请输入你的地址" />
    </uv-form-item>
    <view class="title">
      <text class="title__text">产品资料</text>
    </view>
    <uv-form-item label="产品信息" borderBottom>
      <uv-textarea v-model="submitForm.productInfo" border="none" placeholder="请输入产品名称及详细信息" />
    </uv-form-item>

    <WPicker v-model:visible="areaShow" mode="region" default-type="value" :hide-area="false" @confirm="changeArea" @cancel="areaShow = false" />

    <uv-button type="primary" style="font-size: 28rpx" @click="formSubmit">立即提交</uv-button>
  </uv-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { supApply } from '@/api/modules/resource'
import WPicker from '@/components/WPicker/WPicker.vue'
import { useMessageStore } from '@/store/modules/message'
import { isPhone } from '@/utils/helper'

// 定义类型
interface AreaItem {
  value: string
  label: string
}

interface AreaEvent {
  result: string
  obj: {
    [key: string]: AreaItem
  }
}

// 响应式数据
const submitForm = ref({
  name: '',
  mobile: '',
  area: '',
  productInfo: '',
  address: '',
})
const haSubscribed = ref(false)
const btnShow = ref(false)
const area = ref<AreaItem[]>([
  {
    value: '',
    label: '请点击选择地区',
  },
  {
    value: '',
    label: '',
  },
  {
    value: '',
    label: '',
  },
])
const areaShow = ref(false)

// 方法
const changeArea = (e: AreaEvent) => {
  submitForm.value.area = e.result
  area.value = Object.values(e.obj)
  areaShow.value = false
}

const formSubmit = async () => {
  // 订阅消息
  const auditMsg = useMessageStore().type.auditMsg
  await useMessageStore().subscribe([auditMsg])

  const deliveryTemplateId = useMessageStore().getTemplateId(auditMsg) || null
  const { name, mobile, productInfo } = submitForm.value

  // 表单验证
  if (!name || !mobile || !productInfo) {
    uni.showToast({
      title: '请完整填写表单',
      icon: 'none',
    })
    return
  }

  if (!isPhone(mobile)) {
    uni.showToast({
      title: '手机号码格式不正确',
      icon: 'none',
    })
    return
  }

  // 准备提交数据
  const areaValue = area.value
  const param = {
    ...submitForm.value,
    area: areaValue[0].label + areaValue[1].label + areaValue[2].label,
    city: areaValue[1].value,
    cityId: areaValue[1].value,
    country: areaValue[2].value,
    countryId: areaValue[2].value,
    province: areaValue[0].value,
    provinceId: areaValue[0].value,
    templateId: deliveryTemplateId,
  }

  // 提交数据
  supApply(param)
    .then(() => {
      uni.showToast({
        title: `申请成功`,
        icon: 'none',
      })
    })
    .catch((err: string) => {
      uni.showToast({
        title: `${err}`,
        icon: 'none',
      })
    })

  // 提交后返回
  setTimeout(() => {
    uni.navigateBack({
      delta: 1,
    })
  }, 1500)
}

// 生命周期
onLoad((options: { btnShow?: string } = {}) => {
  if (options.btnShow) {
    btnShow.value = options.btnShow === 'true'
  }
})
</script>

<style lang="scss" scoped>
.title {
  padding-left: 40rpx;
  height: 70rpx;
  margin-top: 40rpx;
  &__text {
    font-size: 26rpx;
    color: #cacaca;
  }
}
.uv-textarea {
  background-color: transparent;
}
</style>
