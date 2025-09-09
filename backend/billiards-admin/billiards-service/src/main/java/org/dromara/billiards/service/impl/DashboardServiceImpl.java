package org.dromara.billiards.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.mapper.DashboardMapper;
import org.dromara.billiards.domain.bo.DashboardQueryRequest;
import org.dromara.billiards.domain.vo.DashboardChartVO;
import org.dromara.billiards.domain.vo.DashboardOverviewVO;
import org.dromara.billiards.domain.vo.dashboard.*;
import org.dromara.billiards.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardOverviewVO getOverviewData(DashboardQueryRequest request) {
        log.info("开始获取仪表盘概览数据, 查询参数: {}", request);
        DashboardOverviewVO vo = new DashboardOverviewVO();
        try {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            if(CollectionUtil.isEmpty(request.getStoreIds())){

            }

            // 获取今日和昨日的营收数据
            vo.setTodayRevenue(dashboardMapper.calculateDailyRevenue(today, request.getStoreIds()));
            vo.setYesterdayRevenue(dashboardMapper.calculateDailyRevenue(yesterday, request.getStoreIds()));

            // 获取今日和昨日的订单数据
            vo.setTodayOrderCount(dashboardMapper.countDailyOrders(today, request.getStoreIds()));
            vo.setYesterdayOrderCount(dashboardMapper.countDailyOrders(yesterday, request.getStoreIds()));

            // 获取总桌台和使用中桌台数量
            vo.setTotalTables(dashboardMapper.countTotalTables(request.getStoreIds()));
            vo.setActiveTables(dashboardMapper.countActiveTables(request.getStoreIds()));

            // 获取今日和昨日的平均使用时长
            vo.setAvgUsageTime(dashboardMapper.calculateAverageUsageTime(today, request.getStoreIds()));
            vo.setYesterdayAvgUsageTime(dashboardMapper.calculateAverageUsageTime(yesterday, request.getStoreIds()));


            // 获取今日和昨日的桌台使用率
//            vo.setTodayTableUsageRate(dashboardMapper.calculateDailyTableUsageRate(today, request.getStoreIds()));
//            vo.setYesterdayTableUsageRate(dashboardMapper.calculateDailyTableUsageRate(yesterday, request.getStoreIds()));

            // 获取今日和昨日的新增会员数
//            vo.setTodayNewMembers(dashboardMapper.countDailyNewMembers(today, request.getStoreIds()));
//            vo.setYesterdayNewMembers(dashboardMapper.countDailyNewMembers(yesterday, request.getStoreIds()));

            log.info("仪表盘概览数据获取成功");
        } catch (Exception e) {
            log.error("获取仪表盘概览数据失败", e);
            throw new RuntimeException("获取仪表盘概览数据失败", e);
        }
        return vo;
    }

    @Override
    public DashboardChartVO getDashboardChartData(DashboardQueryRequest request) {
        log.info("开始获取仪表盘图表数据, 查询参数: {}", request);
        DashboardChartVO vo = new DashboardChartVO();
        try {
            // 初始化数据容器
            List<String> timeLabels = new ArrayList<>();
            List<BigDecimal> revenueData = new ArrayList<>();
            List<Integer> orderData = new ArrayList<>();
            List<BigDecimal> tableUsageData = new ArrayList<>();
            List<Integer> newMemberData = new ArrayList<>();

            // 根据查询类型获取时间维度数据
            LocalDate currentDate = request.getStartDate();
            while (!currentDate.isAfter(request.getEndDate())) {
                // 添加时间标签
                timeLabels.add(formatDate(currentDate, request.getChartType()));

                // 获取营收数据
                revenueData.add(dashboardMapper.calculateDailyRevenue(currentDate, request.getStoreIds()));

                // 获取订单数据
                orderData.add(dashboardMapper.countDailyOrders(currentDate, request.getStoreIds()));

                // 获取桌台使用率
                tableUsageData.add(dashboardMapper.calculateDailyTableUsageRate(currentDate, request.getStoreIds()));

                // 获取新增会员数
                newMemberData.add(dashboardMapper.countDailyNewMembers(currentDate, request.getStoreIds()));

                // 更新日期
                currentDate = updateDate(currentDate, request.getChartType());
            }

            // 设置数据
            vo.setTimeLabels(timeLabels);
            vo.setRevenueData(revenueData);
            vo.setOrderData(orderData);
            vo.setTableUsageData(tableUsageData);
            vo.setNewMemberData(newMemberData);

            log.info("仪表盘图表数据获取成功");
        } catch (Exception e) {
            log.error("获取仪表盘图表数据失败", e);
            throw new RuntimeException("获取仪表盘图表数据失败", e);
        }
        return vo;
    }

    @Override
    public HourlyAnalysisVO getHourlyAnalysisData(DashboardQueryRequest request) {
        log.info("开始获取每日时段分析数据, 查询参数: {}", request);
        try {
            // 获取每小时的订单数和营收数据
            List<HourlyAnalysisVO> hourlyData = dashboardMapper.getHourlyAnalysisData(request.getStartDate(), request.getStoreIds());

            // 初始化24小时的数据容器
            HourlyAnalysisVO result = new HourlyAnalysisVO();
            List<Integer> hours = new ArrayList<>();
            List<Integer> orders = new ArrayList<>();
            List<BigDecimal> revenueList = new ArrayList<>();

            // 填充24小时的数据，没有数据的小时填充0
            for (int i = 0; i < 24; i++) {
                hours.add(i);
                orders.add(0);
                revenueList.add(BigDecimal.ZERO);
            }

            // 填充实际数据
            for (HourlyAnalysisVO data : hourlyData) {
                int hour = data.getHour();
                orders.set(hour, data.getOrderCount());
                revenueList.set(hour, data.getHourlyRevenue());
            }

            result.setHours(hours);
            result.setOrders(orders);
            result.setRevenueList(revenueList);

            log.info("每日时段分析数据获取成功");
            return result;
        } catch (Exception e) {
            log.error("获取每日时段分析数据失败", e);
            throw new RuntimeException("获取每日时段分析数据失败", e);
        }
    }

    @Override
    public List<StoreRankingVO> getStoreRankingData(DashboardQueryRequest request) {
        log.info("开始获取门店营收排行数据, 查询参数: {}", request);
        try {
            List<StoreRankingVO> data = dashboardMapper.getStoreRankingData(
                request.getStartDate(),
                request.getEndDate() == null ? LocalDate.now() : request.getEndDate(),
                request.getStoreIds()
            );
            log.info("门店营收排行数据获取成功");
            return data;
        } catch (Exception e) {
            log.error("获取门店营收排行数据失败", e);
            throw new RuntimeException("获取门店营收排行数据失败", e);
        }
    }

    @Override
    public List<TopTableVO> getTopTablesData(DashboardQueryRequest request) {
        log.info("开始获取热门桌台数据, 查询参数: {}", request);
        try {
            // 默认返回前10个热门桌台
            List<TopTableVO> data = dashboardMapper.getTopTablesData(
                request.getStartDate(),
                request.getEndDate() == null ? LocalDate.now() : request.getEndDate(),
                request.getStoreIds(),
                10
            );
            log.info("热门桌台数据获取成功");
            return data;
        } catch (Exception e) {
            log.error("获取热门桌台数据失败", e);
            throw new RuntimeException("获取热门桌台数据失败", e);
        }
    }

    @Override
    public List<PriceRuleAnalysisVO> getPriceRuleAnalysisData(DashboardQueryRequest request) {
        log.info("开始获取计费规则分析数据, 查询参数: {}", request);
        try {
            List<PriceRuleAnalysisVO> data = dashboardMapper.getPriceRuleAnalysisData(
                request.getStartDate(),
                request.getEndDate() == null ? LocalDate.now() : request.getEndDate(),
                request.getStoreIds()
            );
            log.info("计费规则分析数据获取成功");
            return data;
        } catch (Exception e) {
            log.error("获取计费规则分析数据失败", e);
            throw new RuntimeException("获取计费规则分析数据失败", e);
        }
    }

    @Override
    public byte[] exportDashboardData(DashboardQueryRequest request) {
        log.info("开始导出仪表盘数据, 查询参数: {}", request);
        try {
            // TODO: 实现导出数据的逻辑
            // 1. 获取各项统计数据
            // 2. 创建Excel文件
            // 3. 写入数据
            // 4. 返回文件字节数组
            return new byte[0];
        } catch (Exception e) {
            log.error("导出仪表盘数据失败", e);
            throw new RuntimeException("导出仪表盘数据失败", e);
        }
    }

    @Override
    public DashboardOverviewVO getOverview() {
        log.info("开始获取概览数据");
        return getOverviewData(new DashboardQueryRequest());
    }

    @Override
    public RevenueAnalysisVO getRevenueAnalysis() {
        log.info("开始获取营收分析数据");
        RevenueAnalysisVO vo = new RevenueAnalysisVO();
        try {
            LocalDate today = LocalDate.now();

            // 获取当期和同期营收
            vo.setCurrentRevenue(dashboardMapper.calculateDailyRevenue(today, null));
            vo.setPreviousRevenue(dashboardMapper.calculateDailyRevenue(today.minusYears(1), null));

            // 计算同比和环比增长率
            vo.setYearOverYearGrowth(calculateGrowthRate(vo.getCurrentRevenue(), vo.getPreviousRevenue()));
            vo.setMonthOverMonthGrowth(calculateGrowthRate(
                dashboardMapper.calculateDailyRevenue(today, null),
                dashboardMapper.calculateDailyRevenue(today.minusMonths(1), null)
            ));

            // TODO: 实现预测逻辑
            vo.setForecastRevenue(BigDecimal.ZERO);
            vo.setForecastGrowthRate(BigDecimal.ZERO);

            // 生成评价和建议
            vo.setTrendEvaluation("营收呈现稳定增长趋势");
            vo.setImprovements("建议关注高峰时段的人力资源配置，优化计费规则");

            log.info("营收分析数据获取成功");
        } catch (Exception e) {
            log.error("获取营收分析数据失败", e);
            throw new RuntimeException("获取营收分析数据失败", e);
        }
        return vo;
    }

    @Override
    public RevenueTrendVO getRevenueTrend(DashboardQueryRequest request) {
        log.info("开始获取营收趋势数据, 查询参数: {}", request);
        RevenueTrendVO vo = new RevenueTrendVO();
        try {
            List<String> xAxis = new ArrayList<>();
            List<BigDecimal> revenueSeries = new ArrayList<>();
            List<Integer> orderSeries = new ArrayList<>();

            LocalDate start = request.getStartDate() == null ? LocalDate.now() : request.getStartDate();
            LocalDate end = request.getEndDate() == null ? start : request.getEndDate();
            String type = request.getChartType();

            LocalDate cur = start;
            while (!cur.isAfter(end)) {
                xAxis.add(formatDate(cur, type));
                revenueSeries.add(dashboardMapper.calculateDailyRevenue(cur, request.getStoreIds()));
                orderSeries.add(dashboardMapper.countDailyOrders(cur, request.getStoreIds()));
                cur = updateDate(cur, type);
            }

            vo.setXAxis(xAxis);
            vo.setRevenueSeries(revenueSeries);
            vo.setOrderSeries(orderSeries);

            log.info("营收趋势数据获取成功");
        } catch (Exception e) {
            log.error("获取营收趋势数据失败", e);
            throw new RuntimeException("获取营收趋势数据失败", e);
        }
        return vo;
    }

    @Override
    public StoreAnalysisVO getStoreAnalysis() {
        log.info("开始获取门店分析数据");
        StoreAnalysisVO vo = new StoreAnalysisVO();
        try {
            // TODO: 实现门店分析逻辑
            // 这里暂时返回模拟数据
            vo.setStoreId("1");
            vo.setStoreName("示例门店");
            vo.setCurrentRevenue(BigDecimal.valueOf(10000));
            vo.setRevenueRank(1);
            vo.setYearOverYearGrowth(BigDecimal.valueOf(15.5));
            vo.setMonthOverMonthGrowth(BigDecimal.valueOf(5.2));
            vo.setCustomerFlow(100);
            vo.setCustomerFlowRank(1);
            vo.setAverageConsumption(BigDecimal.valueOf(100));
            vo.setOperationScore(90);
            vo.setImprovements("建议优化高峰时段的服务流程，提升客户体验");

            log.info("门店分析数据获取成功");
        } catch (Exception e) {
            log.error("获取门店分析数据失败", e);
            throw new RuntimeException("获取门店分析数据失败", e);
        }
        return vo;
    }

    @Override
    public TimeAnalysisVO getTimeAnalysis() {
        log.info("开始获取时段分析数据");
        TimeAnalysisVO vo = new TimeAnalysisVO();
        try {
            // TODO: 实现时段分析逻辑
            // 这里暂时返回模拟数据
            vo.setTimeSlot("14:00-18:00");
            vo.setCustomerFlow(50);
            vo.setRevenue(BigDecimal.valueOf(5000));
            vo.setTableUsageRate(BigDecimal.valueOf(85.5));
            vo.setAverageStayDuration(120);
            vo.setIsPeakTime(1);
            vo.setYearOverYearChange(BigDecimal.valueOf(12.5));
            vo.setMonthOverMonthChange(BigDecimal.valueOf(3.8));
            vo.setTimeSlotAnalysis("下午时段客流量较大，台球桌使用率高");
            vo.setOptimizationSuggestions("建议增加工作人员配置，优化服务流程");

            log.info("时段分析数据获取成功");
        } catch (Exception e) {
            log.error("获取时段分析数据失败", e);
            throw new RuntimeException("获取时段分析数据失败", e);
        }
        return vo;
    }

    /**
     * 计算增长率
     */
    private BigDecimal calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.subtract(previous)
                .divide(previous, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * 格式化日期
     */
    private String formatDate(LocalDate date, String chartType) {
        DateTimeFormatter formatter;
        switch (chartType) {
            case "day":
            case "week":
                formatter = DateTimeFormatter.ofPattern("MM-dd");
                break;
            case "month":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        return date.format(formatter);
    }

    /**
     * 更新日期
     */
    private LocalDate updateDate(LocalDate date, String chartType) {
        switch (chartType) {
            case "day":
                return date.plusDays(1);
            case "week":
                return date.plusWeeks(1);
            case "month":
                return date.plusMonths(1);
            default:
                return date.plusDays(1);
        }
    }
}
