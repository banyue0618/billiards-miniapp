// pages/user/points.ts
import { showError } from '../../utils/util'
import apiService, { PointsRecord } from '../../services/api'

Page({
  /**
   * 页面的初始数据
   */
  data: {
    // Tab相关
    activeTab: 0, // 0-全部 1-获得 2-消耗
    tabs: [
      { id: 0, name: '全部', type: 0 },
      { id: 1, name: '获得', type: 1 },
      { id: 2, name: '消耗', type: 2 }
    ],
    
    // 积分记录列表
    records: [] as PointsRecord[],
    loading: false,
    hasMore: true,
    
    // 分页参数
    pageNum: 1,
    pageSize: 20,
    total: 0,
    
    // 统计数据
    totalEarned: 0, // 累计获得
    totalConsumed: 0, // 累计消耗
    currentPoints: 0 // 当前积分
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 每次显示页面时刷新数据
    this.loadUserInfo()
    this.loadPointsRecords()
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.resetPageData()
    Promise.all([
      this.loadUserInfo(),
      this.loadPointsRecords()
    ]).finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreRecords()
    }
  },

  /**
   * 加载用户信息（获取当前积分）
   */
  async loadUserInfo() {
    try {
      const userInfo = await apiService.getUserInfo()
      this.setData({
        currentPoints: userInfo.points || 0
      })
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  },

  /**
   * 切换Tab
   */
  switchTab(e: any) {
    const tabId = e.currentTarget.dataset.tabId
    if (tabId === this.data.activeTab) return
    
    this.setData({ activeTab: tabId })
    this.resetPageData()
    this.loadPointsRecords()
  },

  /**
   * 重置分页数据
   */
  resetPageData() {
    this.setData({
      records: [],
      pageNum: 1,
      hasMore: true,
      total: 0
    })
  },

  /**
   * 加载积分记录列表
   */
  async loadPointsRecords() {
    if (this.data.loading) return
    
    try {
      this.setData({ loading: true })
      
      const currentTab = this.data.tabs[this.data.activeTab]
      const params = {
        type: currentTab.type,
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      }
      
      const result = await apiService.getPointsRecordList(params)
      // 计算统计数据
      this.calculateStatistics(result.rows)
      
      this.setData({
        records: result.rows,
        total: result.total,
        hasMore: result.rows.length >= this.data.pageSize,
        loading: false
      })
      
    } catch (error) {
      console.error('加载积分记录失败:', error)
      showError('加载积分记录失败')
      this.setData({ loading: false })
    }
  },

  /**
   * 加载更多记录
   */
  async loadMoreRecords() {
    if (this.data.loading || !this.data.hasMore) return
    
    try {
      this.setData({ 
        loading: true,
        pageNum: this.data.pageNum + 1
      })
      
      const currentTab = this.data.tabs[this.data.activeTab]
      const params = {
        type: currentTab.type,
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      }
      
      const result = await apiService.getPointsRecordList(params)
      
      this.setData({
        records: [...this.data.records, ...result.rows],
        hasMore: result.rows.length >= this.data.pageSize,
        loading: false
      })
      
    } catch (error) {
      console.error('加载更多积分记录失败:', error)
      showError('加载失败')
      this.setData({ 
        loading: false,
        pageNum: this.data.pageNum - 1 // 恢复页码
      })
    }
  },

  /**
   * 计算统计数据
   */
  calculateStatistics(records: PointsRecord[]) {
    let earned = 0
    let consumed = 0
    
    records.forEach(record => {
      if (record.type === 1) {
        // 获得
        earned += record.points
      } else if (record.type === 2) {
        // 消耗
        consumed += record.points
      }
    })
    
    this.setData({
      totalEarned: earned,
      totalConsumed: consumed
    })
  },

  /**
   * 格式化时间
   */
  formatDate(dateStr: string): string {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const dayDiff = Math.floor(diff / (1000 * 60 * 60 * 24))
    
    if (dayDiff === 0) {
      return '今天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    } else if (dayDiff === 1) {
      return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    } else if (dayDiff < 7) {
      return `${dayDiff}天前`
    } else {
      return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '我的积分记录',
      path: '/pages/user/points'
    }
  }
})