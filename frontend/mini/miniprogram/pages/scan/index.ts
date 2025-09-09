// 扫码开台页面
import { showError, showLoading, hideLoading, showConfirm, formatPrice } from '../../utils/util'
import apiService, { TableInfo, UserInfo, OrderInfo } from '../../services/api'
import { loginCheckAndpublishEvent } from '../../utils/loginUtil'
import type { IAppOptionExtended } from '../../app'

// 使用 CommonJS 导入方式
const loginUtil = require('../../utils/loginUtil');

// 获取全局应用实例
const app = getApp<IAppOptionExtended>();

// 最近使用的桌台类型
interface RecentTable {
  id: string;
  tableNumber: string;
  storeName: string;
  useTime: string;
}

Page({
  data: {
    openAllow: true, // 是否允许开台
    loadingUser: false, // 下拉刷新用户
    isLogin: false, // 是否已登录
    tableNumber: '', // 手动输入的桌台编号
    recentTables: [] as RecentTable[], // 最近使用的桌台
    scanResult: null as TableInfo | null, // 扫码结果
    showModal: false, // 是否显示弹窗
    userInfo: null as UserInfo | null, // 用户信息
    order: null as OrderInfo | null, // 订单信息
    isCreatingOrder: false, // 是否正在创建订单
    pricingText: '', // 计费显示文本
    isOngoing: false, // 是否正在打球
  },

  onLoad() {
    // 获取用户信息
    this.getUserInfo()
    // 获取最近使用的桌台（以后考虑的功能）
    // this.getRecentTables()
  },

  onShow() {
    if(app){
      this.setData({
        isOngoing: app.isOngoing
      })
    }
    // 判断用户是否登录，如果已经登录，则通知其他组件
    loginCheckAndpublishEvent();
    // 更新自定义 tabBar 的选中状态
    this.updateTabBarSelected();
  },

  // 更新 tabBar 选中状态
  updateTabBarSelected() {
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().setData({
        selected: 1 // 扫码页面索引为 1
      });
    }
  },

  // 获取用户信息
  async getUserInfo() {
    try {
      if (!app.isLoggedIn) {
        this.setData({ userInfo: app.customUserInfo })
      } else {
        this.setData({ userInfo: null })
      }
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  },

  // 获取最近使用的桌台
  getRecentTables() {
    try {
      // 读取缓存中的最近使用
      const recentTables = wx.getStorageSync('recentTables') || []
      this.setData({ recentTables })
    } catch (error) {
      console.error('获取最近使用桌台失败', error)
    }
  },

  // 保存最近使用的桌台
  saveRecentTable(table: TableInfo) {
    try {
      // 获取现有数据
      let recentTables: RecentTable[] = wx.getStorageSync('recentTables') || []
      
      // 限制保存10个
      if (recentTables.length >= 10) {
        recentTables.pop() // 移除最旧的一个
      }
      
      // 检查是否已存在相同桌台
      const existIndex = recentTables.findIndex(item => item.id === table.id)
      if (existIndex > -1) {
        recentTables.splice(existIndex, 1) // 如果存在则删除
      }
      
      // 添加到列表前面
      recentTables.unshift({
        id: table.id,
        tableNumber: table.tableNumber,
        storeName: table.storeName || '未知门店',
        useTime: new Date().toLocaleString()
      })
      
      // 保存到缓存
      wx.setStorageSync('recentTables', recentTables)
      
      // 更新数据
      this.setData({ recentTables })
    } catch (error) {
      console.error('保存最近使用桌台失败', error)
    }
  },

  // 开始扫码前检查登录状态
  checkLoginBeforeScan() {
    // 检查是否已登录
    if (!app.isLoggedIn) {
      return false
    }
    return true
  },

  // 1.开始扫码
  async startScan() {
    // 先检查登录状态
    if (!app.isLoggedIn) {
      loginUtil.showLoginModal(this);
      return;
    }

    // 测试使用，当作扫描二维码得到的结果
    let result = "e79af5032d1e659c4738b50a6397d2be";
    this.handleScanResult(result);


    
    // 只有在登录状态下才执行扫码，扫码动作暂时注释
    // wx.scanCode({
    //   scanType: ['qrCode'],
    //   success: (res) => {
    //     this.handleScanResult(res.result)
    //   },
    //   fail: (err) => {
    //     if (err.errMsg !== 'scanCode:fail cancel') {
    //       showError('扫码失败，请重试')
    //     }
    //   }
    // })
  },



  // 处理扫码结果
  async handleScanResult(qrCode: string) {
    try {
      showLoading('识别中...')
      
      // 调用API获取桌台信息
      const tableInfo = await apiService.scanTable(qrCode)
      // 解析出门店后，设置全局门店上下文（后续请求带 X-Store-Id）
      this.setStoreContext(tableInfo.storeId)

      // 余额/开台资格校验（在已设置门店上下文之后）
      const isAllow = await apiService.scanTableEnableCheck();
      if(!isAllow){
        hideLoading()
        await wx.navigateTo({ url: '/pages/prepay/prepay' })
        return;
      }

      hideLoading()
      
      // 检查桌台状态，并显示对应提示或页面
      await this.tableStatusCheck(tableInfo.status)
      
      const { openAllow } = this.data
      // 返回 true 表示允许开台
      if(openAllow){
        // 格式化计费信息文本
        let pricingText = '未知计费方式';
        if (tableInfo.priceRule) {
          if (tableInfo.priceRule.ruleType && tableInfo.priceRule.ruleType === 1) {
            const priceInYuan = formatPrice((tableInfo.priceRule.priceUnit || 0) * 60 * (tableInfo.priceRule.memberDiscount || 1.0));
            pricingText = `标准计费 ${priceInYuan}元/小时`;
          } else {
            pricingText = '阶梯计费';
          }
        }
        
        // 显示确认开台弹窗
        this.setData({
          scanResult: tableInfo,
          pricingText: pricingText,
          showModal: true
        })
      }

      
    } catch (error) {
      hideLoading()
      console.error('识别桌台失败', error)
      showError('无法识别此二维码，请确认是否为有效桌台码')
    }
  },

  // 关闭弹窗
  closeModal() {
    this.setData({ showModal: false })
  },

  // 阻止事件冒泡
  preventBubble() {
    // 空函数，用于阻止事件冒泡
    return;
  },

  // 关闭登录弹窗
  closeLoginModal() {
    loginUtil.closeLoginModal(this);
  },

  // 处理手动输入桌台号变化
  handleTableNumberInput(e: any) {
    this.setData({
      tableNumber: e.detail.value
    })
  },

  async tableStatusCheck(status: number){
    if (status !== 0) {
      const statusMap: Record<number, string> = {
        1: '已被使用',
        2: '维护中',
        3: '已锁定'
      }
      this.setData({
        openAllow: false
      })
      const statusText = statusMap[status] || '不可用'
      await showConfirm('桌台状态', `该桌台当前${statusText}，请选择其他桌台。`)
    }
  },

  // 处理手动确认
  async handleManualConfirm() {
    // 先检查登录状态
    if (!this.checkLoginBeforeScan()) {
      return
    }

    const { tableNumber } = this.data
    if (!tableNumber) return
    
    // 假设手动输入的是桌台编号
    // 在实际应用中，需要后端提供接口查询桌台信息
    try {
      showLoading('查询中...')
      
      // 这里假设可以通过某种方式将桌台编号转为二维码内容
      // 实际可能需要另外的API来支持根据编号查询
      const qrCode = `table_${tableNumber}`
      
      const tableInfo = await apiService.scanTable(qrCode)
      // 设置门店上下文
      this.setStoreContext(tableInfo.storeId)
      // 余额/开台资格校验
      const isAllow = await apiService.scanTableEnableCheck();
      if(!isAllow){
        hideLoading()
        await wx.navigateTo({ url: '/pages/prepay/prepay' })
        return;
      }

      hideLoading()
      
      // 检查桌台状态
      if (tableInfo.status !== 0) {
        const statusMap: Record<string, string> = {
          1: '已被使用',
          2: '维护中',
          3: '已锁定'
        }
        
        const statusText = statusMap[tableInfo.status] || '不可用'
        await showConfirm('桌台状态', `该桌台当前${statusText}，请选择其他桌台。`)
        return
      }
      
      // 格式化计费信息文本
      let pricingText = '未知计费方式';
      if (tableInfo.priceRule) {
        if (tableInfo.priceRule.ruleType && tableInfo.priceRule.ruleType === 1) {
          const priceInYuan = formatPrice((tableInfo.priceRule.priceUnit || 0) * 60);
          pricingText = `标准计费 ${priceInYuan}元/小时`;
        } else {
          pricingText = '阶梯计费';
        }
      }
      
      // 显示确认开台弹窗
      this.setData({
        scanResult: tableInfo,
        pricingText: pricingText,
        showModal: true
      })
    } catch (error) {
      hideLoading()
      console.error('查询桌台失败', error)
      showError('未找到该桌台，请确认编号是否正确')
    }
  },


  // 2.立即开台
  async handleStartTable() {
    const { scanResult, isCreatingOrder } = this.data
    
    // 如果正在创建订单或没有扫码结果，则返回
    if (isCreatingOrder || !scanResult) return
    
    // 再次检查桌台状态
    try {
      showLoading('验证桌台状态...')
      // 根据扫码结果查询一次最新的桌台信息
      const latestTableInfo = await apiService.scanTable(scanResult.id)
      hideLoading()
      await this.tableStatusCheck(latestTableInfo.status);
    } catch (error) {
      hideLoading()
      showError('验证桌台状态失败，请重试')
      return
    }
    
    try {
      this.setData({ isCreatingOrder: true })
      showLoading('开台中...')
      
      // 创建订单
      const order = await apiService.createOrder(app.grantType, scanResult.id)
      
      hideLoading()
      this.setData({ isCreatingOrder: false, isOngoing: true })
      app.isOngoing = true;

      // 保存到最近使用
      // this.saveRecentTable(scanResult)

      // 成功后跳转到计时页面
      wx.navigateTo({
        url: `/pages/timer/index?orderId=${order.id}`,
        success: () => {
          // 关闭当前弹窗
          this.closeModal()
        }
      })
    } catch (err) {
      hideLoading()
      this.setData({ isCreatingOrder: false , isOngoing: false })
      showError('开台失败，请重试')
    }
  },

  // 统一设置门店上下文：写入全局与本地（供请求拦截器读取设置 X-Store-Id）
  setStoreContext(storeId: string) {
    if (storeId) {
      try {
        console.log("设置门店上下文", storeId);
        // 全局保存（供内存读取）
        (app as any).globalData = (app as any).globalData || {}
        ;((app as any).globalData as any).storeId = storeId
        // 本地缓存（供请求拦截器/重启后读取）
        wx.setStorageSync('X-Store-Id', storeId)
      } catch (e) {
        console.warn('设置门店上下文失败', e)
      }
    }
  }
}) 