package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.api.model.DiscountInfoDto;
import com.medusa.gruul.integral.web.model.vo.IntegralShopVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 积分商城 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface IntegralShopMapper extends BaseMapper<IntegralShop> {

    /**
     * 根据shopId(店铺id)获取 积分商城表id
     *
     * @param shopId
     * @return
     */
    Long selectByShopId(@Param("shopId") String shopId);

    /**
     * 修改积分商城开启关闭状态
     *
     * @param shopId 店铺id
     * @param status 状态值
     * @return
     */
    Integer updateShopStatus(@Param("shopId") String shopId, @Param("status") Boolean status);

    /**
     * 店铺信息
     *
     * @param shopId 店铺id
     * @return
     */
    IntegralShopVo getShopInfo(@Param("shopId") String shopId);

    /**
     * 获取商城信息
     *
     * @param shopId
     * @return
     */
    IntegralShop selectByShopInfo(@Param("shopId") String shopId);


    /**
     * 根据商铺id 获取商铺设置的积分抵扣规则
     *
     * @param shopId 店铺id
     * @return
     */
    DiscountInfoDto getDiscountInfo(@Param("shopId") String shopId);

    /**
     * 获取所有商城信息
     *
     * @return
     */
    List<IntegralShop> selectAllShopInfo();

    /**
     * 定時任務 修改积分商城表创建时间
     *
     * @param shopId      商铺id
     * @param currentTime 现在时间
     */
    void updateCreateTime(
            @Param("shopId") String shopId, @Param("currentTime") LocalDateTime currentTime);


}
