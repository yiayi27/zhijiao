package com.medusa.gruul.discount.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 客户优惠劵 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Component
public interface MemberCouponMapper extends BaseMapper<MemberCoupon> {

    /**
     * 获取优惠劵信息list
     *
     * @param userId
     * @return 优惠劵list信息
     */
    List<MemberCouponVo> queryGoingMemberCouponList(@Param("userId") String userId);

    /**
     * 根据ID查询优惠券详情
     *
     * @param userId
     * @param couponId
     * @return com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo
     * @author alan
     * @date 2023/12/29 10:34
     */
    MemberCouponVo findMemberCouponForOrder(@Param("userId") String userId, @Param("couponId") Long couponId);

    /**
     * 定时任务查询可未开始与进行中的客户优惠劵活动
     *
     * @return List<MemberCouponVo>
     */
    List<MemberCouponVo> queryMemberCouponListForTask();

    /**
     * 定时任务查询可未开始与进行中的客户优惠劵活动
     *
     * @param memberCouponVo
     */
    void updateMemberCouponForTask(MemberCouponVo memberCouponVo);
}
