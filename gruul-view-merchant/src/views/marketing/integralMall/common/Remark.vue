<script setup lang="ts">
defineProps({
  selectList: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['remarkSure'])

/** 备注信息 */
const backRemark = ref('')

/** 是否备注 */
const isCoverage = ref(false)

// 获取当前组件实例
const instance = getCurrentInstance()

/**
 * 弹窗确认
 */
function dialogSure() {
  if (backRemark.value === '') {
    ElMessage.error('备注信息不能为空')
    return
  }
  const param = {
    backRemark: backRemark.value,
    isCoverage: isCoverage.value,
  }
  emit('remarkSure', param)
}

/**
 * 弹窗取消
 */
function cancel() {
  // 调用父组件的beforeClose方法
  if (instance && instance.parent && instance.parent.proxy) {
    const parent = instance.parent.proxy as any
    if (parent.beforeClose) {
      parent.beforeClose()
    }
  }
}
</script>

<template>
  <div>
    <div class="">
      您已选择
      <span>{{ selectList.length }}</span>
      笔订单
    </div>
    <el-input v-model="backRemark" style="margin-top: 20px" type="textarea" :autosize="{ minRows: 4, maxRows: 4 }" placeholder="请输入备注" />
    <el-checkbox v-model="isCoverage" style="margin-top: 20px"> 覆盖已有的备注内容 </el-checkbox>
    <div class="digBottom" style="">
      <el-button @click="cancel"> 取 消 </el-button>
      <el-button type="primary" @click="dialogSure"> 确 定 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.digBottom {
  margin-top: 40px;
  display: flex;
  align-content: center;
  justify-content: center;
}
</style>
