package com.medusa.gruul.order.model;

import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.platform.api.model.dto.ShopInfoDto;
import com.medusa.gruul.platform.api.model.vo.MiniMsgVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The type Setting vo.
 * <p>
 * 设置相关
 *
 * @author alan
 * @date 2023/10/6 12:38
 */
@Data
@Schema(title = "设置相关")
public class SettingVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 是否开启评价
     */
    @Schema(title = "是否开启评价")
    private Boolean openEvaluate;
    private OrderSetting setting;
    /**
     * 可以使用的微信订阅消息
     */
    @Schema(title = "可以使用的微信订阅消息")
    private List<MiniMsgVo> miniMsgVoList;
    /**
     * 返利
     */
    @Schema(title = "返利设置")
    private RebateSet rebateSet;
    /**
     * 会员是否开启
     */
    @Schema(title = "会员是否开启")
    private Integer openMember;
    /**
     * 当前的店铺详情
     */
    @Schema(title = "当前的店铺详情")
    private ShopInfoDto curShopInfoDto;
}
