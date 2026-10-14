<script lang="ts" setup>
// 定义接口以匹配UserCenter.ts中的MenuList
interface MenuItem {
  id?: number
  menuName: string
  menuIconUrl: string
  hideMenu: boolean
  [key: string]: any
}

const props = defineProps({
  gridMenu: {
    type: Array as () => MenuItem[],
    default: () => [],
  },
})

const menu = computed(() => props.gridMenu.filter((item) => item.hideMenu && item.menuName))
</script>

<template>
  <div class="user__menu--grid">
    <div class="grid">
      <div class="grid__name">我的工具</div>
      <div class="grid__wrap">
        <div v-for="(gridItem, index) in menu" :key="gridItem.id || index" class="grid__navigator">
          <img class="grid__navigator--image" :src="gridItem.menuIconUrl" />
          <div class="grid__navigator--right">
            <span class="grid__navigator--name">{{ gridItem.menuName }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.grid {
  background-color: #ffffff;
  margin-bottom: 15px;
  border-radius: 5px;

  .grid__name {
    font-size: 14px;
    color: #45403c;
    padding: 15px 0 0 10px;
    font-weight: bolder;
  }

  .grid__wrap {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    flex-wrap: wrap;

    .grid__navigator {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      flex: 0 0 33.33%;
      padding: 15px 0;

      .grid__navigator--image {
        width: 28px;
        height: 28px;
      }

      .grid__navigator--right {
        text-align: center;
        padding-top: 15px;
        display: flex;
        flex-direction: column;
        justify-content: center;

        .grid__navigator--name {
          font-size: 13px;
          color: #45403c;
        }

        .grid__navigator--text {
          font-size: 14px;
          color: #b2b2b2;
        }
      }
    }
  }
}
</style>
