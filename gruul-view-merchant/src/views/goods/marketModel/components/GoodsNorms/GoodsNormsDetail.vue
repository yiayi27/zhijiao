<script setup lang="ts">
import type { NormType } from './goodNormType'

import { upLoad } from '@/api/index'
import { Plus } from '@element-plus/icons-vue'
import type { SkuStockItem } from '../AddGoodTem/src/GoodForm/src/NewGoodSellInfo.vue'

interface Props {
  list: SkuStockItem[]
  classArr: NormType[]
  memoSpecList: SkuStockItem[]
}

const props = defineProps<Props>()
const emit = defineEmits(['changeNormList'])

const specHeader = ['sku图', '指导价', '实售价', '库存', '限购', '重量']
const productSkuIndex = ref(0)

// 批量设置规格
const batchOriginalPrice = ref(0)
const batchPrice = ref(0)
const batchStock = ref(0)
const batchWeight = ref(0)
const batchPerLimit = ref(0)

const filterHeader = computed(() => {
  return props.classArr
    .map((item) => {
      return item.name
    })
    .concat(specHeader)
})

const getSpecList = computed(() => {
  const memoSpecList = props.memoSpecList
  let list = props.list
  console.log('list', list)
  console.log('memoSpecList', memoSpecList)
  if (memoSpecList.length > 0) {
    list = list.map((item) => {
      const tempArr = memoSpecList.filter((ite) => equar(item.productSpecNames, ite.productSpecNames))
      if (tempArr.length > 0) {
        item = tempArr[0]
      }
      return item
    })
  }
  console.log('======', list)
  return list
})

/**
 * 上传sku
 */
async function uploadSkuImg(file: any) {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']
  const isLt1M = file.size < 2 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return
  }
  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 2MB!')
    return
  }

  const res = await upLoad({
    file: file.raw,
  })
  getSpecList.value[productSkuIndex.value].pic = res.data
}

function getNormList() {
  return getSpecList.value
}

// 批量指导价
function batchSetOriginal(e: Long) {
  if (!checkPriceField(e)) return ElMessage.error('请填写数值')
  const tempArr = getSpecList.value.map((item) => {
    item.originalPrice = e
    return item
  })
  emit('changeNormList', tempArr)
}

// 批量设置实售价
function batchSetPrice(e: Long) {
  if (!checkPriceField(e)) return ElMessage.error('请填写数值')
  const tempArr = getSpecList.value.map((item) => {
    item.price = e
    return item
  })
  emit('changeNormList', tempArr)
}

// 批量设置库存
function batchSetStock(e: Long) {
  if (!checkPriceField(e)) return ElMessage.error('请填写数值')
  const tempArr = getSpecList.value.map((item) => {
    item.stock = Number(e)
    return item
  })
  emit('changeNormList', tempArr)
}

// 批量设置重量
function batchSetWeight(e: Long) {
  if (!checkPriceField(e)) return ElMessage.error('请填写数值')
  const tempArr = getSpecList.value.map((item) => {
    item.weight = e
    return item
  })
  emit('changeNormList', tempArr)
}

// 批量 设置限购
function batchSetPerlimit(e: Long) {
  if (!checkPriceField(e)) return ElMessage.error('请填写数值')
  const tempArr = getSpecList.value.map((item) => {
    item.perLimit = Number(e)
    return item
  })
  emit('changeNormList', tempArr)
}

// 检验输入
function checkPriceField(value: Long) {
  let flag = true
  const priceReg = /(^[1-9]\d*(\.\d{1,2})?$)|(^0(\.\d{1,2})?$)/
  if (!value) {
    flag = false
  }
  if (typeof value === 'string' && !priceReg.test(value)) {
    flag = false
  }
  return flag
}

function equar(a: any, b: any) {
  // 处理非数组类型
  if (!Array.isArray(a) || !Array.isArray(b)) {
    return JSON.stringify(a) === JSON.stringify(b)
  }

  // 判断数组的长度
  if (a.length !== b.length) {
    return false
  } else {
    // 循环遍历数组的值进行比较
    for (let i = 0; i < a.length; i++) {
      if (a[i] !== b[i]) {
        return false
      }
    }
    return true
  }
}

defineExpose({
  getNormList,
})
</script>

<template>
  <div>
    <div class="specDetail">
      <div class="el-form-item__label">规格明细</div>
      <div class="specDetail__table">
        <div class="specDetail__table__title">
          <div
            v-for="(item, index) in filterHeader"
            :key="index"
            :class="[!specHeader.includes(item) ? 'specDetail__table__title--custom' : 'specDetail__table__title--primary']"
          >
            {{ item }}
          </div>
        </div>
        <!-- 多规格 -->
        <div class="specDetail__table__list">
          <div v-for="(item, index) in getSpecList" :key="index" class="specDetail__table__item">
            <div v-if="!Array.isArray(item.productSpecNames)" class="specDetail__table__item--font">
              {{ item.productSpecNames?.name }}
            </div>
            <div v-for="(ite, inde) in item.productSpecNames" v-else :key="inde" class="specDetail__table__item--font">
              {{ ite }}
            </div>
            <div class="specDetail__table__item--upload">
              <el-upload
                class="productImg__uploader product__productImg"
                :auto-upload="false"
                :on-change="uploadSkuImg"
                :show-file-list="false"
                style="width: 50px; height: 50px"
              >
                <el-icon
                  v-if="item.pic === ''"
                  style="width: 50px; height: 50px; line-height: 50px"
                  class="product__productImg--uploader--icon"
                  @click="productSkuIndex = index"
                >
                  <Plus />
                </el-icon>
                <img v-else :src="item.pic" alt="" style="width: 50px; height: 50px" @click="productSkuIndex = index" />
              </el-upload>
            </div>
            <div class="specDetail__table__item--input">
              <el-input v-model="item.originalPrice" />
            </div>
            <div class="specDetail__table__item--input">
              <el-input v-model="item.price" />
            </div>
            <div class="specDetail__table__item--input">
              <el-input-number v-model="item.stock" :step="1" placeholder="0" :min="0" step-strictly :controls="false" :precision="0" />
            </div>
            <div class="specDetail__table__item--input">
              <el-input-number v-model="item.perLimit" :step="1" placeholder="0" :min="0" step-strictly :controls="false" :precision="0" />
            </div>
            <div class="specDetail__table__item--input">
              <el-input v-model="item.weight" />
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 批量设置 -->
    <div class="batch">
      <div class="el-form-item__label">批量设置</div>
      <div class="batch__list">
        <div class="batch__list-item">
          <span>指导价</span>
          <el-input v-model="batchOriginalPrice" @change="batchSetOriginal" />
        </div>
        <div class="batch__list-item">
          <span>实售价</span>
          <el-input v-model="batchPrice" @change="batchSetPrice" />
        </div>
        <div class="batch__list-item">
          <span>库存</span>
          <el-input v-model="batchStock" @change="batchSetStock" />
        </div>
        <div class="batch__list-item">
          <span>限购</span>
          <el-input v-model="batchPerLimit" @change="batchSetPerlimit" />
        </div>
        <div class="batch__list-item">
          <span>重量</span>
          <el-input v-model="batchWeight" @change="batchSetWeight" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
