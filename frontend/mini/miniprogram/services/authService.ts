import apiService from './api'
import type { UserInfo as CustomUserInfo } from './api'
import type { IAppOptionExtended } from '../app'

export class AuthService {
  private app: IAppOptionExtended | null;

  constructor() {
    this.app = null;
  }

  init(app: IAppOptionExtended): void {
    this.app = app;
  }

  /**
   * 检查并尝试自动登录
   */
  async checkAndAutoLogin(): Promise<boolean> {
    const storedToken = wx.getStorageSync('token');
    if (!storedToken || !this.app) return false;

    try {
      this.app.token = storedToken;
      const userInfo = await apiService.getUserInfo();
      
      if (userInfo && userInfo.id) {
        this.app.customUserInfo = userInfo;
        this.app.isLoggedIn = true;
        // 同步获取进行中订单并更新全局 isOngoing
        try {
          const ongoing = await apiService.getOngoingOrders();
          this.app.isOngoing = Array.isArray(ongoing) && ongoing.length > 0;
        } catch (ignored) {
          this.app.isOngoing = false;
        }
        return true;
      } else {
        this.clearLoginState();
      }
    } catch (e) {
      this.clearLoginState();
    }
    
    return false;
  }

  /**
   * 清除登录状态
   */
  clearLoginState(): void {
    if (!this.app) return;
    console.log("调用了clearLoginState...");
    wx.removeStorageSync('token');
    this.app.token = null;
    this.app.customUserInfo = null;
    this.app.isLoggedIn = false;
    this.app.isOngoing = false;
  }

  /**
   * 登录流程 - 仅处理手机号登录，不再获取用户资料
   */
  async login(e?: WechatMiniprogram.ButtonGetPhoneNumber): Promise<boolean> {
    if (!this.app) return false;
    // 验证手机号获取事件
    if(e && e.detail && e.detail.errMsg && e.detail.errMsg.includes('fail')){
      wx.showToast({ 
        title: e.detail.errMsg === 'getPhoneNumber:fail user deny' ? '您拒绝了授权' : e.detail.errMsg, 
        icon: 'none' 
      });
      return false;
    }
    if(e && e.detail && e.detail.code){
      const phoneCode = e.detail.code;
      
      wx.showLoading({ title: '登录中...' });

      try {
        // 获取登录凭证
        const loginRes = await wx.login();
        if (!loginRes.code) {
          wx.showToast({ title: '微信登录失败', icon: 'none' });
          wx.hideLoading();
          return false;
        }

        // 构建登录请求参数
        const loginPayload = {
          phoneCode,
          clientId: this.app.clientId,
          grantType: this.app.grantType,
          tenantId: this.app.tenantId,
          xcxCode: loginRes.code,
          appid: this.app.appid
        };

        // 调用登录接口
        const loginResult = await apiService.login(loginPayload);
        
        if (loginResult && loginResult.token) {
          // 保存登录状态
          wx.setStorageSync('token', loginResult.token);
          this.app.token = loginResult.token;
          
          // 更新用户信息
          this.app.customUserInfo = {
            id: loginResult.userId,
            nickname: loginResult.nickname,
            avatarUrl: loginResult.avatarUrl,
            isMember: loginResult.isMember,
            memberLevel: loginResult.memberLevel,
            points: loginResult.points,
            member_expire_time: loginResult.memberExpireTime
          } as CustomUserInfo;
          
          this.app.isLoggedIn = true;
          
          wx.hideLoading();
          wx.showToast({ title: '登录成功', icon: 'success' });
          return true;
        } else {
          this.clearLoginState();
          wx.showToast({ title: '登录认证失败', icon: 'none' });
          wx.hideLoading();
          return false;
        }
      } catch (error: any) {
        this.clearLoginState();
        wx.showToast({ title: '登录过程异常', icon: 'none' });
        wx.hideLoading();
        return false;
      }
    }

    return false
  }
} 