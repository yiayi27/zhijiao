<script setup lang="ts">
// Vue 3 setup script
</script>

<template>
  <div class="live-list">
    <div class="live-list-item">
      <img src="http://mmbiz.qpic.cn/mmbiz_png/s3EL7V6A73pdoU9DuSoA6bmb1aub1yGfxrnxn24ylwyRPZXwDmDKlhzPVLzicTtKkyFPicMvEBibQvqiaI0uibesCFg/0" />
      <div class="live-item-head">
        <!-- <span class="head-live">
          <img src />直播中
        </span> -->
        <span class="head-no-live">未开始</span>
        <!-- <span v-if="item.status === 103" class="head-no-live head-done-live">已结束</span>
          <span v-if="item.status === 104" class="head-no-live head-done-live">禁播</span>
          <span v-if="item.status === 105" class="head-no-live head-done-live">暂停中</span>
          <span v-if="item.status === 106" class="head-no-live head-done-live">异常</span>
        <span v-if="item.status === 107" class="head-no-live head-done-live">已过期</span> -->
        <span class="head-text">101观看</span>
      </div>
      <div style="background: rgba(0, 0, 0, 0.05); height: 65px; width: 100%; bottom: 0; position: absolute">
        <div class="live-item-center">燕美女</div>
        <div class="live-item-bottom">
          <div class="live-anchor">燕美女</div>
          <div class="live-like">
            <img class="like-gif" src="http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/dong_live.gif" />
            <img src="http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/like.png" />
            12
          </div>
        </div>
      </div>
    </div>
    <div class="live-list-item">
      <img src="http://mmbiz.qpic.cn/mmbiz_jpg/s3EL7V6A73p8v87rLtdwNhXYbM9S46OSBxa5OeZgHeScBm9AjpZRoIrOAfwP96XIujvsUpj91ADTF6ibMelDRvg/0" />
      <div class="live-item-head">
        <span class="head-no-live head-done-live">已结束</span>
        <!-- <span v-if="item.status === 104" class="head-no-live head-done-live">禁播</span>
          <span v-if="item.status === 105" class="head-no-live head-done-live">暂停中</span>
          <span v-if="item.status === 106" class="head-no-live head-done-live">异常</span>
        <span v-if="item.status === 107" class="head-no-live head-done-live">已过期</span> -->
        <span class="head-text">101观看</span>
      </div>
      <div style="background: rgba(0, 0, 0, 0.05); height: 65px; width: 100%; bottom: 0; position: absolute">
        <div class="live-item-center">简单发大V</div>
        <div class="live-item-bottom">
          <div class="live-anchor">和2356</div>
          <div class="live-like">
            <img class="like-gif" src="http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/dong_live.gif" />
            <img src="http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/Live/like.png" />
            12
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import './index.scss';
</style>
