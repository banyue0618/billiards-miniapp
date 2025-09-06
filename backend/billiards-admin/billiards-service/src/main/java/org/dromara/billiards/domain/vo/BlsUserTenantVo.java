package org.dromara.billiards.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsUserTenant;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 平台用户-租户映射视图对象 bls_user_tenant
 *
 * @author banyue
 * @date 2025-08-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsUserTenant.class)
public class BlsUserTenantVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 平台用户ID
     */
    @ExcelProperty(value = "平台用户ID")
    private Long userId;

    /**
     * 租户内的用户名/昵称
     */
    @ExcelProperty(value = "租户内的用户名/昵称")
    private String tenantUserName;

    /**
     * 租户内绑定手机号
     */
    @ExcelProperty(value = "租户内绑定手机号")
    private String tenantPhone;

    /**
     * 角色(USER/MEMBER/ADMIN)
     */
    @ExcelProperty(value = "角色(USER/MEMBER/ADMIN)")
    private String role;

    /**
     * 是否在该租户下是会员
     */
    @ExcelProperty(value = "是否在该租户下是会员")
    private Long isMember;

    /**
     * 状态 0-正常 1-禁用 2-注销
     */
    @ExcelProperty(value = "状态 0-正常 1-禁用 2-注销")
    private Long status;

    /**
     * 首次登录时间
     */
    @ExcelProperty(value = "首次登录时间")
    private Date firstTime;

    /**
     * 最后登录时间
     */
    @ExcelProperty(value = "最后登录时间")
    private Date lastTime;


}
