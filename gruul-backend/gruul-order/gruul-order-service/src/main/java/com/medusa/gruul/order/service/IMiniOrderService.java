package com.medusa.gruul.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.PageUtils;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.logistics.model.dto.manager.CountCostDto;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.model.CreateOrderDto;
import com.medusa.gruul.order.api.model.OrderOverviewVo;
import com.medusa.gruul.order.api.model.OrderUser;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.order.model.*;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
public interface IMiniOrderService extends IService<Order> {

    /**
     * 生成确认单信息
     *
     * @param dto the dto
     * @return com.medusa.gruul.order.web.result.ConfirmOrderVo confirm order
     * @Author alan
     * @Date 2023/10/6 12:57
     */
    ConfirmOrderVo getConfirmOrder(ConfirmOrderDto dto);


    /**
     * 生成订单
     *
     * @param dto          the dto
     * @param orderId      the order id
     * @param skuStockList the sku stock list
     * @param curUser      the cur user
     * @return void boolean
     * @author alan
     * @date 2023/11/18 22:09
     */
    Boolean createOrder(CreateOrderDto dto, Long orderId, List<SkuStock> skuStockList, OrderUser curUser);

    /**
     * 检查库存是否足够
     *
     * @param createOrderDto the create order dto
     * @return java.lang.Long string
     * @author alan
     * @date 2023/10/24 22:04
     */
    String preCheckOrder(CreateOrderDto createOrderDto);

    /**
     * 用户取消订单
     *
     * @param orderId the order id
     * @return void
     * @author alan
     * @date 2023/11/20 20:32
     */
    void cancelOrder(Long orderId);

    /**
     * 支付超时取消
     *
     * @param orderId the order id
     * @return void
     * @author alan
     * @date 2023/11/28 21:27
     */
    void autoCancelOrder(Long orderId);

    /**
     * 用户支付订单
     *
     * @param orderId     the order id
     * @param userBalance
     * @param request     the request
     * @return void pay result dto
     * @author alan
     * @date 2023/11/20 20:39
     */
    PayResultDto payOrder(Long orderId, Boolean userBalance, HttpServletRequest request);

    /**
     * 支付回调
     *
     * @param orderId  the order id
     * @return void
     * @author alan
     * @date 2023/11/20 20:39
     */
    void paymentNotify(Long orderId);

    /**
     * 用户确认收货
     *
     * @param orderId  the order id
     * @param isSystem the is system
     * @return void
     * @author alan
     * @date 2023/11/20 22:07
     */
    void receiptOrder(Long orderId, boolean isSystem);

    /**
     * 检查活动是否结束
     *
     * @param dto the dto
     * @return java.lang.Integer integer
     * @author alan
     * @date 2023/11/23 11:04
     */
    Integer checkOrder(GroupOrderResultDto dto);

    /**
     * 用户订单列表
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2023/11/25 21:42
     */
    PageUtils<ApiOrderVo> searchOrder(ApiSearchOrderDto dto);

    /**
     * 用户评价订单
     *
     * @param dto the dto
     * @return void
     * @author alan
     * @date 2023/11/25 21:41
     */
    void evaluateOrder(ApiOrderEvaluateDto dto);

    /**
     * 系统评价订单
     *
     * @param orderId
     * @return void
     * @author alan
     * @date 2023/11/25 21:41
     */
    void evaluateOrder(Long orderId);

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
     * 用户查看评价列表
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2023/12/4 20:20
     */
    PageUtils<ManageEvaluateVo> searchOrderEvaluate(ApiSearchEvaluateDto dto);

    /**
     * 自动完成订单
     *
     * @param orderId the order id
     * @return void
     * @author alan
     * @date 2023/12/8 19:54
     */
    void completeOrder(Long orderId);


    /**
     * 我的订单概况
     *
     * @param
     * @return com.medusa.gruul.order.api.model.OrderOverviewVo order overview vo
     * @author alan
     * @date 2024/2/19 21:53
     */
    OrderOverviewVo orderOverview();

    /**
     * Order overview order overview vo.
     *
     * @param userId the user id
     * @return the order overview vo
     */
    OrderOverviewVo orderOverview(String userId);

    /**
     * 小程序查询晒单内容
     *
     * @param orderId the order id
     * @return the order share info
     */
    OrderShareInfo orderShareInfo(Long orderId);

    /**
     * 获取接龙活动最近的买家
     *
     * @param solitaireActivityId the solitaire activity id
     * @return the list
     */
    List<LatelyBuyerVo> solitaireLatelyBuyer(Integer solitaireActivityId);

    /**
     * 获取接龙活动买家
     *
     * @param dto the dto
     * @return the page utils
     */
    PageUtils<SolitaireBuyerVo> solitaireBuyerPage(SolitaireBuyerDto dto);

    /**
     * 获取我的接龙活动订单
     *
     * @param solitaireActivityId the solitaire activity id
     * @return the list
     */
    List<SolitaireBuyerVo> mySolitaireOrder(Integer solitaireActivityId);

    /**
     * 订单退款回调
     *
     * @param message the message
     */
    void refundNotify(RefundNotifyResultDto message);

    /**
     * 重新获取运费
     *
     * @param dto the dto
     * @return java.math.BigDecimal freight amount
     * @author alan
     * @date 2024/1/4 21:16
     */
    CountCostDto getFreightAmount(GetCostDto dto);
}
