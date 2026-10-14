package com.medusa.gruul.recycling.model.vo;

import com.medusa.gruul.recycling.model.dto.BtnStyleDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/4/30
 */
public class RecyclingSettingVo {

    /**
     * 主键
     */
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "商品基金是否开启 true开启 false 关闭")
    private Boolean goodsFundState;

    @Schema(title = "基金额度")
    private BigDecimal fundQuota;

    @Schema(title = "页面背景图片地址")
    private String pageBgImg;

    @Schema(title = "购买成功展示背景图地址")
    private String buyBgImg;

    @Schema(title = "首页未寄出提示背景图地址")
    private String homePromptBgImg;

    @Schema(title = "回收券功能是否开启  true开启 false 关闭")
    private Boolean recyclingState;

    @Schema(title = "商品详情回收入口是否开启  true开启 false 关闭")
    private Boolean entranceState;

    @Schema(title = "样式配置json")
    private List<BtnStyleDto> btnStyles;

    @Schema(title = "关联卡券id")
    private Long assCouponId;

    @Schema(title = "页面轮播图")
    private String pageCarousels;

    @Schema(title = "活动说明")
    private String activityDesc;

    @Schema(title = "提交按钮")
    private String submitImg;
}
