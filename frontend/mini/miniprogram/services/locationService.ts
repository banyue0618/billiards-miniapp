import type { IAppOptionExtended } from '../app'

export class LocationService {
  private app: IAppOptionExtended | null;

  constructor() {
    this.app = null;
  }

  /**
   * 初始化位置服务
   * @param app 全局app实例
   */
  init(app: IAppOptionExtended): void {
    if (!app) {
      console.error('LocationService.init: 传入的app实例为空');
      return;
    }
    
    console.log('LocationService 初始化成功');
    this.app = app;
  }

  /**
   * 显示授权设置引导弹窗
   * @param message 提示信息
   * @returns Promise<boolean> 用户是否点击了确认按钮
   */
  async showAuthSettingModal(message: string = '为了查找附近的台球厅，需要获取您的位置信息'): Promise<boolean> {
    return new Promise((resolve) => {
      wx.showModal({
        title: '需要位置信息',
        content: message,
        confirmText: '去设置',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            wx.openSetting({
              success: (settingRes) => {
                const authorized = settingRes.authSetting['scope.userLocation'] === true;
                resolve(authorized);
              },
              fail: () => resolve(false)
            });
          } else {
            resolve(false);
          }
        },
        fail: () => resolve(false)
      });
    });
  }

  /**
   * 检查位置权限状态并处理
   * @returns Promise<boolean> 是否获得了授权
   */
  async checkLocationAuth(): Promise<boolean> {
    try {
      // 检查位置权限当前状态
      const setting = await wx.getSetting();
      const hasLocationAuth = setting.authSetting['scope.userLocation'];
      console.log('位置授权状态检查:', hasLocationAuth ? '已授权' : '未授权');
      
      if (hasLocationAuth === true) {
        return true;
      } else if (hasLocationAuth === false) {
        // 明确拒绝过授权，提示用户打开设置
        console.log('用户曾拒绝位置授权，提示用户打开设置');
        return await this.showAuthSettingModal();
      } else {
        // 首次使用，未决定是否授权
        console.log('请求位置授权');
        return true; // 让系统显示授权弹窗
      }
    } catch (e) {
      console.error('检查授权状态出错:', e);
      return false;
    }
  }

  /**
   * 直接获取位置信息
   * @returns Promise<WechatMiniprogram.GetLocationSuccessCallbackResult | null>
   */
  private async getLocationDirectly(): Promise<WechatMiniprogram.GetLocationSuccessCallbackResult | null> {
    try {
      console.log('准备调用 wx.getLocation 获取位置');
      const locationRes = await wx.getLocation({ 
        type: 'gcj02',
        isHighAccuracy: true, // 开启高精度定位
        highAccuracyExpireTime: 3000 // 高精度定位超时时间，单位ms
      });
      console.log('获取位置成功:', locationRes);
      
      if (this.app) {
        this.app.userLocation = locationRes;
      } 
      return locationRes;
    } catch (error: any) {
      console.error('获取位置失败:', error);
      return null;
    }
  }

  /**
   * 请求并获取用户位置
   * @returns Promise<WechatMiniprogram.GetLocationSuccessCallbackResult | null>
   */
  async requestLocation(): Promise<WechatMiniprogram.GetLocationSuccessCallbackResult | null> {
    try {
      // 先检查授权状态
      const isAuthorized = await this.checkLocationAuth();
      
      if (isAuthorized) {
        // 获取位置
        const location = await this.getLocationDirectly();
        if (location) return location;
      }
      
      // 获取位置失败或未授权
      const errorMsg = '小程序搜索需要您的位置信息帮您查找附近的球厅，请允许小程序使用您的位置';
      const userConfirmed = await this.showAuthSettingModal(errorMsg);
      
      if (userConfirmed) {
        // 用户在设置页授权后，重新获取位置
        return await this.getLocationDirectly();
      } else {
        // 用户取消或设置失败
        wx.showToast({
          title: '未获得位置授权',
          icon: 'none'
        });
        if (this.app) {
          this.app.userLocation = null;
        }
        return null;
      }
    } catch (e) {
      console.error('位置服务异常', e);
      wx.showToast({
        title: '位置服务异常',
        icon: 'none'
      });
      if (this.app) {
        this.app.userLocation = null;
      }
      return null;
    }
  }
} 