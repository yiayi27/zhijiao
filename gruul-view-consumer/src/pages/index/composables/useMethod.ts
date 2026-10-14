export const useMethod = () => {
  const isCurrentTab = (currentTab: string, name?: string) => {
    if (name) {
      return currentTab === name
    } else {
      return currentTab === 'home'
    }
  }
  return {
    isCurrentTab,
  }
}
