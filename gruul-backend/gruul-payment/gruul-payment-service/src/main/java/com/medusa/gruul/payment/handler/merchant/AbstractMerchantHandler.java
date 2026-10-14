package com.medusa.gruul.payment.handler.merchant;

import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.constant.PayConst;
import com.medusa.gruul.payment.common.model.MerchantDetailsDTO;

/**
 * 商户抽象处理器
 *
 * @author 张治保
 * date 2022/1/21
 */

public abstract class AbstractMerchantHandler implements Handler<Void> {


    @Override
    public Void handle(Object... params) {
        if (this.hasErrorParam(params, MerchantDetailsDTO.class)) {
            throw new ServiceException("服务器异常,参数错误");
        }
        MerchantDetailsDTO config = this.cast(params[0], MerchantDetailsDTO.class);
        //校验支付平台是否发生了重复
        //如果查询出的
        String shopId = ISystem.shopId();
        String platformCacheKey = RedisUtil.key(PayConst.PAY_TYPE_CONFIG_CACHE_KEY, shopId);
        //保存新支付平台配置
        save(shopId, config);
        //2. 保存新支付平台配置
        RedisUtil.setCacheMapValue(platformCacheKey, type().name(), config.getDetailsId());
        return null;
    }

    /**
     * 更新或保存支付配置
     *
     * @param shopId 店铺id
     * @param config 支付配置
     */
    protected abstract void save(String shopId, MerchantDetailsDTO config);

    /**
     * 获取支付类型
     *
     * @return 支付类型
     */
    protected abstract PayType type();
}
