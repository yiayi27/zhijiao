package com.medusa.gruul.goods.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ProductSpec;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.goods.api.model.dto.manager.SkuStockDto;
import com.medusa.gruul.goods.api.param.OperateStockDto;

import java.util.List;

/**
 * <p>
 * sku的库存 服务类
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
public interface ISkuStockService extends IService<SkuStock> {

    /**
     * 减少库存
     *
     * @param operateStockDto 商品库存信息
     * @return Boolean
     */
    Boolean subtractStock(OperateStockDto operateStockDto);

    /**
     * 批量减少库存
     *
     * @param operateStockDtoList 商品库存信息
     * @return Boolean
     */
    Boolean batchSubtractStock(List<OperateStockDto> operateStockDtoList);

    /**
     * 批量归还库存
     *
     * @param operateStockDtoList 商品库存信息
     * @return java.lang.Boolean
     */
    Boolean batchRevertStock(List<OperateStockDto> operateStockDtoList);

    /**
     * 新增商品库存信息
     * @param productId 商品id
     * @param skuStockDtos 商品sku库存信息
     * @param specs 商品规格信息
     */
    void saveProductSkuStock(Long productId, List<SkuStockDto> skuStockDtos,List<ProductSpec> specs);

    /**
     * 更新商品库存信息
     * @param productId 商品id
     * @param skuStockDtos 商品sku库存信息
     * @param specs 商品规格信息
     */
    void updateProductSkuStock(Long productId,List<SkuStockDto> skuStockDtos,List<ProductSpec> specs);



}
