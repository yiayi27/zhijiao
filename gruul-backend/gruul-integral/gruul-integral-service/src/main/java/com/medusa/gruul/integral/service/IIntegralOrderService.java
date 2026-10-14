package com.medusa.gruul.integral.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.api.entity.IntegralOrder;
import com.medusa.gruul.integral.api.model.IntegralOrderDeliveryDto;
import com.medusa.gruul.integral.api.model.IntegralOrderVo;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralOrderDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralReceivingInfoDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralDeliveryFiltrateParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsOrderParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodsOrderVo;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 积分订单表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
public interface IIntegralOrderService extends IService<IntegralOrder> {

    /**
     * 积分订单查询列表
     *
     * @param integralGoodsOrderParam
     * @return
     */
    IPage<IntegralGoodsOrderVo> searchOrders(IntegralGoodsOrderParam integralGoodsOrderParam);

    /**
     * 获取单个订单商品信息
     *
     * @param orderId 订单id
     * @return
     */
    IntegralGoodsOrderVo getIntegralOrderInfo(Long orderId);


    /**
     * 商家批量备注订单
     *
     * @param integralOrders 积分订单id
     * @param backRemark     备注内容
     * @param isCoverage     是否覆盖
     */
    void remarkIntegralOrder(List<Long> integralOrders, String backRemark, Boolean isCoverage);

    /**
     * 创建积分商品订单
     *
     * @param integralOrderDto
     * @return
     */
    String addIntegralOrder(IntegralOrderDto integralOrderDto);


    /**
     * 商家批量收货功能
     *
     * @param orderIds
     */
    void receive(List<Long> orderIds);


    /**
     * 用户支付订单
     *
     * @param prizeOrderId
     * @param request
     * @return
     */
    PayResultDto payIntegralOrder(Long prizeOrderId, HttpServletRequest request);


    /**
     * 完成订单
     *
     * @param prizeOrderId
     */
    void integralOrderDone(Long prizeOrderId);


    /**
     * 删除订单 并归还用户积分
     *
     * @param prizeOrderId
     */
    void delIntegralOrder(Long prizeOrderId);


    /**
     * 进行 积分商品订单批量发货啊
     *
     * @param integralGoodsOrderVos
     */
    void shipments(List<IntegralGoodsOrderVo> integralGoodsOrderVos);


    /**
     * 运费计算
     *
     * @param regionCode        收货地址code
     * @param freightTemplateId 运费模板id
     * @param weight            重量
     * @return 运费
     */
    BigDecimal freight(String regionCode, Long freightTemplateId, BigDecimal weight);


    /**
     * 筛选符合查询条件的订单数据
     *
     * @param integralDeliveryFiltrateParam 查询条件
     * @return
     */
    List<IntegralGoodsOrderVo> filtrate(IntegralDeliveryFiltrateParam integralDeliveryFiltrateParam);

    /**
     * 积分订单打印并发货
     *
     * @param integralOrderDeliveryDtos 发货信息
     * @return
     */
    int doLogisticsIntegralOrderDelivery(List<IntegralOrderDeliveryDto> integralOrderDeliveryDtos);

    /**
     * 积分订单打印发货 所需信息
     *
     * @param integralOrderId
     * @return
     */
    IntegralOrderVo integralOrderInfo(Long integralOrderId);

    /**
     * 订单无需物流发货
     *
     * @param longs 订单id s
     */
    void updateIntegralOrderStatus(List<Long> longs);

    /**
     * 获取所有待发货的订单
     *
     * @return 所有待发货的订单
     */
    List<IntegralGoodsOrderVo> getAllWaitForSendIntegralOrder();

    /**
     * 修改积分订单收货人信息
     *
     * @param integralReceivingInfoDto 收货人信息 地址 姓名 电话号
     */
    void updateReceivingInfo(IntegralReceivingInfoDto integralReceivingInfoDto);

    /**
     * 获取会员用户是否拥有运单权益
     *
     * @param userId 用户id
     * @return Boolean
     */
    Boolean memberInfo(String userId);

    /**
     * 获取积分订单的运单号
     *
     * @param prizeOrderId 订单id
     * @return Object
     */
    Object payIntegralOrderPage(Long prizeOrderId);

}
