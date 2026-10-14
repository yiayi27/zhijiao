<script setup lang="ts">
import { onLaunch } from '@dcloudio/uni-app'
import storage from '@/utils/storage'
import { useUserStore } from './store/modules/user'
import { useShopsetStore } from './store/modules/shopset'
import { useSettingStore } from './store/modules/setting'
import { useMessageStore } from './store/modules/message'
import { APP_LAUNCH } from './utils/emitNameConfig'

onLaunch(async () => {
  storage.clear()
  useSettingStore().getRule()
  await useUserStore().getTenId()
  await useShopsetStore().getshopset()
  await useSettingStore().getDecorationAllList()
  await useSettingStore().getGuideList()
  await useSettingStore().getGuideVisible()
  const res = await useSettingStore().getSetting()
  useMessageStore().getTemplates(res)
  useSettingStore().getRecyclingSetting()
  useSettingStore().getAllUserCenterData()
  useSettingStore().setIsReady()
  uni.$emit(APP_LAUNCH)
  useUserStore().getShopCarNum()
})
</script>
<style lang="scss"></style>
