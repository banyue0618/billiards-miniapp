// app.ts
import type { UserInfo as CustomUserInfo } from './services/api' // 重命名导入的类型以示区分
import { AuthService } from './services/authService'
import { LocationService } from './services/locationService'
import { StoreService } from './services/storeService'
import type { WxWithEvents, EventListener } from './types/events'

// 事件监听器映射
const eventListeners: Record<string, Map<string, EventListener>> = {};

// 为wx对象扩展事件方法
const wxWithEvents = wx as WxWithEvents;

// 触发事件
wxWithEvents.triggerAppEvent = (eventName: string, data?: any): void => {
  console.log(`[事件总线] 触发事件: ${eventName}`, data);
  const listeners = eventListeners[eventName];
  if (listeners && listeners.size > 0) {
    console.log(`[事件总线] 当前事件监听器数量: ${listeners.size}`);
    listeners.forEach((listener) => {
      try {
        // 打印监听器的明细
        console.log(`[事件总线] 监听器明细: ${listener.componentId}`);
        listener.callback(data);
      } catch (error) {
        console.error(`[事件总线] 事件处理出错: ${eventName}`, error);
      }
    });
  }
};

// 添加事件监听
wxWithEvents.onAppEvent = (eventName: string, callback: Function, componentId?: string): void => {
  console.log(`[事件总线] 添加事件监听: ${eventName}, 组件ID: ${componentId}`);
  if (!eventListeners[eventName]) {
    eventListeners[eventName] = new Map();
  }
  
  // 如果提供了组件ID，先移除该组件的旧监听器
  if (componentId) {
    eventListeners[eventName].delete(componentId);
  } else {
    // 如果没有提供组件ID，生成一个
    componentId = `anonymous-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }
  
  // 添加新的监听器
  eventListeners[eventName].set(componentId, { callback, componentId });
  console.log(`[事件总线] 当前事件监听器数量: ${eventListeners[eventName].size}`);
};

// 移除事件监听
wxWithEvents.offAppEvent = (eventName: string, componentId?: string): void => {
  console.log(`[事件总线] 移除事件监听: ${eventName}, 组件ID: ${componentId}`);
  const listeners = eventListeners[eventName];
  if (listeners) {
    if (componentId) {
      // 打印当前所有监听器的ID
      console.log('[事件总线] 当前所有监听器ID:', Array.from(listeners.keys()));
      // 如果提供了组件ID，只移除该组件的监听器
      const removed = listeners.delete(componentId);
      console.log(`[事件总线] 移除监听器${removed ? '成功' : '失败'}, 组件ID: ${componentId}, 当前监听器数量: ${listeners.size}`);
      // 如果监听器为空，清理事件项，避免内存泄漏
      if (listeners.size === 0) {
        delete eventListeners[eventName];
      }
    } else {
      // 如果没有提供组件ID，清空该事件的所有监听器
      listeners.clear();
      console.log(`[事件总线] 清空所有监听器`);
      delete eventListeners[eventName];
    }
    console.log(`[事件总线] 移除后事件监听器数量: ${listeners.size}`);
  }
};

// 扩展 IAppOption 类型 (或者在 app.d.ts 中定义)
export interface IAppOptionExtended extends IAppOption {
  // 将自定义的状态直接放在 App 实例上
  clientId: string | '428a8310cd442757ae699df5d894f051';
  grantType: string | 'xcx';
  tenantId: string | '000000';
  appid: string; // 新增 appid 属性
  token: string | null;
  customUserInfo: CustomUserInfo | null;
  isLoggedIn: boolean;
  isOngoing: boolean;
  userLocation: WechatMiniprogram.GetLocationSuccessCallbackResult | null; // 修改类型
  // 是否启用游客登录按钮（使用 wx.login code 实现）
  enableGuestLogin: boolean;
  
  // 服务实例
  authService: AuthService;  // 认证服务：处理登录、自动登录等认证相关功能
  locationService: LocationService;  // 位置服务：处理地理位置获取和授权
  storeService: StoreService;  // 门店服务：提供门店数据获取功能
  
  // 额外状态
  isInitialized: boolean; // 标记 App 是否已初始化完成
  
  // globalData 结构应符合 IAppOption 的定义
  // 如果 IAppOption['globalData'] 是可选的或允许为空对象，则可以这样初始化
  globalData: {
    userInfo?: WechatMiniprogram.UserInfo
    // 登录事件相关属性
    loginEventEmitted?: boolean;
    loginEventTimestamp?: number;
  };
  // 允许 IAppOption 中可能存在的其他属性和方法
  [key: string]: any;
}

App<IAppOptionExtended>({
  // App 实例的顶层属性初始化
  clientId: '428a8310cd442757ae699df5d894f051',
  grantType: 'xcx',
  tenantId: '000000',
  appid: wx.getAccountInfoSync().miniProgram.appId, // 初始化 appid
  token: null, 
  customUserInfo: null, 
  isLoggedIn: false,
  isOngoing: false,
  userLocation: null,
  isInitialized: false, // 初始状态为未初始化
  // 默认关闭游客登录按钮，需要时改为 true
  enableGuestLogin: true,

  // globalData 结构应符合 IAppOption 的定义
  // 如果 IAppOption['globalData'] 是可选的或允许为空对象，则可以这样初始化
  globalData: {
    userInfo: undefined,
    loginEventEmitted: false,
    loginEventTimestamp: 0
  },

  // 服务实例初始化
  authService: new AuthService(),
  locationService: new LocationService(),
  storeService: new StoreService(), // 门店服务：在首页用于获取附近门店列表

  /**
   * 初始化各服务
   * 将初始化逻辑抽离为单独方法，方便在需要时重新初始化
   */
  initServices() {
    console.log('App - 初始化各服务');
    try {
      // 初始化服务
      this.authService.init(this);
      this.locationService.init(this);
      this.storeService.init(this);
      this.isInitialized = true;
      console.log('App - 服务初始化完成');
    } catch (error) {
      console.error('App - 服务初始化失败', error);
      this.isInitialized = false;
    }
  },

  async onLaunch() {
    console.log('App onLaunch - 小程序启动');
    
    // 获取小程序账号信息
    const accountInfo = wx.getAccountInfoSync();
    if (accountInfo && accountInfo.miniProgram) {
      this.appid = accountInfo.miniProgram.appId;
    }
    
    // 初始化各服务
    this.initServices();
    
    // 其他启动时需要执行的异步任务可以在这里添加
    // ...
  },
  
  /**
   * 小程序显示时触发
   * 可能是首次启动显示，也可能是从后台切换到前台
   */
  onShow(options) {
    console.log('App onShow - 小程序显示', options);
    
    // 如果未初始化完成，确保初始化
    if (!this.isInitialized) {
      console.log('App onShow - 检测到服务未初始化，重新初始化');
      this.initServices();
    }
    
    // 根据需要在这里添加其他处理逻辑
    // 比如刷新位置信息、检查登录状态等
  },
  
  /**
   * 小程序隐藏时触发
   * 可能是被用户关闭，也可能是切换到后台
   */
  onHide() {
    console.log('App onHide - 小程序隐藏');
    // 在这里添加小程序进入后台时需要执行的逻辑
  }
})