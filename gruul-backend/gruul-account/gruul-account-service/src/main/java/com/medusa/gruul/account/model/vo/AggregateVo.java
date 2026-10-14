package com.medusa.gruul.account.model.vo;

import com.medusa.gruul.integral.api.model.ShopFunctionInfoVo;
import com.medusa.gruul.integral.api.model.UserInfoVo;
import com.medusa.gruul.order.api.model.OrderOverviewVo;
import com.medusa.gruul.shops.api.model.AccountCenterVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author whh
 * @description
 * @date: 2024/5/13
 */
@Data
@Accessors(chain = true)
public class AggregateVo {
    @Schema(title = "订单数据")
    private OrderOverviewVo orderOverviewVo;
    @Schema(title = "用户收藏数量")
    private Integer collectCount;
    @Schema(title = "用户足迹数量")
    private Integer accountFootMarkCount;
    @Schema(title = "用户中心")
    private AccountCenterVo accountCenterVo;

    @Schema(title = "积分商城规则")
    private ShopFunctionInfoVo shopFunctionInfoVo;
    @Schema(title = "积分用户所需")
    private UserInfoVo userInfoVo;

}
