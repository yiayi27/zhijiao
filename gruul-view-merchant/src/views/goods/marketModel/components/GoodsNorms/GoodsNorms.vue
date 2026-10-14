<script setup lang="ts">
import { ElMessageBox } from 'element-plus'

const props = defineProps({
  classArr: {
    type: Array as PropType<any>,
    default: () => [],
  },
})
const emit = defineEmits(['changeClass'])

const normRules = {
  name: [{ required: true, message: '请输入规格名称', trigger: 'blur' }],
}

function handleClose(tag: string, index: number, tagIndex: number) {
  const tempArr = props.classArr || []
  if (tempArr[index].children[tagIndex].name.indexOf(tag) === 0) {
    tempArr[index].children.splice(tagIndex, 1)
  }
  console.log(1)
  emit('changeClass', { type: 'delSpecValue', list: tempArr })
}

function showInput(index: number) {
  const tempArr = props.classArr || []
  tempArr[index].inputVisble = true
}

function handleInputConfirm(index: number) {
  const tempArr = props.classArr || []
  if (tempArr[index].inputValue) {
    tempArr[index].children.push({ name: tempArr[index].inputValue })
  }
  tempArr[index].inputVisble = false
  tempArr[index].inputValue = ''
  console.log(2)
  emit('changeClass', { type: 'addSpecValue', list: tempArr })
}

function addNewClass() {
  const tempArr = props.classArr || []
  tempArr.push({
    inputValue: '',
    inputVisble: false,
    children: [],
    name: '',
    version: '',
  })
  console.log(3)
  emit('changeClass', { type: 'addSpecValue', list: tempArr })
}

function delNormHandle(index: number) {
  ElMessageBox.confirm('确认删除该规格?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const tempArr = props.classArr || []
    tempArr.splice(index, 1)
    console.log(4)
    emit('changeClass', { type: 'delSpec', list: tempArr })
    ElMessage({
      message: '删除成功',
      type: 'success',
    })
  })
}

function specSortHandle() {
  console.log(5)
  emit('changeClass', { type: 'sortSpecValue', list: props.classArr })
}
</script>

<template>
  <div class="norm">
    <div class="el-form-item__label">商品规格</div>
    <div class="norm__list">
      <div v-for="(item, index) in classArr" :key="index" class="norm__item">
        <el-form :model="item" :rules="normRules">
          <div class="norm__item__name">
            <div class="norm__item__name--label">规格名称</div>
            <div class="norm__item__name--left">
              <el-form-item prop="name">
                <el-input v-model="item.name" placeholder="规格名称" class="w-[200px]" />
              </el-form-item>
            </div>
            <el-button type="danger" class="norm__item__name--del" @click="delNormHandle(index)"> 删除规格 </el-button>
          </div>
          <div class="norm__item__value">
            <div class="norm__item__name--label">规格值</div>
            <div class="norm__item__value__content">
              <template v-if="item.children.length">
                <m-draggable v-model="item.children" itemKey="name" class="mr-[10px]" @sort="specSortHandle">
                  <template #item="{ element, index: i }">
                    <el-tag closable :disable-transitions="false" class="mr-[10px]" @close="handleClose(element.name, index, i)">
                      {{ element.name }}
                    </el-tag>
                  </template>
                </m-draggable>
              </template>
              <el-input
                v-if="item.inputVisble"
                v-model="item.inputValue"
                class="input-new-tag w-[200px]"
                placeholder="规格值, 回车添加"
                @keyup.enter="handleInputConfirm(index)"
                @blur="handleInputConfirm(index)"
              />
              <el-button v-else class="button-new-tag" @click="showInput(index)"> 添加新规格 </el-button>
            </div>
          </div>
          <el-button v-if="index === classArr.length - 1" type="primary" @click="addNewClass"> 添加规格项目 </el-button>
        </el-form>
      </div>
      <el-button v-if="classArr.length === 0" type="primary" @click="addNewClass"> +添加规格项目 </el-button>
    </div>
  </div>
</template>
