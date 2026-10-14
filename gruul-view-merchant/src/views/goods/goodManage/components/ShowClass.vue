<script lang="ts" setup>
import { addList, addSecondList, delLevel, getList, Sort, updateList } from '@/api/good/goods'
import PageManage from '@/components/PageManage.vue'
import ShowSecond from '@/components/ShowSecond.vue'
import { ElMessageBox } from 'element-plus'

import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()

// 状态定义
/** 展示分类 */
const navigationListCom = ref<any[]>([])

/** 编辑一级二级分类 */
const rankRadio = ref('1')

/** 列表中选择一级分类 */
const tamValue = ref<any>({})

/** 编辑二级暂存 */
const temObj = ref<any>({})

/** 处理的分类对象 */
const currentItem = reactive({
  id: '',
  name: '',
  level: 0,
  parentId: 0,
  saleMode: '',
  showCategoryVos: [
    {
      id: '',
      level: 1,
      name: '',
      parentId: '',
      saleMode: '',
    },
  ],
})

/** 编辑弹窗 */
const editFlag = ref(false)

/** 编辑或新增状态 */
const addFlag = ref(false)

/** 一级单选框 */
const levelOne = ref(false)

/** 二级单选框 */
const levelTwo = ref(false)

// 分页页数
const pageSize = ref(10)

// 分页页码
const pageNum = ref(1)

// 数据总数
const total = ref(0)

/** 是否发布商品返回 */
const showType = ref(false)

const showClassType = ref(false)

const saleMode = ref('')

// 生命周期
onMounted(() => {
  showType.value = false
  showClassType.value = false
  if (route.query.options === '1') {
    showType.value = true
    saleMode.value = route.query.saleMode as string
  }
  if (route.query.options === 'classSet') {
    showClassType.value = true
    saleMode.value = route.query.id as string
    currentItem.saleMode = saleMode.value ? saleMode.value : ''
    currentItem.showCategoryVos[0].saleMode = saleMode.value ? saleMode.value : ''
  }
  getAllList()
  pageSize.value = 10
  pageNum.value = 1
})

// 方法定义
function getAllList(isEdd = false) {
  if (isEdd) {
    pageNum.value = 1
  }
  const param = {
    current: pageNum.value,
    size: pageSize.value,
    saleMode: saleMode.value,
  }
  getList(param).then((res) => {
    navigationListCom.value = res.data.list
    total.value = res.data.total
  })
}

/** 编辑 新增 操作 */
function modifyList(type: number, item?: any) {
  editFlag.value = true
  levelOne.value = false
  levelTwo.value = false
  if (type === 3) {
    rankRadio.value = '1'
    addFlag.value = false
  } else if (type === 2) {
    rankRadio.value = '1'
    levelTwo.value = true
    addFlag.value = true
    // currentItem = item;
    Object.assign(currentItem, JSON.parse(JSON.stringify(item)))
  } else if (type === 1) {
    rankRadio.value = '2'
    addFlag.value = false
    currentItem.name = item.name
    currentItem.showCategoryVos = []
    currentItem.showCategoryVos.push({
      id: '',
      level: 1,
      name: '',
      parentId: '',
      saleMode: '',
    })
    tamValue.value = item
  }
}

/**
 * 选择一级或二级分类
 */
function changeRadio() {
  Object.assign(currentItem, {
    id: '',
    name: '',
    level: 0,
    parentId: 0,
    saleMode: '',
    showCategoryVos: [
      {
        id: '',
        level: 1,
        name: '',
        parentId: '',
        saleMode: '',
      },
    ],
  })
}

/**
 * 新增是获取一级分类下数据
 */
function select(e: string) {
  navigationListCom.value.forEach((item) => {
    if (item.name === e) {
      tamValue.value = item
    }
  })
}

/**
 * 新增下级分类
 */
function getAddClass() {
  currentItem.showCategoryVos.push({
    id: '',
    level: 1,
    name: '',
    parentId: '',
    saleMode: '',
  })
}

/**
 * 删除新增下级分类
 */
function delAddClass(index: number) {
  if (currentItem.showCategoryVos.length === 1) {
    ElMessage.error('至少保留一行子分类')
  } else {
    currentItem.showCategoryVos.splice(index, 1)
  }
}

/**
 * 完成商品分类的新增/编辑
 */
function editOk() {
  if (rankRadio.value === '1') {
    dealLevelOne()
  } else if (rankRadio.value === '2') {
    dealLevelTwo()
  }
}

/** 处理一级展示分类 */
function dealLevelOne() {
  if (currentItem.name === '') {
    ElMessage.error('一级分类不能为空')
    return
  }
  if (currentItem.name !== '') {
    if (addFlag.value === false) {
      const param = {
        id: currentItem.id,
        name: currentItem.name,
        level: 0,
        parentId: '0',
        saleMode: saleMode.value,
      }
      addList(param).then((res) => {
        if (res.code === 200) {
          ElMessage.success('添加成功')
          editFlag.value = false
          getAllList(true)
          currentItem.name = ''
        }
      })
    } else {
      currentItem.level = 0
      currentItem.parentId = 0
      updateList(currentItem).then((res) => {
        if (res.code === 200) {
          ElMessage.success('编辑成功')
          getAllList()
          editFlag.value = false
          currentItem.name = ''
        }
      })
    }
  }
}

/** 处理二级展示分类 */
function dealLevelTwo() {
  currentItem.id = tamValue.value.id
  const length = currentItem.showCategoryVos.filter((v) => {
    return v.name === ''
  }).length
  if (!currentItem.name) {
    ElMessage.error('请选择一级分类')
    return
  }
  if (length > 0) {
    ElMessage.error('请输入二级分类')
    return
  }
  const arry = currentItem.showCategoryVos.map((item) => {
    return item.name
  })
  const chongfu = arry.every((item, index, self) => {
    return self.indexOf(item) === index
  })
  if (!chongfu) {
    ElMessage.error('输入的二级分类重复')
    return
  }
  currentItem.showCategoryVos.forEach((item) => {
    item.parentId = currentItem.id
  })
  addSecondList(currentItem.showCategoryVos).then((res) => {
    if (res.code === 200) {
      ElMessage.success('添加成功')
      getAllList()
      editFlag.value = false
      currentItem.name = ''
    }
  })
}

/** 删除分类 */
function btnDelClass(item: any) {
  ElMessageBox.confirm('确定要删除选中分类吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delLevel(item.id, {}).then((res) => {
      if (res.code === 200) {
        editFlag.value = false
        ElMessage.success('删除成功')
        getAllList()
      }
    })
  })
}

/**
 * 编辑二级分类
 */
function editClassTwo(item: any) {
  Object.assign(currentItem, {
    id: '',
    name: '',
    level: 0,
    parentId: 0,
    saleMode: '',
    showCategoryVos: [
      {
        id: '',
        level: 1,
        name: '',
        parentId: '',
        saleMode: '',
      },
    ],
  })

  navigationListCom.value.forEach((v) => {
    if (v.showCategoryVos) {
      v.showCategoryVos.forEach((i: any) => {
        if (i.id === item.id) {
          currentItem.showCategoryVos[0].name = i.name
          currentItem.name = v.name
        }
      })
    }
  })

  addFlag.value = true
  editFlag.value = true
  rankRadio.value = '2'
  levelOne.value = true
  temObj.value = item
}

/**
 * 删除二级分类
 */
function delClassTwo(item: any) {
  ElMessageBox.confirm('确定要删除此二级分类吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delLevel(item.id, {}).then((res) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getAllList()
      }
    })
  })
}

/**
 * 保存二级分类编辑
 */
function editClass() {
  currentItem.showCategoryVos[0].id = temObj.value.id

  navigationListCom.value.forEach((v) => {
    if (v.showCategoryVos) {
      v.showCategoryVos.forEach((i: any) => {
        if (i.id === temObj.value.id) {
          currentItem.showCategoryVos[0].parentId = v.id
        }
      })
    }
  })

  updateList({
    id: currentItem.showCategoryVos[0].id,
    level: 1,
    name: currentItem.showCategoryVos[0].name,
    parentId: tamValue.value.id ? tamValue.value.id : currentItem.showCategoryVos[0].parentId,
    saleMode: saleMode.value ? saleMode.value : '',
  }).then((res) => {
    if (res.code === 200) {
      ElMessage.success('修改成功')
      editFlag.value = false
      currentItem.name = ''
      getAllList()
      temObj.value = {}
    }
  })
}

/** 弹窗取消 */
function cancel() {
  rankRadio.value = '1'
  editFlag.value = false
  temObj.value = {}
  levelOne.value = false
  levelTwo.value = false
  if (currentItem.showCategoryVos.length > 0) {
    currentItem.showCategoryVos[0].name = ''
  }
  Object.assign(currentItem, {
    id: '',
    name: '',
    level: 0,
    parentId: 0,
    saleMode: '',
    showCategoryVos: [
      {
        id: '',
        level: 1,
        name: '',
        parentId: '',
        saleMode: '',
      },
    ],
  })
}

function navigationListComChange(list: any[]) {
  list.forEach((item, index) => {
    item.sort = index + 10 * (pageNum.value - 1)
    item?.showCategoryVos?.forEach((child: Obj, index: number) => {
      child.sort = index
    })
  })
  Sort(list)
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  getAllList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getAllList()
}

/**
 * 返回上一页
 */
function back() {
  router.go(-1)
}
</script>

<template>
  <div>
    <div class="button--line">
      <div style="display: flex">
        <el-button v-if="showClassType" @click="back"> 返回专区列表 </el-button>
        <el-button v-if="showType" @click="back"> 返回发布商品 </el-button>
        <el-button type="primary" @click="modifyList(3, undefined)"> 新增分类 </el-button>
      </div>
      <div class="button--right">拖拽可调整展示分类顺序</div>
    </div>
    <ShowSecond
      :navigation-list-com="navigationListCom"
      @modify-list="modifyList"
      @btn-del-class="btnDelClass"
      @edit-class-two="editClassTwo"
      @del-class-two="delClassTwo"
      @navigation-list-com-change="navigationListComChange"
    />
    <PageManage
      :page-size="pageSize"
      :page-num="pageNum"
      :total="total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="editFlag" width="30%" :before-close="cancel">
      <template #header>
        <div class="dialogTitle">{{ addFlag ? '编辑' : '新增' }}商品类别</div>
      </template>
      <div class="dialog__radio dialog__form">
        <div class="dialog__form--label"><span class="required">*</span>级别：</div>

        <el-radio v-model="rankRadio" value="1" :disabled="levelOne" @change="changeRadio"> 一级 </el-radio>
        <el-radio v-model="rankRadio" value="2" :disabled="levelTwo" @change="changeRadio"> 二级 </el-radio>
      </div>
      <div class="dialog__line dialog__form">
        <div class="dialog__form--label"><span class="required">*</span>一级分类：</div>
        <el-input v-if="rankRadio === '1'" v-model="currentItem.name" placeholder="请输入内容" maxlength="8" style="width: 180px" />
        <el-select v-if="rankRadio === '2'" v-model="currentItem.name" placeholder="请选择一级模块" @change="select">
          <el-option v-for="item in navigationListCom" :key="item.name" :label="item.name" :value="item.name" />
        </el-select>
      </div>
      <div v-if="rankRadio === '2'" class="dialog__line dialog__form">
        <div class="dialog__form--label"><span class="required">*</span>二级分类：</div>

        <div class="dialog__form--subClassify">
          <div class="dialog__line__sec--head">
            <div>下级分类名称</div>
            <div>操作</div>
          </div>
          <div v-for="(child, index) in currentItem.showCategoryVos" :key="index">
            <div class="dialog__line__sec--body" :class="[{ 'border-bottom': index !== currentItem.showCategoryVos.length - 1 }]">
              <el-input v-model="child.name" style="width: 150px" placeholder="请输入分类名称" maxlength="8" />
              <div v-if="!temObj.name" class="flex items-center">
                <div
                  v-if="index + 1 === currentItem.showCategoryVos.length"
                  class="add--text"
                  style="color: #2d8cf0; cursor: pointer"
                  @click="getAddClass"
                >
                  新增
                </div>
                <span
                  v-if="currentItem.showCategoryVos.length > 1"
                  style="color: #e96900; cursor: pointer"
                  class="ml-[10px]"
                  @click="delAddClass(index)"
                  >删除</span
                >
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog--footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button v-if="temObj.name" type="primary" @click="editClass">确 定</el-button>
          <el-button v-else type="primary" @click="editOk">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.button--line {
  padding-top: 5px;
  padding-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button--right {
  // padding-right: 20px;
  color: #7f7f7f;
  font-size: 13px;
}

.dialogTitle {
  display: flex;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
}

.dialog__form {
  margin-left: 40px;
  display: flex;
  align-items: center;
  margin-top: 16px;

  .dialog__form--label {
    width: 100px;
    flex-shrink: 0;
  }

  .dialog__form--subClassify {
    width: 300px;
    border: 1px solid #eee;
    border-radius: 10px;
  }

  .dialog__line__sec--head {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    border-bottom: 1px solid #eee;
  }

  .dialog__line__sec--body {
    display: flex;
    justify-content: space-between;
    padding: 10px;

    &.border-bottom {
      border-bottom: 1px solid #eee;
    }
  }
}

.required {
  color: var(--el-color-danger);
  margin-right: 5px;
}
</style>
