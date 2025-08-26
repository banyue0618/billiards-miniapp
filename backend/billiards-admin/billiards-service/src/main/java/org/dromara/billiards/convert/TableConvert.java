package org.dromara.billiards.convert;

import org.dromara.billiards.domain.entity.Table;
import org.dromara.billiards.domain.bo.TableDto;
import org.dromara.billiards.domain.vo.TableVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TableConvert {

    TableConvert INSTANCE = Mappers.getMapper(TableConvert.class);

    TableVO toVo(Table table);

    List<TableVO> toVoList(List<Table> tableList);

    Table toEntity(TableDto requestDto);

    void updateEntityFromDto(TableDto requestDto, @MappingTarget Table table);

    default IPage<TableVO> toVoPage(IPage<Table> entityPage) {
        if (entityPage == null) {
            return null;
        }
        IPage<TableVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(toVoList(entityPage.getRecords()));
        return voPage;
    }
}
