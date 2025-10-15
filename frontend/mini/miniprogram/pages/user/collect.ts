// pages/user/collect.ts
import { showError, showSuccess } from '../../utils/util'
import apiService, { StoreDetail, StoreDetailParam }  from '../../services/api'
import type { IAppOptionExtended } from '../../app'

const app = getApp<IAppOptionExtended>()

Page({
  data: {
    collectedStores: [] as StoreDetail[],
    loading: false,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    // this.loadCollectedStores()
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 每次显示页面时重新加载收藏列表，确保数据最新
    this.loadCollectedStores()
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.loadCollectedStores().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  /**
   * 加载收藏的门店列表
   */
  async loadCollectedStores() {
    try {
      this.setData({ loading: true })
      
      // 从本地存储获取收藏的门店ID列表
      const collectedStoreIds = wx.getStorageSync('collectedStores') || []
      console.log('[Collect] 从本地缓存获取的收藏门店ID:', collectedStoreIds)
      
      if (collectedStoreIds.length === 0) {
        console.log('[Collect] 没有收藏的门店，显示空状态')
        this.setData({ 
          collectedStores: [],
          loading: false 
        })
        return
      }

      // 获取所有收藏门店的详细信息
      const storePromises = collectedStoreIds.map(async (storeId: string) => {
        try {
          // 使用门店服务获取门店详情
          const storeDetailParam: StoreDetailParam = {
            storeId: storeId,
            latitude: app.userLocation ? app.userLocation.latitude : 0,
            longitude: app.userLocation ? app.userLocation.longitude : 0
          }
          
          const store = await apiService.getStoreDetail(storeDetailParam)
          console.log(`[Collect] 成功获取门店详情: ${store.name} (${storeId})`)
          return store
        } catch (error) {
          console.error(`[Collect] 获取门店 ${storeId} 详情失败:`, error)
          // 返回一个基本的门店信息，避免完全丢失
          return {
            id: storeId,
            name: `门店${storeId}`,
            coverImage: '/images/placeholder-store.png',
            images: ['/images/placeholder-store.png'],
            status: '未知',
            businessHours: '营业时间未知',
            address: {
              province: '',
              city: '',
              district: '',
              street: '',
              latitude: 0,
              longitude: 0
            },
            tables: {
              total: 0,
              available: 0
            },
            minPrice: '0.00',
            contactPhone: '',
            announcement: {
              content: '',
              updateTime: ''
            }
          } as StoreDetail
        }
      })

      const storeResults = await Promise.all(storePromises)
      
      // 所有门店都应该有数据，不需要过滤
      const validStores = storeResults as StoreDetail[]
      
      console.log(`[Collect] 加载完成，共${validStores.length}个收藏门店`)
      
      this.setData({ 
        collectedStores: validStores,
        loading: false 
      })

    } catch (error) {
      console.error('加载收藏门店失败:', error)
      showError('加载收藏列表失败')
      this.setData({ loading: false })
    }
  },

  /**
   * 处理store-card组件的收藏事件
   */
  onStoreCardCollectTap(e: any) {
    const { storeId, isCollected } = e.detail
    const { collectedStores } = this.data
    
    if (!isCollected) {
      // 取消收藏，从列表中移除
      const updatedStores = collectedStores.filter(store => store.id !== storeId)
      this.setData({ collectedStores: updatedStores })
    }
  },

  /**
   * 移除收藏（保留此方法以兼容可能的其他调用）
   */
  removeCollect(e: any) {
    e.stopPropagation() // 阻止事件冒泡
    
    const storeId = e.currentTarget.dataset.storeId
    const { collectedStores } = this.data
    
    try {
      // 从本地存储中移除
      let collectedStoreIds = wx.getStorageSync('collectedStores') || []
      collectedStoreIds = collectedStoreIds.filter((id: string) => id !== storeId)
      wx.setStorageSync('collectedStores', collectedStoreIds)
      
      // 从页面数据中移除
      const updatedStores = collectedStores.filter(store => store.id !== storeId)
      this.setData({ collectedStores: updatedStores })
      
      showSuccess('已取消收藏')
      
      // 发布事件通知其他页面更新收藏状态
      if (wx.eventBus) {
        wx.eventBus.emit('collectChanged', { storeId, isCollected: false })
      }
      
    } catch (error) {
      console.error('取消收藏失败:', error)
      showError('操作失败，请重试')
    }
  },

  /**
   * 显示清空确认弹窗
   */
  showClearConfirm() {
    wx.showModal({
      title: '确认清空',
      content: '确定要清空所有收藏的门店吗？',
      confirmText: '清空',
      confirmColor: '#ff4757',
      success: (res) => {
        if (res.confirm) {
          this.clearAllCollects()
        }
      }
    })
  },

  /**
   * 清空所有收藏
   */
  clearAllCollects() {
    try {
      // 清空本地存储
      wx.setStorageSync('collectedStores', [])
      
      // 清空页面数据
      this.setData({ collectedStores: [] })
      
      showSuccess('已清空所有收藏')
      
      // 发布事件通知其他页面更新收藏状态
      if (wx.eventBus) {
        wx.eventBus.emit('collectChanged', { storeId: null, isCollected: false, clearAll: true })
      }
      
    } catch (error) {
      console.error('清空收藏失败:', error)
      showError('操作失败，请重试')
    }
  },

  /**
   * 跳转到门店详情页
   */
  navigateToStore(e: any) {
    const store = e.currentTarget.dataset.store
    if (store && store.id) {
      wx.navigateTo({
        url: `/pages/store/detail?id=${store.id}`
      })
    }
  },

  /**
   * 跳转到首页
   */
  navigateToIndex() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: '我的收藏门店',
      path: '/pages/user/collect'
    }
  }
})