import { getArea } from '@/api/global'
import address from './area.json'

interface AddressItem {
  label: string
  value: string | number
  list?: AddressItem[]
}

class City {
  address = address as AddressItem[]

  province: AddressItem[] = []

  constructor() {
    // this.address
    getArea({ type: 1 }).then((res) => {
      this.province = res.data
    })
  }

  getCityAsync(id: string | number) {
    return getArea({ type: 2, id }).then((res) => res.data)
  }

  getProvince() {
    const list: AddressItem[] = []
    for (const item of this.address) {
      list.push({
        label: item.label,
        value: item.value,
      })
    }
    return list
  }

  getCity(id: string | number) {
    const list: AddressItem[] = []
    for (const item of this.address) {
      if (item.value === id) {
        for (const province of item.list || []) {
          list.push({
            label: province.label,
            value: province.value,
          })
        }
      }
    }
    return list
  }

  getArea(province: string | number, city: string | number) {
    const list: AddressItem[] = []
    for (const item of this.address) {
      if (item.value === province) {
        for (const provinceItem of item.list || []) {
          if (provinceItem.value === city) {
            for (const cityItem of provinceItem.list || []) {
              list.push({
                label: cityItem.label,
                value: cityItem.value,
              })
            }
          }
        }
      }
    }
    return list
  }

  returnAddress(province: string | number, city: string | number) {
    const p = this.getProvince()
    const c = this.getCity(province)
    const a = this.getArea(province, city)
    return {
      province: p,
      city: c,
      area: a,
    }
  }

  getAddressName(list: AddressItem[], value: string | number) {
    let name = ''
    if (list.length > 0) {
      for (const item of list) {
        if (item.value === value) {
          name = item.label
          break
        }
      }
    }
    return name
  }
}

export default new City()
