export interface IComponentItem {
  value: string
  label: string
  icon: string
  type?: boolean
  showType?: boolean // 根据商城模式做对应显示设置
}

export interface IComponent extends IComponentItem {
  id: number
  formData: any
}
