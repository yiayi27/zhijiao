<script lang="ts" setup>
defineProps({
  color: {
    type: String,
    default: '#ffffff',
  },
  bgcolor: {
    type: String,
    default: 'transparent',
  },
})

const now = computed(() => {
  const date = new Date()
  const hour = date.getHours() > 9 ? date.getHours() : `0${date.getHours()}`
  const minutes = date.getMinutes() > 9 ? date.getMinutes() : `0${date.getMinutes()}`
  return `${hour}:${minutes}`
})
</script>

<template>
  <div class="topBar" :style="{ backgroundColor: bgcolor, color }">
    <div class="topBar__one">
      <span>
        <i class="iconfont iconxinhao" />
        <i class="iconfont iconxinhao3-copy-copy-copy" />
        <i class="iconfont icondianliang" />
      </span>
      <span>{{ now }}</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
/* 手机状态栏 */
.topBar {
  width: 100%;
  padding: 5px 10px 5px;

  &__one {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-bottom: 10px;

    span,
    i.iconfont {
      padding: 0 2px;
      font-size: 14px;
    }
  }
}
</style>
