package com.medusa.gruul.common.data;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.Systems;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 店铺维护处理器
 * @Author: alan
 * @Date: 2023/7/13 19:32
 */
@Slf4j
public class GruulShopHandler implements TenantLineHandler {
    @Autowired
    private TenantConfigProperties properties;

    /**
     * 获取店铺值
     *
     * @return 店铺值
     */
    @Override
    public Expression getTenantId() {
        String shopId = ISystem.shopId();
        log.debug("当前商铺为 >> {}", shopId);
        return new StringValue(shopId);
    }

    /**
     * 获取店铺字段名
     *
     * @return 店铺字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getShop_column();
    }

    /**
     * 根据表名判断是否进行过滤
     *
     * @param tableName 表名
     * @return 是否进行过滤
     */
    @Override
    public boolean ignoreTable(String tableName) {
        //如果未启用多店铺 或  店铺id未默认值 则 不使用多店铺
        if (!properties.getUse_shop() || Systems.DEFAULT_SHOP_ID.equals(ISystem.shopId())) {
            return true;
        }
        //移除表名可能存在的反引号
        if (tableName.charAt(0) == '`') {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return properties.getTables().contains(tableName);
    }
}
