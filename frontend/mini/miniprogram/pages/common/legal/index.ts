// 法律协议页面
Page({
  data: {
    currentTab: 0 // 0: 用户协议, 1: 隐私政策
  },
  
  onLoad(options: Record<string, string>) {
    // 如果有传入类型参数，切换到对应的标签页
    if (options.type === 'privacy') {
      this.setData({ currentTab: 1 });
    } else {
      this.setData({ currentTab: 0 });
    }
    
    // 根据标签页设置导航栏标题
    this.setNavigationTitle();
  },
  
  // 切换标签页
  switchTab(e: WechatMiniprogram.TouchEvent) {
    const tab = Number(e.currentTarget.dataset.tab);
    this.setData({ currentTab: tab });
    
    // 根据标签页设置导航栏标题
    this.setNavigationTitle();
  },
  
  // 设置导航栏标题
  setNavigationTitle() {
    if (this.data.currentTab === 0) {
      wx.setNavigationBarTitle({ title: '用户协议' });
    } else {
      wx.setNavigationBarTitle({ title: '隐私政策' });
    }
  },
  
  // 分享
  onShareAppMessage() {
    const title = this.data.currentTab === 0 ? '用户协议' : '隐私政策';
    const path = `/pages/common/legal/index?type=${this.data.currentTab === 0 ? 'protocol' : 'privacy'}`;
    
    return {
      title: `${title} - 台球预订小程序`,
      path
    };
  }
}) 