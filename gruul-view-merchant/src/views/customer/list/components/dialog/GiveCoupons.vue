<script setup lang="ts">
import type { GiveCoupon } from '@/views/customer/list/formModel/GiveCoupon'
import type { IGiveCoupon, User } from '../../formModel/CustomerList'
import { giveCoupon } from '@/api/customer/customer'
import CreateCoupons from '@/views/customer/list/components/dialog/CreateCoupons.vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

import Coupon from '../Coupon.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  checkedItem: {
    type: Array as PropType<any[]>,
    default: () => {
      return []
    },
  },
})

const emit = defineEmits(['update:visible', 'refreshDataList'])

const dialogVisible = ref(props.visible)

const innerVisible = ref(false)

const users = ref<User[]>([])

/** 渲染的优惠券列表 */
const couponList = ref<GiveCoupon[]>([])

/** 最终赠送的优惠券 */
const coupons = computed((): IGiveCoupon[] => {
  return couponList.value
    .filter((i) => {
      return i.selected
    })
    .map((j) => {
      // 创建一个临时对象
      const temp = { ...j }
      // 使用as unknown先将类型转为未知，再转为目标类型
      const coupon = { ...temp } as unknown as IGiveCoupon
      return coupon
    })
})

watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal
  },
)

watch(
  () => dialogVisible.value,
  (v) => {
    emit('update:visible', v)
    if (v) {
      users.value = props.checkedItem.map((item: any) => {
        return {
          nikeName: item.nikeName,
          userId: item.userId,
        }
      })
      couponList.value = []
    }
  },
)

// 赠送优惠券
function createCouponClick() {
  innerVisible.value = true
}

function selectCouponHandle(coupon: GiveCoupon) {
  coupon.selected = !coupon.selected
}

function createCouponsHandle(coupons: GiveCoupon[]) {
  couponList.value.push(...coupons)
}

async function confirmClickHandle() {
  if (coupons.value.length > 0) {
    const ids = users.value.map((user) => {
      return user.userId
    })
    const res = await giveCoupon(ids, coupons.value)
    if (res && res.code === 200) {
      ElMessage({
        type: 'success',
        message: '赠送优惠券成功！',
      })
      dialogVisible.value = false
      emit('refreshDataList')
    }
  } else {
    ElMessage({
      type: 'info',
      message: '请选择优惠券',
    })
  }
}

/**
 * 关闭弹窗
 */
async function closeHandle() {
  const result = await confirmHandle()
  if (result === 'confirm') {
    dialogVisible.value = false
  }
}

async function beforeCloseHandle(done: () => void) {
  const result = await confirmHandle()
  if (result === 'confirm') {
    done()
  }
}

/**
 * 关闭确认
 */
async function confirmHandle() {
  const result = await ElMessageBox.confirm('确定退出赠送优惠券页面？退出后，赠送优惠券信息将不保留！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  return result
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :before-close="beforeCloseHandle" class="dialog" width="840" title="赠送优惠券">
    <div class="container">
      <div class="customers">
        已选择<span v-for="(user, idx) in users" :key="idx">【{{ user.nikeName }}】</span>
      </div>
      <el-button type="primary" plain :icon="Plus" @click="createCouponClick"> 新增优惠券 </el-button>
      <div class="coupons">
        <Coupon
          v-for="(coupon, index) in couponList"
          :key="coupon.id ?? index"
          v-model:selected="coupon.selected"
          :setting="coupon"
          @click="selectCouponHandle(coupon)"
        />
      </div>
    </div>
    <CreateCoupons v-model:visible="innerVisible" @create-coupons="createCouponsHandle" />
    <template #footer>
      <div class="dialog--footer">
        <el-button @click="closeHandle"> 取 消 </el-button>
        <el-button type="primary" @click="confirmClickHandle"> 确 定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.container {
  display: block;
  padding: 25px;

  .el-button {
    margin: 30px 0;
  }

  .coupons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    padding: 0 10px;

    .coupon {
      margin-bottom: 30px;
    }
  }
}

.dialog--footer {
  text-align: center;
}
</style>
