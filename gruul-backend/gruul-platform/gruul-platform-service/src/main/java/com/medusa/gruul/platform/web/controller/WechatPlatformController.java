package com.medusa.gruul.platform.web.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.platform.model.vo.WxOpenMaCodeTemplateVo;
import com.medusa.gruul.platform.service.IWechatPlatformService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import me.chanjar.weixin.common.bean.subscribemsg.CategoryData;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateKeyword;
import me.chanjar.weixin.common.bean.subscribemsg.PubTemplateTitleListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 微信第三方相关接口(部分)
 * </p>
 *
 * @author whh
 * @since 2024-02-02
 */
@Hidden
@RestController
@RequestMapping("/wechat")
public class WechatPlatformController {

    @Autowired
    private IWechatPlatformService wechatPlatformService;

    /**
     * 获取小程序模板
     *
     * @return com.medusa.gruul.platform.model.vo.WxOpenMaCodeTemplateVo
     */
    @Operation(summary = "从开放平台获取小程序模板库")
    @PreAuthorize("permitAll()")
    @GetMapping("/gettemplatelist")
    public Result<List<WxOpenMaCodeTemplateVo>> getTemplateList() {
        List<WxOpenMaCodeTemplateVo> templateList = wechatPlatformService.getTemplateList(null);
        return Result.ok(templateList);
    }

    /**
     * 获取指定appId服务类目
     *
     * @param shopId 店铺id
     * @return cn.binarywang.wx.miniapp.api.WxMaSubscribeService
     */
    @GetMapping("/service-class")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取指定appId的服务类目")
    public Result<List<CategoryData>> getAppIdServiceClass(@RequestParam String shopId) {
        List<CategoryData> appIdServiceClass = wechatPlatformService.getAppIdServiceClass(shopId);
        return Result.ok(appIdServiceClass);
    }

    /**
     * 获取模板标题列表
     *
     * @param appId     店铺id
     * @param serviceIds 服务类目id 逗号分隔
     * @param page       页数
     * @param size       条数
     * @return 获取模板标题下的关键词库
     */
    @GetMapping("/getpubtemplatetitles")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取模板标题列表")
    public Result<PubTemplateTitleListResult> getPubTemplateTitles(@RequestParam(name = "appId") String appId, @RequestParam String serviceIds, @RequestParam Integer page,
                                       @RequestParam Integer size) {
        PubTemplateTitleListResult pubTemplateTitleListResult = wechatPlatformService.getPubTemplateTitles(appId, serviceIds, page, size);
        return Result.ok(pubTemplateTitleListResult);
    }

    /**
     * 获取模板标题下的关键词库
     *
     * @param shopId 店铺id
     * @param tid    模板标题 id，可通过接口获取
     * @return
     */
    @GetMapping("/getpubtemplatekeywords")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取模板标题列表")
    public Result<List<PubTemplateKeyword>> getPubTemplateKeywords(@RequestParam(name = "shopId") String shopId, @RequestParam String tid) {
        List<PubTemplateKeyword> pubTemplateKeywordList = wechatPlatformService.getPubTemplateKeywords(shopId, tid);
        return Result.ok(pubTemplateKeywordList);
    }

    /**
     * 清空小程序模板消息
     */
    @GetMapping("/clear/subscribe/msg")
    @PreAuthorize("permitAll()")
    @Operation(summary = "清空指定小程序订阅模板消息")
    public Result<Void> clearSubscribeMsg() {
        wechatPlatformService.clearSubscribeMsg(ISystem.shopId());
        return Result.ok();
    }

    /**
     * 更新店铺订阅消息,在小程序审核通过之后才会去生成,因为消息最多25个
     *
     */
    @GetMapping("/add/subscribe/msg")
    @PreAuthorize("permitAll()")
    @Operation(summary = "为指定小程序更新指定版本订阅消息")
    public Result<Void> addSubscribeMsg() {
        String shopId = ISystem.shopId();
        wechatPlatformService.addSubscribeMsg(shopId);
        return Result.ok();
    }


}
