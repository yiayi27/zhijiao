<script setup lang="ts">
import { getList } from '@/api/good/goods'
import { CaretBottom } from '@element-plus/icons-vue'
import { cloneDeep } from 'lodash-es'

defineOptions({
  name: 'GoodCategory',
})

const props = defineProps({
  categorySeconds: {
    type: Object as () => CategorySecond,
    default: () => ({}),
  },
  cateType: {
    type: String,
    default: '',
  },
  popVisible: {
    type: Boolean,
    default: false,
  },
  showGetList: {
    type: Array,
    default: () => [],
  },
  saleMode: {
    type: String,
    default: '',
  },
  idsList: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['chPopVisible', 'getNewCateList'])

interface CategoryItem {
  showCategoryId: string | number
  name: string
  disabled?: boolean
  showCategoryVos: CategoryItem[]
  parentId?: string | number

  [key: string]: any
}

interface CategorySecond {
  productShowCategorys: {
    productShowCategorySeconds: {
      showCategoryId: string | number
      name: string
      [key: string]: any
    }[]
    [key: string]: any
  }[]

  [key: string]: any
}

const popoverVisible = ref(false)
const categoryList = ref<CategoryItem[]>([])
const checkedKeys = ref<(string | number)[]>([])

const customProps = {
  label: 'name',
  children: 'showCategoryVos',
}

const numType = computed(() => props.cateType)

// 监听popoverVisible变化
watch(popoverVisible, (nVal) => {
  if (nVal) {
    getAllCategory()
  }
})

// 监听popVisible变化
watch(
  () => props.popVisible,
  (val) => {
    if (val) {
      getAllCategory()
    }
  },
)

function cancel() {
  emit('chPopVisible', props.popVisible)
}

/**
 * 获取展示分类信息
 */
async function getAllCategory() {
  const param = {
    saleMode: props.saleMode,
  }

  const { data } = await getList(param)
  checkedKeys.value = []
  categoryList.value = data.list

  // 设置一级分类为禁用状态
  categoryList.value.forEach((v) => {
    v.disabled = true
  })

  if (numType.value === '1') {
    props.categorySeconds?.productShowCategorys?.forEach((v) => {
      v.productShowCategorySeconds?.forEach((n) => {
        checkedKeys.value.push(n.showCategoryId)
      })
    })
  } else {
    dealShowCate()
  }
}

/**
 * 所有已选择展示分类的数据
 */
function dealShowCate() {
  // 创建一个集合用于快速查找ID
  const idsSet = new Set(props.idsList)
  // 收集所有符合条件的showCategoryId
  const categoryIds: (string | number)[] = []

  props.showGetList.forEach((item: any) => {
    // 如果当前item的id在idsList中存在
    if (idsSet.has(item.id)) {
      // 遍历处理所有分类数据
      item.productShowCategorys?.forEach((category: CategoryItem) => {
        category.productShowCategorySeconds?.forEach((secondCategory: CategoryItem) => {
          categoryIds.push(secondCategory.showCategoryId)
        })
      })
    }
  })

  // 一次性添加所有ID到checkedKeys
  if (categoryIds.length > 0) {
    checkedKeys.value.push(...categoryIds)
  }
}

/**
 * 批量修改展示分类保存
 */
function saveChange() {
  const arr: CategoryItem[] = []
  const temCategoryList = cloneDeep(categoryList.value)

  temCategoryList.forEach((item) => {
    checkedKeys.value.forEach((v) => {
      item.showCategoryVos.forEach((n) => {
        if (v === n.showCategoryId) {
          arr.push(n)
        }
      })
    })
  })

  temCategoryList.forEach((item) => {
    item.showCategoryVos = []
    arr.forEach((n) => {
      if (n.parentId === item.showCategoryId) {
        item.showCategoryVos.push(n)
      }
    })
  })

  emit('getNewCateList', temCategoryList, props.categorySeconds)
  popoverVisible.value = false
}

/**
 * 获取重新选择编辑分类ID
 */
function nodeClick(data: CategoryItem) {
  const index = checkedKeys.value.findIndex((item) => {
    return item === data.showCategoryId
  })

  if (index > -1) {
    checkedKeys.value.splice(index, 1)
  } else {
    checkedKeys.value.push(data.showCategoryId)
  }

  checkedKeys.value = Array.from(new Set(checkedKeys.value))
}
</script>

<template>
  <span class="goodCategory">
    <span v-if="numType === '1'">
      <el-popover v-model:visible="popoverVisible" placement="right" width="260" trigger="click">
        <span class="pop__text">注：一级分类不可勾选，禁用状态</span>
        {{ categoryList }}
        <el-tree
          :data="categoryList"
          :props="customProps"
          show-checkbox
          :default-expand-all="false"
          :default-checked-keys="checkedKeys"
          node-key="showCategoryId"
          style="margin-top: 10px"
          @check="nodeClick"
        />
        <div class="pop--button">
          <el-button type="primary" @click="saveChange">确认</el-button>
          <el-button @click="popoverVisible = false">取消</el-button>
        </div>
        <template #reference>
          <span class="goodCategory__content">
            <div v-for="(v, i) in props.categorySeconds.productShowCategorys" :key="i" style="display: flex">
              <div v-for="(n, iIndex) in v.productShowCategorySeconds" :key="iIndex">{{ n.name }};</div>
            </div>
            <el-icon><CaretBottom /></el-icon>
          </span>
        </template>
      </el-popover>
    </span>
    <span v-if="numType === '2'">
      <span class="pop__text">注：一级分类不可勾选，禁用状态</span>
      <el-tree
        :data="categoryList"
        :props="customProps"
        show-checkbox
        :default-expand-all="false"
        :default-checked-keys="checkedKeys"
        node-key="showCategoryId"
        style="margin-top: 10px"
        @check="nodeClick"
      />
      <div class="pop--button">
        <el-button type="primary" @click="saveChange">确认</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </span>
  </span>
</template>

<style lang="scss" scoped>
.goodCategory {
  margin-left: 10px;
  flex-wrap: wrap;
  display: flex;
  width: 100px;

  &__content {
    cursor: pointer;
    flex-wrap: wrap;
    display: flex;
    width: 120px;
  }
}

.pop__text {
  margin-left: 10px;
  font-size: 14px;
}

.pop--button {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
