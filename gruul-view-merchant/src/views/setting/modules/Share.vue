<script lang="ts" setup>
import { getDefGuideSetting, getGuide, getGuideSetting, getShareSetting, setGuide, setGuideSetting } from '@/api/global'
import LinkSelect from '@/components/LinkSelect'
import UploadFile from '@/views/decoration/components/UserCenter/UploadFile.vue'
import { CircleClose } from '@element-plus/icons-vue'
import { cloneDeep, isString } from 'lodash-es'

// 定义emit
const emit = defineEmits(['submit'])

// 响应式数据
const form = reactive({ background: '', defaultValue: false, title: '' })
const guideList = ref<any>([])
const openGuide = ref(false)

// 方法
function openChange(v: any) {
  setGuideSetting(v).then(() => {
    openGuide.value = v
  })
}

async function handleSave() {
  await Promise.all([onSetGuide()])
  emit('submit')
}

function resetGuide() {
  getDefGuideSetting().then((res) => {
    guideList.value = cloneDeep(res.data).map((item: Obj) => {
      if (item.link) {
        item.link = JSON.parse(item.link)

        if (isString(item.link)) {
          item.link = JSON.parse(item.link)
        }
      } else {
        item.link = {
          url: '',
          path: 'pages/index/index',
          link: '{"id": null,"type":0,"name":"","url":"","append":""}',
          appId: null,
          type: 0,
        }
      }

      return item
    })
    ElMessage.success('恢复成功')
  })
}

function addGuideItem() {
  guideList.value.push({
    url: '',
    path: 'pages/index/index',
    link: { id: null, type: 0, name: '', url: '', append: '' },
    appId: null,
    type: 0,
  })
}

function delGuiteItem(index: number) {
  guideList.value.splice(index, 1)
}

function onSetGuide() {
  const data = cloneDeep(guideList.value).map((item: Obj) => {
    if (item.link) {
      item.link = JSON.stringify(item.link)
    } else {
      item.link = {
        url: '',
        path: 'pages/index/index',
        link: '{"id": null,"type":0,"name":"","url":"","append":""}',
        appId: null,
        type: 0,
      }
    }
    return item
  })
  return setGuide(data)
}

function onGetGuideSetting() {
  getGuideSetting().then((res) => {
    openGuide.value = res.data
  })
}

function onGetGuide() {
  getGuide().then((res) => {
    guideList.value = res.data.map((item: Obj) => {
      if (item.link) {
        item.link = JSON.parse(item.link)
      } else {
        item.link = { id: null, type: 0, name: '', url: '', append: '' }
      }
      return item
    })
  })
}

function onGetShareSetting() {
  getShareSetting().then((res) => {
    Object.assign(form, res.data || { background: '', defaultValue: false, title: '' })
  })
}

// 生命周期钩子 - 组件创建时
onMounted(() => {
  onGetShareSetting()
  onGetGuide()
  onGetGuideSetting()
})
</script>

<template>
  <div class="share">
    <div class="share__tip">
      <div class="share__tip--lump" style="background: #08cc00" />
      <span class="share__tip--title">引导页设置</span>
      <el-switch v-model="openGuide" class="share__tip--switch" @change="openChange" />
    </div>
    <el-form v-if="openGuide" label-width="80px">
      <el-form-item label="引导图">
        <div class="guide">
          <div v-for="(item, i) of guideList" :key="item.id" class="guide__item">
            <div class="item__l">
              <UploadFile v-model:img-url="item.url" :avatar-height="60" :size="1" class="item__uploader" />
            </div>
            <el-icon class="icon-error" @click="delGuiteItem(i)">
              <CircleClose />
            </el-icon>
            <div class="item__r">
              <div>
                <LinkSelect v-model:link="item.link" :limit-pro-tab="false" :no-pro-tab="true" :type="item.type">
                  <template #radio>
                    <div>
                      <el-radio v-model="item.type" :value="0"> 系统连接 </el-radio>
                      <el-radio v-model="item.type" :value="1"> 小程序 </el-radio>
                    </div>
                  </template>
                  <template v-if="item.type === 0" #button>
                    <el-input v-model="item.link.name" placeholder="请选择url" readonly />
                  </template>
                  <template v-else #input>
                    <el-input v-model="item.appId" placeholder="请输入appId" />
                  </template>
                  <!-- <el-input slot="button"
                            readonly
                  placeholder="请输入内容"></el-input> -->
                </LinkSelect>
              </div>
              <div />
            </div>
          </div>
          <div v-if="guideList.length < 5" class="guide__item btn" @click="addGuideItem">添加</div>

          <div class="tips">
            尺寸建议设置750*1134的jpg图片，建议1M以下
            <el-button style="margin-left: 10px" @click="resetGuide"> 恢复默认 </el-button>
          </div>
        </div>
      </el-form-item>
    </el-form>
    <el-form label-width="80px">
      <el-form-item>
        <el-button type="primary" @click="handleSave"> 保 存 </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
.item__uploader {
  display: flex;
  justify-content: center;
  align-items: center;

  width: 100%;
  height: 100%;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
  }
}

.uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.uploader .el-upload:hover {
  border-color: #409eff;
}

.uploader .uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 177px;
  height: 150px;
  line-height: 150px;
  text-align: center;
}

.uploader .uploader__image {
  width: 177px;
  height: 150px;
  display: block;
}

.uploader .uploader__reset {
  position: absolute;
  margin-left: 200px;
  margin-top: -40px;
}

.guide {
  width: 400px;
  position: relative;

  .tips {
    color: rgb(211, 211, 211);
    margin-bottom: 30px;
  }

  .btn {
    background: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }

  &__item {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    position: relative;

    .item__l {
      width: 120px;
      height: 80px;
      margin-right: 10px;

      img {
        width: 100%;
        height: 100%;
      }
    }

    .icon-error {
      position: absolute;
      right: -3px;
      top: -3px;
      cursor: pointer;
    }

    width: 100%;
    padding: 10px;
    background: #ebebeb;
    border: 1px dashed #bebebe;
    border-radius: 5px;
    margin-bottom: 15px;
  }
}

.share {
  &__tip {
    vertical-align: center;
    background-color: rgba(246, 248, 250, 1);
    padding: 15px 15px 15px 30px;
    margin-bottom: 30px;

    &--title {
      margin-left: 12px;
      color: #586884;
      font-weight: 700;
    }

    &--switch {
      float: right;
    }

    &--lump {
      display: inline-block;
      width: 3px;
      height: 12px;
      background-color: rgba(255, 153, 0, 1);
    }
  }

  .tips {
    color: rgb(139, 139, 139);
  }
}
</style>
