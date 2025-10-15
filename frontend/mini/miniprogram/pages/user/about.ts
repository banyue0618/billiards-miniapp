// pages/user/about.ts
Page({
  data: {
    
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '关于台球神探',
      path: '/pages/user/about'
    }
  }
})

