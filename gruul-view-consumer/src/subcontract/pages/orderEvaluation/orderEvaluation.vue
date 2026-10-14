<template>
  <view v-if="detail" class="evaluation">
    <block v-for="(item, index) in detail.orderItemList" :key="index">
      <view class="evaluation__goods">
        <Goods :goodsData="item" />
      </view>
      <view class="evaluation__textarea">
        <uv-textarea
          v-model="form.productEvaluateDtoList[index].comment"
          border="none"
          autoHeight
          height="100"
          placeholder="已经使用一段时间了，有更多宝贝心得？分享给买得他们吧"
        />
      </view>

      <view style="background: #fff">
        <uv-upload
          :fileList="form.productEvaluateDtoList[index].pictures"
          :name="index"
          multiple
          :maxCount="5"
          width="110"
          height="110"
          @afterRead="afterRead"
          @delete="deletePic"
        >
          <view class="evaluation__images">
            <view class="image--add" data-key="pictures">
              <image src="https://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/images/icon-c.png" />
              添加图片
            </view>
          </view>
        </uv-upload>
      </view>
      <view>
        <uv-cell-group :customStyle="{ background: '#fff' }">
          <uv-cell title="综合评分" value="满意请给5分哦" :border="false" />
          <uv-cell title="商品质量">
            <template #title>
              <uv-rate v-model="form.productEvaluateDtoList[index].rate" />
            </template>
          </uv-cell>
        </uv-cell-group>
      </view>
    </block>
    <div class="evaluation__button" @click="submit">提交评论</div>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { evaluate, getOrderDetail as fetchOrderDetail } from '@/api/modules/order'
import { uploadFile } from '@/utils/upload'
import { $Popup } from '@/utils/helper'
import { APP_LAUNCH } from '@/utils/emitNameConfig'

interface UploadFile {
  url?: string
  thumb?: string
  status?: string
  message?: string
  [key: string]: any
}

interface ProductEvaluateDto {
  comment: string
  picture: string
  pictures: UploadFile[]
  originPhotos?: string[]
  productSkuId: string
  rate: number
}

interface OrderItem {
  productSkuId: string
  afs?: {
    status: string
  }
  [key: string]: any
}

interface OrderDetail {
  id: string
  orderItemList: OrderItem[]
  [key: string]: any
}

interface FormData {
  orderId: string
  productEvaluateDtoList: ProductEvaluateDto[]
  shopRate: number
}

interface DeleteEvent {
  name: number
  index: number
}

interface AfterReadEvent {
  file: UploadFile | UploadFile[]
  name: number
}

// 响应式数据
const detail = ref<OrderDetail>({
  id: '',
  orderItemList: [],
})
const form = ref<FormData>({
  orderId: '',
  productEvaluateDtoList: [],
  shopRate: 5,
})
const submitted = ref(false)

// 生命周期
onLoad((options: { id?: string } = {}) => {
  const id = options.id ?? '1324995623470723073'
  getOrderDetail(id)
})

// 方法
const afterRead = async (event: AfterReadEvent) => {
  let lists = Array.isArray(event.file) ? event.file : [event.file]
  let fileListLen = form.value.productEvaluateDtoList[event.name].pictures.length

  lists.forEach((item: UploadFile) => {
    form.value.productEvaluateDtoList[event.name].pictures.push({
      ...item,
      status: 'uploading',
      message: '上传中',
    })
  })

  for (let i = 0; i < lists.length; i++) {
    const result = await uploadFile(lists[i].thumb as string)
    let item = form.value.productEvaluateDtoList[event.name].pictures[fileListLen]
    form.value.productEvaluateDtoList[event.name].pictures.splice(
      fileListLen,
      1,
      Object.assign(item, {
        status: 'success',
        message: '',
        url: result,
      }),
    )
    fileListLen++
  }
}

const deletePic = (event: DeleteEvent) => {
  form.value.productEvaluateDtoList[event.name].pictures.splice(event.index, 1)
}

const getOrderDetail = (id: string) => {
  if (!id) {
    $Popup.toast('订单ID不存在')
    return
  }

  fetchOrderDetail(id)
    .then((detailData: any) => {
      detailData.orderItemList = detailData.orderItemList.filter((item: OrderItem) => {
        return !item.afs || item.afs.status === 'CLOSE'
      })

      detail.value = detailData

      form.value.productEvaluateDtoList = setProductEvaluateDtoList(detailData.orderItemList)
    })
    .catch((err: any) => {
      console.error('获取订单详情失败:', err)
      $Popup.toast(err?.message || '详情获取失败')
    })
}

const setProductEvaluateDtoList = (list: OrderItem[]): ProductEvaluateDto[] => {
  return list.map((item) => {
    return {
      comment: '',
      picture: '',
      pictures: [],
      productSkuId: item.productSkuId,
      rate: 0,
    }
  })
}

const tranfromPics = () => {
  const productEvaluateDtoList = form.value.productEvaluateDtoList.map((item) => {
    item.picture = item.pictures
      .map((ite) => {
        return ite.url || ''
      })
      .join(',')
    return item
  })

  form.value.productEvaluateDtoList = productEvaluateDtoList
}

const verification = (): boolean => {
  const { productEvaluateDtoList } = form.value
  const evLen = productEvaluateDtoList.every((item) => {
    return item.rate >= 1
  })

  if (!evLen) {
    $Popup.toast('商品评分不可为空')
    return false
  }

  return true
}

const submit = () => {
  if (!verification() || submitted.value) return

  // 使用类型断言和属性检查一起确保id存在
  if (!detail.value || typeof detail.value !== 'object' || !('id' in detail.value)) {
    $Popup.toast('订单ID不存在')
    return
  }

  submitted.value = true
  tranfromPics()

  evaluate(
    Object.assign({}, form.value, {
      orderId: (detail.value as OrderDetail).id,
    }),
  )
    .then(() => {
      uni.showToast({
        title: '评价成功',
        icon: 'success',
      })

      setTimeout(() => {
        uni.redirectTo({
          url: '/subcontract/pages/order/order?type=4',
        })
      }, 1500)
    })
    .catch((err: any) => {
      submitted.value = false
      uni.showToast({
        title: err?.message || '评价失败',
        icon: 'none',
      })
    })
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/var.scss';

.evaluation {
  background: $main-background;
  min-height: 100vh;
  padding-bottom: 200rpx;

  @mixin p($v: 20rpx, $l: 20rpx) {
    padding: 20rpx 20rpx;
  }

  &__images {
    @include flex(flex-start);
    @include p;

    width: 100%;
    background: #fff;
    flex-wrap: wrap;

    view:nth-child(4n) {
      margin-right: 0;
    }

    .image--add {
      @include flex;

      flex-direction: column;
      width: 160rpx;
      height: 160rpx;
      border: 1px dashed $main-border-color;
      font-size: 24rpx;
      color: #ccc;
      margin-right: 20rpx;
      margin-bottom: 20rpx;

      image {
        width: 40rpx;
        height: 40rpx;
      }
    }

    .image--item {
      width: 160rpx;
      height: 160rpx;
      margin-right: 20rpx;
      margin-bottom: 20rpx;
      position: relative;

      .del {
        width: 30rpx;
        height: 30rpx;
        // background: rgba($color: #000000, $alpha: .8);
        position: absolute;
        right: -7.1rpx; // 直接使用计算后的固定数值
        top: -7.1rpx;
        border-radius: 50%;
      }

      image {
        width: 100%;
        height: 100%;
      }
    }
  }

  &__textarea {
    min-height: 200rpx;
    background: #fff;

    .van-field__body--textarea {
      min-height: 96rpx;
    }

    .van-field__input {
      padding-top: 20rpx;
      min-height: 240rpx !important;
    }
  }

  &__goods {
    padding: 20rpx 20rpx;
    border-bottom: 1px solid $main-border-color;
    background: #fff;
  }

  &__rate {
    margin-bottom: 20rpx;
    // @include flex();
    position: relative;
    font-size: 30rpx;
    background: white;
  }

  &__button {
    box-sizing: border-box;
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    padding: 20rpx 30rpx;
    text-align: center;
    background: $main-light-color;
    color: #fff;
    z-index: 99;
  }
}
</style>
