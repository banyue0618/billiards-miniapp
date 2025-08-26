/**
 * 登录工具类 - 提供登录状态检查、登录拦截和登录弹窗功能
 */
import type { IAppOptionExtended } from '../app';

const app = getApp<IAppOptionExtended>();

// 扩展 wx 对象，添加自定义事件处理方法
interface WxWithEvents extends WechatMiniprogram.Wx {
  triggerAppEvent?: (eventName: string, data?: any) => void;
}

const wxWithEvents = wx as WxWithEvents;

/**
 * 检查是否已登录
 * @returns {boolean} 是否已登录
 */
export const isLoggedIn = (): boolean => {
  return app && app.isLoggedIn === true;
};

// 页面实例接口，包含必要的setData方法
interface PageInstance {
  setData: (data: Record<string, any>) => void;
}

/**
 * 显示登录弹窗的页面方法
 * 在需要使用登录弹窗的页面中，引入此方法并在页面的 data 中设置 showLoginModal: false
 * @param {PageInstance} pageInstance 页面实例
 */
export const showLoginModal = (pageInstance: PageInstance): void => {
  if (!pageInstance) return;
  pageInstance.setData({
    showLoginModal: true
  });
};

/**
 * 关闭登录弹窗的页面方法
 * @param {PageInstance} pageInstance 页面实例
 */
export const closeLoginModal = (pageInstance: PageInstance): void => {
  if (!pageInstance) return;
  pageInstance.setData({
    showLoginModal: false
  });
};

/**
 * 登录拦截方法 - 检查登录状态，未登录时显示登录弹窗
 * @param {PageInstance} pageInstance 页面实例
 * @param {Function} callback 登录成功后的回调函数
 * @returns {boolean} 是否已登录
 */
export const loginInterceptor = (pageInstance: PageInstance, callback: () => void): boolean => {
  if (isLoggedIn()) {
    // 已登录，直接执行回调
    if (typeof callback === 'function') {
      callback();
    }
    return true;
  } else {
    // 未登录，显示登录弹窗
    showLoginModal(pageInstance);
    return false;
  }
};
/**
 * 登录成功发送通知
 */
export const loginCheckAndpublishEvent = (): void => {
    if(app && app.isLoggedIn == true){
      app.globalData.loginEventEmitted = true;
      app.globalData.loginEventTimestamp = Date.now();
      // 使用wx扩展的事件触发方法
    if (wxWithEvents.triggerAppEvent) {
      console.log('[登录通知] 触发登录成功事件(分域)');
      wxWithEvents.triggerAppEvent('loginSuccess:ongoingOrderCard', { timestamp: Date.now() });
    }
    }
    
};