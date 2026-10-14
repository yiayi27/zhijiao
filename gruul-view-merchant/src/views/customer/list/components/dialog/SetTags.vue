<script setup lang="ts">
import { editTags } from '@/api/customer/customer'

interface TagItem {
  tagId?: number | string
  tagName: string
  option?: boolean
}

interface UserItem {
  userId: string
  nikeName: string
  userTagVos?: TagItem[] | null
}

const props = defineProps({
  allTags: {
    type: Array as () => TagItem[],
    required: true,
  },
})

const emit = defineEmits(['refreshDataList'])

const dataForm = reactive({
  add: [] as TagItem[],
  del: [] as TagItem[],
  update: [] as TagItem[],
  userIds: [] as string[],
})

/** 渲染标签列表 */
const tagList = ref<TagItem[]>([])

/** 用户原本标签列表 */
const orgTags = ref<TagItem[]>([])

const users = ref<string[]>([])
const tagName = ref('')
const dialogVisible = ref(false)
const tagAddVisible = ref(false)
const isinput = ref(false)

function init(multipleSelection: UserItem[]) {
  initData()
  dataForm.userIds = multipleSelection.map((item) => item.userId)
  users.value = multipleSelection.map((item) => item.nikeName)
  // 得到初始用户标签
  multipleSelection.forEach((item) => {
    if (item.userTagVos !== null && item.userTagVos && item.userTagVos.length > 0) {
      orgTags.value.push(...item.userTagVos)
    }
  })
  getTagList()
  dialogVisible.value = true
}

function initData() {
  dataForm.add = []
  dataForm.del = []
  dataForm.update = []
  dataForm.userIds = []
  tagList.value = []
  orgTags.value = []
  tagName.value = ''
}

function getTagList() {
  const allTagsCopy = JSON.parse(JSON.stringify(props.allTags))
  allTagsCopy.forEach((supTag: TagItem) => {
    orgTags.value.forEach((tag) => {
      if (supTag.tagId === tag.tagId) {
        supTag.option = true
      }
    })
  })
  tagList.value = allTagsCopy
}

function getTagName() {
  let charLength = 0
  const currentTagName = tagName.value
  for (let i = 0; i < currentTagName.length; i++) {
    const a = currentTagName.charAt(i)
    if (a.match(/[\u4e00-\u9fa5]|[^\u0020-\u007f]/gi) !== null) {
      if (charLength + 2 >= 12) {
        tagName.value = currentTagName.substr(0, i + 1)
        return
      }
      charLength += 2
    } else {
      if (charLength + 1 >= 12) {
        tagName.value = currentTagName.substr(0, i + 1)
        return
      }
      charLength += 1
    }
  }
}

function addLabel() {
  if (isinput.value) {
    return
  }
  isinput.value = true
  // 判空
  if (tagName.value === '') {
    return
  }
  const item = tagList.value.find((label) => {
    return label.tagName === tagName.value
  })
  const tagNumber = tagList.value.filter((tagItem) => {
    return tagItem.tagName !== ''
  })
  if (tagNumber.length > 99) {
    ElMessage({
      message: '最多添加100个标签',
      type: 'warning',
    })
    return
  }
  if (item !== undefined) {
    ElMessage({
      message: '请勿重复添加标签',
      type: 'warning',
    })
    return
  }
  const tag: TagItem = {
    option: true,
    tagName: tagName.value,
  }
  tagList.value.push(tag)
  tagName.value = ''
  isinput.value = false
}

function submit() {
  const add = tagList.value.filter((tag) => {
    return tag.tagId === undefined && tag.tagName !== ''
  })
  const del = tagList.value.filter((tag) => {
    return tag.tagName === '' && tag.tagId !== undefined
  })
  const update = tagList.value.filter((tag) => {
    // isDel 为初始用户标签里勾选去掉的标签
    let isDel = false
    if (tag.tagId !== undefined && tag.tagName !== '' && tag.option === false) {
      if (orgTags.value.find((label) => label.tagId === tag.tagId) !== undefined) {
        isDel = true
      } else {
        isDel = props.allTags.some((j) => {
          return j.tagId === tag.tagId && j.tagName !== tag.tagName
        })
      }
    }
    return (tag.tagId !== undefined && tag.tagName !== '' && tag.option === true) || isDel
  })
  dataForm.add = add
  dataForm.del = del
  dataForm.update = update
  editTags(dataForm).then(() => {
    ElMessage({
      message: '设置成功',
      type: 'success',
    })
    emit('refreshDataList', 1)
    dialogVisible.value = false
  })
}

// 关闭弹窗
function closeHandle() {
  dialogVisible.value = false
}

// Expose methods for parent components
defineExpose({
  init,
})
</script>

<template>
  <el-dialog v-model="dialogVisible" :before-close="closeHandle" width="500px" class="dialog" title="设置标签">
    <div class="tag--container">
      <div v-for="tag in tagList" :key="tag.tagId" class="tag">
        <el-checkbox v-model="tag.option" />
        <el-input v-model="tag.tagName" class="tag--input" size="small" :maxlength="6" />
      </div>
    </div>
    <el-button v-if="!tagAddVisible" link @click="tagAddVisible = true"> 新增标签 </el-button>
    <div v-if="tagAddVisible" class="tag--add">
      <el-input v-model="tagName" size="small" style="width: 300px" placeholder="请输入标签名" @input="getTagName" />
      <el-button link style="margin-left: 10px" @click="addLabel"> 添加 </el-button>
      <el-button link @click="tagAddVisible = false"> 取消 </el-button>
    </div>
    <template #footer>
      <span class="dialog--footer">
        <el-button @click="closeHandle">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.customers {
  color: black;
}
.tag--container {
  overflow-y: scroll;
  height: 250px;

  .tag {
    display: inline-block;
    margin: 10px 5px;

    &--input {
      width: 120px;
      margin-left: 5px;
    }
  }
}

:deep(.tag--input .el-input__inner) {
  border: 1px solid transparent;
}

:deep(.tag--input .el-input__inner:focus) {
  border-color: #c0c4cc;
}

:deep(.tag--input .el-input__inner:hover) {
  border-color: #c0c4cc;
}

:deep(.tag--container .el-checkbox__inner) {
  width: 12px;
  height: 12px;
}

:deep(.tag--container .el-checkbox__inner::after) {
  left: 3px;
  top: 0px;
}
</style>
