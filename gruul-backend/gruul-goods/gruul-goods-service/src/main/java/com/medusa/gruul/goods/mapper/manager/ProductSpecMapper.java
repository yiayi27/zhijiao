package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.goods.api.entity.ProductSpec;
import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *商品规格查询mapper
 * @author 张治保
 * 2021年11月12日09:40:07
 */
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {

    /**
     * 根据商品id查询规格树结构
     * @param productId 商品id
     * @param parentId 父规格id 规格组为0
     * @return List<ProductSpecVo>
     */
    List<ProductSpecVo> queryProductSpecsByProductId(@Param("productId") Long productId, @Param("parentId") Long parentId);

}




