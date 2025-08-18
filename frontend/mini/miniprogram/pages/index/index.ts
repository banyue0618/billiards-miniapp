// 首页
import { showError, showLoading, hideLoading } from '../../utils/util'
import { loginCheckAndpublishEvent } from '../../utils/loginUtil'
import { StoreDetail, UserInfo as CustomUserInfo } from '../../services/api'
import type { IAppOptionExtended } from '../../app'

const app = getApp<IAppOptionExtended>();

Page({
  data: {
    hasLocationAuth: false, // 是否授权位置
    location: null as { latitude: number; longitude: number } | null, // 用户位置
    keyword: '', // 搜索关键词
    stores: [] as StoreDetail[], // 门店列表
    loading: false, // 是否正在加载
    storeLoading: false, // 门店是否正在加载
    isLoggedIn: false,
    customUserInfo: null as CustomUserInfo | null,
    showLoginPrompt: false, // 初始假设需要显示登录提示
    hasMore: false, // 是否有更多数据
    checkingLocation: false, // 是否正在检查位置授权状态
  },

  onLoad() {
    // onLoad 时，app.ts 的 onLaunch 可能仍在执行
  },

  onShow() {
    console.log('首页 onShow - 检查位置和登录状态');
    // 同步 app.ts 的最新状态
    this.updateLoginStateFromApp();
    this.updateLocationFromApp();
    // 判断用户是否登录，如果已经登录，则通知其他组件
    loginCheckAndpublishEvent();
  

    // 先获取位置信息，再获取门店数据
    app.locationService.requestLocation().then(() => {
      console.log('位置信息获取完成，加载门店数据...');
      this.updateLocationFromApp();
      // 获取位置后再加载门店数据
      this.fetchStores();
    }).finally(() => {
      this.setData({ loading: false });
    });

    // 更新自定义 tabBar 的选中状态
    this.updateTabBarSelected();
  },

  // 更新 tabBar 选中状态
  updateTabBarSelected() {
    if (typeof this.getTabBar === 'function') {
      this.getTabBar().setData({
        selected: 0 // 首页索引为 0
      });
    }
  },

  updateLoginStateFromApp() {
    this.setData({
      isLoggedIn: app.isLoggedIn,
      customUserInfo: app.customUserInfo,
      showLoginPrompt: !app.isLoggedIn,
    });
  },

  updateLocationFromApp() {
    const currentLocation = app.userLocation ? { 
      latitude: app.userLocation.latitude, 
      longitude: app.userLocation.longitude 
    } : null;
    this.setData({
      location: currentLocation,
      hasLocationAuth: !!app.userLocation,
      checkingLocation: false, // 确保检查完成后重置状态
    });
  },

  // 请求位置授权 - 使用微信原生弹窗
  async requestLocationAuth() {
    if (this.data.checkingLocation) return;
    
    this.setData({ checkingLocation: true, loading: true });
    try {
      await app.locationService.requestLocation();
      this.updateLocationFromApp();
      if (this.data.hasLocationAuth) {
        // 成功获取位置后，加载门店数据
        this.fetchStores();
      } else {
        // 无法获取位置时显示空状态
        this.setData({ stores: [] });
      }
    } catch (error) {
      console.error('位置授权错误', error);
      // 出错时也尝试加载门店数据
      this.fetchStores();
    } finally {
      this.setData({ checkingLocation: false, loading: false });
    }
  },

  // 获取门店列表数据
  async fetchStores(keyword?: string) {
    if (this.data.storeLoading) return;
    
    this.setData({ storeLoading: true });
    
    try {
      // 使用StoreService获取门店数据
      const storeData = await app.storeService.fetchStores({
        keyword: keyword || this.data.keyword,
        location: app.userLocation
      });
      
      // 更新本地数据
      this.setData({
        hasLocationAuth: true,
        stores: storeData,
        hasMore: false, // 当前版本暂不支持分页加载
        storeLoading: false
      });
    } catch (error) {
      console.error('获取门店数据失败', error);
      showError('获取门店数据失败');
      this.setData({ loading: false });
    }
  },

  async handleGetPhoneNumber(e: WechatMiniprogram.ButtonGetPhoneNumber) {
    if (!e.detail.code) { 
      wx.showToast({ title: e.detail.errMsg === 'getPhoneNumber:fail user deny' ? '您已拒绝授权' : e.detail.errMsg, icon: 'none' });
      return;
    }
    
    showLoading('登录中...');
    const loginSuccess = await app.authService.login(e); 
    hideLoading();

    if (loginSuccess) {
      this.updateLoginStateFromApp(); // 更新页面状态
      wx.showToast({ title: '登录成功!', icon: 'success' });
      // 登录成功，通知其他组件
      loginCheckAndpublishEvent();
    }
  },
  
  async onPullDownRefresh() {
    if (this.data.loading) {
      wx.stopPullDownRefresh();
      return;
    } 
    
    console.log('首页下拉刷新');
    this.setData({ loading: true }); 
    
    try {
      loginCheckAndpublishEvent();
      
      // 重新获取位置信息
      await app.locationService.requestLocation();
      this.updateLocationFromApp();

      // 刷新门店列表
      await this.fetchStores();
      
      wx.showToast({
        title: '刷新完成', 
        icon: 'success', 
        duration: 1500
      });
      
    } catch (error) {
      console.error('下拉刷新失败', error);
      wx.showToast({
        title: '刷新失败', 
        icon: 'error',
        duration: 1500
      });
    } finally {
      this.setData({ loading: false });
      // 停止下拉刷新动画
      wx.stopPullDownRefresh();
    }
  },

  onReachBottom() {
    // 当前版本门店数据非分页加载，此函数暂时不执行有效操作
    console.log('onReachBottom: 门店列表当前非分页，不加载更多。');
    if (this.data.stores.length > 0) { // 如果已经有数据，就认为没有更多了
      this.setData({ hasMore: false });
    }
  },
  
  async searchStoresByKeyword() {
    if (this.data.loading) return;
    wx.showLoading({ title: '搜索中...'});
    this.setData({ loading: true }); 
    
    // 直接使用关键词获取门店数据
    await this.fetchStores(this.data.keyword);
    
    wx.hideLoading();
  },

  handleInputChange(e: any) {
    this.setData({
      keyword: e.detail.value
    })
  },

  handleSearch() {
    if(!this.data.keyword.trim()){ // 如果关键词为空，则刷新列表
        this.onPullDownRefresh();
        return;
    }
    this.searchStoresByKeyword();
  },

  handleClearKeyword() {
    this.setData({
      keyword: ''
    });
    this.onPullDownRefresh(); // 清空关键词后刷新列表
  }
}) 