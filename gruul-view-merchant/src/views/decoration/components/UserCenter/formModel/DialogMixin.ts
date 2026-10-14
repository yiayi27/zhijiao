import { ElMessageBox } from 'element-plus'

/**
 * DialogMixin 转换为Vue 3 Composition API格式
 */
export default function useDialogMixin() {
  const dialogVisible = ref(false)

  /**
   * 关闭弹窗
   */
  async function closeHandle() {
    const result = await confirmHandle()
    if (result === 'confirm') {
      dialogVisible.value = false
    }
  }

  /**
   * 关闭前确认
   */
  async function beforeCloseHandle(done: () => void) {
    const result = await confirmHandle()
    if (result === 'confirm') {
      done()
    }
  }

  /**
   * 关闭确认
   */
  async function confirmHandle() {
    const result = await ElMessageBox.confirm('确定退出该窗口？退出后，数据可能不保留！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    return result
  }

  return {
    dialogVisible,
    closeHandle,
    beforeCloseHandle,
    confirmHandle,
  }
}
