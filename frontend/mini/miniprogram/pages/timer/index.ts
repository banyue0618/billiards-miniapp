// 计时器页面
import { formatTime, formatDuration, formatPrice, showError, showLoading, hideLoading, showConfirm, calculateDurationAndAmount1, calculateDurationAndAmount0 } from '../../utils/util'
import apiService, { OrderInfo, TableInfo, UserInfo } from '../../services/api'
import type { IAppOptionExtended } from '../../app'

const app = getApp<IAppOptionExtended>();


Page({
  data: {
    orderId: '', // 订单ID
    order: null as OrderInfo | null, // 订单信息
    tableInfo: null as TableInfo | null, // 桌台信息
    userInfo: null as UserInfo | null, // 用户信息
    startTimeText: '', // 开始时间文本
    endTimeText: '', // 结束时间文本
    timerText: '00:00:00', // 计时器文本
    amountText: '0.00', // 当前金额文本
    finalAmountText: '0.00', // 最终结算金额文本
    durationText: '', // 使用时长文本
    timer: null as number | null, // 计时器
    showModal: false, // 是否显示结算弹窗
    isEnding: false, // 是否正在结束
    prepayModel: true // 是否预充值模式
  },

  onLoad(options) {
    const { orderId } = options
    console.log("onload" + orderId)
    if (!orderId) {
      showError('订单ID不能为空')
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
      return
    }
    
    this.setData({ orderId })
    
    // 获取用户信息
    this.getUserInfo()
    
    // 加载订单信息
    this.loadOrderDetail()
  },

  onShow() {
    // 获取当前页面实例
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    
    // 获取当前页面的路由参数
    const options = currentPage.options;
    const newOrderId = options.orderId;
    
    // 如果存在新的订单ID且与当前不同，则更新
    if (newOrderId && newOrderId !== this.data.orderId) {
      // 停止之前的计时器
      this.stopTimer();
      
      // 更新订单ID并重新加载订单
      this.setData({ orderId: newOrderId });
      this.loadOrderDetail();
    } 
    // 如果有订单信息但没有计时器，启动计时器
    else if (this.data.order && !this.data.timer) {
      this.startTimer();
    }
  },

  onHide() {
    // 停止计时器
    this.stopTimer()
  },

  onUnload() {
    // 停止计时器
    this.stopTimer()
  },

  // 获取用户信息
  async getUserInfo() {
    try {
      const userInfo = await apiService.getUserInfo()
      this.setData({ userInfo })
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  },

  // 加载订单详情
  async loadOrderDetail() {
    const { orderId } = this.data
    
    try {
      showLoading('加载中...')
      
      // 获取订单详情
      const order = await apiService.getOrderDetail(orderId)
      
      hideLoading()
      
      // 如果订单已结束，则返回订单列表
      // if (order.status !== '0') {
      //   showError('该订单已结束')
      //   setTimeout(() => {
      //     wx.redirectTo({
      //       url: '/pages/order/list'
      //     })
      //   }, 1500)
      //   return
      // }
      
      // 更新数据
      this.setData({ order })
      
      // 格式化开始时间
      // const startTimeText = formatTime(new Date(order.startTime))
      // this.setData({ startTimeText })
      
      // 获取桌台信息
      // this.loadTableInfo(order.storeId, order.tableId)
      
      // 启动计时器
      this.startTimer()
    } catch (error) {
      hideLoading()
      console.error('加载订单详情失败', error)
      showError('加载订单详情失败')
      
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }
  },

  // 加载桌台信息
  async loadTableInfo(storeId: string, tableId: string) {
    try {
      // 获取门店所有桌台
      const tables = await apiService.getStoreTables(storeId)
      
      // 找到当前桌台
      const tableInfo = tables.find(table => table.id === tableId) || null
      
      this.setData({ tableInfo })
    } catch (error) {
      console.error('加载桌台信息失败', error)
    }
  },

  // 启动计时器
  startTimer() {
    // 先清除可能存在的计时器
    this.stopTimer()
    
    // 创建新计时器，每秒更新一次
    const timer = setInterval(() => {
      this.updateTimer()
    }, 60 * 1000) as unknown as number
    
    this.setData({ timer })
    
    // 立即执行一次更新
    this.updateTimer()
  },

  // 停止计时器
  stopTimer() {
    if (this.data.timer) {
      clearInterval(this.data.timer)
      this.setData({ timer: null })
    }
  },

  // 更新计时器
  updateTimer() {
    const { order } = this.data
    if (!order) return
    if(order.ladderRules){
      const {timeDuration, amountText} = calculateDurationAndAmount1(order.startTime, order.ladderRules || '', order.memberDiscount || 1.0)
      this.setData({
        timeDuration,
        amountText
      })
    }else{
      const {timeDuration, amountText} = calculateDurationAndAmount0(order.startTime, order.priceUnit || 0, order.memberDiscount || 1.0)
      this.setData({
        timeDuration,
        amountText
      })
    }
    
  },

  // 结束使用
  async handleEndOrder() {
    // 再次确认
    const res = await showConfirm('确认结束使用？', '结束后将自动计算费用')
    
    if (!res.confirm) return
    
    const { orderId, isEnding } = this.data
    
    // 如果正在结束，则返回
    if (isEnding) return
    
    try {
      this.setData({ isEnding: true })
      showLoading('结算中...')
      
      // 结束订单（返回 OrderInfo 对象，包含 actualAmount/duration/endTime 等）
      const result = await apiService.endOrder(orderId)
      if(app){
        app.isOngoing = false;
      }
      
      hideLoading()
      
      // 更新结算信息
      console.log(result)
      const storeName = result.storeName
      const tableNumber = result.tableNumber
      const startTimeText = formatTime(new Date(result.startTime || new Date()))
      const endTimeText = formatTime(new Date(result.endTime || new Date()))
      const durationText = formatDuration(result.duration || 0)
      const finalAmountText = formatPrice(result.actualAmount || 0)
      
      this.setData({
        storeName,
        tableNumber,
        startTimeText,
        endTimeText,
        durationText,
        finalAmountText,
        showModal: true
      })
      
      // 停止计时器
      this.stopTimer()
    } catch (error) {
      hideLoading()
      this.setData({ isEnding: false })
      console.error('结束订单失败', error)
      showError('结束订单失败，请重试')
    }
  },

  // 确认支付
  handleConfirmPayment() {
    const { orderId } = this.data
    
    // 跳转到支付页面
    wx.navigateTo({
      url: `/pages/payment/index?orderId=${orderId}`
    })
  },

  // 返回首页
  handleBackToHome() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  }
}) 