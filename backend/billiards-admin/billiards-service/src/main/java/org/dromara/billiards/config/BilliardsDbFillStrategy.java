package org.dromara.billiards.config;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsUser;
import org.dromara.common.mybatis.DataSourceFillStrategy;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.MerchantHolder;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/5/7
 */
@Slf4j
@Component
public class BilliardsDbFillStrategy implements DataSourceFillStrategy {
    @Override
    public String getDataSourceName() {
        return BilliardsConstants.DS_BILLIARDS_PLATFORM;
    }

    @Override
    public void insertFill(MetaObject metaObject) {

        // 创建时间和更新时间填充
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 创建人和更新人填充
        this.strictInsertFill(metaObject, "createBy", String.class, getLoginUsername());
        this.strictInsertFill(metaObject, "updateBy", String.class, getLoginUsername());

        if(!(metaObject.getOriginalObject() instanceof BlsUser)){
            // 租户id填充
            this.strictInsertFill(metaObject, "tenantId", String.class, TenantHelper.getTenantId());
            // 商户id
            this.strictInsertFill(metaObject, "merchantId", String.class, MerchantHolder.get());
        }
        // 删除标记填充
        this.strictInsertFill(metaObject, "isDelete", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        // 更新时间填充
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 更新人填充
        this.strictUpdateFill(metaObject, "updateBy", String.class, getLoginUsername());
    }

    /**
     * 获取当前登录用户名
     */
    private String getLoginUsername() {
        try {
            if (StpUtil.isLogin()) {
                return LoginHelper.getUserIdStr();
            }
        } catch (Exception e) {
            log.debug("获取当前登录用户异常", e);
        }
        return "system";
    }
}
