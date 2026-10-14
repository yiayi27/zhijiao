package com.medusa.gruul.goods.web.controller.manager;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecVo;
import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecsAndSkusVo;
import com.medusa.gruul.goods.service.manager.IProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品规格查询
 *
 * @author 张治保
 * date 2024/11/12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/product/{productId}/spec")
public class ProductSpecController {

    private final IProductSpecService productSpecService;

    /**
     * 根据商品id查询规格树结构
     *
     * @param productId 商品id
     * @param specId  父规格id 规格组为0
     * @return Result<List<ProductSpecVo>>
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "查询商品规格列表(树结构)")
    @GetMapping("/{specId}")
    public Result<List<ProductSpecVo>> get(
            @PathVariable @Parameter(description = "商品id", required = true) Long productId,
            @PathVariable @Parameter(description = "规格id,传0查询所有", required = true) Long specId
    ) {
        return Result.ok(
                productSpecService.getProductSpecs(productId, specId)
        );
    }

    /**
     * 获取商品规格列表与商品sku列表
     * @param productId 商品id
     * @return Result<ProductSpecsAndSkusVo>
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/specs-skus")
    @Operation(summary = "查询商品规格列表与Sku信息列表")
    public Result<ProductSpecsAndSkusVo> getSpecsAndSkusByProductId(@PathVariable @Parameter(description = "商品id", required = true) Long productId) {
        return Result.ok(
                productSpecService.getSpecsAndSkusByProductId(productId)
        );
    }

}
