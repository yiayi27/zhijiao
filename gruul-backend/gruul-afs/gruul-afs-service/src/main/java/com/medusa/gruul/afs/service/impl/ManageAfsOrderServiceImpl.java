package com.medusa.gruul.afs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
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
import com.medusa.gruul.afs.mapper.AfsOrderMapper;
import com.medusa.gruul.afs.model.*;
import com.medusa.gruul.afs.mp.Sender;
import com.medusa.gruul.afs.mp.model.AfsRemoveDeliverOrderMessage;
import com.medusa.gruul.afs.mp.model.BaseAfsOrderMessage;
import com.medusa.gruul.afs.service.IAfsNegotiateHistoryService;
import com.medusa.gruul.afs.service.IAfsOrderItemService;
import com.medusa.gruul.afs.service.IManageAfsOrderService;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import com.medusa.gruul.order.api.model.ExchangeOrderDto;
import com.medusa.gruul.order.api.model.OrderItemVo;
import com.medusa.gruul.order.api.model.OrderVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
public class ManageAfsOrderServiceImpl extends ServiceImpl<AfsOrderMapper, AfsOrder> implements IManageAfsOrderService {

    @Resource
    private IAfsOrderItemService afsOrderItemService;
    @Resource
    private RemoteOrderService orderService;
    @Resource
    private Sender sender;
    @Resource
    private IAfsNegotiateHistoryService negotiateHistoryService;
    @Resource
    private AfsOrderServiceImpl afsOrderService;


    /**
     * 商家拒绝
     *
     * @param dto 售后单拒绝信息
     * @return void
     * @author alan
     * @date 2024/3/17 22:33
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sellerRefuse(SellerRefusalDto dto) {
        AfsOrder afsOrder = this.getById(dto.getAfsId());
        if (ObjectUtil.isNull(afsOrder)) {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
        if (afsOrder.getStatus().waitSellerApprove()) {
            afsOrder.setStatus(AfsOrderStatusEnum.CLOSE);
            afsOrder.setCloseType(AfsOrderCloseTypeEnum.SELLER_REFUSE);
            afsOrder.setCloseTime(LocalDateTime.now());
            afsOrder.setRefusalReason(dto.getRefusalReason());
            afsOrder.setDeadline(null);
            this.updateById(afsOrder);
            negotiateHistoryService.sellerRefuse(afsOrder);
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }

    }

    /**
     * 商家同意
     *
     * @param afsId 售后单ID
     * @param isSystem 是否系统操作
     * @return void
     * @author alan
     * @date 2024/3/17 22:34
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sellerApprove(Long afsId, Boolean isSystem) {
        AfsOrder afsOrder = this.getById(afsId);
        if (ObjectUtil.isNull(afsOrder)) {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
        if (afsOrder.getStatus().waitSellerApprove()) {
            //按照售后类型检查是否满足申请条件
            switch (afsOrder.getType()) {
                //生成新的订单
                case POINT_EXCHANGE -> pointApplyExchange(afsOrder, isSystem);
                //直接退款
                case REFUND -> userApplyRefund(afsOrder, isSystem);
                //检查订单状态。如果未签收，直接换货，已签收需先退货
                case EXCHANGE -> userApplyExchange(afsOrder, isSystem, false);
                //检查订单状态。如果未签收，直退款，已签收需先退货
                case RETURN_REFUND -> userApplyReturn(afsOrder, isSystem, false);
                case RETURN_EXCHANGE -> {
                    if (afsOrder.getStatus() == AfsOrderStatusEnum.WAIT_FOR_BUSINESS_RECEIPT) {
                        changeStatusToWaitForSend(afsOrder);
                        break;
                    }
                    userApplyExchange(afsOrder, isSystem);
                }
                default -> throw new ServiceException("不是有效的售后类型");
            }
            OrderVo orderVo = orderService.orderInfo(afsOrder.getOrderId());
            if (ObjectUtil.isNotEmpty(orderVo.getOriginalStatus())) {
                // 退货换货状态不能更改为订单完成
                if (AfsOrderStatusEnum.WAIT_FOR_RETURN_USER == afsOrder.getStatus() &&
                        (afsOrder.getType() == AfsOrderTypeEnum.RETURN_EXCHANGE || afsOrder.getType() == AfsOrderTypeEnum.RETURN_REFUND)) {
                    afsOrderService.orderChangeState(afsOrder.getOrderId(), OrderStatusEnum.AFS);
                } else {
                    boolean flag = Boolean.FALSE;
                    //Todo 订单退款后续需要拆单
                    for (OrderItemVo orderItemVo : orderVo.getOrderItemList()) {
                        if (BeanUtil.isEmpty(orderItemVo.getAfs())) {
                            flag = Boolean.TRUE;
                            break;
                        } else {
                            AfsOrder afs = this.baseMapper.selectByOrderIdAndProductSkuId(orderVo.getId(), orderItemVo.getProductSkuId());
                            if (afs.getStatus() == AfsOrderStatusEnum.WAIT_FOR_BUSINESS_APPROVED) {
                                flag = Boolean.TRUE;
                                break;
                            }
                        }
                    }
                    afsOrderService.orderChangeState(afsOrder.getOrderId(), flag ? orderVo.getOriginalStatus() : OrderStatusEnum.REFUNDED);
                }
            }
            //关闭其他正在进行的售后单和换货单
            closeAfsOrderAndExChangeOrder(afsOrder);
        } else {
            throw new ServiceException("没有符合条件的售后单，请刷新后重试");
        }
    }

    /**
     * 买家签收退货单
     *
     * @param afsOrder 售后订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeStatusToWaitForSend(AfsOrder afsOrder) {
        afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_SEND);
        negotiateHistoryService.sellerCustomMessage(afsOrder, "卖家已经签收,等待发货\n");
        updateById(afsOrder);
        Boolean b = orderService.updateOrderStatus(Collections.singletonList(afsOrder.getOrderId()), OrderStatusEnum.EXCHANGE_IN_PROGRESS);
        if (!b) {
            throw new ServiceException("订单换货重发状态错误");
        }
    }


    /**
     * 关闭订单和换货单
     *
     * @param afsOrder 售后单
     * @return void
     * @author alan
     * @date 2024/3/17 22:34
     */
    private void closeAfsOrderAndExChangeOrder(AfsOrder afsOrder) {
        //查询这个售后包含的其他订单
        List<AfsOrderItem> itemList = afsOrderItemService.list(new LambdaQueryWrapper<AfsOrderItem>()
                .eq(AfsOrderItem::getAfsId, afsOrder.getId())
        );
        if (itemList.isEmpty()) {
            return;
        }
        //获得订单ID的集合
        String originalOrderIds =
                itemList.stream().map(AfsOrderItem::getOrderId).map(String::valueOf).collect(Collectors.joining(StrUtil.COMMA));
        //查询这些订单是否有其他正在进行的售后单
        List<AfsOrder> afsOrderList =
                baseMapper.selectProgressByOriginalOrderIdsAndIdNotIn(originalOrderIds,
                        afsOrder.getId());
        if (afsOrderList.isEmpty()) {
            return;
        }
        //如果上一步的售后单不为空
        for (AfsOrder order : afsOrderList) {
            //如果其他售后单的类型和当前售后单类型相同，或者其他售后单的列席不是换货单，关闭其他售后单，原因为重新换货
            if (order.getType().equals(afsOrder.getType()) || !afsOrder.getType().isExchange()) {
                order.setStatus(AfsOrderStatusEnum.CLOSE);
                order.setCloseType(AfsOrderCloseTypeEnum.RE_APPLY);
                order.setCloseTime(LocalDateTime.now());
                order.setDeadline(null);
                baseMapper.updateById(order);
            }
        }
        itemList =
                afsOrderItemService.list(new LambdaQueryWrapper<AfsOrderItem>()
                        .in(AfsOrderItem::getAfsId, afsOrderList.stream()
                                .map(AfsOrder::getId)
                                .collect(Collectors.toSet())));

        List<Long> orderIds = itemList.stream().map(AfsOrderItem::getOrderId).toList();
        //关闭换货订单
        orderService.closeExchangeOrder(orderIds);
        for (Long orderId : orderIds) {
            //关闭签收单
            closeReceipt(orderId, afsOrder.getId(), afsOrder.getShopId());
        }
    }

    /**
     * 备注售后单
     *
     * @param dto 售后单备注
     * @return void
     * @author alan
     * @date 2024/3/17 22:34
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void note(SellerNoteDto dto) {
        String note = "";
        List<AfsOrder> records = baseMapper.selectByIds(dto.getAfsIds());
        for (AfsOrder record : records) {
            if (StrUtil.isBlank(dto.getNote())) {
                note = "";
            } else {
                note = dto.getNote();
            }
            if (!dto.getOver() && StrUtil.isNotBlank(record.getNote())) {
                record.setNote(record.getNote() + StrUtil.CRLF + note);
            } else {
                record.setNote(note);
            }
            baseMapper.updateById(record);
        }
    }

    /**
     * 提货点同意换货申请
     *
     * @param afsOrder 售后订单
     * @param isSystem 是否系统操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pointApplyExchange(AfsOrder afsOrder, Boolean isSystem) {
        List<AfsOrderItem> afsOrderItemList = afsOrderItemService.list(new LambdaQueryWrapper<AfsOrderItem>()
                .eq(AfsOrderItem::getAfsId, afsOrder.getId()));
        ExchangeOrderDto dto = new ExchangeOrderDto();
        dto.setAfsId(afsOrder.getId());
        dto.setOrderId(CollUtil.getFirst(afsOrderItemList).getOrderId());
        dto.setProductSkuId(CollUtil.getFirst(afsOrderItemList).getProductSkuId());
        dto.setProductQuantity(afsOrderItemList.stream().mapToInt(AfsOrderItem::getProductQuantity).sum());
        dto.setPointApply(true);

        //新增订单并直接进入发货流程
        Long newOrderId = orderService.createExchangeOrder(dto);
        afsOrder.setOrderId(newOrderId);
        afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_SEND);
        this.updateById(afsOrder);
        //添加协商历史
        negotiateHistoryService.sellerApprove(afsOrder, isSystem);

    }

    /**
     * 卖家同意退货换货
     *
     * @param afsOrder 售后订单
     * @param isSystem 是否系统操作
     */
    @Transactional(rollbackFor = Exception.class)
    public void userApplyExchange(AfsOrder afsOrder, boolean isSystem) {
        //获取订单设置
        OrderSetting orderSetting = orderService.getOrderSetting();
        //获取原订单信息
        List<AfsOrderItem> afsOrderItemList = afsOrderItemService.list(new LambdaQueryWrapper<AfsOrderItem>()
                .eq(AfsOrderItem::getAfsId, afsOrder.getId()));
        afsOrder.setDeadline(LocalDateTimeUtil.now().plusDays(orderSetting.getUserReturnOvertime()));
        afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_RETURN_USER);

        this.updateById(afsOrder);
        //添加协商历史
        negotiateHistoryService.sellerApproveNeedReturn(afsOrder, isSystem,
                "", afsOrder.getIsLogistics());
        //添加用户操作倒计时
        BaseAfsOrderMessage message = new BaseAfsOrderMessage();
        message.setId(afsOrder.getId());
        message.setShopId(ISecurity.userMust().getShopId());
        sender.sendUserReturnOvertimeMessage(message, orderSetting.getUserReturnOvertime() * TimeConstants.ONE_DAY);
        AfsOrderVo afsOrderVo = new AfsOrderVo();
        BeanUtil.copyProperties(afsOrder, afsOrderVo);
        afsOrderVo.setItem(CollUtil.getFirst(afsOrderItemList));
        sender.sendWechatReturnMessage(afsOrderVo);
    }


    /**
     * 用户申请换货
     *
     * @param afsOrder 售后订单
     * @param isSystem 是否系统操作
     * @param fromLeaderApprove 是否来自领导审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userApplyExchange(AfsOrder afsOrder, boolean isSystem, boolean fromLeaderApprove) {
        //获取原订单信息
        List<AfsOrderItem> afsOrderItemList = afsOrderItemService.list(new LambdaQueryWrapper<AfsOrderItem>()
                .eq(AfsOrderItem::getAfsId, afsOrder.getId()));
        OrderVo orderVo = orderService.orderInfo(CollUtil.getFirst(afsOrderItemList).getOrderId());

        //如果原订单已经确认收货，则更改订单状态。需要用户先退货
        for (AfsOrderItem afsOrderItem : afsOrderItemList) {
            if (orderVo.getStatus() == OrderStatusEnum.WAIT_FOR_COMMENT && !fromLeaderApprove) {
                //先执行退货流程
                userApplyReturn(afsOrder, isSystem, false);
            } else {
                //如果用户没有收货
                //新增订单并直接进入发货流程
                ExchangeOrderDto dto = new ExchangeOrderDto();
                dto.setAfsId(afsOrderItem.getAfsId());
                dto.setOrderId(afsOrderItem.getOrderId());
                dto.setProductSkuId(afsOrderItem.getProductSkuId());
                dto.setProductQuantity(afsOrderItem.getProductQuantity());
                dto.setPointApply(Boolean.valueOf(String.valueOf(afsOrder.getType().isLeaderApply())));
                //新增订单并直接进入发货流程
                Long newOrderId = orderService.createExchangeOrder(dto);
                afsOrder.setOrderId(newOrderId);
                afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_SEND);
                this.updateById(afsOrder);
                //添加协商历史
                negotiateHistoryService.sellerApprove(afsOrder, isSystem);
            }
        }
    }

    /**
     * 用户申请退款
     *
     * @param afsOrder 售后订单
     * @param isSystem 是否系统操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userApplyRefund(AfsOrder afsOrder, boolean isSystem) {
        OrderVo orderVo = new OrderVo();
        //获取退款金额和原订单信息
        List<AfsOrderItem> afsOrderItemList = afsOrderItemService.list(new LambdaQueryWrapper<AfsOrderItem>()
                .eq(AfsOrderItem::getAfsId, afsOrder.getId()));

        for (AfsOrderItem orderItem : afsOrderItemList) {
            //调用退款流程
            orderService.closeOrder(orderItem.getAfsId(), orderItem.getRefundAmount(), afsOrder.getType().getCode(),
                    orderItem.getOrderId());

            List<Long> orderIds = afsOrderItemService.getExchangeOrder(orderItem.getOrderId());
            orderService.closeExchangeOrder(orderIds);
            //添加协商历史
            negotiateHistoryService.refund(afsOrder, isSystem);
            //更新退款单的的签收单状态
            orderVo = orderService.orderInfo(orderItem.getOrderId());
            log.info("OrderVo is {}", JSONUtil.toJsonStr(orderVo));
            if (OrderStatusEnum.isClose(orderVo.getStatus())) {
                closeReceipt(orderItem.getOrderId(), orderItem.getAfsId(),
                        orderItem.getShopId());
            }
        }
        //更改售后单状态
        afsOrder.setStatus(AfsOrderStatusEnum.SUCCESS);
        afsOrder.setSuccessTime(LocalDateTime.now());
        afsOrder.setDeadline(null);
        this.updateById(afsOrder);
        //发送微信退款消息
        if (afsOrder.getType().equals(AfsOrderTypeEnum.REFUND) || afsOrder.getType().equals(AfsOrderTypeEnum.RETURN_REFUND)) {
            AfsOrderVo afsOrderVo = new AfsOrderVo();
            BeanUtil.copyProperties(afsOrder, afsOrderVo);
            afsOrderVo.setItem(CollUtil.getFirst(afsOrderItemList));
            afsOrderVo.setOriginalOrder(orderVo);
            sender.sendWechatRefundMessage(afsOrderVo);
        }

    }


    /**
     * 关闭签收单
     *
     * @param orderId 订单id
     * @param afsId 售后单id
     * @param shopId 店铺id
     */
    private void closeReceipt(Long orderId, Long afsId, String shopId) {
        List<AfsOrder> afsOrderList = baseMapper.selectProgressByOrderIdAndIdNotIn(orderId, afsId);
        if (afsOrderList.isEmpty()) {
            AfsRemoveDeliverOrderMessage message = new AfsRemoveDeliverOrderMessage();
            message.setOrderId(orderId);
            message.setShopId(shopId);
            sender.sendRemoveSendBillOrderMessage(message);
        }

    }


    /**
     * 用户申请退货
     *
     * @param afsOrder 售后订单
     * @param isSystem 是否系统操作
     * @param fromLeaderApprove 是否来自领导审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userApplyReturn(AfsOrder afsOrder, Boolean isSystem, boolean fromLeaderApprove) {
        //获取订单设置
        OrderSetting orderSetting = orderService.getOrderSetting();
        //获取原订单信息
        List<AfsOrderItem> afsOrderItemList = afsOrderItemService.list(new LambdaQueryWrapper<AfsOrderItem>()
                .eq(AfsOrderItem::getAfsId, afsOrder.getId()));
        OrderVo orderVo = orderService.orderInfo(CollUtil.getFirst(afsOrderItemList).getOrderId());
        //不需要经过退货
        boolean unwantedReturn;
        if (afsOrder.getIsLogistics()) {
            //待商家确认收货的不需要退货
            unwantedReturn = afsOrder.getStatus().isBusinessReceipted();
        } else {
            unwantedReturn = fromLeaderApprove;
        }
        //如果不符合这些条件则需要退货vc
        if (!unwantedReturn) {
            afsOrder.setDeadline(LocalDateTimeUtil.now().plusDays(orderSetting.getUserReturnOvertime()));
            afsOrder.setStatus(AfsOrderStatusEnum.WAIT_FOR_RETURN);
            this.updateById(afsOrder);
            //添加协商历史
            negotiateHistoryService.sellerApproveNeedReturn(afsOrder, isSystem,
                    null,
                    afsOrder.getIsLogistics());
            //添加用户操作倒计时
            BaseAfsOrderMessage message = new BaseAfsOrderMessage();
            message.setId(afsOrder.getId());
            sender.sendUserReturnOvertimeMessage(message,
                    orderSetting.getUserReturnOvertime() * TimeConstants.ONE_DAY);
            if (afsOrder.getType().equals(AfsOrderTypeEnum.REFUND) || afsOrder.getType().equals(AfsOrderTypeEnum.RETURN_REFUND)) {
                AfsOrderVo afsOrderVo = new AfsOrderVo();
                BeanUtil.copyProperties(afsOrder, afsOrderVo);
                afsOrderVo.setItem(CollUtil.getFirst(afsOrderItemList));
                afsOrderVo.setOriginalOrder(orderVo);
                sender.sendWechatReturnMessage(afsOrderVo);
            }
        } else {
            userApplyRefund(afsOrder, isSystem);
        }
    }

    /**
     * 分页查询售后单
     *
     * @param dto 查询条件
     * @return com.medusa.gruul.common.core.util.PageUtils<com.medusa.gruul.afs.model.ManageAfsOrderVo>
     */
    @Override
    public PageUtils<ManageAfsOrderVo> searchManageAfsOrderVoPage(SearchDto dto) {
        List<AfsOrderStatusEnum> statusList = new ArrayList<>();
        //订单状态 -1：所有订单, 0.待处理, 1.处理中, 2.已完成, 3.已关闭
        switch (dto.getStatus()) {
            case -1:
                statusList.clear();
                break;
            case 0:
                statusList.add(AfsOrderStatusEnum.WAIT_FOR_BUSINESS_APPROVED);
                break;
            case 1:
                statusList.add(AfsOrderStatusEnum.WAIT_FOR_RETURN);
                statusList.add(AfsOrderStatusEnum.WAIT_FOR_SEND);
                statusList.add(AfsOrderStatusEnum.WAIT_FOR_PICKUP);
                statusList.add(AfsOrderStatusEnum.SHIPPED);
                statusList.add(AfsOrderStatusEnum.WAIT_FOR_BUSINESS_RECEIPT);
                break;
            case 2:
                statusList.add(AfsOrderStatusEnum.SUCCESS);
                break;
            case 3:
                statusList.add(AfsOrderStatusEnum.CLOSE);
                break;
            default:
                break;
        }
        setDefaultTime(dto);

        Page<ManageAfsOrderVo> page = baseMapper.searchManageAfsOrderVoPage(new Page<ManageAfsOrderVo>(dto.getCurrent(), dto.getSize()),
                statusList, dto);
        return new PageUtils<>(page);
    }

    private void setDefaultTime(SearchDto dto) {
        if (StrUtil.isBlank(dto.getStartTime())) {
            dto.setStartTime(DateUtil.beginOfMonth(new Date()).toString());
        } else {
            dto.setStartTime(dto.getStartTime() + " 00:00:00");
        }
        if (StrUtil.isBlank(dto.getEndTime())) {
            dto.setEndTime(DateUtil.endOfMonth(new Date()).toString());
        } else {
            dto.setEndTime(dto.getEndTime() + " 23:59:59");
        }
    }
}
