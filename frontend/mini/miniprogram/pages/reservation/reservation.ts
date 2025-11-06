import api from '../../services/api'
import { StoreDetail, TimeSlot, TableTag, CurrentReservation } from '../../services/api'
import type { IAppOptionExtended } from '../../app'
const loginUtil = require('../../utils/loginUtil')
const { showConfirm } = require('../../utils/util')

const app = getApp<IAppOptionExtended>();

interface TableItem {
  id: string;
  name: string;
  statusText: string;
  description?: string;
  image?: string; // 桌台图片
  tags?: TableTag[]; // 标签列表
  slots: TimeSlot[];
  selectedSlot?: TimeSlot;
  // 范围选择相关
  startIndex?: number; // 起始时间段索引
  endIndex?: number;   // 结束时间段索引
}

Page({
  /**
   * 页面的初始数据
   */
  data: {
    tables: [] as TableItem[],
    selectedDate: '',
    selectedTableId: '',
    selectedTable: null as TableItem | null, // 当前选择的表格信息
    storeId: '', // 门店ID
    store: null as StoreDetail | null, // 门店信息
    currentReservation: null as CurrentReservation | null, // 当前预约记录
    countdownText: '', // 倒计时文本
    countdownTimer: null as number | null, // 倒计时定时器
    isLogin: false, // 是否已登录
    showLoginModal: false, // 是否显示登录弹窗
  },

  /**
   * 生命周期函数--监听页面加载
   */
  async onLoad(options: any) {
    try {
      console.log('预约页面 onLoad 开始', options)
      
      // 更新自定义 tabBar 的选中状态
      this.updateTabBarSelected();
      
      // 优先从全局变量获取门店ID（通过 switchTab 跳转时使用）
      // 如果没有，则从 options 中获取（兼容直接点击 tabBar 或其他方式跳转）
      let storeId = app.globalData.reservationStoreId || options.storeId || ''
      // 获取后清空全局变量，避免影响下次跳转
      if (app.globalData.reservationStoreId) {
        app.globalData.reservationStoreId = undefined
      }
      
      const date = options.date || this.getTodayDate()
      let needLoadStoreInfo = false // 是否需要加载门店信息
      
      this.setData({
        selectedDate: date,
        storeId: storeId,
      })

      // 如果没有传入门店ID，自动获取最近的门店
      if (!storeId) {
        console.log('未传入门店ID，开始获取最近门店')
        await this.loadNearestStore()
        storeId = this.data.storeId
        console.log('获取到的门店ID:', storeId)
        // 如果通过 loadNearestStore 获取了门店信息，已经设置了 store，不需要再调用 loadStoreInfo
        needLoadStoreInfo = false
      } else {
        // 如果传入了门店ID，需要加载门店信息
        needLoadStoreInfo = true
      }

      // 如果还是没有门店ID，显示提示并返回
      if (!storeId) {
        console.warn('无法获取门店ID，页面无法加载数据')
        wx.showToast({
          title: '无法获取门店信息',
          icon: 'none',
          duration: 2000,
        })
        return
      }

      // 如果需要加载门店信息，则调用（只有当传入storeId参数时才需要）
      if (needLoadStoreInfo) {
        this.loadStoreInfo(storeId)
      }

      // 加载桌台数据
      console.log('开始加载桌台数据，storeId:', storeId, 'date:', date)
      this.loadTables(storeId, date)
      
      // 检查登录状态并加载当前预约记录
      this.checkLoginAndLoadReservation()
    } catch (error: any) {
      console.error('预约页面 onLoad 出错:', error)
      wx.showToast({
        title: '页面加载失败',
        icon: 'none',
        duration: 2000,
      })
    }
  },

  /**
   * 加载最近的门店
   */
  async loadNearestStore() {
    try {
      // 检查是否已获取位置信息
      if (!app.userLocation || !app.userLocation.latitude || !app.userLocation.longitude) {
        wx.showLoading({
          title: '获取位置信息...',
          mask: true,
        })
        
        // 使用 LocationService 获取位置信息
        const location = await app.locationService.requestLocation()
        
        if (!location || !location.latitude || !location.longitude) {
          wx.hideLoading()
          wx.showToast({
            title: '获取位置信息失败，无法查找附近门店',
            icon: 'none',
            duration: 2000,
          })
          return
        }
        
        wx.hideLoading()
      }

      wx.showLoading({
        title: '查找附近门店...',
        mask: true,
      })

      const nearestStore = await api.getNearestStore({
        latitude: app.userLocation!.latitude,
        longitude: app.userLocation!.longitude,
      })

      if (nearestStore && nearestStore.id) {
        this.setData({
          storeId: nearestStore.id,
          store: nearestStore
        })
      } else {
        wx.showToast({
          title: '未找到附近门店',
          icon: 'none',
          duration: 2000,
        })
      }
    } catch (error: any) {
      console.error('获取最近门店失败', error)
      wx.showToast({
        title: error?.message || '获取最近门店失败',
        icon: 'none',
        duration: 2000,
      })
    } finally {
      wx.hideLoading()
    }
  },

  /**
   * 加载门店信息
   */
  async loadStoreInfo(storeId: string) {
    try {
      const storeDetailParam = {
        storeId: storeId,
        latitude: app.userLocation ? app.userLocation.latitude : 0,
        longitude: app.userLocation ? app.userLocation.longitude : 0
      }
      
      const store = await api.getStoreDetail(storeDetailParam)
      
      this.setData({
        store: store,
      })
    } catch (error: any) {
      console.error('加载门店信息失败', error)
      // 门店信息加载失败不影响预约功能，静默处理
    }
  },

  /**
   * 获取今天的日期字符串
   */
  getTodayDate(): string {
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  /**
   * 检查登录状态并加载预约记录
   */
  checkLoginAndLoadReservation() {
    const isLoggedIn = loginUtil.isLoggedIn()
    this.setData({
      isLogin: isLoggedIn
    })
    
    if (isLoggedIn) {
      // 已登录，加载当前预约记录
      this.loadCurrentReservation()
    } else {
      // 未登录，清除预约记录
      this.setData({
        currentReservation: null,
        countdownText: ''
      })
      this.clearCountdown()
    }
  },

  /**
   * 加载当前预约记录
   */
  async loadCurrentReservation() {
    try {
      const reservation = await api.getCurrentReservation()
      
      if (reservation) {
        // 格式化时间段显示
        const formattedTime = this.formatReservationTime(reservation.startTime, reservation.endTime)
        
        this.setData({
          currentReservation: {
            ...reservation,
            formattedTime: formattedTime
          }
        })
        
        // 启动倒计时
        this.startCountdown()
      } else {
        this.setData({
          currentReservation: null,
          countdownText: ''
        })
        
        // 清除倒计时
        this.clearCountdown()
      }
    } catch (error: any) {
      console.error('加载当前预约失败', error)
      // 加载失败不影响页面，静默处理
      this.setData({
        currentReservation: null,
        countdownText: ''
      })
    }
  },

  /**
   * 启动倒计时
   */
  startCountdown() {
    // 清除旧的定时器
    this.clearCountdown()
    
    const updateCountdown = () => {
      const reservation = this.data.currentReservation
      if (!reservation || !reservation.startTime) {
        this.clearCountdown()
        return
      }
      
      // 解析开始时间（支持多种格式）
      const startTime = new Date(reservation.startTime.replace(/-/g, '/'))
      const now = new Date()
      const diff = startTime.getTime() - now.getTime()
      
      if (diff <= 0) {
        // 已经到时间了
        this.setData({
          countdownText: '已到开台时间'
        })
        this.clearCountdown()
        return
      }
      
      // 计算剩余时间
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      const seconds = Math.floor((diff % (1000 * 60)) / 1000)
      
      let countdownText = ''
      if (hours > 0) {
        countdownText = `${hours}小时${minutes}分钟`
      } else if (minutes > 0) {
        countdownText = `${minutes}分钟${seconds}秒`
      } else {
        countdownText = `${seconds}秒`
      }
      
      this.setData({
        countdownText: `距离开台还有 ${countdownText}`
      })
    }
    
    // 立即更新一次
    updateCountdown()
    
    // 每秒更新一次
    const timer = setInterval(updateCountdown, 1000)
    this.setData({
      countdownTimer: timer as any
    })
  },

  /**
   * 清除倒计时
   */
  clearCountdown() {
    if (this.data.countdownTimer) {
      clearInterval(this.data.countdownTimer)
      this.setData({
        countdownTimer: null
      })
    }
  },

  /**
   * 格式化预约时间段显示
   */
  formatReservationTime(startTime: string, endTime: string): string {
    try {
      // 解析时间
      const start = new Date(startTime.replace(/-/g, '/'))
      const end = new Date(endTime.replace(/-/g, '/'))
      
      // 格式化日期和时间
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
        // 同一天
        return `${startDateStr} ${formatTime(start)}-${formatTime(end)}`
      } else {
        // 跨天
        return `${startDateStr} ${formatTime(start)} - ${endDateStr} ${formatTime(end)}`
      }
    } catch (error) {
      console.error('格式化时间失败', error)
      return `${startTime} - ${endTime}`
    }
  },

  /**
   * 加载桌台数据
   */
  async loadTables(storeId: string, date: string) {
    if (!storeId) {
      console.error('门店ID不能为空')
      return
    }

    try {
      wx.showLoading({
        title: '加载中...',
        mask: true,
      })

      // 调用后端API获取预约数据
      const tables = await api.getReservationTables(storeId, date)
      
      // 转换为前端需要的格式（添加选择状态相关字段）
      const tableItems: TableItem[] = tables.map(table => ({
        ...table,
        startIndex: undefined,
        endIndex: undefined,
        selectedSlot: undefined,
      }))
      
      this.setData({
        tables: tableItems,
      })

      wx.hideLoading()
    } catch (error: any) {
      wx.hideLoading()
      console.error('加载桌台数据失败', error)
      wx.showToast({
        title: error?.message || '加载失败，请重试',
        icon: 'none',
        duration: 2000,
      })
    }
  },

  /**
   * 生成模拟数据（用于原型展示）
   */
  generateMockTables(): TableItem[] {
    // 生成24小时的时间段
    const generate24HourSlots = (): TimeSlot[] => {
      const slots: TimeSlot[] = []
      for (let hour = 0; hour < 24; hour++) {
        const startHour = String(hour).padStart(2, '0')
        const endHour = String(hour + 1).padStart(2, '0')
        const status = hour >= 10 && hour < 18 ? 'available' : 'blocked'
        slots.push({
          startTime: `${startHour}:00`,
          endTime: `${endHour}:00`,
          status,
          label: '',
        })
      }
      return slots
    }

    return [
      {
        id: 'table-a01',
        name: '桌台 A01',
        statusText: '可预约',
        image: '', // 暂无图片
        tags: [
          { type: 'disinfection', text: '时间段消毒' },
          { type: 'holiday', text: '节假日优惠' },
        ],
        slots: generate24HourSlots(),
      },
      {
        id: 'table-b12',
        name: '桌台 B12',
        statusText: '可预约',
        image: '',
        tags: [
          { type: 'discount', text: '会员折扣' },
        ],
        slots: generate24HourSlots(),
      },
      {
        id: 'table-c05',
        name: '桌台 C05',
        statusText: '可预约',
        image: '',
        tags: [
          { type: 'maintenance', text: '维护中' },
        ],
        slots: generate24HourSlots(),
      },
    ]
  },

  /**
   * 时间段点击事件 - 支持范围选择
   * 第一次点击：设置起始时间
   * 第二次点击：设置结束时间（必须在起始时间之后），高亮中间范围
   * 后续点击：如果点击的时间段与当前选择连续，则扩展选择范围；否则重置为新的起始时间
   * 点击起止时间段时缩小选择范围，点击中间时间段时重新开始选择
   * 
   * 示例：
   * 选择 10:00-12:00 后：
   * 点击 10:00 → 缩小为 11:00-12:00
   * 点击 12:00 → 缩小为 10:00-11:00
   * 点击 11:00 → 重置为 11:00（重新开始选择）
   * 点击 13:00 → 扩展为 10:00-13:00（如果 12:00-13:00 可预约）
   * 
   */
  onSlotTap(e: any) {
    const { tableId, slotIndex, slotStatus } = e.currentTarget.dataset
    const slotIndexNum = parseInt(slotIndex, 10)
    
    // 如果不可用，不处理点击
    if (slotStatus !== 'available') {
      return
    }

    const tables = this.data.tables as TableItem[]
    
    // 找到对应的桌台
    const tableIndex = tables.findIndex(t => t.id === tableId)
    if (tableIndex === -1) return

    const table = tables[tableIndex]
    const slot = table.slots[slotIndexNum]

    if (!slot || slot.status !== 'available') return

    // 获取当前选择状态
    const currentStartIndex = table.startIndex
    const currentEndIndex = table.endIndex

    let updatedTables = [...tables]
    let newStartIndex: number | undefined
    let newEndIndex: number | undefined
    let newSelectedSlot: TimeSlot | undefined

    // 判断选择状态
    if (currentStartIndex === undefined) {
      // 第一次点击：设置起始时间
      newStartIndex = slotIndexNum
    } else if (currentEndIndex === undefined) {
      // 第二次点击：设置结束时间
      if (slotIndexNum > currentStartIndex) {
        // 点击在起始时间之后，设置为结束时间
        newStartIndex = currentStartIndex
        newEndIndex = slotIndexNum
        
        // 检查中间的时间段是否都可预约
        let canSelect = true
        for (let i = currentStartIndex + 1; i < slotIndexNum; i++) {
          if (table.slots[i].status !== 'available') {
            canSelect = false
            break
          }
        }
        
        if (canSelect) {
          // 构建选择的时间范围
          newSelectedSlot = {
            startTime: table.slots[currentStartIndex].startTime,
            endTime: table.slots[slotIndexNum].endTime,
            status: 'available',
            label: '已选择'
          }
        } else {
          // 中间有不可预约时间段，重置为新的起始时间
          newStartIndex = slotIndexNum
          newEndIndex = undefined
        }
      } else {
        // 点击在起始时间之前或相同，重置为新的起始时间
        newStartIndex = slotIndexNum
        newEndIndex = undefined
      }
    } else {
      // 已有选择范围，尝试扩展、缩小或重置
      if (slotIndexNum === currentStartIndex) {
        // 点击起始位置，缩小选择范围（取消起始时间点）
        if (currentStartIndex === currentEndIndex) {
          // 如果只有一个时间段，点击后清空选择
          newStartIndex = undefined
          newEndIndex = undefined
          newSelectedSlot = undefined
        } else {
          // 缩小范围：新的起始时间向后移动一位
          newStartIndex = currentStartIndex + 1
          newEndIndex = currentEndIndex
          newSelectedSlot = {
            startTime: table.slots[newStartIndex].startTime,
            endTime: table.slots[currentEndIndex].endTime,
            status: 'available',
            label: '已选择'
          }
        }
      } else if (slotIndexNum === currentEndIndex) {
        // 点击结束位置，缩小选择范围（取消结束时间点）
        if (currentStartIndex === currentEndIndex) {
          // 如果只有一个时间段，点击后清空选择
          newStartIndex = undefined
          newEndIndex = undefined
          newSelectedSlot = undefined
        } else {
          // 缩小范围：新的结束时间向前移动一位
          newStartIndex = currentStartIndex
          newEndIndex = currentEndIndex - 1
          newSelectedSlot = {
            startTime: table.slots[currentStartIndex].startTime,
            endTime: table.slots[newEndIndex].endTime,
            status: 'available',
            label: '已选择'
          }
        }
      } else if (slotIndexNum > currentEndIndex) {
        // 点击在结束时间之后，尝试扩展结束时间
        // 检查从结束时间到点击位置之间是否都可预约
        let canExtend = true
        for (let i = currentEndIndex + 1; i < slotIndexNum; i++) {
          if (table.slots[i].status !== 'available') {
            canExtend = false
            break
          }
        }
        
        if (canExtend) {
          // 可以扩展，更新结束时间
          newStartIndex = currentStartIndex
          newEndIndex = slotIndexNum
          newSelectedSlot = {
            startTime: table.slots[currentStartIndex].startTime,
            endTime: table.slots[slotIndexNum].endTime,
            status: 'available',
            label: '已选择'
          }
        } else {
          // 中间有不可预约时间段，重置为新的起始时间
          newStartIndex = slotIndexNum
          newEndIndex = undefined
          newSelectedSlot = undefined
        }
      } else if (slotIndexNum < currentStartIndex) {
        // 点击在起始时间之前，尝试扩展起始时间
        // 检查从点击位置到起始时间之间是否都可预约
        let canExtend = true
        for (let i = slotIndexNum + 1; i < currentStartIndex; i++) {
          if (table.slots[i].status !== 'available') {
            canExtend = false
            break
          }
        }
        
        if (canExtend) {
          // 可以扩展，更新起始时间
          newStartIndex = slotIndexNum
          newEndIndex = currentEndIndex
          newSelectedSlot = {
            startTime: table.slots[slotIndexNum].startTime,
            endTime: table.slots[currentEndIndex].endTime,
            status: 'available',
            label: '已选择'
          }
        } else {
          // 中间有不可预约时间段，重置为新的起始时间
          newStartIndex = slotIndexNum
          newEndIndex = undefined
          newSelectedSlot = undefined
        }
      } else {
        // 点击在已选择范围内（但不是起止位置），重置为新的起始时间
        newStartIndex = slotIndexNum
        newEndIndex = undefined
        newSelectedSlot = undefined
      }
    }

    // 更新表格数据
    updatedTables[tableIndex] = {
      ...table,
      startIndex: newStartIndex,
      endIndex: newEndIndex,
      selectedSlot: newSelectedSlot,
    }

    // 更新选择的表格信息
    const updatedTable = updatedTables[tableIndex]
    
    this.setData({
      tables: updatedTables,
      selectedTableId: tableId,
      selectedTable: updatedTable,
    })
  },

  /**
   * 预约按钮点击事件
   */
  async onReserveTap(e: any) {
    const { tableId } = e.currentTarget.dataset
    const tables = this.data.tables as TableItem[]
    const table = tables.find(t => t.id === tableId)

    if (!table || table.startIndex === undefined || table.endIndex === undefined) {
      wx.showToast({
        title: '请先选择时间段',
        icon: 'none',
      })
      return
    }

    // 验证选择的范围是否有效
    if (table.startIndex >= table.endIndex) {
      wx.showToast({
        title: '请选择有效的时间范围',
        icon: 'none',
      })
      return
    }

    try {
      wx.showLoading({
        title: '预约中...',
        mask: true,
      })

      // 获取选择的开始和结束时间段
      const startSlot = table.slots[table.startIndex]
      const endSlot = table.slots[table.endIndex]

      // 组合成完整的日期时间字符串（格式：yyyy-MM-dd HH:mm:ss）
      const startDateTime = `${this.data.selectedDate} ${startSlot.startTime}:00`
      const endDateTime = `${this.data.selectedDate} ${endSlot.endTime}:00`

      // 调用后端API创建预约
      await api.createReservation({
        tableId: table.id,
        startTime: startDateTime,
        endTime: endDateTime,
      })

      wx.hideLoading()
      wx.showToast({
        title: '预约成功',
        icon: 'success',
        duration: 2000,
      })

      // 刷新数据
      setTimeout(() => {
        this.loadTables(this.data.storeId, this.data.selectedDate)
        // 刷新当前预约记录
        this.checkLoginAndLoadReservation()
      }, 1500)
    } catch (error: any) {
      wx.hideLoading()
      console.error('预约失败', error)
      wx.showToast({
        title: error?.message || '预约失败，请重试',
        icon: 'none',
        duration: 2000,
      })
    }
  },
  
  /**
   * 清除选择
   */
  onClearSelection(e: any) {
    const { tableId } = e.currentTarget.dataset
    const tables = this.data.tables as TableItem[]
    const tableIndex = tables.findIndex(t => t.id === tableId)
    
    if (tableIndex === -1) return
    
    const updatedTables = [...tables]
    updatedTables[tableIndex] = {
      ...tables[tableIndex],
      startIndex: undefined,
      endIndex: undefined,
      selectedSlot: undefined,
    }
    
    // 如果清除的是当前选中的表格，更新selectedTable
    let updatedSelectedTable = this.data.selectedTable
    if (this.data.selectedTableId === tableId) {
      updatedSelectedTable = updatedTables[tableIndex]
    }
    
    this.setData({
      tables: updatedTables,
      selectedTable: updatedSelectedTable,
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    console.log('预约页面 onShow')
    // 更新自定义 tabBar 的选中状态
    this.updateTabBarSelected();
    
    // 检查是否有通过全局变量传递的门店ID（通过 switchTab 跳转时使用）
    if (app.globalData.reservationStoreId) {
      const storeId = app.globalData.reservationStoreId
      app.globalData.reservationStoreId = undefined // 获取后清空
      
      // 如果门店ID发生变化，重新加载数据
      if (this.data.storeId !== storeId) {
        this.setData({ storeId })
        this.loadStoreInfo(storeId)
        this.loadTables(storeId, this.data.selectedDate)
      }
    }
    
    // 如果页面数据为空，重新加载
    if (this.data.tables.length === 0 && this.data.storeId) {
      console.log('页面数据为空，重新加载数据')
      this.loadTables(this.data.storeId, this.data.selectedDate)
    }
    
    // 检查登录状态并刷新当前预约记录
    this.checkLoginAndLoadReservation()
  },

  // 更新 tabBar 选中状态
  updateTabBarSelected() {
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().setData({
        selected: 1 // 预约页面索引为 1
      });
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    // 清除倒计时定时器
    this.clearCountdown()
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.loadTables('', this.data.selectedDate)
    wx.stopPullDownRefresh()
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 处理登录按钮点击
   */
  handleLogin() {
    loginUtil.showLoginModal(this)
  },

  /**
   * 关闭登录弹窗
   */
  closeLoginModal() {
    loginUtil.closeLoginModal(this)
  },

  /**
   * 登录成功回调
   */
  onLoginSuccess() {
    this.setData({
      isLogin: true,
      showLoginModal: false
    })
    // 登录成功后加载当前预约记录
    this.loadCurrentReservation()
  },

  /**
   * 取消当前预约
   */
  async handleCancelReservation(e: any) {
    const { id } = e.currentTarget.dataset
    
    if (!id) {
      wx.showToast({
        title: '预约ID不存在',
        icon: 'none'
      })
      return
    }

    // 确认取消
    const res = await showConfirm('确认取消', '确定要取消当前预约吗？')
    
    if (!res.confirm) {
      return
    }

    try {
      wx.showLoading({
        title: '取消中...',
        mask: true
      })

      await api.cancelReservation(id)

      wx.hideLoading()
      wx.showToast({
        title: '取消成功',
        icon: 'success',
        duration: 2000
      })

      // 清除当前预约记录
      this.setData({
        currentReservation: null,
        countdownText: ''
      })
      this.clearCountdown()

      // 刷新桌台数据
      setTimeout(() => {
        if (this.data.storeId) {
          this.loadTables(this.data.storeId, this.data.selectedDate)
        }
      }, 1500)
    } catch (error: any) {
      wx.hideLoading()
      console.error('取消预约失败', error)
      wx.showToast({
        title: error?.message || '取消失败，请重试',
        icon: 'none',
        duration: 2000
      })
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '台球厅预约',
      path: '/pages/reservation/reservation',
    }
  },
})
