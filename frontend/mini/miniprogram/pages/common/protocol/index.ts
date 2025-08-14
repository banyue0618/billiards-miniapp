// 用户协议页面
Page({
  data: {},
  
  onLoad() {},
  
  onShareAppMessage() {
    return {
      title: '用户协议 - 自助台球小程序',
      path: '/pages/common/protocol/index'
    }
  }
}) 