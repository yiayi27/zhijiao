export const useHelper = () => {
  const isCurrent = (itemType: string, currentType: string) => {
    return itemType === currentType
  }
  return {
    isCurrent,
  }
}
