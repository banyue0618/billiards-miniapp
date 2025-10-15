// pages/user/feedback.ts
import { showSuccess, showError } from '../../utils/util'

Page({
  data: {
    feedbackContent: '',
    contactInfo: ''
  },

  /**
   * 反馈内容输入
   */
  onContentInput(e: any) {
    this.setData({
      feedbackContent: e.detail.value
    })
  },

  /**
   * 联系方式输入
   */
  onContactInput(e: any) {
    this.setData({
      contactInfo: e.detail.value
    })
  },

  /**
   * 提交反馈
   */
  handleSubmit() {
    const { feedbackContent, contactInfo } = this.data

    // 验证反馈内容
    if (!feedbackContent || feedbackContent.trim().length === 0) {
      showError('请输入反馈内容')
      return
    }

    if (feedbackContent.trim().length < 5) {
      showError('反馈内容至少5个字')
      return
    }

    setTimeout(() => {
      wx.hideLoading()
      // 先显示成功提示
      wx.showToast({
        title: '感谢您的反馈！',
        icon: 'success',
        duration: 1500,
        mask: true,
        success: () => {
          // Toast显示后再返回
          setTimeout(() => {
            wx.navigateBack({
              delta: 1
            })
          }, 1500)
        }
      })
    }, 800)
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '意见反馈',
      path: '/pages/user/feedback'
    }
  }
})

