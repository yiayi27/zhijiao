package com.medusa.gruul.shops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.shops.api.entity.ShopsPartner;
import com.medusa.gruul.shops.model.vo.ShopsPartnerVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Repository
public interface ShopsPartnerMapper extends BaseMapper<ShopsPartner> {

    /**
     * 获取店铺 by shopId
     *
     * @param shopId
     * @return shops
     */
    ShopsPartnerVo oneByShopId(String shopId);

    /**
     * 获取店铺 by shopId
     *
     * @param platformId 平台ID
     * @param shopId 店铺ID
     * @return ShopsPartner
     */
    ShopsPartner selectByPlatformIdAndShopId(@Param("platformId") Long platformId, @Param("shopId") String shopId);

    /**
     * 获取店铺 by shopId
     *
     * @param shopId 店铺ID
     * @return ShopsPartner
     */
    ShopsPartner selectByShopIdAndPartnerIdIsNull(@Param("shopId") String shopId);

}
