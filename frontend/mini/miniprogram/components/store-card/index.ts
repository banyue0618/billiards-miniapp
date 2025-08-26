import { formatDistance, formatPrice } from '../../utils/util'
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
    statusText: ''
  } as StoreCardData,

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
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
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