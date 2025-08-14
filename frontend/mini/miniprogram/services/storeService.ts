import apiService, { StoreDetail } from './api'
import type { StoreListParams } from './api'
import type { IAppOptionExtended } from '../app'

export class StoreService {
  private app: IAppOptionExtended | null = null;

  init(app: IAppOptionExtended): void {
    this.app = app;
  }

  /**
   * 获取门店列表
   * @returns 返回门店列表数据
   */
  async fetchStores(options: {
    keyword?: string;
    location?: WechatMiniprogram.GetLocationSuccessCallbackResult | null;
  }): Promise<StoreDetail[]> {
    if (!this.app) return [];

    const { keyword, location } = options;
    const params: StoreListParams = {};

    // 添加位置参数
    if (location) {
      params.latitude = location.latitude;
      params.longitude = location.longitude;
      params.radius = 10; // 默认5公里
    }

    // 添加关键词
    if (keyword) {
      params.keyword = keyword;
    }

    try {
      const stores = await apiService.getNearbyStores(params);
      return stores;
    } catch (error) {
      wx.showToast({
        title: '获取门店失败，请稍后重试',
        icon: 'none'
      });
      return [];
    }
  }
} 