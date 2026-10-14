package com.medusa.gruul.goods.service.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.model.dto.manager.RebateSetDto;
import com.medusa.gruul.goods.mapper.manager.RebateSetMapper;
import com.medusa.gruul.goods.service.manager.IRebateSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 店铺返利信息设置 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2024-05-21
 */
@Service
public class RebateSetServiceImpl extends ServiceImpl<RebateSetMapper, RebateSet> implements IRebateSetService {

    @Autowired
    private RebateSetMapper rebateSetMapper;

    /**
     * 获取店铺返利信息设置
     *
     * @return RebateSetVo
     */
    @Override
    public RebateSet selectRebateSet() {
        String shopId = ISecurity.userMust().getShopId();
        return rebateSetMapper.selectOne(new LambdaQueryWrapper<RebateSet>().eq(RebateSet::getShopId, shopId));
    }

    /**
     * feign获取店铺返利信息设置
     * @param shopId 店铺id
     * @return RebateSetVo
     */
    @Override
    public RebateSet getRebateSet(String shopId) {
        return rebateSetMapper.selectOne(new LambdaQueryWrapper<RebateSet>().eq(RebateSet::getShopId, shopId));
    }

    /**
     * 新增/修改店铺返利信息设置
     *
     * @param rebateSetDto 返利信息
     */
    @Override
    public void saveRebateSet(RebateSetDto rebateSetDto) {
        if(rebateSetDto.getId() != null){
            RebateSet rebateSet = rebateSetDto.coverRebateSet();
            int update = rebateSetMapper.updateById(rebateSet);
            if(update == 0){
                throw new ServiceException("店铺信息设置失败！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }else{
            RebateSet rebateSet = rebateSetDto.coverRebateSet();
            int insert = rebateSetMapper.insert(rebateSet);
            if(insert == 0){
                throw new ServiceException("店铺信息设置失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
    }
}
