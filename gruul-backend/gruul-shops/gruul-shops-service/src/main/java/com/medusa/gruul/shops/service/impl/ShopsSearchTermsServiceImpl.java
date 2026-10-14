package com.medusa.gruul.shops.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.shops.api.entity.ShopsSearchTerms;
import com.medusa.gruul.shops.mapper.ShopsSearchTermsMapper;
import com.medusa.gruul.shops.model.param.ShopsSearchTermsParam;
import com.medusa.gruul.shops.model.vo.ShopsSearchTermsVo;
import com.medusa.gruul.shops.properties.GlobalConstant;
import com.medusa.gruul.shops.service.ShopsSearchTermsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * 热门搜索词汇
 * @author create by zq
 * @date created in 2024/01/14
 */
@Service(value = "shopsSearchTermsServiceImpl")
public class ShopsSearchTermsServiceImpl extends ServiceImpl<ShopsSearchTermsMapper, ShopsSearchTerms> implements ShopsSearchTermsService {


    /**
     * 获取热门词汇
     *
     * @return ShopsSearchTermsVo
     */
    @Override
    public Result<ShopsSearchTermsVo> getTerms() {
        return Result.ok(this.innerGetCache());
    }


    /**
     * 新增保存商铺热搜词汇信息
     *
     * @param param 商铺热搜词汇信息
     * @return Result
     */
    @Override
    public Result<Void> updateTerms(ShopsSearchTermsParam param) {
        if (null == param.getId()) {
            ShopsSearchTerms shopsSearchTerms = new ShopsSearchTerms();
            BeanUtil.copyProperties(param, shopsSearchTerms);
            if (this.baseMapper.insert(shopsSearchTerms) < 1) {
                log.error(String.format("新增 商铺热搜词汇信息失败! 参数 : %s", param));
                return Result.failed();
            }
            return Result.ok();
        }

        ShopsSearchTerms shopsSearchTerms = new ShopsSearchTerms();
        BeanUtil.copyProperties(param, shopsSearchTerms);
        if (!this.updateById(shopsSearchTerms)) {
            log.error(String.format("修改 商铺热搜词汇信息失败! 参数 : %s", param));
            return Result.failed();
        }
        //删除缓存装修信息
        RedisUtil.delete(RedisUtil.key(Keys.SHOPS_RENOVATION, ISecurity.userMust().getShopId(), GlobalConstant.STRING_SHOP_SEARCH_TERMS));
        return Result.ok();
    }

    /**
     * 获取热门词汇
     *
     * @return ShopsSearchTermsVo
     */
    private ShopsSearchTermsVo innerGetCache() {
        Optional<SecureUser<Object>> userOpt = ISecurity.userOpt();
        if (userOpt.isEmpty()) {
            return null;
        }
        String shopId = userOpt.get().getShopId();
        String cache = RedisUtil.getCacheObject(RedisUtil.key(Keys.SHOPS_RENOVATION, shopId, GlobalConstant.STRING_SHOP_SEARCH_TERMS));
        if (StringUtils.isBlank(cache)) {
            ShopsSearchTermsVo shopsSearchTermsVo = this.baseMapper.getTerms();
            RedisUtil.setCacheObject(RedisUtil.key(Keys.SHOPS_RENOVATION, shopId, GlobalConstant.STRING_SHOP_SEARCH_TERMS), JSONUtil.toJsonStr(shopsSearchTermsVo));
            return shopsSearchTermsVo;
        }
        return JSONUtil.toBean(cache, ShopsSearchTermsVo.class);
    }

}
