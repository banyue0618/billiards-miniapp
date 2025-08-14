import apiService, { OrderInfo } from '../../services/api';
import { formatDuration, hideLoading, showConfirm, showLoading, showToast, calculateDurationAndAmount0 } from '../../utils/util';
import type { IAppOptionExtended } from '../../app';
import type { WxWithEvents } from '../../types/events';

// 扩展 OrderInfo 类型以包含新增字段
interface ExtendedOrderInfo extends OrderInfo {
  timeDuration?: string;
  amountText?: string;
}

const wxWithEvents = wx as WxWithEvents;

const app = getApp<IAppOptionExtended>();

Component({
  properties: {
    enableTimer: {
      type: Boolean,
      value: true
    }
  },

  data: {
    currentIndex: 0,
    timerInterval: null as number | null,
    orders: [] as ExtendedOrderInfo[],
    isLoggedIn: false,
    loading: false,
    loginCheckInterval: null as number | null,
    isEnding: false, // 是否正在结束
    loginEventHandled: false, // 标记是否正处理中登录事件
    loginEventRegistered: false, // 标记是否已注册登录事件
    componentId: '', // 组件唯一标识
  },

  lifetimes: {
    created() {
      // 生成唯一组件ID
      const componentId = 'ongoing-order-card';
      this.setData({ componentId });
      console.log('[订单卡片] 组件创建，ID:', componentId);
      // 初始化并发控制标记
      (this as any)._isFetchingOrders = false;
      (this as any)._fetchOrdersPromise = null as Promise<void> | null;
    },
    attached() {
      console.log('[订单卡片] 组件被添加到页面实例上，ID:', this.data.componentId);
      this.watchLoginEvent();
      this.updateLoginState();
      if (this.data.isLoggedIn) {
        this.getOngoingOrders();
      } else {
        this.checkGlobalLoginState();
      }
    },
    detached() {
      console.log('[订单卡片] 组件被从页面实例上移除，ID:', this.data.componentId);
      this._clearTimer();
      this.unregisterEvents();
    }
  },

  pageLifetimes: {
    // 页面显示时，重新获取数据
    show(): void {
      console.log('[订单卡片] 页面被显示');
      // 重新注册事件监听
      this.watchLoginEvent();
      this.updateLoginState();
      if (this.data.isLoggedIn) {
        this.getOngoingOrders();
      } else {
        // 重新检查登录状态
        this.checkGlobalLoginState();
      }
    },
    
    // 页面隐藏时，停止计时器并移除事件监听
    hide(): void {
      console.log('[订单卡片] 页面被隐藏');
      this._clearTimer();
      // 移除事件监听
      this.unregisterEvents();
    }
  },

  methods: {
    // 检查全局登录状态
    checkGlobalLoginState(): void {
      if (app.globalData.loginEventEmitted && app.isLoggedIn) {
        console.log('[订单卡片] 组件初始化时检测到已登录状态');
        this.updateLoginState();
        this.getOngoingOrders();
      }
    },
    
    // 取消事件监听
    unregisterEvents(): void {
      if (wxWithEvents.offAppEvent) {
        console.log('[订单卡片] 准备移除事件监听，组件ID:', this.data.componentId);
        wxWithEvents.offAppEvent('loginSuccess:ongoingOrderCard', this.data.componentId);
        this.setData({ loginEventRegistered: false });
      }
      
      // 重置并发锁，避免隐藏/销毁后锁定
      (this as any)._isFetchingOrders = false;

      this._clearLoginCheckInterval();
    },
    
    // 清除登录检查定时器
    _clearLoginCheckInterval(): void {
      if (this.data.loginCheckInterval) {
        clearInterval(this.data.loginCheckInterval);
        this.setData({ loginCheckInterval: null });
        console.log('[订单卡片] 已清除登录检查定时器');
      }
    },
    
    // 监听登录成功事件（采用事件命名分域，避免影响其他模块）
    watchLoginEvent(): void {
      // 先按组件ID精确移除自身旧监听，避免重复
      if (wxWithEvents.offAppEvent && this.data.componentId) {
        wxWithEvents.offAppEvent('loginSuccess:ongoingOrderCard', this.data.componentId);
      }

      // 使用标记避免重复处理，并节流触发
      let lastTrigger = 0;
      const handleLoginSuccess = async (data?: any) => {
        console.log('[订单卡片] 收到登录成功事件', data, '组件ID:', this.data.componentId);
        const now = Date.now();
        if (now - lastTrigger < 500) {
          console.log('[订单卡片] 事件触发过于频繁，进行节流');
          return;
        }
        lastTrigger = now;
        if (!this.data.loginEventHandled) {
          this.setData({ loginEventHandled: true });
          this.updateLoginState();
          try {
            await this.getOngoingOrders();
          } finally {
            // 处理完毕，开始接收下一次登录成功事件
            this.setData({ loginEventHandled: false });
          }
        } else {
          console.log('[订单卡片] 忽略重复的登录事件');
        }
      };
    
      // 添加小程序全局事件监听（仅使用分域事件名）
      if (wxWithEvents.onAppEvent) {
        console.log(`[订单卡片] 准备添加事件监听, 组件ID: ${this.data.componentId}`);
        wxWithEvents.onAppEvent('loginSuccess:ongoingOrderCard', handleLoginSuccess, this.data.componentId);
        this.setData({ loginEventRegistered: true });
      } else {
        console.warn('[订单卡片] 事件API不可用，使用轮询机制');
      }
      
      // 兼容低版本或开发工具 - 同时使用轮询方式检查登录状态
      // 避免仅依赖事件机制导致的监听失败
      const checkLoginInterval = setInterval(() => {
        if (app.globalData.loginEventEmitted && app.isLoggedIn && !this.data.isLoggedIn) {
          console.log('[订单卡片] 通过轮询检测到登录状态变化');
          handleLoginSuccess({ source: 'polling' });
        }
      }, 1000); // 每1秒检查一次，提高响应速度
      
      // 保存轮询定时器ID以便清除
      this.setData({
        loginCheckInterval: checkLoginInterval as unknown as number
      });
    },

    // 更新登录状态
    updateLoginState(): void {
      // 异或运算 判断本地登录状态与app上的登录状态，不一致本地则更新
      if(this.data.isLoggedIn !== app.isLoggedIn){
        console.log('[订单卡片] 更新登录状态:', app.isLoggedIn);
        this.setData({
          isLoggedIn: app.isLoggedIn
        })
      }
    },
    
    // 获取进行中订单
    async getOngoingOrders(): Promise<void> {
      if (!this.data.isLoggedIn) {
        this.setData({ orders: [] });
        return;
      }
      
      // 防抖/并发控制：避免重复请求
      if ((this as any)._isFetchingOrders) {
        return;
      }
      (this as any)._isFetchingOrders = true;
      this.setData({ loading: true });
      
      try {
        // 获取所有进行中的订单
        console.log("getOngoingOrders...")
        const orders = await apiService.getOngoingOrders();
        
        if (!orders || orders.length === 0) {
          app.isOngoing = false
          // 没有订单时清空并停止计时器
          this._clearTimer();
          this.setData({ 
            orders: [],
            loading: false
          });
          return;
        }

        app.isOngoing = orders.length > 0;
        
        // 处理每个订单的时间和金额
        const processedOrders = orders.map(order => {
          // 将原始订单转换为扩展订单类型
          const extendedOrder = order as ExtendedOrderInfo;
          this.calculateDurationAndAmount(extendedOrder);
          return extendedOrder;
        });
        
        // 更新订单列表
        this.setData({ 
          orders: processedOrders,
          loading: false,
          // 如果当前索引超出范围，重置为0
          currentIndex: this.data.currentIndex >= processedOrders.length ? 0 : this.data.currentIndex
        });
        
        // 启动计时器
        if (this.properties.enableTimer) {
          this._startTimer();
        }
      } catch (error) {
        console.error('[订单卡片] 获取进行中订单失败', error);
        this.setData({ 
          orders: [],
          loading: false
        });
      } finally {
        (this as any)._isFetchingOrders = false;
      }
    },
    
    // 计算订单时长与消费金额
    calculateDurationAndAmount(order: ExtendedOrderInfo): void {
      if (!order) {
        return;
      }
      // 计算时长与消费金额，根据startTime
      const {timeDuration, amountText} = calculateDurationAndAmount0(order.startTime, order.priceUnit || 0, order.memberDiscount || 1.0);
      order.timeDuration = timeDuration;
      order.amountText = amountText;
    },
    
    // 开始定时更新
    _startTimer(): void {
      this._clearTimer();
      this.data.timerInterval = setInterval(() => {
        if (this.data.orders.length === 0) {
          this._clearTimer();
          return;
        }
        
        // 更新所有订单的时间和金额
        const updatedOrders = this.data.orders.map(order => {
          this.calculateDurationAndAmount(order);
          return { ...order }; // 创建新对象以触发视图更新
        });
        
        this.setData({
          orders: updatedOrders
        });
      }, 60000); // 每分钟更新一次
    },
    
    // 清除定时器
    _clearTimer(): void {
      if (this.data.timerInterval) {
        clearInterval(this.data.timerInterval);
        this.data.timerInterval = null;
      }
    },
    
    // 滑动切换
    handleSwiperChange(e: any): void {
      this.setData({
        currentIndex: e.detail.current
      });
    },
    
    // 点击结束计费
    async handleEndSession(e: any): Promise<void> {
      // 阻止事件冒泡，避免触发父级的 handleViewOrder
      if (e && typeof e.stopPropagation === 'function') {
        e.stopPropagation();
      }
      if(this.data.isEnding){
        showToast('请稍后再试')
        return;
      }
      const orderId = e.currentTarget.dataset.orderId as string;

      // 确认提示 提示框增加显示当前的消费金额
      const order = this.data.orders.find(order => order.id === orderId);
      const res = await showConfirm('确认结束使用', `当前预估扣除费用：¥ ${order?.amountText},确认结束后余额将原路返回`)
      if(res.cancel){
        console.log('用户取消结束使用')
        return;
      }
      // 如果用户确认结束使用，则进行结算
      console.log('用户确认结束使用')
      try {
        this.setData({ isEnding: true })
        showLoading('结算中...')
        
        // 结束订单
        const result = await apiService.endOrder(orderId)
        
        hideLoading()

        // 简单显示结算信息，提示用户余额将原路返回
        showToast(`结算成功，消费金额：${result.actualAmount}，时长：${formatDuration(result.duration || 0)}\n\n余额将原路返回。`)
        
        // 刷新订单列表
        this.getOngoingOrders() 
      } catch (error) {
        hideLoading()
        this.setData({ isEnding: false })
        console.error('结束订单失败', error)
        showToast(error instanceof Error ? error.message : '结算失败')
      }
      
    },
    
    // 点击查看订单详情
    handleViewOrder(e: any): void {
      // 获取当前点击的订单索引
      const orderId = e.currentTarget.dataset.orderId as string;
      console.log("handleViewOrder:" + orderId);
      // 直接跳转到订单详情页面
      wx.navigateTo({ 
        url: `/pages/timer/index?orderId=${orderId}`
      });
    }
  }
}) 