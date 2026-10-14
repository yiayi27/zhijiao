<script setup lang="ts">
import { delCompony, getLogAddress, openLogAddress } from '@/api/logistics/logistics'
import config from '@/config'
import { ElMessageBox } from 'element-plus'

import AddModel from './common/AddModel.vue'

// 定义类型
interface LogisticsAddress {
  cid: string
  customerName: string
  customerPassword: string
  linkName: string
  linkTel: string
  name: string
  phone: string
  province: string
  city: string
  country: string
  address: string
}

interface LogisticsCompany {
  name: string
  code: string
  phone: string
  id: string
  number: string
  addressList: LogisticsAddress[]
  linkName: string
  customerName: string
  customerPassword: string
  linkTel: string
}

// 组件引用
const addModelRef = ref<InstanceType<typeof AddModel>>()

// 响应式数据
const logCompany = reactive<LogisticsCompany[]>([
  {
    name: '顺丰快递',
    code: 'sf',
    phone: '95546',
    id: '',
    number: '',
    addressList: [],
    linkName: '',
    customerName: '',
    customerPassword: '',
    linkTel: '',
  },
  {
    name: '中通快递',
    phone: '',
    code: 'zt',
    id: '',
    number: '',
    addressList: [],
    linkTel: '',
    linkName: '',
    customerName: '',
    customerPassword: '',
  },
  {
    name: '圆通快递',
    phone: '',
    code: 'yt',
    id: '',
    number: '',
    addressList: [],
    linkTel: '',
    linkName: '',
    customerName: '',
    customerPassword: '',
  },
  {
    name: '韵达快递',
    phone: '',
    code: 'yd',
    id: '',
    number: '',
    addressList: [],
    linkTel: '',
    linkName: '',
    customerName: '',
    customerPassword: '',
  },
  {
    name: '百世快递',
    phone: '',
    code: 'ht',
    id: '',
    number: '',
    addressList: [],
    linkTel: '',
    linkName: '',
    customerName: '',
    customerPassword: '',
  },
])

const imgList = ref(config.logisticsImageList)

const imgIndex = ref(0)
const addDialog = ref(false)
const editCurrent = ref<Record<string, any>>({})
const showModel = ref(false)
const chooseCompany = reactive({
  name: '',
  code: '',
})

interface LogisticsItem {
  id: string
  code: string
  logisticsAddressVos: LogisticsAddress[]
  customerName: string
  customerPassword: string
  linkName: string
  linkTel: string
}

/**
 * 获取已开通物流公司信息
 */
function getList() {
  getLogAddress({}).then((res) => {
    res.data.forEach((item: LogisticsItem) => {
      item.logisticsAddressVos.forEach((logItem: LogisticsAddress) => {
        logItem.cid = item.id
        logItem.customerName = item.customerName
        logItem.customerPassword = item.customerPassword
        logItem.linkName = item.linkName
        logItem.linkTel = item.linkTel
      })
    })
    logCompany.forEach((comItem) => {
      comItem.addressList = []
      res.data.forEach((resItem: LogisticsItem) => {
        if (comItem.code === resItem.code) {
          comItem.addressList = comItem.addressList.concat(resItem.logisticsAddressVos)
        }
      })
    })
  })
}

/**
 * 根据选择的服务上新增模板
 */
function addAddress(item: LogisticsCompany) {
  addDialog.value = true
  editCurrent.value = {}
  chooseCompany.name = item.name
  chooseCompany.code = item.code
}

/**
 * 开通物流公司
 */
function openLogistic() {
  if (!addModelRef.value) return

  if (addModelRef.value.serveOption.code !== 'sf') {
    if (!addModelRef.value.serveOption.customerName || !addModelRef.value.serveOption.customerPassword) {
      ElMessage.warning('请完善页面内容')
      return
    }
  }
  if (
    !addModelRef.value.serveOption.linkName ||
    !addModelRef.value.serveOption.linkTel ||
    !addModelRef.value.serveOption.name ||
    !addModelRef.value.serveOption.addressId
  ) {
    ElMessage.warning('请完善页面内容')
    return
  }
  addModelRef.value.serveOption.code = chooseCompany.code
  addModelRef.value.serveOption.name = chooseCompany.name
  openLogAddress(addModelRef.value.serveOption).then(() => {
    ElMessage.success('开通成功,请等待审核通过')
    addDialog.value = false
    getList()
  })
}

/**
 * 编辑物流公司
 */
function editCompany(item: LogisticsCompany, v: LogisticsAddress) {
  addDialog.value = true
  editCurrent.value = item
  editCurrent.value.getAddress = v
  chooseCompany.name = item.name
  chooseCompany.code = item.code
}

/**
 * 删除选中物流公司
 */
function delCompany(cid: string) {
  ElMessageBox.confirm('确定删除此发货地址吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    delCompony(cid, {}).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

/**
 * 关闭新增弹窗
 */
function handleClose() {
  addDialog.value = false
}

/**
 * 查看不同物流电子面单
 */
function showLogistc(item: LogisticsCompany) {
  showModel.value = true
  switch (item.code) {
    case 'sf':
      imgIndex.value = 0
      break
    case 'zt':
      imgIndex.value = 1
      break
    case 'yt':
      imgIndex.value = 2
      break
    case 'yd':
      imgIndex.value = 3
      break
    case 'ht':
      imgIndex.value = 4
      break
  }
}

/**
 * 关闭模板弹窗
 */
function handleModeClose() {
  showModel.value = false
}

// 生命周期钩子
onMounted(() => {
  getList()
})
</script>

<template>
  <div class="flex-1 overflow-auto">
    <div class="serveTop">
      <div style="width: 15%" class="center">服务商名称（编号）</div>
      <div style="width: 15%" class="center">面单数量</div>
      <div style="width: 40%" class="center">发货地址</div>
      <div style="width: 15%" class="center">状态</div>
      <div style="width: 15%" class="center">操作</div>
    </div>
    <div v-for="(item, index) in logCompany" :key="index" style="margin-top: 10px">
      <div class="comPanyList">
        <div>
          {{ item.name }}
          <span style="margin-left: 10px; color: #0e76ff">{{ item.phone }}</span>
          <span style="margin-left: 40px; cursor: pointer" @click="showLogistc(item)">查看模板</span>
        </div>
        <div style="color: #0e76ff; cursor: pointer" @click="addAddress(item)">新增地址</div>
      </div>
      <div v-if="item.addressList.length > 0" class="comPanyAddress">
        <div class="comPanyAddress__logName">
          {{ item.name }}
        </div>
        <div class="comPanyAddress__logName">{{ item.number }}123</div>
        <div class="comPanyAddress__addList">
          <div v-for="(v, i) in item.addressList" :key="i" style="display: flex">
            <div class="comPanyAddress__addList--content">
              <div style="width: 400px">负责人：{{ v.linkName }}({{ v.linkTel }})</div>
              <div style="width: 400px">发货信息：{{ v.name }}({{ v.phone }})</div>
              <div style="width: 400px">{{ v.province }}{{ v.city }}{{ v.country }}{{ v.address }}</div>
            </div>
            <div class="comPanyAddress__addList--apply">审核通过</div>
            <div class="comPanyAddress__addList--deal">
              <div class="comPanyAddress__addList--deal--way">
                <div @click="editCompany(item, v)">修改</div>
                <div style="margin-top: 10px" @click="delCompany(v.cid)">删除</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="comPanyApply" @click="addAddress(item)">申请开通服务</div>
    </div>
    <el-dialog v-model="showModel" width="500px" :before-close="handleModeClose">
      <template #header>
        <div class="digTitle" style="color: black">面单预览</div>
      </template>
      <img :src="imgList[imgIndex]" style="width: 100%; height: 100%" />
    </el-dialog>
    <el-dialog v-model="addDialog" width="600px" :before-close="handleClose">
      <template #header>
        <div class="digTitle">开通服务商</div>
      </template>
      <AddModel ref="addModelRef" :edit-current="editCurrent" :add-dialog="addDialog" :choose-company="chooseCompany" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="openLogistic">开通</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.serveTop {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  background-color: #f6f8fa;
  color: #586884;
  font-size: 14px;
}

.center {
  display: flex;
  justify-content: center;
}

.comPanyList {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px;
  background-color: #f6f8fa;
  padding: 0px 20px;
  font-size: 12px;
  color: #0e76ff;
  border: 1px solid #cccccc;
}

.comPanyAddress {
  border: 1px solid #cccccc;
  display: flex;
  border-top: none;
  border-bottom: none;
  &__logName {
    width: 15%;
    display: flex;
    align-items: center;
    justify-content: center;
    border-right: 1px solid #cccccc;
    border-bottom: 1px solid #cccccc;
  }
  &__addList {
    width: 40%;
    display: flex;
    flex-wrap: wrap;
    &--content {
      width: 400px;
      height: 100px;
      border-bottom: 1px solid #cccccc;
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      padding: 20px;
      border-right: 1px solid #cccccc;
    }

    &--apply {
      width: 150px;
      border-bottom: 1px solid #cccccc;
      display: flex;
      align-items: center;
      justify-content: center;
      border-right: 1px solid #cccccc;
    }

    &--deal {
      width: 136px;
      border-bottom: 1px solid #cccccc;
      display: flex;
      align-items: center;
      justify-content: center;
      &--way {
        cursor: pointer;
        color: #006eff;
        width: 30px;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
      }
    }
  }
}

.comPanyApply {
  width: 120px;
  height: 30px;
  border-radius: 5px;
  color: #006eff;
  border: 1px solid #006eff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
  cursor: pointer;
}

.digTitle {
  font-size: 17px;
  font-weight: bold;
}
</style>
