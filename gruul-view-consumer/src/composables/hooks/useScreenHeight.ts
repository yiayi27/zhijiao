import { type Ref, ref, onBeforeMount, onUnmounted } from 'vue'

export default function useScreenHeight(): Ref<number> {
  const screenHeight = ref(0)

  const handleResize = (res: any) => {
    screenHeight.value = res.size.windowHeight
  }
  onBeforeMount(() => {
    uni.getSystemInfo({
      success: (res) => {
        screenHeight.value = res.windowHeight
      },
    })
    uni.onWindowResize(handleResize)
  })
  onUnmounted(() => {
    uni.offWindowResize(handleResize)
  })
  return screenHeight
}
