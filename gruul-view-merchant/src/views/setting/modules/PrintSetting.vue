<script setup lang="ts">
import { addSheetMach, delSheetMach, getSheetList, sheetLink } from '@/api/logistics/logistics'
import { ElMessageBox } from 'element-plus'

import AddSheetModel from '../common/AddSheetModel.vue'

// 定义类型接口
interface SheetItem {
  id: string
  deviceType: string
  deviceName: string
  status: string
}

// refs
const addSheetModelRef = ref()

// 电子面单数组
const sheetList = ref<SheetItem[]>([])

// // 小票打印数组
// const receiptList = reactive([
//   {
//     id: '0',
//     type: '打印机',
//     xinghao: '备注名称/热敏条码云打印机 365M2',
//     yici: '1',
//     lianjie: '0',
//     linkType: '0',
//     setType: '0',
//   },
//   {
//     id: '1',
//     type: '打印机',
//     xinghao: '备注名称/映美标签打印机',
//     yici: '2',
//     lianjie: '1',
//     linkType: '1',
//     setType: '1',
//   },
// ])

// 新增打印机
const addDialog = ref(false)

// 当前编辑的对象
const editCurrent = ref<SheetItem | Record<string, any>>({})

// 生命周期钩子
onMounted(() => {
  getSheet()
})

/**
 * 获取电子面单列表
 */
function getSheet() {
  getSheetList({ currnt: 1, size: 10 }).then((res) => {
    sheetList.value = res.data.list
  })
}

/**
 * 新增电子面单
 */
function addSheetNew() {
  addDialog.value = true
  editCurrent.value = {}
}

/**
 * 电子面单打印机保存
 */
async function saveSheet() {
  await getSheetFormModel()
  const param = addSheetModelRef.value.sheetForm
  addSheetMach(param).then(() => {
    addDialog.value = false
    ElMessage.success('保存成功')
    getSheet()
  })
}

/**
 * 获取表单验证结果
 */
async function getSheetFormModel() {
  try {
    await addSheetModelRef.value.validate()
  } catch (error) {
    return Promise.reject(error)
  }
}

/**
 * 启用/停用电子面单打印机
 */
function linkDel(id: string, status: string) {
  sheetLink(id, status === '1' ? '0' : '1', {}).then(() => {
    ElMessage.success('设置成功')
    getSheet()
  })
}

/**
 * 编辑电子面单
 */
function editSheet(item: SheetItem) {
  addDialog.value = true
  editCurrent.value = item
}

/**
 * 删除电子面单打印机
 */
function delSheet(id: string) {
  ElMessageBox.confirm('确定删除与打印机的连接吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delSheetMach(id, {}).then(() => {
      ElMessage.success('删除成功')
      getSheet()
    })
  })
}

/**
 * 关闭新增打印机弹窗
 */
function handleClose() {
  addDialog.value = false
  addSheetModelRef.value.clearValidate()
}
</script>

<template>
  <div>
    <div class="printTop">
      <div class="printTop__icon" />
      <div class="printTop__value">电子面单</div>
      <div class="printTop__desc">用于打印物流发货单</div>
    </div>
    <div class="sheet">
      <el-button type="primary" @click="addSheetNew()"> 新增设备 </el-button>
      <div class="sheet__top">
        <div style="width: 200px" class="center">设备类型</div>
        <div style="width: 300px" class="center">设备备注名称</div>
        <div style="width: 300px" class="center">连接状态</div>
        <div style="width: 130px" class="center">操作</div>
      </div>
      <div v-for="(item, index) in sheetList" :key="index" class="sheet__list">
        <div class="sheet__list--type">
          {{ item.deviceType }}
        </div>
        <div class="sheet__list--model">
          {{ item.deviceName }}
        </div>
        <div class="sheet__list--link">
          {{ item.status === '1' ? '已连接' : '未连接' }}
        </div>
        <div class="sheet__list--deal">
          <div class="sheet__list--deal--noDel" @click="linkDel(item.id, item.status)">
            {{ item.status === '1' ? '断开' : '连接' }}
          </div>
          <div class="sheet__list--deal--noDel" @click="editSheet(item)">编辑</div>
          <div class="sheet__list--deal--del" @click="delSheet(item.id)">删除</div>
        </div>
      </div>
      <div v-if="sheetList.length === 0" class="emptyLine">暂无数据~</div>
    </div>
    <el-dialog v-model="addDialog" width="500px" :before-close="handleClose">
      <template #header>
        <div class="digTitle">新增设备</div>
      </template>
      <AddSheetModel ref="addSheetModelRef" :edit-current="editCurrent" :add-dialog="addDialog" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="saveSheet">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.printTop {
  width: 100%;
  display: flex;
  align-items: center;
  background-color: #f6f8fa;
  color: #586884;
  font-size: 14px;
  padding: 15px 15px 15px 30px;

  &__icon {
    width: 3px;
    height: 12px;
    background-color: #199ed8;
    margin-right: 10px;
  }

  &__value {
    color: #586884;
    font-weight: 700;
    font-size: 13px;
  }

  &__desc {
    margin-left: 20px;
    color: #515a6e;
    font-size: 12px;
  }
}

.center {
  display: flex;
  // justify-content: center;
  justify-content: flex-start;
}

.sheet {
  padding: 20px;

  &__top {
    margin-top: 10px;
    padding-left: 10px;
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    background-color: #f6f8fa;
    color: #586884;
    font-size: 14px;
  }

  &__list {
    display: flex;
    height: 60px;

    &--type {
      width: 200px;
      display: flex;
      padding-left: 10px;
      justify-content: flex-start;
      align-items: center;
    }

    &--model {
      width: 300px;
      display: flex;
      padding-left: 10px;
      justify-content: flex-start;
      align-items: center;
    }

    &--link {
      width: 300px;
      display: flex;
      padding-left: 10px;
      justify-content: flex-start;
      align-items: center;
    }

    &--deal {
      width: 130px;
      display: flex;
      justify-content: space-around;
      align-items: center;

      &--noDel {
        color: #6eb5ff;
        cursor: pointer;
      }

      &--del {
        color: #ff5500;
        cursor: pointer;
      }
    }
  }
}

.digTitle {
  font-size: 15px;
  font-weight: bold;
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
}
</style>
