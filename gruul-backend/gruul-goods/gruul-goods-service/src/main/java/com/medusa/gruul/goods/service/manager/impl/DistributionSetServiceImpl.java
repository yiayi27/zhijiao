package com.medusa.gruul.goods.service.manager.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.goods.api.entity.DistributionSet;
import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;
import com.medusa.gruul.goods.mapper.manager.DistributionSetMapper;
import com.medusa.gruul.goods.service.manager.IDistributionSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 商品分销信息 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-09-30
 */
@Service
public class DistributionSetServiceImpl extends ServiceImpl<DistributionSetMapper, DistributionSet> implements IDistributionSetService {

    @Autowired
    private DistributionSetMapper distributionSetMapper;

    /**
     * 根据商品id获取分销设置信息
     *
     * @param productId 产品id
     * @return 分销设置list
     */
    @Override
    public List<DistributionSetVo> getByProductId(Long productId) {
        return distributionSetMapper.getByProductId(productId);
    }

}
