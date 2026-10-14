<script setup lang="ts">
defineOptions({
  name: 'EditActivity',
})

interface FormState {
  date: string
  startTime: string
  middleTime: string
  endTime: string
  resource: string
  newUserOnly: string
}

const form = reactive<FormState>({
  date: '',
  startTime: '',
  middleTime: '',
  endTime: '',
  resource: '手动排序',
  newUserOnly: '否',
})
</script>

<template>
  <div class="spike__edit">
    <el-form ref="form" :model="form" label-width="120px">
      <el-form-item label="选择场次" required>
        <el-row>
          <el-date-picker v-model="form.date" class="date--item" type="date" placeholder="选择日期" />
        </el-row>

        <el-row style="margin-top: 10px">
          <el-col :span="6">
            <el-date-picker v-model="form.date" class="date--item" type="date" placeholder="选择日期" />
          </el-col>
          <el-col :span="6">
            <el-time-select
              v-model="form.startTime"
              class="date--item"
              :picker-options="{
                start: '08:30',
                step: '00:15',
                end: '18:30',
              }"
              placeholder="选择时间"
            />
          </el-col>
          <el-col :span="6">
            <el-time-select
              v-model="form.middleTime"
              class="date--item"
              :picker-options="{
                start: '08:30',
                step: '00:15',
                end: '18:30',
              }"
              placeholder="选择时间"
            />
          </el-col>
          <el-col :span="6">
            <el-time-select
              v-model="form.endTime"
              class="date--item"
              :picker-options="{
                start: '08:30',
                step: '00:15',
                end: '18:30',
              }"
              placeholder="选择时间"
            />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="选择商品" required>
        <el-link type="primary" underline="never"> 添加商品 </el-link>
      </el-form-item>
      <el-form-item label="商品排序" required>
        <el-radio-group v-model="form.resource">
          <el-radio value="手动排序"> 手动排序 </el-radio>
          <el-radio value="自动按销量排序"> 自动按销量排序 </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="设置新人专享" required>
        <el-radio-group v-model="form.newUserOnly">
          <el-radio value="是"> 是 </el-radio>
          <el-radio value="否"> 否 </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/marketing/spike-edit';
</style>
