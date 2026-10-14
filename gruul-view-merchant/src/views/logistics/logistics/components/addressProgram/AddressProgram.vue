<script setup lang="ts">
import { addShopAddress, delShopAddress, getShopAddress, setShopAddress } from '@/api/logistics/logistics'
import MContainer from '@/components/MContainer/MContainer.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessageBox } from 'element-plus'

import AddModel from './edit/AddModel.vue'

// 定义地址项类型接口
interface AddressItem {
  id: string
  name: string
  province: string
  city: string
  country: string
  address: string
  zipCode: string
  phone: string
  defSend: number
  defReceive: number
  isShow?: boolean
}

// 引用子组件
const addModelRef = ref()

// 地址管理
const addList = ref<AddressItem[]>([])

// 是否有list
const hasList = ref(false)

// 是否添加状态
const isAdd = ref(false)

// 添加弹窗
const addDialog = ref(false)

// 编辑内容
const currentItem = ref<AddressItem | Record<string, any>>({})

// 分页信息
const query = reactive({
  current: 1,
  size: 10,
  total: 0,
})

/**
 * 获取地址管理数据
 */
function getList() {
  getShopAddress(query).then((res) => {
    res.data.list.forEach((item: any) => {
      item.isShow = true
    })
    addList.value = res.data.list
    hasList.value = res.data.list.length === 0
    query.total = res.data.total
  })
}

function addNew() {
  addDialog.value = true
  currentItem.value = {}
}

/**
 * 添加地址
 */
function sureAdd() {
  const passFlag = saveAddress(addModelRef.value)
  if (!passFlag) {
    return
  }
  addModelRef.value.addressOption.countryId = addModelRef.value.countryId
  addShopAddress(addModelRef.value.addressOption).then(() => {
    ElMessage.success(`${addModelRef.value.addressOption.id ? '修改' : ' 新增'}成功`)
    getList()
    addDialog.value = false
    currentItem.value = {}
  })
}

function handleClose() {
  currentItem.value = {}
  addDialog.value = false
}

/**
 * 保存地址
 */
function saveAddress(addModel: any) {
  const postReg = /^\d{6}$/
  const phoneReg = /^1([3-9])\d{9}$/
  const passFlag = true

  if (!addModel.addressOption.name) {
    ElMessage.error('请输入联系人')
    return false
  }

  if (!phoneReg.test(addModel.addressOption.phone)) {
    ElMessage.error('请输入正确手机号码')
    return false
  }

  if (!postReg.test(addModel.addressOption.zipCode)) {
    ElMessage.error('请输入正确邮政编码')
    return false
  }

  if (!addModel.addressOption.cityId || !addModel.countryId || !addModel.addressOption.provinceId) {
    ElMessage.error('请输入地区信息')
    return false
  }

  if (!addModel.addressOption.address) {
    ElMessage.error('请输入详细地址')
    return false
  }
  return passFlag
}

/**
 * 设置收货退货地址
 */
function setSendArea(item: AddressItem, type: number) {
  setShopAddress(item.id, type, {}).then(() => {
    ElMessage.success('设置成功')
    getList()
  })
}

/**
 * 编辑地址
 */
function editAddress(item: AddressItem) {
  currentItem.value = item
  addDialog.value = true
}

/**
 * 删除地址
 */
function delAddress(item: AddressItem) {
  ElMessageBox.confirm('确定删除此条地址？删除后不会保留已删除地址?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delShopAddress(item.id, {}).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
function handleSizeChange(val: number) {
  query.size = val
  getList()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
function handleCurrentChange(val: number) {
  query.current = val
  getList()
}

// 生命周期函数
onMounted(() => {
  getList()
})
</script>

<template>
  <MContainer>
    <template #section>
      <el-button type="primary" @click="addNew"> 添加地址 </el-button>
    </template>

    <template #table>
      <div class="addressBox">
        <div class="addressBox__top">
          <span style="width: 15%">联系人</span>
          <span style="width: 31%">地址</span>
          <span style="width: 14%">邮政编码</span>
          <span style="width: 18%">联系电话</span>
          <span style="width: 20%">默认设置</span>
          <span style="width: 10%">操作</span>
        </div>
        <div v-if="addList.length !== 0">
          <div
            v-for="(item, index) in addList"
            :key="index"
            class="addressBox__content"
            :style="{ 'background-color': index % 2 === 0 ? '#F2F2F6' : '' }"
          >
            <div style="width: 20%; margin-right: 20px">
              <div>{{ item.name }}</div>
            </div>
            <div style="width: 60%; margin-right: 20px">
              <div>{{ item.province }}{{ item.city }}{{ item.country }}{{ item.address }}</div>
            </div>
            <div style="width: 20%; margin-right: 20px">
              <div>{{ item.zipCode }}</div>
            </div>
            <div style="width: 30%; margin-right: 20px">
              <div>{{ item.phone }}</div>
            </div>
            <div :style="{ width: isAdd ? '20%' : '30%' }">
              <div v-if="!isAdd" style="display: flex; width: 80px; flex-wrap: wrap">
                <div
                  class="addressBox__content--botton"
                  :style="{
                    'background-color': item.defSend === 1 ? '#2D8CF0' : '#FFFFFF',
                    color: item.defSend === 1 ? 'white' : 'black',
                  }"
                  @click="setSendArea(item, 1)"
                >
                  发货地址
                </div>
                <div
                  class="addressBox__content--botton"
                  style="margin-top: 5px"
                  :style="{
                    'background-color': item.defReceive === 1 ? '#2D8CF0' : '#FFFFFF',
                    color: item.defReceive === 1 ? 'white' : 'black',
                  }"
                  @click="setSendArea(item, 2)"
                >
                  退货地址
                </div>
              </div>
            </div>
            <div class="doDeal">
              <div style="width: 20%; color: #409eff; margin-right: 10px; cursor: pointer" @click="editAddress(item)">编辑</div>
              <div style="width: 20%; color: rgb(250, 100, 101); cursor: pointer" @click="delAddress(item)">删除</div>
            </div>
          </div>
        </div>
        <div v-if="hasList" class="emptyLine">暂无数据~</div>
      </div>
    </template>

    <template #pagination>
      <PageManage
        :page-size="query.size"
        :page-num="query.current"
        :total="query.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </template>

    <el-dialog v-model="addDialog" width="580px" :before-close="handleClose">
      <template #header>
        <div class="digTitle">{{ currentItem.id ? '编辑' : '新增' }}地址</div>
      </template>
      <AddModel ref="addModelRef" :current-item="currentItem" :add-dialog="addDialog" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="sureAdd">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </MContainer>
</template>

<style lang="scss" scoped>
.addressBox {
  width: 100%;
  border: 1px solid #c9c9c9;
  color: #595961;
  border-radius: 5px;

  &__top {
    display: flex;
    padding: 10px 10px 10px 40px;
    font-size: 12px;
  }

  &__content {
    display: flex;
    padding: 10px 10px 10px 40px;
    border-top: 1px solid #c9c9c9;
    height: 80px;
    align-items: center;
    font-size: 12px;

    &--botton {
      width: 80px;
      height: 24px;
      display: flex;
      border-radius: 5px;
      justify-content: center;
      align-items: center;
      border: 1px solid #c9c9c9;
      cursor: pointer;
    }
  }

  &__addButton {
    display: flex;
    padding: 10px 10px 10px 40px;
    height: 80px;
    align-items: center;
  }
}

.doDeal {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30%;
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
  margin-top: 15px;
  margin-left: 0;
  font-size: 14px;
  color: #b3b3b3;
  border-top: 1px solid #ebeef5;
}
</style>
