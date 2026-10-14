package com.medusa.gruul.goods.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.DistributionSet;
import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;

import java.util.List;

/**
 * <p>
 * 商品分销信息 服务类
 * </p>
 *
 * @author lcysike
 * @since 2023-09-30
 */
public interface IDistributionSetService extends IService<DistributionSet> {

    /**
     * 查询列表
     *
     * @param productId 产品id
     * @return 产品分销信息对象
     */
    List<DistributionSetVo> getByProductId(Long productId);

}
