<template>
  <UPopup v-model:show="visible">
    <view class="w-picker-cnt" :class="{ visible }">
      <view class="w-picker-header" catchtouchmove="true" @touchmove.stop.prevent>
        <text @tap.stop.prevent="onCancel">取消</text>
        <slot></slot>
        <text :style="{ color: themeColor }" @tap.stop.prevent="pickerConfirm">确定</text>
      </view>

      <RegionPicker
        v-if="mode === 'region'"
        class="w-picker-wrapper"
        :value="value"
        :hide-area="hideArea"
        :default-type="defaultType"
        :item-height="itemHeight"
        @change="handlerChange"
        @touchstart="touchStart"
        @touchend="touchEnd"
      >
      </RegionPicker>
    </view>
  </UPopup>
</template>

<script setup lang="ts">
import { ref, reactive, type PropType, watchEffect } from 'vue'
import RegionPicker from './RegionPicker.vue'
import UPopup from '../UPopup/UPopup.vue'

defineOptions({
  name: 'WPicker',
})
// 定义Props
const props = defineProps({
  mode: {
    type: String,
    default: 'date',
  },
  value: {
    //默认值
    type: [String, Array, Number] as PropType<Long | Long[]>,
    default: '',
  },
  themeColor: {
    //确认按钮主题颜色
    type: String,
    default: '#f5a200',
  },
  defaultType: {
    type: String,
    default: 'label',
  },
  hideArea: {
    //mode=region时，是否隐藏区县列
    type: Boolean,
    default: false,
  },
  timeout: {
    //是否开启点击延迟,当快速滚动 还没有滚动完毕点击关闭时得到的值是不准确的
    type: Boolean,
    default: false,
  },
})

const visible = defineModel<boolean>('visible', { required: true, default: false })

watchEffect(() => {
  if (visible.value) {
    // 收起键盘
    uni.hideKeyboard()
  }
})

// 定义emit
const emit = defineEmits(['cancel', 'confirm'])

// 响应式数据
const itemHeight = ref(`height: ${uni.upx2px(88)}px;`)
const result = reactive({})
const confirmFlag = ref(true)

// 方法
const touchStart = () => {
  if (props.timeout) {
    confirmFlag.value = false
  }
}

const touchEnd = () => {
  if (props.timeout) {
    setTimeout(() => {
      confirmFlag.value = true
    }, 500)
  }
}

const handlerChange = (res: any) => {
  Object.assign(result, res)
}

const onCancel = () => {
  visible.value = false
  emit('cancel')
}

const pickerConfirm = () => {
  if (!confirmFlag.value) {
    return
  }
  emit('confirm', result)
  visible.value = false
}
</script>

<style lang="scss">
.w-picker-cnt {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  transition: all 0.3s ease;
  transform: translateY(100%);
  z-index: 3000;
  background-color: #fff;
}
.w-picker-cnt.visible {
  transform: translateY(0);
}
.w-picker-header {
  display: flex;
  align-items: center;
  padding: 0 30upx;
  height: 88upx;
  background-color: #fff;
  position: relative;
  text-align: center;
  font-size: 32upx;
  justify-content: space-between;
  border-bottom: solid 1px #eee;
  .w-picker-btn {
    font-size: 30upx;
  }
}

.w-picker-hd:after {
  content: ' ';
  position: absolute;
  left: 0;
  bottom: 0;
  right: 0;
  height: 1px;
  border-bottom: 1px solid #e5e5e5;
  color: #e5e5e5;
  transform-origin: 0 100%;
  transform: scaleY(0.5);
}
</style>
