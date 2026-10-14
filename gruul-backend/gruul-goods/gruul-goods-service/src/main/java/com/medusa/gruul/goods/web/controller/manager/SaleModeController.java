package com.medusa.gruul.goods.web.controller.manager;



import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.dto.manager.SaleModeDto;
import com.medusa.gruul.goods.api.model.param.manager.SaleModeParam;
import com.medusa.gruul.goods.api.model.vo.manager.SaleModeVo;
import com.medusa.gruul.goods.service.manager.ISaleModeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品自定义专区 前端控制器
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@RestController
@RequestMapping("/manager/sale/mode")
public class SaleModeController {

    @Autowired
    private ISaleModeService saleModeService;

    /**
     * 获取商品专区列表
     */
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    @Operation(summary = "商品专区列表")
    public Result<PageUtils<SaleModeVo>> getSaleModeList(SaleModeParam saleModeParam) {
        PageUtils<SaleModeVo> pageUtils = new PageUtils<>(saleModeService.getSaleModeList(saleModeParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取所有商品专区列表
     */
    @GetMapping("/get/all")
    @PreAuthorize("permitAll()")
    @Operation(summary = "商品所有专区列表")
    public Result<List<SaleModeVo>> getAllSaleModeList() {
        List<SaleModeVo> saleModeVos = saleModeService.getAllSaleModeList();
        return Result.ok(saleModeVos);
    }

    /**
     * 商品专区新增
     */
    @PostMapping("/save")
    @Operation(summary = "商品专区新增")
    public Result<Void> addSaleMode(@RequestBody @Validated List<SaleModeDto> saleModeDtos) {
        saleModeService.addSaleMode(saleModeDtos);
        return Result.ok();
    }

    /**
     * 商品专区修改
     */
    @PutMapping("/update")
    @Operation(summary = "商品专区修改")
    public Result<Void> updateSaleMode(@RequestBody @Validated SaleModeDto saleModeDto) {
        saleModeService.updateSaleMode(saleModeDto);
        return Result.ok();
    }

    /**
     * 商品专区删除
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "商品专区删除")
    public Result<Void> deleteShowCategoryList(@Parameter(description = "展示分类id", required = true) @PathVariable(name = "id") Long id) {
        saleModeService.deleteSaleMode(id);
        return Result.ok();
    }

    /**
     * 商品专区排序
     */
    @PutMapping("/sort")
    @Operation(summary = "商品专区排序")
    public Result<Void> updateSaleModeSort(@RequestBody List<SaleModeDto> saleModeDtos) {
        saleModeService.updateSaleModeSort(saleModeDtos);
        return Result.ok();
    }

}
