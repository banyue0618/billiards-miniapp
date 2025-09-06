package org.dromara.billiards.controller.admin;

import org.dromara.billiards.common.result.Result;
import org.dromara.billiards.domain.bo.DashboardQueryRequest;
import org.dromara.billiards.domain.vo.DashboardChartVO;
import org.dromara.billiards.domain.vo.DashboardOverviewVO;
import org.dromara.billiards.domain.vo.dashboard.HourlyAnalysisVO;
import org.dromara.billiards.domain.vo.dashboard.PriceRuleAnalysisVO;
import org.dromara.billiards.domain.vo.dashboard.StoreRankingVO;
import org.dromara.billiards.domain.vo.dashboard.TopTableVO;
import org.dromara.billiards.domain.vo.dashboard.*;
import org.dromara.billiards.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台仪表盘控制器
 */
@Slf4j
@RestController("adminDashboardController")
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘接口", description = "管理端仪表盘相关接口")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘概览数据
     */
    @GetMapping("/overview")
    @Operation(summary = "仪表盘概览", description = "获取仪表盘概览数据")
    public Result<DashboardOverviewVO> getOverviewData(@Validated DashboardQueryRequest request) {
        DashboardOverviewVO data = dashboardService.getOverviewData(request);
        return Result.success(data);
    }

    /**
     * 获取仪表盘图表数据
     */
    @GetMapping("/chart")
    @Operation(summary = "仪表盘图表", description = "获取仪表盘图表数据")
    public Result<DashboardChartVO> getDashboardChartData(@Validated DashboardQueryRequest request) {
        DashboardChartVO data = dashboardService.getDashboardChartData(request);
        return Result.success(data);
    }

    /**
     * 获取每日时段分析数据
     */
    @GetMapping("/hourly-analysis")
    @Operation(summary = "每日时段分析", description = "获取每日时段分析数据")
    public Result<HourlyAnalysisVO> getHourlyAnalysisData(@Validated DashboardQueryRequest request) {
        HourlyAnalysisVO data = dashboardService.getHourlyAnalysisData(request);
        return Result.success(data);
    }

    /**
     * 获取门店营收排行数据
     */
    @GetMapping("/store-ranking")
    @Operation(summary = "门店营收排行", description = "获取门店营收排行数据")
    public Result<List<StoreRankingVO>> getStoreRankingData(@Validated DashboardQueryRequest request) {
        List<StoreRankingVO> data = dashboardService.getStoreRankingData(request);
        return Result.success(data);
    }

    /**
     * 获取热门桌台数据
     */
    @GetMapping("/top-tables")
    @Operation(summary = "热门桌台", description = "获取热门桌台数据")
    public Result<List<TopTableVO>> getTopTablesData(@Validated DashboardQueryRequest request) {
        List<TopTableVO> data = dashboardService.getTopTablesData(request);
        return Result.success(data);
    }

    /**
     * 获取计费规则分析数据
     */
    @GetMapping("/price-rule-analysis")
    @Operation(summary = "计费规则分析", description = "获取计费规则分析数据")
    public Result<List<PriceRuleAnalysisVO>> getPriceRuleAnalysisData(@Validated DashboardQueryRequest request) {
        List<PriceRuleAnalysisVO> data = dashboardService.getPriceRuleAnalysisData(request);
        return Result.success(data);
    }

    /**
     * 导出仪表盘数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出数据", description = "导出仪表盘数据")
    public Result<byte[]> exportDashboardData(@Validated DashboardQueryRequest request) {
        byte[] data = dashboardService.exportDashboardData(request);
        return Result.success(data);
    }

    @Operation(summary = "获取营收趋势（柱：订单 + 线：营收）")
    @GetMapping("/revenue-chart")
    public Result<RevenueTrendVO> getRevenueTrend(@Validated DashboardQueryRequest request) {
        return Result.success(dashboardService.getRevenueTrend(request));
    }

    @Operation(summary = "获取门店分析数据")
    @GetMapping("/store")
    public StoreAnalysisVO getStoreAnalysis() {
        return dashboardService.getStoreAnalysis();
    }

    @Operation(summary = "获取时段分析数据")
    @GetMapping("/time")
    public TimeAnalysisVO getTimeAnalysis() {
        return dashboardService.getTimeAnalysis();
    }
}
