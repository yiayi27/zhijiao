package com.medusa.gruul.order.model;

import com.medusa.gruul.account.api.entity.MiniAccountAddress;
import com.medusa.gruul.account.api.model.MemberCardInfoCenterDto;
import com.medusa.gruul.account.api.model.MemberTopCardInfoDto;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo;
import com.medusa.gruul.goods.api.model.vo.manager.ItemVo;
import com.medusa.gruul.integral.api.model.DiscountInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * The type Confirm order vo.
 * <p>
 * 结算页信息封装
 *
 * @author alan
 * @date 2023/10/6 12:38
 */
@Data
@Schema(title = "结算页信息封装")
public class ConfirmOrderVo {
    /**
     * 所选商品
     */
    @Schema(title = "所选商品")
    List<ItemVo> itemVoList;
    /**
     * /**
     * 客户优惠劵信息
     */
    @Schema(title = "客户优惠劵信息")
    List<MemberCouponVo> memberCoupons;
    /**
     * 满减活动信息
     */
    @Schema(title = "满减活动信息")
    List<FullScaleVo> fullScales;
    /**
     * 系统配置的自定义表单
     */
    @Schema(title = "系统配置的自定义表单")
    List<OrderComponentVo> componentVoList;
    /**
     * 收货地址列表
     */
    @Schema(title = "收货地址列表")
    private List<MiniAccountAddress> miniAccountAddress;
    /**
     * 拥有积分值
     */
    @Schema(title = "拥有积分值")
    private BigDecimal integral;
    /**
     * 用户余额
     */
    @Schema(title = "用户余额")
    private BigDecimal supplyBonus;
    /**
     * 查询积分使用规则
     */
    @Schema(title = "查询积分使用规则")
    private DiscountInfoDto discountInfoDto;
    /**
     * 用户会员信息
     */
    @Schema(title = "用户会员信息")
    private MemberCardInfoCenterDto memberInfo;
    /**
     * 最高的会员等级，用户不是会员时使用
     */
    @Schema(title = "最高的会员等级，用户不是会员时使用")
    private MemberTopCardInfoDto topMemberCardInfo;

}
