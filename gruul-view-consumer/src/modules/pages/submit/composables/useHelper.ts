export const useHelper = () => {
  const isSelfPick = (key: string) => {
    return key === 'SELF_PICKUP'
  }

  const getCanUseCoupon = (coupons: any[]) => {
    return coupons.filter((item) => !!item.canUse)
  }
  return {
    isSelfPick,
    getCanUseCoupon,
  }
}
