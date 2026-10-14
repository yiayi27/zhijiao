package com.medusa.gruul.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
public interface IRemoteOrderService extends IService<Order> {


    /**
     * 订单详情
     *
     * @param orderId the order id
     * @return com.medusa.gruul.order.model.OrderVo order vo
     * @author alan
     * @date 2023/11/25 22:32
     */
    OrderVo orderInfo(Long orderId);


    /**
     * 获取商品评价
     *
     * @param productIds the product ids
     * @return java.util.List<com.medusa.gruul.order.api.model.ProductRateVo> list
     * @author alan
     * @date 2024/2/7 20:22
     */
    List<ProductRateVo> productRate(List<Long> productIds);

    /**
     * 获取未生成发货单的订单
     *
     * @param param the param
     * @return java.util.List<com.medusa.gruul.order.api.model.GetOrderListDto> not shipped order
     * @author alan
     * @date 2023/12/12 21:11
     */
    List<GetOrderListDto> getNotShippedOrder(GetOrderListDtoByTimeScope param);

    /**
     * 根据订单Id获取指定订单详情
     *
     * @param param the param
     * @return java.util.List<com.medusa.gruul.order.api.model.GetOrderListDto> order list by ids
     * @author alan
     * @date 2023/12/12 21:11
     */
    List<GetOrderListDto> getOrderListByIds(GetOrderListParam param);


    /***
     * 物流订单发货
     * @param orderDeliveryDtos the order delivery dtos
     * @return int
     */
    int doLogisticsOrderDelivery(List<OrderDeliveryDto> orderDeliveryDtos);

    /**
     * 活动统计
     *
     * @param activityIds the activity ids
     * @return java.util.List<com.medusa.gruul.order.api.model.ActivityStatisticsVo> list
     */
    List<ActivityStatisticsVo> activityStatisticsByActivityId(Long[] activityIds);

    /**
     * 获取指定商品最后购买人的信息
     *
     * @param productIds the product ids
     * @return java.util.List<com.medusa.gruul.order.api.model.ProductBuyerVo> product last buyers
     * @author alan
     * @date 2024/6/15 20:56
     */
    List<ProductBuyerVo> getProductLastBuyers(String[] productIds);

    /**
     * 等待发货的商品ID
     *
     * @param sendBillId the send bill id
     * @return java.util.List<java.lang.Long> list
     * @Author alan
     * @Date 2024/6/20 11:09 AM
     */
    List<Long> waitSendProductList(String sendBillId);

    /**
     * 获取订单设置
     *
     * @return the order setting
     */
    OrderSetting getOrderSetting();

    /**
     * 根据订单ID获取订单详情
     *
     * @param orderIds the order ids
     * @return the list
     */
    List<OrderVo> orderInfoList(List<Long> orderIds);

    /**
     * 创建一个换货单
     *
     * @param dto the dto
     * @return the long
     */
    Long createExchangeOrder(ExchangeOrderDto dto);

    /**
     * 关闭订单
     *
     * @param afsId        the afs id
     * @param refundAmount the refund amount
     * @param type         the type
     * @param orderId      the order id
     */
    void closeOrder(Long afsId, BigDecimal refundAmount, Integer type, Long orderId);

    /**
     * 关闭换货单
     *
     * @param orderIds the order ids
     */
    void closeExchangeOrder(List<Long> orderIds);

    /**
     * 统计满减活动的销售额
     *
     * @param fullScaleIds the full scale ids
     * @return the list
     */
    List<ActivityStatisticsVo> fullScaleStatisticsByActivityId(Long[] fullScaleIds);

    /**
     * 根据订单ID和SkuId查询指定商品有没有售后信息
     *
     * @param orderId      the order id
     * @param productSkuId the product sku id
     * @return the afs order
     */
    AfsOrder selectByOrderIdAndProductSkuId(Long orderId, Long productSkuId);

    /**
     * 检查订单是否有真正进行中的售后
     *
     * @param orderIds
     * @return java.lang.Boolean
     * @author alan
     * @date 2024/3/17 21:49
     */
    Boolean checkAfsOrder(List<Long> orderIds);

    /**
     * 更新订单到指定状态
     *
     * @param orderIds
     * @param statusEnum
     * @return java.lang.Boolean
     * @author alan
     * @date 2024/3/17 21:49
     */
    Boolean updateOrderStatus(List<Long> orderIds, OrderStatusEnum statusEnum);

    /**
     *设置订单业务流水号
     * @param orderId 订单id
     * @param transactionId 业务流水号
     */
    void setOrderTransactionId(Long orderId,String transactionId);

    /**
     * 归还库存
     * @param type 库存类型 1-普通库存 2-虚拟库存
     * @param skuId sku id
     * @param quantity 归还数量
     */
    void stockReturn(Integer type ,Long skuId,int quantity);
}
