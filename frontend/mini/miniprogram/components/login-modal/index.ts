// 登录弹窗组件
import type { IAppOptionExtended } from '../../app';

const app = getApp<IAppOptionExtended>();

Component({
  properties: {
    show: {
      type: Boolean,
      value: false
    }
  },
  
  data: {},
  
  methods: {
    // 关闭弹窗
    handleClose() {
      this.triggerEvent('close');
    },
    
    // 处理微信手机号登录
    async handlePhoneLogin(e: WechatMiniprogram.ButtonGetPhoneNumber) {
      const authService = app.authService;
      if (!authService) {
        wx.showToast({ 
          title: '授权服务未初始化', 
          icon: 'none' 
        });
        return;
      }
      
      const success = await authService.login(e);
      if (success) {
        this.triggerEvent('login-success');
        this.triggerEvent('close');
      }
    },
    
    // 跳转到法律协议页面（默认显示隐私政策）
    navigateToPrivacy() {
      wx.navigateTo({
        url: '/pages/common/legal/index?type=privacy'
      });
    }
  }
})