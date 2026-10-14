<script setup lang="ts">
import BaseTable from '@/components/BaseTable/index.vue'

const props = defineProps({
  fullContent: {
    type: Object,
    default: () => ({}),
  },
})

const fullList = ref([
  {
    ruleType: '',
    discount: '',
    discountAmount: '',
    conditionAmount: '',
    disType: false,
  },
])

/** 可选择优惠券列表 */
const modelList = [
  {
    id: 0,
    fullValue: '满X元减',
  },
  {
    id: 1,
    fullValue: '满X元折',
  },
]

/** 验证状态 */
const validType = ref(false)

// 监听fullContent变化
watch(
  () => props.fullContent,
  () => {
    getFullContent()
  },
  { deep: true },
)

/**
 * 编辑满减获取规则
 */
function getFullContent() {
  if (!props.fullContent.fullScaleRules) return

  validType.value = true
  const rules = [...props.fullContent.fullScaleRules].map((item) => ({
    ...item,
    disType: !!props.fullContent.disType,
  }))
  fullList.value = rules
}

/**
 * 添加优惠券
 */
function addCopon() {
  validType.value = false
  fullList.value.push({
    ruleType: '',
    discount: '',
    discountAmount: '',
    conditionAmount: '',
    disType: false,
  })
}

function selectType(row: any) {
  row.conditionAmount = ''
  row.disType = false
  row.discount = ''
  row.discountAmount = ''
}

/**
 * 输入折扣验证
 */
function vaildInput(e: any) {
  const regs = /^(\d)(\.\d)?$/
  validType.value = regs.test(e.target.value)
  if (!regs.test(e.target.value)) {
    e.target.value = ''
    ElMessage.error('请输入小于10的折扣')
  }
}

/**
 * 输入数字验证
 */
function vaildNumInput(e: any) {
  const regs = /^0*[1-9]\d*$/
  validType.value = regs.test(e.target.value)
  if (!regs.test(e.target.value)) {
    e.target.value = ''
    ElMessage.error('请输入非负整数')
  }
}

function Clicklongin(index: number) {
  fullList.value.splice(index, 1)
}

// 导出变量和方法
defineExpose({
  fullList,
  validType,
  addCopon,
  selectType,
  vaildInput,
  vaildNumInput,
  Clicklongin,
})
</script>

<template>
  <div>
    <el-button link @click="addCopon"> 添加规则 </el-button>
    <div class="addCompent">
      <BaseTable :data="fullList" border>
        <el-table-column label="满减条件" width="180" align="center">
          <template #default="{ row }">
            <el-select v-model="row.ruleType" placeholder="请选择" :disabled="row.disType" @change="selectType(row)">
              <el-option v-for="item in modelList" :key="item.id" :label="item.fullValue" :value="item.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="满减规则" width="300" align="center">
          <template #default="{ row }">
            <div v-if="row.ruleType === 0" class="ruleStyle">
              满
              <el-input-number v-model="row.conditionAmount" :min="0" :controls="false" class="ruleStyle__firIpt" @blur="vaildNumInput($event)" />
              元，减
              <el-input-number v-model="row.discountAmount" :min="0" :controls="false" class="ruleStyle__secIpt" @blur="vaildNumInput($event)" />
              元
            </div>
            <div v-if="row.ruleType === 1" class="ruleStyle">
              满
              <el-input-number v-model="row.conditionAmount" :min="0" :controls="false" class="ruleStyle__firIpt" @blur="vaildNumInput($event)" />
              元，打
              <el-input-number v-model="row.discount" :min="0" :max="10" :controls="false" class="ruleStyle__secIpt" @blur="vaildInput($event)" />
              折
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="{ row, $index }">
            <div :style="{ color: row.disType ? '#E4E4E4' : '#FE4E63' }" class="delBtn" @click="row.disType ? '' : Clicklongin($index)">删除</div>
          </template>
        </el-table-column>
      </BaseTable>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.addCompent {
  margin-top: 10px;
  margin-bottom: 20px;
}

.ruleStyle {
  display: flex;
  justify-content: center;
  align-items: center;
  &__firIpt {
    width: 80px;
    margin: 0px 5px;
  }
  &__secIpt {
    width: 60px;
    margin: 0px 5px;
  }
}

.delBtn {
  cursor: pointer;
}

:deep(.cell) {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
