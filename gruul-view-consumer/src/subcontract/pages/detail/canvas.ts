// 定义接口
interface Good {
  actPrice: Long
  price: Long
  name: string
  integralPrice: Long
  guidePrice: Long
  integralGoodName: string
}

interface WordsOptions {
  canvas: any // UniApp画布上下文
  fontSize: number
  word: string
  maxWidth: number
  x: number
  y: number
  maxLine: number
}

const page = {
  methods: {
    canvCard(good: Good, type: number, shareImgUrl: string, codeImg: string, componentInstance?: any): Promise<string> {
      const options = {
        actPrice: type === 0 ? good.actPrice : good.integralPrice,
        price: type === 0 ? good.price : good.guidePrice,
        goodsName: type === 0 ? good.name : good.integralGoodName,
        desc: '测试商品描述测试商品描述测试商品描述',
      }
      return new Promise((resolve) => {
        // const baseX = 94;

        const textPosY = 500

        const priceY = textPosY + 95

        const goodsNameY = textPosY + 25

        const canvas = uni.createCanvasContext('cardCanvas', componentInstance)

        canvas.save()
        canvas.rect(0, 0, 560, 880)
        canvas.setFillStyle('white')
        canvas.fill()
        canvas.drawImage(shareImgUrl, 74, 64, 420, 420)
        canvas.save()

        canvas.beginPath()
        canvas.setFillStyle('#050505')
        this.dealWords({
          // 画布上下文
          canvas,
          // 字体大小
          fontSize: 28,
          // 需要处理的文字
          word: `${options.goodsName}`,
          // 一行文字最大宽度
          maxWidth: 500,
          // 文字在x轴要显示的位置
          x: 40,
          // 文字在y轴要显示的位置
          y: goodsNameY,
          // 文字最多显示的行数
          maxLine: 1,
        })
        canvas.setFontSize(26)
        canvas.setFillStyle('red')
        canvas.fillText(`${type === 0 ? '￥' : '积分'}`, 40, priceY + 10, 50)
        canvas.setFontSize(34)

        canvas.fillText(`${options.actPrice}`, Number(`${type === 0 ? 0 : 30}`) + 65, priceY + 10, 150)
        canvas.fillText(`${options.actPrice}`, Number(`${type === 0 ? 0 : 31}`) + 66, priceY + 10, 150)
        canvas.fillText(`${options.actPrice}`, Number(`${type === 0 ? 0 : 29}`) + 66, priceY + 11, 150)
        if (options.price) {
          const priceWidth = canvas.measureText(`${options.actPrice}`)
          let newWidth = 85
          let temWidth = 105
          uni.getSystemInfo({
            success(res) {
              if (res.model.indexOf('iPhone X') > -1) {
                newWidth = 115
                temWidth = 135
              }
            },
          })
          console.log(newWidth, priceWidth.width, `${type === 0 ? 50 + priceWidth.width : newWidth + priceWidth.width}`)
          canvas.setFontSize(22)
          canvas.setFillStyle('#9E9E9E')
          canvas.fillText(`￥${options.price}`, Number(`${type === 0 ? 50 + priceWidth.width : newWidth + priceWidth.width}`) + 30, priceY + 10, 150)
          const delPriceWidth = canvas.measureText(`${options.price}`)
          canvas.moveTo(Number(`${type === 0 ? 50 + priceWidth.width : newWidth + priceWidth.width}`) + 30, priceY + 2)
          canvas.lineTo(
            Number(`${type === 0 ? 70 + priceWidth.width + delPriceWidth.width : temWidth + priceWidth.width + delPriceWidth.width}`) + 35,
            priceY + 2,
          )
          canvas.setStrokeStyle('#9E9E9E')
        }
        canvas.stroke()

        canvas.drawImage(codeImg, 320, textPosY + 150, 180, 180)
        canvas.setFontSize(24)
        canvas.setFillStyle('#050505')
        canvas.fillText(`长按识别小程序码`, 40, textPosY + 225, 250)
        canvas.setFontSize(24)
        canvas.fillText(`更多爆款等你来抢`, 40, textPosY + 265, 250)
        canvas.draw(false, () => {
          uni.canvasToTempFilePath({
            canvasId: 'cardCanvas',
            success(res) {
              resolve(res.tempFilePath)
            },
          })
        })
      })
    },

    dealWords(options: WordsOptions): void {
      options.canvas.setFontSize(options.fontSize) // 设置字体大小
      const allRow = Math.ceil(options.canvas.measureText(`${options.word}`).width / options.maxWidth) // 实际总共能分多少行
      const count = allRow >= options.maxLine ? options.maxLine : allRow // 实际能分多少行与设置的最大显示行数比，谁小就用谁做循环次数

      const endPos = 0 // 当前字符串的截断点
      for (let j = 0; j < count; j++) {
        const nowStr = options.word.slice(endPos) // 当前剩余的字符串
        const rowWid = 0 // 每一行当前宽度
        if (options.canvas.measureText(`${nowStr}`).width > options.maxWidth) {
          // 如果当前的字符串宽度大于最大宽度，然后开始截取
          this.dealWidth(endPos, j, rowWid, nowStr, options)
        } else {
          // 如果当前的字符串宽度小于最大宽度就直接输出
          options.canvas.fillText(nowStr.slice(0), options.x, options.y + (j + 1) * 18)
        }
      }
    },

    dealWidth(endPos: number, j: number, rowWid: number, nowStr: string, options: WordsOptions): void {
      for (let m = 0; m < nowStr.length; m++) {
        rowWid += options.canvas.measureText(`${nowStr[m]}`).width // 当前字符串总宽度
        if (rowWid > options.maxWidth) {
          if (j === options.maxLine - 1) {
            // 如果是最后一行
            options.canvas.fillText(nowStr.slice(0, m - 1) + '...', options.x, options.y + (j + 1) * 18) // (j+1)*18这是每一行的高度
          } else {
            options.canvas.fillText(nowStr.slice(0, m), options.x, options.y + (j + 1) * 18)
          }
          endPos += m // 下次截断点
          break
        }
      }
    },
  },
}

// 使用ES模块导出
export default page
