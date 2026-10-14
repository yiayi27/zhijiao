package com.medusa.gruul.platform.web.remote;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.order.api.model.UploadShippingInfoDTO;
import com.medusa.gruul.platform.api.model.dto.OssConfigDto;
import com.medusa.gruul.platform.api.model.dto.ShopConfigDto;
import com.medusa.gruul.platform.api.model.dto.ShopInfoDto;
import com.medusa.gruul.platform.api.model.vo.MiniMsgVo;
import com.medusa.gruul.platform.service.IPlatformShopInfoService;
import com.medusa.gruul.platform.service.IPlatformShopMessageService;
import com.medusa.gruul.platform.service.ISystemConfService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author whh
 */
@RestController(value = "remoteMiniInfoController")
@RequestMapping("/")
@Hidden
@Tag(name = "远程调用接口,仅限后端fegin调用-->RemoteMiniInfoService")
public class RemoteMiniInfoController {

    @Autowired
    private ISystemConfService systemConfService;
    @Autowired
    private IPlatformShopMessageService platformShopMessageService;
    @Autowired
    private IPlatformShopInfoService platformShopInfoService;


    /**
     * 获取指定店铺id店铺信息
     *
     * @return com.medusa.gruul.platform.api.model.dto.ShopInfoDto
     */
    @GetMapping(value = "/get/shop/info")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取指定店铺id店铺信息")
    public Result<ShopInfoDto> getShopInfo(@RequestParam(value = "shopId", required = true) String shopId) {
        return platformShopInfoService.getShopInfo(shopId);
    }

    /**
     * 获取当前使用oss配置
     *
     * @return com.medusa.gruul.platform.api.model.dto.OssConfigDto
     */
    @GetMapping("/oss/config")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取当前平台使用oss配置")
    public Result<OssConfigDto> currentOssConfig() {
        return systemConfService.currentOssConfig();
    }

    /**
     * 获取店铺配置信息
     * 远程调用接口使用
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.platform.api.model.dto.ShopConfigDto
     */
    @GetMapping("/shop/config")
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据店铺id获取店铺配置信息(小程序信息,支付配置)")
    public ShopConfigDto getShopConfig(@RequestParam String shopId) {
        return platformShopInfoService.getShopConfig(shopId);
    }

    @GetMapping("/shop/config/appid")
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据appid获取店铺配置信息(小程序信息,支付配置)")
    public ShopConfigDto getShopConfigAndAppId(@RequestParam String appId) {
        return platformShopInfoService.getShopConfigAndAppId(appId);
    }

    @PutMapping("/upload/shipping/info")
    @PreAuthorize("permitAll()")
    @Operation(summary = "小程序发货信息录入")
    public Result<Boolean> uploadShippingInfo(@RequestBody UploadShippingInfoDTO param) {
        return platformShopMessageService.uploadShippingInfo(param.getOrder(), param.getOrderDelivery());
    }

    @GetMapping(value = "/mini/subscribe/msg")
    @Operation(summary = "获取当前店铺可使用的小程序订阅模板")
    public List<MiniMsgVo> getCurrentMiniMsg() {
        return platformShopMessageService.getCurrentMiniMsg();
    }

}
