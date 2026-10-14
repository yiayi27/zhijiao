package com.medusa.gruul.recycling.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.recycling.api.entity.RecyclingAssOrderAccount;
import com.medusa.gruul.recycling.model.dto.SelectStatisticalDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 基金所得订单用户关联表 Mapper 接口
 * </p>
 *
 * @author whh
 * @since 2024-05-05
 */
public interface RecyclingAssOrderAccountMapper extends BaseMapper<RecyclingAssOrderAccount> {

    /**
     * 获取店铺统计
     * @param shopUserId 店铺用户id
     *
     * @return 统计数据
     */
    SelectStatisticalDto selectStatistical(@Param("shopUserId") String shopUserId);

    /**
     * 获取指定排序
     *
     * @param shopId   店铺店铺id
     * @return 排序
     */
    @MapKey("shop_user_id")
    Map<Long, HashMap> selectByAccountRanking(@Param("shopId") String shopId);

}
