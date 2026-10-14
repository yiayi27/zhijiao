/**
 * 是否为质数
 */
export const isPrimeNum = (num: number) => {
  for (let i = 2; i < num; i++) {
    if (num % i === 0) {
      return false
    }
  }
  return true
}

/**
 * 防抖函数 - 与lodash的debounce功能一致
 * @param func 需要防抖的函数
 * @param wait 等待时间，单位毫秒
 * @param options 配置选项 {leading: 是否在延迟开始前调用, trailing: 是否在延迟结束后调用, maxWait: 最大等待时间}
 * @returns 返回新的防抖函数
 */
export const debounce = <T extends (...args: any[]) => any>(
  func: T,
  wait = 300,
  options = {},
): T & { cancel: () => void; flush: () => ReturnType<T> } => {
  type DebounceOptions = {
    leading?: boolean
    trailing?: boolean
    maxWait?: number
  }

  const opts = options as DebounceOptions
  let lastArgs: any[] | undefined
  let result: any
  let lastCallTime: number | undefined
  let timerId: NodeJS.Timeout | undefined
  let lastInvokeTime = 0
  let boundFunc: T | undefined

  // 默认配置
  const leading = !!opts.leading
  const trailing = 'trailing' in opts ? !!opts.trailing : true
  const maxWait = opts.maxWait || wait

  function invokeFunc(time: number) {
    const args = lastArgs

    lastArgs = undefined
    boundFunc = undefined
    lastInvokeTime = time

    if (args) {
      result = func(...args)
    }
    return result
  }

  function startTimer(pendingFunc: () => any, waitTime: number) {
    return setTimeout(pendingFunc, waitTime)
  }

  function cancelTimer(id?: NodeJS.Timeout) {
    if (id !== undefined) {
      clearTimeout(id)
    }
  }

  function shouldInvoke(time: number) {
    const timeSinceLastCall = time - (lastCallTime || 0)
    const timeSinceLastInvoke = time - lastInvokeTime

    // 首次调用或者超过等待时间
    return lastCallTime === undefined || timeSinceLastCall >= wait || timeSinceLastCall < 0 || (!!opts.maxWait && timeSinceLastInvoke >= maxWait)
  }

  function remainingWait(time: number) {
    const timeSinceLastCall = time - (lastCallTime || 0)
    const timeSinceLastInvoke = time - lastInvokeTime
    const timeWaiting = wait - timeSinceLastCall

    return opts.maxWait ? Math.min(timeWaiting, maxWait - timeSinceLastInvoke) : timeWaiting
  }

  function trailingEdge(time: number) {
    timerId = undefined

    // 只有在有参数且trailing为true时执行函数
    if (trailing && lastArgs && boundFunc) {
      // 使用绑定的函数和参数
      result = boundFunc(...lastArgs)
      lastArgs = undefined
      boundFunc = undefined
      return result
    }
    lastArgs = undefined
    boundFunc = undefined
    return result
  }

  function leadingEdge(time: number) {
    lastInvokeTime = time
    // 开始计时器处理尾随的调用
    timerId = startTimer(() => trailingEdge(Date.now()), wait)
    // 如果启用leading，则立即调用
    if (leading && lastArgs && boundFunc) {
      // 使用绑定的函数和参数
      result = boundFunc(...lastArgs)
      lastArgs = undefined
      boundFunc = undefined
      return result
    }
    return result
  }

  function timerExpired() {
    const time = Date.now()
    if (shouldInvoke(time)) {
      return trailingEdge(time)
    }
    // 重启计时器
    timerId = startTimer(timerExpired, remainingWait(time))
  }

  function debounced(this: any, ...args: any[]) {
    const time = Date.now()
    const isInvoking = shouldInvoke(time)

    lastArgs = args
    // 使用Function.prototype.bind绑定this上下文，而不是保存this引用
    boundFunc = func.bind(this) as T
    lastCallTime = time

    if (isInvoking) {
      if (timerId === undefined) {
        return leadingEdge(lastCallTime)
      }
      if (opts.maxWait) {
        // 处理正常的防抖和最大等待时间的情况
        timerId = startTimer(timerExpired, wait)
        // 使用绑定的函数和参数
        result = boundFunc(...args)
        lastArgs = undefined
        boundFunc = undefined
        return result
      }
    }
    if (timerId === undefined) {
      timerId = startTimer(timerExpired, wait)
    }
    return result
  }

  // 取消防抖函数
  function cancel() {
    if (timerId !== undefined) {
      cancelTimer(timerId)
      timerId = undefined
    }
    lastInvokeTime = 0
    lastArgs = undefined
    lastCallTime = undefined
    boundFunc = undefined
  }

  // 立即执行
  function flush() {
    return timerId === undefined ? result : trailingEdge(Date.now())
  }

  // 添加取消和立即执行方法
  debounced.cancel = cancel
  debounced.flush = flush

  return debounced as T & { cancel: () => void; flush: () => ReturnType<T> }
}
