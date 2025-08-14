package org.dromara.billiards.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.billiards.domain.vo.dashboard.HourlyAnalysisVO;
import org.dromara.billiards.domain.vo.dashboard.PriceRuleAnalysisVO;
import org.dromara.billiards.domain.vo.dashboard.StoreRankingVO;
import org.dromara.billiards.domain.vo.dashboard.TopTableVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 仪表盘数据访问接口
 */
@Mapper
public interface DashboardMapper {

    /**
     * 计算指定日期的营收
     *
     * @param date     日期
     * @param storeIds 门店ID列表
     * @return 营收金额
     */
    BigDecimal calculateDailyRevenue(@Param("date") LocalDate date, @Param("storeIds") List<Long> storeIds);

    /**
     * 统计指定日期的订单数
     *
     * @param date     日期
     * @param storeIds 门店ID列表
     * @return 订单数
     */
    Integer countDailyOrders(@Param("date") LocalDate date, @Param("storeIds") List<Long> storeIds);

    /**
     * 计算指定日期的台球桌使用率
     *
     * @param date     日期
     * @param storeIds 门店ID列表
     * @return 使用率（百分比）
     */
    BigDecimal calculateDailyTableUsageRate(@Param("date") LocalDate date, @Param("storeIds") List<Long> storeIds);

    /**
     * 统计指定日期的新增会员数
     *
     * @param date     日期
     * @param storeIds 门店ID列表
     * @return 新增会员数
     */
    Integer countDailyNewMembers(@Param("date") LocalDate date, @Param("storeIds") List<Long> storeIds);

    /**
     * 获取每小时分析数据
     *
     * @param date     日期
     * @param storeIds 门店ID列表
     * @return 每小时分析数据列表
     */
    List<HourlyAnalysisVO> getHourlyAnalysisData(@Param("date") LocalDate date, @Param("storeIds") List<Long> storeIds);

    /**
     * 获取门店营收排行数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param storeIds  门店ID列表
     * @return 门店排行数据列表
     */
    List<StoreRankingVO> getStoreRankingData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("storeIds") List<Long> storeIds
    );

    /**
     * 获取热门桌台数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param storeIds  门店ID列表
     * @param limit     返回记录数限制
     * @return 热门桌台数据列表
     */
    List<TopTableVO> getTopTablesData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("storeIds") List<Long> storeIds,
            @Param("limit") Integer limit
    );

    /**
     * 获取计费规则分析数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param storeIds  门店ID列表
     * @return 计费规则分析数据列表
     */
    List<PriceRuleAnalysisVO> getPriceRuleAnalysisData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("storeIds") List<Long> storeIds
    );
}
