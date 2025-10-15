import { formatDistance, showSuccess, showError } from '../../utils/util'
import { StoreDetail } from '../../services/api'

/**
 * 根据状态返回对应的CSS类名
 * @param status 状态值
 * @returns CSS类名
 */
function formatStatusClass(status: string): string {
  // 根据status的值返回对应的CSS类名
  switch (status) {
    case '0':
      return 'status-open';
    case '1':
      return 'status-closed';
    case '2':
      return 'status-full';
    default:
      return 'status-closed';
  }
}

interface StoreCardData {
  store?: StoreDetail;
  distanceText: string;
  priceText: number;
  statusClass: string;
  statusText: string;
  isCollected: boolean;
  componentId?: string;
}

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    store: {
      type: Object,
      value: {}
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    distanceText: '',
    priceText: 0,
    statusClass: '',
    statusText: '',
    isCollected: false,
    componentId: ''
  } as StoreCardData,

  /**
   * 组件生命周期
   */
  lifetimes: {
    attached() {
      // 组件初始化时检查收藏状态
      this.checkCollectStatus()
      
      // 生成组件ID用于事件监听
      const componentId = `store-card-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
      this.setData({ componentId })
      
      // 监听收藏状态变化事件
      if (wx.eventBus) {
        wx.eventBus.on('collectChanged', this.onCollectChanged.bind(this), componentId)
      }
    },
    
    detached() {
      // 组件销毁时移除事件监听
      if (wx.eventBus && this.data.componentId) {
        wx.eventBus.off('collectChanged', this.data.componentId)
      }
    }
  },

  /**
   * 数据监听器
   */
  observers: {
    'store': function(store: StoreDetail) {
      if (!store) return

      // 计算距离文字
      if (store.distance) {
        this.setData({
          distanceText: formatDistance(store.distance)
        })
      }

      // 格式化价格
      if (store.minPrice) {
        this.setData({
          priceText: parseFloat(store.minPrice),
        })
      }

      // 设置状态样式和文本
      if (store.status) {
        // 根据状态返回对应的CSS类名
        const statusClass = formatStatusClass(store.status);
        
        // 设置状态文本
        let statusText = '';
        switch (store.status) {
          case '0':
            statusText = '营业中';
            break;
          case '1':
            statusText = '休息中';
            break;
          case '2':
            statusText = '已满';
            break;
          default:
            statusText = store.status; // 如果已经是文本则直接使用
        }
        
        this.setData({
          statusClass,
          statusText
        })
      }

      // 检查收藏状态
      this.checkCollectStatus()
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    /**
     * 检查收藏状态
     */
    checkCollectStatus() {
      const store = (this.data as StoreCardData).store;
      if (!store || !store.id) return;

      try {
        const collectedStores = wx.getStorageSync('collectedStores') || [];
        const isCollected = collectedStores.includes(store.id);
        this.setData({ isCollected });
      } catch (error) {
        console.error('检查收藏状态失败:', error);
      }
    },

    /**
     * 处理收藏状态变化事件
     */
    onCollectChanged(event: any) {
      const store = (this.data as StoreCardData).store;
      if (!store || !store.id) return;

      // 事件总线直接传递数据，不需要event.detail
      const { storeId, isCollected, clearAll } = event;
      
      if (clearAll) {
        // 清空所有收藏
        this.setData({ isCollected: false });
      } else if (storeId === store.id) {
        // 当前门店的收藏状态变化
        this.setData({ isCollected });
      }
    },

    /**
     * 点击收藏按钮
     */
    onCollectTap() {
      // catchtap 会自动阻止事件冒泡，不需要手动调用 stopPropagation
      
      const store = (this.data as StoreCardData).store;
      if (!store || !store.id) return;

      try {
        const { isCollected } = this.data;
        let collectedStores = wx.getStorageSync('collectedStores') || [];
        console.log(`[StoreCard] 收藏操作前: ${store.name} (${store.id}) - 当前状态: ${isCollected ? '已收藏' : '未收藏'}`);
        console.log(`[StoreCard] 当前收藏列表:`, collectedStores);

        if (isCollected) {
          // 取消收藏
          collectedStores = collectedStores.filter((storeId: string) => storeId !== store.id);
          showSuccess('已取消收藏');
        } else {
          // 添加收藏
          if (!collectedStores.includes(store.id)) {
            collectedStores.push(store.id);
          }
          showSuccess('收藏成功');
        }

        console.log(`[StoreCard] 收藏操作后:`, collectedStores);
        // 更新本地存储
        wx.setStorageSync('collectedStores', collectedStores);

        // 更新状态
        this.setData({ isCollected: !isCollected });

        // 发布事件通知其他页面更新收藏状态
        if (wx.eventBus) {
          wx.eventBus.emit('collectChanged', {
            storeId: store.id,
            isCollected: !isCollected
          });
        }

        // 触发自定义事件，供父组件监听
        this.triggerEvent('collectTap', {
          storeId: store.id,
          isCollected: !isCollected
        });
      } catch (error) {
        console.error('收藏操作失败:', error);
        showError('操作失败，请重试');
      }
    },

    /**
     * 点击整个卡片
     */
    onCardTap() {
      const store = (this.data as StoreCardData).store;
      if (!store) return;

      wx.navigateTo({
        url: `/pages/store/detail?id=${store.id}`
      })
    },

    /**
     * 点击扫码按钮
     */
    onScanTap(e: any) {
      // 阻止冒泡，避免触发卡片点击
      e.stopPropagation();

      wx.navigateTo({
        url: '/pages/scan/index'
      })
    },

    /**
     * 点击导航按钮
     */
    onNavigateTap(e: any) {
      // 阻止冒泡，避免触发卡片点击
      e.stopPropagation();

      const store = (this.data as StoreCardData).store;
      if (!store) return;

      wx.openLocation({
        latitude: store.address.latitude,
        longitude: store.address.longitude,
        name: store.name,
        address: `${store.address.province}${store.address.city}${store.address.district}${store.address.street}`,
        scale: 18
      })
    },

    /**
     * 点击电话按钮
     */
    onPhoneTap(e: any) {
      // 阻止冒泡，避免触发卡片点击
      e.stopPropagation();

      const store = (this.data as StoreCardData).store;
      if (!store || !store.contactPhone) return;

      wx.makePhoneCall({
        phoneNumber: store.contactPhone
      })
    }
  }
}) 