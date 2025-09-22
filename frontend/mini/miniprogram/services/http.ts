/**
 * HTTP 请求服务
 */
import { showError } from '../utils/util'
import type { IAppOptionExtended } from '../app'

// API 基础地址 - 开发环境
const BASE_URL = 'https://banyue.xin'

// 请求队列
const requestQueue: Set<string> = new Set()

// 请求配置接口
interface RequestConfig extends Omit<WechatMiniprogram.RequestOption, 'url'> {
  url: string;
  showLoading?: boolean;
  loadingText?: string;
  showError?: boolean;
}

// 响应数据接口
interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

/**
 * HTTP请求工具类
 */
class Http {
  /**
   * 发送请求
   * @param config 请求配置
   */
  request<T = any>(config: RequestConfig): Promise<T> {
    // 默认配置
    const defaultConfig: Omit<RequestConfig, 'url'> = {
      timeout: 10000,
      showLoading: true,
      loadingText: '加载中...',
      showError: true,
      header: {
        'content-type': 'application/json'
      }
    }

    // 合并配置
    const mergedConfig = { ...defaultConfig, ...config }
    
    // 添加认证头和clientId
    const token = wx.getStorageSync('token')
    const app = getApp<IAppOptionExtended>();
    
    const storeId = wx.getStorageSync('X-Store-Id')
    mergedConfig.header = {
      ...mergedConfig.header,
      'clientid': app ? app.clientId : '',
      ...(storeId ? { 'X-Store-Id': storeId } : {})
    }
    
    if (token) {
      mergedConfig.header = {
        ...mergedConfig.header,
        'Authorization': `Bearer ${token}`
      }
    }

    // 完整URL
    let url = config.url
    if (!url.startsWith('http')) {
      url = BASE_URL + url
    }
    mergedConfig.url = url

    // 生成请求Key
    const requestKey = `${mergedConfig.url}_${mergedConfig.method || 'GET'}`
    
    // 防止重复请求
    if (requestQueue.has(requestKey)) {
      return Promise.reject(new Error('请求正在进行中，请勿重复提交'))
    }
    
    // 将请求添加到队列
    requestQueue.add(requestKey)
    
    // 显示加载提示
    if (mergedConfig.showLoading) {
      wx.showLoading({
        title: mergedConfig.loadingText || '加载中...',
        mask: true
      })
    }

    // 发送请求
    return new Promise<T>((resolve, reject) => {
      wx.request({
        ...mergedConfig,
        url,
        success: (res: any) => {
          // 响应状态码处理
          if (res.statusCode !== 200) {
            // HTTP错误
            this.handleHttpError(res, mergedConfig)
            reject(new Error(`HTTP错误: ${res.statusCode}`))
            return
          }
          
          // 业务状态码处理
          const response = res.data as ApiResponse<T>
          if (response.code !== 200) {
            // 业务错误
            this.handleBusinessError(response, mergedConfig)
            console.log("业务异常：{}", response.msg);
            reject(new Error(response.msg || '请求失败'))
            return
          }
          
          // 请求成功
          resolve(response.data)
        },
        fail: (err) => {
          // 网络错误
          if (mergedConfig.showError) {
            showError('网络异常，请检查网络设置')
          }
          reject(err)
        },
        complete: () => {
          // 隐藏加载提示
          if (mergedConfig.showLoading) {
            wx.hideLoading()
          }
          
          // 从队列中移除请求
          requestQueue.delete(requestKey)
        }
      })
    })
  }

  /**
   * 处理HTTP错误
   */
  private handleHttpError(res: WechatMiniprogram.RequestSuccessCallbackResult, config: RequestConfig): void {
    if (!config.showError) return

    // 根据状态码处理
    switch (res.statusCode) {
      case 401:
        // 未认证
        showError('登录已失效，请重新登录')
        this.handleUnauthorized()
        break
      case 403:
        // 权限不足
        showError('没有访问权限')
        break
      case 404:
        // 资源不存在
        showError('请求的资源不存在')
        break
      case 500:
      case 502:
      case 503:
        // 服务器错误
        showError('服务器异常，请稍后再试')
        break
      default:
        // 其他错误
        showError(`请求失败(${res.statusCode})`)
    }
  }

  /**
   * 处理业务错误
   */
  private handleBusinessError(response: ApiResponse, config: RequestConfig): void {
    if (!config.showError) return
    
    // 显示业务错误
    showError(response.message || '请求失败')
    
    // 特殊错误码处理
    switch (response.code) {
      case 1001:
        // 登录凭证无效
        this.handleUnauthorized()
        break
      // 其他业务错误码处理...
    }
  }

  /**
   * 处理未授权错误
   */
  private handleUnauthorized(): void {
    // 清除登录信息
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    
    // 跳转到登录页（如果有的话）
    setTimeout(() => {
      wx.redirectTo({
        url: '/pages/user/index'
      })
    }, 1500)
  }

  /**
   * GET请求
   */
  get<T = any>(url: string, data?: any, config?: Omit<RequestConfig, 'url'>): Promise<T> {
    return this.request<T>({
      url,
      method: 'GET',
      data,
      ...config
    })
  }

  /**
   * POST请求
   */
  post<T = any>(url: string, data?: any, config?: Omit<RequestConfig, 'url'>): Promise<T> {
    return this.request<T>({
      url,
      method: 'POST',
      data,
      ...config
    })
  }

  /**
   * PUT请求
   */
  put<T = any>(url: string, data?: any, config?: Omit<RequestConfig, 'url'>): Promise<T> {
    return this.request<T>({
      url,
      method: 'PUT',
      data,
      ...config
    })
  }

  /**
   * DELETE请求
   */
  delete<T = any>(url: string, data?: any, config?: Omit<RequestConfig, 'url'>): Promise<T> {
    return this.request<T>({
      url,
      method: 'DELETE',
      data,
      ...config
    })
  }
}

// 导出HTTP实例
export default new Http() 