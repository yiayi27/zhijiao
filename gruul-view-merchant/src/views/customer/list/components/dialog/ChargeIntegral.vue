<script setup lang="ts">
import { changeIntegral } from '@/api/integral/integral'
import { ElMessageBox } from 'element-plus'

interface CheckedItem {
  userId: string | number
  integral: number
  [key: string]: any
}

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  checkedItem: {
    type: Array as () => CheckedItem[],
    default: () => [],
  },
})

const emit = defineEmits(['update:visible', 'refreshDataList'])

// 本地对话框可见状态
const dialogVisible = ref(false)

// 表单数据 - 不在watch函数中重新赋值，避免重置
const formData = reactive({
  integral: 0,
  userIds: [] as (string | number)[],
  isDeduct: false,
})

// 当前积分显示
const currentIntegral = ref('')

// 同步对话框可见状态
watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal

    // 仅在对话框打开时更新用户ID
    if (newVal && props.checkedItem.length > 0) {
      updateUserIds()
    }
  },
)

// 对话框状态同步回父组件
watch(
  () => dialogVisible.value,
  (v) => {
    emit('update:visible', v)
  },
)

// 仅更新用户ID和当前积分，不重置其他表单数据
function updateUserIds() {
  formData.userIds = props.checkedItem.map((item) => item.userId)

  currentIntegral.value = props.checkedItem
    .map((item) => item.integral)
    .reduce((total, val) => total + Number(val), 0)
    .toString()
}

// 提交表单
async function comfirmClickHandle() {
  const integral = formData.integral
  if (!integral || integral <= 0) {
    ElMessage({
      type: 'warning',
      message: '请填写正确的积分数',
    })
    return
  }

  // 处理小数点后两位
  if (typeof integral === 'number' && !Number.isInteger(integral)) {
    formData.integral = Number(integral.toFixed(2))
  }

  const res = await changeIntegral(formData)
  if (res && res.code === 200) {
    ElMessage({
      type: 'success',
      message: formData.isDeduct ? '扣除成功' : '充值成功',
    })
    dialogVisible.value = false
    emit('refreshDataList')
  }
}

// 关闭弹窗
async function closeHandle() {
  const result = await ElMessageBox.confirm('确定退出积分充值页面？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })

  if (result === 'confirm') {
    dialogVisible.value = false
  }
}

// 关闭前确认
async function beforeCloseHandle(done: () => void) {
  const result = await ElMessageBox.confirm('确定退出积分充值页面？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })

  if (result === 'confirm') {
    done()
  }
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :before-close="beforeCloseHandle" class="dialog" width="500px" title="积分充值">
    <el-form :model="formData" label-width="120px">
      <el-form-item label="当前积分">
        <span>{{ currentIntegral }}</span>
      </el-form-item>
      <el-form-item>
        <el-radio v-model="formData.isDeduct" :value="false"> 充值 </el-radio>
        <el-radio v-model="formData.isDeduct" :value="true"> 扣除 </el-radio>
      </el-form-item>
      <el-form-item>
        <el-input-number v-model="formData.integral" :controls="false" type="number" :step="1" :min="0" step-strictly placeholder="请输入积分数">
          点积分
        </el-input-number>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog--footer">
        <el-button @click="closeHandle">取 消</el-button>
        <el-button type="primary" @click="comfirmClickHandle">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.container {
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
      padding-bottom: 30px;
    }
  }
}

.dialog--footer {
  text-align: center;
}
</style>
