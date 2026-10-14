package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.goods.api.entity.DistributionSet;
import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 商品分销信息 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-09-30
 */
@Repository
public interface DistributionSetMapper extends BaseMapper<DistributionSet> {

    /**
     * 获取指定分销信息
     *
     * @param productId 商品id
     * @return 分销list信息
     */
    List<DistributionSetVo> getByProductId(Long productId);

}
