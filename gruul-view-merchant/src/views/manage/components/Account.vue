<script setup lang="ts">
import Ranking from './Ranking.vue'

interface AccountQuery {
  uv: number
  pv: number
  totalUv: number
}

defineProps<{
  accountQuery: AccountQuery
}>()
</script>

<template>
  <div class="account">
    <div class="account__left">
      <div class="store">
        <span class="store__title">今日访客统计</span>
      </div>

      <div class="py-[18px]">
        <el-row>
          <el-col :span="12">
            <div class="account__left--accNum--inCenter">
              <p class="inCenter__text">新增访客(人)</p>
              <p class="inCenter__number red">
                {{ accountQuery.uv }}
              </p>
            </div>
          </el-col>
        </el-row>
        <el-row class="mt-[20px]">
          <el-col :span="12">
            <div class="account__left--accNum--inCenter">
              <p class="inCenter__text">当日浏览量</p>
              <p class="inCenter__number">
                {{ accountQuery.pv }}
              </p>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="account__left--accNum--inCenter">
              <p class="inCenter__text">总访客(人)</p>
              <p class="inCenter__number">
                {{ accountQuery.totalUv }}
              </p>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="account__center">
      <Ranking />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.account {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;

  .account__left {
    width: 35%;
    height: 250px;
    background-color: white;
    padding: 20px;
  }

  .account__center {
    width: 64%;
    background-color: white;
    // padding: 20px;
    height: 250px;
    overflow: auto;
    .account__center--precent {
      .account__center--precent--inCenter {
        display: flex;
        flex-wrap: wrap;
        width: 90px;
        justify-content: center;
        margin-top: 40px;
      }
    }

    .account__center--header {
      display: flex;
      margin-top: 10px;
      margin-left: 10px;
      flex-wrap: wrap;
      width: 450px;
      align-content: space-around;
      height: 190px;

      .account__center--header--inCenter {
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }

  .account__center::-webkit-scrollbar {
    display: none;
  }
}

.lineCenter {
  display: flex;
  width: 260px;
}

.inCenter__number {
  color: #515a6e;
  font-size: 22px;
  margin-top: 10px;
  font-weight: bold;
  width: 40px;
  display: flex;
  justify-content: center;

  &.red {
    color: #f36673;
  }
}

.inCenter__precent {
  color: #5c86fd;
  font-size: 22px;
}

.inCenter__precent::after {
  content: '%';
  font-weight: normal;
  font-size: 15px;
  margin-left: 4px;
}

.inCenter__img {
  .inCenter__img--head {
    width: 50px;
    height: 50px;
    border-radius: 50px;
  }
}

.inCenter__text {
  color: #515a6e;
  font-size: 14px;
  margin-top: 5px;
}

.active {
  color: #fd7c25;
  position: relative;

  // .activeLine {
  //   width: 26px;
  //   height: 2px;
  //   background-color: #2d8cf0;
  //   position: absolute;
  //   bottom: -8px;
  // }
}

.store {
  vertical-align: center;

  .store__lump {
    display: inline-block;
    width: 3px;
    height: 12px;
    background-color: #5b85fd;
  }

  .store__title {
    color: #586884;
    font-weight: 700;
  }
}

.el-range-editor--small.el-input__inner {
  height: 30px;
}
</style>
