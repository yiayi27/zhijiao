<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
</script>

<template>
  <div>
    <div class="search-wrap">
      <div class="search-main">
        <el-icon class="mx-[10px]">
          <Search />
        </el-icon>
        请搜索输入框
      </div>
      <div class="tab-wrap">
        <div class="navList__title navList__item navList__itemCheck">
          <div class="mt10">全部</div>
          <div class="navList__title--line" />
        </div>
        <div class="navList__title navList__item">
          <div class="mt10">分类一</div>
        </div>
      </div>
    </div>
    <div class="pro-wrap">
      <div class="goods">
        <div class="goods__image">
          <img
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200514/3c3cc8ab4e3f495db1005cb17a8faaff.jpg"
            width="100%"
          />
        </div>
        <div class="goods__infoBox">
          <div class="goods__infoBox--name">小L加拿大代购Hermes/爱马仕100ml血色大黄</div>
          <div class="goods__infoBox__info">
            <div class="goods--specification">
              <div class="goods--specification--actPrice">￥0.01</div>
              <div class="goods--specification--price">￥100.00</div>
            </div>
            <div class="goods__infoBox__bottom">立即购买</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.search-wrap {
  background: #fff;
  height: 92rpx;
  width: 100%;
  padding: 5px 20px;
}
.search-main {
  border-radius: 40px;
  height: 35px;
  display: inline-block;
  display: -webkit-box;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  background-color: #f8f8f8;
  width: 100%;
}
.tab-wrap {
  height: 45px;
  width: 100%;
  display: -webkit-box;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  overflow: auto;
  background-color: white;
  margin-bottom: 5px;
}
.navList__item {
  width: 75px;
  height: 35px;
  display: flex;
  align-items: center;
  flex-direction: column;
  margin-right: 15px;
  font-weight: bold;
  font-size: 14px;
}
.navList__itemCheck {
  color: #dd3c2b;
  font-size: 16px;
}
.navList__title--line {
  width: 25px;
  height: 4px;
  background-color: #dd3c2b;
  margin-top: 5px;
  border-radius: 5px;
  border-radius: 90px;
  margin-left: 3px;
}
.mt10 {
  margin-top: 10px;
}
.pro-wrap {
  padding: 5px 7px;
  background-color: #f2f2f2;
}
.goods {
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  background-color: #fff;
}
.goods__image {
  height: 140px;
  overflow: hidden;
}
.goods__image img {
  border-radius: none;
}
.product__name {
  font-size: 16px;
  line-height: 26px;
  font-weight: bold;
  color: #1e1c1c;
}
.goods__infoBox__bottom {
  width: 80px;
  height: 30px;
  background-color: #dd3c2b;
  color: white;
  border-radius: 20px;
  font-size: 13px;
  margin-top: 10px;
  display: -webkit-box;
  display: flex;
  -webkit-box-pack: center;
  justify-content: center;
  -webkit-box-align: center;
  align-items: center;
}
.goods__infoBox__info {
  display: flex;
  -webkit-box-pack: justify;
  justify-content: space-between;
  -webkit-box-align: center;
  align-items: center;
  width: 100%;
}
.goods__infoBox {
  padding: 15px;
}
.goods .goods__infoBox__info .goods--specification {
  display: -webkit-box;
  display: flex;
  -webkit-box-pack: justify;
  justify-content: space-between;
  -webkit-box-align: center;
  align-items: center;
  margin-top: 10px;
}

.goods .goods__infoBox__info .goods--specification--price {
  text-decoration: line-through;
  color: #bbbbbb;
  font-size: 12px;
  margin-left: 8px;
}
.goods .goods__infoBox__info .goods--specification--price {
  text-decoration: line-through;
  color: #bbbbbb;
  font-size: 12px;
  margin-left: 8px;
}
</style>
