package org.dromara.billiards.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.entity.BlsMemberPointsConsumeDetail;
import org.dromara.billiards.domain.vo.BlsMemberPointsConsumeDetailVo;
import org.dromara.billiards.domain.bo.BlsMemberPointsConsumeDetailBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员积分消费详情Service接口
 *
 * @author banyue
 * @date 2025-09-09
 */
public interface IBlsMemberPointsConsumeDetailService extends IService<BlsMemberPointsConsumeDetail> {

    /**
     * 查询会员积分消费详情
     *
     * @param id 主键
     * @return 会员积分消费详情
     */
    BlsMemberPointsConsumeDetailVo queryById(String id);

    /**
     * 分页查询会员积分消费详情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员积分消费详情分页列表
     */
    TableDataInfo<BlsMemberPointsConsumeDetailVo> queryPageList(BlsMemberPointsConsumeDetailBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员积分消费详情列表
     *
     * @param bo 查询条件
     * @return 会员积分消费详情列表
     */
    List<BlsMemberPointsConsumeDetailVo> queryList(BlsMemberPointsConsumeDetailBo bo);

    /**
     * 新增会员积分消费详情
     *
     * @param bo 会员积分消费详情
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberPointsConsumeDetailBo bo);

    /**
     * 修改会员积分消费详情
     *
     * @param bo 会员积分消费详情
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberPointsConsumeDetailBo bo);

    /**
     * 校验并批量删除会员积分消费详情信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
