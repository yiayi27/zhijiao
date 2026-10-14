package com.medusa.gruul.goods.web.controller.api;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryVo;
import com.medusa.gruul.goods.service.api.IApiShowCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序商品展示分类 前端控制器
 *
 * @author kyl
 * @since 2023-11-05
 */
@RestController
@RequestMapping("/api/show/category")
public class ApiShowCategoryController {

    @Autowired
    private IApiShowCategoryService showCategoryService;

    /**
     * 获取所有含有商品的展示分类
     * @return Result<List<ApiShowCategoryVo>>
     */
    @GetMapping("/get/all")
    @Operation(summary = "所有含有商品的展示分类")
    @PreAuthorize("permitAll()")
    public Result<List<ApiShowCategoryVo>> getAllApiShowCategoryList() {
        List<ApiShowCategoryVo> attributeTemplateVos = showCategoryService.getAllApiShowCategoryList();
        return Result.ok(attributeTemplateVos);
    }

    /**
     * 获取对应分区含有商品的展示分类
     * @param saleMode 商品分区 0-商超
     * @return Result<List<ApiShowCategoryVo>>
     */
    @GetMapping("/get/{saleMode}")
    @Operation(summary = "获取对应分区含有商品的展示分类")
    @PreAuthorize("permitAll()")
    public Result<List<ApiShowCategoryVo>> getApiShowCategoryListBySaleMode(@Parameter(description = "商品分区 0-商超", required = true) @PathVariable("saleMode") Long saleMode) {
        List<ApiShowCategoryVo> attributeTemplateVos = showCategoryService.getApiShowCategoryListBySaleMode(saleMode);
        return Result.ok(attributeTemplateVos);
    }
}
