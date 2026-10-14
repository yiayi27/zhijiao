<script setup lang="ts">
import { getMemberCardUpgrade, updateRank } from '@/api/customer/member'
import type { IMemberRecord } from '../memberTabs/MemberList.vue'
import type { MemberInfo } from './RenewMember.vue'

// 定义接口
interface RankItem {
  id: string
  rankCode: string | number
  rankName: string
  isOpen: number
  [key: string]: any
}

const emit = defineEmits(['refreshDataList'])

const dialogVisible = ref(false)

// 会员信息
const memberInfo = ref<IMemberRecord>({ memberInnerListVos: [] } as unknown as IMemberRecord)

// 暂存所有会员等级列表
const allRankLists = ref<RankItem[]>([])

// 会员等级列表
const rankLists = ref<RankItem[]>([])

// 选中的会员
const activeMember = ref<MemberInfo>({} as MemberInfo)

// 选中的会员卡号
const activeMemberNumber = ref('')

// 升级后的等级
const upgradeRank = ref('')

// 开卡方式MAP
const radioMap: Record<number, string> = {
  1: '付费购买',
  2: '积分兑换',
  3: '累计消费金额',
  4: '购买指定商品',
}

const selectUpMember = ref<RankItem | null>(null)

function init(info: IMemberRecord): void {
  memberInfo.value = info
  activeMember.value = info.memberInnerListVos[0] || ({} as MemberInfo)
  dialogVisible.value = true
  selectChange(info.memberInnerListVos[0])
}

// 需要升级的会员卡选择
function selectChange(info: MemberInfo): void {
  upgradeRank.value = ''
  selectUpMember.value = null
  getMemberCardUpgrade({ memberId: info.id }).then((res) => {
    const data = res.data
    allRankLists.value = (data.memberCardInfoCenterVos || []).filter((v: RankItem) => v.isOpen === 1)
    rankLists.value = allRankLists.value.filter((v: RankItem) => v.rankCode > info.rankCode)
  })
}

// 升级后会员卡选择
function selectUpChange(info: RankItem): void {
  selectUpMember.value = info
}

watch(
  () => dialogVisible.value,
  () => {
    upgradeRank.value = ''
    selectUpMember.value = null
  },
)

// 升级会员确认
function onOk(): boolean | void {
  if (!activeMemberNumber.value) {
    ElMessage({
      type: 'error',
      message: '请先选择需要升级的会员卡',
    })
    return false
  }
  if (!upgradeRank.value) {
    ElMessage({
      type: 'error',
      message: '请选择需要升级等级',
    })
    return false
  }
  const updateMember = rankLists.value.find((v) => v.rankCode === upgradeRank.value)
  if (!updateMember) return false

  updateRank({
    memberNumber: activeMemberNumber.value,
    rankCode: upgradeRank.value,
    rankName: updateMember.rankName,
  }).then(() => {
    ElMessage({
      type: 'success',
      message: '升级成功',
    })
    dialogVisible.value = false
    emit('refreshDataList', 1)
    upgradeRank.value = ''
    selectUpMember.value = null
  })
}

defineExpose({
  init,
})
</script>

<template>
  <el-dialog v-model="dialogVisible" width="860px" class="dialog" title="升级会员">
    <el-row class="memberRow">
      <el-col :span="1"> &nbsp; </el-col>
      <el-col :span="5"> 会员卡名称 </el-col>
      <el-col :span="4"> 会员卡等级 </el-col>
      <el-col :span="4"> 开卡方式 </el-col>
      <el-col :span="5"> 会员卡号 </el-col>
      <el-col :span="5"> 有效期 </el-col>
    </el-row>
    <el-row v-for="item in memberInfo.memberInnerListVos" :key="item.memberNumber" class="memberRow">
      <el-col :span="1">
        <el-radio v-model="activeMemberNumber" :value="item.memberNumber" @change="selectChange(item)">{{ item.memberNumber }}</el-radio>
      </el-col>
      <el-col :span="5">
        {{ item.title }}
      </el-col>
      <el-col :span="4">
        {{ item.rankName }}
      </el-col>
      <el-col :span="4">
        {{ radioMap[item.openType] }}
      </el-col>
      <el-col :span="5">
        {{ item.memberNumber }}
      </el-col>
      <el-col :span="5">
        {{ item.memberExpireTime }}
      </el-col>
    </el-row>
    <!-- <div class="notes">注：不可升级会员卡为往期会员卡</div> -->

    <!-- <div>
      <div
        :style="
          `background: url(${item.backGroundPicUrl}); background-size: 100% 100%;`
        "
        v-for="item in rankLists"
        :key="item.id"
        @click="activeMember = item"
        :class="[
          activeMember.rankCode === item.rankCode
            ? 'iMember sMember'
            : 'iMember',
        ]"
      >
        <span class="iMember__name">{{ item.rankName }}</span>
        <br />
        <span class="iMember__info">{{ item.introduce }}</span>
        <span v-if="item.openType === 1" class="iMember__price">
          ¥{{ item.openValue }}
        </span>
        <span v-if="item.openType === 2" class="iMember__price">
          {{ item.openValue }}积分
        </span>
        <i class="el-icon-success"></i>
      </div>
      <br clear="all" />
    </div> -->
    <div class="Umember">
      升级等级：<el-radio v-for="item in rankLists" :key="item.id" v-model="upgradeRank" :value="item.rankCode" @change="selectUpChange(item)">
        {{ item.rankName }}
      </el-radio>
      <span v-if="rankLists.length === 0 && activeMemberNumber !== ''" class="Umember__tip"> 当前选中的会员卡已经是最高等级 </span>
    </div>
    <div v-if="selectUpMember && (selectUpMember.openType === 1 || selectUpMember.openType === 2)" class="Umember">
      支付：{{ selectUpMember.openType === 1 ? '¥' : '' }}{{ selectUpMember.actualOpenValue }}{{ selectUpMember.openType === 2 ? '积分' : '' }}
    </div>
    <template #footer>
      <div class="dialog--footer">
        <el-button @click="dialogVisible = false"> 取 消 </el-button>
        <el-button type="primary" @click="onOk"> 确 定 </el-button>
      </div>
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

.notes {
  color: #999;
  text-align: right;
  margin: 20px 0;
}

.iMember {
  width: 240px;
  height: 140px;
  float: left;
  // background: #bbb;
  position: relative;
  cursor: pointer;
  padding: 20px 12px;
  margin: 24px;
  line-height: 30px;

  &__name {
    color: #fff;
    font-size: 24px;
  }

  &__info {
    color: #fff;
    font-size: 14px;
  }

  &__price {
    position: absolute;
    display: block;
    width: 80px;
    border-radius: 40px;
    box-shadow: -2px 2px 2px #888888;
    left: 12px;
    bottom: 20px;
    color: #fff;
    text-align: center;
    background: #aaa;
  }

  > i {
    position: absolute;
    top: 12px;
    right: 12px;
    color: #999;
  }
}

.sMember {
  > i {
    font-size: 24px;
    color: #409eff !important;
  }
}

.Umember {
  margin-top: 20px;

  &__tip {
    color: #aaa;
    font-size: 12px;
  }
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(128, 128, 128, 0.23);
}
</style>
