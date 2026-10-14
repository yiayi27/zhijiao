package com.medusa.gruul.afs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.api.entity.AfsOrderItem;
import com.medusa.gruul.afs.api.enums.AfsOrderCloseTypeEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderStatusEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderTypeEnum;
import com.medusa.gruul.afs.api.model.AfsSimpleVo;
import com.medusa.gruul.afs.mapper.AfsOrderItemMapper;
import com.medusa.gruul.afs.mapper.AfsOrderMapper;
import com.medusa.gruul.afs.model.*;
import com.medusa.gruul.afs.mp.Sender;
import com.medusa.gruul.afs.mp.model.BaseAfsOrderMessage;
import com.medusa.gruul.afs.service.IAfsNegotiateHistoryService;
import com.medusa.gruul.afs.service.IAfsOrderItemService;
import com.medusa.gruul.afs.service.IAfsOrderService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.logistics.api.feign.RemoteLogisticsFeginService;
import com.medusa.gruul.logistics.model.vo.LogisticsAddressVo;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.OrderItem;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.enums.DeliverTypeEnum;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import com.medusa.gruul.order.api.model.OrderItemVo;
import com.medusa.gruul.order.api.model.OrderVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 售后工单 服务实现类
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
@Slf4j
@Service
public class AfsOrderServiceImpl extends ServiceImpl<AfsOrderMapper, AfsOrder> implements IAfsOrderService {
    @Resource
    private RemoteOrderService orderService;
    @Resource
    private AfsOrderItemMapper afsOrderItemMapper;
    @Resource
    private Sender sender;
    @Resource
    private IAfsNegotiateHistoryService negotiateHistoryService;
    @Resource
    private RemoteLogisticsFeginService remoteLogisticsFeginService;
    @Resource
    @Lazy
    private IAfsOrderItemService afsOrderItemService;


    /**
     * 新增售后详情
     *
     * @param dto            the dto
     * @param orderVo        the order vo
     * @param applyOrderItem the apply order item
     * @param afsOrder       the afs order
     * @return the afs order item
     */
    public AfsOrderItem saveAfsOrderItem(UserApplyDto dto, OrderVo orderVo, OrderItem applyOrderItem,
                                         AfsOrder afsOrder) {
        AfsOrderItem afsOrderItem = new AfsOrderItem();
        afsOrderItem.setAfsId(afsOrder.getId());
        afsOrderItem.setProductId(applyOrderItem.getProductId());
        afsOrderItem.setProductSkuId(applyOrderItem.getProductSkuId());
        afsOrderItem.setProductQuantity(dto.getProductQuantity());
        afsOrderItem.setOrderId(orderVo.getId());
        afsOrderItem.setOrderId(orderVo.getId());
        afsOrderItem.setOrderArea(BooleanUtil.toInt(StrUtil.isNotBlank(orderVo.getActivityIds())));
        afsOrderItem.setProductPic(applyOrderItem.getProductPic());
        afsOrderItem.setProductName(applyOrderItem.getProductName());
        afsOrderItem.setProductPrice(applyOrderItem.getProductPrice());
        afsOrderItem.setSpecs(applyOrderItem.getSpecs());
        afsOrderItem.setRefundAmount(dto.getRefundAmount());
        for (OrderItem orderItem : orderVo.getOrderItemList()) {
            if (orderItem.getProductSkuId().equals(dto.getProductSkuId())) {
                if (orderItem.getGiftIntegration().compareTo(BigDecimal.ZERO) == 0) {
                    afsOrderItem.setRefundIntegration(CommonConstants.NUMBER_ZERO);
                } else {
                    BigDecimal rate = NumberUtil.div(dto.getProductQuantity(), orderItem.getProductQuantity());
                    afsOrderItem.setRefundIntegration(
                            NumberUtil.mul(orderItem.getGiftIntegration(), rate).intValue()
                    );
                }
            }
        }
        afsOrderItemMapper.insert(afsOrderItem);
        return afsOrderItem;
    }

    /**
     * 用户申请售后
     *
     * @param dto 申请售后信息
     * @return com.medusa.gruul.afs.api.entity.AfsOrder
     * @author alan
     * @date 2024/3/17 22:26
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AfsOrder userApply(UserApplyDto dto) {
        SecureUser secureUser = ISecurity.userMust();
        OrderVo orderVo = orderService.orderInfo(dto.getOrderId());
        if (ObjectUtil.isNull(orderVo)) {
            throw new ServiceException("订单获取失败，请刷新后重试");
        }
        if (orderVo.getType().equals(OrderTypeEnum.EXCHANGE) || orderVo.getType().equals(OrderTypeEnum.REPLENISH)) {
            Long orderId = baseMapper.selectOriginalOrderByOrderId(dto.getOrderId());
            orderVo = orderService.orderInfo(orderId);
        }
        if (ObjectUtil.isNull(orderVo)) {
            throw new ServiceException("订单获取失败，请刷新后重试");
        }
        //查询之前的申请次数
        Integer userAfterSaleNum = getApplyNumber(orderVo.getId(), dto.getProductSkuId());

        //获取订单设置
        OrderSetting orderSetting = orderService.getOrderSetting();
        if (ObjectUtil.isNull(orderSetting)) {
            throw new ServiceException("订单设置获取失败，请管理员检查设置并稍后重试");
        }
        // 获取申请的订单详情
        OrderItem applyOrderItem = orderVo.getOrderItemList().stream()
                .filter(orderItem -> orderItem.getProductSkuId().equals(dto.getProductSkuId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException("无效的商品信息"));
        //检查订单状态
        checkOrderStatus(orderVo, dto, orderSetting, userAfterSaleNum);
        //检查售后入参
        //checkAfsOrderParam(orderVo, dto);
        BigDecimal refundAmount = this.getRefundAmount(dto, orderVo);
        dto.setRefundAmount(refundAmount);
        log.info("计算后的退款金额:{}", refundAmount.toPlainString());
        //按照售后类型检查是否满足申请条件
        switch (dto.getType()) {
            //退款
            case REFUND -> checkRefundParam(dto, orderVo);
            //换货
            case EXCHANGE -> {
                if (orderVo.getStatus() == OrderStatusEnum.WAIT_FOR_SEND) {
                    throw new ServiceException("订单还未配送,不支持换货");
                }
                checkExchangeParam(dto, orderVo);
            }
            //退货退款
            case RETURN_REFUND -> {
                checkRefundParam(dto, orderVo);
                checkReturnParam(dto, orderVo);
            }
            default -> throw new ServiceException("不是有效的售后类型");
        }
        if (orderVo.getStatus().equals(OrderStatusEnum.WAIT_FOR_SEND)) {
            //商品最大退款成功的次数
            int count = orderVo.getOrderItemList().size();
            LambdaQueryWrapper<AfsOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper
                    .eq(AfsOrder::getUserId, orderVo.getUserId())
                    .likeRight(AfsOrder::getNo, dto.getOrderId())
                    .eq(AfsOrder::getStatus, AfsOrderStatusEnum.SUCCESS.getCode());
            List<AfsOrder> list = this.list(lambdaQueryWrapper);
            list = list.stream().filter(afsOrder -> {
                AfsOrderTypeEnum type = afsOrder.getType();
                return type.equals(AfsOrderTypeEnum.REFUND) || type.equals(AfsOrderTypeEnum.RETURN_REFUND);
            }).toList();
            if (list.size() == (count - 1)) {
                refundAmount = NumberUtil.add(refundAmount, orderVo.getFreightAmount());
            }
        }
        dto.setRefundAmount(refundAmount);
        AfsOrder afsOrder = saveAfsOrder(dto, secureUser, orderVo, userAfterSaleNum, orderSetting, null);
        AfsOrderItem afsOrderItem = saveAfsOrderItem(dto, orderVo, applyOrderItem, afsOrder);
        //初始化协商历史
        negotiateHistoryService.init(Collections.singletonList(afsOrderItem), dto, "买家");
        BaseAfsOrderMessage message = new BaseAfsOrderMessage();
        message.setId(afsOrder.getId());
        sender.sendMerchantAutoConfirmMessage(message,
                orderSetting.getMerchantConfirmOvertime() * TimeConstants.ONE_DAY);
        return afsOrder;
    }

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param orderStatusEnum
     */
    public void orderChangeState(Long orderId, OrderStatusEnum orderStatusEnum) {
        //更改订单状态
        Boolean flag = orderService.updateOrderStatus(Collections.singletonList(orderId), orderStatusEnum);
        //
        if (!flag) {
            throw new ServiceException("修改订单状态失败");
        }
    }


    /**
     * 保存售后单
     *
     * @param dto 售后信息
     * @param secureUser 用户信息
     * @param orderVo 订单信息
     * @param userAfterSaleNum 用户售后次数
     * @param orderSetting 订单设置信息
     * @param receiptBillId 签收单id
     * @return com.medusa.gruul.afs.api.entity.AfsOrder
     */
    @Override
    public AfsOrder saveAfsOrder(BaseApplyDto dto, SecureUser secureUser, OrderVo orderVo, Integer userAfterSaleNum,
                                 OrderSetting orderSetting, Long receiptBillId) {
        AfsOrder afsOrder = new AfsOrder();
        afsOrder.setShopId(orderVo.getShopId());
        afsOrder.setNo(orderVo.getId().toString().concat(dto.getType().getName()).concat(String.valueOf(userAfterSaleNum + 1)));
        afsOrder.setType(dto.getType());
        afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_BUSINESS_APPROVED);
        afsOrder.setReceiptBillId(receiptBillId);
        afsOrder.setUserId(secureUser.getId());
        afsOrder.setUserName(secureUser.getNickname());
        afsOrder.setUserAvatarUrl(secureUser.getAvatar());
        afsOrder.setProductSkuId(dto.getProductSkuId());
        afsOrder.setTemplateId(dto.getTemplateId());
        BigDecimal refundAmount = dto.getRefundAmount();
        afsOrder.setRefundAmount(refundAmount);
        afsOrder.setDeadline(LocalDateTimeUtil.now().plusDays(orderSetting.getMerchantConfirmOvertime()));
        afsOrder.setDescription(dto.getDescription());
        afsOrder.setImages(dto.getImages());
        afsOrder.setIsLogistics(orderVo.getOrderDelivery().getDeliveryType().equals(DeliverTypeEnum.LOGISTICS));
        this.save(afsOrder);
        return afsOrder;
    }

    /**
     * 计算退款金额
     */
    public BigDecimal getRefundAmount(BaseApplyDto applyInfo, OrderVo order) {
        Optional<OrderItemVo> first = order.getOrderItemList()
                .stream().filter(item -> item.getRealAmount() != null && item.getProductSkuId().equals(applyInfo.getProductSkuId()))
                .findFirst();
        if (first.isEmpty()) {
            throw new ServiceException("申请的订单不包含所选商品");
        }
        OrderItemVo item = first.get();
        Integer applyQuantity = applyInfo.getProductQuantity();
        Integer orderQuantity = item.getProductQuantity();
        if (applyQuantity > orderQuantity) {
            throw new ServiceException("退款申请数量大于购买的商品数量");
        }
        BigDecimal rate = NumberUtil.div(applyQuantity, orderQuantity);
        log.info(String.format("计算后的数量比例%s", rate));
        log.info(String.format("真实付款金额:%s", item.getRealAmount()));
        boolean isNullAmount = ObjectUtil.isNull(item.getRealAmount()) || item.getRealAmount().equals(BigDecimal.ZERO);
        BigDecimal refundAmount = isNullAmount ?
                NumberUtil.mul(order.getPayAmount(), rate).setScale(2, RoundingMode.HALF_UP) :
                NumberUtil.mul(item.getRealAmount(), rate).setScale(2, RoundingMode.HALF_UP);
        if (refundAmount.equals(BigDecimal.ZERO)) {
            refundAmount = OrderConstant.MIN_PAY_FEE;
        }
        return refundAmount;
    }

    /**
     * 检查订单状态
     *
     * @param order 订单信息
     * @param dto 售后信息
     * @param orderSetting 订单设置信息
     * @param userAfterSaleNum 用户售后次数
     */
    @Override
    public void checkOrderStatus(OrderVo order, BaseApplyDto dto, OrderSetting orderSetting, Integer userAfterSaleNum) {
        if (ObjectUtil.isNull(order)) {
            throw new ServiceException("订单不存在，请刷新后再试");
        }
        if (userAfterSaleNum >= orderSetting.getAfsApplyNumber()) {
            throw new ServiceException(String.format("订单%s申请售后次数已经超过最大限制，不能继续申请售后", order.getId()));
        }
        if (order.getOrderDelivery().getDeliveryType().equals(DeliverTypeEnum.LOGISTICS) && dto.getType().equals(AfsOrderTypeEnum.EXCHANGE)) {
            throw new ServiceException("物流订单不能申请换货");
        }
        if (OrderStatusEnum.isClose(order.getStatus())) {
            throw new ServiceException(String.format("订单%s已经关闭，不能继续申请售后", order.getId()));
        }
        if (ObjectUtil.isNotNull(order.getOrderDelivery().getReceiveTime()) &&
                DateUtil.offsetDay(LocalDateTimeUtils.convertLdtToDate(order.getOrderDelivery().getReceiveTime()),
                        orderSetting.getFinishOvertime()).getTime()
                        < System.currentTimeMillis()) {
            throw new ServiceException(String.format("订单%s已经离确认收货已经超过%s天，不能继续申请售后", order.getId(), orderSetting.getFinishOvertime()));
        }
        AfsOrder afsOrder = this.selectProgressByOrderIdAndProductSkuId(order.getId(), dto.getProductSkuId());
        if (ObjectUtil.isNotNull(afsOrder)) {
            if (afsOrder.getProductSkuId().equals(dto.getProductSkuId())) {
                //当前售后订单没有结束，并且当前售后订单不是换货单的不可再次申请售后
                if (!afsOrder.getType().isExchange()) {
                    throw new ServiceException(String.format("订单%s正在申请售后，请等待当前售后流程结束", order.getId()));
                } else {
                    //当前换货单等待审核的也不能再次申请
                    if (afsOrder.getStatus().equals(AfsOrderStatusEnum.WAIT_FOR_BUSINESS_APPROVED)) {
                        throw new ServiceException(String.format("订单%s正在申请售后，请等待当前售后流程结束", order.getId()));
                    }
                }
                if (afsOrder.getType().isRefund() && afsOrder.getStatus().equals(AfsOrderStatusEnum.SUCCESS)) {
                    throw new ServiceException(String.format("订单%s中商品%s，已经成功退款，不能继续申请售后", order.getId(), dto.getProductSkuId()));
                }
            }
        }
    }

    /**
     * 根据售后单ID获取售后单详情
     *
     * @param afsId
     * @return com.medusa.gruul.afs.model.AfsOrderVo
     * @author alan
     * @date 2024/3/17 22:27
     */
    @Override
    public AfsOrderVo getAfsOrderInfo(Long afsId) {
        AfsOrder afsOrder = baseMapper.selectById(afsId);
        if (ObjectUtil.isNull(afsOrder)) {
            throw new ServiceException("无效的售后信息，请刷新后重试");
        }
        AfsOrderVo vo = new AfsOrderVo();
        BeanUtil.copyProperties(afsOrder, vo);
        AfsOrderItem afsOrderItem = afsOrderItemMapper.selectOne(new LambdaQueryWrapper<AfsOrderItem>()
                .eq(AfsOrderItem::getAfsId, afsId)
                .last(CommonConstants.SQL_LIMIT_1));
        vo.setItem(afsOrderItem);
        OrderVo orderVo = orderService.orderInfo(afsOrderItem.getOrderId());
        vo.setOriginalOrder(orderVo);
        return vo;
    }


    /**
     * 检查换货参数
     *
     * @param dto
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:28
     */
    private void checkExchangeParam(UserApplyDto dto, OrderVo orderVo) {
        if (ObjectUtil.isNull(dto.getProductQuantity()) || dto.getProductQuantity() < 1) {
            throw new ServiceException("换货数量不得小于1");
        }
        for (OrderItem orderItem : orderVo.getOrderItemList()) {
            if (orderItem.getProductSkuId().equals(dto.getProductSkuId())) {
                if (orderItem.getProductQuantity() < dto.getProductQuantity()) {
                    throw new ServiceException("换货数量不得大于总数量");
                }
            }
        }
    }

    /**
     * 检查退货参数
     *
     * @param dto
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:28
     */
    private void checkReturnParam(UserApplyDto dto, OrderVo orderVo) {
        if (ObjectUtil.isNull(dto.getProductQuantity()) || dto.getProductQuantity() < 1) {
            throw new ServiceException("退货数量不得小于1");
        }
        for (OrderItem orderItem : orderVo.getOrderItemList()) {
            if (orderItem.getProductSkuId().equals(dto.getProductSkuId())) {
                if (orderItem.getProductQuantity() < dto.getProductQuantity()) {
                    throw new ServiceException("退货数量不得大于总数量");
                }
            }
        }
    }

    /**
     * 检查退款参数
     *
     * @param dto
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:28
     */
    private void checkRefundParam(UserApplyDto dto, OrderVo orderVo) {
        if (ObjectUtil.isNull(dto.getRefundAmount()) || NumberUtil.isLess(dto.getRefundAmount(), BigDecimal.ZERO)) {
            throw new ServiceException("退款金额不得小于0");
        }
        if (NumberUtil.isLess(orderVo.getPayAmount(), dto.getRefundAmount())) {
            throw new ServiceException("退款金额不得大于实际支付金额");
        }
    }

    /**
     * 根据订单ID和商品ID查询进行中的订单
     *
     * @param orderId 订单id
     * @param productSkuId skuId
     * @return com.medusa.gruul.afs.api.entity.AfsOrder
     */
    @Override
    public AfsOrder selectProgressByOrderIdAndProductSkuId(Long orderId, Long productSkuId) {
        return baseMapper.selectProgressByOrderIdAndProductSkuId(orderId, productSkuId);
    }


    /**
     * 获取用户申请售后的次数
     *
     * @param orderId
     * @return java.lang.Integer
     * @author alan
     * @date 2024/3/17 22:30
     */
    @Override
    public Integer getUserApplyNumber(Long orderId) {
        Set<Long> originalOrderIds = new HashSet<>();
        Long originalOrderId = baseMapper.selectOriginalOrderByOrderId(orderId);
        if (ObjectUtil.isNotNull(originalOrderId)) {
            originalOrderIds.add(originalOrderId);
        }
        originalOrderIds.add(orderId);
        return baseMapper.getOrderApplyNumber(CollUtil.newArrayList(originalOrderIds),
                Arrays.asList(AfsOrderTypeEnum.REFUND.getCode(), AfsOrderTypeEnum.EXCHANGE.getCode(),
                        AfsOrderTypeEnum.RETURN_REFUND.getCode()));
    }

    /**
     * 获取指定商品SKU的售后申请次数统计
     */
    private int getApplyNumber(Long orderId, Long skuId) {
        HashMap<String, Object> orderItemAfses = afsOrderItemService.userApply(orderId);
        if (CollUtil.isEmpty(orderItemAfses)) {
            return CommonConstants.NUMBER_ZERO;
        }
        if (Boolean.parseBoolean(String.valueOf(orderItemAfses.get("expire")))) {
            throw new ServiceException("订单已超过申请售后时间");
        }
        Object afsObject = orderItemAfses.get("number");
        if (afsObject == null) {
            return CommonConstants.NUMBER_ZERO;
        }
        List<Map<String, Object>> numbers = (List<Map<String, Object>>) afsObject;
        if (CollUtil.isEmpty(numbers)) {
            return CommonConstants.NUMBER_ZERO;
        }
        for (Map<String, Object> orderItemAfs : numbers) {
            if (orderItemAfs == null) {
                continue;
            }
            Long productSkuId = Long.valueOf(String.valueOf(orderItemAfs.get("productSkuId")));
            if (skuId.equals(productSkuId)) {
                return Integer.parseInt(String.valueOf(orderItemAfs.get("userApplyNumber")));
            }
        }
        log.info("没有获取到改商品skuId的售后数据");
        return CommonConstants.NUMBER_ZERO;
    }

    /**
     * 用户取消售后
     *
     * @param afsId 售后订单id
     * @param isSystem 是否系统取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userCancel(Long afsId, boolean isSystem) {
        AfsOrder afsOrder = this.getById(afsId);
        if (afsOrder.getStatus() == AfsOrderStatusEnum.SUCCESS) {
            throw new ServiceException("该售后单已处理");
        }
        if (ObjectUtil.isNull(afsOrder)) {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
        if (afsOrder.getStatus().canReceipt()) {
            afsOrder.setStatus(AfsOrderStatusEnum.CLOSE);
            afsOrder.setCloseType(AfsOrderCloseTypeEnum.USER_CANCEL);
            afsOrder.setCloseTime(LocalDateTime.now());
            afsOrder.setDeadline(null);
            this.updateById(afsOrder);
            negotiateHistoryService.userCancel(afsOrder, isSystem);
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
    }

    /**
     * 用户确认已退货
     *
     * @param afsId           售后订单id
     * @param deliveryCode    快递公司编码
     * @param deliveryCompany 快递公司名称
     * @param deliverySn      快递单号
     * @param phone           收件人电话
     * @param reason          原因
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userReturn(Long afsId, String deliveryCode, String deliveryCompany, String deliverySn, String phone,
                           String reason) {
        AfsOrder afsOrder = this.getById(afsId);
        if (ObjectUtil.isNull(afsOrder)) {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
        //获取订单设置
        OrderSetting orderSetting = orderService.getOrderSetting();
        if (afsOrder.getIsLogistics()) {
            if (StrUtil.isBlank(deliveryCompany)) {
                throw new ServiceException("物流公司不能为空");
            }
            if (StrUtil.isBlank(deliverySn)) {
                throw new ServiceException("物流单号不能为空");
            }
            if (StrUtil.isBlank(phone)) {
                throw new ServiceException("联系电话不能为空");
            }
        }
        if (afsOrder.getStatus().equals(AfsOrderStatusEnum.WAIT_FOR_RETURN)) {
            afsOrder.setDeliverySn(deliverySn);
            afsOrder.setDeliveryCode(deliveryCode);
            afsOrder.setDeliveryCompany(deliveryCompany);
            // TODO: 2024/8/3 暂且换成 商家最大审核期限
            afsOrder.setDeadline(LocalDateTimeUtil.now().plusDays(orderSetting.getMerchantConfirmOvertime()));
            afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_BUSINESS_RECEIPT);
            this.updateById(afsOrder);
            //添加协商历史
            negotiateHistoryService.userReturn(afsOrder, afsOrder.getIsLogistics());
            //添加操作倒计时
            BaseAfsOrderMessage message = new BaseAfsOrderMessage();
            message.setId(afsOrder.getId());
            // TODO: 2024/8/3 同理换
            sender.sendUserReturnOvertimeMessage(message,
                    orderSetting.getMerchantConfirmOvertime() * TimeConstants.ONE_DAY);
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
    }


    /**
     * 根据签收单查询售后
     *
     * @param receiptBillId
     * @return java.util.List<com.medusa.gruul.afs.api.model.AfsSimpleVo>
     * @author alan
     * @date 2024/3/17 22:30
     */
    @Override
    public List<AfsSimpleVo> getAfsOrderByReceiptBillId(Long receiptBillId) {
        return baseMapper.selectAfsOrderByReceiptBillId(receiptBillId);
    }

    /**
     * 订单签收
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:30
     */
    @Override
    public void orderReceipt(OrderVo orderVo) {
        if (orderVo.getType().equals(OrderTypeEnum.EXCHANGE)) {
            Long orderId = baseMapper.selectOriginalOrderByOrderId(orderVo.getId());
            orderService.receiptOrder(orderId);
        }
        //生成此售后单的原始订单，在确认收货后自动取消等待商家审批的售后单
        AfsOrder closeAfsOrder = baseMapper.selectByOriginalOrderId(orderVo.getId());
        if (ObjectUtil.isNotNull(closeAfsOrder)) {
            if (closeAfsOrder.getStatus().isBusinessApprove()) {
                closeAfsOrder.setStatus(AfsOrderStatusEnum.CLOSE);
                closeAfsOrder.setCloseType(AfsOrderCloseTypeEnum.USER_CANCEL);
                closeAfsOrder.setCloseTime(LocalDateTime.now());
                this.updateById(closeAfsOrder);
            }
        }
        //此售后单生成的新订单，在确认收货后自动更新此售后单
        AfsOrder completeAfsOrder = baseMapper.selectByOrderId(orderVo.getId());
        if (ObjectUtil.isNotNull(completeAfsOrder)) {
            completeAfsOrder.setStatus(AfsOrderStatusEnum.SUCCESS);
            completeAfsOrder.setSuccessTime(LocalDateTime.now());
            this.updateById(completeAfsOrder);
        }
    }

    /**
     * 更新补货单的相关售后状态
     *
     * @param orderIds
     * @return void
     * @author alan
     * @date 2024/3/17 22:31
     */
    @Override
    public void updateReplenish(List<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return;
        }
        //处理提货点的数据
        List<AfsOrder> afsOrders = baseMapper.selectList(new LambdaQueryWrapper<AfsOrder>()
                .in(AfsOrder::getOrderId, orderIds)
                .in(AfsOrder::getType, Arrays.asList(AfsOrderTypeEnum.POINT_EXCHANGE.getCode()
                        , AfsOrderTypeEnum.POINT_REFUND.getCode()))
        );
        for (AfsOrder afsOrder : afsOrders) {
            afsOrder.setStatus(AfsOrderStatusEnum.SUCCESS);
            afsOrder.setSuccessTime(LocalDateTime.now());
            afsOrder.setDeadline(null);
            this.updateById(afsOrder);
            negotiateHistoryService.pointExchangeSuccess(afsOrder);
        }
        //处理用户发货单
        List<AfsOrder> userAfsOrders = baseMapper.selectList(new LambdaQueryWrapper<AfsOrder>()
                .in(AfsOrder::getOrderId, orderIds)
                .in(AfsOrder::getType, List.of(AfsOrderTypeEnum.EXCHANGE.getCode())));
        for (AfsOrder userAfsOrder : userAfsOrders) {
            userAfsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_PICKUP);
            this.updateById(userAfsOrder);
        }
        userAfsOrders = baseMapper.selectList(new LambdaQueryWrapper<AfsOrder>()
                .in(AfsOrder::getOrderId, orderIds)
                .in(AfsOrder::getType, Arrays.asList(AfsOrderTypeEnum.POINT_EXCHANGE.getCode()
                        , AfsOrderTypeEnum.EXCHANGE.getCode())));
        //处理用户订单
        if (!userAfsOrders.isEmpty()) {
            List<Long> afsIds = userAfsOrders.stream().map(AfsOrder::getId).toList();
            List<AfsOrderItem> afsOrderItemList =
                    afsOrderItemMapper.selectList(new LambdaQueryWrapper<AfsOrderItem>().in(AfsOrderItem::getAfsId,
                            afsIds));
            orderService.updateOrderStatus(afsOrderItemList.stream().map(AfsOrderItem::getOrderId).toList(), OrderStatusEnum.WAIT_FOR_PICKUP);
        }
    }

    /**
     * 订单发货
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:31
     */
    @Override
    public void orderShipped(OrderVo orderVo) {
        log.info("执行订单发货逻辑");
        log.info("OrderVo is {}", JSONUtil.toJsonStr(orderVo));
        if (OrderTypeEnum.isExchange(orderVo.getType())) {
            AfsOrder afsOrder = baseMapper.selectByOrderId(orderVo.getId());
            afsOrder.setStatus(AfsOrderStatusEnum.SHIPPED);
            this.updateById(afsOrder);
            negotiateHistoryService.sellerShipped(afsOrder);
        }
    }


    /**
     * 发货单签收
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:31
     */
    @Override
    public void deliverReceipt(OrderVo orderVo) {
        log.info("执行发货单签收逻辑");
        log.info("OrderVo is {}", JSONUtil.toJsonStr(orderVo));
        if (OrderTypeEnum.isExchange(orderVo.getType())) {
            AfsOrder afsOrder = baseMapper.selectByOrderId(orderVo.getId());
            afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_PICKUP);
            this.updateById(afsOrder);
        }
    }

    /**
     * 查询订单列表
     *
     * @param dto
     * @return com.medusa.gruul.common.core.util.PageUtils
     * @author alan
     * @date 2024/3/17 22:31
     */
    @Override
    public PageUtils<ApiAfsOrderVo> searchOrder(SearchOrderDto dto) {
        Page<ApiAfsOrderVo> page = baseMapper.searchOrder(new Page<ApiAfsOrderVo>(dto.getCurrent(), dto.getSize()),
                ISecurity.userMust().getId());
        return new PageUtils<>(page);

    }

    /**
     * 订单完成
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:32
     */
    @Override
    public void orderCompleted(OrderVo orderVo) {
        //生成此售后单的原始订单，在评价订单后自动取消等待商家审批的售后单
        AfsOrder closeAfsOrder = baseMapper.selectByOriginalOrderId(orderVo.getId());
        if (ObjectUtil.isNotNull(closeAfsOrder)) {
            if (closeAfsOrder.getStatus().isBusinessApprove()) {
                closeAfsOrder.setStatus(AfsOrderStatusEnum.CLOSE);
                closeAfsOrder.setCloseType(AfsOrderCloseTypeEnum.USER_CANCEL);
                closeAfsOrder.setCloseTime(LocalDateTime.now());
                this.updateById(closeAfsOrder);
            }
        }
    }

    /**
     * 获取退货地址
     *
     * @param orderId
     * @return com.medusa.gruul.afs.model.ReturnAddressVo
     * @author alan
     * @date 2024/3/17 22:32
     */
    @Override
    public ReturnAddressVo getReturnAddress(Long orderId) {
        ReturnAddressVo returnAddressVo = new ReturnAddressVo();
        OrderVo orderVo = orderService.orderInfo(orderId);
        if (ObjectUtil.isNull(orderVo)) {
            throw new ServiceException("无效的订单信息");
        }
        LogisticsAddressVo logisticsAddressVo = remoteLogisticsFeginService.getFeignDefaultAddress(2);
        if (ObjectUtil.isNotNull(logisticsAddressVo)) {
            log.info(JSONUtil.toJsonStr(logisticsAddressVo));
            returnAddressVo.setAddress(logisticsAddressVo.getProvince() + logisticsAddressVo.getCity() + logisticsAddressVo.getCountry() + logisticsAddressVo.getAddress());
            returnAddressVo.setName(logisticsAddressVo.getName());
            returnAddressVo.setPhone(logisticsAddressVo.getPhone());
        } else {
            throw new ServiceException("获取默认退货地址失败");
        }
        return returnAddressVo;
    }

    /**
     * 获取订单是否过期
     *
     * @param orderId
     * @return java.lang.Boolean
     * @author alan
     * @date 2024/3/17 22:33
     */
    @Override
    public Boolean getAfsExpire(Long orderId) {
        OrderVo orderVo = orderService.orderInfo(orderId);
        if (ObjectUtil.isNull(orderVo)) {
            throw new ServiceException("订单获取失败，请刷新后重试");
        }
        //获取订单设置
        boolean expire = false;
        if (ObjectUtil.isNotNull(orderVo.getOrderDelivery().getReceiveTime())) {
            OrderSetting orderSetting = orderService.getOrderSetting();
            if (ObjectUtil.isNull(orderSetting)) {
                throw new ServiceException("订单设置获取失败，请管理员检查设置并稍后重试");
            }
            expire =
                    DateUtil.offsetDay(LocalDateTimeUtils.convertLdtToDate(orderVo.getOrderDelivery().getReceiveTime()),
                            orderSetting.getFinishOvertime()).getTime()
                            < System.currentTimeMillis();
        }
        return expire;
    }

}
