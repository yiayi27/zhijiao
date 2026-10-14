package com.medusa.gruul.shops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.shops.api.entity.ShopGuidePage;
import com.medusa.gruul.shops.model.dto.ShopGuidePageDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author create by xiaoq
 * @date created in 2024/10/15
 */
@Repository
public interface ShopGuidePageMapper extends BaseMapper<ShopGuidePage> {
    /**
     * 获取店铺自定义引导页
     *
     * @return
     */
    List<ShopGuidePageDto> selectGuidePage();

    /**
     * 店铺自定义引导页更新
     *
     * @param shopGuidePageDto 店铺自定义引导页Dto
     * @return
     */
    int updateShopGuidePage(@Param("shopGuidePageDto") ShopGuidePageDto shopGuidePageDto);

    /**
     * 获取店铺引导页默认值
     *
     * @return
     */
    List<ShopGuidePageDto> selectGuidePageDefault();


}
