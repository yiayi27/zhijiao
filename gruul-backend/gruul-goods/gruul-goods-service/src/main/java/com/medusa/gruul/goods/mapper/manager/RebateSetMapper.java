package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.goods.api.entity.DistributionSet;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;
import com.medusa.gruul.goods.api.model.vo.manager.RebateSetVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 店铺返利信息设置 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-09-30
 */
@Repository
public interface RebateSetMapper extends BaseMapper<RebateSet> {

    /**
     * 获取默认返利信息
     *
     * @return RebateSetVo
     */
    RebateSetVo queryDefaultRebateSet();

}
