// 门店详情页
import { formatDistance, formatPrice, showError, showLoading, hideLoading, showSuccess, formatDuration } from '../../utils/util'
import apiService, { StoreDetail, StoreDetailParam } from '../../services/api'
import type { IAppOptionExtended } from '../../app'

const app = getApp<IAppOptionExtended>();

// 阶梯计费规则接口
interface LadderRule {
  startMinute: number;
  endMinute: number;
  price: number;
  memberPrice: number;
}

// 格式化后的价格项接口
interface FormattedPriceItem {
  type: string;
  price: string;
  memberPrice: string;
}

Page({
  data: {
    id: '', // 门店ID
    store: null as StoreDetail | null, // 门店信息
    distanceText: '', // 距离文本
    standardPriceText: '0.00', // 标准价格文本
    memberPriceText: '0.00', // 会员价格文本
    isCollected: false, // 是否已收藏
    refreshTimer: null as number | null, // 刷新定时器
    showPricingDetails: false, // 是否显示计费规则详情
    // 价格规则相关
    priceRule: {
      type: '', // 计费类型：standard-标准计费，ladder-阶梯计费
      priceUnit: 0, // 价格单位，分/分钟
      maxPrice: 0, // 封顶价格，分
      minConsumption: 0, // 最低消费，分钟
      ladderRules: [] as LadderRule[] // 阶梯计费规则
    },
    // 格式化后的价格文本
    minConsumptionText: '0分钟', // 最低消费时间
    maxPriceText: '0.00', // 封顶价格
    ladderRulesFormatted: [] as Array<{
      timeRange: string,
      standardPrice: string,
      memberPrice: string
    }>,
    // 价格列表
    formattedPriceList: [] as FormattedPriceItem[]
  },

  onLoad(options) {
    // 获取门店ID
    const id = options.id
    if (!id) {
      showError('缺少门店ID')
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
      return
    }

    this.setData({ id })

    // 加载门店详情
    this.loadStoreDetail(id)

    // 检查是否已收藏
    this.checkCollectionStatus(id)
  },

  onShow() {
    // 启动定时刷新（每30秒刷新一次桌台状态）
    // this.startRefreshTimer()
  },

  onHide() {
    // 停止定时刷新
    // this.stopRefreshTimer()
  },

  onUnload() {
    // 停止定时刷新
    // this.stopRefreshTimer()
  },

  onPullDownRefresh() {
    // 下拉刷新
    this.loadStoreDetail(this.data.id, true)
  },

  // 加载门店详情
  async loadStoreDetail(storeId: string, showRefresh = false) {
    try {
      if (!showRefresh) {
        showLoading('加载中...')
      }
      // StoreDetailParam
      const storeDetailParam: StoreDetailParam = {
        storeId: storeId,
        latitude: app.userLocation ? app.userLocation.latitude : 0,
        longitude: app.userLocation ? app.userLocation.longitude : 0
      }
      
      const store = await apiService.getStoreDetail(storeDetailParam)
      
      // 格式化距离
      let distanceText = ''
      if (store.distance) {
        distanceText = formatDistance(store.distance)
      }
      
      this.setData({ 
        store,
        distanceText
      })
      
      // 处理价格规则
      this.processPriceRule(store)
      
      if (showRefresh) {
        wx.stopPullDownRefresh()
      }
    } catch (error) {
      console.error('加载门店详情失败', error)
      showError('加载门店详情失败')
      
      if (showRefresh) {
        wx.stopPullDownRefresh()
      }
    } finally {
      if (!showRefresh) {
        hideLoading()
      }
    }
  },

  // 处理价格规则
  processPriceRule(store: StoreDetail) {
    if (!store) return;
    
    try {
      const memberDiscount = 0.8; // 会员折扣，实际应从API获取
      
      // 1. 处理priceList数据
      if (store.priceList && store.priceList.length > 0) {
        const formattedPriceList = store.priceList.map(item => {
          // 计算会员价格（原价乘以折扣）
          const originalPrice = parseFloat(item.price);
          const memberPrice = (originalPrice * (item.memberDiscount || 1.0)).toFixed(2);
          
          return {
            type: item.type,
            price: item.price,
            memberPrice
          };
        });
        
        this.setData({ formattedPriceList });
        
        // 设置默认显示价格（第一项或价格最低的项）
        if (formattedPriceList.length > 0) {
          const firstItem = formattedPriceList[0];
          this.setData({
            standardPriceText: firstItem.price,
            memberPriceText: firstItem.memberPrice
          });
        }
      } 
      // 2. 处理旧版价格规则（如果有）
      else if (store.priceRule) {
        const priceRule = store.priceRule;
        
        // 处理最低消费
        const minConsumptionText = formatDuration(priceRule.minConsumption || 0);
        
        // 处理封顶价格
        const maxPriceText = formatPrice(priceRule.maxPrice || 0);
        
        // 标准计费价格
        const standardPriceText = formatPrice((priceRule.priceUnit || 0) * 60);
        const memberPriceText = formatPrice((priceRule.priceUnit || 0) * 60 * memberDiscount);
        
        // 处理阶梯计费规则
        let ladderRules: LadderRule[] = [];
        if (priceRule.type === 'ladder' && priceRule.ladderRules) {
          try {
            // 如果是字符串，解析为对象
            if (typeof priceRule.ladderRules === 'string') {
              ladderRules = JSON.parse(priceRule.ladderRules);
            } else {
              ladderRules = priceRule.ladderRules as LadderRule[];
            }
          } catch (e) {
            console.error('解析阶梯计费规则失败', e);
            ladderRules = [];
          }
        }
        
        // 格式化阶梯计费规则用于显示
        const ladderRulesFormatted = ladderRules.map(rule => {
          // 时间范围文本
          let timeRange = '';
          if (rule.endMinute === -1) {
            timeRange = `${formatDuration(rule.startMinute)}以上`;
          } else {
            timeRange = `${formatDuration(rule.startMinute)}-${formatDuration(rule.endMinute)}`;
          }
          
          // 价格文本
          const standardPrice = formatPrice(rule.price * 60); // 转为小时单位
          
          // 会员价格文本（如果没有指定，使用标准价格乘以折扣）
          const memberPrice = rule.memberPrice 
            ? formatPrice(rule.memberPrice * 60)
            : formatPrice(rule.price * 60 * memberDiscount);
            
          return { timeRange, standardPrice, memberPrice };
        });
        
        // 更新数据
        this.setData({
          priceRule: {
            type: priceRule.type || 'standard',
            priceUnit: priceRule.priceUnit || 0,
            maxPrice: priceRule.maxPrice || 0,
            minConsumption: priceRule.minConsumption || 0,
            ladderRules
          },
          minConsumptionText,
          maxPriceText,
          standardPriceText,
          memberPriceText,
          ladderRulesFormatted
        });
      }
      // 3. 使用minPrice作为备选方案
      else if (store.minPrice) {
        const priceInYuan = Number(store.minPrice) / 100; // 转换为元
        const standardPriceText = priceInYuan.toFixed(2);
        const memberPriceText = (priceInYuan * memberDiscount).toFixed(2);
        
        this.setData({
          standardPriceText,
          memberPriceText
        });
      }
      
    } catch (error) {
      console.error('处理价格规则失败', error);
    }
  },

  // 检查收藏状态
  checkCollectionStatus(storeId: string) {
    try {
      // 从本地存储获取收藏列表
      const collectedStores = wx.getStorageSync('collectedStores') || []
      
      // 检查当前门店是否在收藏列表中
      const isCollected = collectedStores.includes(storeId)
      
      this.setData({ isCollected })
    } catch (error) {
      console.error('检查收藏状态失败', error)
    }
  },

  // 启动定时刷新
  startRefreshTimer() {
    // 先清除可能存在的定时器
    this.stopRefreshTimer()
    
    // 创建新定时器，每30秒刷新一次
    const refreshTimer = setInterval(() => {
      if (this.data.id) {
        this.loadStoreDetail(this.data.id, true)
      }
    }, 30000) as unknown as number
    
    this.setData({ refreshTimer })
  },

  // 停止定时刷新
  stopRefreshTimer() {
    if (this.data.refreshTimer) {
      clearInterval(this.data.refreshTimer)
      this.setData({ refreshTimer: null })
    }
  },

  // 预览图片
  previewImage(e: any) {
    const { index } = e.currentTarget.dataset
    const { store } = this.data
    
    if (!store || !store.images || store.images.length === 0) return
    
    wx.previewImage({
      current: store.images[index],
      urls: store.images
    })
  },

  // 扫码开台
  handleScan() {
    // 目标页面是 tabBar 页面，必须使用 switchTab
    wx.switchTab({
      url: '/pages/scan/index'
    })
  },

  // 联系客服
  handleContact() {
    const { store } = this.data
    
    if (!store || !store.contactPhone) {
      showError('暂无联系电话')
      return
    }
    
    wx.makePhoneCall({
      phoneNumber: store.contactPhone
    })
  },

  // 导航
  handleNavigate() {
    const { store } = this.data
    
    if (!store || !store.address) {
      showError('暂无位置信息')
      return
    }
    
    wx.openLocation({
      latitude: store.address.latitude,
      longitude: store.address.longitude,
      name: store.name,
      address: `${store.address.province}${store.address.city}${store.address.district}${store.address.street}`,
      scale: 18
    })
  },

  // 收藏/取消收藏
  handleCollect() {
    const { id, isCollected } = this.data
    
    try {
      // 从本地存储获取收藏列表
      let collectedStores = wx.getStorageSync('collectedStores') || []
      
      if (isCollected) {
        // 取消收藏
        collectedStores = collectedStores.filter((storeId: string) => storeId !== id)
        showSuccess('已取消收藏')
      } else {
        // 添加收藏
        if (!collectedStores.includes(id)) {
          collectedStores.push(id)
        }
        showSuccess('收藏成功')
      }
      
      // 更新本地存储
      wx.setStorageSync('collectedStores', collectedStores)
      
      // 更新状态
      this.setData({ isCollected: !isCollected })
    } catch (error) {
      console.error('收藏操作失败', error)
      showError('操作失败，请重试')
    }
  },

  // 购买会员
  handleBuyMember() {
    // 跳转到会员购买页面
    wx.navigateTo({
      url: '/pages/user/member/index'
    })
  },
  
  // 切换计费规则显示 - 该功能已不再需要，计费规则始终显示
  togglePricingDetails() {
    // 保留此方法以兼容已有代码，但不再执行任何操作
  },
  
  // 分享
  onShareAppMessage() {
    const { store } = this.data
    
    if (!store) {
      return {
        title: '自助台球厅',
        path: '/pages/index/index'
      }
    }
    
    return {
      title: `${store.name} - 自助台球厅`,
      path: `/pages/store/detail?id=${this.data.id}`,
      imageUrl: store.coverImage
    }
  }
}) 