<script setup lang="ts">
import type { IComponent } from './interfaces/component'
import { useDecorationStore } from '@/store/modules/decoration'
import { Setting as SettingIcon } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'
import setting from './compontents/index/setting'

// 定义props
defineProps({
  currentComponent: {
    type: Object as () => IComponent | null,
    default: () => ({}),
  },
})

const decorationStore = useDecorationStore()
const { activePageType } = storeToRefs(decorationStore)
</script>

<template>
  <!-- 设置编辑页面 -->
  <div class="editor__form_wrap">
    <div class="clearfix editor__form_header">
      <span>{{ currentComponent ? `${currentComponent.label}` : '' }}组件设置</span>
    </div>
    <div class="editor__form_wrap_main">
      <el-scrollbar>
        <div class="pd20">
          <!--          <transition name="el-fade-in-linear" mode="out-in"> -->
          <component
            :is="setting[currentComponent?.value as keyof typeof setting]"
            v-if="currentComponent && activePageType !== 'bussiness'"
            :form-data="currentComponent.formData"
          />
          <div v-else class="form-tips">
            <div class="form-tips-logo">
              <el-icon size="100" class="mb-[20px]">
                <SettingIcon />
              </el-icon>
              <p>
                {{ activePageType === 'bussiness' ? '当前组件,无法进行设置' : '当前未设置组件,请选择组件进行设置' }}
              </p>
            </div>
          </div>
          <!--          </transition> -->
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/editorPage';

.editor__form_wrap {
  height: calc(100vh - 62px);

  width: 100%;
  padding-bottom: 0 !important;
  display: flex;
  flex-direction: column;

  .editor__form_wrap_main {
    width: 100%;
    flex: 1;
    overflow: hidden;
    position: relative;
  }

  .editor__form_header {
    padding: 18px 20px;
    border-bottom: 1px solid #dcdfe6;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
  }

  .pd20 {
    padding: 20px;
    padding-bottom: 0;
  }

  .form-tips {
    .form-tips-logo {
      width: 435px;
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      text-align: center;
      color: #b2b2b2;
    }
  }
}
</style>
