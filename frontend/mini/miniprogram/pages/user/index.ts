// 用户页面
import { showError, showLoading, hideLoading, showSuccess, showConfirm } from '../../utils/util'
import apiService, { UserInfo } from '../../services/api'
// 使用 CommonJS 导入方式
const loginUtil = require('../../utils/loginUtil');

const app = getApp();

Page({
  data: {
    isLogin: false, // 是否已登录
    userInfo: null as UserInfo | null, // 用户信息
    memberExpireText: '', // 会员到期文本
    memberLevelText: '',
    showLoginModal: false, // 是否显示登录弹窗
    loadingUser: false // 下拉刷新用户
  },

  /**
   * 生命周期函数 - 页面加载时触发，只会触发一次
   * 作用：适合进行初始化工作，如获取页面参数、初始化页面状态等
   * 时机：在页面初始化时触发，早于onShow
   */
  onLoad() {
    // 在onLoad中初始化页面，首次加载时检查登录状态
    this.checkLoginStatus();
  },

  /**
   * 生命周期函数 - 页面显示时触发，每次页面显示都会触发
   * 作用：适合更新页面数据、处理页面显示逻辑等
   * 时机：页面每次进入可见状态时都会触发，如首次加载、从其他页面返回、小程序前台显示等
   */
  onShow() {
    // 更新自定义 tabBar 的选中状态
    this.updateTabBarSelected();
    
    // 如果已经登录，检查登录状态是否发生变化
    // 比如：用户可能在其他页面登出或登录状态过期
    if (this.data.isLogin !== loginUtil.isLoggedIn()) {
      this.checkLoginStatus();
    }
  },

  async onPullDownRefresh() {
    if (this.data.loadingUser) return; 
    this.setData({ loadingUser: true }); 
    wx.showLoading({ title: '刷新中...'});
    try {
      // 重新获取位置信息
      await this.getUserInfo();
      
      wx.showToast({title: '刷新完成', icon: 'none'});
      wx.hideLoading();
    } catch (error) {
      console.log(error);
      showError('刷新失败');
    } finally {
      this.setData({ loadingUser: false });
      wx.stopPullDownRefresh();
    }
  },
  
  // 更新 tabBar 选中状态
  updateTabBarSelected() {
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().setData({
        selected: 2 // 用户页面索引为 2
      });
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    const isLoggedIn = loginUtil.isLoggedIn();
    
    if (isLoggedIn) {
      // 已登录，获取用户信息
      this.setData({ isLogin: true })
      this.getUserInfo()
    } else {
      // 未登录
      this.setData({ 
        isLogin: false,
        userInfo: null
      })
    }
  },

  // 获取用户信息
  async getUserInfo() {
    try {
      showLoading('加载中...')
      
      const userInfo = await apiService.getUserInfo()
      
      // 处理会员到期时间
      let memberExpireText = ''
      if (userInfo.isMember && userInfo.member_expire_time) {
        // 解析时间
        const expireDate = new Date(userInfo.member_expire_time)
        // 格式化为文本
        memberExpireText = `有效期至 ${expireDate.getFullYear()}.${expireDate.getMonth() + 1}.${expireDate.getDate()}`
      }
      let memberLevelText = '';
      if(userInfo.memberLevel){
        if(userInfo.memberLevel == 0){
          memberLevelText = "白银会员";
        }else if(userInfo.memberLevel == 1){
          memberLevelText = "黄金会员";
        }else{
          memberLevelText = "钻石会员";
        }
      }
      this.setData({ 
        userInfo,
        memberExpireText,
        memberLevelText
      })
      
      hideLoading()
    } catch (error: any) {
      hideLoading()
      console.error('获取用户信息失败', error)
      
      // 只有在授权相关错误时才清除登录状态
      if (error && typeof error === 'object') {
        // 检查是否为重复请求错误
        if (error.message && error.message.includes('请求正在进行中')) {
          console.log('重复请求，忽略错误');
          return;
        }
        
        // 检查是否为401未授权错误或token相关错误
        const isAuthError = error.statusCode === 401 || 
                           (error.errMsg && error.errMsg.includes('token')) ||
                           (error.message && (
                             error.message.includes('登录') || 
                             error.message.includes('授权') || 
                             error.message.includes('认证') || 
                             error.message.includes('token')
                           ));
        
        if (isAuthError) {
          console.log('授权错误，清除登录状态');
          // 清除登录状态
          if (app && app.authService) {
            app.authService.clearLoginState();
          }
          
          this.setData({ 
            isLogin: false,
            userInfo: null
          });
        }
      }
    }
  },
  
  // 获取用户信息资料
  async handleGetUserProfile() {
    if (!this.data.isLogin) {
      this.handleLogin();
      return;
    }
    
    try {
      // 调用微信API获取用户信息
      const profileRes = await wx.getUserProfile({
        desc: '用于完善会员资料'
      });
      
      if (!profileRes.userInfo) {
        showError('获取信息失败');
        return;
      }
      
      showLoading('更新资料中...');
      
      try {
        // 调用API更新用户资料
        await apiService.updateUserProfile({
          nickname: profileRes.userInfo.nickName,
          avatarUrl: profileRes.userInfo.avatarUrl
        });
        
        hideLoading();
        showSuccess('资料更新成功');
        
        // 重新获取用户信息
        this.getUserInfo();
      } catch (apiError: any) {
        hideLoading();
        // 如果有错误信息，显示错误信息；否则显示默认错误信息
        showError(apiError.message || '资料更新失败');
      }
    } catch (error: any) {
      hideLoading();
      
      // 用户拒绝授权的特殊处理
      if (error.errMsg && error.errMsg.includes('getUserProfile:fail')) {
        showError('您已拒绝授权获取信息');
      } else {
        showError('获取资料异常');
      }
    }
  },

  // 打开登录弹窗
  handleLogin() {
    loginUtil.showLoginModal(this);
  },

  // 关闭登录弹窗
  closeLoginModal() {
    loginUtil.closeLoginModal(this);
  },

  // 登录成功回调
  onLoginSuccess() {
    this.setData({ isLogin: true });
    this.getUserInfo();
  },

  // 退出登录
  async handleLogout() {
    const res = await showConfirm('提示', '确定要退出登录吗？')
    
    if (res.confirm) {
      // 使用 authService 清除登录状态
      if (app && app.authService) {
        app.authService.clearLoginState();
      }
      
      // 更新页面状态
      this.setData({ 
        isLogin: false,
        userInfo: null
      })
      
      showSuccess('已退出登录')
    }
  },

  // 购买会员
  handleBuyMember() {
    wx.navigateTo({
      url: '/pages/user/member'
    })
  },

  // 查看会员权益
  handleViewMember() {
    wx.navigateTo({
      url: '/pages/user/member'
    })
  },

  // 页面导航
  navigateTo(e: any) {
    const { url } = e.currentTarget.dataset
    
    // 使用登录拦截器
    const isLoggedIn = loginUtil.loginInterceptor(this, () => {
      wx.navigateTo({ url });
    });
    
    // 如果已登录，导航方法会在回调中执行
  },

  // 联系客服
  handleContact() {
    wx.navigateTo({
      url: '/pages/user/contact'
    })
  }
}) 