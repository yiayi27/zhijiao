package com.medusa.gruul.shops.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.Shops;
import com.medusa.gruul.shops.mapper.ShopsMapper;
import com.medusa.gruul.shops.model.param.ShopsSearchTermsParam;
import com.medusa.gruul.shops.model.vo.ShopsSearchTermsVo;
import com.medusa.gruul.shops.service.ShopsSearchTermsService;
import com.medusa.gruul.shops.service.ShopsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 热门搜索词汇
 * @author create by zq
 * @date created in 2024/01/14
 */
@Service(value = "shopsServiceImpl")
public class ShopsServiceImpl extends ServiceImpl<ShopsMapper, Shops> implements ShopsService {


    @Autowired
    private ShopsSearchTermsService shopsSearchTermsService;


    /**
     * 获取商铺 前端获取城市信息
     *
     * @return List<Shops>
     */
    @Override
    public List<Shops> listShops() {
        return this.baseMapper.selectList(null);
    }

    @Override
    public Long getMaxId() {
        Shops shop = this.baseMapper.getMaxId();
        Long id = 1L;
        if(shop != null){
            id = shop.getId();
        }
        return id;
    }


    /**
     * 获取热门词汇
     *
     * @return ShopsSearchTermsVo
     */
    @Override
    public Result<ShopsSearchTermsVo> getTerms() {
        return shopsSearchTermsService.getTerms();
    }


    /**
     * 新增保存商铺热搜词汇信息
     *
     * @param shopsSearchTermsParam 商铺热搜词汇信息
     * @return Result
     */
    @Override
    public Result<Void> updateTerms(ShopsSearchTermsParam shopsSearchTermsParam) {
        return shopsSearchTermsService.updateTerms(shopsSearchTermsParam);
    }

    /**
     * 获取shopId
     * @return String
     */
    @Override
    public String getShopId() {
        Shops shop = super.lambdaQuery().one();
        return shop == null?null:shop.getShopId();
    }

}
