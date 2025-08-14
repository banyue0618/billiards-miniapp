package org.dromara.billiards.controller.miniapp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.convert.TableConvert;
import org.dromara.billiards.domain.bo.TableQueryRequest;
import org.dromara.billiards.domain.entity.Table;
import org.dromara.billiards.domain.vo.TableVO;
import org.dromara.billiards.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序桌台控制器
 */
@Slf4j
@RestController("miniappTableController")
@RequestMapping("/api/miniapp/tables")
@RequiredArgsConstructor
@Tag(name = "桌台接口", description = "小程序桌台相关接口")
public class TableController {

    private final TableService tableService;
    private final TableConvert tableConvert = TableConvert.INSTANCE;

    /**
     * 获取门店桌台列表
     */
    @GetMapping("/list")
    @Operation(summary = "桌台列表", description = "获取门店下的桌台列表")
    public R<List<TableVO>> getTableList(@Parameter(description = "门店ID", required = true) @RequestParam String storeId) {
        List<org.dromara.billiards.domain.entity.Table> tableList = tableService.getTablesByStore(storeId);
        return ApiResult.success(tableConvert.toVoList(tableList));
    }

    /**
     * 获取桌台详情
     */
    @GetMapping("/detail")
    @Operation(summary = "桌台详情", description = "获取桌台详细信息")
    public R<TableVO> getTableDetail(@Parameter(description = "桌台ID", required = true) @RequestParam String tableId) {
        return ApiResult.success(tableService.getTableInfo(tableId));
    }

    /**
     * 根据二维码获取桌台,扫描二维码得到的是桌台的id
     */
    @GetMapping("/qrcode/{tableId}")
    @Operation(summary = "二维码查询", description = "根据二维码获取桌台信息")
    public R<TableVO> getTableByQrcode(@PathVariable String tableId) {
        return ApiResult.success(tableService.getTableInfo(tableId));
    }

    /**
     * 根据门店获取桌台列表，支持分页和条件查询
     * @param storeId 门店ID
     * @param queryRequest 查询条件
     * @return 分页桌台列表
     */
    @GetMapping("/{storeId}/tables")
    @Operation(summary = "桌台列表", description = "根据门店获取桌台列表，支持分页和条件查询")
    public R<IPage<TableVO>> tables(@PathVariable String storeId, TableQueryRequest queryRequest ) {
        queryRequest.setStoreId(storeId);
        IPage<Table> page = tableService.getTablePage(queryRequest);
        return ApiResult.success(tableConvert.toVoPage(page));
    }
}
