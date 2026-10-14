<script setup lang="ts">
// 定义组件名称
defineOptions({
  name: 'MCard',
})

// 定义组件属性
withDefaults(defineProps<CardProps>(), {
  needToggle: false,
  showText: '收起搜索条件',
  hideText: '展开搜索条件',
})

// 定义组件属性的接口
interface CardProps {
  /** 是否需要缩放按钮 */
  needToggle?: boolean
  /** 展开时显示的文字 */
  showText?: string
  /** 收起时显示的文字 */
  hideText?: string
}

// 卡片展开状态
const visible = ref(false)

/** 切换展开状态 */
function toggleVisible() {
  visible.value = !visible.value
}

// 暴露方法给父组件
defineExpose({
  toggleVisible,
})
</script>

<template>
  <div class="m__card" :class="[!needToggle ? 'show' : visible ? 'show' : 'hide']">
    <slot />
    <div v-if="needToggle" class="m__card--btn" @click="toggleVisible">
      <span>
        {{ visible ? showText : hideText }}
        <i :class="`iconfont ${!visible ? 'iconxiajiantou-tianchong' : 'iconxiangshangyuanjiantoushangjiantouxiangshangmianxing'}`" />
      </span>
    </div>
  </div>
</template>

<style lang="scss" src="../styles/m-card.scss"></style>
