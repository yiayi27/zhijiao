<script setup lang="ts">
import { insertMemberCardPrivilege, updateMemberCardPrivilege } from '@/api/customer/member'
import { upLoad } from '@/api/index'
import { ptypeMapIcon } from '@/libs/const'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'

// 定义类型接口
interface FormData {
  ptype: number
  privilegeName: string
  illustrate: string
  icon: string
  [key: string]: any
}

// 请求函数类型
type RequestFunction = (data: any) => Promise<any>

const emit = defineEmits(['reFresh'])

const formRef = ref()
const dialogVisible = ref(false)

// 表单数据
const formData = reactive<FormData>({
  ptype: 0,
  privilegeName: '',
  illustrate: '',
  icon: '',
})

// 上传的图片
const uploadIcon = ref('')
const ruleForm = reactive<any>({})
const ruleVisible = ref(false)
const ruleType = ref('new')
const operateType = ref('new')
const operateIndex = ref(0)

// 权益类型映射
const ptypeMap: Record<number, string> = {
  0: '自定义权益',
  1: '商品折扣',
  2: '物流优惠',
  3: '积分加倍',
  4: '专属客服',
  5: '优先发货',
  6: '急速售后',
}

const hideError = ref(false)

// 表单的校验规则
const coustomRules = {
  privilegeName: [{ required: true, message: '权益名称不可为空' }],
  illustrate: [{ required: true, message: '权益说明不可为空' }],
  icon: [{ required: true, message: '权益图标不可为空', trigger: 'change' }],
}

function init(data: Partial<FormData> | undefined) {
  Object.assign(formData, cloneDeep(data || { ptype: 0 }))
  dialogVisible.value = true
  ruleType.value = data ? 'edit' : 'new'
  uploadIcon.value = formData.icon || ptypeMapIcon[formData.ptype as keyof typeof ptypeMapIcon] || ''
  formData.icon = formData.icon || ptypeMapIcon[formData.ptype as keyof typeof ptypeMapIcon] || ''
}

// 上传图片
async function addUploadBigProductImg(file: any) {
  const whiteList = ['image/jpeg', 'image/jpg', 'image/png']

  const isLt1M = file.size < 1 * 1024 * 1024
  if (!whiteList.includes(file.raw.type)) {
    ElMessage.error('上传文件只能是 JPG或PNG 格式!')
    return
  }
  if (!isLt1M) {
    ElMessage.error('上传文件大小不能超过 150kb!')
    return
  }

  const res = await upLoad({
    file: file.raw,
  })
  hideError.value = true
  formData.icon = res.data
  uploadIcon.value = res.data
}

// 恢复默认图片
function resetIcon() {
  uploadIcon.value = ptypeMapIcon[formData.ptype as keyof typeof ptypeMapIcon] || ''
  formData.icon = ptypeMapIcon[formData.ptype as keyof typeof ptypeMapIcon] || ''
}

function onRuleOk() {
  if (operateType.value === 'edit') {
    formData.rules[operateIndex.value] = ruleForm
  } else {
    formData.rules = [...(formData.rules || []), { ...ruleForm }]
  }
  ruleVisible.value = false
}

// 规则设置表单确认
async function onOk() {
  hideError.value = false
  await formRef.value.validate(() => {
    if (formData.icon && formData.illustrate && formData.privilegeName) {
      hideError.value = true
      const reqMap: Record<string, RequestFunction> = {
        new: insertMemberCardPrivilege,
        edit: updateMemberCardPrivilege,
      }
      reqMap[ruleType.value]({
        ...formData,
        pType: formData.ptype,
        open: 1,
        allowDel: formData.ptype === 0 ? 1 : 0,
      }).then(() => {
        ElMessage({
          type: 'success',
          message: ruleType.value === 'edit' ? '更新成功' : '插入成功',
        })
        dialogVisible.value = false
        emit('reFresh')
      })
    }
  })
}

// 点击取消弹窗
function onCancel() {
  ElMessageBox.confirm('确定退出设置规则？退出后，设置规则信息将不保留。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    dialogVisible.value = false
  })
}

defineExpose({
  init,
})
</script>

<template>
  <div>
    <el-dialog v-model="dialogVisible" width="750px" class="dialog" title="设置规则">
      <el-form ref="formRef" :rules="coustomRules" :model="formData" label-width="100px">
        <el-form-item label="权益类型" prop="ptype">
          {{ ptypeMap[formData.ptype] }} &nbsp;&nbsp;&nbsp;
          <span v-if="formData.ptype === 2" style="color: #999">拥有该权益，快递配送订单免运费</span>
          <span v-if="formData.ptype === 5" style="color: #999">拥有该权益，订单会自动打标权益名称，可优先发货</span>
          <span v-if="formData.ptype === 6" style="color: #999">拥有该权益的会员，申请售后系统将立即同意售后</span>
        </el-form-item>
        <div :class="hideError && 'hError'">
          <el-form-item label="权益图标" prop="icon">
            <div>
              <div class="product" style="display: flex">
                <el-upload
                  class="productImg__uploader product__productImg"
                  :auto-upload="false"
                  :on-change="addUploadBigProductImg"
                  :show-file-list="false"
                  style="width: 100px; height: 120px"
                >
                  <img v-if="uploadIcon" :src="uploadIcon" class="product__productImg" style="width: 100px; height: 120px" />
                  <el-icon
                    v-if="!uploadIcon"
                    class="product__productImg--uploader--icon"
                    style="width: 100px; height: 100px; line-height: 100px; border: 1px dotted #999; border-radius: 10px"
                  >
                    <Plus />
                  </el-icon>
                </el-upload>
                <el-button v-if="formData.ptype !== 0" style="height: 32px; margin: 70px 0 0 20px" @click="resetIcon"> 恢复默认 </el-button>
              </div>
              <div class="text-info fs-12" style="color: rgba(69, 64, 60, 0.6)">
                如不设置封面卡片，则为默认官方图片
                <span style="color: red">（最佳尺寸100*100，图片大小不超过150kb）</span>
              </div>
            </div>
          </el-form-item>
        </div>
        <el-form-item label="权益名称" prop="privilegeName">
          <el-input v-model="formData.privilegeName" style="width: 200px" placeholder="请输入，最多5个字符" :maxlength="5" />
        </el-form-item>
        <el-form-item label="权益说明" prop="illustrate">
          <el-input v-model="formData.illustrate" :rows="2" type="textarea" style="width: 200px" />
        </el-form-item>
        <!-- <el-form-item label="权益说明" prop="rules">
          <el-table
          :data="formData.rules"
          style="width: 100%">
           <el-table-column
            prop="ruleName"
            label="权益规则名称"
          >
          </el-table-column>
          <el-table-column
            prop="bei"
            label="默认倍数"
           >
          </el-table-column>
          <el-table-column
            width="120"
            label="操作">
            <template #default="scope">
              <el-button @click="onEditRule(scope.row, scope.$index)" link>编辑</el-button>
              <el-button @click="formData.rules.splice(scope.$index, 1)"  link>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button class="setupcon__item3--addnew"
          link
          @click="onAddNewRule">新增权益规则
        </el-button>
        </el-form-item> -->
      </el-form>

      <template #footer>
        <div class="dialog--footer">
          <el-button @click="onCancel"> 取 消 </el-button>
          <el-button type="primary" @click="onOk"> 确 定 </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="ruleVisible" width="30%" title="添加权益规则">
      <el-form :model="ruleForm" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="ruleForm.ruleName" style="width: 160px" />
        </el-form-item>
        <el-form-item label="规则名称" prop="bei">
          <el-input v-model="ruleForm.bei" style="width: 160px">
            <template #append> 倍 </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog--footer">
          <el-button type="primary" @click="onRuleOk"> 确 定 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.hError {
  :deep(.el-form-item__error) {
    display: none;
  }
}
</style>
