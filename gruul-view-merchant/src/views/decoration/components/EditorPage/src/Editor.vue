<script lang="ts" setup>
import type { IComponent } from './interfaces/component'
import { addControl, addControlMore, getControl } from '@/api/decoration/decoration'
import { addAssembly, getAssembly } from '@/api/group/group'
import { useDecorationStore } from '@/store/modules/decoration'
import UserCenter from '@/views/decoration/UserCenter.vue'
import { ElMessage } from 'element-plus'
import { storeToRefs } from 'pinia'

import EditorComponent from './EditorComponent.vue'
import EditorControl from './EditorControl.vue'
import EditorControlPage from './EditorControlPage.vue'
import EditorForm from './EditorForm.vue'
import EditorLinkAddress from './EditorLinkAddress.vue'
import EditorPreview from './EditorPreview.vue'
import EditorTab from './EditorTab.vue'

interface Assembly {
  properties: string
  pageId: string
  modelId: string | null
}

interface ControlItem {
  id?: number
  pluginProperties: string
  pluginNameCn?: string
  pluginNameEn?: string
  [key: string]: any
}

interface NavBarItem {
  linkUrl?: string
  getFrom?: string
  isHome?: boolean
  [key: string]: any
}

interface PageItem {
  id: string | number
  name?: string
  isHome?: boolean
  components?: IComponent[]
  modelId?: string | number
  isDef?: string | number
  pageName?: string
  type?: string
  [key: string]: any
}

defineOptions({
  name: 'EditorPage',
})

const decorationStore = useDecorationStore()
const { activePage, activePageType, isUsercenterCompontents, saleMode, userCenterType, getFrom } = storeToRefs(decorationStore)

// 组件状态
const currentComponent = ref<IComponent | null>(null)
const components = ref<IComponent[]>([])
const components2 = ref<IComponent[]>([])
const currentTab = ref(0)
const pageId = ref<string>('')
const assembleIds = ref<string[]>([])
const pageType = ref(0)
const isSaveTip = ref(false)
const controlItem = ref<ControlItem | null>(null)
const controlIndex = ref(0)
// ref引用
const EditorPreviewRef = ref<InstanceType<typeof EditorPreview> | null>(null)
const editorControlPageRef = ref<InstanceType<typeof EditorControlPage> | null>(null)
const usercenter = ref<InstanceType<typeof UserCenter> | null>(null)

watch(currentTab, () => {
  if (currentTab.value === 2) {
    // 获取页面中营销页面数据
    editorControlPageRef.value?.getMarketingPage()
  }
  setCurrentComponent(null)
})

watch(saleMode, () => {
  const activePageValue = activePage.value
  getPageAssembly(activePageValue?.id) //  获取页面数据
})

watch(activePage, selectPage)

// 方法
function init(): void {
  console.log(1)
  components.value = []
  console.log('components', components.value)
  currentComponent.value = null
  pageId.value = ''
  onGetControl()
}

async function onGetControl(): Promise<void> {
  const data: ControlItem[] = [
    {
      id: 12,
      pluginProperties: JSON.stringify(components2.value),
      pluginNameCn: '底部导航',
      pluginNameEn: 'navBar',
    },
  ]

  const res = await getControl({})
  if (res.data.length === 0) {
    onAddControlMore(data)
  }
}

async function onAddControlMore(data: ControlItem[]): Promise<void> {
  const res = await addControlMore(data)
  if (res.code === 200) {
    ElMessage.success(`操作成功！`)
  }
}

async function submit(nomessage?: any): Promise<void> {
  isSaveTip.value = true
  if (isUsercenterCompontents.value) {
    return usercenter.value?.submit()
  }

  // 格式话保存数据
  const data = formateComponents() || []
  console.log('data1', data)
  if (activePageType.value === 'bussiness') {
    // 营销页面不保存
    isSaveTip.value = false
    return
  }
  if (data.length && isEmpetyCubox(data).length) {
    isSaveTip.value = false
    return
  }
  if (data.length && !isNavLength4(data)) {
    return
  }
  addNewPage(data, 2, nomessage)
}

function formateComponents(): Assembly[] {
  const data: Assembly[] = []
  console.log('components==', components.value)
  components.value.forEach((item) => {
    const properties = JSON.stringify({ ...item })
    const assembly = {
      properties,
      pageId: pageId.value,
      modelId: saleMode.value ? saleMode.value : null,
    }
    data.push(assembly)
  })
  return data
}

function isEmpetyCubox(coms: Assembly[]): Array<{ img?: string }> {
  for (let index = 0; index < coms.length; index++) {
    const item = coms[index]
    if (item.properties) {
      try {
        const properties = JSON.parse(item.properties)
        if (properties.value === 'CubeBox' && properties.formData?.subEntry?.length) {
          const arr = properties.formData.subEntry.filter((subItem: { img?: string }) => !subItem.img)
          if (arr.length) {
            const result = arr
            ElMessage.warning('该页面使用了魔方组建,蓝色区域请添加完整的图片')
            const el = document.querySelector(`#editor-preview-com-${index}`)
            if (el instanceof HTMLElement) {
              el.click()
            }
            return result
          }
        }
      } catch (error) {
        console.error('Error parsing component properties:', error)
      }
    }
  }
  return []
}

function isNavLength4(coms: Assembly[]): boolean {
  let flag = true
  for (let index = 0; index < coms.length; index++) {
    const item = coms[index]
    if (item.properties) {
      try {
        const properties = JSON.parse(item.properties)
        if (properties.value === 'StoreNavigation' && properties.formData?.storeNavigations?.length < 4) {
          ElMessage.warning('店铺导航最少添加4个')
          flag = false
          break
        }
      } catch (error) {
        console.error('Error parsing component properties:', error)
      }
    }
  }
  return flag
}

async function addOrEditControl(isdefaultTemp: boolean, type: number, controlList: ControlItem[]): Promise<void> {
  if (!controlItem.value) return

  const data = {
    ...controlItem.value,
    pluginProperties: JSON.stringify(components2.value),
  }
  if (isdefaultTemp) {
    await saveDefaultTemplate({ controlIndex: controlIndex.value, controlList, type })
  } else {
    if (data.pluginNameEn === 'navBar' && data.pluginProperties !== '[]') {
      try {
        // 始终保持底部导航第一位置为首页 路径为 /pages/index/index
        const pluginProperties = JSON.parse(data.pluginProperties)
        if (Array.isArray(pluginProperties) && pluginProperties[0]?.formData?.menuList) {
          pluginProperties[0].formData.menuList.forEach((item: NavBarItem) => (item.getFrom = getFrom.value))
          if (pluginProperties[0].formData.menuList[0]) {
            pluginProperties[0].formData.menuList[0].linkUrl = '/pages/index/index'
          }
          data.pluginProperties = JSON.stringify(pluginProperties)
        }
      } catch (error) {
        console.error('Error parsing plugin properties:', error)
      }
    }

    const res = await addControl(data)
    if (res.code === 200) {
      ElMessage.success(`${type === 1 ? '新增' : '修改'}控件成功`)
    }
  }
}

async function saveDefaultTemplate({
  controlIndex,
  controlList,
  type,
}: {
  controlIndex: number
  controlList: ControlItem[]
  type: number
}): Promise<void> {
  if (controlList[controlIndex]) {
    controlList[controlIndex].pluginProperties = JSON.stringify(components2.value)
    const res = await addControlMore(controlList)
    if (res.code === 200) {
      ElMessage.success(`${type === 1 ? '新增' : '修改'}控件成功`)
    }
  }
}

function saveControlId({ item, index }: { item: ControlItem; index: number }): void {
  decorationStore.setLinkForm('bottomNav')
  controlItem.value = item
  controlIndex.value = index
}

function submit2(): void {
  addOrEditControl(false, 2, [])
}

async function addNewPage(data: Assembly[], type: number, nomessage?: any): Promise<boolean | void> {
  console.log('data2', data)
  // 说明点击保存
  if (typeof nomessage === 'object') {
    nomessage = 0
  }
  if (!data.length && !nomessage) {
    ElMessage.warning(`当前页面组件为空，请添加组件进行保存`)
    return false
  }
  if (!data.length) {
    isSaveTip.value = false
    return false
  } else if (!checkClassPage(JSON.parse(data[0].properties || '{}'))) {
    // 分类页校验输入是否完成
    isSaveTip.value = false
    return false
  }
  console.log('data3', data)
  const res = await addAssembly(data)
  isSaveTip.value = false
  if (res.code === 200 && !nomessage) {
    ElMessage.success(`${type === 1 ? '新增' : '修改'}页面成功`)
  }
  return true
}

function checkClassPage(data: { value?: string; formData?: any }): boolean {
  let flag = true
  if (['SpellPage', 'BusinessSuper'].includes(data.value || '')) {
    const formData = data.formData || {}
    const { firstCatList = [], selectMode, selectType } = formData
    let errMsg = ''
    if (selectMode === 2 && selectType === 2) {
      firstCatList.forEach((i: { category?: { name: string }; classChart?: any[] }) => {
        if (!i.classChart || !i.classChart.length) {
          errMsg = `${i.category?.name}一级分类下未添加分类图，请添加！`
          flag = false
        }
      })
    }
    if (!flag) {
      ElMessage.error(errMsg)
    }
  }
  return flag
}

function addComponents(com: IComponent, index?: number): void {
  console.log(com)
  console.log(index)
  console.log('===============')
  console.log(components.value)
  if (components.value.length > 0 && ['SpellPage', 'BusinessSuper'].includes(components.value[0].value)) {
    console.log(2)
    components.value = components.value.splice(0, 1)
    console.log('components', components.value)
    if (com.type) {
      return
    } else {
      ElMessage.error('分类页不能添加组件！')
      return
    }
  }
  if (activePageType.value === 'bussiness') {
    ElMessage.warning('营销页面无法添加组件')
    return
  } else {
    currentComponent.value = com
    const i = components.value.length - 1
    EditorPreviewRef.value?.setCurrentFlag(i)
  }
}

function addComponents2(com: IComponent): void {
  components2.value = []
  components2.value.push(com)
  currentComponent.value = com
}

function selectPage(item: PageItem | null): boolean | void {
  if (isUsercenterCompontents.value) {
    return
  }
  if (item) {
    if (activePageType.value === 'bussiness') {
      console.log(3)
      components.value = item.components || []
      console.log('components', components.value)
      currentComponent.value = null
      isSaveTip.value = false
      return false
    }

    // 获取对象为空
    item = activePage.value
    const id = item?.id
    pageType.value = 0
    pageId.value = String(id || '')
    currentComponent.value = null
    getPageAssembly(id) //  获取页面数据
  } else {
    isSaveTip.value = false
    console.log(4)
    components.value = []
    console.log('components', components.value)
  }
}

async function getPageAssembly(id: string | number | undefined): Promise<void> {
  if (!id) return

  const res = await getAssembly({
    pageId: id,
    modelId: saleMode.value ? saleMode.value : null,
  })
  isSaveTip.value = false
  if (res.code === 200) {
    const newList: IComponent[] = []
    assembleIds.value = []
    res.data.forEach((item: { properties: string; id: string }) => {
      try {
        newList.push(JSON.parse(item.properties))
        assembleIds.value.push(item.id)
      } catch (error) {
        console.error('Error parsing assembly properties:', error)
      }
    })
    console.log(5)
    components.value = newList
    console.log('components', components.value)
  }
}

function setCurrentComponent(com: IComponent | null): void {
  if (!com) {
    currentComponent.value = null
    return
  }

  // editorControlPage.value?.getMarketingPage();
  const saleModeValue = saleMode.value
  if (saleModeValue && Number(saleModeValue) > 0 && editorControlPageRef.value) {
    const marketingPage = editorControlPageRef.value.marketingPage || []
    const selectItem = marketingPage.filter((item: any) => item.id === saleModeValue)
    if (selectItem && selectItem.length > 0) {
      com.label = selectItem[0].modeName
    }
  }
  currentComponent.value = com
}

function selectTab(tab: number): void {
  currentTab.value = tab
}

function changeFncMarkPage(): void {
  pageType.value = 1
}

function userCenterChang(e: boolean): void {
  isSaveTip.value = e
}

// 生命周期钩子
onUnmounted(() => {
  decorationStore.setFirstStatus(false)
})

// 暴露给父组件的方法
defineExpose({
  init,
  submit,
})
</script>

<template>
  <!-- 入口页面 -->
  <div class="editorWrapper">
    <!-- 头部编辑保存部分Start -->
    <div class="wraper_top flex justify-between">
      <div class="wraper_top--left flex">
        <router-link to="/overview/index">
          <div class="editor_tab_title_top">
            <span class="editor_tab_icon1" />
            <div>店铺后台</div>
          </div>
        </router-link>
        <div class="editor_tab_title_top no-l-b">
          <span id="savetip">
            {{ !isSaveTip ? '所有更改已保存' : '正在保存...' }}
          </span>
        </div>
      </div>

      <div class="dec_footer">
        <el-button v-show="[0, 2].includes(currentTab)" type="primary" @click="submit"> 保存 </el-button>
        <el-button v-show="currentTab === 1" :disabled="!controlItem" type="primary" @click="submit2"> 保存控件 </el-button>
      </div>
    </div>
    <div class="editorPage">
      <div class="editorPage_left">
        <EditorTab @change="selectTab" />
        <div class="editor_view">
          <!-- 组件列表 -->
          <EditorComponent v-if="currentTab === 0" @change="addComponents" />
          <!-- 控件列表 -->
          <EditorControl v-if="currentTab === 1" @change="addComponents2" @save-control-id="saveControlId" />
          <!-- 页面列表 -->
          <EditorControlPage
            v-show="currentTab === 2"
            ref="editorControlPageRef"
            :active-page-id="activePage ? activePage.modelId : ''"
            @change="addComponents"
            @change-page="selectPage"
            @change-fnc-mark-page="changeFncMarkPage"
          />
        </div>
      </div>
      <div class="editorPage_main">
        <div class="tab_con">
          <!-- 个人中心 -->
          <UserCenter
            v-if="currentTab === 2 && isUsercenterCompontents && userCenterType === 'usercenter'"
            ref="usercenter"
            @importr-center-chang="userCenterChang"
          />
          <!-- 组件以及页面预览 -->
          <EditorPreview
            v-show="!isUsercenterCompontents && (currentTab === 0 || currentTab === 2)"
            ref="EditorPreviewRef"
            v-model:components="components"
            @addchange="addComponents"
            @change="setCurrentComponent"
          />
          <!-- 控件预览 -->
          <EditorPreview v-show="currentTab === 1" v-model:components="components2" @change="setCurrentComponent" />
        </div>
      </div>
      <div :style="!currentComponent ? 'display:none;' : ''" class="editorPage_right">
        <div class="editorPage_right_wrap">
          <!-- 样式编辑 -->
          <EditorForm v-show="!isUsercenterCompontents && pageType === 0" :current-component="currentComponent" />
          <!-- 编辑链接地址 -->
          <EditorLinkAddress v-show="!isUsercenterCompontents && pageType === 1" />
          <!-- End -->
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scope>
@import '@/assets/styles/decoration/editorPage';

#editor__preview_position {
  position: relative;
  padding-bottom: 15px;
  overflow: auto;
  width: 375px;
  height: calc(100vh - 80px);
  border: 1px solid #ccc;
  background-color: #fff;
  box-sizing: border-box;
  margin-bottom: 100px;
  visibility: hidden;
}

#editor__from_position {
  width: 435px;
  height: 667px;
  overflow-y: scroll;
  box-sizing: border-box;
  padding-bottom: 50px;
}

/**新的编辑样式**/
.editorPage_right_wrap {
  // height: 100%;
  width: 415px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

div::-webkit-scrollbar {
  width: 0 !important;
  height: 0 !important;
}

.editor__preview {
  height: calc(100vh - 80px) !important;
  box-shadow:
    0 2px 4px 0 rgba(0, 0, 0, 0.12),
    0 0 6px 0 rgba(0, 0, 0, 0.04);
}

#savetip {
  font-size: 14px;
  color: #888;
  margin-left: 6px;
}

.no-l-b {
  border-right: none !important;
}
</style>
