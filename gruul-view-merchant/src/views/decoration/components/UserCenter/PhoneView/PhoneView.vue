<script lang="ts" setup>
import { getShopInfo } from '@/api/businessCenter/setting'

import config from '@/config'
import UserCenter from '@/views/decoration/components/UserCenter/formModel/UserCenter'
import GridMenu from '@/views/decoration/components/UserCenter/GridMenu.vue'
import Menu from '@/views/decoration/components/UserCenter/Menu.vue'
import MenuItem from '@/views/decoration/components/UserCenter/MenuItem.vue'
import MaskSetting from '@/views/decoration/components/UserCenter/PhoneView/MaskSetting.vue'
import TopBar from '@/views/decoration/components/UserCenter/PhoneView/TopBar.vue'

// 定义菜单项接口
interface MenuData {
  id: string | number
  splitFlag?: boolean
  menuName: string
  hideMenu?: boolean
  menuIconUrl?: string
  [key: string]: any
}

const props = defineProps({
  /** 配置数据 */
  formData: {
    type: Object,
    default: () => new UserCenter(),
  },
  /** 自定义样式数据 */
  customStyle: {
    type: Object,
    default: () => ({
      /** 背景图片 */
      backgroundImage: '',
      /** 卡面颜色 */
      cardColor: '',
      /** 文字颜色 */
      textColor: '',
    }),
  },
  /** 用户信息数据 */
  orderInfo: {
    type: Object,
    default: () => ({
      /* orderInfo* 售后 */
      afterSaleIcon: {
        name: '',
        url: '',
      },
      waitDelivered: {
        name: '',
        url: '',
      },
      /** 待付款 */
      waitIcon: {
        name: '',
        url: '',
      },
      /** 待发货 */
      waitPickingIcon: {
        name: '',
        url: '',
      },
      /** 配送中 */
      deliveryIcon: {
        name: '',
        url: '',
      },
      /** 评价中心 */
      evaluateIcon: {
        name: '',
        url: '',
      },
    }),
  },
  /**
   * 展开式菜单数据
   */
  expandMenu: {
    type: Array as PropType<MenuData[]>,
    default: () => [],
  },
  /**
   * 折叠式菜单数据
   */
  foldMenu: {
    type: Array as PropType<any[]>,
    default: () => [],
  },
  headStyleConfig: {
    type: Object,
    default: () => ({
      headBackGround: {},
      cardColor: '#45403C',
      textColor: '#E4CB98',
    }),
  },
})

const userPosition = ref(0)
const showUserSetting = ref(false)
const showAddress = ref(true)
const scrollTop = ref('')
const version = ref('')
const miniBottomLog = ref(config.logo)

/**
 * @param e
 * @description 滚动事件
 */
function scrollHandle(e: Event) {
  const target = e.target as HTMLElement
  scrollTop.value = `${target.scrollTop}px`
}

/**
 * @param val
 * @description 设置遮罩层显示隐藏
 */
function showUserSettingHandle(val: boolean) {
  showUserSetting.value = val
}

/**
 * @param menuItemName 菜单字符名
 * @param type 菜单字符名
 * @description 菜单项点击事件
 */
function menuItemClickHandle(menuItemName: string, type: boolean) {
  if (menuItemName === '设置') {
    showAddress.value = type
    showUserSettingHandle(true)
  }
}

async function getInfo() {
  const queryForm = await getShopInfo()
  version.value = queryForm.teamplateVersion

  if (queryForm.miniBottomLog) {
    miniBottomLog.value = queryForm.miniBottomLog
  }
}

// 组件创建后获取信息
onMounted(() => {
  getInfo()
})
</script>

<template>
  <div
    class="user"
    :style="{
      overflowY: showUserSetting ? 'hidden' : 'scroll',
      left: `${userPosition}px`,
    }"
    @scroll="scrollHandle"
  >
    <div :class="`mask ${showUserSetting ? 'mask__show' : 'mask__hidden'}`" :style="{ top: scrollTop }">
      <MaskSetting :show-address="showAddress" @go-back="showUserSettingHandle" />
    </div>
    <div class="user__radius" :style="headStyleConfig.headBackGround">
      <TopBar />
      <div class="center">个人中心</div>
      <div class="info">
        <img
          src="https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIj8fkACXBjG5oxepn1slUQm5ibBia0PvZPdo7Gib583vmDKuYxQOkuWsAd1eotA6fbLXMYiaXAdgRu5A/132"
          class="info__avatar"
          alt="info"
        />
        <div class="info__name">元宵汤圆水饺</div>
        <!-- <div
          @click="pickCodeClickHandle"
          class="info__qrcode"
          v-if="formData.codeStyle === 2 && formData.qrcodeVisible"
        >
        </div> -->
      </div>
      <div class="data">
        <div class="data__item">
          <div class="data__number">60.30</div>
          <div class="data__text">余额</div>
        </div>
        <div>｜</div>
        <div class="data__item">
          <div class="data__number">1024</div>
          <div class="data__text">积分</div>
        </div>
        <div>｜</div>
        <div class="data__item">
          <div class="data__number">50</div>
          <div class="data__text">收藏</div>
        </div>
        <div>｜</div>
        <div class="data__item">
          <div class="data__number">100</div>
          <div class="data__text">足迹</div>
        </div>
      </div>
      <div v-if="formData.hideCartInlet" class="radius">
        <div class="radius__inner" :style="{ backgroundColor: headStyleConfig.cardColor }">
          <div class="radius__inner--item" :style="{ color: headStyleConfig.textColor }">
            <span class="radius__inner--text">尊享会员</span>
            <span class="radius__inner--split">|</span>
            <span class="radius__inner--text" :style="{ color: headStyleConfig.textColor }">{{ formData.getCartText }}</span>
          </div>
          <span
            class="radius__inner--item radius__inner--btn"
            :style="{
              backgroundColor: headStyleConfig.textColor,
              color: headStyleConfig.cardColor,
            }"
            >立即开通</span
          >
        </div>
      </div>
    </div>
    <div class="user__cover">
      <div class="order">
        <div class="order__all">
          <div class="order__all--me">我的订单</div>
          <div class="order__all--check">
            查看全部订单
            <span>></span>
          </div>
        </div>
        <div class="order__quick">
          <div class="order__quick--item">
            <img :src="orderInfo.waitIcon.url" class="order__quick--img" />
            <div class="order__quick--text">
              {{ orderInfo.waitIcon.name }}
            </div>
          </div>
          <div class="order__quick--item">
            <img :src="orderInfo.waitDelivered.url" class="order__quick--img" />
            <div class="order__quick--text">
              {{ orderInfo.waitDelivered.name }}
            </div>
          </div>
          <!-- <div class="order__quick--item">
            <img :src="orderInfo.deliveryIcon.url" class="order__quick--img" alt="deliveryIcon" />
            <div class="order__quick--text">
              {{ orderInfo.deliveryIcon.name }}
            </div>
          </div> -->
          <div class="order__quick--item">
            <img :src="orderInfo.waitPickingIcon.url" class="order__quick--img" />
            <div class="order__quick--text">
              {{ orderInfo.waitPickingIcon.name }}
            </div>
          </div>
          <div class="order__quick--item">
            <img :src="orderInfo.afterSaleIcon.url" class="order__quick--img" />
            <div class="order__quick--text">
              {{ orderInfo.afterSaleIcon.name }}
            </div>
          </div>
          <div class="order__quick--item">
            <img :src="orderInfo.evaluateIcon.url" class="order__quick--img" />
            <div class="order__quick--text">
              {{ orderInfo.evaluateIcon.name }}
            </div>
          </div>
        </div>
      </div>
      <div class="user__menu">
        <div v-if="formData.menuStyle === 1" class="user__menu--expand">
          <Menu>
            <template v-for="expandItem in expandMenu" :key="expandItem.id">
              <template v-if="expandItem.splitFlag && !expandItem.menuName">
                <div :class="{ splitFlag: expandItem.splitFlag }" />
              </template>
              <template v-else>
                <MenuItem v-if="expandItem.hideMenu" :img-url="expandItem.menuIconUrl" @click="menuItemClickHandle(expandItem.menuName || '', false)">
                  {{ expandItem.menuName }}
                </MenuItem>
              </template>
            </template>
          </Menu>
        </div>
        <GridMenu v-if="formData.menuStyle === 2" :grid-menu="foldMenu" />
        <!-- <pick-code v-if="formData.codeStyle === 1 && formData.qrcodeVisible" /> -->
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '@/assets/styles/decoration/phoneView';
</style>
