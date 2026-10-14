<script lang="ts" setup>
import type { LinkSelectItem } from '@/components/LinkSelect/type'

import type { MenuList, SubMenuList } from './formModel/UserCenter'
import LinkSelect from '@/components/LinkSelect'
import { typeNameMap } from '@/components/LinkSelect/constants'
import UploadFile from '@/views/decoration/components/UserCenter/UploadFile.vue'

import useDialogMixin from './formModel/DialogMixin'

// 定义菜单项的基础默认值接口
interface BaseMenuItemDefaults {
  menuName: string
  menuIconUrl: string
  defaultIcon: string
  hideMenu: boolean
  linkSelectItem: {
    id: number
    name: string
    type: number | null
    url: string
    append: string
  }
}

const props = defineProps({
  menuVisible: {
    type: Boolean,
    default: false,
  },
  menuProp: {
    type: Object,
    default: () => ({}),
  },
  /**
   * 1 修改菜单
   * 2 新增菜单
   */
  type: {
    type: Number,
    default: 1,
  },
})

const emit = defineEmits(['update:menuVisible', 'updateMenu'])

// 使用转换后的DialogMixin
const { beforeCloseHandle, closeHandle, dialogVisible } = useDialogMixin()

watch(
  () => props.menuVisible,
  (newVal) => {
    dialogVisible.value = newVal
  },
)
watch(dialogVisible, (newVal) => {
  emit('update:menuVisible', newVal)
})

// 菜单项默认值
const menuItemDefaults: BaseMenuItemDefaults = {
  menuName: '',
  menuIconUrl: '',
  defaultIcon: '',
  hideMenu: false,
  linkSelectItem: {
    id: 0,
    name: '',
    type: null,
    url: '',
    append: '',
  },
}

// 菜单项数据
const menuItem = ref<Partial<MenuList & SubMenuList>>({
  ...menuItemDefaults,
  id: undefined,
  styleType: undefined,
  sortIndex: undefined,
  allowUse: undefined,
  splitFlag: undefined,
  subMenu: undefined,
  pid: undefined,
})

// 表单验证规则
const formRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuIconUrl: [{ required: true, message: '请选择图标', trigger: 'change' }],
}

// 监听对话框显示状态
watch(dialogVisible, (v) => {
  if (v) {
    menuItem.value = JSON.parse(JSON.stringify(props.menuProp))
    if (props.type === 2) {
      menuItem.value = {
        ...menuItemDefaults,
        id: undefined,
        styleType: props.menuProp.styleType,
        sortIndex: undefined,
        allowUse: undefined,
        splitFlag: undefined,
        subMenu: undefined,
        pid: undefined,
      }
    }
  }
})

/**
 * 选择链接
 */
function selectLinkHandle(linkDataItem: LinkSelectItem) {
  if (menuItem.value.linkSelectItem) {
    menuItem.value.linkSelectItem = linkDataItem
  }
}

/**
 * 获取链接名称
 */
const linkInfo = computed(() => {
  if (menuItem.value.linkSelectItem?.type && typeNameMap[menuItem.value.linkSelectItem.type]) {
    return `${typeNameMap[menuItem.value.linkSelectItem.type].text}-${menuItem.value.linkSelectItem.name}`
  } else {
    return ''
  }
})

/**
 * 校验
 */
function confirmClickHandle() {
  if (!menuItem.value.menuName) {
    ElMessage({
      type: 'warning',
      message: '请填写菜单名称',
    })
    return
  }
  // if (!menuItem.value.linkSelectItem.id) {
  //   ElMessage({
  //     type: "warning",
  //     message: "请选择链接",
  //   });
  //   return;
  // }
  if (!menuItem.value.menuIconUrl) {
    ElMessage({
      type: 'warning',
      message: '请选择图标',
    })
    return
  }
  emitUpdateMenu()
  dialogVisible.value = false
}

/**
 * 触发更新菜单事件
 */
function emitUpdateMenu() {
  emit('updateMenu', menuItem.value)
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :before-close="beforeCloseHandle" width="450px" :append-to-body="true" :modal-append-to-body="true" title="编辑">
    <el-form :model="menuItem" :rules="formRules" label-width="120px" label-position="left">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="menuItem.menuName" placeholder="请输入菜单名称" maxlength="10" show-word-limit />
      </el-form-item>
      <el-form-item label="菜单图标" prop="menuIconUrl">
        <div class="form-item__content">
          <UploadFile v-model:img-url="menuItem.menuIconUrl" :default-icon="menuItem.defaultIcon || ''" />
        </div>
      </el-form-item>
      <el-form-item label="跳转链接">
        <el-input v-show="false" v-model="menuItem.linkSelectItem.name" />
        <LinkSelect inner :show-clear="type === 2" :link="menuItem.linkSelectItem" @select="selectLinkHandle" />
        <div>
          <span style="color: #9797a1">{{ linkInfo }}</span>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog--footer">
        <el-button @click="closeHandle">取 消</el-button>
        <el-button type="primary" @click="confirmClickHandle">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.form--item {
  display: flex;
  justify-content: flex-start;
  align-content: flex-end;
  padding: 10px;

  .form-item__label {
    width: 120px;
    text-align: left;
    vertical-align: middle;
    float: left;
    font-size: 14px;
    color: #606266;
    line-height: 14px;
    box-sizing: border-box;
  }
}

.el-upload__tip {
  color: #9797a1;
}
</style>
