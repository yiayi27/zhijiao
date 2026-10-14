<script setup lang="ts">
import { changeActivitySetting, getActivitySetting } from '@/api/chainActivity/chainActivity'

import MContainer from '@/components/MContainer/MContainer.vue'

defineOptions({
  name: 'ChainIndex',
})

// 组件引用
const componentRef = ref()

// 组件异步加载映射
const componentMap = {
  Activity: defineAsyncComponent(() => import('./common/Activity/ActivityIndex.vue')),
}

/** 是否开启社群接龙 */
const checkValue = ref(false)

/** 接龙活动相关处理 */
const query = ref({
  id: '',
})

/** 所选择tab */
const activeName = ref<keyof typeof componentMap>('Activity')

onMounted(() => {
  onGetActivitySetting()
})

/**
 * 获取接龙活动开关状态
 */
function onGetActivitySetting() {
  getActivitySetting({}).then((res) => {
    checkValue.value = Boolean(res.data.activityState)
    query.value.id = res.data.id
  })
}

/**
 * 控制开启关闭操作
 */
function changeOpen(e: boolean) {
  const parma = {
    activityState: Number(checkValue.value),
    id: query.value.id,
  }
  changeActivitySetting(parma)
    .then(() => {
      ElMessage.success('设置成功')
      onGetActivitySetting()
    })
    .catch(() => {
      checkValue.value = !e
    })
}
</script>

<template>
  <MContainer>
    <template #top>
      <div class="chainTop">
        <div class="chainTop__inBox">
          <span>开启接龙功能,可以活跃微信群,促进用户下单。</span>
          <div>
            <el-switch v-model="checkValue" active-color="#2D8CF0" inactive-color="#F2F2F2" @change="(e: any) => changeOpen(e)" />
          </div>
        </div>
      </div>
    </template>
    <template #content>
      <component :is="componentMap[activeName]" ref="componentRef" />
    </template>
  </MContainer>
</template>

<style lang="scss" scoped>
.chainTop {
  width: 100%;
  height: 120px;
  background-color: #f4f9fd;
  padding: 15px;

  &__inBox {
    width: 100%;
    height: 100%;
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0px 25px;
    color: #586884;

    &--copyText {
      color: #2d8cf0;
      margin-right: 20px;
      cursor: pointer;
    }
  }
}
</style>
