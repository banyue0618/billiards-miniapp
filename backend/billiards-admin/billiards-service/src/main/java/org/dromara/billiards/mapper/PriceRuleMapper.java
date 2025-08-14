package org.dromara.billiards.mapper;

import org.dromara.billiards.domain.entity.PriceRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计费规则Mapper接口
 */
@Mapper
public interface PriceRuleMapper extends BaseMapper<PriceRule> {

    /**
     * 根据商家ID查询计费规则列表
     */
    List<PriceRule> selectByMerchantId(@Param("merchantId") String merchantId);

    /**
     * 根据ID查询计费规则详情
     */
    PriceRule selectRuleById(@Param("id") String id);

    /**
     * 根据桌台类型查询默认计费规则
     */
    PriceRule selectDefaultByTableType(@Param("merchantId") String merchantId, @Param("tableType") Integer tableType);

    /**
     * 查询指定门店下所有桌台中，标准计费规则（rule_type=1, status=0）的最低price_unit (元/分钟)
     * @param storeId 门店ID
     * @return 最低的标准计费单价 (元/分钟)，如果找不到则返回null
     */
    BigDecimal findMinStandardPriceUnitForStore(@Param("storeId") String storeId);
}
