<script setup lang="ts">
import { batchSetBlackList } from '@/api/customer/customer'
import { ElMessageBox } from 'element-plus'

const emit = defineEmits(['refreshDataList'])

const dialogVisible = ref(false)

const dataForm = reactive({
  option: 0,
  rejectInteger: [],
  userIds: [] as string[],
})

function init(multipleSelection: Obj[], option: number) {
  initData()
  dataForm.option = option
  dataForm.userIds = multipleSelection.map((item) => item.userId)
  if (multipleSelection.length === 1 && multipleSelection[0].blacklistType) {
    dataForm.rejectInteger = multipleSelection[0].blacklistType
  }
  dialogVisible.value = true
}

function initData() {
  dataForm.option = 0
  dataForm.rejectInteger = []
  dataForm.userIds = []
}

async function submitDataForm() {
  if (dataForm.rejectInteger.length === 0) {
    return ElMessage({
      message: '至少选择一个限制行为',
      type: 'warning',
    })
  }
  await batchSetBlackList(dataForm)
  ElMessage({
    message: `已成功${dataForm.option === 1 ? '加入' : '修改'}黑名单`,
    type: 'success',
  })
  emit('refreshDataList', 1)
  dialogVisible.value = false
}

// 关闭弹窗
function closeHandle(done?: Fn) {
  ElMessageBox.confirm('确定退出加入黑名单页面，退出后，未保存的信息将不再保留！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    if (typeof done === 'function') {
      done()
    } else {
      dialogVisible.value = false
    }
  })
}

defineExpose({
  init,
})
</script>

<template>
  <el-dialog v-model="dialogVisible" :before-close="closeHandle" class="dialog" title="黑名单限制行为">
    <div class="container">
      <el-checkbox-group v-model="dataForm.rejectInteger">
        <el-checkbox :value="1"> 下单 </el-checkbox>
        <el-checkbox :value="2"> 评论 </el-checkbox>
      </el-checkbox-group>
    </div>
    <template #footer>
      <span class="dialog--footer">
        <el-button @click="() => closeHandle()">取 消</el-button>
        <el-button type="primary" @click="submitDataForm">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.container {
  padding: 25px;

  .el-button {
    margin: 30px 0px;
  }
}

.dialog--footer {
  text-align: center;
}
</style>
