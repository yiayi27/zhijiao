package com.medusa.gruul.goods.web.controller.manager;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.dto.manager.ShowCategoryDto;
import com.medusa.gruul.goods.api.model.dto.manager.ShowCategorySecondDto;
import com.medusa.gruul.goods.api.model.param.manager.ShowCategoryParam;
import com.medusa.gruul.goods.api.model.vo.manager.ShowCategoryVo;
import com.medusa.gruul.goods.service.manager.IShowCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 产品展示分类 前端控制器
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@RestController
@RequestMapping("/manager/show/category")
public class ShowCategoryController {

    @Autowired
    private IShowCategoryService showCategoryService;

    /**
     * 产品展示分类列表
     */
    @GetMapping("/list")
    @Operation(summary = "产品展示分类列表")
    public Result<PageUtils<ShowCategoryVo>> getShowCategoryList(ShowCategoryParam showCategoryParam) {
        PageUtils<ShowCategoryVo> pageUtils = new PageUtils<>(showCategoryService.getShowCategoryList(showCategoryParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取所有产品展示分类
     */
    @GetMapping("/get/all")
    @Operation(summary = "所有产品展示分类")
    public Result<List<ShowCategoryVo>> getAllShowCategoryList(ShowCategoryParam showCategoryParam) {
        List<ShowCategoryVo> showCategoryVos = showCategoryService.getAllShowCategoryList(showCategoryParam);
        return Result.ok(showCategoryVos);
    }

    /**
     * 获取单个产品展示分类
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "单个产品展示分类")
    public Result<ShowCategoryVo> getShowCategoryById(@Parameter(description = "展示分类id", required = true) @PathVariable("id") String id) {
        ShowCategoryVo showCategoryVo = showCategoryService.getShowCategoryById(id);
        return Result.ok(showCategoryVo);
    }

    /**
     * 一级产品展示分类新增
     */
    @PostMapping("/save")
    @Operation(summary = "一级产品展示分类保存")
    public Result<Void> addShowCategory(@RequestBody @Validated ShowCategoryDto showCategoryDto) {
        showCategoryService.addShowCategory(showCategoryDto);
        return Result.ok();
    }

    /**
     * 二级产品展示分类新增
     */
    @PostMapping("/save/second")
    @Operation(summary = "二级产品展示分类保存")
    public Result<Void> addSecondList(@RequestBody List<ShowCategorySecondDto> showCategorySecondDtos) {
        showCategoryService.addSecondList(showCategorySecondDtos);
        return Result.ok();
    }

    /**
     * 产品展示分类修改
     */
    @PutMapping("/update")
    @Operation(summary = "产品展示分类修改")
    public Result<Void> updateShowCategory(@RequestBody @Validated ShowCategoryDto showCategoryDto) {
        showCategoryService.updateShowCategory(showCategoryDto);
        return Result.ok();
    }

    /**
     * 产品展示分类删除
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "产品展示分类删除")
    public Result<Void> deleteShowCategoryList(@Parameter(description = "展示分类id", required = true) @PathVariable(name = "id") Long id) {
        showCategoryService.deleteShowCategoryList(id);
        return Result.ok();
    }

    /**
     * 产品展示分类排序
     */
    @PutMapping("/sort")
    @Operation(summary = "产品展示分类排序")
    public Result<Void> updateShowCategorySort(@RequestBody List<ShowCategoryDto> showCategoryDtos) {
        showCategoryService.updateShowCategorySort(showCategoryDtos);
        return Result.ok();
    }

}
