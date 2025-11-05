// 预约记录列表页面
import { showError, showLoading, hideLoading, showConfirm, showSuccess } from '../../utils/util'
import apiService, { ReservationItem } from '../../services/api'
const loginUtil = require('../../utils/loginUtil')

Page({
  data: {
    reservationList: [] as ReservationItem[],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    hasMore: true,
    selectedStatus: undefined as number | undefined, // 选中的状态筛选
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

    this.loadReservationList(true)
  },

  onPullDownRefresh() {
    this.setData({
      pageNum: 1,
      hasMore: true
    })
    this.loadReservationList(true)
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadReservationList(false)
    }
  },

  /**
   * 加载预约记录列表
   * @param refresh 是否刷新（true=重置页码，false=加载更多）
   */
  async loadReservationList(refresh: boolean = true) {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })
      
      if (refresh) {
        showLoading('加载中...')
      }

      const pageNum = refresh ? 1 : this.data.pageNum + 1
      
      const params: any = {
        pageNum,
        pageSize: this.data.pageSize
      }
      
      // 如果有状态筛选，添加到参数中
      if (this.data.selectedStatus !== undefined) {
        params.status = this.data.selectedStatus
      }

      const result = await apiService.getReservationList(params)
      
      let list = result.records || []
      
      // 处理数据，添加状态文本和格式化时间
      list = list.map((item: ReservationItem) => {
        return {
          ...item,
          statusText: this.getStatusText(item.status),
          payStatusText: this.getPayStatusText(item.payStatus),
          formattedTime: this.formatReservationTime(item.startTime, item.endTime),
          formattedStartTime: this.formatDateTime(item.startTime),
          formattedEndTime: this.formatDateTime(item.endTime),
          formattedAmount: item.payAmount ? `¥${(item.payAmount / 100).toFixed(2)}` : '',
        }
      })

      const newList = refresh ? list : [...this.data.reservationList, ...list]
      const hasMore = newList.length < result.total

      this.setData({
        reservationList: newList,
        pageNum,
        total: result.total,
        hasMore,
        loading: false
      })

      if (refresh) {
        hideLoading()
        wx.stopPullDownRefresh()
      }
    } catch (error: any) {
      console.error('加载预约记录失败', error)
      this.setData({ loading: false })
      hideLoading()
      wx.stopPullDownRefresh()
      showError(error.message || '加载失败')
    }
  },

  /**
   * 获取状态文本
   */
  getStatusText(status: number): string {
    const statusMap: Record<number, string> = {
      0: '预约中',
      1: '已到店',
      2: '已完成',
      3: '已取消',
      4: '已过期'
    }
    return statusMap[status] || '未知'
  },

  /**
   * 获取支付状态文本
   */
  getPayStatusText(payStatus?: number): string {
    if (payStatus === undefined || payStatus === null) {
      return ''
    }
    const payStatusMap: Record<number, string> = {
      0: '未支付',
      1: '已支付',
      2: '已退款'
    }
    return payStatusMap[payStatus] || ''
  },

  /**
   * 格式化预约时间段显示
   */
  formatReservationTime(startTime: string, endTime: string): string {
    try {
      const start = new Date(startTime.replace(/-/g, '/'))
      const end = new Date(endTime.replace(/-/g, '/'))
      
      const formatDate = (date: Date) => {
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${month}-${day}`
      }
      
      const formatTime = (date: Date) => {
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        return `${hours}:${minutes}`
      }
      
      const startDateStr = formatDate(start)
      const endDateStr = formatDate(end)
      
      if (startDateStr === endDateStr) {
        return `${startDateStr} ${formatTime(start)}-${formatTime(end)}`
      } else {
        return `${startDateStr} ${formatTime(start)} - ${endDateStr} ${formatTime(end)}`
      }
    } catch (error) {
      console.error('格式化时间失败', error)
      return `${startTime} - ${endTime}`
    }
  },

  /**
   * 格式化日期时间
   */
  formatDateTime(dateTimeStr: string): string {
    try {
      const date = new Date(dateTimeStr.replace(/-/g, '/'))
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${month}-${day} ${hours}:${minutes}`
    } catch (error) {
      return dateTimeStr
    }
  },

  /**
   * 状态筛选切换
   */
  onStatusFilterTap(e: any) {
    const { status } = e.currentTarget.dataset
    // 将字符串转换为数字，空字符串转为undefined
    const statusNum = status === '' ? undefined : Number(status)
    const selectedStatus = statusNum === this.data.selectedStatus ? undefined : statusNum
    
    this.setData({
      selectedStatus,
      pageNum: 1,
      hasMore: true
    })
    
    this.loadReservationList(true)
  },

  /**
   * 取消预约
   */
  async handleCancel(e: any) {
    const { id, reservationNo } = e.currentTarget.dataset
    
    const res = await showConfirm(
      '确认取消',
      `确定要取消预约 ${reservationNo} 吗？`
    )

    if (!res.confirm) {
      return
    }

    try {
      showLoading('取消中...')
      
      await apiService.cancelReservation(id)
      
      hideLoading()
      showSuccess('取消成功')
      
      // 重新加载列表
      this.setData({
        pageNum: 1,
        hasMore: true
      })
      this.loadReservationList(true)
    } catch (error: any) {
      hideLoading()
      console.error('取消预约失败', error)
      showError(error.message || '取消预约失败')
    }
  }
})
