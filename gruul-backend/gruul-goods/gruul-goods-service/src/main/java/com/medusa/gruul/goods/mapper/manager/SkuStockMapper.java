package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.goods.api.model.vo.manager.ItemVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * sku的库存 Mapper 接口
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Repository
public interface SkuStockMapper extends BaseMapper<SkuStock> {

    /**
     * 获取单个sku关联商品会员价信息
     *
     * @param skuIds
     * @return ItemVo
     */
    List<ItemVo> queryItemVoByIds(@Param("skuIds") List<Long> skuIds);


}
