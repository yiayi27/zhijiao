<script setup lang="ts">
import { addAttrList, delAttr, delAttrSon, getAttsList, updateAttrList } from '@/api/good/goods'
import MContent from '@/components/MContainer/MContent.vue'
import PageManage from '@/components/PageManage.vue'
import { CircleClose } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

defineOptions({
  name: 'CategoryTem',
})

// 声明类型
interface AttributeTemplate {
  content: string
  name: string
  id: string
  parentId: string
}

interface AttributeItem {
  name: string
  content: string
  id: string
  parentId: string
  attributeTemplates: AttributeTemplate[]
}

// 定义表单检验类型
interface ValidationRule {
  required?: boolean
  trigger?: string
  message?: string
  validator?: (rule: unknown, value: any, callback: (error?: Error) => void) => void
}

function getDefaultAttrTemplate() {
  return {
    name: '',
    content: '',
    id: '',
    parentId: '0',
    attributeTemplates: [
      {
        content: '',
        name: '',
        id: '',
        parentId: '1',
      },
    ],
  }
}

// 响应式数据
const attributeList = ref<AttributeItem[]>([])
const attributeTemplateRef = ref()
const attributeTemplate = reactive<AttributeItem>(getDefaultAttrTemplate())

const attributeTemplateRules: Record<string, ValidationRule[]> = {
  name: [{ required: true, trigger: 'blur', message: '模板名称不能为空' }],
  attributeTemplates: [
    {
      required: true,
      trigger: 'focus',
      validator: (_rule: unknown, value: AttributeTemplate[], callback: (error?: Error) => void) => {
        value.forEach((item) => {
          if (item.content === '' || item.name === '') {
            callback(new Error('属性名称与特征不能为空'))
          }
        })
        callback()
      },
    },
  ],
}

const editFlag = ref(false)
const dialogVisible = ref(false)
const pageSize = ref(10)
const pageNum = ref(1)
const total = ref(0)
const hasList = ref(false)

// 生命周期钩子
onMounted(() => {
  getAttList()
})

/**
 * 获取属性模板
 */
function getAttList() {
  const param = {
    current: pageNum.value,
    size: pageSize.value,
  }
  getAttsList(param).then((res) => {
    attributeList.value = res.data.list
    hasList.value = res.data.list.length === 0
    total.value = res.data.total
  })
}

/**
 * @method editParmList
 * @description 编辑模板
 */
function editAttributeTemplate(type: number, item: AttributeItem) {
  dialogVisible.value = true
  Object.assign(attributeTemplate, JSON.parse(JSON.stringify(item)))
  editFlag.value = type === 2
}

/**
 * 删除列表中属性
 */
function deleteAttributeList(index: number, listIndex: number) {
  ElMessageBox.confirm('确定要删除选中属性模板吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const item = JSON.parse(JSON.stringify(attributeList.value[index]))
    const ids = item.attributeTemplates[listIndex].id
    delAttrSon(ids, {}).then(() => {
      ElMessage.success('删除成功')
      getAttList()
    })
  })
}

/**
 * 删除属性模板
 */
function deleteAttribute(item: AttributeItem) {
  ElMessageBox.confirm('确定要删除选中属性模板吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delAttr(item.id, {}).then(() => {
      ElMessage.success('删除成功')
      getAttList()
    })
  })
}

/**
 * 添加模板名称
 */
function addParmList() {
  attributeTemplate.attributeTemplates.push({
    content: '',
    name: '',
    id: '',
    parentId: '1',
  })
}

/**
 * 删除属性名称
 */
function deleteParmList(index: number) {
  if (attributeTemplate.attributeTemplates.length > 1) {
    attributeTemplate.attributeTemplates.splice(index, 1)
  } else {
    ElMessage.error('不能删除最后一条数据')
  }
}

/**
 * 取消操作
 */
function cancelAttributeTemplate() {
  ElMessageBox.confirm('确定要退出编辑属性列表页面? 退出后，未保存的信息将不会保留!', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    onCloseDialog()
  })
}

function onCloseDialog() {
  Object.assign(attributeTemplate, getDefaultAttrTemplate())
  dialogVisible.value = false
}

/**
 * 保存模板
 */
function saveAttributeTemplate(_formName: string) {
  let hasBlack = true
  attributeTemplateRef.value.validate((valid: boolean) => {
    if (!valid) {
      hasBlack = false
    }
  })
  if (!hasBlack) {
    return
  }
  if (editFlag.value) {
    attributeTemplate.attributeTemplates.forEach((item) => {
      item.parentId = attributeTemplate.id
    })
    updateAttrList(attributeTemplate).then((res) => {
      if (res.code === 200) {
        ElMessage.success('编辑成功')
        getAttList()
        onCloseDialog()
      }
    })
  } else {
    addAttrList(attributeTemplate).then((res) => {
      if (res.code === 200) {
        ElMessage.success('添加成功')
        getAttList()
        onCloseDialog()
      }
    })
  }
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  pageSize.value = val
  getAttList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  pageNum.value = val
  getAttList()
}

defineExpose({
  dialogVisible,
  attributeTemplate,
})
</script>

<template>
  <MContent>
    <template #table>
      <div v-if="hasList" class="emptyLine">暂无数据~</div>
      <div v-for="(item, index) in attributeList" v-else :key="index" class="tem__list table-border">
        <div class="tem__list--column">
          <div class="table-head-color tem__list--column--head">属性模板名称</div>
          <div style="background-color: #f6f7fb" class="tem__list--column--body table-border-top">
            {{ item.name }}
          </div>
        </div>
        <div class="table-border-left tem__list--right">
          <div class="table-head-color tem__list--column--head">
            <div class="tem__list--column--head--name">属性名称</div>
            <div class="tem__list--column--head--par">属性特征</div>
            <div class="tem__list--column--head--do">
              <el-button style="padding: 0" link @click="editAttributeTemplate(2, item)"> 编辑 </el-button>
              <el-button style="padding: 0" link @click="deleteAttribute(item)"> 删除 </el-button>
            </div>
          </div>
          <div v-for="(listItem, listIndex) in item.attributeTemplates" :key="listIndex" class="table-border-top tem__list--navList">
            <div class="tem__list--navList--param">
              {{ listItem.name }}
            </div>
            <div class="tem__list--right">
              {{ listItem.content }}
            </div>
            <div class="tem__list--right--icon">
              <el-icon class="text-info pointer">
                <CircleClose @click="deleteAttributeList(index, listIndex)" />
              </el-icon>
            </div>
          </div>
        </div>
      </div>
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

    <el-dialog
      v-model="dialogVisible"
      :title="editFlag ? '编辑商品属性模板' : '新增商品属性模板'"
      width="560px"
      :before-close="cancelAttributeTemplate"
      @close="editFlag = false"
    >
      <el-form ref="attributeTemplateRef" :model="attributeTemplate" :rules="attributeTemplateRules">
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="attributeTemplate.name" style="width: 40%" :maxlength="15" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="" prop="attributeTemplates" class="attribute-templates">
          <div class="attribute__table--head table-head-color">
            <div style="width: 34%; margin-left: 20px">属性名称</div>
            <div style="width: 50%">属性特征</div>
            <div style="width: 60px" />
          </div>
          <div v-for="(item, index) in attributeTemplate.attributeTemplates" :key="index" class="table-head-color attribute__table--body">
            <div class="attribute__table--body--item">
              <div style="width: 50%">
                <el-input v-model="item.name" size="large" maxlength="8" style="width: 80%" placeholder="请输入属性名称" />
              </div>
              <div style="width: 50%">
                <el-input v-model.trim="item.content" size="large" maxlength="100" style="width: 80%" placeholder="请输入属性特征" />
              </div>
              <div class="tem__list--right" style="width: 80px; text-align: right">
                <el-icon class="text-info">
                  <CircleClose @click="deleteParmList(index)" />
                </el-icon>
              </div>
            </div>
          </div>
          <div>
            <el-button link @click="addParmList"> 添加 </el-button>
          </div>
        </el-form-item>
      </el-form>
      <div style="text-align: center">
        <el-button @click="cancelAttributeTemplate"> 取 消 </el-button>
        <el-button type="primary" @click="saveAttributeTemplate('attributeTemplateRef')"> 确 定 </el-button>
      </div>
    </el-dialog>
  </MContent>
</template>

<style lang="scss" scoped>
@import '../../../../assets/styles/goods/index.scss';

.noList {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  color: #3d3d3d;
  width: 100%;
}

.notemp {
  margin-top: 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.tem__list {
  display: flex;
  justify-content: space-between;

  & + & {
    margin-top: 20px;
  }

  &--column {
    width: 180px;
    display: flex;
    flex-direction: column;
  }

  &--column--head {
    display: flex;
    justify-content: center;
    padding-top: 20px;
    padding-bottom: 20px;

    &--name {
      padding-left: 20px;
      padding-right: 20px;
      width: 40%;
    }

    &--par {
      flex: auto;
    }

    &--do {
      flex: none;
      width: 120px;
      padding-left: 20px;
      padding-right: 20px;
      text-align: right;
    }
  }

  &--column--body {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    flex: auto;
  }

  &--navList {
    display: flex;
    line-height: 44px;

    &--param {
      width: 40%;
      padding-left: 20px;
      padding-right: 20px;
    }
  }

  &--right {
    flex: auto;

    &--icon {
      flex: none;
      width: 120px;
      padding-left: 20px;
      padding-right: 20px;
      text-align: right;
    }
  }
}

.attribute__table--head {
  display: flex;
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
  margin-bottom: 10px;
}

.attribute__table--body {
  padding: 8px 10px;
  display: flex;

  &--item {
    display: flex;
    align-items: center;
  }
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
  margin-top: 15px;
  font-size: 14px;
  color: #b3b3b3;
  border-bottom: 1px solid #ebeef5;
  border-top: 1px solid #ebeef5;
}

.attribute-templates {
  :deep(.el-form-item__content) {
    display: block;
  }
}
</style>
