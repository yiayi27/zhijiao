<script setup lang="ts">
import { addRegionList as apiAddRegionList, delRegionItem, editRegionList, getRegionList, sortRegionList } from '@/api/good/goods'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'

import { useUserStore } from '@/store/modules/user'

import { useRoute } from 'vue-router'
import ClassCom from './components/ClassCom.vue'

defineOptions({
  name: 'GoodRegion',
})

// 状态
const showRegion = ref(false)
const addFlag = ref(true)
const deleteType = ref(false)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const regionList = ref<any[]>([])
const addList = ref<any[]>([{ modeName: '' }])
const showType = ref(false)
const click = ref(false)

// 路由
const route = useRoute()
const userStore = useUserStore()

// 计算属性
const shopInfoId = computed(() => {
  return userStore.shopInfo?.platformShopId || ''
})

// 组件挂载
onMounted(() => {
  getInit()
})

/**
 * 删除专区
 */
function regionDelete(id: string) {
  delRegionItem(id, {}).then(() => {
    ElMessage.success('删除成功')
    getInit()
  })
}

/**
 * 获得已有专区
 */
function getInit() {
  // const level = shopLevel.value;
  const param = {
    current: pageNum.value,
    size: pageSize.value,
  }
  getRegionList(param).then((res) => {
    regionList.value = res.data.list
    total.value = res.data.total
  })
}

/**
 * 添加专区
 */
function addRegion() {
  showRegion.value = true
  addFlag.value = true
}

/**
 * 排序专区
 */
function sort(list: any[]) {
  list.forEach((item, index) => {
    item.sort = index
  })
  sortRegionList(list).then(() => {
    getInit()
  })
}

/**
 * 删除专区
 */
function deleteRegion(item: any, index: number) {
  addList.value.splice(index, 1)
}

/**
 * 新增专区
 */
function textAdd() {
  addList.value.push({
    modeName: '',
  })
}

/**
 * 取消操作
 */
function cancel() {
  showRegion.value = false
  addList.value = [
    {
      modeName: '',
    },
  ]
}

/**
 * 子组件编辑操作
 */
function editFucntion(item: any) {
  showRegion.value = true
  addFlag.value = false
  addList.value = []
  addList.value.push(item)
}

/**
 * 确认操作
 */
function sureDeal() {
  const list = addList.value[0]
  if (click.value) {
    return
  }
  if (!list.id) {
    addRegionListFunc()
    return
  }
  click.value = true
  editRegionList(list)
    .then(() => {
      ElMessage.success('设置成功')
      cancel()
      getInit()
    })
    .finally(() => {
      click.value = false
    })
}

/**
 * 添加专区
 */
function addRegionListFunc() {
  click.value = true
  const list = addList.value
  const nameType = list.some((item) => {
    return item.modeName === ''
  })
  if (nameType) {
    ElMessage.error('专区名称不能为空')
    return
  }
  apiAddRegionList(list)
    .then(() => {
      ElMessage.success('设置成功')
      showRegion.value = false
      getInit()
    })
    .finally(() => {
      click.value = false
    })
}

/**
 * 删除子组件专区
 */
function deleteRegionFunction(item: any) {
  if (regionList.value.length <= 1) {
    ElMessage.error('至少保留一个专区')
    return
  }
  if (item.productNumber > 0) {
    ElMessage.error('请将商品移至其它专区后，再进行删除操作')
    return
  }
  regionDelete(item.id)
  deleteType.value = true
}

/**
 * 页码大小变化
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  getInit()
}

/**
 * 当前页变化
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getInit()
}

// 暴露方法供组件内部使用
defineExpose({
  editFucntion,
  sort,
  deleteRegionFunction,
})
</script>

<template>
  <MContainer>
    <template #form>
      <div class="regionTop">
        <div class="regionTop__lineWidth">使用须知:</div>
        <div class="regionTop__lineWidth">1.至少保留一个专区</div>
      </div>
    </template>

    <template #section>
      <div class="regionAdd">
        <el-button :disabled="showType" @click="addRegion"> 添加专区 </el-button>
        <div class="regionAdd__tip">拖拽可调整专区顺序</div>
      </div>
    </template>

    <template #table>
      <ClassCom :region-list="regionList" @sort="sort" @edit-fucntion="editFucntion" @delete-region-function="deleteRegionFunction" />
    </template>

    <template #pagination>
      <PageManage
        :page-size="pageSize"
        :page-num="pageNum"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>

    <el-dialog v-model="showRegion" width="400px" :before-close="cancel">
      <template #header>
        <div class="dialogTitle">{{ addFlag ? '添加' : '编辑' }}专区</div>
      </template>
      <div class="dialogName">专区名称</div>
      <div v-for="(item, index) in addList" :key="index" class="dialogList">
        <div class="dialogList__item">
          <el-input v-model="item.modeName" style="width: 200px" placeholder="请输入专区名称" />
          <el-button v-if="index !== 0" link @click="deleteRegion(item, index)"> 删除 </el-button>
        </div>
      </div>
      <div>
        <el-button v-if="addFlag" link @click="textAdd"> 添加 </el-button>
      </div>
      <template #footer>
        <span class="dialog--footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="sureDeal">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </MContainer>
</template>

<style lang="scss" scoped>
.regionTop {
  width: 100%;
  height: 100px;
  background-color: #f2f2f2;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding: 10px 30px;
  color: #666666;
  font-size: 12px;

  &__lineWidth {
    width: 100%;
    display: flex;
  }
}

.regionAdd {
  display: flex;
  justify-content: space-between;

  &__tip {
    color: #666666;
    font-size: 14px;
  }
}

.dialogTitle {
  display: flex;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
}

.dialogName {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  padding-left: 20px;
  background-color: #e9f3fb;
  font-size: 14px;
  font-weight: bold;
  margin-top: -20px;
}

.dialogList {
  margin-top: 20px;

  &__item {
    display: flex;
    margin-bottom: 20px;
    justify-content: space-between;
    width: 300px;
  }
}

.center {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
</style>
