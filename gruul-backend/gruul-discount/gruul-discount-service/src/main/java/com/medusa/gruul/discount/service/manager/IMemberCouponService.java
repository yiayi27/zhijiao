package com.medusa.gruul.discount.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo;

import java.util.List;

/**
 * <p>
 * 客户优惠劵 服务类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
public interface IMemberCouponService extends IService<MemberCoupon> {

    /**
     * 查询客户可使用优惠劵
     *
     * @param userId
     * @param productIds
     * @return List<MemberCouponVo>
     */
    List<MemberCouponVo> findMemberCouponListForOrder(String userId, List<Long> productIds);

    /**
     * 使用优惠劵
     *
     * @param id
     * @return Boolean
     */
    Boolean useMemberCouponForOrder(Long id);

    /**
     * 归还优惠劵
     *
     * @param id
     * @return Boolean
     */
    Boolean revertMemberCouponForOrder(Long id);

    /**
     * 查询单个优惠劵信息
     *
     * @param userId
     * @param couponId
     * @return com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo
     * @author alan
     * @date 2023/12/29 10:33
     */
    MemberCouponVo findMemberCouponForOrder(String userId, Long couponId);

    /**
     * 定时任务查询可未开始与进行中的客户优惠劵活动
     *
     * @return List<MemberCouponVo>
     */
    List<MemberCouponVo> findMemberCouponListForTask();

    /**
     * 定时任务查更新客户优惠劵活动状态
     *
     * @param memberCouponVo
     */
    void updateMemberCouponForTask(MemberCouponVo memberCouponVo);

    /**
     *  领取回收券
     * @param userId  用户id
     * @param couponId 优惠券id
     * @param number 优惠券数量
     * @return
     */
    Result<String> grantDiscountCoupon(String  userId, Long couponId, Integer number);

}
