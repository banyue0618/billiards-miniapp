/// <reference path="./types/index.d.ts" />

interface IAppOption {
  globalData: {
    userInfo?: WechatMiniprogram.UserInfo,
  }
  userInfoReadyCallback?: WechatMiniprogram.GetUserInfoSuccessCallback,
}

// 扩展wx对象，添加eventBus属性
declare namespace WechatMiniprogram {
  interface Wx {
    eventBus?: {
      emit: (eventName: string, data?: any) => void;
      on: (eventName: string, callback: Function, componentId?: string) => void;
      off: (eventName: string, componentId?: string) => void;
    };
  }
}