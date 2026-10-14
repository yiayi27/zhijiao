package com.medusa.gruul.common.security.resource.helper;

/**
 * 权限常量类
 *
 * @author 张治保
 * @since 2025/4/9
 */
public interface Perms {

    /**
     * 经营概况
     */
    String OVERVIEW = "overview";

    /**
     * 商品管理
     */
    GoodManager GOOD = new GoodManager() {
    };

    /**
     * 订单管理
     */
    OrderManager ORDER = new OrderManager() {
    };

    /**
     * 营销应用
     */
    MarketingApp MARKETING = new MarketingApp() {
    };

    /**
     * 财务管理
     */
    FinanceManager FINANCE = new FinanceManager() {
    };

    /**
     * 客户管理
     */
    CustomerManager CUSTOMER = new CustomerManager() {
    };

    /**
     * 配送方式
     */
    DeliveryManager DELIVERY = new DeliveryManager() {
    };

    /**
     * 商城设置
     */
    MallSetting MALL_SETTING = new MallSetting() {
    };

    /**
     * 商品管理权限
     */
    interface GoodManager {
        /**
         * 商品列表
         */
        String LIST = "good:list";


        /**
         * 设置专区
         */
        String SET_AREA = "good:set:area";


        /**
         * 属性模板
         */
        String ATTR_TEMPLATE = "good:attr:template";

        /**
         * 供货商
         */
        String SUPPLIER = "good:supplier";

    }


    /**
     * 订单管理
     */
    interface OrderManager {

        /**
         * 快递订单
         */
        String EXPRESS_ORDER = "order:express:order";

        /**
         * 评价管理
         */
        String COMMENT_MANAGER = "order:comment:manager";

        /**
         * 售后工单
         */
        String AFS_ORDER = "order:afs:order";

        /**
         * 储值订单
         */
        String VALUE_ORDER = "order:value:order";
    }

    /**
     * 营销管理
     */
    interface MarketingApp {

        /**
         * 优惠券
         */
        String COUPON = "marketing:coupon";

        /**
         * 满减
         */
        String FULL_REDUCE = "marketing:full:reduce";


        /**
         * 积分商城
         */
        String INTEGRAL = "marketing:integral";

        /**
         * 直播
         */
        String LIVE = "marketing:live";

        /**
         * 商品基金
         */
        String GOOD_FUND = "marketing:good:fund";

        /**
         * 回收返券
         */
        String RECYCLE_COUPON = "marketing:recycle:coupon";

        /**
         * 社区接龙
         */
        String COMMUNITY = "marketing:community";
    }

    /**
     * 财务管理
     */
    interface FinanceManager {

        /**
         * 对账单
         */
        String BILL = "finance:bill";

        /**
         * 提现工单
         */
        String WITHDRAW_ORDER = "finance:withdraw:order";
    }

    /**
     * 客户管理
     */
    interface CustomerManager {

        /**
         * 普通会员
         */
        String MEMBER = "customer:member";

        /**
         * 会员管理
         */
        String MEMBER_MANAGER = "customer:member:manager";

        /**
         * 黑名单
         */
        String BLACKLIST = "customer:blacklist";

    }

    /**
     * 配送方式
     */
    interface DeliveryManager {

        /**
         * 快递配送
         */
        String EXPRESS = "delivery:express";
    }

    /**
     * 商城设置
     */
    interface MallSetting {

        /**
         * 销售渠道
         */
        String SALES_CHANNEL = "mall:sales:channel";

        /**
         * 装修
         */
        String DECORATION = "mall:decoration";

        /**
         * 通用设置
         */
        String COMMON_SETTING = "mall:common:setting";


        /**
         * 权限管理
         */
        String PERMISSION = "mall:permission";
    }
}
