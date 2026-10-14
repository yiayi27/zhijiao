package com.medusa.gruul.goods.task;

import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.goods.service.manager.ISkuStockService;
import com.medusa.gruul.goods.util.GoodUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author alan
 * @since 2023/11/22 21:41
 */
@Slf4j
@Component
public class UpdateSkuTask {

    @Resource
    private ISkuStockService skuStockService;

    /**
     * sku库存定时任务
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    @SuppressWarnings("unchecked")
    public void updateSkuTask() {
        log.info("-----------sku库存定时任务开启-----------");
        List<SkuStock> skuStocks = ISystem.disableTenant(
                () -> skuStockService.lambdaQuery()
                        .select(SkuStock::getId, SkuStock::getStock)
                        .list()
        );
        //redis pipeline batch submit
        RedisUtil.executePipelined(
                redistemplate -> {
                    ValueOperations<String, Object> opsForValue = redistemplate.opsForValue();
                    for (SkuStock skuStock : skuStocks) {
                        opsForValue.set(RedisUtil.key(Keys.GOODS_SKU_STOCK, skuStock.getId()), String.valueOf(skuStock.getStock()));
                    }
                }
        );

        log.info("-----------sku库存定时任务结束-----------");
    }

    /**
     * 购物车选中状态每晚12点定时更新为未选中
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateShoppingCartSelectStatusTask() {
        log.debug("-----------购物车选中状态定时任务开启-----------");
        //每晚12点更新购物车选中状态
        Set<String> keys = RedisUtil.scan(Set.of(GoodUtil.allCartCachePatternKey()));
        //lua脚本 批量更新为未选中状态
        GoodUtil.updateCartSelectStatus(keys, false);
        log.debug("-----------购物车选中状态定时任务结束-----------");
    }
}
