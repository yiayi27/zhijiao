<script setup lang="ts">
// 定义父组件
const props = defineProps({
  navigationListCom: {
    type: Array,
    default: () => [],
  },
})

// 定义事件
const emit = defineEmits(['navigationListComChange', 'modifyList', 'btnDelClass', 'editClassTwo', 'delClassTwo'])

/** 处理数组 */
const list = ref<any[]>([])

/** 展开数组 */
const showList = ref<any[]>([])

/** 是否有数据 */
const hasList = ref(false)

// // 获取父组件的navigationListCom，使用计算属性获取最新值
// const navigationListComValue = computed(() => props.navigationListCom || [])

// 监听navigationListCom的变化
watch(
  () => props.navigationListCom,
  (newVal) => {
    list.value = JSON.parse(JSON.stringify(newVal || []))
    hasList.value = list.value.length === 0

    // 重置showList
    showList.value = []
    list.value.forEach((item, index) => {
      if (item.showCategoryVos && item.showCategoryVos.length > 0) {
        showList.value.push(index)
      }
    })
  },
  { deep: true, immediate: true },
)

/**
 * 处理拖动后的列表更新
 */
function onMove() {
  setTimeout(() => {
    emit('navigationListComChange', list.value)
  }, 500)
}

/**
 * 一级编辑
 */
function modifyList(type: number, item: any) {
  emit('modifyList', type, item)
}

/**
 * 一级删除
 */
function btnDelClass(item: any) {
  emit('btnDelClass', item)
}

/**
 * 二级编辑
 */
function editClassTwo(item: any) {
  emit('editClassTwo', item)
}

/**
 * 二级删除
 */
function delClassTwo(item: any) {
  emit('delClassTwo', item)
}

// 导出组件内部方法和属性，使它们可以被父组件访问
defineExpose({
  list,
  showList,
  hasList,
})
</script>

<template>
  <div class="borderBox">
    <m-drag-list v-model:data="list" v-model:default-open="showList" item-key="id" child-key="showCategoryVos" @on-move="onMove">
      <template #item="{ itemData }">
        <div class="borderBox__first">
          <div>{{ itemData.name }}</div>
          <div class="borderBox__first--deal">
            <span style="color: #2d8cf0" @click.stop="modifyList(1, itemData)">新增二级分类</span>
            <span style="color: #2d8cf0" @click.stop="modifyList(2, itemData)">编辑</span>
            <span style="color: #fa6465" @click.stop="btnDelClass(itemData)">删除</span>
          </div>
        </div>
      </template>
      <template #child="{ childData }">
        <div class="borderBox__child" @onMove="onMove">
          <div class="borderBox__child--left">
            {{ childData.name }}
            <div style="text-align: left; width: 92px">商品数量： {{ childData.productNumber }}</div>
          </div>
          <div class="borderBox__child--right">
            <div style="color: #2d8cf0" @click="editClassTwo(childData)">编辑</div>
            <div style="color: #fa6465" @click="delClassTwo(childData)">删除</div>
          </div>
        </div>
      </template>
    </m-drag-list>
    <div v-if="hasList" class="emptyLine">暂无数据~</div>
  </div>
</template>

<style lang="scss" scoped>
.borderBox {
  &__first {
    background-color: #f2f2f6;
    // padding: 0px 10px;
    display: flex;
    justify-content: space-between;

    &--deal {
      margin-right: 10px;
      display: flex;
      justify-content: space-between;
      width: 200px;
      cursor: pointer;
    }
  }

  &__child {
    padding: 15px 10px 15px 20px;
    border-bottom: 1px solid #f2f2f2;
    display: flex;
    justify-content: space-between;

    &--left {
      display: flex;
      justify-content: space-between;
      width: 350px;
      padding-left: 10px;
    }

    &--right {
      display: flex;
      justify-content: space-between;
      width: 88px;
      cursor: pointer;
    }
  }

  :deep(.el-icon-arrow-right:before) {
    content: '\e791';
  }

  :deep(.el-collapse-item__content) {
    padding-bottom: 0;
  }

  :deep(.el-collapse-item__header) {
    background-color: #f2f2f6;
    padding-left: 10px;
  }
}

.borderBox__child:hover {
  background-color: #f5f5f5;
  cursor: move;
}

.emptyLine {
  width: 100%;
  height: 80px;
  background-color: white;
  display: flex;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  font-size: 14px;
  color: #b3b3b3;
  border-bottom: 1px solid #ebeef5;
  border-top: 1px solid #ebeef5;
}
</style>
