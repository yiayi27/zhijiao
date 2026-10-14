<script setup lang="ts">
import EditRule from './EditRule.vue'
import RuleContent from './RuleContent.vue'

const comName = ref('RuleContent')
const ruleOption = ref<any>({})
const getList = ref<any>([])

function editRule(newRuleOption: any, newGetList: any) {
  ruleOption.value = newRuleOption
  getList.value = newGetList
}

function onSetComName(val: string) {
  comName.value = val
}
</script>

<template>
  <div>
    <component
      :is="comName === 'RuleContent' ? RuleContent : EditRule"
      :com-name="comName"
      :get-list="getList"
      :rule-option="ruleOption"
      @edit-rule="editRule"
      @set-com-name="onSetComName"
    />
  </div>
</template>

<style lang="scss" scoped></style>
