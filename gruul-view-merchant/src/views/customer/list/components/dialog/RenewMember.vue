<script setup lang="ts">
import { expireTime } from '@/api/customer/member'

const emit = defineEmits(['refreshDataList'])

// 定义类型接口
export interface MemberInfo {
  id: string
  memberNumber: string
  memberExpireTime: string
  title: string
  rankName: string
  rankCode: string | number
  openType: 1 | 2 | 3 | 4 // 开卡方式: 1-付费购买, 2-积分兑换, 3-累计消费金额, 4-购买指定商品
  memberRegisterTime: string
}

interface MemberData {
  memberInnerListVos: MemberInfo[]
}

const dialogVisible = ref(false)
// 会员详情
const memberInfo = ref<MemberData>({
  memberInnerListVos: [],
})
// 选中的会员卡
const selectMember = ref<Partial<MemberInfo>>({})
const selectRadio = ref('')
// 续费时间
const timeLength = ref<number>(0)

// 开卡方式MAP
const radioMap = {
  1: '付费购买',
  2: '积分兑换',
  3: '累计消费金额',
  4: '购买指定商品',
}

function init(info: MemberData) {
  memberInfo.value = info
  selectRadio.value = ''
  selectMember.value = {}
  dialogVisible.value = true
}

// 选中的会员卡
function memberChange(info: MemberInfo) {
  selectMember.value = info
}

// 续费保存
function onOk() {
  if (!selectMember.value.memberNumber) {
    ElMessage({
      type: 'error',
      message: '请先选择需要续费的会员卡',
    })
    return false
  }
  if (!timeLength.value) {
    ElMessage({
      type: 'error',
      message: '请正确填写续费时长',
    })
    return false
  }
  expireTime({
    memberNumber: selectMember.value.memberNumber,
    memberExpireTime: selectMember.value.memberExpireTime,
    date: timeLength.value,
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '续费成功',
    })
    dialogVisible.value = false
    emit('refreshDataList')
  })
}

defineExpose({
  init,
})
</script>

<template>
  <el-dialog v-model="dialogVisible" width="1050px" class="dialog" title="会员续费">
    <el-row class="memberRow">
      <el-col :span="1"> &nbsp; </el-col>
      <el-col :span="4"> 会员卡名称 </el-col>
      <el-col :span="3"> 会员卡等级 </el-col>
      <el-col :span="3"> 开卡方式 </el-col>
      <el-col :span="5"> 会员卡号 </el-col>
      <el-col :span="4"> 购买时间 </el-col>
      <el-col :span="4"> 有效期 </el-col>
    </el-row>
    <el-row v-for="item in memberInfo.memberInnerListVos" :key="item.memberNumber" class="memberRow">
      <el-col :span="1">
        <el-radio v-model="selectRadio" :value="item.memberNumber" @change="memberChange(item)">{{ item.memberNumber }}</el-radio>
      </el-col>
      <el-col :span="4">
        {{ item.title }}
      </el-col>
      <el-col :span="3">
        {{ item.rankName }}
      </el-col>
      <el-col :span="3">
        {{ radioMap[item.openType] }}
      </el-col>
      <el-col :span="5">
        {{ item.memberNumber }}
      </el-col>
      <el-col :span="4">
        {{ item.memberRegisterTime }}
      </el-col>
      <el-col :span="4">
        {{ item.memberExpireTime }}
      </el-col>
    </el-row>
    <div class="tInput">
      <span>续费时长&nbsp;</span>
      <el-input-number v-model="timeLength" :step="1" :min="0" step-strictly :controls="false" />天
      <br clear="all" />
    </div>
    <template #footer>
      <span class="dialog--footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onOk">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.memberRow {
  padding: 10px;
  border: 1px solid rgba(128, 128, 128, 0.23);
  &:last-child {
    border-top: none;
  }
  :deep(.el-radio__label) {
    display: none;
  }
}
.tInput {
  margin-top: 24px;
  > span {
    line-height: 32px;
    float: left;
  }
  :deep(.el-input-group) {
    width: 200px;
    float: left;
  }
}
:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(128, 128, 128, 0.23);
}
</style>
