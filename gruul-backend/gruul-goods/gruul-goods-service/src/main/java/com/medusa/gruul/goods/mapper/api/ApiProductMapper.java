package com.medusa.gruul.goods.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 小程序商品信息 Mapper接口
 *
 * @author kyl
 * @since 2023-10-06
 */
@Repository
public interface ApiProductMapper extends BaseMapper<Product> {

    /**
     * 获取商品详情
     *
     * @param id 商品id
     * @return 商品信息
     */
    ApiProductVo queryByPrimaryKey(Long id);

    /**
     * 获取商品列表
     *
     * @param page
     * @param apiProductParam
     * @return 商品list信息
     */
    List<ApiProductVo> queryList(IPage page, @Param("apiProductParam") ApiProductParam apiProductParam);


    /**
     * 获取接龙活动的商品信息
     *
     * @param productType 0-全部商品参与  1-指定商品参与  2-指定商品不参与
     * @param productIds
     * @return List<ApiProductVo>
     */
    List<ApiProductVo> querySolitaireActivityProduct(@Param("productType") Integer productType, @Param("productIds") List<Long> productIds);
}
