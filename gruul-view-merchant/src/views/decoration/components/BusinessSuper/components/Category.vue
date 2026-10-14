<script setup lang="ts">
import BusinessSuper from './../BusinessSuper'
import { getList } from '@/api/good/goods'
import BaseTable from '@/components/BaseTable/index.vue'
import { useDecorationStore } from '@/store/modules/decoration'

const props = defineProps({
  dialogShow: {
    type: Boolean,
    default: false,
  },
  formData: {
    type: Object as () => typeof BusinessSuper,
    required: true,
  },
})

const emit = defineEmits(['update:dialogShow', 'choose'])

// 双向绑定dialogShow
const syncDialogShow = computed({
  get: () => props.dialogShow,
  set: (val) => emit('update:dialogShow', val),
})

const chooseItem = ref([])
const productName = ref('')
const goodList = ref([])
const multipleTable = ref<InstanceType<typeof BaseTable> | null>(null)

const decorationStore = useDecorationStore()
const saleMode = computed(() => {
  return decorationStore.saleMode
})

onMounted(() => {
  getUseEffectiveCategory()
})

async function getUseEffectiveCategory() {
  const param = {
    saleMode: saleMode.value,
  }
  const res = await getList(param)
  goodList.value = res.data.list || []
  nextTick(() => {
    const { firstCatList } = props.formData
    goodList.value.forEach((i: any) => {
      if (firstCatList.find((k) => k.category?.id === i.id)) {
        multipleTable.value?.toggleRowSelection(i)
      }
    })
  })
}

/**
 * 勾选商品
 */
function handleGoodIdsChange(item = []) {
  chooseItem.value = item
}

/** 保存 */
function submit() {
  emit('choose', chooseItem.value)
  syncDialogShow.value = false
}
</script>

<template>
  <el-dialog
    v-model="syncDialogShow"
    title="选择展示分类"
    append-to-body
    top="10vh"
    lock-scroll
    :close-on-click-modal="false"
    width="480px"
    class="select__category-dialog"
  >
    <div v-if="false" class="row-wrap" style="margin-bottom: 20px">
      <!--      <el-button @click="getCategoryList"> -->
      <!--        刷新 -->
      <!--      </el-button> -->
      <el-input v-model="productName" placeholder="请输入搜索内容" class="search__category-input" />
    </div>
    <div class="page--top row-wrap">
      <BaseTable ref="multipleTable" :data="goodList" height="260" style="width: 100%" @selection-change="handleGoodIdsChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="分类名称" />
      </BaseTable>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="syncDialogShow = false">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
:deep(.el-pagination) {
  float: right;
}

.row-wrap {
  padding: 0 20px;
  position: relative;
  &-btn {
    height: 32px;
    border: 1px solid #000;
  }
  &-input {
    width: 200px;
    height: 33px;
    position: absolute;
    top: 0;
    right: 24px;
    :deep(.el-input__inner) {
      outline: none;
      border: 1px solid #000;
    }
  }
  &-selected {
    height: 40px;
    line-height: 40px;
    color: rgb(199, 22, 22);
    text-align: right;
    user-select: none;
  }
}
.page--top {
  .pro__list {
    width: 100%;
    &__tr2 {
      height: 62px;
      line-height: 62px;
      border-top: 1px solid #f2f2f2;
      &__td {
        position: relative;
        padding-left: 20px;
        &__check {
          position: absolute;
          top: -4px;
          left: 0px;
        }
      }
      td {
        width: 210px;
      }
    }
    &__tr1 {
      background: #f2f2f2;
      height: 40px;
      line-height: 40px;
      td {
        width: 210px;
      }
    }
  }
}
</style>

<style lang="scss">
.select__category-dialog {
  .search__category-input {
    float: right;
    width: 160px;
  }

  .el-dialog__body {
    padding: 15px 20px;
  }
}
</style>
