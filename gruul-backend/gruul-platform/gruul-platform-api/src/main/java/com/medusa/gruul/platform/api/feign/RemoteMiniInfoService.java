package com.medusa.gruul.platform.api.feign;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.order.api.model.UploadShippingInfoDTO;
import com.medusa.gruul.platform.api.model.dto.*;
import com.medusa.gruul.platform.api.model.vo.MiniMsgVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author whh
 */
@FeignClient(value = "platform")
public interface RemoteMiniInfoService {


    /**
     * 获取指定店铺id店铺信息
     *
     * @return com.medusa.gruul.platform.api.model.dto.ShopInfoDto
     */
    @RequestMapping(value = "/get/shop/info", method = RequestMethod.GET)
    @Operation(summary = "获取指定店铺id店铺信息")
    Result<ShopInfoDto> getShopInfo();


    /**
     * 获取当前店铺可使用的小程序订阅模板
     *
     * @return com.medusa.gruul.platform.api.model.vo.MiniMsgVo
     */
    @RequestMapping(value = "/mini/subscribe/msg", method = RequestMethod.GET)
    @Operation(summary = "获取当前店铺可使用的小程序订阅模板")
    List<MiniMsgVo> getCurrentMiniMsg();

    /**
     * 确认token和店铺id是否一致,确认是否商家账号
     *
     * @param token 请求token
     * @return java.lang.Boolean
     */
    @RequestMapping(value = "/affirm/lessee/{token}", method = RequestMethod.GET)
    @Operation(summary = "确认是否商家,返回token和店铺id是否一致 确认是否商家账号")
    Result<Boolean> affirmLessee(@PathVariable(value = "token") String token);

    /**
     * 获取当前平台使用oss配置(默认使用七牛云)
     *
     * @return com.medusa.gruul.platform.api.model.dto.OssConfigDto
     */
    @RequestMapping(value = "/oss/config", method = RequestMethod.GET)
    @Operation(summary = "获取当前平台使用oss配置")
    Result<OssConfigDto> currentOssConfig();

    /**
     * 根据code换取小程序用户基本信息
     *
     * @param code code
     * @return com.medusa.gruul.platform.api.entity.MiniInfo
     */
    @Operation(summary = "根据code换取小程序用户基本信息")
    @RequestMapping(value = "/login/{code}", method = RequestMethod.GET)
    LoginDto login(@PathVariable(value = "code") String code);

    /**
     * 根据店铺id换取指定小程序信息
     *
     * @return com.medusa.gruul.platform.api.model.dto.MiniAuthInfoDto
     * code=200 返回所需信息;  code=400返回错误原因
     */
    @GetMapping("/mini/auth/info")
    @Operation(summary = "根据店铺id换取小程序部分授权信息")
    Result<MiniAuthInfoDto> getMiniAuthInfo();

    /**
     * 获取店铺配置信息
     *
     * @return com.medusa.gruul.platform.api.model.dto.ShopConfigDto
     */
    @RequestMapping(value = "/shop/config", method = RequestMethod.GET)
    @Operation(summary = "根据店铺id获取店铺配置信息")
    ShopConfigDto getShopConfig();


    /**
     * 根据appid获取店铺配置信息
     *
     * @param appId 小程序appId
     * @return com.medusa.gruul.platform.api.model.dto.ShopConfigDto
     */
    @RequestMapping(value = "/shop/config/appid", method = RequestMethod.GET)
    @Operation(summary = "根据appid获取店铺配置信息(小程序信息,支付配置)")
    ShopConfigDto getShopConfigAndAppId(@RequestParam(value = "appId", required = true) String appId);

    /**
     * 获取当前所有的店铺id列表
     * 仅限默认数据生成兼容使用，调用方请勿频繁调用
     *
     * @return List<String>
     */
    @RequestMapping(value = "/shops/all", method = RequestMethod.GET)
    @Operation(summary = "获取当前所有的店铺id列表，仅限默认数据生成兼容使用，调用方请勿频繁调用")
    List<String> getShopsAll();

    /**
     * 小程序发货信息录入
     *
     * @param param 订单和发货信息
     * @return Result<Boolean>
     */
    @RequestMapping(value = "/upload/shipping/info", method = RequestMethod.PUT)
    @Operation(summary = "小程序发货信息录入")
    Result<Boolean> uploadShippingInfo(@RequestBody UploadShippingInfoDTO param);

}
