<script setup lang="ts">
defineOptions({
  name: 'MTableColumn',
})

defineProps({
  prop: {
    type: String,
    default: '',
  },
  row: {
    type: Object as PropType<any>,
    default: () => ({}),
  },
  align: {
    type: String,
    default: 'center',
  },
})
</script>

<template>
  <span class="item__content" :class="[`item__content--${align}`]">
    <slot :row="row">
      {{ row[prop as string] }}
    </slot>
  </span>
</template>
