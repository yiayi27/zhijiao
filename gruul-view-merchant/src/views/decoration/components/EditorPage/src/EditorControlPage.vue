<script setup lang="ts">
import type { CategoryPage, CustomizedPages } from './interfaces/EditorControlPage'
import { addControl, getControl } from '@/api/decoration/decoration'
import { getAllRegionList } from '@/api/good/goods'
import { addAssembly, addPage, delPage, getJudgeAssembly, getPageList } from '@/api/group/group'
import GoodNameEdit from '@/components/GoodNameEdit2.vue' // 编辑商品名称
import { useDecorationStore } from '@/store/modules/decoration'
import { CaretBottom, CaretRight, Edit, More } from '@element-plus/icons-vue'
import type { IComponentItem, IComponent } from './interfaces/component'
import EdtiorFormData from './compontents/index/formModel'

interface PluginItem {
  formData: {
    menuList?: {
      isHome?: boolean
      id?: string | number
      linkUrl?: string
      type?: number | string
      linkName?: string
    }[]
    [key: string]: any
  }
  [key: string]: any
}

interface ControlPlugin {
  pluginProperties: string
  [key: string]: any
}

interface LivePageItem {
  name: string
  components: {
    formData: Record<string, any>
    icon: string
    label: string
    value: string
    link: string
  }[]
}

interface MarketingPageItem {
  id: string | number
  modeName: string
  link?: string
  modeType?: string
  [key: string]: any
}

interface PageTypeParams {
  type: string
  link: string
}

defineProps({
  activePageId: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['change', 'changePage', 'changeFncMarkPage'])

/** 当前自定义页面 */
const currentItem = ref<CustomizedPages | any>({})

/** 判断更改标题新增 */
const activeType = ref(0)

/** 展示用户中心列表 */
const showUserList = ref(true)

/** 展示营销列表 */
const showBussinessList = ref(true)

/** 展示自定义列表 */
const showPageList = ref(true)

/** 自定义页面列表 */
const pageList = ref<CustomizedPages[]>([
  {
    id: 1,
    name: '空白页',
    isHome: false,
  },
])

/** 商超分类页 */
const classificationPage = ref<CategoryPage>({
  id: 0,
  name: '商超分类页',
  isHome: false,
  type: '2',
})

/** 首页页面id匹配首页 */
const homeId = ref<number | string>('')

/** 获取的首页tab配置，设置时写回去 */
const homePlugin = ref<ControlPlugin | null>(null)

const livePage = ref<LivePageItem[]>([
  {
    name: '直播页面',
    components: [
      {
        formData: {},
        icon: '',
        label: '直播',
        value: 'Live',
        link: '/subcontract/pages/live/live',
      },
    ],
  },
])

/** 营销页面 */
const marketingPage = ref<MarketingPageItem[]>([
  {
    id: 1,
    name: '直播页面',
    modeName: '直播页面',
    components: [
      {
        formData: {},
        icon: '',
        label: '直播',
        value: 'Live',
        link: '/subcontract/pages/live/live',
      },
    ],
  },
])

const decorationStore = useDecorationStore()

/** 功能页面选中 */
const functionpagetype = computed(() => {
  return decorationStore.activePageType
})

/**
 * 获取营销页面数据
 */
async function getMarketingPage(): Promise<void> {
  const res = await getAllRegionList({})
  const data = res.data
  data.forEach((item: MarketingPageItem) => {
    item.link = '/pages/index/index?page=home'
  })
  marketingPage.value = data
}

/** 设为首页 */
async function setHomePage(item: CustomizedPages): Promise<void> {
  await getControlList()
  addOrEditControl(item.id)
  await editOrAdd({ id: item.id, isDef: 1, pageName: item.name || item.pageName }, 3)
  fetchPageList()
}

/**
 * 初始获取控件数据
 */
async function getControlList(): Promise<void> {
  const res = await getControl({
    pluginNameEn: 'navBar',
    isDeleted: 0,
  })
  if (res.code === 200 && res.data.length > 0) {
    homePlugin.value = res.data[0]
    try {
      const pluginProperties = JSON.parse(res.data[0].pluginProperties || '[]') as PluginItem[]
      if (pluginProperties && pluginProperties.length > 0) {
        const navBarPlugin = pluginProperties[0].formData?.menuList || []
        if (navBarPlugin && navBarPlugin.length > 0) {
          const navbarHome = navBarPlugin.filter((item) => item.isHome)
          if (navbarHome.length > 0) {
            const { linkUrl = '', type = '', id = '' } = navbarHome[0]
            if (type === 5 && linkUrl === 'pages/index/index') {
              homeId.value = id || ''
            }
          }
        }
      }
    } catch (error) {
      console.error('Error parsing plugin properties:', error)
    }
  }
}

/**
 * 修改/新增控件
 */
async function addOrEditControl(id: string | number): Promise<void> {
  const homePluginValue = homePlugin.value
  if (!homePluginValue || !homePluginValue.pluginProperties) return

  try {
    const pluginPropertiesData = JSON.parse(homePluginValue.pluginProperties) as PluginItem[]
    if (!pluginPropertiesData || !pluginPropertiesData[0]) return

    let pluginProperties = pluginPropertiesData[0]
    let formData = pluginPropertiesData[0].formData || {}
    const navBarPlugin = formData.menuList || []

    dealEditControl({ navBarPlugin, id })
    formData = { ...formData, menuList: navBarPlugin }
    const updatedPluginProperties = [{ ...pluginProperties, formData }]
    homePluginValue.pluginProperties = JSON.stringify(updatedPluginProperties)
    await addControl(homePluginValue)
  } catch (error) {
    console.error('Error parsing plugin properties:', error)
  }
}

/**
 * 处理控件数据
 */
function dealEditControl({
  navBarPlugin,
  id,
  name = '',
}: {
  navBarPlugin: Array<{
    isHome?: boolean
    id?: string | number
    linkUrl?: string
    type?: number | string
    linkName?: string
    [key: string]: any
  }>
  id: string | number
  name?: string
}): void {
  if (!Array.isArray(navBarPlugin)) return

  navBarPlugin.forEach((item, i) => {
    if (item.isHome) {
      navBarPlugin[i].id = id
      navBarPlugin[i].linkUrl = 'pages/index/index'
      navBarPlugin[i].type = 5
      navBarPlugin[i].linkName = name
    }
  })
}

/**
 * 获取自定义页面数据
 */
async function fetchPageList(): Promise<void> {
  const res = await getPageList({ size: 100 })

  if (res.code === 200) {
    const list = res.data.list || []
    pageList.value = filterFunctionPage(list)
    if (!decorationStore.firstStatus && !decorationStore.isUsercenterCompontents && activeType.value !== 1 && res.data.list.length) {
      const activePage = res.data.list.filter((item: any) => item.isDef !== '0')[0]
      // 初始化获取 设为首页的页面id
      const homePageId = activePage?.id || ''

      decorationStore.setHomePageId(homePageId)
      decorationStore.setActivePage(activePage)
      decorationStore.setFirstStatus(true)
    }
  }
}

/**
 * 过滤功能页面
 * 因为现在的功能页是存在自定义页面的
 */
function filterFunctionPage(list: any[]): CustomizedPages[] {
  const temp2 = list.find((i) => i.type === '2')
  if (temp2) classificationPage.value = temp2
  return list.filter((i) => i.type !== '1' && i.type !== '2')
}

/**
 * 修改/新增自定义页面
 */
async function editOrAdd(
  data: {
    id?: string | number
    isDef: number
    pageName: string
    type?: string
    modelId?: string | number
  },
  type: number,
): Promise<void> {
  const res = await addPage(data)
  if (res.code === 200) {
    activeType.value = 1
    if (type === 1) {
      decorationStore.setActivePage(res.data)
    }
    fetchPageList()
  }
}

/**
 * 折叠标签
 */
function fold(v: string): void {
  if (v === 'showUserList') showUserList.value = !showUserList.value
  if (v === 'showBussinessList') showBussinessList.value = !showBussinessList.value
  if (v === 'showPageList') showPageList.value = !showPageList.value
}

/**
 * 新增自定义页面
 */
function onAddPage(): void {
  let data = {
    isDef: 0,
    pageName: '自定义页面',
  }
  if (!pageList.value.length) {
    data = {
      isDef: 1,
      pageName: '首页',
    }
  }
  editOrAdd(data, 1)
}

/**
 * 删除页面
 */
async function onDelPage(id: string | number): Promise<void> {
  const res = await delPage({ ids: id })

  if (res.code === 200) {
    activeType.value = 0
    decorationStore.setFirstStatus(false)
    fetchPageList()
  }
}

/**
 * 点击自定义页面装修
 */
async function selectPage(type: string, link: string, item: any, index: number = -1): Promise<void> {
  currentItem.value = item
  decorationStore.setActivePageType({ type, link })
  decorationStore.setActivePage(item)
  emit('changePage', { type, link, item, index })
}

/**
 * 初始化请求参数
 */
function initPostData({ type }: { type: string }): { icon: string; value: string; label: string; id?: number } {
  const tempdata = { icon: '', value: 'SpellPage', label: '拼团分类页' }
  if (type === '2') {
    tempdata.value = 'BusinessSuper'
    tempdata.label = '商超分类页'
  }
  return { ...tempdata }
}

/**
 * 返回一个新页面对象
 */
function getNewComponent(currentComponent: IComponentItem): IComponent {
  const FormData = EdtiorFormData[currentComponent.value as keyof typeof EdtiorFormData]
  return { ...currentComponent, id: Date.now(), formData: FormData }
}
/**
 * 手动指定插入一个组件到一个页面
 */
const addNewComponent = async (data: IComponent, option: any) => {
  const properties = JSON.stringify({ ...data })
  const assembly = {
    properties,
    ...option,
  }
  const res = await addAssembly([assembly])
  return res
}

/**
 * 选择拼团/商超分类页
 */
async function selectcollagePage(
  type: string,
  link: string,
  item: CategoryPage,
  newItem: MarketingPageItem = { id: '', modeName: '' },
  id: string | number | null = null,
): Promise<void> {
  console.log('type', type)
  console.log('link', link)
  console.log('item', item)
  console.log('newItem', newItem)
  console.log('id', id)
  if (newItem.id) {
    item.modelId = newItem.id
  }
  const param = {
    modelId: newItem.id,
  }

  if (newItem.link) {
    const res = await getJudgeAssembly(param)
    console.log('res=======================', res.data)
    if (res.data.length === 0) {
      const data = {
        isDef: 0,
        pageName: item.name,
        type: item.type,
        modelId: newItem.id,
      }
      const res2 = await addPage(data)
      if (res2.code !== 200) {
        console.error(res2.msg)
        return
      }

      const tempdata = initPostData({ type: item.type })
      tempdata.id = Date.now()
      const com = getNewComponent(tempdata)
      const addmsg = await addNewComponent(com, { pageId: res2.data.id, templateId: res.data.templateId })
      if (addmsg.code !== 200) {
        console.error(addmsg.msg)
        return
      }
      classificationPage.value = res.data
      decorationStore.setActivePage(res.data)
      selectPage(type, link, res.data)

      emit('changeFncMarkPage', { type, link, item, id, tempdata })
    } else {
      decorationStore.setActivePageType({ type, link })
      const res2 = await getPageList({ size: 100 })
      if (res2.code === 200) {
        const list = res2.data.list || []
        const itemPage = list.find((listItem: any) => listItem.modelId === newItem.id)
        if (itemPage) {
          decorationStore.setActivePage(itemPage)
        }
      }

      const tempdata = initPostData({ type: item.type })
      tempdata.id = Date.now()

      emit('changeFncMarkPage', { type, link, item, id, tempdata })
    }
  } else {
    decorationStore.setActivePageType({ type, link })
    decorationStore.setActivePage(item)
    const tempdata = initPostData({ type: item.type })
    tempdata.id = Date.now()

    emit('changeFncMarkPage', { type, link, item, id, tempdata })
  }
}

/**
 * 处理编辑商品名称
 */
async function updateGoodName(pageName: string, item: CustomizedPages): Promise<void> {
  const data = {
    id: item.id,
    pageName,
    isDef: item.isDef,
  }
  await editOrAdd(data, 2)
}

enum CommandType {
  DELETE,
  SET_HOME,
}

function handleCommand(command: CommandType, item: CustomizedPages): void {
  switch (command) {
    case CommandType.DELETE:
      onDelPage(item.id)
      break
    case CommandType.SET_HOME:
      setHomePage(item)
      break
  }
}

// 初始化
onBeforeMount(() => {
  getMarketingPage()
  fetchPageList()
  getControlList()
})

defineExpose({
  getMarketingPage,
  marketingPage,
})
</script>

<template>
  <!-- 页面管理页面 -->
  <div class="editor_control_wrap">
    <div class="editor_control_wrap_main">
      <el-scrollbar style="height: 100%; width: 100%">
        <!-- 功能页面 -->
        <div class="tab_item" @click.prevent="fold('showUserList')">
          <el-icon>
            <CaretBottom v-if="showUserList" />
            <CaretRight v-else />
          </el-icon>
          <span>功能页面</span>
        </div>
        <div v-if="showUserList" :class="`${showUserList}`">
          <div class="page_item" @click="selectPage('usercenter', '', 'usercenter', -1)">
            <span>用户中心</span>
            <span v-if="functionpagetype === 'usercenter'" class="el-dropdown-link">
              <el-icon><Edit /></el-icon>
            </span>
          </div>
        </div>
        <div class="page_item" @click="selectPage('bussiness', livePage[0].components[0].link, livePage[0], -1)">
          <span>直播页面</span>
          <span v-if="functionpagetype === 'bussiness'" class="el-dropdown-link">
            <el-icon><Edit /></el-icon>
          </span>
        </div>

        <!-- 营销页面 -->
        <div class="tab_item" @click="fold('showBussinessList')">
          <el-icon>
            <CaretBottom v-if="showBussinessList" />
            <CaretRight v-else />
          </el-icon>
          <span>商品专区</span>
        </div>

        <div v-if="showBussinessList">
          <div
            v-for="item in marketingPage"
            :key="item.id"
            class="page_item"
            @click="selectcollagePage('customize', item.link || '', classificationPage, item, item.id)"
          >
            <span class="page_name2">{{ item.modeName }}</span>
            <span v-if="activePageId && activePageId === `${item.id}`" class="el-dropdown-link">
              <el-icon><Edit /></el-icon>
            </span>
          </div>
        </div>

        <!-- 自定义页面 -->
        <div class="tab_item" @click="fold('showPageList')">
          <el-icon>
            <CaretBottom v-if="showPageList" />
            <CaretRight v-else />
          </el-icon>
          <span>自定义页面</span>
        </div>

        <div v-if="showPageList">
          <div
            v-for="item in pageList"
            :key="item.id"
            class="page_item"
            @click="selectPage('customize', '/subcontract/pages/index/index?page=home', item, -1)"
          >
            <GoodNameEdit v-if="item.pageName" :good-name="item.pageName" :good="item" @change="(name) => updateGoodName(name, item)" />
            <div v-if="item.isDef !== '0'" style="font-size: 12px; color: #9999">首页</div>
            <el-dropdown v-if="item.isDef === '0'" class="page_item_icon" @command="(value: CommandType) => handleCommand(value, item)">
              <span class="el-dropdown-link">
                <el-icon><More /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="CommandType.SET_HOME">
                    <span>设为首页</span>
                  </el-dropdown-item>
                  <el-dropdown-item :command="CommandType.DELETE">
                    <span class="block">删除</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <div v-if="pageList.length < 10" class="add_page_btn" @click="onAddPage()">
          <span>+ 添加页面</span>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/editorControlPage';
</style>
