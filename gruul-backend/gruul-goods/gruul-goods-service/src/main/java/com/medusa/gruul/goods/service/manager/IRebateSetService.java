package com.medusa.gruul.goods.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.model.dto.manager.RebateSetDto;


/**
 * <p>
 * 店铺返利信息设置 服务类
 * </p>
 *
 * @author lcysike
 * @since 2024-05-21
 */
public interface IRebateSetService extends IService<RebateSet> {

    /**
     * 获取店铺返利信息设置
     *
     * @return RebateSet
     */
    RebateSet selectRebateSet();

    /**
     * feign接口获取店铺返利信息设置
     *
     * @param shopId 店铺id
     * @return RebateSet
     */
    RebateSet getRebateSet(String shopId);

    /**
     * 新增/修改店铺返利信息设置
     *
     * @param rebateSetDto 返利信息
     */
    void saveRebateSet(RebateSetDto rebateSetDto);

}
