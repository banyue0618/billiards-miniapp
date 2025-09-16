/**
 * API服务
 */
import http from './http'

// 门店列表参数
export interface StoreListParams {
  latitude?: number;
  longitude?: number;
  radius?: number; // Note: Backend expects radius in KM, ensure frontend sends appropriately or backend adjusts.
  status?: number; // This might be deprecated if filtering is based on new string status from dict.
  keyword?: string;
  pageNum?: number;
  pageSize?: number;
  storeId?: string;
}

// 门店详情参数
export interface StoreDetailParam {
  latitude?: number;
  longitude?: number;
  storeId?: string;
}

// 门店详情 - This interface will now be directly used by getNearbyStores
export interface StoreDetail {
  id: string;
  name: string;
  coverImage: string; // Path to the original image, if needed by frontend
  images: string[]; // List of previewable image URLs
  status: string; // e.g., "营业中", "休息中" (text from dictionary)
  businessHours: string;
  address: {
    province: string;
    city: string;
    district: string;
    street: string;
    latitude: number;
    longitude: number;
  };
  tables: {
    total: number;
    available: number;
  };
  minPrice: string; // 最低价格，单位：元/时
  contactPhone: string;
  announcement: {
    content: string;
    updateTime: string; // Formatted datetime string
  };
  description?: string;
  distance?: number; // 用户到门店的距离，单位：米
  priceRule?: {
    type: string; // 计费类型: standard-标准计费, ladder-阶梯计费
    priceUnit: number; // 价格单位，分/分钟
    maxPrice?: number; // 封顶价格，分
    minConsumption?: number; // 最低消费时间，分钟
    ladderRules?: any; // 阶梯计费规则，字符串或对象
  };
  priceList?: Array<{
    type: string;  // 球桌类型，如"普通"、"专业"、"大师"
    price: string; // 价格，字符串格式如"10.20"
    memberDiscount?: number; // 会员折扣
  }>;
}

// 桌台信息
export interface TableInfo {
  id: string;
  storeId: string;
  storeName: string; // 门店名称
  tableNumber: string;
  type: string;
  status: number; // 0-空闲 1-使用中 2-维修中 3-锁定
  priceRule: {
    ruleType: number; // 1, 2
    priceUnit?: number; // 标准价格，单位：分/分钟
    memberPrice?: number; // 会员价格，单位：分/分钟
    memberDiscount?: number; // 会员折扣,如0.8表示8折
    ladderRules?: string; // 阶梯计费、计费规则
    maxPrice?: number; // 封顶价格，单位：分
  };
}

// 订单信息
export interface OrderInfo {
  id?: string;
  orderNo?: string;
  userId?: number;
  storeId?: string;
  storeName: string;
  tableId?: string;
  tableNumber: string;
  priceRuleId?: string;
  startTime: string; // yyyy-MM-dd HH:mm:ss
  endTime?: string; // yyyy-MM-dd HH:mm:ss
  duration?: number; // 分钟
  originalAmount?: number;
  discountAmount?: number;
  actualAmount?: number; // 元
  paymentStatus?: number; // 0未支付 1已支付 2退款
  status?: number; // 0进行中 1已完成 2已取消
  createTime?: string;
  updateTime?: string;
  priceUnit?: number; // 标准计费 单价（元/分）
  memberPrice?: number; // 标准计费 会员价（元/分）
  ladderRules?: string; // 阶梯计费JSON
  memberDiscount?: number; // 会员折扣
  refundAmount?: number;
  refundStatus?: number;
  userName?: string;
  userPhone?: string;
}

// 用户信息
export interface UserInfo {
  id: string;
  nickname: string;
  avatarUrl: string;
  isMember: boolean;
  memberLevel?: number;
  member_expire_time?: string;
  points?: number;
  balance?: number;
}

/**
 * API服务类
 */
class ApiService {
  /**
   * 用户登录
   * @param body 登录凭证信息字符串
   */
  login(body: any) {
    return http.post('/auth/login', body)
  }

  /**
   * 获取用户信息
   */
  getUserInfo() {
    return http.get<UserInfo>('/api/miniapp/user/info')
  }

  /**
   * 获取附近门店列表
   * @param params 查询参数
   */
  getNearbyStores(params: StoreListParams) {
    return http.post<StoreDetail[]>(
        '/api/miniapp/stores/nearby',
        params
    );
  }

  /**
   * 获取门店详情
   * @param storeId 门店ID
   */
  getStoreDetail(request: StoreDetailParam) {
    return http.post<StoreDetail>(`/api/miniapp/stores/detail`, request)
  }

  /**
   * 校验用户是否允许开台
   */
  scanTableEnableCheck() {
    return http.get<boolean>(`/api/miniapp/user/scanTableEnableCheck`)
  }

  /**
   * 获取门店桌台列表
   * @param storeId 门店ID
   * @param params 查询参数
   */
  getStoreTables(storeId: string, params?: { status?: string; type?: string }) {
    return http.get<TableInfo[]>(`/api/miniapp/tables/${storeId}/tables`, params)
  }

  /**
   * 扫码识别桌台(应该是识别解析二维码内容)
   * @param tableId 二维码内容
   */
  scanTable(tableId: string) {
    return http.get<TableInfo>(`/api/miniapp/tables/qrcode/${tableId}`)
  }

   /**
   * 随机获取一个桌台id
   */
  randomTableId() {
    return http.get<string>(`/api/miniapp/tables/randomTableId`)
  }

  /**
   * 创建开台订单
   * @param channel 渠道
   * @param tableId 桌台ID
   */
  createOrder(channel: string, tableId: string) {
    return http.post<OrderInfo>('/api/miniapp/orders/create', { channel, tableId })
  }

  /**
   * 结束使用订单
   * @param orderId 订单ID
   */
  endOrder(orderId: string) {
    // 后端返回 OrderVO，这里与 OrderInfo 对齐
    return http.post<OrderInfo>(`/api/miniapp/orders/${orderId}/end`)
  }

  /**
   * 获取进行中的订单
   */
  getOngoingOrders() {
    return http.get<OrderInfo[]>('/api/miniapp/orders/current')
  }

  /**
   * 获取用户订单列表
   * @param params 查询参数
   */
  getUserOrders(params: {
    status?: number;
    startDate?: string;
    endDate?: string;
    pageNum?: number;
    pageSize?: number;
  }) {
    return http.get<{
      total: number;
      records: OrderInfo[];
    }>('/api/miniapp/orders/list', params)
  }

  /**
   * 获取订单详情
   * @param orderId 订单ID
   */
  getOrderDetail(orderId: string) {
    return http.get<OrderInfo>(`/api/miniapp/orders/${orderId}`)
  }

  /**
   * 调用预支付接口
   * @param amount 充值金额
   */
  createPayment(amount: number) {
    return http.post('/api/miniapp/payment/pay', { amount })
  }

  /**
   * 查询支付状态
   * @param orderId 订单ID
   */
  getPaymentStatus(orderId: string) {
    return http.get(`/api/miniapp/payment/status/${orderId}`)
  }

  /**
   * 更新用户资料
   * @param profileData 包含用户昵称和头像的对象
   */
  updateUserProfile(profileData: { nickname: string; avatarUrl: string }) {
    // 直接修改请求URL以符合实际接口
    return http.put('/api/miniapp/user/update', profileData)
  }
}

// 导出API服务实例
export default new ApiService() 