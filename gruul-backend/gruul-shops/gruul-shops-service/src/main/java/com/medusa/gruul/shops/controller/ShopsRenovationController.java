package com.medusa.gruul.shops.controller;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.ShopsRenovationPage;
import com.medusa.gruul.shops.model.param.ShopsRenovationAssemblyParam;
import com.medusa.gruul.shops.model.param.ShopsRenovationPageParam;
import com.medusa.gruul.shops.model.param.ShopsRenovationPluginParam;
import com.medusa.gruul.shops.model.vo.ShopsRenovationAssemblyVo;
import com.medusa.gruul.shops.model.vo.ShopsRenovationPageVo;
import com.medusa.gruul.shops.model.vo.ShopsRenovationPluginVo;
import com.medusa.gruul.shops.model.vo.ShopsRenovationTemplateVo;
import com.medusa.gruul.shops.service.ShopsRenovationPluginService;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageAssService;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 店铺装修模板页面
 * @author create by zq
 * @date created in 2024/01/14
 */
@RestController
@RequestMapping(value = "/renovation")
@PreAuthorize("@S.hasPerm(@P.MALL_SETTING.DECORATION)")
public class ShopsRenovationController {


    @Autowired
    private ShopsRenovationTemPageService shopsRenovationTemPageService;

    @Autowired
    private ShopsRenovationPluginService shopsRenovationPluginService;

    @Autowired
    private ShopsRenovationTemPageAssService shopsRenovationTemPageAssService;

    /**
     * 保存模板页面
     *
     * @param param 店铺装修模板页面信息
     * @return Result
     */
    @PutMapping("/template/page/save")
    @Operation(summary = "保存模板页面")
    public Result<Void> saveTemplatePage(@RequestBody ShopsRenovationPageParam param) {
        shopsRenovationTemPageService.saveTemplatePage(param);
        return Result.ok();
    }

    /**
     * 获取模版信息
     *
     * @param modelId 模板id
     * @return Result
     */
    @GetMapping("/template/page/modelId")
    @Operation(summary = "getByModelId")
    public Result<List<ShopsRenovationPage>> getByModelId(@RequestParam String modelId) {
        return Result.ok(shopsRenovationTemPageService.getByModelId(modelId));
    }

    /**
     * 删除商铺装修模板页面 by ids or 模板ids
     *
     * @param ids 模板ids
     * @return Result
     */
    @GetMapping("/template/page/delete")
    @Operation(summary = "删除商铺装修模板 by ids or 模板ids")
    public Result<Void> delTemplatePage(@RequestParam Set<String> ids) {
        shopsRenovationTemPageService.delTemplatePage(ids);
        return Result.ok();
    }

    /**
     * 获取商铺装修模板页面list
     *
     * @param param 店铺装修模板页面信息
     * @return Result
     */
    @GetMapping("/template/page/list")
    @Operation(summary = "获取商铺装修模板list")
    public Result<PageUtils<ShopsRenovationPageVo>> listTemplatePage(ShopsRenovationPageParam param) {
        return Result.ok(shopsRenovationTemPageService.listTemplatePage(param));
    }

    /**
     * 获取已经完成装修的商品组件
     *
     * @param param 店铺装修模板页面信息
     * @return Result<PageUtils<ShopsRenovationPageVo>>
     */
    @Operation(summary = "获取商品专区已完成的装修组件")
    @GetMapping("/fitment/end/page/list")
    public Result<PageUtils<ShopsRenovationPageVo>> fitmentPrefectureEnd(ShopsRenovationPageParam param) {
        return Result.ok(shopsRenovationTemPageService.fitmentPrefectureEndPage(param));
    }


    /**
     * 店铺装修模板 - 全局控件属性配置新增&修改
     *
     * @param param 店铺装修模板页面信息
     * @return Result
     */
    @PostMapping("/plugin/add")
    @Operation(summary = "店铺装修模板 - 全局控件属性配置新增&修改")
    public Result<Void> addPlugin(@RequestBody ShopsRenovationPluginParam param) {
        return shopsRenovationPluginService.addPlugin(param);
    }

    /**
     * 店铺装修模板 - 全局控件属性配置查询list
     *
     * @return Result
     */
    @PostMapping("/plugin/list")
    @Operation(summary = "店铺装修模板 - 全局控件属性配置查询list")
    public Result<List<ShopsRenovationPluginVo>> listPlugin() {
        return Result.ok(shopsRenovationPluginService.listPlugin());
    }


    /**
     * 店铺装修模板 - 页面插件属性配置新增&修改
     *
     * @param params 店铺装修模板页面组件信息
     * @return Result
     */
    @PostMapping("/template/page/assembly/add")
    @Operation(summary = "店铺装修模板 - 页面插件属性配置新增&修改")
    public Result<Void> addTemplatePageAssembly(@RequestBody @NotNull @Size(min = 1) List<ShopsRenovationAssemblyParam> params) {
        shopsRenovationTemPageAssService.addTemplatePageAssembly(params);
        return Result.ok();
    }


    /**
     * 店铺装修模板 - 页面插件属性配置逻辑删除 by ids
     *
     * @param ids 模版ids
     * @return Result
     */
    @GetMapping("/template/page/assembly/delete")
    @Operation(summary = "店铺装修模板 - 页面插件属性配置逻辑删除 by ids")
    public Result<Void> delTemplatePageAssembly(@RequestParam @NotNull @Size(min = 1) Set<Long> ids) {
        shopsRenovationTemPageAssService.delTemplatePageAssembly(ids);
        return Result.ok();
    }


    /**
     * 店铺装修模板 - 页面插件属性配置查询list
     *
     * @param param 店铺装修模板页面组件信息
     * @return Result
     */
    @PostMapping("/template/page/assembly/list")
    @Operation(summary = "店铺装修模板 - 页面插件属性配置查询list")
    @PreAuthorize("permitAll()")
    public Result<List<ShopsRenovationAssemblyVo>> listTemplatePageAssembly(@RequestBody ShopsRenovationAssemblyParam param) {
        return Result.ok(shopsRenovationTemPageAssService.listTemplatePageAssembly(param));
    }

    /**
     * 小程序装修默认聚合接口
     *
     * @return Result
     */
    @GetMapping("/template/def/one")
    @PreAuthorize("permitAll()")
    @Operation(summary = "小程序装修默认聚合接口")
    public Result<ShopsRenovationTemplateVo> templateDefOne() {
        return Result.ok(shopsRenovationTemPageService.defOne());
    }


}
