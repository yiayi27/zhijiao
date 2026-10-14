import DateUtil from '@/mixins/date'
const storageKey = import.meta.env.VITE_LOCAL_STORAGE_KEY_PREFIX

class Storage {
  // 私有静态实例
  private static instance: Storage | null = null

  // 私有构造函数，防止直接实例化
  private constructor() {}

  /**
   * 获取Storage实例的静态方法
   * @returns Storage实例
   */
  public static getInstance(): Storage {
    if (!Storage.instance) {
      Storage.instance = new Storage()
    }
    return Storage.instance
  }

  /**
   * 设置缓存
   * @param key 键名
   * @param data 数据
   * @param expired 持续时间（可选，单位天）
   */
  set(key: string, data: any, expired = 1) {
    // import.meta.env.VITE_BASE_URL
    uni.setStorageSync(`${storageKey}_${key}`, JSON.stringify({ data, time: +new Date(), expired: expired * 60 * 60 * 24 * 1000 }))
  }

  /**
   * 获取缓存
   * @param key 键名
   */
  get(key: string): any {
    try {
      const { data, time = 0, expired = 0 } = JSON.parse(uni.getStorageSync(`${storageKey}_${key}`))

      if (!expired) return null

      if (time + expired > +new Date()) return data

      this.remove(key)
      return null
    } catch {
      return null
    }
  }

  /**
   * 清除缓存
   * @param key 键名
   */
  remove(key: string) {
    return uni.removeStorageSync(`${storageKey}_${key}`)
  }

  /**
   * 清除当前项目缓存(不影响其他端的缓存)
   */
  removeThis() {
    const res = uni.getStorageInfoSync()
    res.keys.forEach((key) => {
      if (key.startsWith(storageKey)) {
        uni.removeStorageSync(key)
      }
    })
  }

  /**
   * 日常清空老缓存，一日一次
   */
  clear() {
    const dateUtil = new DateUtil(new Date())
    const lastClearTime = uni.getStorageSync('B2C_CONSUMER_CLEAR_DATE')
    const toady = dateUtil.getYMD(new Date())
    if (lastClearTime === toady) return
    this.removeThis()
    uni.setStorageSync('B2C_CONSUMER_CLEAR_DATE', toady)
  }
}

// 导出单例实例
export default Storage.getInstance()
