import { ElForm } from 'element-plus'

// 扩展FormItemRule类型，使validator可以返回Promise
declare module 'element-plus' {
  interface FormItemRule {
    validator?: (rule: any, value: any, callback: (error?: Error) => void) => void | Promise<void>
  }
}
