// 为wx对象添加事件总线功能
export interface WxWithEvents extends WechatMiniprogram.Wx {
  triggerAppEvent?: (eventName: string, data?: any) => void;
  onAppEvent?: (eventName: string, callback: Function, componentId?: string) => void;
  offAppEvent?: (eventName: string, componentId?: string) => void;
}

// 事件监听器接口
export interface EventListener {
  callback: Function;
  componentId?: string;
} 