package org.dromara.billiards.convert;

import org.dromara.billiards.domain.bo.StoreDto;
import org.dromara.billiards.domain.entity.BlsStore;
import org.dromara.billiards.domain.vo.StoreVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

@Mapper(componentModel = "spring") // 使用 componentModel = "spring" 可以让 Spring 管理 Mapper bean
public interface StoreConvert {

    StoreConvert INSTANCE = Mappers.getMapper(StoreConvert.class);

    /**
     * 将 StoreDto 转换为 Store 实体
     * <p>
     * 注意：
     * 1. StoreDto 中的 id 字段在创建时不使用，在更新时使用。MapStruct默认会映射。
     *    如果StoreService的save方法不处理传入的id（即总是新生成id），则没问题。
     *    如果save方法会复用id，而我们希望创建时总是新id，则可能需要针对创建场景单独写一个方法或用@Mapping忽略id。
     *    对于更新场景，这个id是需要的。
     *    Store 实体中的 status (Integer类型) 在创建时 Service 层应该设置一个初始值。
     * 3. BaseEntity 中的字段 (createTime, updateTime 等) 由 MyBatis-Plus 自动填充。
     */
    @Mappings({
        // 如果有字段名不一致或者需要特殊转换的可以在这里配置
        // @Mapping(source = "dtoFieldName", target = "entityFieldName"),
        // 例如，如果DTO中没有coverImage，但Entity中有，且需要默认值，则可以:
        // @Mapping(target = "coverImage", ignore = true) // 或者用 defaultExpression
        // 这里假设大部分字段名一致，MapStruct会自动映射
        @Mapping(target = "id", source = "id") // 明确映射id，用于更新场景
        // status 将由Service层设置
        // createTime, updateTime 等BaseEntity字段由MP自动处理
    })
    BlsStore toEntity(StoreDto dto);

    /**
     * 根据 DTO 更新已存在的 Store 实体
     * @param dto 包含更新信息的 DTO
     * @param entity 需要被更新的实体
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        // createTime 字段不应该被更新
        @Mapping(target = "createTime", ignore = true),
        // createBy 字段不应该被更新 (如果存在)
        // @Mapping(target = "createBy", ignore = true)
        // updateTime 和 updateBy 由 MyBatis-Plus 自动处理，无需显式忽略，MapStruct通常也不会映射它们，除非VO中有
    })
    void updateStoreFromDto(StoreDto dto, @MappingTarget BlsStore entity);

    /**
     * 将 Store 实体转换为 StoreVo
     * <p>
     * BaseEntity 中的 createTime, updateTime 等字段会自动映射 (如果名称和类型兼容)。
     */
    @Mappings({
        // @Mapping(source = "entity.createTime", target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss") // 如果需要日期格式转换
    })
    StoreVO toVo(BlsStore entity);

    /**
     * 将 List<Store> 转换为 List<StoreVo>
     */
    List<StoreVO> toVoList(List<BlsStore> blsStoreList);

    /**
     * 将 IPage<Store> 转换为 IPage<StoreVo>
     * This default method manually constructs the new Page to ensure all pagination properties are copied.
     */
    default IPage<StoreVO> toVoPage(IPage<BlsStore> storePage) {
        if (storePage == null) {
            return null;
        }
        IPage<StoreVO> voPage = new Page<>(storePage.getCurrent(), storePage.getSize(), storePage.getTotal());
        voPage.setRecords(toVoList(storePage.getRecords()));
        // Ensure other properties like pages, orders, etc., are copied if necessary, though usually current, size, total, and records are sufficient.
        // For Page, it also has setPages(long pages) but it's often calculated from total and size.
        return voPage;
    }
}
