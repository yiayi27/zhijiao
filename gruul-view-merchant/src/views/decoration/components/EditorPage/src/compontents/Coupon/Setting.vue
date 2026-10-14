<script setup lang="ts">
const props = defineProps({
  formData: {
    type: Object,
    default: () => null,
  },
})

const emit = defineEmits(['update:formData'])

const formModel = computed({
  get: () => props.formData,
  set: (value) => emit('update:formData', value),
})
</script>

<template>
  <!-- 轮播图 -->
  <div class="coupon">
    <div class="coupon-form">
      <el-form label-position="left" :model="formModel" label-width="85px">
        <el-form-item label="优惠券样式">
          <el-radio-group v-model="formModel.style">
            <el-radio value="style1"> 样式一 </el-radio>
            <el-radio value="style2"> 样式二 </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="更多设置">
          <el-checkbox v-model="formModel.hide"> 隐藏已抢完的券 </el-checkbox>
          <div class="coupon_tip">页面无可显示的优惠券时，优惠券模块将隐藏</div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style lang="scss">
.coupon_tip {
  color: #8c939d;
}
.homeSwiperForm-item {
  display: flex;
  border: 1px solid #e4e4e4;
  padding: 10px;
  margin-bottom: 10px;
  &__right {
    flex: 1;
    padding-left: 10px;
  }
  &__uploader {
    width: 80px;
    height: 80px;
    display: block;
    .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
    }
    .el-upload:hover {
      border-color: #409eff;
    }
  }

  &__img {
    width: 80px;
    height: 80px;
    display: block;
  }

  &__plus {
    font-size: 28px;
    color: #8c939d;
    width: 80px;
    height: 80px;
    line-height: 80px;
    text-align: center;
  }
}

.homeSwiperForm-add {
  width: 100%;
  height: 100px;
  border: 1px solid #e4e4e4;
  margin-bottom: 10px;
  .el-upload {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
  }
  span {
    color: #3088f0;
    cursor: pointer;
  }
  p {
    font-size: 12px;
    color: #a7a7a7;
  }
}

.homeSwiperForm-form {
  border: 1px solid #e4e4e4;
  padding: 10px;
}
</style>
