export interface Category {
  id: Long
  name?: string
  showCategoryVos?: CategoryVo[]
  [key: string]: any
}

export interface CategoryVo {
  id: Long
  [key: string]: any
}

export interface FirstCategory {
  category: Category
  navigation: Navigation[]
  swiperList: any[]
  [key: string]: any
}

export interface Navigation {
  class: (number | string)[]
  chartList: { category?: CategoryVo }[]
  [key: string]: any
}

export interface FormData {
  firstCatList: FirstCategory[]
  currentFirstCategory: FirstCategory
  selectMode?: number
  selectType?: number
  pagePadding?: number
  [key: string]: any
}
