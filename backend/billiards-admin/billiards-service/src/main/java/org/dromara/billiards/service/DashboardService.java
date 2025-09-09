package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.DashboardQueryRequest;
import org.dromara.billiards.domain.vo.DashboardChartVO;
import org.dromara.billiards.domain.vo.DashboardOverviewVO;
import org.dromara.billiards.domain.vo.dashboard.*;

import java.util.List;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取仪表盘概览数据
     *
     * @param request 查询参数
     * @return 概览数据
     */
    DashboardOverviewVO getOverviewData(DashboardQueryRequest request);

    /**
     * 获取仪表盘图表数据
     *
     * @param request 查询参数
     * @return 图表数据
     */
    DashboardChartVO getDashboardChartData(DashboardQueryRequest request);

    /**
     * 获取每日时段分析数据
     *
     * @param request 查询参数
     * @return 分析数据
     */
    HourlyAnalysisVO getHourlyAnalysisData(DashboardQueryRequest request);

    /**
     * 获取门店营收排行数据
     *
     * @param request 查询参数
     * @return 排行数据列表
     */
    List<StoreRankingVO> getStoreRankingData(DashboardQueryRequest request);

    /**
     * 获取热门桌台数据
     *
     * @param request 查询参数
     * @return 热门桌台列表
     */
    List<TopTableVO> getTopTablesData(DashboardQueryRequest request);

    /**
     * 获取计费规则分析数据
     *
     * @param request 查询参数
     * @return 分析数据列表
     */
    List<PriceRuleAnalysisVO> getPriceRuleAnalysisData(DashboardQueryRequest request);

    /**
     * 导出仪表盘数据
     *
     * @param request 查询参数
     * @return Excel文件的字节数组
     */
    byte[] exportDashboardData(DashboardQueryRequest request);

    /**
     * 获取概览数据
     */
    DashboardOverviewVO getOverview();

    /**
     * 获取营收分析数据
     */
    RevenueAnalysisVO getRevenueAnalysis();

    /**
     * 获取营收趋势（柱：订单数 + 线：总收入）
     */
    RevenueTrendVO getRevenueTrend(DashboardQueryRequest request);

    /**
     * 获取门店分析数据
     */
    StoreAnalysisVO getStoreAnalysis();

    /**
     * 获取时段分析数据
     */
    TimeAnalysisVO getTimeAnalysis();
}
