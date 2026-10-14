package com.medusa.gruul.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.MemberBalanceUpdateDto;
import com.medusa.gruul.account.api.model.MemberCardInfoCenterDto;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.api.enums.AfsOrderStatusEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderTypeEnum;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.finance.api.feign.RemoteFinanceService;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.param.OperateStockDto;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.order.api.constant.OrderQueueNameConstant;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderDelivery;
import com.medusa.gruul.order.api.entity.OrderItem;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import com.medusa.gruul.order.api.enums.PayTypeEnum;
import com.medusa.gruul.order.api.model.*;
import com.medusa.gruul.order.mapper.*;
import com.medusa.gruul.order.model.ActivityStatisticsDto;
import com.medusa.gruul.order.mq.Sender;
import com.medusa.gruul.order.service.IRemoteOrderService;
import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDto;
import com.medusa.gruul.platform.api.feign.RemoteMiniInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
@Slf4j
@Service
public class RemoteOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IRemoteOrderService {
    @Resource
    private OrderProductEvaluateMapper orderProductEvaluateMapper;
    @Resource
    private OrderEvaluateMapper orderEvaluateMapper;
    @Resource
    private OrderDeliveryMapper orderDeliveryMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private AfsOrderMapper afsOrderMapper;
    @Resource
    private RemoteFinanceService financeService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderSettingMapper orderSettingMapper;
    @Resource
    private RemotePaymentService remotePaymentService;
    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;
    @Resource
    private RemoteIntegralService remoteIntegralService;
    @Resource
    private Sender sender;
    @Resource
    private RemoteGoodsService remoteGoodsService;
    @Resource
    private RemoteMiniInfoService remoteMiniInfoService;


    @Override
    public OrderVo orderInfo(Long orderId) {
        OrderVo vo = baseMapper.selectOrderVoById(orderId);
        vo.setProductTotalQuantity(vo.getOrderItemList().stream().mapToInt(OrderItem::getProductQuantity).sum());
        return vo;
    }

    @Override
    public List<ProductRateVo> productRate(List<Long> productIds) {
        return orderProductEvaluateMapper.selectProductRateByList(productIds);
    }

    @Override
    public List<GetOrderListDto> getNotShippedOrder(GetOrderListDtoByTimeScope param) {
        List<GetOrderListDto> dtoList = baseMapper.selectNotShippedOrder(param.getStart(), param.getEnd(),
                param.getShopId());
        dtoList = dtoList.stream().filter(dto -> CollUtil.isEmpty(dto.getAfsOrderList())).toList();
        return dtoList;
    }

    @Override
    public List<GetOrderListDto> getOrderListByIds(GetOrderListParam param) {
        if (param.getOrderIds().isEmpty()) {
            return new ArrayList<>();
        }
        return baseMapper.selectOrderListByIds(param.getOrderIds(), param.getShopId());
    }


    @Override
    public List<ActivityStatisticsVo> activityStatisticsByActivityId(Long[] activityIds) {
        List<Long> activityIdList = Arrays.asList(activityIds);
        if (activityIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<ActivityStatisticsVo> vos = new ArrayList<>();
        List<ActivityStatisticsDto> statisticsDtoList = orderItemMapper.selectListByActivityIds(activityIdList);
        Map<Long, List<ActivityStatisticsDto>> statisticsDtoMap =
                statisticsDtoList.stream().collect(Collectors.groupingBy(e -> e.getActivityId()));
        for (Long activityId : statisticsDtoMap.keySet()) {
            List<ActivityStatisticsDto> activityStatisticsDtoList = statisticsDtoMap.get(activityId);
            if (!activityStatisticsDtoList.isEmpty()) {
                ActivityStatisticsVo vo = new ActivityStatisticsVo();
                vo.setGaapTotal(activityStatisticsDtoList.stream().map(ActivityStatisticsDto::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                vo.setPayOrder(activityStatisticsDtoList.stream().map(ActivityStatisticsDto::getId).collect(Collectors.toSet()).size());
                vo.setPeopleNum(activityStatisticsDtoList.stream().map(ActivityStatisticsDto::getUserId).collect(Collectors.toSet()).size());
                vo.setActivityId(activityId);
                vos.add(vo);
            }
        }
        return vos;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int doLogisticsOrderDelivery(List<OrderDeliveryDto> orderDeliveryDtos) {
        AtomicInteger res = new AtomicInteger(0);
        OrderSetting orderSetting = orderSettingMapper.selectOne(null);
        orderDeliveryDtos.forEach(orderDeliveryDto -> {
            OrderDto orderDto = baseMapper.selectOneById(orderDeliveryDto.getOrderId());
            if (null == orderDto) {
                throw new ServiceException(SystemCode.DATA_NOT_EXIST);
            }
            orderDto.setStatus(OrderStatusEnum.WAIT_FOR_PICKUP.getCode());
            orderDto.setUpdateTime(LocalDateTime.now());
            int i = baseMapper.updateOneById(orderDto);

            OrderDeliveryDto orderDeliveryDb = orderDeliveryMapper.selectOneByOrderId(orderDeliveryDto.getOrderId());
            if (null == orderDeliveryDb) {
                throw new ServiceException(SystemCode.DATA_NOT_EXIST);
            }
            orderDeliveryDto.setDeliveryCompany(orderDeliveryDto.getDeliveryCompany());
            orderDeliveryDto.setDeliverySn(orderDeliveryDto.getDeliverySn());
            orderDeliveryDto.setDeliveryCode(orderDeliveryDto.getDeliveryCode());
            orderDeliveryDto.setUpdateTime(LocalDateTime.now());
            orderDeliveryDto.setDeliveryTime(LocalDateTime.now());
            int i1 = orderDeliveryMapper.updateByParam(orderDeliveryDto);
            //查询会员持有的积分、收货地址
            OrderVo vo = orderMapper.selectOrderVoById(orderDto.getId());
            sender.sendDeliveryMessage(vo);
            sender.sendShippedOrderMessage(vo);
            BaseOrderMessage baseOrderMessage = new BaseOrderMessage();
            baseOrderMessage.setOrderId(vo.getId());
            baseOrderMessage.setShopId(vo.getShopId());
            baseOrderMessage.setShopId(vo.getShopId());
            sender.sendAutoReceiptOrderMessage(baseOrderMessage,
                    orderSetting.getConfirmOvertime() * TimeConstants.ONE_DAY);
            res.set(i + i1);
            //微信支付情况下调用小程序发货信息录入接口
            if (PayTypeEnum.WECHAT.equals(vo.getPayType())) {
                Result<Boolean> booleanResult = remoteMiniInfoService.uploadShippingInfo(
                        new UploadShippingInfoDTO()
                                .setOrder(vo)
                                .setOrderDelivery(orderDeliveryDto)
                );
                if (booleanResult.isSuccess() && booleanResult.getData()) {
                    log.info("小程序发货信息录入成功");
                } else {
                    log.error("小程序发货信息录入失败");
                    throw new ServiceException("小程序发货信息录入失败!");
                }
            }
        });
        return res.get();
    }

    @Override
    public List<ProductBuyerVo> getProductLastBuyers(String[] productIds) {
        List<ProductBuyerVo> vos = new ArrayList<>();
        for (String productId : productIds) {
            ProductBuyerVo vo = new ProductBuyerVo();
            vo.setProductId(productId);
            List<BuyerVo> buyerVos = baseMapper.getProductLastBuyers(productId);
            vo.setBuyerList(buyerVos);
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 查询待发货商品id列表
     *
     * @return java.util.List<java.lang.Long>
     */
    @Override
    public List<Long> waitSendProductList(String sendBillId) {
        return baseMapper.waitSendProductList(sendBillId);
    }

    /**
     * 获取订单设置信息
     *
     * @return java.util.List<java.lang.Long>
     */
    @Override
    public OrderSetting getOrderSetting() {
        return orderSettingMapper.selectOne(null);
    }

    /**
     * 获取订单信息
     *
     * @return java.util.List<java.lang.Long>
     */
    @Override
    public List<OrderVo> orderInfoList(List<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return CollUtil.newArrayList();
        }
        List<OrderVo> vos = baseMapper.selectOrderVoListByIds(orderIds);
        return vos;
    }

    /**
     * 生成换货单
     *
     * @param dto
     * @return java.lang.Long
     */
    @Override
    public Long createExchangeOrder(ExchangeOrderDto dto) {
        OrderVo orderVo = baseMapper.selectOrderVoById(dto.getOrderId());
        //生成订单
        Order exchangeOrder = new Order();

        exchangeOrder.setId(IdWorker.getId());
        exchangeOrder.setUserId(orderVo.getUserId());
        exchangeOrder.setUserName(orderVo.getUserName());
        exchangeOrder.setUserAvatarUrl(orderVo.getUserAvatarUrl());
        exchangeOrder.setUserNote("");
        if (dto.getPointApply()) {
            exchangeOrder.setType(OrderTypeEnum.REPLENISH);
        } else {
            exchangeOrder.setType(OrderTypeEnum.EXCHANGE);
        }
        exchangeOrder.setTotalAmount(BigDecimal.ZERO);
        exchangeOrder.setDiscountsAmount(BigDecimal.ZERO);
        exchangeOrder.setPayAmount(BigDecimal.ZERO);
        exchangeOrder.setFreightAmount(BigDecimal.ZERO);
        exchangeOrder.setPromotionAmount(BigDecimal.ZERO);
        exchangeOrder.setIntegration(BigDecimal.ZERO);
        exchangeOrder.setIntegrationAmount(BigDecimal.ZERO);
        exchangeOrder.setCouponId(null);
        exchangeOrder.setCouponAmount(BigDecimal.ZERO);
        exchangeOrder.setSolitaireActivityId(null);
        exchangeOrder.setFullScaleId(null);
        exchangeOrder.setFullScaleAmount(BigDecimal.ZERO);
        exchangeOrder.setMemberAmount(BigDecimal.ZERO);
        exchangeOrder.setPayType(PayTypeEnum.FREE);
        exchangeOrder.setTransactionId(null);
        exchangeOrder.setPayTime(LocalDateTime.now());
        exchangeOrder.setSourceType(orderVo.getSourceType());
        exchangeOrder.setStatus(OrderStatusEnum.WAIT_FOR_SEND);
        exchangeOrder.setNote("");
        exchangeOrder.setMemberTemplateId(orderVo.getMemberTemplateId());
        exchangeOrder.setCustomForm(orderVo.getCustomForm());
        exchangeOrder.setPrivileges(orderVo.getPrivileges());
        exchangeOrder.setRebateAmount(BigDecimal.ZERO);
        exchangeOrder.setRefundAmount(BigDecimal.ZERO);
        exchangeOrder.setFirstDistributionAmount(BigDecimal.ZERO);
        exchangeOrder.setSecondDistributionAmount(BigDecimal.ZERO);
        baseMapper.insert(exchangeOrder);


        //生成订单明细
        OrderItem orderItem =
                orderVo.getOrderItemList().stream()
                        .filter(o -> o.getProductSkuId().equals(dto.getProductSkuId()))
                        .findFirst().get();
        OrderItem exchangeOrderItem = new OrderItem();

        exchangeOrderItem.setOrderId(exchangeOrder.getId());
        exchangeOrderItem.setProductId(orderItem.getProductId());
        exchangeOrderItem.setProductPic(orderItem.getProductPic());
        exchangeOrderItem.setProductName(orderItem.getProductName());
        exchangeOrderItem.setProductSn(orderItem.getProductSn());
        exchangeOrderItem.setProductPrice(orderItem.getProductPrice());
        exchangeOrderItem.setBrokerageAmount(BigDecimal.ZERO);
        exchangeOrderItem.setProductOriginalPrice(orderItem.getProductOriginalPrice());
        exchangeOrderItem.setProductQuantity(dto.getProductQuantity());
        exchangeOrderItem.setProductSkuId(orderItem.getProductSkuId());
        exchangeOrderItem.setProductSkuCode(orderItem.getProductSkuCode());
        exchangeOrderItem.setPromotionAmount(BigDecimal.ZERO);
        exchangeOrderItem.setCouponAmount(BigDecimal.ZERO);
        exchangeOrderItem.setIntegrationAmount(BigDecimal.ZERO);
        exchangeOrderItem.setMemberAmount(BigDecimal.ZERO);
        exchangeOrderItem.setRealAmount(orderItem.getRealAmount());
        exchangeOrderItem.setSpecs(orderItem.getSpecs());
        exchangeOrderItem.setActivityId(orderItem.getActivityId());
        exchangeOrderItem.setActivityProductId(orderItem.getActivityProductId());
        exchangeOrderItem.setProviderId(orderItem.getProviderId());
        exchangeOrderItem.setRebateAmount(BigDecimal.ZERO);
        exchangeOrderItem.setFirstDistributionAmount(BigDecimal.ZERO);
        exchangeOrderItem.setSecondDistributionAmount(BigDecimal.ZERO);
        orderItemMapper.insert(exchangeOrderItem);

        //生成订物流信息
        OrderDelivery orderDelivery =
                orderVo.getOrderDelivery();
        OrderDelivery exchangeOrderDelivery = new OrderDelivery();
        exchangeOrderDelivery.setOrderId(exchangeOrder.getId());
        exchangeOrderDelivery.setDeliveryType(orderDelivery.getDeliveryType());
        exchangeOrderDelivery.setReceiverName(orderDelivery.getReceiverName());
        exchangeOrderDelivery.setReceiverPhone(orderDelivery.getReceiverPhone());
        exchangeOrderDelivery.setReceiverPostCode(orderDelivery.getReceiverPostCode());
        exchangeOrderDelivery.setReceiverProvince(orderDelivery.getReceiverProvince());
        exchangeOrderDelivery.setReceiverCity(orderDelivery.getReceiverCity());
        exchangeOrderDelivery.setReceiverRegion(orderDelivery.getReceiverRegion());
        exchangeOrderDelivery.setReceiverDetailAddress(orderDelivery.getReceiverDetailAddress());
        exchangeOrderDelivery.setDeliveryTemplateId(orderDelivery.getDeliveryTemplateId());
        orderDeliveryMapper.insert(exchangeOrderDelivery);
        return exchangeOrder.getId();
    }

    /**
     * 关闭订单
     *
     * @param afsId        售后id
     * @param refundAmount 退款金额
     * @param type         售后类型
     * @param orderId      订单id
     */
    @Override
    public void closeOrder(Long afsId, BigDecimal refundAmount, Integer type, Long orderId) {
        OrderVo order = baseMapper.selectOrderVoById(orderId);
        log.info("订单数据为" + JSONUtil.toJsonStr(order));
        AfsOrder applyOrderItem = afsOrderMapper.selectById(afsId);
        order.setRefundAmount(NumberUtil.add(order.getRefundAmount(), refundAmount));
        if (NumberUtil.isLess(order.getPayAmount(), order.getRefundAmount())) {
            throw new ServiceException("退款金额不得大于支付金额");
        }
        int refundQuantity = 0;
        for (OrderItemVo orderItemVo : order.getOrderItemList()) {
            log.info("orderItemVo is {}", JSONUtil.toJsonStr(orderItemVo));
            log.info("applyOrderItem is {}", JSONUtil.toJsonStr(applyOrderItem));
            if (orderItemVo.getProductSkuId().equals(applyOrderItem.getProductSkuId())) {
                orderItemVo.setRefundAmount(refundAmount);
                orderItemMapper.updateById(orderItemVo);
                refundQuantity = refundQuantity + orderItemVo.getProductQuantity();
            }
        }
        order.setRefundQuantity(refundQuantity);
        //退库存
        stockReturn(type, applyOrderItem.getProductSkuId(), refundQuantity);
        //退费用
        payRefund(refundAmount, order);
        //退积分
        integralReturn(applyOrderItem, order);
        //针于退款，只要没钱了就关闭订单
        if (type.equals(AfsOrderTypeEnum.REFUND.getCode()) || type.equals(AfsOrderTypeEnum.POINT_REFUND.getCode()) || type.equals(AfsOrderTypeEnum.RETURN_REFUND.getCode())) {
            log.info("正在进行订单关闭判断!............");
            //查询订单商品类别 根据商品的类别计算售后成功次数来关闭订单
            List<OrderItemVo> orderItemList = order.getOrderItemList();
            int count = orderItemList.size();
            //当前申请商品的skuId
            Long productSkuId = applyOrderItem.getProductSkuId();
            //排除当前的商品，查询以前已经售后成功的次数来关闭订单
            List<Long> skuIds = orderItemList.stream().filter(item ->
                    !item.getProductSkuId().equals(productSkuId)
            ).map(item -> item.getProductSkuId()).toList();
            long sucCount = 0;
            if (CollUtil.isNotEmpty(skuIds)) {
                LambdaQueryWrapper<AfsOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper
                        .likeRight(AfsOrder::getNo, orderId)
                        .in(AfsOrder::getProductSkuId, skuIds)
                        .eq(AfsOrder::getStatus, AfsOrderStatusEnum.SUCCESS.getCode())
                        .in(AfsOrder::getType, Arrays.asList(
                                AfsOrderTypeEnum.POINT_REFUND,
                                AfsOrderTypeEnum.REFUND,
                                AfsOrderTypeEnum.RETURN_REFUND));
                sucCount = afsOrderMapper.selectCount(lambdaQueryWrapper);
            }
            if (count == 1 || sucCount == (count - 1)) {
                // 订单关闭成功订阅
                sender.sendCloseOrderMessage(order);
                // 订单退货或全额退款
                order.setStatus(OrderStatusEnum.REFUNDED);
                order.setCloseTime(LocalDateTime.now());
            }
            baseMapper.updateById(order);


        }
        sender.sendReturnOrderMessage(order);
        sender.sendRefundSuccess(order);
        //针于换货，没有正在进行的售后就关闭
        if (type.equals(AfsOrderTypeEnum.EXCHANGE.getCode()) || type.equals(AfsOrderTypeEnum.POINT_EXCHANGE.getCode())) {
            List<AfsOrder> afsOrderList = afsOrderMapper.selectProgressByOrderId(order.getId());
            afsOrderList =
                    afsOrderList.stream().filter(afsOrder -> !afsOrder.getId().equals(afsId)).toList();
            boolean hasNotApplyOrderItem =
                    order.getOrderItemList().stream().filter(orderItemVo -> ObjectUtil.isNotNull(orderItemVo.getRefundAmount())).anyMatch(orderItemVo -> NumberUtil.isGreater(orderItemVo.getRefundAmount(), BigDecimal.ZERO));
            if (afsOrderList.isEmpty() || !hasNotApplyOrderItem) {
                sender.sendCloseOrderMessage(order);
                order.setStatus(OrderStatusEnum.EXCHANGE_SUCCESS_CLOSE);
                order.setCloseTime(LocalDateTime.now());
            }
            baseMapper.updateById(order);

        }

    }

    /**
     * 归还库存
     *
     * @param type     工单类型
     * @param skuId    skuid
     * @param quantity 归还的库存数量
     * @author 张治保
     */
    @Override
    public void stockReturn(Integer type, Long skuId, int quantity) {
        if (quantity <= 0 || Objects.equals(type, AfsOrderTypeEnum.POINT_EXCHANGE.getCode())) {
            return;
        }
        Boolean success = remoteGoodsService.batchRevertStock(
                Collections.singletonList(
                        OperateStockDto.builder()
                                .skuId(skuId)
                                .number(quantity)
                                .build()
                )
        );
        if (!success) {
            throw new ServiceException("库存归还失败，请稍后重试");
        }
    }

    /**
     * 归还积分
     *
     * @param applyOrderItem 申请单
     * @param order          订单
     */
    private void integralReturn(AfsOrder applyOrderItem, OrderVo order) {
        Map<Long, OrderItem> orderItemMap =
                order.getOrderItemList().stream().collect(Collectors.toMap(OrderItemVo::getProductSkuId, o -> o));
        OrderItem orderItem = orderItemMap.get(applyOrderItem.getProductSkuId());
        log.info("applyOrderItem:{}", applyOrderItem);
        log.info("order:{}", order);

        if (ObjectUtil.isNotNull(orderItem)) {
            OrderSetting orderSetting = orderSettingMapper.selectOne(null);
            //归还 根据积分优惠的金额算出使用多少积分
            if (NumberUtil.isGreater(orderItem.getIntegrationAmount(), BigDecimal.ZERO)) {
                BigDecimal returnIntegral = NumberUtil.div(orderItem.getIntegrationAmount(),
                        NumberUtil.div(orderSetting.getRatio(), 100), 2);
                log.info("input:{},{}", order.getUserId(), returnIntegral);
                Boolean integralSuccess = remoteIntegralService.integralReturn(order.getUserId(), returnIntegral);
                if (!integralSuccess) {
                    throw new ServiceException("失败，请稍后重试");
                }
            }
            //扣除赠送的积分
            log.info("扣除赠送的积分");
            if (NumberUtil.isGreater(orderItem.getGiftIntegration(), BigDecimal.ZERO)) {
                log.info("input:{},{}", order.getUserId(), orderItem.getGiftIntegration().negate());
                Boolean integralSuccess = remoteIntegralService.integralReturn(order.getUserId(),
                        orderItem.getGiftIntegration().negate());
                log.info("积分归还返回状态 ： " + integralSuccess);
                if (!integralSuccess) {
                    throw new ServiceException("取消赠送积分失败，请稍后重试");
                }
            }
        }
    }

    /**
     * 退款
     *
     * @param refundAmount 退款金额
     * @param order        订单
     */
    private void payRefund(BigDecimal refundAmount, Order order) {
        boolean success = false;
        if (NumberUtil.isGreater(refundAmount, BigDecimal.ZERO)) {
            switch (order.getPayType()) {
                case BALANCE:
                    success = balancePayRefund(refundAmount, order);
                    break;
                case ALIPAY:
                case WECHAT:
                case FRIEND:
                    success = wechatPayRefund(refundAmount, order);
                    break;
                default:
                    break;
            }
        } else if (NumberUtil.equals(refundAmount, BigDecimal.ZERO)) {
            success = true;
        }
        if (!success) {
            throw new ServiceException("退款失败");
        }

    }

    /**
     * 微信退款
     *
     * @param refundAmount 退款金额
     * @param order        订单
     * @return boolean
     */
    private boolean wechatPayRefund(BigDecimal refundAmount, Order order) {
        RefundRequestDto dto = new RefundRequestDto();
        try {
            dto.setShopId(order.getShopId());
            dto.setOrderId(order.getTransactionId());
            dto.setRouteKey(OrderQueueNameConstant.REFUND_NOTIFY);
            //没有发货的订单退掉运费
           /* if (order.getStatus().equals(OrderStatusEnum.WAIT_FOR_SEND)) {
                refundAmount = refundAmount.add(order.getFreightAmount());
            }*/
            dto.setTotalFee(NumberUtil.mul(refundAmount, 100).intValue());
            Result result = remotePaymentService.payRefund(dto);
            log.info("微信/支付宝退款,入参为: {}", JSONUtil.toJsonStr(dto));
            log.info("微信/支付宝退款,出参为: {}", JSONUtil.toJsonStr(result));
            return result != null && result.getCode() == 200;
        } catch (Exception e) {
            log.error("调用退款接口失败：入参为：{}", JSONUtil.toJsonStr(dto));
            log.error("", e);
            return false;
        }
    }

    /**
     * 余额退款
     *
     * @param refundAmount 退款金额
     * @param order        订单
     * @return 是否成功
     */
    private boolean balancePayRefund(BigDecimal refundAmount, Order order) {
        //查询用户的会员情况
        MemberCardInfoCenterDto memberInfo =
                remoteMiniAccountService.getMemberInfo(order.getUserId());
        MemberBalanceUpdateDto memberBalanceUpdateDto = new MemberBalanceUpdateDto();
        if (ObjectUtil.isNotNull(memberInfo)) {
            memberBalanceUpdateDto.setMemberInfoId(memberInfo.getId());
        } else {
            memberBalanceUpdateDto.setShopUserId(order.getUserId());
        }
        memberBalanceUpdateDto.setUseBalance(refundAmount);
        //变化类型 0-添加 1-减少
        memberBalanceUpdateDto.setChangeType(0);
        memberBalanceUpdateDto.setDetailName(StrUtil.format("订单{}退款", order.getId()));
        //交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功
        memberBalanceUpdateDto.setDealType(3);
        //订单来源：1-会员，2-商品，3-积分
        memberBalanceUpdateDto.setSource(2);
        memberBalanceUpdateDto.setOutOrderId(order.getId());
        boolean success = remoteMiniAccountService.memberBalanceModify(memberBalanceUpdateDto);
        log.info("余额退款,入参为: {}", memberBalanceUpdateDto);
        log.info("余额退款,出参为: {}", success);
        if (!success) {
            throw new ServiceException("余额使用失败，请稍后重试");
        }
        return success;
    }

    /**
     * 关闭换货单
     *
     * @param orderIds 订单id
     */
    @Override
    public void closeExchangeOrder(List<Long> orderIds) {
        if (CollUtil.isNotEmpty(orderIds)) {
            baseMapper.closeExchangeOrder(orderIds, OrderStatusEnum.EXCHANGE_CANCEL_CLOSE);
        }
    }

    /**
     * 获取满减活动信息
     *
     * @param fullScaleIds 满减活动id集合
     */
    @Override
    public List<ActivityStatisticsVo> fullScaleStatisticsByActivityId(Long[] fullScaleIds) {
        List<Long> fullScaleIdList = Arrays.asList(fullScaleIds);
        if (fullScaleIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<ActivityStatisticsVo> vos = orderMapper.selectListByFullScaleIds(fullScaleIdList);

        return vos;
    }

    /**
     * 获取售后工单信息
     *
     * @param orderId 订单id
     */
    @Override
    public AfsOrder selectByOrderIdAndProductSkuId(Long orderId, Long productSkuId) {
        AfsOrder afsOrder = afsOrderMapper.selectByOrderIdAndProductSkuId(orderId, productSkuId);
        if (ObjectUtil.isNotNull(afsOrder)) {
            return afsOrder;
        } else {
            return null;
        }
    }

    /**
     * 校验售后工单信息
     *
     * @param orderIds 订单id集合
     */
    @Override
    public Boolean checkAfsOrder(List<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return Boolean.TRUE;
        }
        Integer size = afsOrderMapper.selectCountProgressByOrderIds(orderIds);
        return size < 1;
    }

    /**
     * 更新订单状态
     *
     * @param orderIds 订单id集合
     * @param statusEnum 订单状态枚举
     */
    @Override
    public Boolean updateOrderStatus(List<Long> orderIds, OrderStatusEnum statusEnum) {
        if (orderIds.isEmpty()) {
            return Boolean.TRUE;
        }
        Order order = new Order();
        order.setStatus(statusEnum);
        baseMapper.update(order, new LambdaQueryWrapper<Order>().in(Order::getId, orderIds));
        return Boolean.TRUE;
    }

    /**
     * 设置订单支付流水号信息
     *
     * @param orderId 订单id
     * @param transactionId 支付流水号
     */
    @Override
    public void setOrderTransactionId(Long orderId, String transactionId) {
        boolean update = this.lambdaUpdate().set(Order::getTransactionId, transactionId).eq(Order::getId, orderId).update();
        if (!update) {
            throw new ServiceException("订单更新失败");
        }
    }
}
