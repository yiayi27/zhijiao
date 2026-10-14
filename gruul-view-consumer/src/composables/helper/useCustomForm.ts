export const useCustomForm = () => {
  const isInputItem = (type: string) => {
    return ['phone', 'id', 'text', 'number'].indexOf(type) !== -1
  }
  const isDateItem = (type: string) => {
    return ['date', 'datetime', 'time'].indexOf(type) !== -1
  }
  return {
    isInputItem,
    isDateItem,
  }
}
