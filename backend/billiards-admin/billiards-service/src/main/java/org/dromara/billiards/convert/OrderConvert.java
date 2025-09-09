package org.dromara.billiards.convert;

import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.vo.OrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderConvert {

    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);

    OrderVO toVo(BlsOrder blsOrder);

    List<OrderVO> toVoList(List<BlsOrder> blsOrderList);

    default IPage<OrderVO> toVoPage(IPage<BlsOrder> orderPage) {
        if (orderPage == null) {
            return null;
        }
        IPage<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        voPage.setRecords(toVoList(orderPage.getRecords()));
        return voPage;
    }
}
