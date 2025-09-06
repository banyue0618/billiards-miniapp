package org.dromara.billiards.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.billiards.domain.entity.BlsPayRecord;

/**
 * 充值支付记录 Mapper 接口
 *
 * @author zhangsip
 * @date 2024-07-02
 */
@Mapper
public interface PayRecordMapper extends BaseMapper<BlsPayRecord> {
}
