package org.dromara.billiards.controller.admin;

import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.convert.TableConvert;
import org.dromara.billiards.domain.bo.TableDto;
import org.dromara.billiards.domain.bo.TableQueryRequest;
import org.dromara.billiards.domain.bo.BatchTableRequest;
import org.dromara.billiards.domain.entity.Table;
import org.dromara.billiards.domain.vo.TableVO;
import org.dromara.billiards.service.TableService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*; // Spring MVC RequestBody

import java.util.List;


/**
 * 桌台管理控制器(管理端)
 */
@Slf4j
@RestController("adminTableController") // 规范化命名
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
@Tag(name = "桌台管理", description = "桌台管理相关接口")
public class TableController {

    private final TableService tableService;
    private final TableConvert tableConvert; // 注入转换器

    /**
     * 桌台分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "桌台列表", description = "分页获取桌台列表")
    public R<IPage<TableVO>> list(TableQueryRequest tableQueryRequest) {
        IPage<Table> tablePage = tableService.getTablePage(tableQueryRequest);
        return ApiResult.success(tableConvert.toVoPage(tablePage));
    }

    /**
     * 桌台详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "桌台详情", description = "获取桌台详细信息")
    public R<TableVO> detail(@PathVariable String id) {
        return ApiResult.success(tableConvert.toVo(tableService.getById(id)));
    }

    /**
     * 新增桌台
     */
    @PostMapping
    @Operation(summary = "新增桌台", description = "添加单个桌台")
    public R<TableVO> add(@Validated @RequestBody TableDto tableDto) {
        Table createdTable = tableService.createTable(tableDto);
        return ApiResult.success(tableConvert.toVo(createdTable));
    }

    /**
     * 批量新增桌台
     */
    @PostMapping("/batch/add")
    @Operation(summary = "批量新增桌台", description = "批量添加多个桌台")
    public R<Boolean> batchAdd(@Validated @RequestBody BatchTableRequest request) {
        tableService.batchCreateTables(request);
        return ApiResult.success(true);
    }

    /**
     * 修改桌台
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改桌台", description = "更新桌台信息")
    public R<Boolean> update(@PathVariable String id, @Validated @RequestBody TableDto requestDto) {
        Table tableToUpdate = tableService.getById(id); // 先获取托管实体
        tableConvert.updateEntityFromDto(requestDto, tableToUpdate);
        tableService.updateTable(tableToUpdate); // 假设 updateTable 接收实体
        return ApiResult.success(true); // 按照规范，操作成功返回 true
    }

    /**
     * 删除桌台
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除桌台", description = "删除指定桌台")
    @Parameter(name = "id", description = "桌台ID", required = true)
    public R<Boolean> delete(@PathVariable String id) {
        tableService.deleteTable(id);
        return ApiResult.success(true);
    }

    /**
     * 批量删除桌台
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除桌台", description = "批量删除指定的多个桌台")
    public R<Boolean> batchDelete(@RequestBody List<String> ids) {
        tableService.batchDeleteTables(ids);
        return ApiResult.success(true);
    }

    /**
     * 更新桌台状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新桌台状态", description = "修改桌台状态：0-空闲 1-使用中 2-维修中 3-锁定")
    @Parameters({
        @Parameter(name = "id", description = "桌台ID", required = true),
        @Parameter(name = "status", description = "状态值 (0-3)", required = true, schema = @Schema(type = "integer", allowableValues = {"0", "1", "2", "3"}))
    })
    public R<Boolean> updateStatus(@PathVariable String id, @RequestParam Integer status) {
        if (status < 0 || status > 3) {
             throw new IllegalArgumentException("状态值不合法，必须在0-3之间"); // 示例抛出异常
        }
        tableService.updateStatus(id, status);
        return ApiResult.success(true);
    }

    /**
     * 生成桌台二维码
     */
    @PostMapping("/{id}/qrcode")
    @Operation(summary = "生成二维码", description = "为桌台生成二维码")
    @Parameter(name = "id", description = "桌台ID", required = true)
    public R<TableVO> generateQrcode(@PathVariable String id) {
        Table table = tableService.generateQrcode(id);
        return ApiResult.success(tableConvert.toVo(table));
    }

    /**
     * 根据门店获取桌台列表，支持分页和条件查询
     * @param storeId 门店ID
     * @param queryRequest 查询条件
     * @return 分页桌台列表
     */
    @GetMapping("/{storeId}/tables")
    @Operation(summary = "桌台列表", description = "根据门店获取桌台列表，支持分页和条件查询")
    public R<IPage<TableVO>> tables( @PathVariable String storeId, TableQueryRequest queryRequest ) {
        queryRequest.setStoreId(storeId);
        IPage<Table> page = tableService.getTablePage(queryRequest);
        return ApiResult.success(tableConvert.toVoPage(page));
    }
}
