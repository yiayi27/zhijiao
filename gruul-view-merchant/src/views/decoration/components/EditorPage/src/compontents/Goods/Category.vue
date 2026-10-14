<script setup lang="ts">
import type { CategoryItem } from './Goods'
import Goods from './Goods'
import { getAllRegionList, getEffectiveCategory } from '@/api/good/goods'
import BaseTable from '@/components/BaseTable/index.vue'

// 定义props和emit
const props = defineProps({
  dialogShow: {
    type: Boolean,
    default: false,
  },
  subForm: {
    type: Object as () => typeof Goods,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:dialogShow', 'choose'])

// 同步对话框显示状态
const syncDialogShow = ref(props.dialogShow)

// 监听dialogShow变化并同步
watch(
  () => props.dialogShow,
  (val) => {
    syncDialogShow.value = val
  },
)

// 监听syncDialogShow变化并同步回父组件
watch(syncDialogShow, (val) => {
  emit('update:dialogShow', val)
})

// 选择的项目
const chooseItem = ref([])

// 所有专区的数据
const list = ref<CategoryItem[]>([])

// 选中专区
const activeName = ref('')

// 选中专区id
const saleMode = ref('')
const productName = ref('')
const goodList = ref<CategoryItem[]>([])

// 表格引用
const multipleTable = ref<typeof BaseTable | null>(null)

onMounted(() => {
  getCategoryList()
})

/**
 * 获取分类列表
 */
async function getCategoryList() {
  const res = await getAllRegionList({})
  list.value = res.data
  activeName.value = res.data[0].modeName
  saleMode.value = res.data[0].id
  getUseEffectiveCategory()
}

/** 根据saleMode查询对应展示分类 */
async function getUseEffectiveCategory() {
  const res = await getEffectiveCategory(saleMode.value)
  goodList.value = res.data || []
  goodList.value.forEach((i: CategoryItem) => {
    i.saleMode = saleMode.value
    i.productNumber =
      i.showCategoryVos?.reduce((a: number, b: CategoryItem) => {
        a += Number(b.productNumber || 0)
        return a
      }, 0) || 0
    if (i.productNumber > 50) i.productNumber = 50
  })

  // 下一个DOM更新周期后执行
  nextTick(() => {
    const { firstCatList = [] } = props.subForm
    goodList.value.forEach((i: CategoryItem) => {
      if (firstCatList.find((k: any) => k.id === i.id) && multipleTable.value) {
        multipleTable.value.toggleRowSelection(i)
      }
    })
  })
}

/**
 * 顶部专区选择
 */
function selectTem(item: CategoryItem) {
  saleMode.value = item.id
  getUseEffectiveCategory()
}

/**
 * 勾选商品
 */
function handleGoodIdsChange(item = []) {
  chooseItem.value = item
}

/** 保存 */
function submit() {
  // addSelectedCategory();
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
      <el-button @click="getCategoryList"> 刷新 </el-button>
      <el-input v-model="productName" placeholder="请输入搜索内容" class="search__category-input" />
    </div>
    <el-select v-model="activeName" style="width: 120px" placeholder="全部专区">
      <el-option-group v-for="group in list" :key="group.id">
        <el-option :label="group.modeName" :value="group.modeName" @change="selectTem(group)" />
      </el-option-group>
    </el-select>
    <!-- <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane
        v-for="(item, index) in list"
        :key="index"
        :label="item.modeName"
        :name="item.modeName"
        :id="item.id"
      >
      </el-tab-pane>
    </el-tabs> -->
    <div class="page--top row-wrap">
      <BaseTable ref="multipleTable" :data="goodList" style="width: 100%" max-height="350" @selection-change="handleGoodIdsChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="productNumber" label="商品数量" width="140" />
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
