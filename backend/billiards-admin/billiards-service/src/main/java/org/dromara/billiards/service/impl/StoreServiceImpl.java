package org.dromara.billiards.service.impl;

import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsPriceRule;
import org.dromara.billiards.domain.entity.BlsStore;
import org.dromara.billiards.domain.entity.BlsTable;
import org.dromara.billiards.mapper.StoreMapper;
import org.dromara.billiards.mapper.TableMapper;
import org.dromara.billiards.domain.vo.NearbyStoreVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.service.PriceRuleService;
import org.dromara.billiards.service.StoreService;
import org.dromara.billiards.domain.bo.StoreDto;
import org.dromara.billiards.convert.StoreConvert;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.common.core.enums.FormatsType;
import org.dromara.common.core.utils.DistanceCalculateOfVincentyUtil;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.common.file.service.factory.ResourceServiceFactory;
import org.dromara.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.billiards.domain.bo.StoreQueryRequest;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.domain.bo.StatusRequest;
import org.dromara.system.domain.vo.SysDictDataVo;
import org.dromara.system.service.ISysDictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.dromara.common.tenant.helper.MerchantHolder;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.billiards.security.MerchantWriteGuard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 门店服务实现类
 */
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class StoreServiceImpl extends ServiceImpl<StoreMapper, BlsStore> implements StoreService {

    @Autowired
    private TableMapper tableMapper;

    private final StoreConvert storeConvert = StoreConvert.INSTANCE;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ResourceServiceFactory resourceServiceFactory;

    @Autowired
    private PriceRuleService priceRuleService;

    private static final String DICT_TYPE_STORE_BUSINESS_STATUS = "store_business_status";
    private static final String DICT_TYPE_TABLE_STATUS = "table_status";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlsStore createStore(StoreDto storeDto) {
        BlsStore blsStoreToSave = storeConvert.toEntity(storeDto);
        this.save(blsStoreToSave);
        return blsStoreToSave;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlsStore updateStore(String id, StoreDto storeDto) {

        BlsStore existingBlsStore = this.getById(id);
        if (existingBlsStore == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "门店不存在，无法更新");
        }
        // 商户越权校验（仅当会话绑定了商户时收敛）
        MerchantWriteGuard.assertWritable(existingBlsStore.getMerchantId());

        storeConvert.updateStoreFromDto(storeDto, existingBlsStore);
        boolean success = this.updateById(existingBlsStore);
        if (!success) {
            throw BilliardsException.of(ResultCode.ERROR, "更新门店信息失败");
        }
        return existingBlsStore;
    }

    @Override
    public BlsStore getStoreInfo(String id) {
        BlsStore blsStore = this.getById(id);
        if (blsStore == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "门店不存在");
        }
        return blsStore;
    }

    @Override
    public NearbyStoreVO getStoreInfo(String id, BigDecimal latitude, BigDecimal longitude) {
        BlsStore blsStore = getStoreInfo(id);
        // 计算距离
        double distanceMeters = DistanceCalculateOfVincentyUtil.getDistance(
            latitude.doubleValue(), longitude.doubleValue(),
            blsStore.getLatitude().doubleValue(), blsStore.getLongitude().doubleValue()
        );
        // 转换为公里进行比较
        double distanceKm = distanceMeters / 1000.0;
        // 使用提取的通用方法构建VO
        NearbyStoreVO nearbyStoreVO = buildNearbyStoreVO(blsStore, distanceKm, null, null, null);
        // 标准计费
        List<BlsPriceRule> blsPriceRuleList = priceRuleService.listPriceRulesByMerchantId(blsStore.getMerchantId(), 1);
        List<NearbyStoreVO.PriceVO> priceList = blsPriceRuleList.stream().map(priceRule -> {
            NearbyStoreVO.PriceVO priceVO = new NearbyStoreVO.PriceVO();
            priceVO.setType(priceRule.getName());
            priceVO.setPrice(priceRule.getPriceUnit().multiply(BilliardsConstants.MINUTES_PER_HOUR));
            priceVO.setMemberDiscount(priceRule.getMemberDiscount());
            return priceVO;
        }).toList();
        nearbyStoreVO.setPriceList(priceList);
        return nearbyStoreVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStoreStatusAndAnnouncement(StatusRequest request) {
        if (request == null || request.getId() == null || request.getStatus() == null) {
            // Or throw IllegalArgumentException, or handle as per project's validation strategy
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "ID和状态不能为空");
        }

        BlsStore blsStoreToUpdate = this.getById(request.getId());
        if (blsStoreToUpdate == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "门店不存在，无法更新状态");
        }

        blsStoreToUpdate.setStatus(request.getStatus());
        // Announcement can be null/empty, allow clearing it if client sends null/empty string
        blsStoreToUpdate.setAnnouncement(request.getAnnouncement());

        boolean success = this.updateById(blsStoreToUpdate);
        if (!success) {
            throw BilliardsException.of(ResultCode.ERROR, "更新门店状态和公告失败");
        }
        return true;
    }

    @Override
    public Long getTableCount(String storeId) {
        if (storeId == null) {
            return 0L;
        }

        LambdaQueryWrapper<BlsTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlsTable::getStoreId, storeId);

        return tableMapper.selectCount(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(String[] ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }

        // 先删除门店下的所有桌台
        Arrays.stream(ids).forEach(id -> {
            // 商户越权校验（仅当会话绑定了商户时收敛）
            BlsStore s = this.getById(id);
            if (s != null) {
                MerchantWriteGuard.assertWritable(s.getMerchantId());
            }
            LambdaQueryWrapper<BlsTable> tableQueryWrapper = new LambdaQueryWrapper<>();
            tableQueryWrapper.eq(BlsTable::getStoreId, id);
            tableMapper.delete(tableQueryWrapper);
        });

        // 再批量删除门店
        return this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 获取附近门店列表
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 搜索半径(公里)
     * @return 门店列表（包含距离信息）
     */
    @Override
    public List<NearbyStoreVO> getNearbyStores(BigDecimal latitude, BigDecimal longitude, Integer radius) {
        if (latitude == null || longitude == null || radius == null || radius <= 0) {
            return Collections.emptyList();
        }

        double lat = latitude.doubleValue();
        double lon = longitude.doubleValue();

        // 基于近似比例，先用矩形框进行数据库预筛选，减少候选门店数量
        double[] bbox = DistanceCalculateOfVincentyUtil.boundingBox(lat, lon, radius.doubleValue());
        BigDecimal minLat = BigDecimal.valueOf(bbox[0]);
        BigDecimal maxLat = BigDecimal.valueOf(bbox[1]);
        BigDecimal minLon = BigDecimal.valueOf(bbox[2]);
        BigDecimal maxLon = BigDecimal.valueOf(bbox[3]);

        LambdaQueryWrapper<BlsStore> storeQuery = new LambdaQueryWrapper<>();
        storeQuery.isNotNull(BlsStore::getLatitude)
            .isNotNull(BlsStore::getLongitude)
            .between(BlsStore::getLatitude, minLat, maxLat)
            .between(BlsStore::getLongitude, minLon, maxLon);
        List<BlsStore> candidateBlsStores = this.list(storeQuery);
        if (candidateBlsStores.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取字典数据
        Map<String, List<SysDictDataVo>> dictDataMap = dictTypeService.selectDictDataByType(DICT_TYPE_STORE_BUSINESS_STATUS, DICT_TYPE_TABLE_STATUS);

        // 预聚合桌台统计，避免 N+1 查询
        List<String> storeIds = candidateBlsStores.stream().map(BlsStore::getId).filter(Objects::nonNull).toList();
        Map<String, Integer> storeIdToTotalTables = new HashMap<>();
        Map<String, Integer> storeIdToAvailableTables = new HashMap<>();
        if (!storeIds.isEmpty()) {
            QueryWrapper<BlsTable> tableAggQw = new QueryWrapper<>();
            tableAggQw.select("store_id as storeId",
                "COUNT(*) as total",
                "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as available")
                .in("store_id", storeIds)
                .groupBy("store_id");
            List<Map<String, Object>> rows = tableMapper.selectMaps(tableAggQw);
            for (Map<String, Object> row : rows) {
                Object sid = row.get("storeId");
                if (sid == null) continue;
                String storeId = String.valueOf(sid);
                Integer total = toInteger(row.get("total"));
                Integer available = toInteger(row.get("available"));
                storeIdToTotalTables.put(storeId, total != null ? total : 0);
                storeIdToAvailableTables.put(storeId, available != null ? available : 0);
            }
        }

        // 以商户维度预取标准计费最低单价
        Map<String, BigDecimal> merchantMinPriceUnitPerMinute = new HashMap<>();
        Set<String> merchantIds = candidateBlsStores.stream()
            .map(BlsStore::getMerchantId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        for (String merchantId : merchantIds) {
            List<BlsPriceRule> blsPriceRules = priceRuleService.listPriceRulesByMerchantId(merchantId, 1);
            if (blsPriceRules == null || blsPriceRules.isEmpty()) continue;
//            BigDecimal minUnit = priceRules.stream()
//                .map(PriceRule::getPriceUnit)
//                .filter(Objects::nonNull)
//                .min(Comparator.naturalOrder())
//                .orElse(null);
            BigDecimal minUnit = blsPriceRules.stream()
                .filter(r -> r.getPriceUnit() != null)
                .min(Comparator.comparing(BlsPriceRule::getPriceUnit))
                .map(r -> {
                    BigDecimal discount = r.getMemberDiscount() != null ? r.getMemberDiscount() : BigDecimal.ONE;
                    return r.getPriceUnit().multiply(discount);
                })
                .orElse(null);
            if (minUnit != null) {
                merchantMinPriceUnitPerMinute.put(merchantId, minUnit);
            }
        }

        // 计算距离、过滤、装配 VO
        List<NearbyStoreVO> result = candidateBlsStores.stream()
            .map(store -> {
                double meters = DistanceCalculateOfVincentyUtil.getDistanceHaversineMeters(
                    lat, lon,
                    store.getLatitude().doubleValue(), store.getLongitude().doubleValue()
                );
                double distanceKm = meters / 1000.0;
                if (distanceKm > radius) return null;

                Integer total = storeIdToTotalTables.getOrDefault(store.getId(), 0);
                Integer available = storeIdToAvailableTables.getOrDefault(store.getId(), 0);

                NearbyStoreVO vo = buildNearbyStoreVO(store, distanceKm, dictDataMap, total, available);

                BigDecimal minPriceUnit = merchantMinPriceUnitPerMinute.get(store.getMerchantId());
                if (minPriceUnit != null) {
                    vo.setMinPrice(minPriceUnit.multiply(BilliardsConstants.MINUTES_PER_HOUR).intValue());
                }
                return vo;
            })
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(NearbyStoreVO::getDistance))
            .toList();

        return result;
    }

    /**
     * 构建NearbyStoreVO对象
     * @param blsStore 门店实体
     * @param distanceKm 距离(公里)
     * @param dictDataMap 字典数据(可为null，会自动获取)
     * @return NearbyStoreVO对象
     */
    private NearbyStoreVO buildNearbyStoreVO(BlsStore blsStore, double distanceKm, Map<String, List<SysDictDataVo>> dictDataMap, Integer totalTables, Integer availableTables) {
        NearbyStoreVO vo = new NearbyStoreVO();

        // 基本信息
        vo.setId(blsStore.getId());
        vo.setName(blsStore.getName());
        String coverImageUrl = null;
        if (StringUtils.isNotBlank(blsStore.getCoverImage())) {
            coverImageUrl = resourceServiceFactory.getService().getResourceUrl(blsStore.getCoverImage(), ResourceType.STORE_COVER);
        }
        vo.setCoverImage(coverImageUrl);
        vo.setImages(coverImageUrl != null ? Collections.singletonList(coverImageUrl) : Collections.emptyList());

        // 状态信息
        vo.setStatus(String.valueOf(blsStore.getStatus()));
        // 根据是否提供字典数据选择不同的获取方式
        if (dictDataMap != null) {
            // 从提供的字典数据中获取
            String storeStatusLabel = Optional.ofNullable(dictDataMap.get(DICT_TYPE_STORE_BUSINESS_STATUS))
                .map(dictDataVoList -> dictDataVoList.stream()
                    .filter(d -> d.getDictValue().equals(String.valueOf(blsStore.getStatus())))
                    .map(SysDictDataVo::getDictLabel)
                    .findFirst()
                    .orElse("未知状态"))
                .orElse("未知状态");
            vo.setStatusText(storeStatusLabel);
        } else {
            // 直接从服务获取
            String dictLabel = dictDataService.selectDictLabel(DICT_TYPE_STORE_BUSINESS_STATUS, vo.getStatus());
            vo.setStatusText(dictLabel);
        }

        vo.setBusinessHours(blsStore.getBusinessHours());

        // 地址信息
        NearbyStoreVO.AddressVO addressVO = new NearbyStoreVO.AddressVO();
        addressVO.setProvince(blsStore.getProvince());
        addressVO.setCity(blsStore.getCity());
        addressVO.setDistrict(blsStore.getDistrict());
        addressVO.setStreet(blsStore.getAddress()); // Store.address是街道
        addressVO.setLatitude(blsStore.getLatitude() != null ? blsStore.getLatitude().doubleValue() : null);
        addressVO.setLongitude(blsStore.getLongitude() != null ? blsStore.getLongitude().doubleValue() : null);
        vo.setAddress(addressVO);

        // 桌台信息：优先使用预聚合数据，缺失时回退到查询
        NearbyStoreVO.TablesInfoVO tablesInfoVO = new NearbyStoreVO.TablesInfoVO();
        if (totalTables != null && availableTables != null) {
            tablesInfoVO.setTotal(totalTables);
            tablesInfoVO.setAvailable(availableTables);
        } else {
            List<BlsTable> blsTables = tableMapper.selectList(new LambdaQueryWrapper<BlsTable>().eq(BlsTable::getStoreId, blsStore.getId()));
            Long availableCnt = blsTables.stream().filter(table -> table.getStatus() != null && table.getStatus() == 0).count();
            tablesInfoVO.setTotal(blsTables.size());
            tablesInfoVO.setAvailable(availableCnt.intValue());
        }
        vo.setTables(tablesInfoVO);


        vo.setContactPhone(blsStore.getContactPhone());

        // 公告信息
        NearbyStoreVO.AnnouncementVO announcementVO = new NearbyStoreVO.AnnouncementVO();
        announcementVO.setContent(blsStore.getAnnouncement());
        if (blsStore.getUpdateTime() != null) {
            announcementVO.setUpdateTime(blsStore.getUpdateTime().format(DateTimeFormatter.ofPattern(FormatsType.YYYY_MM_DD_HH_MM_SS.getTimeFormat())));
        }
        vo.setAnnouncement(announcementVO);

        // 距离(单位：米)
        vo.setDistance(BigDecimal.valueOf(distanceKm * 1000).setScale(0, RoundingMode.HALF_UP).doubleValue());

        return vo;
    }

    private static Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof BigDecimal) return ((BigDecimal) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception ignored) {
            return null;
        }
    }

    // 逐步迁移至公共工具，无需保留内部实现

    @Override
    public IPage<BlsStore> pageStores(StoreQueryRequest request) {
        Page<BlsStore> pageParam = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<BlsStore> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(request.getName()), BlsStore::getName, request.getName());
        queryWrapper.eq(request.getStatus() != null, BlsStore::getStatus, request.getStatus());
        // 可选商户范围过滤
        MerchantQueryHelper.apply(queryWrapper, BlsStore::getMerchantId);
        queryWrapper.orderByDesc(BlsStore::getCreateTime);

        return this.page(pageParam, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStore(String id) {
        LambdaQueryWrapper<BlsTable> tableQueryWrapper = new LambdaQueryWrapper<>();
        tableQueryWrapper.eq(BlsTable::getStoreId, id);
        // 商户越权校验（仅当会话绑定了商户时收敛）
        BlsStore store = this.getById(id);
        if (store == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "门店不存在");
        }
        MerchantWriteGuard.assertWritable(store.getMerchantId());
        tableMapper.delete(tableQueryWrapper);
        boolean success = this.removeById(id);
        if (!success) {
            throw BilliardsException.of(ResultCode.ERROR, "删除门店主记录失败或门店不存在");
        }
        return true;
    }

    @Override
    public List<BlsStore> listAvailableStores() {
        LambdaQueryWrapper<BlsStore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlsStore::getStatus, 0); // 仅返回正常营业的门店
        queryWrapper.orderByDesc(BlsStore::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<BlsStore> pageAvailableStores(StoreQueryRequest request) {
        Page<BlsStore> pageParam = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<BlsStore> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(BlsStore::getStatus, 0); // 仅返回正常营业的门店

        // 添加关键词搜索 (如果 StoreQueryRequest 有 keyword 字段)
        if (StringUtils.isNotBlank(request.getKeyword())) {
            queryWrapper.and(qw -> qw.like(BlsStore::getName, request.getKeyword())
                                 .or()
                                 .like(BlsStore::getAddress, request.getKeyword()));
        }
        // TODO: 如果 StoreQueryRequest 还有其他小程序端特定的查询字段，在这里添加

        queryWrapper.orderByDesc(BlsStore::getCreateTime);

        return this.page(pageParam, queryWrapper);
    }
}

