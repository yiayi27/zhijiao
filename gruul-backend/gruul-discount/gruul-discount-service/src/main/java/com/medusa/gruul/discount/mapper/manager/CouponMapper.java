package com.medusa.gruul.discount.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.medusa.gruul.discount.api.model.param.manager.CouponParam;
import com.medusa.gruul.discount.api.model.vo.manager.CouponVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 优惠劵 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Component
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 获取优惠劵信息list
     *
     * @param page        分页对象
     * @param couponParam 优惠劵信息查询参数
     * @return 优惠劵list信息
     */
    List<CouponVo> queryCouponList(IPage page, @Param("couponParam") CouponParam couponParam);

    /**
     * 获取积分优惠劵信息list
     *
     * @param page        分页对象
     * @param couponParam 优惠劵信息查询参数
     * @return 积分优惠劵list信息
     */
    List<CouponVo> queryIntegralCouponList(IPage page, @Param("couponParam") CouponParam couponParam);

    /**
     * 获取优惠劵详细信息
     *
     * @param id
     * @return 单张优惠劵详细信息
     */
    CouponVo queryCouponById(@Param("id") Long id);

    /**
     * 定时任务查询可未开始与进行中的优惠劵活动
     *
     * @return List<CouponVo>
     */
    List<CouponVo> queryCouponListForTask();

    /**
     * 定时任务查询可未开始与进行中的优惠劵活动
     *
     * @param couponVo
     */
    void updateCouponForTask(CouponVo couponVo);
}
