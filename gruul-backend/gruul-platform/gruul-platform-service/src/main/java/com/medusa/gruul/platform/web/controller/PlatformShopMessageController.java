package com.medusa.gruul.platform.web.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.platform.api.model.vo.ShopMessageVo;
import com.medusa.gruul.platform.model.dto.MotifyMsgStateDto;
import com.medusa.gruul.platform.model.vo.PcMsgVo;
import com.medusa.gruul.platform.service.IPlatformShopMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 店铺消息配置 前端控制器
 * </p>
 *
 * @author whh
 * @since 2024-05-22
 */
@RestController
@RequestMapping("/shop-message")
@Tag(name = "店铺消息相关接口")
@PreAuthorize("@S.hasPerm(@P.MALL_SETTING.COMMON_SETTING)")
public class PlatformShopMessageController {

    @Autowired
    private IPlatformShopMessageService platformShopMessageService;

    /**
     * 获取店铺消息
     *
     * @return com.medusa.gruul.platform.api.model.vo.ShopMessageVo
     */
    @GetMapping(value = "buyer/notify")
    @Operation(summary = "获取店铺买家通知")
    public Result<List<ShopMessageVo>> msgAll() {
        List<ShopMessageVo> vos = platformShopMessageService.msgAll();
        return Result.ok(vos);
    }

    /**
     * 修改消息状态
     *
     * @param msgStateDto com.medusa.gruul.platform.model.dto.MotifyMsgStateDto
     */
    @PutMapping(value = "/state")
    @Operation(summary = "修改消息开启状态")
    public Result<Void> motifyMsgState(@RequestBody MotifyMsgStateDto msgStateDto) {
        platformShopMessageService.motifyState(msgStateDto);
        return Result.ok();
    }


    @GetMapping(value = "/up/subscription/msg")
    @Operation(summary = "更新店铺指定版本买家消息至微信(生成消息模板id)")
    public Result<List<PcMsgVo>> upSubscriptionMsg() {
        platformShopMessageService.upSubscriptionMsg(ISystem.shopId());
        return Result.ok();
    }


}
