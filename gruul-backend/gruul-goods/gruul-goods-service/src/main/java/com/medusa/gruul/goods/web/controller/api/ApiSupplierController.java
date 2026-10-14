package com.medusa.gruul.goods.web.controller.api;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.dto.api.ApiSupplierDto;
import com.medusa.gruul.goods.service.api.IApiSupplierService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序供应商 前端控制器
 *
 * @author: KYL
 * @since: 2023/11/13
 */
@RestController
@RequestMapping("/api/supplier")
public class ApiSupplierController {

    @Autowired
    private IApiSupplierService supplierService;

    /**
     * 供应商新增
     * @param supplierDto 供应商信息
     * @return Result<Void>
     */
    @PostMapping("/save")
    @Operation(summary = "供应商申请")
    public Result<Void> addSupplier(@RequestBody @Validated ApiSupplierDto supplierDto) {
        supplierService.addSupplier(supplierDto);
        return Result.ok();
    }

}
