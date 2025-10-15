// pages/user/contact.ts
import { showSuccess, showError } from '../../utils/util'

Page({
  data: {
    
  },

  /**
   * 拨打客服电话
   */
  handleCall() {
    wx.showModal({
      title: '拨打电话',
      content: '400-123-4567',
      confirmText: '拨打',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: '4001234567',
            fail: () => {
              showError('拨打失败，请稍后重试')
            }
          })
        }
      }
    })
  },

  /**
   * 复制邮箱地址
   */
  handleEmail() {
    wx.setClipboardData({
      data: 'support@billiards.com',
      success: () => {
        showSuccess('邮箱地址已复制')
      }
    })
  },

  /**
   * 在线客服
   */
  handleOnlineService() {
    wx.showToast({
      title: '客服功能开发中',
      icon: 'none',
      duration: 2000
    })
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '联系客服',
      path: '/pages/user/contact'
    }
  }
})

