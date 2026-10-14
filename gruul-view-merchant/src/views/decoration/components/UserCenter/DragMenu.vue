<script lang="ts" setup>
import type { MenuList, SubMenuList } from '@/views/decoration/components/UserCenter/formModel/UserCenter'
import MenuDialog from '@/views/decoration/components/UserCenter/MenuDialog.vue'
import { CircleCloseFilled, Edit } from '@element-plus/icons-vue'

const props = defineProps({
  menuList: {
    type: Array as () => MenuList[],
    default: () => [],
  },
  /**
   * 1. 列表式 2. 九宫格
   */
  menuStyle: {
    type: Number,
    default: 1,
  },
  /**
   * 展开式是否显示分隔
   */
  splitFlag: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['update:menuList'])

// 内部处理数据，避免直接修改props
const innerMenuList = ref<MenuList[]>([])

// 初始化内部菜单数据
watch(
  () => props.menuList,
  (newVal) => {
    innerMenuList.value = JSON.parse(JSON.stringify(newVal)).flatMap((item: MenuList) => {
      if (item.splitFlag && item.menuName) {
        return [
          getDefaultMenuProp({ splitFlag: true, hideMenu: true }),
          {
            ...item,
            splitFlag: false,
          },
        ]
      }
      return item
    })
  },
  { immediate: true, deep: true },
)

// 拖拽开始状态
const isDragging = ref(false)

function onDragStart() {
  isDragging.value = true
}

// 监听menu的变化并更新父组件
function updateMenuList() {
  emit('update:menuList', innerMenuList.value)
}

// 处理拖拽结束事件
function handleDragEnd(newList: MenuList[]) {
  innerMenuList.value = newList
  updateMenuList()
}

function onDragEnd() {
  isDragging.value = false
  // 确保拖拽结束后更新数据
  nextTick(() => {
    updateMenuList()
  })
}

/**
 * 1 修改菜单
 * 2 新增菜单
 */
const optionType = ref(1)

/** 传入菜单属性 */
const menuProp = ref<MenuList | SubMenuList>(getDefaultMenuProp())

function getDefaultMenuProp(def: object = {}) {
  return {
    id: 0,
    menuName: '',
    menuIconUrl: '',
    defaultIcon: '',
    styleType: 1,
    sortIndex: 0,
    hideMenu: false,
    allowUse: 1,
    linkSelectItem: {
      id: 0,
      name: '',
      type: null,
      url: '',
      append: '',
    },
    splitFlag: false,
    subMenu: null,
    pid: '',
    ...def,
  }
}

/** 菜单设置弹窗 */
const menuVisible = ref(false)

/**
 * 菜单点击
 */
function menuClickHandle(item: MenuList | SubMenuList, type: number) {
  Object.assign(menuProp.value, item)
  optionType.value = type
  menuVisible.value = true
}

/**
 * 删除菜单
 */
function expandMenuDeleteHandle(index: number) {
  innerMenuList.value.splice(index, 1)
  updateMenuList()
}

/**
 * 拖动删除
 */
function dragDeleteHandle(index: number) {
  if (innerMenuList.value[index]) {
    innerMenuList.value[index].splitFlag = false
    updateMenuList()
  }
}

/**
 * 展开式菜单分隔栏现隐
 */
function createDragItem() {
  innerMenuList.value.push(getDefaultMenuProp({ splitFlag: true, hideMenu: true }))
}

/**
 * 前端更新菜单数据值
 * 1 修改菜单
 * 2 新增菜单
 * @param menuItem
 */
function updateMenuHandle(menuItem: MenuList | SubMenuList) {
  if (optionType.value === 1) {
    updateMenuInfo(menuItem)
  }
  if (optionType.value === 2) {
    addMenu(menuItem)
  }
}

/**
 * 更新菜单信息
 */
function updateMenuInfo(menuItem: MenuList | SubMenuList) {
  const index = innerMenuList.value.findIndex((item: MenuList) => item.id === menuItem.id)
  if (index !== -1) {
    innerMenuList.value[index].menuName = menuItem.menuName
    innerMenuList.value[index].menuIconUrl = menuItem.menuIconUrl
    innerMenuList.value[index].linkSelectItem = menuItem.linkSelectItem
    updateMenuList()
  }
}

/**
 * 新增一个菜单
 */
function addMenu(menuItem: MenuList | SubMenuList) {
  const newMenu: MenuList = {
    id: innerMenuList.value.length,
    menuName: menuItem.menuName,
    menuIconUrl: menuItem.menuIconUrl,
    defaultIcon: '',
    styleType: innerMenuList.value[0]?.styleType || props.menuStyle,
    sortIndex: 0,
    hideMenu: true,
    allowUse: 1,
    linkSelectItem: menuItem.linkSelectItem,
    splitFlag: false,
    subMenu: null,
    pid: '',
  }
  innerMenuList.value.push(newMenu)
  updateMenuList()
}
</script>

<template>
  <div class="drag--menu">
    <div class="draggable__menu">
      <m-draggable
        :list="innerMenuList"
        item-key="menuName"
        :animation="200"
        @update:model-value="handleDragEnd"
        @start="onDragStart"
        @end="onDragEnd"
      >
        <template #item="{ element, index }">
          <div>
            <div v-if="splitFlag && element.splitFlag" class="menu__item error-menuItem">
              <div :id="element.id" class="drag--menuitem" />
              <el-icon class="icon-error" @click="dragDeleteHandle(index)">
                <CircleCloseFilled />
              </el-icon>
            </div>
            <div v-else :id="element.menuName" class="menu__item error-menuItem">
              <el-checkbox v-model="element.hideMenu" @change="updateMenuList">
                {{ element.menuName }}
              </el-checkbox>
              <span>
                <el-icon class="el-collapse-item__arrow menu__item--icon" @click="menuClickHandle(element, 1)">
                  <Edit />
                </el-icon>

                <el-icon class="icon-error" @click="expandMenuDeleteHandle(index)">
                  <CircleCloseFilled />
                </el-icon>
              </span>
            </div>
          </div>
        </template>
      </m-draggable>
      <slot name="qrcodeVisible" />
    </div>
    <div class="footer">
      <div class="footer__button">
        <el-button
          type="primary"
          plain
          @click="
            menuClickHandle(
              getDefaultMenuProp({
                styleType: innerMenuList[0]?.styleType || menuStyle,
              }),
              2,
            )
          "
        >
          添加菜单
        </el-button>
        <el-button v-if="splitFlag" type="primary" plain @click="createDragItem"> 添加分隔栏 </el-button>
      </div>
    </div>
    <MenuDialog v-model:menu-visible="menuVisible" :menu-prop="menuProp" :type="optionType" @update-menu="updateMenuHandle" />
  </div>
</template>

<style lang="scss" scoped>
.menu {
  &__item {
    position: relative;

    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 48px;
    padding: 0 10px;
    margin: 8px 0;
    background-color: #ffffff;

    &--icon {
      margin-right: 0;
      color: #9797a1;
      font-size: 16px;
    }
  }
}

.footer {
  padding: 10px 0 15px 0;
  background-color: #ffffff;

  &__button {
    padding: 10px 0 0 10px;
  }
}

.drag--menuList {
  background-color: #ffffff;
  padding: 10px;
}

.draggable__menu {
  background-color: #f1f2f6;
  padding: 2px 10px;

  .drag--menuitem {
    position: relative;
    height: 15px;
    width: 100%;
    background-color: #e9e9e9;
  }
}

.draggable__menu--fold {
  background-color: #f1f2f6;
  padding: 8px 10px;

  .drag--menuitem {
    position: relative;
    height: 15px;
    width: 100%;
    background-color: #e9e9e9;
  }
}

.drag--shadow__item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 48px;
  padding: 0 15px;
  margin: 8px 0;
  background-color: #ffffff;
}

.drag--shadow {
  position: relative;
  height: 15px;
  width: 100%;
  background-color: #e9e9e9;
}

.drag--shadow:hover {
  box-shadow: #9797a1 0 0 5px;
}

// hover显示隐藏
.error-menuItem {
  position: relative;
  font-size: 16px;
  z-index: 2;

  .icon-error {
    opacity: 0;
    color: #9797a1;
    position: absolute;
    right: -6px;
    top: -6px;
    z-index: 9999;
  }
}

.error-menuItem:hover {
  position: relative;
  z-index: 2;

  .icon-error {
    opacity: 1;
  }
}
</style>
