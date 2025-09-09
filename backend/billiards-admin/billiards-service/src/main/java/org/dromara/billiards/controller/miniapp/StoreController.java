package org.dromara.billiards.controller.miniapp;

import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.convert.StoreConvert;
import org.dromara.billiards.domain.bo.NearbyStoreRequest;
import org.dromara.billiards.domain.bo.StoreQueryRequest;
import org.dromara.billiards.domain.entity.BlsStore;
import org.dromara.billiards.domain.vo.NearbyStoreVO;
import org.dromara.billiards.domain.vo.StoreVO;
import org.dromara.billiards.service.StoreService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序门店控制器
 */
@Slf4j
@RestController("miniappStoreController")
@RequestMapping("/api/miniapp/stores")
@RequiredArgsConstructor
@Tag(name = "门店接口", description = "小程序门店相关接口")
public class StoreController {

    private final StoreService storeService;
    private final StoreConvert storeConvert = StoreConvert.INSTANCE;

    /**
     * 获取门店列表
     */
    @GetMapping("/list")
    @Operation(summary = "门店列表", description = "获取所有可用门店列表")
    public R<List<StoreVO>> getStoreList() {
        List<BlsStore> blsStoreList = storeService.listAvailableStores();
        return ApiResult.success(storeConvert.toVoList(blsStoreList));
    }

    /**
     * 获取附近门店列表
     */
    @PostMapping("/nearby")
    @Operation(summary = "附近门店", description = "根据用户当前位置获取附近门店")
    public R<List<NearbyStoreVO>> getNearbyStores(@Validated @RequestBody NearbyStoreRequest request) {
        List<NearbyStoreVO> nearbyStores = storeService.getNearbyStores(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius());
        return ApiResult.success(nearbyStores);
    }

    /**
     * 分页查询门店列表
     */
    @GetMapping("/page")
    @Operation(summary = "门店分页列表", description = "分页查询门店列表")
    public R<IPage<StoreVO>> getStorePage(@Validated StoreQueryRequest request) {
        IPage<BlsStore> storePage = storeService.pageAvailableStores(request);
        return ApiResult.success(storeConvert.toVoPage(storePage));
    }

    /**
     * 获取门店详情
     */
    @PostMapping("/detail")
    @Operation(summary = "门店详情", description = "获取门店详细信息")
    public R<NearbyStoreVO> getStoreDetail(@Validated @RequestBody NearbyStoreRequest request) {
        NearbyStoreVO store = storeService.getStoreInfo(request.getStoreId(), request.getLatitude(), request.getLongitude());
        return ApiResult.success(store);
    }
}
