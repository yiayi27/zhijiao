package com.medusa.gruul.goods.service.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ProductSpec;
import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecVo;
import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecsAndSkusVo;

import java.util.List;

/**
 * 商品规格查询service
 * @author lcysike
 */
public interface IProductSpecService extends IService<ProductSpec> {

    /**
     * 根据商品id查询规格树结构
     *
     * @param productId 商品id
     * @param parentId  父规格id 规格组为0
     * @return List<ProductSpecVo>
     */
    List<ProductSpecVo> getProductSpecs(Long productId, Long parentId);

    /**
     * 新增规格结构
     * @param productId  商品id
     * @param parentId  父规格id 传0为规格组
     * @param specs 商品规格列表
     */
    void saveProductSpecs(Long productId, Long parentId, List<ProductSpec> specs);


    /**
     * 更新规格结构
     * @param productId  商品id
     * @param parentId  父规格id 传0为规格组
     * @param specs 商品规格列表
     */
    void updateProductSpecs(Long productId, Long parentId, List<ProductSpec> specs);

    /**
     * 获取商品规格列表与商品sku列表
     * @param productId 商品id
     * @return ProductSpecsAndSkusVo
     */
    ProductSpecsAndSkusVo getSpecsAndSkusByProductId(Long productId);

}
