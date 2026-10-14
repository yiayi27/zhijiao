package com.medusa.gruul.shops.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.Shops;
import com.medusa.gruul.shops.api.entity.ShopsSearchTerms;
import com.medusa.gruul.shops.model.param.ShopsSearchTermsParam;
import com.medusa.gruul.shops.model.vo.ShopsSearchTermsVo;

import java.util.List;

/**
 * 热门搜索词汇
 * @author create by zq
 * @date created in 2024/01/14
 */
public interface ShopsSearchTermsService extends IService<ShopsSearchTerms> {


    /**
     * 获取热门词汇
     *
     * @return ShopsSearchTermsVo
     */
    Result<ShopsSearchTermsVo> getTerms();


    /**
     * 新增保存商铺热搜词汇信息
     *
     * @param shopsSearchTermsParam 商铺热搜词汇信息
     * @return Result
     */
    Result<Void> updateTerms(ShopsSearchTermsParam shopsSearchTermsParam);
}
