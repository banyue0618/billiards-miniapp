// 订单列表页面
import { formatPrice, formatDuration, showLoading, hideLoading, showError } from '../../utils/util'
import apiService, { OrderInfo } from '../../services/api'

// 每页显示数量
const PAGE_SIZE = 10

Page({
  data: {
    currentTab: 'all', // 当前选中的状态标签
    orders: [] as OrderInfo[], // 订单列表
    pageNum: 1, // 当前页码
    hasMore: true, // 是否有更多数据
    loading: false // 是否加载中
  },

  onLoad() {
    // 加载订单列表
    this.loadOrders()
  },

  onShow() {
    // 每次进入页面都刷新数据
    this.refreshData()
  },

  onPullDownRefresh() {
    // 下拉刷新
    this.refreshData(true)
  },

  onReachBottom() {
    // 触底加载更多
    if (this.data.hasMore && !this.data.loading) {
      this.loadMore()
    }
  },

  // 刷新数据
  refreshData(showRefresh = false) {
    // 重置页码
    this.setData({
      pageNum: 1,
      hasMore: true
    })

    // 重新加载数据
    this.loadOrders(showRefresh)
  },

  // 切换标签
  switchTab(e: any) {
    const tab = e.currentTarget.dataset.tab
    
    // 如果点击当前选中的标签，不做任何操作
    if (tab === this.data.currentTab) return
    
    this.setData({
      currentTab: tab,
      orders: [], // 清空订单列表
      pageNum: 1, // 重置页码
      hasMore: true // 重置加载状态
    })
    
    // 加载对应标签的数据
    this.loadOrders()
  },

  // 加载订单列表
  async loadOrders(showRefresh = false) {
    const { currentTab, pageNum } = this.data
    
    // 避免重复加载
    if (this.data.loading) return
    
    // 设置加载状态
    this.setData({ loading: true })
    
    if (!showRefresh) {
      showLoading('加载中...')
    }
    
    try {
      // 准备请求参数
      const params: any = {
        pageNum,
        pageSize: PAGE_SIZE
      }
      
      // 根据当前标签设置状态过滤
      if (currentTab !== 'all') {
        params.status = currentTab == 'ongoing' ? 0 : 1
      }
      
      // 请求API
      const response = await apiService.getUserOrders(params)
      
      // 更新数据
      const { records, total } = response
      
      // 格式化订单时间
      const orders = pageNum === 1 ? records : [...this.data.orders, ...records]
      
      // 检查是否还有更多数据
      const hasMore = orders.length < total
      
      this.setData({
        orders,
        hasMore
      })
      
      if (showRefresh) {
        wx.stopPullDownRefresh()
      }
    } catch (error) {
      console.error('加载订单列表失败', error)
      showError('加载失败，请重试')
      
      if (showRefresh) {
        wx.stopPullDownRefresh()
      }
    } finally {
      // 取消加载状态
      this.setData({ loading: false })
      
      if (!showRefresh) {
        hideLoading()
      }
    }
  },

  // 加载更多
  loadMore() {
    // 页码+1
    this.setData({
      pageNum: this.data.pageNum + 1
    })
    
    // 加载下一页数据
    this.loadOrders()
  },

  // 查看订单详情
  viewOrderDetail(e: any) {
    const id = e.currentTarget.dataset.id
    
    wx.navigateTo({
      url: `/pages/order/detail?id=${id}`
    })
  },

  // 查看计时页面
  navigateToTimer(e: any) {
    const id = e.currentTarget.dataset.id
    
    wx.navigateTo({
      url: `/pages/timer/index?orderId=${id}`
    })
  },
  
  // 格式化价格
  formatPrice(cents: number) {
    return formatPrice(cents)
  },
  
  // 格式化使用时长
  formatDuration(minutes: number) {
    return formatDuration(minutes)
  },
  
  // 格式化日期
  formatDate(dateStr: string) {
    const date = new Date(dateStr)
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hour}:${minute}`
  }
}) 