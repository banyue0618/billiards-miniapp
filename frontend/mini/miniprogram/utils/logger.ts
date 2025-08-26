enum LogLevel {
  DEBUG = 0,
  INFO = 1,
  WARN = 2,
  ERROR = 3,
  NONE = 100
}

interface LogConfig {
  level: LogLevel;
  prefix?: string;
  showTimestamp?: boolean;
}

/**
 * 日志管理类
 */
export class Logger {
  private static instance: Logger;
  private config: LogConfig = {
    level: LogLevel.INFO,
    prefix: '',
    showTimestamp: true
  };

  private constructor() {}

  /**
   * 获取Logger实例
   */
  static getInstance(): Logger {
    if (!Logger.instance) {
      Logger.instance = new Logger();
    }
    return Logger.instance;
  }

  /**
   * 配置日志
   */
  configure(config: Partial<LogConfig>): void {
    this.config = { ...this.config, ...config };
  }

  /**
   * 获取当前时间戳
   */
  private getTimestamp(): string {
    if (!this.config.showTimestamp) return '';
    return `[${new Date().toISOString()}]`;
  }

  /**
   * 获取格式化的前缀
   */
  private getPrefix(): string {
    return this.config.prefix ? `[${this.config.prefix}]` : '';
  }

  /**
   * 调试日志
   */
  debug(message: string, ...args: any[]): void {
    if (this.config.level <= LogLevel.DEBUG) {
      console.debug(`${this.getTimestamp()}${this.getPrefix()}[DEBUG] ${message}`, ...args);
    }
  }

  /**
   * 信息日志
   */
  info(message: string, ...args: any[]): void {
    if (this.config.level <= LogLevel.INFO) {
      console.log(`${this.getTimestamp()}${this.getPrefix()}[INFO] ${message}`, ...args);
    }
  }

  /**
   * 警告日志
   */
  warn(message: string, ...args: any[]): void {
    if (this.config.level <= LogLevel.WARN) {
      console.warn(`${this.getTimestamp()}${this.getPrefix()}[WARN] ${message}`, ...args);
    }
  }

  /**
   * 错误日志
   */
  error(message: string, ...args: any[]): void {
    if (this.config.level <= LogLevel.ERROR) {
      console.error(`${this.getTimestamp()}${this.getPrefix()}[ERROR] ${message}`, ...args);
    }
  }
} 