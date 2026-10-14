<script setup lang="ts">
import { getMemberCardInfoSwitch, setMemberCardInfoStatus } from '@/api/customer/member'
import MContainer from '@/components/MContainer/MContainer.vue'
import { ElMessageBox } from 'element-plus'

import MemberCard from './components/memberTabs/MemberCard.vue'
import MemberList from './components/memberTabs/MemberList.vue'
import MemberRequity from './components/memberTabs/MemberRequity.vue'
import MemberStore from './components/memberTabs/MemberStore.vue'

defineOptions({
  name: 'MemberList',
})

// Refs
const memberStoreRef = ref()

// 会员功能开关
const memberSwitch = ref(1)

const listKey = ref(Number.parseInt(`${Math.random() * 10000}`))
const cardKey = ref(Number.parseInt(`${Math.random() * 10000}`))
const equityKey = ref(Number.parseInt(`${Math.random() * 10000}`))
const storeKey = ref(Number.parseInt(`${Math.random() * 10000}`))
type TabName = 'list' | 'card' | 'equity' | 'store'
// 选中的tab
const activeTab = ref<TabName>('list')

// tab切换
function handleTabClick(tab: any) {
  activeTab.value = tab.name
  if (tab.name === 'list') {
    listKey.value = Number.parseInt(`${Math.random() * 10000}`)
  } else if (tab.name === 'card') {
    cardKey.value = Number.parseInt(`${Math.random() * 10000}`)
  } else if (tab.name === 'equity') {
    equityKey.value = Number.parseInt(`${Math.random() * 10000}`)
  } else if (tab.name === 'store') {
    storeKey.value = Number.parseInt(`${Math.random() * 10000}`)
  }
}

function handelSwitch(v: any) {
  if (v === 0) {
    ElMessageBox.confirm('关闭会员功能后小程序端会员及其他优惠功能将关闭，不影响之前购买的会员卡使用，确定关闭吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        setMemberCardInfoStatus({ memberCardInfoSwitch: 0, memberModel: 0 })
          .then(memberStoreRef.value.closeSwitch)
          .catch(() => {
            memberSwitch.value = 1
          })
      })
      .catch(() => {
        memberSwitch.value = 1
      })
  } else {
    setMemberCardInfoStatus({
      memberCardInfoSwitch: 1,
      memberModel: 0,
    }).catch(() => {
      memberSwitch.value = 0
    })
  }
}

// Lifecycle
onMounted(async () => {
  const res = await getMemberCardInfoSwitch()
  const memberSwitchObj = (res.data || []).filter((v: Obj) => v.memberModel === 0)[0]
  memberSwitch.value = memberSwitchObj ? memberSwitchObj.state : 0
  if (!res.data) {
    memberStoreRef.value.closeSwitch()
  }
})
</script>

<template>
  <MContainer>
    <template #top>
      <div class="cmember">
        <div class="cmember__tip">
          开启会员功能后，商家可以依据客户设置不同类的会员卡，进行会员卡分类管理，同时会员卡储值功能帮助商家快速回笼资金，充实资金链。
          <br />
          <span>注意：会员功能关闭后，小程序端会员将不再显示。</span>
        </div>
        <div class="cmember__switch">
          <el-switch v-model="memberSwitch" :active-value="1" :inactive-value="0" @change="handelSwitch" />
        </div>
      </div>
    </template>

    <template #tabs>
      <el-tabs v-model="activeTab" class="cmember__tabs" @tab-click="handleTabClick">
        <el-tab-pane label="会员列表" name="list" />
        <el-tab-pane label="会员卡" name="card" />
        <el-tab-pane label="会员权益" name="equity" />
        <el-tab-pane label="储值管理" name="store" />
      </el-tabs>
    </template>

    <template #content>
      <MemberList v-show="activeTab === 'list'" :key="listKey" />
      <MemberCard v-show="activeTab === 'card'" :key="cardKey" />
      <MemberRequity v-show="activeTab === 'equity'" :key="equityKey" />
      <MemberStore v-show="activeTab === 'store'" :key="storeKey" ref="memberStoreRef" :member-switch="memberSwitch" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
.cmember {
  padding: 12px 24px;
  overflow: hidden;
  background: rgb(244, 252, 255);
  > div {
    float: left;
  }

  &__tip {
    width: 80%;
    > span {
      display: inline-block;
      margin-top: 12px;
      color: red;
    }
  }

  &__switch {
    width: 15%;
    margin-left: 5%;
  }
  // &__tabs {
  //   /deep/ .el-tabs__active-bar.is-top {
  //     width: 56px !important;
  //   }
  // }
}

.cmember__tabs {
  :deep(.el-tabs__nav-wrap::after) {
    background-color: #f5f5f5;
  }
}
</style>
