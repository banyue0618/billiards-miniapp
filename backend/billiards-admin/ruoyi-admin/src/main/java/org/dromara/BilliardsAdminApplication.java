package org.dromara;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 自助台球厅管理系统应用程序启动类
 */
@SpringBootApplication
@EnableTransactionManagement
// 扩展Mapper扫描路径，包括若依框架的Mapper和您的应用Mapper
@MapperScan({"org.dromara.**.mapper"})
//@ServletComponentScan(basePackages = {})  // 禁用WebFilter等自动扫描，使用FilterRegistrationBean注册过滤器
public class BilliardsAdminApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BilliardsAdminApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  自助台球厅管理系统启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
