<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'
import ClassifiedGoods from './ClassifiedGoods.vue'

defineProps({
  formData: {
    type: Object as () => typeof BusinessSuper,
    required: true,
  },
})
</script>

<template>
  <div class="classifica__list-page">
    <ClassifiedGoods :form-data="formData" />
  </div>
</template>
