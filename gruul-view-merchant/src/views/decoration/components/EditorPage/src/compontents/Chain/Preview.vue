<script setup lang="ts">
import PreImg from '@/components/PreImg.vue'

const preList = ref([
  {
    img: 'https://qiniu-app.qtshe.com/u391.png',
    id: '100',
    name: '微信昵称',
    time: '2分钟前',
    goodName: '商品名称（5件）+1',
  },
  {
    img: 'https://qiniu-app.qtshe.com/u391.png',
    name: '微信昵称',
    id: '99',
    time: '昨天',
    goodName: '商品名称（5件）+1',
  },
  {
    img: 'https://qiniu-app.qtshe.com/u391.png',
    name: '微信昵称',
    id: '98',
    time: '前天',
    goodName: '商品名称（5件）+1',
  },
  {
    img: 'https://qiniu-app.qtshe.com/u391.png',
    name: '微信昵称',
    id: '97',
    time: '三天前',
    goodName: '商品名称（5件）+1',
  },
])
</script>

<template>
  <div>
    <div class="chainTop">
      <div class="chainTop__content">
        <PreImg />
        <span style="font-size: 16px">接龙活动</span>
        <PreImg />
      </div>
    </div>
    <div class="chainDesc">
      <div class="chainDesc__top">
        <span class="name">活动名称</span>
        <span class="day">6天前</span>
      </div>
      <div class="chainDesc__desc">活动描述活动描述活动描述活动描述活动描述活动描述活动描述活动描述活动描述活动描述活动描述活动描述活动</div>
      <div class="chainDesc__img">
        <img src="https://qiniu-app.qtshe.com/u391.png" alt="" style="width: 100%; height: 170px" />
      </div>
      <div class="chainDesc__list">
        <div v-for="(item, index) in preList" :key="index">
          <div class="chainDesc__list--content">
            <div class="line">
              <div class="contentId">
                {{ item.id }}
              </div>
              <img src="https://qiniu-app.qtshe.com/u391.png" alt="" class="img" />
              <div style="color: #6b6b6b">
                {{ item.name }}
              </div>
            </div>
            <div style="color: #6b6b6b">
              {{ item.time }}
            </div>
            <div style="color: #6b6b6b">
              {{ item.goodName }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="chainBottom">
      <div class="chainBottom__left">
        <span>有1578人看过</span>
        <span>100人参与</span>
      </div>
      <div>剩余:08:08:08</div>
      <div class="chainBottom__status">进行中</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.chainTop {
  display: flex;
  justify-content: center;

  &__content {
    display: flex;
    align-items: center;
    width: 180px;
    justify-content: space-between;
  }
}

.chainDesc {
  margin-top: 20px;
  padding: 0px 20px;
  &__top {
    display: flex;
    justify-content: space-between;

    .name {
      font-size: 18px;
      font-weight: bold;
    }

    .day {
      font-size: 14px;
      color: #bcbcbc;
    }
  }
  &__desc {
    margin-top: 10px;
    color: #868686;
  }
  &__img {
    width: 100%;
    margin-top: 10px;
  }
  &__list {
    margin-top: 10px;
    &--content {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 5px;

      .line {
        display: flex;
        width: 150px;
        justify-content: space-between;
        align-items: center;

        .contentId {
          width: 20px;
          display: flex;
          justify-content: flex-end;
        }

        .img {
          width: 40px;
          height: 40px;
          border-radius: 50px;
        }
      }
    }
  }
}

.chainBottom {
  display: flex;
  margin-top: 10px;
  padding: 0px 20px;
  justify-content: space-between;
  align-items: center;
  color: #a1a1a1;
  &__left {
    width: 160px;
    display: flex;
    justify-content: space-between;
  }

  &__status {
    width: 60px;
    height: 24px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50px;
    font-size: 12px;
    color: white;
    background-color: #f73119;
  }
}
</style>
