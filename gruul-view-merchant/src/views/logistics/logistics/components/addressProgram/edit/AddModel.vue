<script setup lang="ts">
import address from '@/views/goods/common/city.js'
import { cloneDeep } from 'lodash-es'

// 定义地址项接口
interface AddressItem {
  value: string | number
  label: string
  [key: string]: any
}

// 定义自定义地址接口
interface AddressData {
  name: string
  address: string
  zipCode: string
  phone: string
  isShow: boolean
  cityId: string
  provinceId: string
  area: string
  province: string
  city: string
  country: string
  countryId?: string
  [key: string]: any
}

const props = defineProps({
  addDialog: {
    type: Boolean,
    default: false,
  },
  currentItem: {
    type: Object,
    default: () => ({}),
  },
})

function getDetailData(): AddressData {
  return {
    name: '',
    address: '',
    zipCode: '',
    phone: '',
    isShow: false,
    cityId: '',
    provinceId: '',
    area: '',
    province: '',
    city: '',
    country: '',
  }
}

const addressOption = ref<AddressData>(getDetailData())

const countryId = ref<string>('')

/** 省数组 */
const addressProv = ref<AddressItem[]>([])

/** 市数组 */
const addressCity = ref<AddressItem[]>([])

/** 区数组 */
const addressArea = ref<AddressItem[]>([])

// 初始化省市区数据
onMounted(() => {
  addressProv.value = address.getProvince() as AddressItem[]
})

// Watch for dialog open to initialize data
watch(
  () => props.addDialog,
  (newVal) => {
    if (newVal) {
      getData()
    }
  },
)

watch(
  () => props.currentItem,
  () => {
    getData()
  },
  { deep: true, immediate: true },
)

/**
 * 编辑时获取地址信息
 */
function getData(): void {
  if (props.currentItem && Object.keys(props.currentItem).length > 0) {
    addressOption.value = cloneDeep(props.currentItem as AddressData)
  } else {
    addressOption.value = getDetailData()
  }

  addressProv.value = address.getProvince() as AddressItem[]

  if (addressOption.value.provinceId) {
    addressCity.value = address.getCity(addressOption.value.provinceId) as AddressItem[]
  }

  if (addressOption.value.provinceId && addressOption.value.cityId) {
    addressArea.value = address.getArea(addressOption.value.provinceId, addressOption.value.cityId) as AddressItem[]
  }

  countryId.value = props.currentItem.countryId ? JSON.parse(JSON.stringify(props.currentItem.countryId)) : ''
}

/**
 * 选择省市区
 */
function selectChang(value: string, type: 'prov' | 'city' | 'area'): void {
  if (type === 'prov' && value) {
    addressCity.value = address.getCity(value) as AddressItem[]
    addressArea.value = []
    addressOption.value.cityId = ''
    countryId.value = ''
  }

  if (type === 'city' && value) {
    addressArea.value = address.getArea(addressOption.value.provinceId, value) as AddressItem[]
    countryId.value = ''
  }

  if (type === 'area' && value) {
    countryId.value = JSON.parse(JSON.stringify(value))
  }

  addressOption.value.province = getAddressName(addressProv.value, addressOption.value.provinceId)
  addressOption.value.city = getAddressName(addressCity.value, addressOption.value.cityId)
  addressOption.value.country = getAddressName(addressArea.value, countryId.value)
  addressOption.value.area = addressOption.value.province + addressOption.value.city + addressOption.value.country
}

/**
 * 获取省市区所有数据
 */
function getAddressName(list: AddressItem[], value: string): string {
  let name = ''
  if (list.length > 0) {
    for (const item of list) {
      if (item.value === value) {
        name = item.label
        break
      }
    }
  }
  return name
}

/**
 * Validate form data
 */
function validateForm(): Promise<any> {
  // You can add form validation logic here
  if (!addressOption.value.name) {
    ElMessage.error('联系人不能为空')
    return Promise.reject('联系人不能为空')
  }

  if (!addressOption.value.phone) {
    ElMessage.error('联系电话不能为空')
    return Promise.reject('联系电话不能为空')
  }

  if (!/^1[3-9]\d{9}$/.test(addressOption.value.phone)) {
    ElMessage.error('请输入正确的手机号码')
    return Promise.reject('请输入正确的手机号码')
  }

  if (!addressOption.value.zipCode) {
    ElMessage.error('邮政编号不能为空')
    return Promise.reject('邮政编号不能为空')
  }

  if (!/^\d{6}$/.test(addressOption.value.zipCode)) {
    ElMessage.error('请输入正确的邮政编号')
    return Promise.reject('请输入正确的邮政编号')
  }

  if (!addressOption.value.provinceId || !addressOption.value.cityId || !countryId.value) {
    ElMessage.error('请选择完整的地区')
    return Promise.reject('请选择完整的地区')
  }

  if (!addressOption.value.address) {
    ElMessage.error('详细地址不能为空')
    return Promise.reject('详细地址不能为空')
  }

  // Create a model to expose to parent component
  const shippAddressModel = {
    ...addressOption.value,
    countryId: countryId.value,
  }

  return Promise.resolve(shippAddressModel)
}

// Expose methods and properties to parent component
defineExpose({
  validateForm,
  addressOption,
  countryId,
  shippAddressModel: {
    get() {
      return {
        ...addressOption.value,
        countryId: countryId.value,
      }
    },
  },
})
</script>

<template>
  <div class="diaLog">
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>联系人： </span>
      <el-input v-model="addressOption.name" style="width: 450px" maxlength="10" placeholder="最多输入10个字符" />
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>联系电话： </span>
      <el-input v-model="addressOption.phone" style="width: 450px" maxlength="11" placeholder="请输入手机号" />
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>邮政编号： </span>
      <el-input v-model="addressOption.zipCode" style="width: 450px" maxlength="6" placeholder="请输入邮政编号" />
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>地区选择： </span>
      <div class="diaLog__content--area">
        <el-select v-model="addressOption.provinceId" placeholder="省份选择" style="margin-right: 5px" @change="selectChang($event, 'prov')">
          <el-option v-for="item in addressProv" :key="item.value" :value="item.value" :label="item.label">
            {{ item.label }}
          </el-option>
        </el-select>
        <el-select
          v-model="addressOption.cityId"
          placeholder="城市选择"
          not-found-text="请先选择省份"
          style="margin-right: 5px"
          @change="selectChang($event, 'city')"
        >
          <el-option v-for="item in addressCity" :key="item.value" :value="item.value" :label="item.label">
            {{ item.label }}
          </el-option>
        </el-select>
        <el-select v-model="countryId" placeholder="区县选择" not-found-text="请先选择城市" @change="selectChang($event, 'area')">
          <el-option v-for="item in addressArea" :key="item.value" :value="item.value" :label="item.label">
            {{ item.label }}
          </el-option>
        </el-select>
      </div>
    </div>
    <div class="diaLog__content">
      <span class="diaLog__content--left"> <span class="star">*</span>详细地址： </span>
      <el-input v-model="addressOption.address" type="textarea" :rows="3" style="width: 450px" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.star {
  color: #f56c84;
  margin-right: 3px;
}

.diaLog {
  &__content {
    margin-bottom: 20px;
    display: flex;
    align-items: center;

    &--left {
      width: 80px;
      display: flex;
      justify-content: flex-end;
      margin-right: 10px;
    }

    &--area {
      display: flex;
      flex: 1;
      justify-content: space-between;
    }
  }

  &__agree {
    width: 100%;
    display: flex;
    justify-content: center;

    &--open {
      color: #1e7fff;
      cursor: pointer;
    }
  }
}
</style>
