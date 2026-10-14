// 定义接口
interface Point {
  x: number
  y: number
}

interface BezierResult {
  bezierPoints: Point[]
}

const page = {
  methods: {
    bezier(points: Point[], times: number): BezierResult {
      // 0、以3个控制点为例，点A,B,C,AB上设置点D,BC上设置点E,DE连线上设置点F,则最终的贝塞尔曲线是点F的坐标轨迹。
      // 1、计算相邻控制点间距。
      // 2、根据完成时间,计算每次执行时D在AB方向上移动的距离，E在BC方向上移动的距离。
      // 3、时间每递增100ms，则D,E在指定方向上发生位移, F在DE上的位移则可通过AD/AB = DF/DE得出。
      // 4、根据DE的正余弦值和DE的值计算出F的坐标。
      // 邻控制AB点间距
      const bezierPoints: Point[] = []
      const pointsD: Point[] = []
      const pointsE: Point[] = []
      const distAb = Math.sqrt(Math.pow(points[1]['x'] - points[0]['x'], 2) + Math.pow(points[1]['y'] - points[0]['y'], 2))
      // 邻控制BC点间距
      const distBc = Math.sqrt(Math.pow(points[2]['x'] - points[1]['x'], 2) + Math.pow(points[2]['y'] - points[1]['y'], 2))
      // D每次在AB方向上移动的距离
      const eachMoveAd = distAb / times
      // E每次在BC方向上移动的距离
      const eachMoveBe = distBc / times
      // 点AB的正切
      const tanAb = (points[1]['y'] - points[0]['y']) / (points[1]['x'] - points[0]['x'])
      // 点BC的正切
      const tanBc = (points[2]['y'] - points[1]['y']) / (points[2]['x'] - points[1]['x'])
      // 点AB的弧度值
      const radiusAb = Math.atan(tanAb)
      // 点BC的弧度值
      const radiusBc = Math.atan(tanBc)
      // 每次执行
      for (let i = 1; i <= times; i++) {
        // AD的距离
        const distAd = eachMoveAd * i
        // BE的距离
        const distBe = eachMoveBe * i
        // D点的坐标
        const pointD: Point = { x: 0, y: 0 }
        pointD['x'] = distAd * Math.cos(radiusAb) + points[0]['x']
        pointD['y'] = distAd * Math.sin(radiusAb) + points[0]['y']
        pointsD.push(pointD)
        // E点的坐标
        const pointE: Point = { x: 0, y: 0 }
        pointE['x'] = distBe * Math.cos(radiusBc) + points[1]['x']
        pointE['y'] = distBe * Math.sin(radiusBc) + points[1]['y']
        pointsE.push(pointE)
        // 此时线段DE的正切值
        const tanDE = (pointE['y'] - pointD['y']) / (pointE['x'] - pointD['x'])
        // tanDE的弧度值
        const radiusDE = Math.atan(tanDE)
        // 地市DE的间距
        const distDE = Math.sqrt(Math.pow(pointE['x'] - pointD['x'], 2) + Math.pow(pointE['y'] - pointD['y'], 2))
        // 此时DF的距离
        const distDF = (distAd / distAb) * distDE
        // 此时DF点的坐标
        const pointF: Point = { x: 0, y: 0 }
        pointF['x'] = distDF * Math.cos(radiusDE) + pointD['x']
        pointF['y'] = distDF * Math.sin(radiusDE) + pointD['y']
        bezierPoints.push(pointF)
      }
      return {
        bezierPoints: bezierPoints,
      }
    },
  },
}

// 使用ES模块导出
export default page
