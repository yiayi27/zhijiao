/**
 * @description 是否为身份证
 * @param str 身份证号
 */
export const isIDnumber = (str: string) => {
  return /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(str)
}

/**
 * @description 是否为手机号
 * @param str 手机号
 */
export const isPhone = (str: string) => {
  return /^1(3[0-9]|4[0-9]|5[0-9]|6[0-9]|7[0-9]|8[0-9]|9[1,8,9])\d{8}$/.test(str)
}

class Popup {
  /**
   * @description 提示框，默认显示2000ms
   * @param text 提示文字，
   * @param type 可不填，true为成功提示，空或false为失败提示
   */
  public toast(text: string | any, type?: boolean) {
    if (text) {
      uni.showToast({
        title: String(text),
        icon: type ? 'success' : 'none',
      })
    }
  }

  /**
   * @description 模态弹窗，显示确认和取消按钮
   * @param title 提示标题，
   * @param content 提示内容，
   * @returns  Promise 使用.then({})表示确定操作.catch({})表示取消操作
   */
  public modal({ title, content }: { title?: string; content: string }) {
    return new Promise<void>((resolve, reject) => {
      uni.showModal({
        title,
        content,
        success: (res) => {
          if (res.confirm) {
            resolve()
          } else {
            reject()
          }
        },
        fail: (err) => {
          reject(err)
        },
      })
    })
  }

  /**
   * @description 模态弹窗，只显示确认按钮
   * @param title 提示标题，
   * @param content 提示内容，
   * @returns  Promise 使用.then({})表示确定操作
   */
  public alert({ title, content }: { title?: string; content: string }) {
    return new Promise<void>((resolve) => {
      uni.showModal({
        title: title ?? '',
        content,
        showCancel: false,
        success: () => {
          resolve()
        },
        fail: (err) => {
          console.log('alert模态弹窗报错：', err)
        },
      })
    })
  }
}

export const $Popup = new Popup()

// 获取用户当前设置
export const getSetting = () => {
  return new Promise((resolve, reject) => {
    // #ifdef MP-WEIXIN
    uni.getSetting({
      success: function (res) {
        resolve(res)
      },
      fail: function (err) {
        reject(err)
      },
    })
    // #endif
    // #ifndef MP-WEIXIN
    // h5获取当前是否有权限
    if ('permissions' in navigator) {
      navigator.permissions
        .query({ name: 'geolocation' })
        .then((permissionStatus) => {
          switch (permissionStatus.state) {
            case 'granted':
              resolve({
                authSetting: {
                  'scope.userLocation': true,
                },
              })
              break
            case 'prompt':
              resolve({
                authSetting: {
                  'scope.userLocation': false,
                },
              })
              break
            case 'denied':
              resolve({
                authSetting: {
                  'scope.userLocation': false,
                },
              })
              break
            default:
              resolve({
                authSetting: {
                  'scope.userLocation': false,
                },
              })
          }
        })
        .catch((error) => {
          reject(error)
        })
    } else {
      reject('当前浏览器不支持权限查询 API。')
    }

    // #endif
  })
}
