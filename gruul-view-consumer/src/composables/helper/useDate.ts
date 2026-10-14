export const useDate = () => {
  const getTimeDifference = (endTime: number, needTime: boolean = false) => {
    const now = new Date()
    const diffTime = new Date(endTime).getTime() - now.getTime()
    const days = Math.floor(diffTime / (24 * 3600 * 1000))
    const leave1 = diffTime % (24 * 3600 * 1000)
    const hours = Math.floor(leave1 / (3600 * 1000))
    const leave2 = leave1 % (3600 * 1000)
    const minutes = Math.floor(leave2 / (60 * 1000))
    const leave3 = leave2 % (60 * 1000)
    const seconds = Math.round(leave3 / 1000)
    let date = (days > 0 ? days : '0') + '天' + (hours > 0 ? hours : '0') + '小时' + (minutes > 0 ? minutes : '0') + '分钟'

    if (needTime) {
      date += seconds > 0 ? seconds : 0 + '秒'
    }

    return date
  }
  return {
    getTimeDifference,
  }
}
