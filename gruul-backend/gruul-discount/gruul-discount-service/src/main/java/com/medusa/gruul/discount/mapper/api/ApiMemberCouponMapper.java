package com.medusa.gruul.discount.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import com.medusa.gruul.discount.api.model.vo.api.ApiMemberCouponVo;
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
public interface ApiMemberCouponMapper extends BaseMapper<MemberCoupon> {

    /**
     * 获取用户优惠劵信息list
     * @param userId
     * @return 用户优惠劵list信息
     */
    List<ApiMemberCouponVo> queryMemberCouponListByUserId(String userId);
}
