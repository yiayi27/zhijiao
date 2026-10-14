<script setup lang="ts">
import { ElMessageBox } from 'element-plus'

import SelectGoods from './SelectGoods.vue'

// 定义商品类型接口
interface GoodsItem {
  id: string | number
  pic: string
  name: string
  limitType: number
  minPrice: number
  maxPrice: number
  isCheck?: boolean
  productId?: string | number
}

// Props 定义
const props = defineProps({
  /** 获取活动类型 */
  actType: {
    type: String,
    default: '',
  },
  /** 获取优惠券或满减详情 */
  content: {
    type: Object,
    default: () => ({}),
  },
})

// 组件引用
const selectGoods = ref()

/** 当前单选框内容 */
const goodRadio = ref(0)

const goodsVisible = ref(false)

/** 已选择商品数组 */
const pointGoodsList = ref<GoodsItem[]>([])

/** 参加商品数组 */
const joinList = ref<GoodsItem[]>([])

/** 不参加商品数组 */
const unJoinList = ref<GoodsItem[]>([])

/** 回收返券上传图片 */
const baseImg = ref('')

/**
 * 选择指定条件
 */
function changeList(e: number) {
  pointGoodsList.value = []
  if (e === 1) {
    pointGoodsList.value = joinList.value
  }

  if (e === 2) {
    pointGoodsList.value = unJoinList.value
  }
}

/**
 * 获取优惠券或满减详情
 */
function getContentDetail() {
  const temOption = JSON.parse(JSON.stringify(props.content))
  goodRadio.value = temOption.productType
  if (props.actType === 'AddChainActivity') {
    goodRadio.value = temOption.solitaireActivityVo.productType
  }
  if (goodRadio.value === 2) {
    pointGoodsList.value = unJoinList.value = temOption.productVos
  } else if (goodRadio.value === 1) {
    pointGoodsList.value = joinList.value = temOption.productVos
  } else {
    pointGoodsList.value = temOption.productVos ? temOption.productVos : []
  }
}

/**
 * 取消操作
 */
function cancelOption() {
  ElMessageBox.confirm('确定要退出选择商品页面? 退出后，未保存的信息将不会保留!', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    goodsVisible.value = false
    selectGoods.value.search = {
      ...selectGoods.value.search,
      maxPrice: '',
      minPrice: '',
      name: '',
      current: 1,
      size: 10,
    }
    selectGoods.value.goodsList.map((item: GoodsItem) => {
      return (item.isCheck = false)
    })
  })
}

function sureDig() {
  const list = selectGoods.value.selectpointGoodsList
  if (goodRadio.value === 1) {
    joinList.value = list
    goodsVisible.value = false
    // list.forEach(item => {
    //   if (item.isCheck) {
    //     joinList.value.push(item)
    //   }
    //   item.isCheck = false
    // })
    pointGoodsList.value = joinList.value
  } else if (goodRadio.value === 2) {
    unJoinList.value = list
    goodsVisible.value = false
    // list.forEach(item => {
    //   if (item.isCheck) {
    //     unJoinList.value.push(item)
    //   }
    //   item.isCheck = false
    // })
    pointGoodsList.value = unJoinList.value
  }
}

// 监听 content 属性变化
watch(
  () => props.content,
  () => {
    getContentDetail()
  },
  { deep: true },
)

// 导出组件方法和数据
defineExpose({
  goodRadio,
  pointGoodsList,
  joinList,
  unJoinList,
  baseImg,
})
</script>

<template>
  <div>
    <el-radio-group v-model="goodRadio" @change="(e: any) => changeList(e)">
      <el-radio :value="0"> 全部商品参与 </el-radio>
      <el-radio :value="1"> 指定商品参与 </el-radio>
      <el-radio :value="2"> 指定商品不参与 </el-radio>
      <!-- <el-radio :value="3">回收返券</el-radio> -->
    </el-radio-group>
    <div v-if="goodRadio !== 0 && goodRadio !== 3" class="addCompent">
      <div v-if="pointGoodsList.length > 0" class="tableBox">
        <div class="tableBox__top">
          <div class="tableBox__top--left">商品信息</div>
          <div class="tableBox__top--right">操作</div>
        </div>
        <div v-for="(item, index) in pointGoodsList" :key="index" class="tableBox__content">
          <div class="tableBox__content--left">
            <img :src="item.pic" class="tableBox__content--left--img" />
            <div style="color: #fe5f4b">
              <div style="color: #40b8ff">
                {{ item.name }}
              </div>
              <div v-if="item.limitType === 0">
                <span v-if="item.minPrice > 0">￥{{ item.minPrice }}</span>
                <span v-else>￥{{ item.maxPrice }}</span>
              </div>
              <div v-else>￥{{ item.minPrice }}~￥{{ item.maxPrice }}</div>
            </div>
          </div>
          <div class="tableBox__content--right" style="cursor: pointer" @click="pointGoodsList.splice(index, 1)">删除</div>
        </div>
      </div>
      <div class="tableBox__bottom">
        <el-button type="primary" plain @click="goodsVisible = true"> 选择商品 </el-button>
        <span v-if="pointGoodsList.length > 0" style="color: #9b9b9b">已选择{{ pointGoodsList.length }}款商品</span>
      </div>
      <el-dialog v-model="goodsVisible" width="50%" append-to-body :before-close="cancelOption">
        <SelectGoods
          ref="selectGoods"
          :act-type="actType"
          :point-goods-list="pointGoodsList"
          :goods-visible="goodsVisible"
          @changelist="changeList"
        />
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="cancelOption"> 取 消 </el-button>
            <el-button type="primary" @click="sureDig"> 确 定 </el-button>
          </div>
        </template>
      </el-dialog>
    </div>

    <!-- <div class="couponUpload" v-if="goodRadio === 3">
      <div class="couponUpload__baseUpload">
        <el-upload
          class="couponUpload__baseUpload--inUpload"
          :auto-upload="false"
          :on-change="uploadProductImg"
          action
          :show-file-list="false"
        >
          <img v-if="baseImg" :src="baseImg" />
          <i class="el-icon-plus" v-else></i>
        </el-upload>
        <div style class="position1">发券字段位置</div>
        <div style class="position2">按钮位置</div>
      </div>

      <div class="couponUploadTip">
        <div class="couponUploadTip__tip" style="height: 40px;margin-top:20px">
          <p>上传样式</p>
          <div>建议尺寸：750 * 1350px</div>
        </div>
        <div class="couponUploadTip__tipTwo">设计图片上传时，请将点击按钮尽量对其按钮位置，并在字段位置留空</div>
      </div>
    </div> -->
  </div>
</template>

<style lang="scss" scoped>
.addCompent {
  margin-top: 10px;
}

.tableBox {
  width: 600px;
  border: 1px solid #e7e8eb;
  margin: 30px 0px 10px 0px;
  &__top {
    height: 40px;
    background-color: #f2f2f2;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 30px;
    &--left {
      width: 480px;
      border-right: 1px solid #e7e8eb;
    }

    &--right {
      flex: 0 0 40px;
      margin-left: 5px;
    }
  }
  &__content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0px 30px 0px 10px;
    border-bottom: 1px solid #e7e8eb;
    &--left {
      display: flex;
      align-items: center;
      width: 500px;
      height: 60px;
      border-right: 1px solid #e7e8eb;
      &--img {
        width: 50px;
        height: 50px;
        margin-right: 20px;
      }
    }

    &--right {
      flex: 0 0 40px;
      margin-left: 5px;
    }
  }
  &__bottom {
    width: 600px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
