package org.dromara.billiards.controller.admin;

import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.domain.bo.StatusRequest;
import org.dromara.billiards.domain.bo.StoreQueryRequest;
import org.dromara.billiards.domain.bo.StoreDto;
import org.dromara.billiards.domain.entity.Store;
import org.dromara.billiards.domain.vo.StoreVO;
import org.dromara.billiards.convert.StoreConvert;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.billiards.service.StoreService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.file.annotation.ProcessFilePersistence;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 门店管理控制器
 */
@Slf4j
@RestController("adminStoreController")
@RequestMapping("/api/admin/stores")
@RequiredArgsConstructor
@Tag(name = "门店接口", description = "管理端门店相关接口")
public class StoreController {

    private final StoreService storeService;
    private final StoreConvert storeConvert = StoreConvert.INSTANCE;

    /**
     * 门店列表
     */
    @GetMapping("/list")
    @Operation(summary = "门店列表", description = "分页查询门店列表")
    public R<IPage<StoreVO>> list(@Validated StoreQueryRequest request) {
        IPage<Store> storePage = storeService.pageStores(request);

        // 转换为VO并填充桌台数量
        IPage<StoreVO> voPage = storeConvert.toVoPage(storePage);
        voPage.getRecords().forEach(storeVO -> {
            storeVO.setTableCount(storeService.getTableCount(storeVO.getId()));
        });

        return ApiResult.success(voPage);
    }

    /**
     * 获取门店详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "门店详情", description = "获取门店详细信息")
    public R<StoreVO> detail(@PathVariable String id) {
        Store store = storeService.getStoreInfo(id);
        StoreVO storeVO = storeConvert.toVo(store);

        // 填充桌台数量
        storeVO.setTableCount(storeService.getTableCount(id));

        return ApiResult.success(storeVO);
    }

    /**
     * 新增门店
     */
    @PostMapping
    @Operation(summary = "新增门店", description = "添加新的门店信息")
    @ProcessFilePersistence
    public R<StoreVO> add(@Validated(AddGroup.class) @RequestBody StoreDto storeDto) {
        return ApiResult.success(storeConvert.toVo(storeService.createStore(storeDto)));
    }

    /**
     * 修改门店
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改门店", description = "修改门店信息")
    @ProcessFilePersistence
    public R<StoreVO> update(@PathVariable String id, @Validated(EditGroup.class) @RequestBody StoreDto storeDto) {
        Store updatedStore = storeService.updateStore(id, storeDto);
        return ApiResult.success(storeConvert.toVo(updatedStore));
    }

    /**
     * 删除门店
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除门店", description = "删除指定门店")
    public R<Boolean> delete(@PathVariable String id) {
        storeService.deleteStore(id);
        return ApiResult.success(true);
    }

    /**
     * 更新门店状态
     */
    @PutMapping("/status")
    @Operation(summary = "更新门店状态", description = "修改门店营业状态：0-正常 1-休息 2-停业，并可填写描述")
    public R<Boolean> updateStatus(@Validated @RequestBody StatusRequest request) {
        if (request.getStatus() < 0 || request.getStatus() > 2) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "状态值不合法，必须为0、1或2");
        }
        storeService.updateStoreStatusAndAnnouncement(request);
        return ApiResult.success(true);
    }
}
