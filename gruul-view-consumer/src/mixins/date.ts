export default class DateUtil {
  ms: any | undefined

  constructor(ms?: Date | any) {
    this.ms = ms ? new Date(ms) : new Date()
  }

  /**
   * @method
   * @description 测试时间参数
   * @param {} ms
   */
  GMTTest(ms: Long | Date) {
    const mss = ms || this.ms
    const GMT = new Date(this.iosFormat(mss))
    return GMT
  }

  /**
   * @method
   * @description 苹果兼容
   * @param {String} str
   */
  iosFormat(str: string) {
    let strs = String(str)
    if (String(str).match(/-/)) {
      strs = String(str).replace(/-/g, '/')
    }
    return strs
  }

  /**
   * @method
   * @description 获取年
   * @param {*} ms
   */
  getY(ms: Long) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const y = GMT.getFullYear()
    return y
  }

  /**
   * @method
   * @description 获取月
   * @param {*} ms
   */
  getM(ms: Long) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const m = GMT.getMonth() + 1
    return this.formatLength(m)
  }

  /**
   * @method
   * @description 获取日
   * @param {*} ms
   */
  getD(ms: Long | Date) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const d = GMT.getDate()
    return this.formatLength(d)
  }

  /**
   * @method
   * @description 获取时
   * @param {*} ms
   */
  getH(ms: string) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const H = GMT.getHours()
    return this.formatLength(H)
  }

  /**
   * @method
   * @description 获取时
   * @param {*} ms
   */
  getMin(ms: string) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const M = GMT.getMinutes()
    return this.formatLength(M)
  }

  /**
   * @method
   * @description 获取时
   * @param {*} ms
   */
  getS(ms: string) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const S = GMT.getSeconds()
    return this.formatLength(S)
  }

  /**
   * @method
   * @description 获取年/月/日
   * @param {} ms
   */
  getYMD(ms?: string | Date) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const y = GMT.getFullYear()
    const m = GMT.getMonth() + 1
    const d = GMT.getDate()
    // return y+'-'+m+'-'+d; // 苹果不识别'-'
    return [y, m, d].map(this.formatLength).join('/')
  }

  /**
   * @method
   * @description 获取年-月-日
   * @param {} ms
   */
  getYMDs(ms?: Long | Date) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const y = GMT.getFullYear()
    const m = GMT.getMonth() + 1
    const d = GMT.getDate()
    // return y+'-'+m+'-'+d; // 苹果不识别'-'
    return [y, m, d].map(this.formatLength).join('-')
  }

  /**
   * @method
   * @description 获取时: 分: 秒
   * @param {} ms
   */
  getHMS(ms?: Long | Date) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const h = GMT.getHours()
    const m = GMT.getMinutes()
    const s = GMT.getSeconds()
    // return h+'-'+m+'-'+s; // 苹果不识别'-'
    return [h, m, s].map(this.formatLength).join(':')
  }

  /**
   * @method
   * @description 获取时: 分
   * @param {} ms
   */
  getHM(ms?: string | Date | number) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const h = GMT.getHours()
    const m = GMT.getMinutes()
    // return h+'-'+m+'-'+s; // 苹果不识别'-'
    return [h, m].map(this.formatLength).join(':')
  }

  /**
   * @method
   * @description 获取年/月/日 时: 分: 秒
   * @param {} ms
   */
  getYMDHMS(ms: string | Date) {
    const mss = ms || this.ms
    return this.getYMD(mss) + ' ' + this.getHMS(mss)
  }

  /**
   * @method
   * @description 获取年-月-日 时: 分: 秒
   * @param {} ms
   */
  getYMDHMSs(ms?: Long | Date) {
    const mss = ms || this.ms
    return this.getYMDs(mss) + ' ' + this.getHMS(mss)
  }

  /**
   * @method
   * @description 年月日加天数
   * @param {} ms
   */
  getAddDays(ms: Long | Date, day: number) {
    const mss = ms || this.ms
    let GMT = this.getTime(mss)
    GMT = GMT + day * 24 * 60 * 60 * 1000
    const Y = this.getY(GMT)
    const M = this.getM(GMT)
    const D = this.getD(GMT)
    return [Y, M, D].map(this.formatLength).join('-')
  }

  /**
   * @description 时分秒推算
   * @param ms
   */
  getHMSs(ms: number, h: number) {
    const newMs = this.getYMDHMSs(ms - h)
    return newMs
  }

  /**
   * @method
   * @description 获取毫秒数
   * @param {*} ms
   */
  getTime(ms: Long | Date) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    return GMT.getTime()
  }

  getObj(ms: Long) {
    const mss = ms || this.ms
    const GMT = this.GMTTest(mss)
    const Y = GMT.getFullYear()
    const M = GMT.getMonth() + 1
    const D = GMT.getDate()
    const h = GMT.getHours()
    const m = GMT.getMinutes()
    const s = GMT.getSeconds()
    return [Y, M, D, h, m, s].map(this.formatLength)
  }

  /**
   * @method
   * @description 格式化单位长度
   * @param {} ms
   */
  formatLength(ms: Long) {
    return String(ms)[1] ? ms : '0' + ms
  }
}
