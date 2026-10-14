export interface IFileItem {
  url: string
  status: 'uploading' | 'success' | 'fail'
  message?: string
}

export interface IFromItem {
  key: string
  value: string | IFileItem[]
  type: string
  required: boolean
  placeholder?: string
  time?: string
}
