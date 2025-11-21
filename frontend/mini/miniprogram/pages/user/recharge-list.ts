// 充值记录列表页面
import { showError, showLoading, hideLoading, showConfirm, showSuccess } from '../../utils/util'
import apiService from '../../services/api'
const loginUtil = require('../../utils/loginUtil')

interface RechargeRecord {
  id: string
  payNo: string
  amount: number
  paymentStatus: number
  statusText: string
  createTime: string
  transactionId?: string
  merchantName?: string
  hasRefund: boolean
  refundStatus?: number
  refundStatusText?: string
  refundAmount?: number
}

Page({
  data: {
    rechargeList: [] as RechargeRecord[],
    loading: false,
    userBalance: undefined as number | undefined
  },

  onLoad() {
    // 检查登录状态
    if (!loginUtil.isLoggedIn()) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
      return
    }

    this.loadRechargeList()
  },

  onPullDownRefresh() {
    this.loadRechargeList()
  },

  async loadRechargeList() {
    try {
      this.setData({ loading: true })
      showLoading('加载中...')

      // 并行获取充值记录和用户信息
      const [list, userInfo] = await Promise.all([
        apiService.getRechargeList(),
        apiService.getUserInfo()
      ])
      
      // 处理数据，添加状态文本
      const processedList = list.map((item: any) => {
        let statusText = '未知'
        if (item.paymentStatus === 0) {
          statusText = '待支付'
        } else if (item.paymentStatus === 1) {
          statusText = '支付中'
        } else if (item.paymentStatus === 2) {
          statusText = '已支付'
        } else if (item.paymentStatus === 4) {
          statusText = '支付失败'
        } else if (item.paymentStatus === 8) {
          statusText = '支付取消'
        }

        let refundStatusText = ''
        if (item.refundStatus === 0) {
          refundStatusText = '退款中'
        } else if (item.refundStatus === 1) {
          refundStatusText = '退款成功'
        } else if (item.refundStatus === 2) {
          refundStatusText = '退款失败'
        }

        return {
          ...item,
          statusText,
          refundStatusText,
          hasRefund: item.refundStatus !== 1
        }
      })

      this.setData({ 
        rechargeList: processedList,
        userBalance: userInfo.balance,
        loading: false
      })

      hideLoading()
      wx.stopPullDownRefresh()
    } catch (error: any) {
      console.error('加载充值记录失败', error)
      this.setData({ loading: false })
      hideLoading()
      wx.stopPullDownRefresh()
      showError(error.message || '加载失败')
    }
  },

  async handleRefund(e: any) {
    const { id, amount } = e.currentTarget.dataset

    const res = await showConfirm(
      '确认退款',
      `确定要申请退款 ¥${amount} 吗？\n\n退款说明：\n• 退款将原路返回到您的支付账户\n• 退款成功后，该金额将从您的钱包余额中扣除\n• 退款处理时间通常为1-3个工作日`
    )

    if (!res.confirm) {
      return
    }

    try {
      showLoading('提交中...')
      
      await apiService.applyRefund(id)
      
      hideLoading()
      showSuccess('退款申请已提交')
      
      // 重新加载列表
      this.loadRechargeList()
    } catch (error: any) {
      hideLoading()
      console.error('申请退款失败', error)
      showError(error.message || '申请退款失败')
    }
  }
})

