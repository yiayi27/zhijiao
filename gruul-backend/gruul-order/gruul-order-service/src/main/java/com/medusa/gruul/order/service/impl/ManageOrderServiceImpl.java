package com.medusa.gruul.order.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.UserOrderDistributionVo;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderDelivery;
import com.medusa.gruul.order.api.entity.OrderProductEvaluate;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.model.BaseOrderMessage;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.order.api.model.RemoveSendBillOrderMessage;
import com.medusa.gruul.order.mapper.*;
import com.medusa.gruul.order.model.*;
import com.medusa.gruul.order.mq.Sender;
import com.medusa.gruul.order.service.IManageOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class ManageOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IManageOrderService {

    @Resource
    private Sender sender;
    @Resource
    private MiniOrderServiceImpl miniOrderService;
    @Resource
    private OrderDeliveryMapper orderDeliveryMapper;
    @Resource
    private OrderEvaluateMapper orderEvaluateMapper;
    @Resource
    private OrderProductEvaluateMapper orderProductEvaluateMapper;
    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;
    @Resource
    private OrderSettingMapper orderSettingMapper;

    /**
     * PC管理端订单列表
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2023/11/20 20:04
     */
    @Override
    public PageUtils<ManageOrderVo> searchOrder(ManageSearchOrderDto dto) {
        List<Integer> orderStatusList = new ArrayList<>(4);
        //订单状态 -1：所有订单, 0.待付款（待买家付款）, 1.待发货（买家已付款）, 2.配送中（卖家已发货）, 3.待提货（物流订单已发货）, 4.已完成（用户已经签收）, 5.已关闭
        switch (dto.getOrderStatus()) {
            case -1:
                if (ObjectUtil.isNotNull(dto.getSendBillId()) && dto.getSendBillId() == -1) {
                    orderStatusList.add(OrderStatusEnum.WAIT_FOR_SEND.getCode());
                } else {
                    orderStatusList.clear();
                }
                break;
            case 0:
                orderStatusList.add(OrderStatusEnum.WAIT_FOR_PAY.getCode());
                break;
            case 1:
                orderStatusList.add(OrderStatusEnum.WAIT_FOR_SEND.getCode());
                break;
            case 2:
                orderStatusList.add(OrderStatusEnum.SHIPPED.getCode());
                break;
            case 3:
                orderStatusList.add(OrderStatusEnum.WAIT_FOR_PICKUP.getCode());
                break;
            case 4:
                orderStatusList.add(OrderStatusEnum.WAIT_FOR_COMMENT.getCode());
                orderStatusList.add(OrderStatusEnum.COMPLETE.getCode());
                break;
            case 5:
                orderStatusList.add(OrderStatusEnum.BUYER_PAY_TIMEOUT_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.BUYER_CANCEL_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.SELLER_CANCEL_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.EXCHANGE_SUCCESS_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.REFUNDED.getCode());
                break;
            default:
                break;
        }
        String startDate;
        String endDate;
        //近一个月->0; 近三个月->1; 全部->2;
        switch (dto.getQuicklyDate()) {
            case 1:
                startDate = DateUtil.offsetMonth(DateUtil.date(), -3).toDateStr();
                endDate = DateUtil.date().toDateStr();
                break;
            case 2:
                startDate = null;
                endDate = null;
                break;
            case 0:
            default:
                startDate = DateUtil.offsetMonth(DateUtil.date(), -1).toDateStr();
                endDate = DateUtil.date().toDateStr();
                break;
        }
        Page<ManageOrderVo> page = baseMapper.searchManageOrderVoPage(new Page(dto.getCurrent(), dto.getSize()),
                orderStatusList, startDate, endDate, dto);

        return new PageUtils<>(page);
    }

    /**
     * 商家批量关闭订单
     *
     * @param orderIds the order ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(List<Long> orderIds) {
        List<Order> orders = baseMapper.selectByIds(orderIds);
        for (Order order : orders) {
            if (!OrderStatusEnum.canCancel(order.getStatus())) {
                throw new ServiceException("当前状态不能取消此订单");
            }
        }
        for (Order order : orders) {
            miniOrderService.cancelOrder(order.getId(), order, OrderStatusEnum.SELLER_CANCEL_CLOSE);
        }
    }

    /**
     * 商家批量备注订单
     *
     * @param orderIds the order ids
     * @param note     the note
     * @param isOver   the is over
     */
    @Override
    public void noteOrder(List<Long> orderIds, String note, Boolean isOver) {
        List<Order> orders = baseMapper.selectByIds(orderIds);
        for (Order order : orders) {
            StringBuilder sb = new StringBuilder();
            if (StrUtil.isBlank(note)) {
                note = "";
            }
            if (isOver || StrUtil.isEmpty(order.getNote())) {
                order.setNote(note);
            } else {
                order.setNote(sb.append(order.getNote()).append(StrUtil.CRLF).append(note).toString());
            }
        }
        this.updateBatchById(orders);
    }

    /**
     * 商家移出发货单
     *
     * @param orderIds the order ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSendBill(List<Long> orderIds) {
        List<OrderDelivery> orders = orderDeliveryMapper.selectByIds(orderIds);
        for (OrderDelivery order : orders) {
            sender.sendRemoveSendBillOrderMessage(new RemoveSendBillOrderMessage(
                    order.getOrderId()));
        }
        for (OrderDelivery order : orders) {
            order.setDeliverySn(null);
            order.setDeliveryCompany(null);
            order.setDeliveryTime(null);
            orderDeliveryMapper.updateById(order);
        }

    }

    /**
     * 商家查看评价
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     */
    @Override
    public PageUtils<ManageEvaluateVo> searchOrderEvaluate(ManageSearchEvaluateDto dto) {
        Page<ManageEvaluateVo> page = orderEvaluateMapper.searchOrderEvaluate(new Page(dto.getCurrent(),
                dto.getSize()), dto);
        for (ManageEvaluateVo record : page.getRecords()) {
            record.setRate(record.getShopRate());
        }
        return new PageUtils<>(page);
    }

    /**
     * 商家设置评价为精选
     *
     * @param ids the ids
     */
    @Override
    public void choiceEvaluate(List<Long> ids) {
        List<OrderProductEvaluate> productEvaluateList = orderProductEvaluateMapper.selectByIds(ids);
        for (OrderProductEvaluate productEvaluate : productEvaluateList) {
            productEvaluate.setChoice(true);
            orderProductEvaluateMapper.updateById(productEvaluate);
        }
    }

    /**
     * 商家回复评价
     *
     * @param id    the id
     * @param reply the reply
     */
    @Override
    public void replyEvaluate(Long id, String reply) {
        OrderProductEvaluate productEvaluate = orderProductEvaluateMapper.selectById(id);
        productEvaluate.setReply(reply);
        orderProductEvaluateMapper.updateById(productEvaluate);
    }

    /**
     * 商家查询订单详情
     *
     * @param orderId the order id
     * @return com.medusa.gruul.order.model.OrderVo order vo
     */
    @Override
    public OrderVo orderInfo(Long orderId) {
        OrderVo orderVo = baseMapper.selectOrderVoById(orderId);
        if (orderVo.getFirstDistributionAmount() != null) {
            UserOrderDistributionVo userOrderDistributionVo = remoteMiniAccountService.getDistributionInfo(orderVo.getShopId(), orderVo.getUserId());
            userOrderDistributionVo.setFirstDistributionAmount(orderVo.getFirstDistributionAmount());
            orderVo.setUserOrderDistributionVo(userOrderDistributionVo);
        }
        return orderVo;
    }

    /**
     * 商家取消设置评价为精选
     *
     * @param ids the as list
     */
    @Override
    public void unChoiceEvaluate(List<Long> ids) {
        List<OrderProductEvaluate> productEvaluateList = orderProductEvaluateMapper.selectByIds(ids);
        for (OrderProductEvaluate productEvaluate : productEvaluateList) {
            if (productEvaluate.getChoice()) {
                productEvaluate.setChoice(false);
                orderProductEvaluateMapper.updateById(productEvaluate);
            }
        }
    }

    /**
     * Search logistics order list page utils.
     *
     * @param dto the manage logistics order dto
     * @return the page utils
     * 物流批量发货订单列表
     */
    @Override
    public PageUtils<ManageDeliveryOrderVo> searchLogisticsOrderList(ManageLogisticsOrderDto dto) {
        Page<ManageDeliveryOrderVo> page = baseMapper.searchLogisticsOrderList(new Page(dto.getCurrent(),
                dto.getSize()), dto);
        return new PageUtils<>(page);

    }

    /**
     * 控制台订单数据概况
     *
     * @return com.medusa.gruul.order.model.ManageOrderOverviewVo overview
     */
    @Override
    public ManageOrderOverviewVo getOverview() {
        ManageOrderOverviewVo vo = new ManageOrderOverviewVo();
        Long waitForPay = baseMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus,
                OrderStatusEnum.WAIT_FOR_PAY));
        Long waitForSend = baseMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus,
                OrderStatusEnum.WAIT_FOR_SEND));
        Long shipped = baseMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus,
                OrderStatusEnum.SHIPPED));
        Long waitForPickup = baseMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus,
                OrderStatusEnum.WAIT_FOR_PICKUP));
        vo.setWaitForPay(waitForPay);
        vo.setWaitForSend(waitForSend);
        vo.setShipped(shipped);
        vo.setWaitForPickup(waitForPickup);
        return vo;
    }

    /**
     * 快递订单搜索
     *
     * @param dto the dto
     * @return java.util.List<com.medusa.gruul.order.model.ManageOrderVo> list
     */
    @Override
    public List<ManageOrderVo> searchLogisticsOrder(ManageSearchLogisticsOrderDto dto) {
        List<Long> orderIds = new ArrayList<>();
        if (StrUtil.isNotBlank(dto.getOrderIds())) {
            orderIds =
                    Arrays.stream(dto.getOrderIds().split(StrUtil.COMMA)).map(Long::parseLong).toList();
        }

        return baseMapper.searchLogisticsOrder(orderIds);
    }

    /**
     * 查询是否有待发货的快递订单
     *
     * @return java.lang.Integer integer
     * @author alan
     * @date 2024/6/17 22:02
     */
    @Override
    public Integer countLogisticsWaitSend() {
        return baseMapper.countLogisticsWaitSend();
    }

    /**
     * 无需快递的物流发货
     *
     * @param dto the dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logisticsSend(ManageSearchLogisticsOrderDto dto) {
        if (StrUtil.isBlank(dto.getOrderIds())) {
            throw new ServiceException("订单条目不能为空");
        }
        List<Long> orderIds =
                Arrays.stream(dto.getOrderIds().split(StrUtil.COMMA)).map(Long::parseLong).toList();
        OrderSetting orderSetting = orderSettingMapper.selectOne(null);
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setDeliveryCompany("无");
        orderDelivery.setDeliverySn("无");
        orderDelivery.setDeliveryTime(LocalDateTime.now());
        orderDeliveryMapper.update(orderDelivery, new LambdaQueryWrapper<OrderDelivery>().in(OrderDelivery::getOrderId,
                orderIds));
        Order order = new Order();
        order.setStatus(OrderStatusEnum.WAIT_FOR_PICKUP);
        baseMapper.update(order, new LambdaQueryWrapper<Order>().in(Order::getId, orderIds));

        for (Long orderId : orderIds) {
            OrderVo vo = baseMapper.selectOrderVoById(orderId);
            //查询会员持有的积分、收货地址
            sender.sendDeliveryMessage(vo);
            BaseOrderMessage baseOrderMessage = new BaseOrderMessage();
            baseOrderMessage.setOrderId(vo.getId());
            baseOrderMessage.setShopId(vo.getShopId());
            sender.sendAutoReceiptOrderMessage(baseOrderMessage,
                    orderSetting.getConfirmOvertime() * TimeConstants.ONE_DAY);
        }
    }

    /**
     * 商家移出发货单
     *
     * @param productIds the product ids
     */
    @Override
    public void removeSendBillByProductIds(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return;
        }
        List<Long> orderIds = baseMapper.selectShippedOrderByProductIds(productIds);
        if (orderIds.isEmpty()) {
        } else {
            this.removeSendBill(orderIds);
        }
    }

    /**
     * 查询激励佣金详情
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     */
    @Override
    public PageUtils<ManageOrderVo> searchIncentiveDetail(IncentiveDetailDto dto) {
        DateTime dateTime = DateUtil.parse(dto.getMonth(), "yyyy-MM");
        DateTime beginTime = DateUtil.beginOfMonth(dateTime);
        DateTime endTime = DateUtil.endOfMonth(dateTime);
        Page<ManageOrderVo> page = baseMapper.searchIncentiveDetailPage(new Page(dto.getCurrent(), dto.getSize()),
                beginTime.toString(), endTime.toString(), dto.getAccountType(), dto.getUserId(), dto.getDeliverType());

        return new PageUtils<>(page);
    }
}
