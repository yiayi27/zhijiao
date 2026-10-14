<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'

interface TimeSlot {
  time: string
}

interface FormState {
  name: string
  date?: string
  times: TimeSlot[]
}

const form = reactive<FormState>({
  name: '',
  date: '',
  times: [{ time: '' }, { time: '' }],
})
</script>

<template>
  <div class="spike__setting">
    <el-form ref="form" :model="form" label-width="80px">
      <el-form-item label="活动名称" required>
        <el-upload class="setting--uploader" action="" :show-file-list="false">
          <el-icon class="avatar-uploader-icon">
            <Plus />
          </el-icon>
          上传封面
        </el-upload>
        <span class="tips"> (请上传大小750×200px的图片，大小不超过180kb，图片格式支持 png、jpg、jpeg，gif。) </span>
      </el-form-item>

      <el-form-item label="活动名称" required>
        <el-input v-model="form.name" class="form--item" />
        <span class="tips">(活动名称不超过5个字) </span>
      </el-form-item>
      <el-form-item label="预热时间">
        <el-date-picker
          v-model="form.date"
          class="form--item"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
        <span class="tips"> (从活动开始时间返推预热开始时间，预热期间商品不能购买) </span>
      </el-form-item>
      <el-form-item label="场次设置" required>
        <div v-for="(item, i) of form.times" :key="i" style="margin-bottom: 10px">
          <el-input v-model="form.times[i].time" style="width: 240px" />
          <template v-if="i === 0">
            <el-button class="form--button"> 添加 </el-button>
          </template>
          <template v-else>
            <el-button class="form--button del"> 删除 </el-button>
          </template>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>
