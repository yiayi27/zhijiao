<script setup lang="ts">
import { getRebateInSet, putRebateInSet } from '@/api/good/goods'

const formRule = ref()

const form = reactive({
  rePrice: '',
  useWayModels: [] as string[],
  reWayModels: [] as string[],
  id: '',
})

const rules = {
  rePrice: [
    {
      required: true,
      message: '请输入商品返利数值',
      trigger: 'blur',
    },
  ],
  useWayModels: [
    {
      required: true,
      message: '至少选择一种使用条件',
      trigger: 'change',
    },
  ],
  reWayModels: [
    {
      required: true,
      message: '至少选择一种返利条件',
      trigger: 'change',
    },
  ],
}

function init() {
  getRebateInSet({}).then((res) => {
    form.id = res.data ? res.data.id : ''
    form.rePrice = res.data ? res.data.rebateRatio : ''
    form.useWayModels = res.data ? res.data.balanceUseCondition.split(',') : ['1']
    form.reWayModels = res.data ? res.data.rebateCondition.split(',') : ['1']
  })
}

/**
 * 保存操作
 */
async function save() {
  await formRule.value.validate()

  const reg = /^\d+$/
  if (!reg.test(form.rePrice) || Number(form.rePrice) < 0 || Number(form.rePrice) > 100) {
    ElMessage.error('请输入0-100的返利比例')
    return
  }
  const param = {
    balanceUseCondition: form.useWayModels.join(','),
    rebateCondition: form.reWayModels.join(','),
    id: form.id,
    rebateRatio: form.rePrice,
  }
  putRebateInSet(param).then(() => {
    ElMessage.success('保存成功')
    init()
  })
}

onMounted(() => {
  init()
})
</script>

<template>
  <div class="rebate__setting--form">
    <div class="rebate__tip">
      <div class="rebate__tip--lump" />
      <span class="rebate__tip--title">返利设置</span>
    </div>
    <div>
      <el-form ref="formRule" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="商品返利" prop="rePrice">
          <div style="display: flex">
            <el-input v-model="form.rePrice" :min="0" :controls="false" placeholder="输入0-100之间的数字" style="width: 220px">
              <template #append>
                <el-button>%</el-button>
              </template>
            </el-input>
            <div class="textTip" style="margin-left: 44px">所有商品统一使用，如需单独配置前往商品页面设置</div>
          </div>
        </el-form-item>
        <el-form-item label="余额使用条件" prop="useWayModels">
          <div style="display: flex">
            <el-checkbox-group v-model="form.useWayModels">
              <el-checkbox value="0" name="useWayModels"> 普通用户 </el-checkbox>
              <el-checkbox value="1" name="useWayModels"> 会员 </el-checkbox>
            </el-checkbox-group>
            <div class="textTip">可在小程序中使用返利余额</div>
          </div>
        </el-form-item>
        <el-form-item label="返利条件" prop="reWayModels">
          <div style="display: flex">
            <el-checkbox-group v-model="form.reWayModels">
              <el-checkbox value="0" name="reWayModels"> 普通用户 </el-checkbox>
              <el-checkbox value="1" name="reWayModels"> 会员 </el-checkbox>
            </el-checkbox-group>
            <div class="textTip">成功购买商品获得返利金额，勾选会员后 会员权益自动增加返利金额</div>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-left: 140px">
      <el-button type="primary" @click="save"> 保存 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.rebate__setting--form {
  font-size: 14px;

  .rebate__tip {
    vertical-align: center;
    background-color: rgba(246, 248, 250, 1);
    padding: 15px 15px 15px 30px;
    margin-bottom: 30px;
    display: flex;

    .rebate__tip--lump {
      display: inline-block;
      width: 3px;
      height: 12px;
      background-color: #ff9900;
    }

    .rebate__tip--title {
      margin-left: 12px;
      color: #586884;
      font-weight: 700;
    }
  }
}

.textTip {
  font-size: 12px;
  color: #aeaeae;
  margin-left: 100px;
}
</style>
