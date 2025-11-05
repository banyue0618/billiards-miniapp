package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 桌台标签视图对象
 */
@Data
@Schema(description = "桌台标签")
public class TableTagVO {

    @Schema(description = "标签类型 disinfection-时间段消毒 holiday-节假日优惠 discount-会员折扣 maintenance-维护中 other-其他")
    private String type;

    @Schema(description = "标签文本")
    private String text;
}

