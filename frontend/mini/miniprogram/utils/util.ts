/**
 * 通用工具函数
 */

// 格式化时间
export const formatTime = (date: Date): string => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return (
    [year, month, day].map(formatNumber).join('/') +
    ' ' +
    [hour, minute, second].map(formatNumber).join(':')
  )
}

// 数字补零
export const formatNumber = (n: number): string => {
  const s = n.toString()
  return s[1] ? s : '0' + s
}

// 计算两点之间的距离（米）
export const calcDistance = (lat1: number, lng1: number, lat2: number, lng2: number): number => {
  const radLat1 = (lat1 * Math.PI) / 180
  const radLat2 = (lat2 * Math.PI) / 180
  const a = radLat1 - radLat2
  const b = (lng1 * Math.PI) / 180 - (lng2 * Math.PI) / 180
  const s =
    2 *
    Math.asin(
      Math.sqrt(
        Math.pow(Math.sin(a / 2), 2) +
          Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
      )
    )
  return s * 6378137
}

// 将距离格式化为友好显示
export const formatDistance = (distance: number): string => {
  if (distance < 1000) {
    return Math.round(distance) + 'm'
  } else {
    return (distance / 1000).toFixed(1) + 'km'
  }
}

// 金额格式化：兼容 number/string/undefined，统一保留两位小数
export const formatPrice = (amount: number | string | null | undefined): string => {
  const n = Number(amount ?? 0)
  if (Number.isNaN(n)) return '0.00'
  return n.toFixed(2)
}

// 格式化时长显示(单位：分钟)
export const formatDuration = (minutes: number): string => {
  var h = Math.floor(minutes / 60);
  var m = Math.max(minutes % 60, 1);
  var arr = [];
  if (h > 0) arr.push(h + '小时');
  if (m > 0 || h === 0) arr.push(m + '分');
  return arr.join('');
}

// 显示加载中
export const showLoading = (title: string = '加载中...'): void => {
  wx.showLoading({
    title,
    mask: true
  })
}

// 隐藏加载中
export const hideLoading = (): void => {
  wx.hideLoading()
}

// 显示提示
export const showToast = (title: string, icon: 'none' | 'success' = 'none'): void => {
  wx.showToast({
    title,
    icon,
    duration: 2000
  })
}

// 显示成功提示
export const showSuccess = (title: string): void => {
  showToast(title, 'success')
}

// 显示错误提示
export const showError = (title: string): void => {
  showToast(title)
}

// 确认框返回类型
interface ConfirmResult {
  confirm: boolean;
  cancel: boolean;
  errMsg?: string;
}

// 显示确认框
export const showConfirm = (title: string, content: string): Promise<ConfirmResult> => {
  return new Promise((resolve) => {
    wx.showModal({
      title,
      content,
      success: (res) => {
        resolve({
          confirm: res.confirm,
          cancel: res.cancel
        })
      },
      fail: () => {
        resolve({
          confirm: false,
          cancel: true,
          errMsg: 'showModal:fail'
        })
      }
    })
  })
}

// 获取当前位置
export const getLocation = (): Promise<WechatMiniprogram.GetLocationSuccessCallbackResult> => {
  return new Promise((resolve, reject) => {
    wx.getLocation({
      type: 'gcj02',
      success: resolve,
      fail: reject
    })
  })
}

// 检查位置授权
export const checkLocationAuth = async (): Promise<boolean> => {
  return new Promise((resolve) => {
    wx.getSetting({
      success: (res) => {
        if (res.authSetting['scope.userLocation']) {
          resolve(true)
        } else {
          resolve(false)
        }
      },
      fail: () => resolve(false)
    })
  })
}

// 打开位置设置页
export const openLocationSetting = async (): Promise<boolean> => {
  return new Promise((resolve) => {
    wx.openSetting({
      success: (res) => {
        resolve(!!res.authSetting['scope.userLocation'])
      },
      fail: () => resolve(false)
    })
  })
}

// 时长返回类型
interface CalculateResult {
  timeDuration: string;
  amountText: string;
}

/**
 * 标准计费
 * @param startTime 开始时间
 * @param priceUnit 单位（元/分）
 * @param memberDiscount 会员折扣，默认是1.0（1.0表示不打折）
 */
export const calculateDurationAndAmount0 = (startTime: any, priceUnit: number, memberDiscount: number): CalculateResult => {
  // 计算时长与消费金额，根据startTime
  startTime = startTime ? new Date(startTime.replace(/-/g, '/')) : new Date();
  const currentTime = new Date();
  const duration = Math.max(Math.floor((currentTime.getTime() - startTime.getTime()) / (1000 * 60)), 1); // 分钟数
  
  // 设置时长文本
  const timeDuration = formatDuration(duration);

  // 每分钟的单价 * 时长
  const actualAmount = (priceUnit || 0) * duration;
  // 格式化金额文本
  const amountText = formatPrice(actualAmount * memberDiscount);

  return {timeDuration: timeDuration, amountText: amountText};
}

/**
 * 阶梯计费
 */
export const calculateDurationAndAmount1 = (startTime: any, ladderRules: string, memberDiscount: number): CalculateResult =>{
  startTime = startTime ? new Date(startTime.replace(/-/g, '/')) : new Date();
  const currentTime = new Date();
  const durationMinutes = Math.floor((currentTime.getTime() - startTime.getTime()) / (1000 * 60)); // 分钟数
  
  // 计算消费金额
  let amount = 0

  // 阶梯计费
  const tiers = JSON.parse(ladderRules)
  
      
  // 排序确保从小到大
  tiers.sort((a: any, b: any) => a.startMinute - b.startMinute)
  
  let remainingMinutes = durationMinutes
  
  for (let i = 0; i < tiers.length; i++) {
    const tier = tiers[i]
    const nextTier = tiers[i + 1]
    
    // 计算当前阶梯的分钟数
    let tierMinutes
    
    if (nextTier) {
      // 如果有下一阶梯，则计算到下一阶梯开始前
      tierMinutes = Math.min(remainingMinutes, nextTier.startMinute - tier.startMinute)
    } else {
      // 如果是最后一阶梯，则计算剩余所有
      tierMinutes = remainingMinutes
    }
    
    // 只有在使用时间达到当前阶梯时才计算
    if (durationMinutes >= tier.startMinute) {
      amount += tierMinutes * tier.price
      remainingMinutes -= tierMinutes
    }
  }

  // 返回结算结果
  

  // 格式化金额文本
  const amountText = formatPrice(amount * memberDiscount);

  return {timeDuration: "", amountText: amountText};


}
