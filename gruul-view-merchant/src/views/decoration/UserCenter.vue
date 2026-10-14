<script setup lang="ts">
import type { MenuList } from '@/views/decoration/components/UserCenter/formModel/UserCenter'

import type { CustomStyle, OptionType, OrderInfo, OrderItem } from '@/views/decoration/interfaces/UserCenter'
import { getUserCenterConfig, setUserCenterConfig } from '@/api/decoration/decoration'
import DragMenu from '@/views/decoration/components/UserCenter/DragMenu.vue'
import UserCenter from '@/views/decoration/components/UserCenter/formModel/UserCenter'
import OrderDialog from '@/views/decoration/components/UserCenter/OrderDialog.vue'
import PhoneView from '@/views/decoration/components/UserCenter/PhoneView/PhoneView.vue'
import UploadFile from '@/views/decoration/components/UserCenter/UploadFile.vue'
import { Edit } from '@element-plus/icons-vue'

interface ISetVisible {
  [x: string]: boolean
}

// 定义emit
const emit = defineEmits(['userCenterChang'])

// 配置数据
const formData = reactive(new UserCenter())

// 折叠面板状态
const setVisible = reactive<ISetVisible>({
  /** 用户折叠面板 */
  setInfoVisible: true,
  /** 订单折叠面板 */
  setOrderVisible: false,
  /** 菜单折叠面板 */
  setMenuVisible: false,
})

// 订单设置弹窗
const orderVisible = ref(false)

// 自定义样式数据
const customStyle = reactive<CustomStyle>({
  /** 背景图片 */
  backgroundImage: '',
  /** 卡面颜色 */
  cardColor: '',
  /** 文字颜色 */
  textColor: '',
})

// 用户信息数据
const orderInfo = ref<OrderInfo>({
  /* orderInfo* 售后 */
  afterSaleIcon: {
    name: '',
    url: '',
  },
  /** 待付款 */
  waitIcon: {
    name: '',
    url: '',
  },
  waitDelivered: {
    name: '',
    url: '',
  },
  /** 待发货 */
  waitPickingIcon: {
    name: '',
    url: '',
  },
  /** 配送中 */
  deliveryIcon: {
    name: '',
    url: '',
  },
  /** 评价中心 */
  evaluateIcon: {
    name: '',
    url: '',
  },
})

// 展开式菜单数据
const expandMenu = ref<MenuList[]>([])

// 折叠式菜单数据
const foldMenu = ref<MenuList[]>([])

// 传入订单属性
const orderProp = reactive<OrderItem & OptionType>({
  key: '',
  name: '',
  url: '',
})

// 计算头部样式配置
const headStyleConfig = computed(() => {
  let config = {
    headBackGround: {},
    cardColor: '#45403C',
    textColor: '#E4CB98',
  }
  // 系统风格
  if (formData.headStyle === 1) {
    config.headBackGround = {
      'background-color': '#FE4E63',
    }
  }
  if (formData.headStyle === 2) {
    config = {
      headBackGround: {
        'background-image': `url(${customStyle.backgroundImage})`,
      },
      cardColor: customStyle.cardColor,
      textColor: customStyle.textColor,
    }
  }
  return config
})

onMounted(() => {
  getAllSetting()
})

onBeforeUnmount(() => {
  submit(1)
})

/**
 * @description: 接口获取所有配置数据
 */
async function getAllSetting() {
  const result = await getUserCenterConfig()
  const { code, data } = result
  if (code === 200) {
    Object.assign(formData, data)
    const orderInfoData = JSON.parse(formData.orderInfo)
    orderInfo.value = {
      ...orderInfo.value,
      ...orderInfoData,
      waitDelivered: {
        name: '待发货',
        url: orderInfoData.waitDelivered ? orderInfoData.waitDelivered.url : '',
      },
    }

    Object.assign(customStyle, JSON.parse(formData.customStyle))
    getFinalExpandMenu()
    getFinalFoldMenu()
  }
}

/**
 * @description: 设置最终列表菜单接口数据
 */
function getFinalExpandMenu() {
  const filteredMenus = formData.menuVos
    .filter((item) => {
      return item.styleType === 1
    })
    .map((mapItem) => {
      if (typeof mapItem.linkSelectItem === 'string') {
        mapItem.linkSelectItem = JSON.parse(mapItem.linkSelectItem)
      }
      return mapItem
    })

  expandMenu.value.length = 0
  expandMenu.value.push(...filteredMenus)
}

/**
 * @description: 设置最终折叠式菜单接口数据
 */
function getFinalFoldMenu() {
  const filteredMenus = formData.menuVos
    .filter((item) => {
      if (item.subMenu === null) {
        item.subMenu = []
      }
      return item.styleType === 2
    })
    .map((mapItem) => {
      if (typeof mapItem.linkSelectItem === 'string') {
        mapItem.linkSelectItem = JSON.parse(mapItem.linkSelectItem)
      }
      mapItem.subMenu =
        mapItem.subMenu?.map((subItem) => {
          if (typeof subItem.linkSelectItem === 'string') {
            subItem.linkSelectItem = JSON.parse(subItem.linkSelectItem)
          }
          return subItem
        }) || []
      return mapItem
    })

  foldMenu.value.length = 0
  foldMenu.value.push(...filteredMenus)
}

/**
 * @description: 配置项收起展开
 * @param {string} type
 */
function collapseVisibleHandle(type: string) {
  const map = ['Info', 'Order', 'Menu']
  map.forEach((name) => {
    setVisible[`set${name}Visible`] = type === name
  })
}

/**
 * @description: 显示订单编辑详情弹窗
 */
function orderClickHandle(key: string, value: { name: string; url: string }) {
  orderProp.key = key
  orderProp.name = value.name
  orderProp.url = value.url
  orderVisible.value = true
}

/**
 * @description: 关闭订单编辑详情弹窗
 * @param {*} orderItem
 */
function updateOrderHandle(orderItem: { key: string; name: string; url: string }) {
  orderInfo.value[orderItem.key as keyof OrderInfo] = {
    name: orderItem.name,
    url: orderItem.url,
  }
}

/**
 * @description: 提交
 * @param {number} nomessage
 */
async function submit(nomessage?: number) {
  formData.customStyle = JSON.stringify(customStyle)
  formData.orderInfo = JSON.stringify(orderInfo.value)
  const expandMenuCopy: MenuList[] = JSON.parse(JSON.stringify(expandMenu.value))
  const foldMenuCopy: MenuList[] = JSON.parse(JSON.stringify(foldMenu.value))

  expandMenuCopy.forEach((item: MenuList, index: number) => {
    item.sortIndex = index
    if (item.linkSelectItem instanceof Object) {
      item.linkSelectItem = JSON.stringify(item.linkSelectItem)
    }
  })

  foldMenuCopy.forEach((supMenu: MenuList) => {
    if (supMenu.linkSelectItem instanceof Object) {
      supMenu.linkSelectItem = JSON.stringify(supMenu.linkSelectItem)
    }
    if (supMenu.subMenu) {
      supMenu.subMenu.forEach((submenu, index) => {
        submenu.sortIndex = index
        if (submenu.linkSelectItem instanceof Object) {
          submenu.linkSelectItem = JSON.stringify(submenu.linkSelectItem)
        }
      })
    }
  })

  formData.menuVos = expandMenuCopy.concat(foldMenuCopy)
  try {
    const response = await setUserCenterConfig(formData)
    emit('userCenterChang', false)

    if (response.code === 200) {
      if (!nomessage) {
        ElMessage({
          message: '保存配置成功',
          type: 'success',
          duration: 500,
        })
      }
      await getAllSetting()
    }
  } catch (error) {
    console.error(error)
    await getAllSetting()
  }
}

defineExpose({
  submit,
})
</script>

<template>
  <div class="decoration__page">
    <div class="editor__preview_new">
      <PhoneView
        :custom-style="customStyle"
        :fold-menu="foldMenu"
        :head-style-config="headStyleConfig"
        :form-data="formData"
        :order-info="orderInfo"
        :expand-menu="expandMenu"
      />
    </div>
    <div class="editor__form_wrap">
      <div class="clearfix editor__form_header">
        <span>用户中心组件设置</span>
      </div>
      <div class="editor__form_wrap_main">
        <el-scrollbar style="height: 100%">
          <div class="pd20">
            <transition name="el-fade-in-linear" mode="out-in">
              <el-row class="setting">
                <el-form :model="formData" label-width="80px" class="w-full">
                  <div class="setting__collapse setinfo">
                    <div class="setting__collapse--header" @click="collapseVisibleHandle('Info')">
                      <span>用户信息</span>
                      <span style="color: #9797a1; padding-left: 40px" />
                    </div>
                    <div v-show="setVisible.setInfoVisible" class="setting__collapse--content" style="padding: 20px 0">
                      <el-form-item label="头部风格">
                        <el-radio-group v-model="formData.headStyle">
                          <el-radio :value="1"> 系统风格 </el-radio>
                          <el-radio :value="2"> 自定义风格 </el-radio>
                        </el-radio-group>
                      </el-form-item>
                      <el-row v-show="formData.headStyle === 2">
                        <el-form-item label="背景图片">
                          <UploadFile v-model:img-url="customStyle.backgroundImage" />
                          <div class="el-upload__tip" style="color: #9797a1">已投放会员卡：建议尺寸750px*443px</div>
                        </el-form-item>
                        <el-form-item label="卡面颜色">
                          <div class="divContant">
                            <span style="width: 76px">
                              {{ customStyle.cardColor }}
                            </span>
                            <el-color-picker v-model="customStyle.cardColor" class="color-picker" />
                          </div>
                        </el-form-item>
                        <el-form-item label="文字颜色">
                          <div class="divContant">
                            <span style="width: 76px">
                              {{ customStyle.textColor }}
                            </span>
                            <el-color-picker v-model="customStyle.textColor" class="color-picker" />
                          </div>
                        </el-form-item>
                      </el-row>
                      <el-form-item label="领卡文案">
                        <el-input v-model="formData.getCartText" maxlength="10" show-word-limit style="width: 200px" />
                      </el-form-item>
                      <el-form-item>
                        <el-checkbox v-model="formData.hideCartInlet"> 非会员显示领卡入口 </el-checkbox>
                      </el-form-item>
                    </div>
                  </div>
                  <div class="setting__collapse setorder">
                    <div class="setting__collapse--header" @click="collapseVisibleHandle('Order')">订单信息</div>
                    <div v-if="setVisible.setOrderVisible" class="setting__collapse--content">
                      <div class="setorder__item">
                        <span class="setorder__item--text">
                          {{ orderInfo.waitIcon.name }}
                        </span>
                        <el-icon class="el-collapse-item__arrow setorder__item--icon" @click="orderClickHandle('waitIcon', orderInfo.waitIcon)">
                          <Edit />
                        </el-icon>
                      </div>
                      <div class="setorder__item">
                        <span class="setorder__item--text">
                          {{ orderInfo.waitDelivered.name }}
                        </span>
                        <el-icon
                          class="el-collapse-item__arrow setorder__item--icon"
                          @click="orderClickHandle('waitDelivered', orderInfo.waitDelivered)"
                        >
                          <Edit />
                        </el-icon>
                      </div>
                      <div class="setorder__item">
                        <span class="setorder__item--text">
                          {{ orderInfo.waitPickingIcon.name }}
                        </span>
                        <el-icon
                          class="el-collapse-item__arrow setorder__item--icon"
                          @click="orderClickHandle('waitPickingIcon', orderInfo.waitPickingIcon)"
                        >
                          <Edit />
                        </el-icon>
                      </div>
                      <div class="setorder__item">
                        <span class="setorder__item--text">
                          {{ orderInfo.afterSaleIcon.name }}
                        </span>
                        <el-icon
                          class="el-collapse-item__arrow setorder__item--icon"
                          @click="orderClickHandle('afterSaleIcon', orderInfo.afterSaleIcon)"
                        >
                          <Edit />
                        </el-icon>
                      </div>
                      <div class="setorder__item">
                        <span class="setorder__item--text">
                          {{ orderInfo.evaluateIcon.name }}
                        </span>
                        <el-icon
                          class="el-collapse-item__arrow setorder__item--icon"
                          @click="orderClickHandle('evaluateIcon', orderInfo.evaluateIcon)"
                        >
                          <Edit />
                        </el-icon>
                      </div>
                    </div>
                  </div>
                  <div class="setting__collapse setmenu">
                    <div class="setting__collapse--header" @click="collapseVisibleHandle('Menu')">菜单</div>
                    <div v-if="setVisible.setMenuVisible" class="setting__collapse--content">
                      <div class="menu__form">
                        <div class="menu__form--item">
                          <label class="el-form-item__label">选择样式:</label>
                          <el-radio-group v-model="formData.menuStyle">
                            <el-radio :value="1"> 列表式 </el-radio>
                            <el-radio :value="2"> 九宫格 </el-radio>
                          </el-radio-group>
                        </div>
                        <div class="menu__form--item" style="padding-top: 30px">
                          <label class="el-form-item__label">设置菜单:</label>
                          <span style="color: #9797a1">长按点击可拖拽调整顺序</span>
                        </div>
                      </div>

                      <!-- 列表式 -->
                      <DragMenu v-if="formData.menuStyle === 1" v-model:menu-list="expandMenu" :menu-style="1"> </DragMenu>

                      <!-- 九宫格 -->
                      <DragMenu v-if="formData.menuStyle === 2" v-model:menu-list="foldMenu" :split-flag="false" :menu-style="2"> </DragMenu>
                    </div>
                  </div>
                </el-form>
              </el-row>
            </transition>
          </div>
        </el-scrollbar>
      </div>
    </div>
    <OrderDialog v-model:order-visible="orderVisible" :order-prop="orderProp" @update-order="updateOrderHandle" />
  </div>
</template>

<style lang="scss" scoped>
@import '../../assets/styles/decoration/userCenter';
@import '@/assets/styles/decoration/editorPage';

.decoration__page {
  padding-bottom: 10px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-around;
  width: 100%;
  min-width: 1280px;
}

#editor__component_position {
  width: 300px;
  height: calc(100vh - 70px);
  overflow: auto;
  color: #333;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  padding-bottom: 50px;
}

.setting {
  width: 100%;
  overflow: initial;
}

.decoration__page .usercenter {
  margin-top: 0;
}

.editor__preview_new {
  width: 375px;
  height: calc(100vh - 80px) !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow:
    0 2px 4px 0 rgba(0, 0, 0, 0.12),
    0 0 6px 0 rgba(0, 0, 0, 0.04);
  overflow: scroll;
}

.editor__form_wrap {
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
  padding: 0;
  background-color: #fff;
  z-index: 4;
  width: 405px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding-bottom: 0 !important;

  .editor__form_wrap_main {
    width: 100%;
    flex: 1;
    overflow: hidden;
  }

  .editor__form_header {
    padding: 18px 20px;
    border-bottom: 1px solid #dcdfe6;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
  }

  .pd20 {
    padding: 20px 20px 0 20px;
  }
}
</style>
