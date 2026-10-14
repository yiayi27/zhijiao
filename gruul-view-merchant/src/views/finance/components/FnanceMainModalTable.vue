<script setup lang="ts">
const props = defineProps({
  detail: {
    type: Object,
    default: () => ({}),
  },
})

const list = computed(() => [props.detail])

// getAccountType(key) {
//   return {
//     ASS: "团长",
//     AREA_ASS: "区域团长",
//     POINT: "提货点",
//   }[key || "ASS"];
// }

const columns = [
  {
    label: `用户名称`,
    width: 150,
  },
  // {
  //   label: "手机号",
  // },
  {
    label: '提交时间',
  },
  {
    label: '到账时间',
  },
  {
    label: '提现方式',
    width: 120,
  },
  {
    label: '金额',
    width: 80,
  },
  {
    label: '状态',
    width: 100,
  },
]

function getAuditStatusName(key: string) {
  return {
    PENDING_PARTNER: '待审核',
    PENDING_MAIN: '待审核',
    PASSED: '已成功',
    REJECTED: '已拒绝',
  }[key]
}

// /** 中文状态 */
// function getStatusName(key) {
//   return {
//     UNCONFIRMED: '未确认',
//     CONFIRMED: '已确认',
//     REVOKED: '已撤销',
//   }[key]
// }

function getChannel(key: string) {
  return {
    WECAHT: '微信',
    ALIPAY: '支付宝',
    BANK: '银行卡',
    OFFLINE: '线下结算',
  }[key]
}
</script>

<template>
  <m-table :data="list" :custom="true" :columns="columns">
    <template #header="{ row }">
      <div style="width: 100%">
        <span style="margin-right: 50px">提现工单号：{{ row.id }}</span>
        <!-- <span style="float: right;"><i class="iconfont iconPC-beizhu"></i></span> -->
      </div>
    </template>
    <template #custom-body="{ row }">
      <tr class="body--content" style="text-align: center">
        <td>{{ row.name }}</td>
        <!-- <td>{{ row.phone }}</td> -->
        <td>{{ row.createTime }}</td>
        <td>{{ row.arriveTime }}</td>
        <td>{{ getChannel(row.channel) }}</td>
        <td>{{ row.amount }}</td>
        <td>{{ getAuditStatusName(row.auditStatus) }}</td>
      </tr>
    </template>
  </m-table>
</template>

<style lang="scss" scoped></style>
