/**
 * 从对象或对象数组中移除指定属性并赋值（支持嵌套结构）
 * @param data - 要处理的对象或对象数组
 * @param rules - 移除规则，格式：'key' | 'path.key' | { key: string, value?: any | ((item: T) => any) }
 * @returns 处理后的新对象或对象数组
 *
 * @example
 * // 移除顶层属性（对象）
 * mapToRemoveAttrs(data, ['id'])
 *
 * // 移除嵌套属性（对象数组）
 * mapToRemoveAttrs(data, ['children.id'])
 *
 * // 移除属性并赋值
 * mapToRemoveAttrs(data, [{ key: 'id', value: { version: 1 } }])
 *
 * // 移除属性并使用函数赋值
 * mapToRemoveAttrs(data, [{ key: 'id', value: (item) => ({ version: item.version }) }])
 */
export function mapToRemoveAttrs<T extends Record<string, any>>(
  data: T | T[] | null | undefined,
  rules: (string | { key: string; value?: any | ((item: T) => any) })[],
): T | T[] {
  if (!data) return Array.isArray(data) ? [] : ({} as T)

  // 处理规则
  const processRule = (rule: string | { key: string; value?: any | ((item: T) => any) }) => {
    if (typeof rule === 'string') {
      return { key: rule }
    }
    return rule
  }

  // 获取对象指定路径的值
  const getByPath = (obj: any, path: string[]): any => {
    return path.reduce((curr, key) => (curr && typeof curr === 'object' ? curr[key] : undefined), obj)
  }

  // 设置对象指定路径的值
  const setByPath = (obj: any, path: string[], value: any): void => {
    const lastIndex = path.length - 1
    const lastKey = path[lastIndex]
    let current = obj

    for (let i = 0; i < lastIndex; i++) {
      const key = path[i]
      if (!(key in current) || !current[key] || typeof current[key] !== 'object') {
        current[key] = {}
      }
      current = current[key]
    }

    if (value === undefined) {
      delete current[lastKey]
    } else {
      current[lastKey] = value
    }
  }

  // 处理单个对象的属性
  const processObject = (obj: Record<string, any>, rule: { key: string; value?: any | ((item: T) => any) }): void => {
    const paths = rule.key.split('.')
    const { value } = rule

    function setValue(obj: Record<string, any>) {
      if (value) {
        const newValue = typeof value === 'function' ? value(JSON.parse(JSON.stringify(obj)), data) : value
        Object.assign(obj, newValue)
      }
    }

    // 处理当前对象
    if (paths.length === 1) {
      setValue(obj)
      delete obj[paths[0]]
      return
    }

    // 处理嵌套路径
    const process = (current: any, pathIndex: number = 0) => {
      if (!current || typeof current !== 'object') return

      const remainingPaths = paths.slice(pathIndex)
      const target = getByPath(current, remainingPaths)

      if (target !== undefined) {
        // 添加新值
        setValue(current)
        // 删除目标属性
        setByPath(current, remainingPaths, undefined)
      }

      // 如果当前是数组，递归处理每个元素
      if (Array.isArray(current)) {
        current.forEach((item) => process(item, pathIndex))
      } else {
        // 处理下一层对象
        const nextKey = paths[pathIndex]
        if (current[nextKey] && typeof current[nextKey] === 'object') {
          process(current[nextKey], pathIndex + 1)
        }
      }
    }

    process(obj)
  }

  // 主处理逻辑
  const processData = (obj: T): T => {
    const objCopy = JSON.parse(JSON.stringify(obj))
    rules.forEach((rule) => processObject(objCopy, processRule(rule)))
    return objCopy as T
  }

  return Array.isArray(data) ? data.map(processData) : processData(data)
}
