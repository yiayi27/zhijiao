package com.medusa.gruul.goods.web.controller.manager;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.dto.manager.SupplierDto;
import com.medusa.gruul.goods.api.model.param.manager.ProductParam;
import com.medusa.gruul.goods.api.model.param.manager.SupplierParam;
import com.medusa.gruul.goods.api.model.vo.manager.ProductVo;
import com.medusa.gruul.goods.api.model.vo.manager.SupplierVo;
import com.medusa.gruul.goods.service.manager.IProductService;
import com.medusa.gruul.goods.service.manager.ISupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商 前端控制器
 *
 * @author kyl
 * @since 2023-10-06
 */
@RestController
@RequestMapping("/manager/supplier")
public class SupplierController {
    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private IProductService productService;

    /**
     * 供应商商品列表
     */
    @GetMapping("/product")
    @Operation(summary = "供应商商品列表")
    public Result<PageUtils<ProductVo>> getSupplierProductList(ProductParam productParam) {
        PageUtils<ProductVo> pageUtils = new PageUtils<>(productService.getSupplierProductList(productParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取所有供应商
     */
    @GetMapping("/get/all")
    @Operation(summary = "所有供应商")
    public Result<List<SupplierVo>> getAllSupplierList() {
        List<SupplierVo> supplierVos = supplierService.getAllSupplierList();
        return Result.ok(supplierVos);
    }

    /**
     * 供应商列表
     */
    @GetMapping("/list")
    @Operation(summary = "供应商列表")
    public Result<PageUtils<SupplierVo>> getSupplierList(SupplierParam supplierParam) {
        PageUtils<SupplierVo> pageUtils = new PageUtils<>(supplierService.getSupplierList(supplierParam));
        return Result.ok(pageUtils);
    }


    /**
     * 供应商新增
     */
    @PostMapping("/save")
    @Operation(summary = "供应商保存")
    public Result<Void> addSupplier(@RequestBody @Validated SupplierDto supplierDto) {
        supplierService.addSupplier(supplierDto);
        return Result.ok();
    }

    /**
     * 供应商修改
     */
    @PutMapping("/update")
    @Operation(summary = "供应商修改")
    public Result<Void> updateSupplier(@RequestBody SupplierDto supplierDto) {
        supplierService.updateSupplier(supplierDto);
        return Result.ok();
    }

    /**
     * 供应商审核
     */
    @PutMapping("/check")
    @Operation(summary = "供应商审核")
    public Result<Void> checkSupplier(@RequestBody SupplierDto supplierDto) {
        supplierService.checkSupplier(supplierDto);
        return Result.ok();
    }

    /**
     * 供应商删除
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "供应商删除")
    public Result<Void> deleteSupplierList(@Parameter(description = "供应商ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        supplierService.deleteSupplierList(ids);
        return Result.ok();
    }
}
