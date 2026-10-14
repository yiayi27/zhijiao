package com.medusa.gruul.goods.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 小程序商品信息 Mapper接口
 *
 * @author lcy
 * @since 2024-08-17
 */
@Repository
public interface ApiProductSkuMapper extends BaseMapper<Product> {

    /**
     * 获取接龙活动的商品信息
     *
     * @param page
     * @param productType 0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds
     * @param distributionMode
     * @return List<ApiProductVo>
     */
    List<ApiProductVo> querySolitaireActivityProductByParam(IPage page, @Param("productType") Integer productType,
                                                            @Param("productIds") Long[] productIds,
                                                            @Param("distributionMode") Integer distributionMode);
}
