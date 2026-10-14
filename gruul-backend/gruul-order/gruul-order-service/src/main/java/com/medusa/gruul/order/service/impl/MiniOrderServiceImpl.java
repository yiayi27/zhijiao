package com.medusa.gruul.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.api.entity.MiniAccountAddress;
import com.medusa.gruul.account.api.enums.BlacklistEnum;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.*;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.api.enums.AfsOrderStatusEnum;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.IPUtils;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.discount.api.feign.RemoteDiscountService;
import com.medusa.gruul.discount.api.model.vo.manager.*;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.entity.SkuStock;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.goods.api.model.vo.api.ApiSkuRebateVo;
import com.medusa.gruul.goods.api.model.vo.manager.ItemVo;
import com.medusa.gruul.goods.api.model.vo.manager.MemberPriceVo;
import com.medusa.gruul.goods.api.param.OperateStockDto;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.integral.api.model.DiscountInfoDto;
import com.medusa.gruul.logistics.api.feign.RemoteLogisticsFeginService;
import com.medusa.gruul.logistics.model.dto.manager.CountCostDto;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsFreightDto;
import com.medusa.gruul.order.api.constant.OrderCode;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.constant.OrderQueueEnum;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.DeliverTypeEnum;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import com.medusa.gruul.order.api.enums.PayTypeEnum;
import com.medusa.gruul.order.api.model.*;
import com.medusa.gruul.order.mapper.*;
import com.medusa.gruul.order.model.*;
import com.medusa.gruul.order.mq.Sender;
import com.medusa.gruul.order.service.IMiniOrderService;
import com.medusa.gruul.order.service.IOrderShareSettingService;
import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.payment.api.model.dto.PayRequestDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDto;
import com.medusa.gruul.platform.api.feign.RemoteMiniInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
public class MiniOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IMiniOrderService {
    @Resource
    private RemoteGoodsService remoteGoodsService;
    @Resource
    private RemotePaymentService remotePaymentService;
    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;
    @Resource
    private RemoteIntegralService remoteIntegralService;
    @Resource
    private RemoteLogisticsFeginService remoteLogisticsFeginService;
    @Resource
    private RemoteDiscountService remoteDiscountService;
    @Resource
    private IOrderShareSettingService orderShareSettingService;
    @Resource
    private OrderDeliveryMapper orderDeliveryMapper;
    @Resource
    private OrderSettingMapper orderSettingMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private OrderEvaluateMapper orderEvaluateMapper;
    @Resource
    private OrderProductEvaluateMapper productEvaluateMapper;
    @Resource
    private Sender sender;
    @Resource
    private AfsOrderMapper afsOrderMapper;


    /**
     * 根据用户选择的商品返回结算页需要的数据
     *
     * @param dto 确认订单参数
     * @return com.medusa.gruul.order.model.ConfirmOrderVo
     */
    @Override
    public ConfirmOrderVo getConfirmOrder(ConfirmOrderDto dto) {
        TimeInterval timer = DateUtil.timer();
        SecureUser<?> secureUser = ISecurity.userMust();
        log.debug("当前用户信息:{}", secureUser);

        if (ObjectUtil.isNull(secureUser)) {
            throw new ServiceException(SystemCode.UNAUTHORIZED);
        }
        ConfirmOrderVo result = new ConfirmOrderVo();
        //查询商品库存情况
        List<ItemVo> itemVoList = remoteGoodsService.findItemVoByIds(dto.getItemSkuIds());
        if (CollUtil.isEmpty(itemVoList)) {
            throw new ServiceException(OrderCode.DATA_HAS_BEEN_UPDATED);
        }
        Map<Long, ItemVo> itemVoMap = itemVoList.stream().collect(Collectors.toMap(ItemVo::getProductSkuId, v -> v));
        for (ItemDto item : dto.getItems()) {
            ItemVo itemVo = itemVoMap.get(item.getSkuId());
            itemVo.setProductQuantity(item.getNumber());
            String stock = RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, item.getSkuId()));
            if (ObjectUtil.isNull(stock)) {
                throw new ServiceException(OrderCode.THIRD_PARTY_SERVICE_EXCEPTION);
            }
            if (Integer.parseInt(stock) > item.getNumber()) {
                //库存状态：0->有库存
                itemVo.setStatus(CommonConstants.NUMBER_ZERO);
            } else {
                //库存状态：1->无库存
                itemVo.setStatus(CommonConstants.NUMBER_ONE);
            }
        }
        result.setItemVoList(itemVoList);
        log.info(ObjectUtil.isNotNull(itemVoList) ? itemVoList.toString() : "");
        log.info("查询商品耗时{}ms", timer.intervalRestart());
        //查询用户持有的积分、收货地址
        AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(secureUser.getId(), Arrays.asList(2,
                3, 5));
        result.setSupplyBonus(accountInfoDto.getMiniAccountExtends().getSupplyBonus());
        result.setIntegral(accountInfoDto.getMiniAccountExtends().getIntegral());
        result.setMiniAccountAddress(accountInfoDto.getMiniAccountAddress());
        log.info(ObjectUtil.isNotNull(accountInfoDto) ? accountInfoDto.toString() : "");
        log.info("查询用户耗时{}ms", timer.intervalRestart());
        //查询用户的会员情况
        MemberCardInfoCenterDto memberInfo = remoteMiniAccountService.getMemberInfo(secureUser.getId());

        if (ObjectUtil.isNotNull(memberInfo)) {
            //如果当前用户是会，返回会员信息
            result.setMemberInfo(memberInfo);
            log.info(ObjectUtil.isNotNull(memberInfo) ? memberInfo.toString() : "");
        } else {
            //如果当前用户不是会员，返回当前最高的会员等级，用于计算"开通会员本单立省"的金额
            MemberTopCardInfoDto topMemberCardInfo =
                    remoteMiniAccountService.getTopMemberCardInfo();
            result.setTopMemberCardInfo(topMemberCardInfo);
            log.info(ObjectUtil.isNotNull(topMemberCardInfo) ? topMemberCardInfo.toString() : "");
        }
        log.info("查询会员耗时{}ms", timer.intervalRestart());
        //查询积分使用规则
        OrderSetting orderSetting = orderSettingMapper.selectOne(null);
        DiscountInfoDto discountInfoDto = new DiscountInfoDto();
        discountInfoDto.setCeiling(orderSetting.getCeiling());
        discountInfoDto.setRatio(orderSetting.getRatio());
        result.setDiscountInfoDto(discountInfoDto);
        log.info(ObjectUtil.isNotNull(discountInfoDto) ? discountInfoDto.toString() : "");
        log.info("查询积分耗时{}ms", timer.intervalRestart());

        //查询满减活动信息
        Set<Long> productIds = itemVoList.stream().map(ItemVo::getProductId).collect(Collectors.toSet());
        List<FullScaleVo> fullScaleList = remoteDiscountService.findFullScaleListForOrder(new ArrayList<>(productIds));
        result.setFullScales(fullScaleList);
        log.info(ObjectUtil.isNotNull(fullScaleList) ? fullScaleList.toString() : "");
        log.info("查询满减耗时{}ms", timer.intervalRestart());

        //查询单个客户优惠劵信息
        List<MemberCouponVo> memberCouponList =
                remoteDiscountService.findMemberCouponListForOrder(secureUser.getId(), new ArrayList<>(productIds));
        result.setMemberCoupons(memberCouponList);
        log.info(ObjectUtil.isNotNull(memberCouponList) ? memberCouponList.toString() : "");
        log.info("查询优惠劵耗时{}ms", timer.intervalRestart());
        return result;
    }


    /**
     * 获取运费的参数包装类，将参数包装成需要的格式
     *
     * @param miniAccountAddress 地址
     * @param deliverType 配送方式
     * @param itemDtoList 订单明细
     * @return com.medusa.gruul.shipping.model.dto.CountCostDto
     */
    private CountCostDto getFreightAmount(MiniAccountAddress miniAccountAddress,
                                          DeliverTypeEnum deliverType,
                                          List<ItemDto> itemDtoList) {
        //查询运费
        log.info("miniAccountAddress is {}", JSONUtil.toJsonStr(miniAccountAddress));
        log.info("deliverType is {}", deliverType.getDesc());
        CountCostDto countCostDto;
        GetCostDto getCostDto = new GetCostDto();
        getCostDto.setType(CommonConstants.NUMBER_ONE);
        getCostDto.setRegion(miniAccountAddress.getPostCode());
        getCostDto.setItems(itemDtoList);
        log.info("getCostDto is {}", JSONUtil.toJsonStr(getCostDto));
        countCostDto = getFreightAmount(getCostDto);
        log.info("CountCostDto is {}", JSONUtil.toJsonStr(countCostDto));
        return countCostDto;
    }

    /**
     * 提前订单时候的前置检查
     *
     * @param createOrderDto 创建订单参数
     * @return java.lang.String
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String preCheckOrder(CreateOrderDto createOrderDto) {
        SecureUser<?> secureUser = ISecurity.userMust();
        //查询会员持有的积分、收货地址
        String userId = secureUser.getId();
        AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(userId, Arrays.asList(2,
                3, 5));
        log.debug("当前用户信息:{}", secureUser);
        //检查账号
        checkAccount(accountInfoDto);
        //检查缓存中商品库存
        checkStock(createOrderDto);
        //查询用户的会员情况
        MemberCardInfoCenterDto memberInfo = remoteMiniAccountService.getMemberInfo(userId);
        List<ItemVo> itemVoList = remoteGoodsService.findItemVoByIds(createOrderDto.getItemSkuIds());
        if (CollUtil.isEmpty(itemVoList)) {
            throw new ServiceException(OrderCode.DATA_HAS_BEEN_UPDATED);
        }
        //计算订单总金额
        BigDecimal totalAmount = getTotalAmount(itemVoList, createOrderDto.getItemDtoList());
        //检查限购
        checkLimit(itemVoList, userId);
        //查询返利设置
        RebateSet rebateSet = remoteGoodsService.getRebateSet(ISecurity.userMust().getShopId());
        List<OrderItem> orderItemList = getOrderItemList(itemVoList, 1L, createOrderDto.getItemDtoList(),
                memberInfo, rebateSet);
        //检查优惠券
        checkCoupon(orderItemList, createOrderDto.getCouponId(), userId, totalAmount);
        //检查满减活动
        checkFullScale(orderItemList, createOrderDto, totalAmount);
        //检查积分
        checkIntegral(totalAmount, createOrderDto.getIntegral(), accountInfoDto.getMiniAccountExtends().getIntegral());
        //检查收货地址
        MiniAccountAddress accountAddress = checkAddress(accountInfoDto.getMiniAccountAddress(), createOrderDto);
        //计算运费
        BigDecimal freightAmount =
                getFreightAmount(accountAddress, createOrderDto.getDeliverType(),
                        createOrderDto.getItemDtoList()).getCost();

        if (createOrderDto.getDeliverType().equals(DeliverTypeEnum.LOGISTICS)) {
            if (ObjectUtil.isNull(freightAmount) || freightAmount.equals(BigDecimal.valueOf(-1))) {
                throw new ServiceException(OrderCode.NOT_IN_THE_SCOPE_OF_DISTRIBUTION);
            }
        }
        //检查通过发送创建订单的消息
        CreateOrderMessage message = new CreateOrderMessage();
        message.setOrderVo(createOrderDto);
        //预生成的订单ID
        Long orderId = IdWorker.getId();
        message.setOrderId(orderId);
        message.setUser(
                new OrderUser()
                        .setId(userId)
                        .setShopId(secureUser.getShopId())
                        .setOpenid(secureUser.getOpenid())
                        .setNickname(secureUser.getNickname())
                        .setAvatar(secureUser.getAvatar())
        );
        sender.sendCreateOrderMessage(message);
        return orderId.toString();
    }

    /**
     * 检查是否符合限购要求
     *
     * @param itemVoList 订单商品信息
     * @param userId 用户ID
     */
    private void checkLimit(List<ItemVo> itemVoList, String userId) {
        for (ItemVo itemVo : itemVoList) {
            if (ObjectUtil.isNotNull(itemVo.getPerLimit()) && itemVo.getPerLimit() != 0) {
                if (itemVo.getLimitType() != 1) {
                    Integer total = orderItemMapper.countSkuPurchased(itemVo.getProductSkuId(), userId);

                    total = ObjectUtil.isNull(total) ? 0 : total;
                    if (total > 0) {
                        Integer returnNum = afsOrderMapper.countSkuReturn(itemVo.getProductSkuId(), userId);
                        returnNum = ObjectUtil.isNull(returnNum) ? 0 : returnNum;
                        total = total - returnNum;
                    }

                    if (total + itemVo.getProductQuantity() > itemVo.getPerLimit()) {
                        throw new ServiceException(StrUtil.format("商品{}已经超过限购数量", itemVo.getProductName()));
                    }
                } else {
                    List<ItemVo> productItemList =
                            itemVoList.stream().filter(vo -> vo.getProductId().equals(itemVo.getProductId())).toList();
                    int total = 0;
                    for (ItemVo vo : productItemList) {
                        Integer skuPurchase = orderItemMapper.countSkuPurchased(vo.getProductSkuId(), userId);
                        skuPurchase = ObjectUtil.isNull(skuPurchase) ? 0 : skuPurchase;
                        if (skuPurchase > 0) {
                            Integer returnNum = afsOrderMapper.countSkuReturn(itemVo.getProductSkuId(), userId);
                            returnNum = ObjectUtil.isNull(returnNum) ? 0 : returnNum;
                            skuPurchase = skuPurchase - returnNum;
                        }
                        total = total + skuPurchase;
                    }
                    if (total + itemVo.getProductQuantity() > itemVo.getPerLimit()) {
                        throw new ServiceException(StrUtil.format("商品{}已经超过限购数量", itemVo.getProductName()));
                    }
                }

            }
        }
    }

    /**
     * 检查账户是否在黑名单中
     *
     * @param accountInfoDto 账户信息
     */
    private void checkAccount(AccountInfoDto accountInfoDto) {
        if (ObjectUtil.isNull(accountInfoDto)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        if (CollUtil.isNotEmpty(accountInfoDto.getRestrictTypes()) && accountInfoDto.getRestrictTypes().contains(BlacklistEnum.REJECT_ORDER.getType())) {
            throw new ServiceException(OrderCode.ACCOUNT_NUMBER_EXCEPTION);
        }
    }


    /**
     * 检查商品库存
     *
     * @param createOrderDto 订单信息
     */
    private void checkStock(CreateOrderDto createOrderDto) {
        List<ItemDto> itemDtoList = createOrderDto.getItemDtoList();
        for (ItemDto itemDto : itemDtoList) {
            //redis预减库存
            Integer stock = Integer.valueOf(RedisUtil.getCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, itemDto.getSkuId())));
            if (stock < itemDto.getNumber()) {
                //读取数据库，刷新缓存，查看商品是否确实售罄
                SkuStock skuStock = remoteGoodsService.findSkuStockById(itemDto.getSkuId());
                if (ObjectUtil.isNotNull(skuStock)) {
                    stock = skuStock.getStock();
                }
                if (stock < 1) {
                    throw new ServiceException(SystemCode.ITEM_SOLD_OUT);
                }
            }
            stock = stock - itemDto.getNumber();
            RedisUtil.setCacheObject(RedisUtil.key(Keys.GOODS_SKU_STOCK, itemDto.getSkuId()), String.valueOf(stock));
        }
    }


    /**
     * 根据商品的销售价格获取总价
     *
     * @param itemVoList 商品查询信息
     * @param itemDtoList 订单商品传输数量
     * @return java.math.BigDecimal
     */
    private BigDecimal getTotalAmount(List<ItemVo> itemVoList, List<ItemDto> itemDtoList) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<Long, ItemVo> itemVoMap = itemVoList.stream().collect(Collectors.toMap(ItemVo::getProductSkuId, v -> v));
        for (ItemDto itemDto : itemDtoList) {
            ItemVo itemVo = itemVoMap.get(itemDto.getSkuId());
            itemVo.setProductQuantity(itemDto.getNumber());
            BigDecimal itemTotal = itemVo.getProductPrice().multiply(BigDecimal.valueOf(itemVo.getProductQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        return totalAmount;
    }

    /**
     * 检查优惠券是否符合要求
     *
     * @param orderItemList 订单商品信息
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @param totalAmount 订单总金额
     */
    private void checkCoupon(List<OrderItem> orderItemList, Long couponId, String userId, BigDecimal totalAmount) {
        if (ObjectUtil.isNotNull(couponId) && couponId > 0L) {
            MemberCouponVo memberCouponVo = remoteDiscountService.findMemberCouponForOrder(userId, couponId);
            if (ObjectUtil.isNull(memberCouponVo)) {
                throw new ServiceException("优惠券不满足使用条件:无效数据", SystemCode.DATA_EXISTED_CODE);
            }
            // 0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵
            switch (memberCouponVo.getCouponType()) {
                case 0:
                case 1:
                    break;
                case 2:
                case 3:
                    //总金额小于满减金额
                    if (totalAmount.compareTo(memberCouponVo.getConditionAmount()) == -1) {
                        throw new ServiceException("优惠券不满足使用条件:订单总金额未满足");
                    }
                    break;
                default:
                    throw new ServiceException(SystemCode.PARAM_VALID_ERROR);
            }
            //适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与
            Set<Long> couponProductIds;
            Set<Long> productIds = orderItemList.stream().map(OrderItem::getProductId).collect(Collectors.toSet());
            switch (memberCouponVo.getProductType()) {
                case 0:
                    break;
                case 1:
                    couponProductIds =
                            memberCouponVo.getCouponProducts().stream().map(CouponProductVo::getProductId).collect(Collectors.toSet());
                    if (!CollUtil.containsAny(couponProductIds, productIds)) {
                        throw new ServiceException("优惠券不满足使用条件:订单未包含指定参与的商品");
                    }
                    break;
                case 2:
                    couponProductIds =
                            memberCouponVo.getCouponProducts().stream().map(CouponProductVo::getProductId).collect(Collectors.toSet());
                    if (CollUtil.containsAny(couponProductIds, productIds)) {
                        throw new ServiceException("优惠券不满足使用条件:订单包含指定不参与的商品");
                    }
                    break;
                default:
                    throw new ServiceException(SystemCode.PARAM_VALID_ERROR);

            }
        }
    }


    /**
     * 检查满减是否符合要求
     *
     * @param orderItemList 订单商品信息
     * @param createOrderDto 订单信息
     * @param totalAmount 订单总金额
     */
    private void checkFullScale(List<OrderItem> orderItemList, CreateOrderDto createOrderDto, BigDecimal totalAmount) {
        if (ObjectUtil.isNotNull(createOrderDto.getFullScaleId()) && createOrderDto.getFullScaleId() > 0L) {
            FullScaleVo fullScaleVo = remoteDiscountService.findFullScaleForOrder(createOrderDto.getFullScaleId());
            if (ObjectUtil.isNull(fullScaleVo)) {
                throw new ServiceException("活动不满足使用条件:无效数据", SystemCode.DATA_NOT_EXIST_CODE);
            }
            if (CommonConstants.NUMBER_ZERO.equals(fullScaleVo.getStatus())) {
                throw new ServiceException("活动不满足使用条件:活动未开始");
            }
            if (CommonConstants.NUMBER_TWO.equals(fullScaleVo.getStatus())) {
                throw new ServiceException("活动不满足使用条件:活动已经结束");
            }
            if (StringUtils.isBlank(fullScaleVo.getDeductionType()) && ObjectUtil.isNotNull(createOrderDto.getIntegral()) && createOrderDto.getIntegral().compareTo(BigDecimal.ZERO) == 1) {
                throw new ServiceException("活动不满足使用条件:不能和积分抵扣同时使用");
            }

            if (StringUtils.isNotBlank(fullScaleVo.getApplyType())) {
                List<String> applyTypeList = StrUtil.split(fullScaleVo.getApplyType(), StrUtil.C_COMMA);
                if (!applyTypeList.contains(CommonConstants.STRING_ZERO) && createOrderDto.getUseMemberPrice()) {
                    throw new ServiceException("活动不满足使用条件:不能和会员价同时使用");
                }
                if (!applyTypeList.contains(CommonConstants.STRING_ONE) && ObjectUtil.isNotNull(createOrderDto.getCouponId())) {
                    throw new ServiceException("活动不满足使用条件:不能和优惠券同时使用");
                }
            }
            //适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与
            Set<Long> fullScaleProductIds =
                    fullScaleVo.getFullScaleProducts().stream().map(FullScaleProductVo::getProductId).collect(Collectors.toSet());
            Set<Long> productIds = orderItemList.stream().map(OrderItem::getProductId).collect(Collectors.toSet());
            switch (fullScaleVo.getProductType()) {
                case 0:
                    break;
                case 1:
                    if (!CollUtil.containsAny(fullScaleProductIds, productIds)) {
                        throw new ServiceException("活动不满足使用条件:订单未包含指定参与的商品");
                    }
                    break;
                case 2:
                    if (CollUtil.containsAny(fullScaleProductIds, productIds)) {
                        throw new ServiceException("活动不满足使用条件:订单包含指定不参与的商品");
                    }
                    break;
                default:
                    throw new ServiceException(SystemCode.PARAM_VALID_ERROR);
            }
            FullScaleRuleVo ruleVo =
                    fullScaleVo.getFullScaleRules().stream().min(Comparator.comparing(FullScaleRuleVo::getConditionAmount)).get();
            //总金额小于满减金额
            if (totalAmount.compareTo(ruleVo.getConditionAmount()) == -1) {
                throw new ServiceException("活动不满足使用条件:订单总金额未满足");
            }
        }
    }


    /**
     * 检查积分是否符合要求
     *
     * @param totalAmount 订单总金额
     * @param integral 积分
     * @param allIntegral 用户持有的积分
     */
    private void checkIntegral(BigDecimal totalAmount, BigDecimal integral, BigDecimal allIntegral) {
        if (integral.compareTo(BigDecimal.ZERO) > 0) {
            if (allIntegral.compareTo(integral) < 0 || allIntegral.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("积分不满足使用条件:积分不足");
            } else {
                OrderSetting orderSetting = orderSettingMapper.selectOne(null);
                BigDecimal discountAmount = NumberUtil.mul(totalAmount, NumberUtil.div(orderSetting.getCeiling(),
                        100));
                BigDecimal needIntegral = NumberUtil.div(discountAmount, NumberUtil.div(orderSetting.getRatio(),
                        100), 2);
                if (needIntegral.compareTo(integral) != 0) {
                    log.error("当前总金额为:{},当前规则为:{},前端提交的积分为:{},后端计算积分应该为:{}", totalAmount, orderSetting,
                            integral, needIntegral);
                    throw new ServiceException("积分不满足使用条件:积分计算错误");
                }
            }
        }
    }


    /**
     * 检查用户收货地址信息
     *
     * @param addressList 收货地址列表
     * @param dto 订单信息
     * @return com.medusa.gruul.account.api.entity.MiniAccountAddress
     */
    private MiniAccountAddress checkAddress(List<MiniAccountAddress> addressList, CreateOrderDto dto) {
        MiniAccountAddress accountAddress = new MiniAccountAddress();
        if (CollUtil.isEmpty(addressList)) {
            throw new ServiceException("收货地址数据已更新,请刷新后重试");
        }
        for (MiniAccountAddress address : addressList) {
            if (address.getId().equals(dto.getMiniAccountAddressId())) {
                accountAddress = address;
            }
        }
        if (ObjectUtil.isNull(accountAddress)) {
            throw new ServiceException(OrderCode.NO_VALID_SHIP_TO_ADDRESS);
        }
        return accountAddress;
    }

    /**
     * 创建订单
     *
     * @return java.lang.Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createOrder(CreateOrderDto dto, Long orderId, List<SkuStock> skuStockList, OrderUser curUser) {
        boolean done = false;
        log.debug("当前用户信息:{}", curUser);
        String userId = curUser.getId();
        try {
            //查询会员持有的积分、收货地址
            AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(userId, Arrays.asList(2
                    , 3, 5));
            //查询用户的会员情况
            MemberCardInfoCenterDto memberInfo =
                    remoteMiniAccountService.getMemberInfo(userId);
            //查询商品描述信息
            List<ItemVo> itemVoList = remoteGoodsService.findItemVoByIds(dto.getItemSkuIds());
            //查询返利设置
            RebateSet rebateSet = remoteGoodsService.getRebateSet(curUser.getShopId());
            //获得当前订单的总金额
            BigDecimal totalAmount = getTotalAmount(itemVoList, dto.getItemDtoList());
            //创建订单详情并且获得每个商品的总价
            List<OrderItem> orderItemList = getOrderItemList(itemVoList, orderId, dto.getItemDtoList(), memberInfo,
                    rebateSet);
            //计算优惠券
            BigDecimal couponAmount = handleCouponAmount(orderItemList, dto.getCouponId(), userId);
            //计算满减
            BigDecimal fullScaleAmount = handleFullScaleAmount(orderItemList, dto.getFullScaleId());
            //计算积分抵扣
            BigDecimal integralAmount = handleIntegralAmount(totalAmount, dto.getIntegral(),
                    accountInfoDto.getMiniAccountExtends().getIntegral());
            // TODO 并不是每个商品都参与了优惠 平摊存在不合理
            //将订单的总优惠平摊到商品上
            shareDiscount(orderItemList, couponAmount, fullScaleAmount, integralAmount);
            //获取用户收货地址
            MiniAccountAddress accountAddress = checkAddress(accountInfoDto.getMiniAccountAddress(), dto);
            //计算运费
            CountCostDto costDto = getFreightAmount(accountAddress, dto.getDeliverType(),
                    dto.getItemDtoList());
            BigDecimal freightAmount = costDto.getCost();
            if (ObjectUtil.isNull(freightAmount) || freightAmount.equals(BigDecimal.valueOf(-1))) {
                freightAmount = BigDecimal.ZERO;
            }
            BigDecimal promotionAmount = BigDecimal.ZERO;
            // 尝试使用会员权益中的包邮
            if (ObjectUtil.isNotNull(memberInfo) && CollUtil.isNotEmpty(memberInfo.getMemberCardPrivileges())) {
                Optional<MemberCardPrivilege> optionalMemberCardPrivilege =
                        memberInfo.getMemberCardPrivileges().stream().filter(memberCardPrivilege -> "2".equalsIgnoreCase(memberCardPrivilege.getPType())).findFirst();
                if (optionalMemberCardPrivilege.isPresent()) {
                    promotionAmount = freightAmount;
                }
            }
            //扣除库存
            List<ItemDto> itemDtoList = dto.getItemDtoList();
            Set<OperateStockDto> skuSet = new HashSet<>();
            for (ItemDto itemDto : itemDtoList) {
                skuSet.addAll(skuStockList.stream()
                        .map(vo -> new OperateStockDto(itemDto.getSkuId(), itemDto.getNumber()))
                        .collect(Collectors.toSet()));
            }
            //扣除优惠券
            List<OperateStockDto> operateStockDtoList = new ArrayList<>(skuSet);
            if (ObjectUtil.isNotNull(dto.getCouponId()) && dto.getCouponId() > 0) {
                boolean couponSuccess = remoteDiscountService.useMemberCoupon(dto.getCouponId());
                if (!couponSuccess) {
                    RedisUtil.setCacheObject(RedisUtil.key(Keys.ORDER_FAILED, String.valueOf(orderId)), "优惠券使用失败", TimeConstants.ONE_DAY, TimeUnit.MILLISECONDS);
                    return false;
                }
            }
            //扣除积分
            if (ObjectUtil.isNotNull(dto.getIntegral()) && NumberUtil.isGreater(dto.getIntegral(), BigDecimal.ZERO)) {
                log.info("integralSubtractAffirm request param is {},{},{}", userId,
                        dto.getIntegral(), true);
                Boolean integralSuccess = remoteMiniAccountService.integralSubtractAffirm(userId,
                        dto.getIntegral(), true,curUser.getShopId());
                log.info("integralSubtractAffirm result is {}", integralSuccess);
                if (!integralSuccess) {
                    OrderFailMessage failMessage = new OrderFailMessage()
                            .integralFail(dto.getCouponId(), userId, curUser.getShopId());
                    sender.sendCreateOrderFailMessage(failMessage);
                    RedisUtil.setCacheObject(RedisUtil.key(Keys.ORDER_FAILED, String.valueOf(orderId)), "积分使用失败", TimeConstants.ONE_DAY, TimeUnit.MILLISECONDS);
                    return false;
                }
            }
            //删除购物车数据
            remoteGoodsService.deleteShoppingCartByOrder(dto.getItemSkuIds(), userId);

            //扣除库存
            boolean goodsSuccess = remoteGoodsService.batchSubtractStock(operateStockDtoList);

            if (goodsSuccess) {
                //创建订单
                Order order = new Order();
                order.setId(orderId);
                order.setUserId(userId);
                order.setOpenId(curUser.getOpenid());
                order.setUserName(curUser.getNickname());
                order.setUserAvatarUrl(curUser.getAvatar());
                order.setUserNote(dto.getUserNote());
                order.setType(dto.getOrderType());
                order.setSolitaireActivityId(dto.getSolitaireActivityId());
                order.setMemberTemplateId(dto.getMemberTemplateId());
                order.setTotalAmount(totalAmount.add(freightAmount));

                //合计返利金额
                BigDecimal rebateAmount =
                        orderItemList.stream().map(OrderItem::getRebateAmount).reduce(BigDecimal.ZERO,
                                BigDecimal::add);
                order.setRebateAmount(rebateAmount);

                //合计分销金额
                BigDecimal firstDistributionAmount =
                        orderItemList.stream().map(OrderItem::getFirstDistributionAmount).reduce(BigDecimal.ZERO,
                                BigDecimal::add);
                order.setFirstDistributionAmount(firstDistributionAmount);

                //合计会员优惠
                BigDecimal memberAmount = orderItemList.stream().map(OrderItem::getMemberAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                order.setMemberAmount(memberAmount);
                //促销优化金额=积分抵扣+优惠券+满减+会员价+会员免运费
                promotionAmount =
                        NumberUtil.add(promotionAmount, integralAmount, couponAmount, fullScaleAmount, memberAmount);
                order.setPromotionAmount(promotionAmount);
                //应付金额（实际支付金额）=订单总金额-促销优化金额+运费
                BigDecimal payAmount = NumberUtil.sub(totalAmount, promotionAmount);
                if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
                    payAmount = BigDecimal.ZERO.add(freightAmount);
                } else {
                    payAmount = payAmount.add(freightAmount);
                }
                //支付金额异常

                if (NumberUtil.isLess(payAmount, OrderConstant.MIN_PAY_FEE)) {
                    log.error("订单{},支付金额:{},总金额:{},优惠券优惠:{},满减优惠:{},积分优惠:{},运费:{}", dto, payAmount,
                            totalAmount, couponAmount, fullScaleAmount, integralAmount, freightAmount);
                    payAmount = OrderConstant.MIN_PAY_FEE;
                }
                log.info("订单{},支付金额:{},总金额:{},优惠券优惠:{},满减优惠:{},积分优惠:{},运费:{}", dto, payAmount,
                        totalAmount, couponAmount, fullScaleAmount, integralAmount, freightAmount);
                Set<Long> activityIds =
                        orderItemList.stream().map(OrderItem::getActivityId)
                                .filter(ObjectUtil::isNotNull)
                                .collect(Collectors.toSet());
                if (CollUtil.isNotEmpty(activityIds)) {
                    order.setActivityIds(CollUtil.join(activityIds, StrUtil.COMMA));
                }
                //实际金额=应付金额-退款金额
                order.setDiscountsAmount(payAmount.setScale(2, RoundingMode.DOWN));
                order.setPayAmount(payAmount.setScale(2, RoundingMode.DOWN));

                order.setFreightAmount(freightAmount);

                order.setIntegration(dto.getIntegral());
                order.setIntegrationAmount(integralAmount);
                order.setCouponId(dto.getCouponId());
                order.setCouponAmount(couponAmount);
                order.setFullScaleId(dto.getFullScaleId());
                order.setFullScaleAmount(fullScaleAmount);
                order.setPayType(dto.getPayType());
                order.setSourceType(dto.getSourceType());
                order.setStatus(OrderStatusEnum.WAIT_FOR_PAY);
                BigDecimal giftIntegration =
                        orderItemList.stream().map(OrderItem::getGiftIntegration).reduce(BigDecimal.ZERO,
                                BigDecimal::add);
                order.setGiftIntegration(giftIntegration);
                order.setCustomForm(dto.getCustomForm());
                if (ObjectUtil.isNotNull(memberInfo) && CollUtil.isNotEmpty(memberInfo.getMemberCardPrivileges())) {
                    String privileges =
                            memberInfo.getMemberCardPrivileges().stream().map(MemberCardPrivilege::getPType).distinct().collect(Collectors.joining(","));
                    order.setPrivileges(privileges);
                }
                //创建订单收货
                installOrderDelivery(dto, orderId, accountAddress);
                //发送到期自动取消消息
                log.info("设置超时取消订单=====>");
                long exTime = getExTime(order.getType());
                sender.sendAutoCancelOrderMessage(
                        new BaseOrderMessage()
                                .setOrderId(orderId)
                                .setShopId(curUser.getShopId()),
                        exTime
                );
                order.setExpireTime(LocalDateTime.now().plusSeconds(exTime / 1000));
                baseMapper.insert(order);
                for (OrderItem orderItem : orderItemList) {
                    orderItemMapper.insert(orderItem);
                }
                done = true;
            } else {
                OrderFailMessage failMessage = new OrderFailMessage().stockFail(dto.getCouponId(), dto.getIntegral(),
                        userId, curUser.getShopId());
                sender.sendCreateOrderFailMessage(failMessage);
                RedisUtil.setCacheObject(RedisUtil.key(RedisUtil.key(Keys.ORDER_FAILED, String.valueOf(orderId))), "库存扣除失败", TimeConstants.ONE_DAY, TimeUnit.MILLISECONDS);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            RedisUtil.setCacheObject(RedisUtil.key(RedisUtil.key(Keys.ORDER_FAILED, String.valueOf(orderId))), e.getMessage(), TimeConstants.ONE_DAY, TimeUnit.MILLISECONDS);
        }
        return done;
    }


    private void shareDiscount(List<OrderItem> orderItemList, BigDecimal couponAmount, BigDecimal fullScaleAmount,
                               BigDecimal integralAmount) {
        BigDecimal totalRealAmount = orderItemList.stream()
                .map(OrderItem::getRealAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for (OrderItem orderItem : orderItemList) {
            log.info("优惠分解之前的数据：" + JSONUtil.toJsonStr(orderItem));
            orderItem.setPromotionAmount(NumberUtil.mul(fullScaleAmount, NumberUtil.div(orderItem.getRealAmount(),
                    totalRealAmount)));
            orderItem.setIntegrationAmount(NumberUtil.mul(integralAmount, NumberUtil.div(orderItem.getRealAmount(),
                    totalRealAmount)));
            BigDecimal realAmount = NumberUtil.sub(orderItem.getRealAmount(), orderItem.getCouponAmount(),
                    orderItem.getPromotionAmount(), orderItem.getIntegrationAmount(), orderItem.getMemberAmount());
            //Todo 向下舍入会出现差1分的情况
            orderItem.setRealAmount(realAmount.setScale(2, RoundingMode.HALF_UP));
            if (NumberUtil.isLessOrEqual(orderItem.getRealAmount(), BigDecimal.ZERO)) {
                orderItem.setRealAmount(BigDecimal.ZERO);
            }
            log.info("优惠分解之后的数据：" + JSONUtil.toJsonStr(orderItem));
        }
    }

    /**
     * 创建订单收货信息
     *
     * @param dto 订单信息
     * @param orderId 订单ID
     * @param accountAddress 收货地址
     */
    private void installOrderDelivery(CreateOrderDto dto, Long orderId,
                                      MiniAccountAddress accountAddress) {
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setOrderId(orderId);
        orderDelivery.setDeliveryType(dto.getDeliverType());
        orderDelivery.setDeliveryTemplateId(dto.getDeliveryTemplateId());
        orderDelivery.setReceived(false);
        if (ObjectUtil.isNotNull(accountAddress)) {
            orderDelivery.setReceiverName(accountAddress.getUserName());
            orderDelivery.setReceiverPhone(accountAddress.getPhone());
            orderDelivery.setReceiverPostCode(accountAddress.getPostCode());
            orderDelivery.setReceiverProvince(accountAddress.getProvince());
            orderDelivery.setReceiverCity(accountAddress.getCity());
            orderDelivery.setReceiverRegion(accountAddress.getCounty());
            orderDelivery.setReceiverDetailAddress(accountAddress.getDetailInfo());
        }
        orderDeliveryMapper.insert(orderDelivery);
    }

    /**
     * 组装订单详情表
     *
     * @param itemVoList 订单商品信息
     * @param orderId 订单ID
     * @param itemDtoList 订单商品传输信息
     * @param memberInfo 会员信息
     * @param rebateSet 返利设置
     * @return List<OrderItem>
     */
    private List<OrderItem> getOrderItemList(List<ItemVo> itemVoList, Long orderId, List<ItemDto> itemDtoList,
                                             MemberCardInfoCenterDto memberInfo, RebateSet rebateSet) {
        List<OrderItem> itemList = new LinkedList<>();
        Map<Long, ItemDto> itemDtoMap = itemDtoList.stream().collect(Collectors.toMap(ItemDto::getSkuId, v -> v));
        for (ItemVo itemVo : itemVoList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(itemVo.getProductId());
            if (StrUtil.isNotBlank(itemVo.getProductSkuPic())) {
                orderItem.setProductPic(itemVo.getProductSkuPic());
            } else {
                orderItem.setProductPic(itemVo.getProductPic());
            }
            orderItem.setProductName(itemVo.getProductName());
            orderItem.setProductSn(itemVo.getProductSn());
            orderItem.setProductPrice(itemVo.getProductPrice());
            orderItem.setMemberAmount(BigDecimal.ZERO);
            orderItem.setProductOriginalPrice(itemVo.getProductOriginalPrice());
            orderItem.setProductQuantity(itemVo.getProductQuantity());
            orderItem.setProductSkuId(itemVo.getProductSkuId());
            orderItem.setProductSkuCode(itemVo.getProductSkuCode());
            orderItem.setPromotionAmount(BigDecimal.ZERO);
            orderItem.setCouponAmount(BigDecimal.ZERO);
            orderItem.setIntegrationAmount(BigDecimal.ZERO);
            // 处理会员价逻辑
            setMemberAmount(memberInfo, itemVo, orderItem);
            // 设置商品返利金额
            setRebateAmount(memberInfo, rebateSet, itemVo, orderItem);
            //设置商品分销金额
            setDistributionAmount(memberInfo, itemVo, orderItem);
            //该商品经过优惠后的最终金额
            orderItem.setRealAmount(orderItem.getProductPrice().multiply(BigDecimal.valueOf(itemVo.getProductQuantity())));

            //计算商品赠送积分
            BigDecimal giftIntegration = BigDecimal.valueOf(itemVo.getGiftIntegration());
            if (ObjectUtil.isNotNull(memberInfo) && CollUtil.isNotEmpty(memberInfo.getMemberCardPrivileges())) {
                giftIntegration = giftIntegration.multiply(BigDecimal.valueOf(itemVo.getProductQuantity()));
                for (MemberCardPrivilege memberCardPrivilege : memberInfo.getMemberCardPrivileges()) {
                    if (CommonConstants.STRING_THREE.equalsIgnoreCase(memberCardPrivilege.getPType())) {
                        giftIntegration = NumberUtil.mul(giftIntegration, memberCardPrivilege.getDefValue());
                    }
                }
            }
            orderItem.setGiftIntegration(giftIntegration);
            orderItem.setSpecs(itemVo.getSpecs());
            orderItem.setProviderId(itemVo.getProviderId());
            if (ObjectUtil.isNotNull(itemDtoMap.get(orderItem.getProductSkuId()).getActivityId())) {
                orderItem.setActivityId(itemDtoMap.get(orderItem.getProductSkuId()).getActivityId());
            }
            if (ObjectUtil.isNotNull(itemDtoMap.get(orderItem.getProductSkuId()).getActivityProductId())) {
                orderItem.setActivityProductId(itemDtoMap.get(orderItem.getProductSkuId()).getActivityProductId());
            }
            //计算佣金
            setBrokerageAmount(itemVo, orderItem);
            itemList.add(orderItem);
        }
        itemList.sort(Comparator.comparing(OrderItem::getRealAmount).reversed());
        log.info("return orderItem {}", JSONUtil.toJsonStr(itemList));
        return itemList;
    }

    /**
     * 设置商品佣金
     *
     * @param itemVo 商品信息
     * @param orderItem 订单商品信息
     */
    private void setBrokerageAmount(ItemVo itemVo, OrderItem orderItem) {
//        if (ObjectUtil.isNotNull(itemVo.getCommanderCommission())) {
//            if (itemVo.getCommissionType() == 0) {
//                orderItem.setBrokerageAmount(itemVo.getCommanderCommission());
//            } else {
//                BigDecimal percentage = NumberUtil.div(itemVo.getCommanderCommission(), 100);
//                BigDecimal brokerageAmount = NumberUtil.mul(orderItem.getProductPrice(),
//                        percentage);
//                orderItem.setBrokerageAmount(brokerageAmount);
//            }
//            //将单个商品佣金乘于商品数量
//            orderItem.setBrokerageAmount(NumberUtil.mul(orderItem.getBrokerageAmount(),
//                    itemVo.getProductQuantity()));
//
//        } else {
        orderItem.setBrokerageAmount(BigDecimal.ZERO);
//        }
    }

    /**
     * 设置商品返利金额
     *
     * @param memberInfo 会员信息
     * @param rebateSet 返利设置
     * @param itemVo 商品信息
     * @param orderItem 订单商品信息
     */
    private void setRebateAmount(MemberCardInfoCenterDto memberInfo, RebateSet rebateSet, ItemVo itemVo,
                                 OrderItem orderItem) {
        if (ObjectUtil.isNull(rebateSet)) {
            log.info("返利未设置，已经跳过");
        } else {
            if (StrUtil.isBlank(rebateSet.getRebateCondition())) {
                log.info("没有设置允许使用的用户");
                orderItem.setRebateAmount(BigDecimal.ZERO);
                return;
            }
            //使用条件仅为会员用户，并且当前用户为普通用户
            if (ObjectUtil.isNull(memberInfo) && CommonConstants.STRING_ONE.equals(rebateSet.getRebateCondition())) {
                log.info("使用条件仅为会员用户，并且当前用户为普通用户");
                orderItem.setRebateAmount(BigDecimal.ZERO);
                return;
            }
            //使用条件仅为普通用户，并且当前用户为会员
            if (ObjectUtil.isNotNull(memberInfo) && CommonConstants.STRING_ZERO.equals(rebateSet.getRebateCondition())) {
                log.info("使用条件仅为普通用户，并且当前用户为会员");
                orderItem.setRebateAmount(BigDecimal.ZERO);
                return;
            } else {
                Optional<ApiSkuRebateVo> skuRebateVoOptional =
                        itemVo.getSkuRebates().stream().filter(skuRebate -> skuRebate.getSkuId().equals(itemVo.getProductSkuId())).findFirst();
                if (skuRebateVoOptional.isPresent()) {
                    //返利价格类型 0-不使用 1-价格 2-比例
                    if (CommonConstants.NUMBER_ZERO.equals(itemVo.getRebateType())) {
                        orderItem.setRebateAmount(BigDecimal.ZERO);
                    } else if (CommonConstants.NUMBER_ONE.equals(itemVo.getRebateType())) {
                        orderItem.setRebateAmount(skuRebateVoOptional.get().getRebatePrice());
                    } else if (CommonConstants.NUMBER_TWO.equals(itemVo.getRebateType())) {
                        orderItem.setRebateAmount(NumberUtil.mul(itemVo.getProductPrice(),
                                NumberUtil.div(skuRebateVoOptional.get().getRebatePrice(), 100)));
                    }
                } else {
                    if (CommonConstants.NUMBER_THREE.equals(itemVo.getRebateType())) {
                        BigDecimal total = NumberUtil.mul(itemVo.getProductPrice(),
                                itemVo.getProductQuantity());
                        orderItem.setRebateAmount(NumberUtil.mul(total, NumberUtil.div(rebateSet.getRebateRatio(),
                                100)));
                    } else {
                        orderItem.setRebateAmount(BigDecimal.ZERO);
                    }
                }
            }
            //将单个商品返利乘于商品数量
            orderItem.setRebateAmount(NumberUtil.mul(orderItem.getRebateAmount(),
                    itemVo.getProductQuantity()));
        }
    }

    /**
     * 设置商品分销金额
     *
     * @param memberInfo 留扩展用
     * @param itemVo 商品信息
     * @param orderItem 订单商品信息
     */
    private void setDistributionAmount(MemberCardInfoCenterDto memberInfo, ItemVo itemVo, OrderItem orderItem) {
        //返利价格类型 0-不使用 1-价格 2-比例
        if (itemVo.getUseDistribution().equals(0)) {
            orderItem.setFirstDistributionAmount(BigDecimal.ZERO);
        } else if (itemVo.getUseDistribution().equals(1)) {
            orderItem.setFirstDistributionAmount(itemVo.getDistributionAmount());
        } else if (itemVo.getUseDistribution().equals(2)) {
            orderItem.setFirstDistributionAmount(NumberUtil.mul(itemVo.getProductPrice(),
                    NumberUtil.div(itemVo.getDistributionAmount(), 100)));
        }
        //将单个商品分销乘于商品数量
        orderItem.setFirstDistributionAmount(NumberUtil.mul(orderItem.getFirstDistributionAmount(),
                itemVo.getProductQuantity()));
    }

    /**
     * 设置商品会员优惠金额
     *
     * @param memberInfo 会员信息
     * @param itemVo 商品信息
     * @param orderItem 订单商品信息
     */
    private void setMemberAmount(MemberCardInfoCenterDto memberInfo, ItemVo itemVo, OrderItem orderItem) {
        BigDecimal memberPrice;
        if (ObjectUtil.isNotNull(memberInfo)) {
            //此商品已经单独定义了会员价
            if (CollUtil.isNotEmpty(itemVo.getMemberPrices())) {
                for (MemberPriceVo memberPriceVo : itemVo.getMemberPrices()) {
                    if (memberPriceVo.getMemberLevelId().toString().equals(memberInfo.getRankCode())) {
                        //会员价格类型 0-不使用 1-价格 2-比例
                        if (CommonConstants.NUMBER_ZERO.equals(itemVo.getMemberType())) {
                            orderItem.setMemberAmount(BigDecimal.ZERO);
                        } else if (CommonConstants.NUMBER_ONE.equals(itemVo.getMemberType())) {
                            memberPrice = memberPriceVo.getMemberPrice();
                            orderItem.setMemberAmount(NumberUtil.sub(itemVo.getProductPrice(), memberPrice));
                        } else if (CommonConstants.NUMBER_TWO.equals(itemVo.getMemberType())) {
                            memberPrice = NumberUtil.mul(itemVo.getProductPrice(),
                                    NumberUtil.div(memberPriceVo.getMemberPrice(), 100));
                            orderItem.setMemberAmount(NumberUtil.sub(itemVo.getProductPrice(), memberPrice));
                        } else {
                            orderItem.setMemberAmount(BigDecimal.ZERO);
                        }
                        break;
                    }

                }
            } else {
                //尝试使用会员权益中的商品折扣
                if (ObjectUtil.isNotNull(memberInfo) && CollUtil.isNotEmpty(memberInfo.getMemberCardPrivileges())) {
                    //会员价格类型 0-不使用 1-价格 2-比例
                    if (CommonConstants.NUMBER_ZERO.equals(itemVo.getMemberType())) {
                        orderItem.setMemberAmount(BigDecimal.ZERO);
                    } else {
                        Optional<MemberCardPrivilege> optional =
                                memberInfo.getMemberCardPrivileges()
                                        .stream()
                                        .filter(memberCardPrivilege -> "1".equalsIgnoreCase(memberCardPrivilege.getPType()))
                                        .findFirst();
                        if (optional.isPresent()) {
                            MemberCardPrivilege discountPrivilege = optional.get();
                            memberPrice = NumberUtil.mul(itemVo.getProductPrice(),
                                    NumberUtil.div(discountPrivilege.getDefValue(), 100));
                            orderItem.setMemberAmount(NumberUtil.sub(itemVo.getProductPrice(), memberPrice));
                        }

                    }
                }
            }
            //将单个商品会员价乘于商品数量
            orderItem.setMemberAmount(NumberUtil.mul(orderItem.getMemberAmount(),
                    itemVo.getProductQuantity()));
        }
    }

    /**
     * 计算优惠券优惠
     *
     * @param orderItemList 订单商品信息
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @return java.math.BigDecimal
     */
    private BigDecimal handleCouponAmount(List<OrderItem> orderItemList, Long couponId, String userId) {
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (ObjectUtil.isNotNull(couponId) && couponId > 0L) {
            MemberCouponVo memberCouponVo = remoteDiscountService.findMemberCouponForOrder(userId, couponId);
            if (ObjectUtil.isNull(memberCouponVo)) {
                throw new ServiceException("优惠券不满足使用条件:优惠券不可用");
            }
            //适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与
            Set<Long> couponProductIds;
            List<OrderItem> canUseCouponOrderItem = null;
            switch (memberCouponVo.getProductType()) {
                case 0:
                    canUseCouponOrderItem = orderItemList;
                    break;
                case 1:
                    couponProductIds =
                            memberCouponVo.getCouponProducts().stream().map(CouponProductVo::getProductId).collect(Collectors.toSet());
                    canUseCouponOrderItem =
                            orderItemList.stream().filter(orderItem -> couponProductIds.contains(orderItem.getProductId())).toList();
                    break;
                case 2:
                    couponProductIds =
                            memberCouponVo.getCouponProducts().stream().map(CouponProductVo::getProductId).collect(Collectors.toSet());
                    canUseCouponOrderItem =
                            orderItemList.stream().filter(orderItem -> !couponProductIds.contains(orderItem.getProductId())).toList();
                    break;
                default:
                    break;
            }
            if (CollUtil.isEmpty(canUseCouponOrderItem)) {
                throw new ServiceException("优惠券不满足使用条件:无符合条件的商品");
            }
            OrderItem useCouponOrderItem = null;
            //优惠劵类型：  0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵
            BigDecimal maxGoodsPrice;
            switch (memberCouponVo.getCouponType()) {
                case 0:
                    // 取可使用优惠券得商品集合中 单价最高得
                    useCouponOrderItem = useCouponGoods(canUseCouponOrderItem);
                    couponAmount = NumberUtil.mul(useCouponOrderItem.getProductPrice(),
                            NumberUtil.div(memberCouponVo.getDiscount(), BigDecimal.TEN));
                    couponAmount = NumberUtil.mul(NumberUtil.sub(useCouponOrderItem.getProductPrice(), couponAmount),
                            useCouponOrderItem.getProductQuantity());
                    useCouponOrderItem.setCouponAmount(couponAmount);
                    break;
                case 1:
                    // 取可使用优惠券得商品集合中 单价最高得
                    useCouponOrderItem = useCouponGoods(canUseCouponOrderItem);
                    couponAmount = memberCouponVo.getDiscountAmount();
                    useCouponOrderItem.setCouponAmount(couponAmount);
                    break;
                case 2:
                    canUseCouponOrderItem =
                            canUseCouponOrderItem.stream().filter(orderItem -> orderItem.getProductPrice()
                                            .compareTo(memberCouponVo.getConditionAmount()) != -1)
                                    .toList();
                    // 取可使用优惠券得商品集合中 单价最高得
                    useCouponOrderItem = useCouponGoods(canUseCouponOrderItem);
                    couponAmount = NumberUtil.mul(useCouponOrderItem.getProductPrice(),
                            NumberUtil.div(memberCouponVo.getDiscount(), BigDecimal.TEN));
                    couponAmount = NumberUtil.mul(NumberUtil.sub(useCouponOrderItem.getProductPrice(), couponAmount),
                            useCouponOrderItem.getProductQuantity());
                    useCouponOrderItem.setCouponAmount(couponAmount);
                    break;
                case 3:
                    canUseCouponOrderItem =
                            canUseCouponOrderItem.stream().filter(orderItem -> orderItem.getProductPrice()
                                            .compareTo(memberCouponVo.getConditionAmount()) != -1)
                                    .toList();
                    // 取可使用优惠券得商品集合中 单价最高得
                    useCouponOrderItem = useCouponGoods(canUseCouponOrderItem);
                    couponAmount = memberCouponVo.getDiscountAmount();
                    useCouponOrderItem.setCouponAmount(couponAmount);
                    break;
                default:
                    break;
            }
            //如果优惠券的优惠金额超过了符合优惠券的商品的总金额，那么最高优惠金额为符合优惠券的商品的总金额
            BigDecimal canUseCouponAmount;
            //最高商品的商品总价
            canUseCouponAmount = NumberUtil.mul(useCouponOrderItem.getProductPrice(), useCouponOrderItem.getProductQuantity());

            if (canUseCouponAmount.compareTo(couponAmount) < 0) {
                couponAmount = canUseCouponAmount;
            }
            useCouponOrderItem.setCouponAmount(couponAmount);
            log.info("当前优惠的商品：{},优惠类型：{},优惠折扣：{},优惠金额{},优惠前的价格：{},优惠：{}", useCouponOrderItem.getProductId(),
                    memberCouponVo.getCouponType(), memberCouponVo.getDiscount(), memberCouponVo.getDiscountAmount(),
                    useCouponOrderItem.getProductPrice(), couponAmount);
        }
        return couponAmount;

    }

    /**
     * 取最高商品单价得商品信息
     *
     * @param canUseCouponOrderItem 可以使用优惠券得商品
     */
    private OrderItem useCouponGoods(List<OrderItem> canUseCouponOrderItem) {
        OrderItem orderItem = new OrderItem();
        BigDecimal maxGoodsPrice = NumberUtil.mul(canUseCouponOrderItem.get(CommonConstants.NUMBER_ZERO).getProductPrice(), canUseCouponOrderItem.get(0).getProductQuantity());
        for (OrderItem item : canUseCouponOrderItem) {
            BigDecimal currentGoodsPrice = NumberUtil.mul(item.getProductPrice(), item.getProductQuantity());
            if (maxGoodsPrice.compareTo(currentGoodsPrice) < 1) {
                orderItem = item;
            }
        }
        return orderItem;
    }

    /**
     * 计算满减优惠
     *
     * @param orderItemList 订单商品信息
     * @param fullScaleId 满减ID
     * @return java.math.BigDecimal
     */
    private BigDecimal handleFullScaleAmount(List<OrderItem> orderItemList, Long fullScaleId) {
        BigDecimal fullScaleAmount = BigDecimal.ZERO;
        if (ObjectUtil.isNotNull(fullScaleId) && fullScaleId > 0L) {
            FullScaleVo fullScaleVo = remoteDiscountService.findFullScaleForOrder(fullScaleId);
            if (ObjectUtil.isNull(fullScaleVo)) {
                throw new ServiceException("活动不满足使用条件:活动不可用");
            }
            //适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与
            Set<Long> couponProductIds;
            // 筛选出可以使用满减的订单详情
            List<OrderItem> canUseFullScaleOrderItem = null;
            switch (fullScaleVo.getProductType()) {
                case 0:
                    canUseFullScaleOrderItem = orderItemList;
                    break;
                case 1:
                    couponProductIds =
                            fullScaleVo.getFullScaleProducts().stream().map(FullScaleProductVo::getProductId).collect(Collectors.toSet());
                    canUseFullScaleOrderItem =
                            orderItemList.stream().filter(orderItem -> couponProductIds.contains(orderItem.getProductId())).toList();
                    break;
                case 2:
                    couponProductIds =
                            fullScaleVo.getFullScaleProducts().stream().map(FullScaleProductVo::getProductId).collect(Collectors.toSet());
                    canUseFullScaleOrderItem =
                            orderItemList.stream().filter(orderItem -> !couponProductIds.contains(orderItem.getProductId())).toList();
                    break;
                default:
                    break;
            }
            if (ObjectUtil.isNull(canUseFullScaleOrderItem)) {
                return fullScaleAmount;
            }
            BigDecimal canUseFullScaleAmount =
                    canUseFullScaleOrderItem.stream().map(orderItem -> orderItem.getProductPrice().multiply(BigDecimal.valueOf(orderItem.getProductQuantity()))).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
            List<FullScaleRuleVo> fullScaleRulesList = fullScaleVo.getFullScaleRules();
            fullScaleRulesList.sort(Comparator.comparing(FullScaleRuleVo::getConditionAmount));
            FullScaleRuleVo canUseFullScaleRule = null;
            for (FullScaleRuleVo ruleVo : fullScaleRulesList) {
                if (NumberUtil.isLessOrEqual(ruleVo.getConditionAmount(), canUseFullScaleAmount)) {
                    BigDecimal currFullScaleAmount = BigDecimal.ZERO;
                    //满减条件：  0-满减劵  1-满折劵
                    switch (ruleVo.getRuleType()) {
                        case 0:
                            currFullScaleAmount = ruleVo.getDiscountAmount();
                            break;
                        case 1:
                            currFullScaleAmount = NumberUtil.mul(canUseFullScaleAmount,
                                    NumberUtil.div(ruleVo.getDiscount(), BigDecimal.TEN));
                            currFullScaleAmount = NumberUtil.sub(canUseFullScaleAmount, currFullScaleAmount);
                            break;
                        default:
                            break;
                    }
                    log.info("当前最大的优惠金额:{},本次循环的优惠金额:{},对比结果:{}", fullScaleAmount, currFullScaleAmount,
                            NumberUtil.isLess(fullScaleAmount, currFullScaleAmount));
                    if (NumberUtil.isLess(fullScaleAmount, currFullScaleAmount) || ObjectUtil.isNull(canUseFullScaleRule)) {
                        fullScaleAmount = currFullScaleAmount;
                        canUseFullScaleRule = ruleVo;
                    }
                }
            }
            if (ObjectUtil.isNotNull(canUseFullScaleRule)) {
                log.info("当前优惠等级：{},优惠前的价格：{},优惠：{}", canUseFullScaleRule, canUseFullScaleAmount,
                        fullScaleAmount);
            }
        }
        return fullScaleAmount;
    }


    /**
     * 计算积分优惠
     *
     * @param totalAmount 订单总金额
     * @param integral 积分
     * @param allIntegral 用户所有积分
     * @return java.math.BigDecimal
     */
    private BigDecimal handleIntegralAmount(BigDecimal totalAmount, BigDecimal integral, BigDecimal allIntegral) {
        BigDecimal integralAmount = BigDecimal.ZERO;
        if (ObjectUtil.isNotNull(integral) && integral.compareTo(BigDecimal.ZERO) > 0) {
            OrderSetting orderSetting = orderSettingMapper.selectOne(null);
            BigDecimal discountAmount = NumberUtil.mul(totalAmount, NumberUtil.div(orderSetting.getCeiling(), 100));
            BigDecimal needIntegral = NumberUtil.div(discountAmount, NumberUtil.div(orderSetting.getRatio(), 100), 2);
            if (allIntegral.compareTo(needIntegral) < 0) {
                throw new ServiceException("积分不满足使用条件:积分不足");
            }
            integralAmount = discountAmount;
        }
        return integralAmount;
    }


    /**
     * 根据订单类型获取订单过期时间
     *
     * @param type 订单类型
     * @return long
     */
    private long getExTime(OrderTypeEnum type) {
        OrderSetting orderSetting = orderSettingMapper.selectOne(null);
        if (type.equals(OrderTypeEnum.SEC_KILL)) {
            return TimeConstants.ONE_MINUTES * orderSetting.getFlashOrderOvertime();
        } else {
            return TimeConstants.ONE_MINUTES * orderSetting.getNormalOrderOvertime();
        }
    }


    /**
     * 取消订单
     *
     * @param orderId 订单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        Order order = baseMapper.selectById(orderId);
        if (ObjectUtil.isNull(order)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        if (!order.getUserId().equals(ISecurity.userMust().getId())) {
            throw new ServiceException("您无此操作的权限");
        }
        if (!OrderStatusEnum.canCancel(order.getStatus())) {
            throw new ServiceException(OrderCode.ABNORMAL_DATA_STATUS);
        }
        cancelOrder(orderId, order, OrderStatusEnum.BUYER_CANCEL_CLOSE);
    }


    /**
     * 过期自动取消订单
     *
     * @param orderId 订单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoCancelOrder(Long orderId) {
        Order order = baseMapper.selectById(orderId);
        if (ObjectUtil.isNull(order)) {
            return;
        }
        if (!OrderStatusEnum.canCancel(order.getStatus())) {
            return;
        }
        cancelOrder(orderId, order, OrderStatusEnum.BUYER_PAY_TIMEOUT_CLOSE);
    }

    /**
     * Cancel order.
     *
     * @param orderId the order id
     * @param order   the order
     * @param status  the status
     */
    public void cancelOrder(Long orderId, Order order, OrderStatusEnum status) {
        if (ObjectUtil.isNotNull(order.getCouponId()) && order.getCouponId() > 0L) {
            log.info("归还优惠券参数:{}", order.getCouponId());
            Boolean couponSuccess = remoteDiscountService.revertMemberCoupon(order.getCouponId());
            if (!couponSuccess) {
                throw new ServiceException("优惠券归还失败，请稍后重试");
            }
        }
        if (ObjectUtil.isNotNull(order.getIntegration()) && order.getIntegration().compareTo(BigDecimal.ZERO) == 1) {
            log.info("归还积分参数:{},{}", order.getUserId(), order.getIntegration());

            Boolean integralSuccess = remoteIntegralService.integralReturn(order.getUserId(), order.getIntegration());
            if (!integralSuccess) {
                OrderFailMessage failMessage = new OrderFailMessage().integralFail(order.getCouponId(),
                        order.getUserId(), order.getShopId());
                sender.sendCancelFailOrderMessage(failMessage);
                throw new ServiceException("积分归还失败，请稍后重试");
            }
        }
        order.setStatus(status);
        order.setCloseTime(LocalDateTime.now());
        List<OperateStockDto> operateStockDtoList =
                orderItemMapper.selectItemDtoByOrderIds(Collections.singletonList(orderId));
        log.info("归还库存参数:{}", operateStockDtoList.toString());
        Boolean success = remoteGoodsService.batchRevertStock(operateStockDtoList);
        if (!success) {
            OrderFailMessage failMessage = new OrderFailMessage().stockFail(order.getCouponId(),
                    order.getIntegration(), order.getUserId(), order.getShopId());
            sender.sendCancelFailOrderMessage(failMessage);
            throw new ServiceException("库存归还失败，请稍后重试");
        }
        baseMapper.updateById(order);
    }

    /**
     * 用户支付订单
     *
     * @param orderId     the order id
     * @param userBalance the user balance
     * @param request     the request
     * @return void pay result dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDto payOrder(Long orderId, Boolean userBalance, HttpServletRequest request) {
        SecureUser secureUser = ISecurity.userMust();
        //查询会员持有的积分、收货地址
        AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(secureUser.getId(), Arrays.asList(2,
                3));
        log.info("当前用户信息:" + secureUser);
        //检查账号
        checkAccount(accountInfoDto);
        OrderVo orderVo = baseMapper.selectOrderVoById(orderId);
        if (ObjectUtil.isNull(orderVo)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        if (!OrderStatusEnum.canPay(orderVo.getStatus())) {
            throw new ServiceException("当前状态不能支付此订单");
        }
        if (NumberUtil.isLess(orderVo.getPayAmount(), OrderConstant.MIN_PAY_FEE)) {
            throw new ServiceException("支付金额有误");
        }
        log.info("进入支付订单===================>");
        if (userBalance) {
            return userBalance(orderId, secureUser, orderVo);
        }
        if (orderVo.getPayType().equals(PayTypeEnum.WECHAT) || orderVo.getPayType().equals(PayTypeEnum.WECHAT_H5)) {
            return userWechat(request, secureUser, orderVo);
        }
        return null;

    }

    /**
     * 使用微信支付
     *
     * @param request 请求信息
     * @param secureUser 用户信息
     * @param orderVo 订单信息
     * @return com.medusa.gruul.payment.api.model.dto.PayResultDto
     */
    private PayResultDto userWechat(HttpServletRequest request, SecureUser secureUser, OrderVo orderVo) {
        Integer payType = CommonConstants.NUMBER_ONE;
        PayRequestDto dto = new PayRequestDto();
        dto.setPayChannel(payType);
        if (orderVo.getPayType().equals(PayTypeEnum.WECHAT)) {
            dto.setTradeType(CommonConstants.NUMBER_ONE);
        }
        if (Platform.H5==ISystem.platform()){
            dto.setTradeType(CommonConstants.NUMBER_SIX);
        }

        dto.setOutTradeNo(orderVo.getId().toString());
        dto.setRouteKey(OrderQueueEnum.QUEUE_ORDER_PAYMENT_NOTIFY.getRouteKey());
        dto.setOpenId(secureUser.getOpenid());
        dto.setTotalFee(orderVo.getPayAmount());
        dto.setShopId(secureUser.getShopId());
        dto.setTerminalIp(IPUtils.getIpAddr(request));
        dto.setFeeType("CNY");
        if (orderVo.getType() == OrderTypeEnum.SEC_KILL) {
            dto.setTimeoutExpress("30m");
        } else {
            dto.setTimeoutExpress("6h");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attach",orderVo.getId());
        dto.setBody("小程序购物");
        dto.setSubject("");
        dto.setBusinessParams(String.valueOf(jsonObject));
        PayResultDto resultDto = remotePaymentService.payRequest(dto);
        if (ObjectUtil.isNull(resultDto)
                || ObjectUtil.isNull(resultDto.getWxResult())
                || ObjectUtil.isNull(resultDto.getWxResult().getTransactionId())) {
            log.error("支付创建失败,入参为: {}", dto);
            log.error("支付创建失败,出参为: {}", resultDto.toString());

        }
        orderVo.setTransactionId(resultDto.getWxResult().getTransactionId());
        this.updateById(orderVo);
        return resultDto;
    }

    /**
     * 使用余额支付
     *
     * @param orderId 订单ID
     * @param secureUser 用户信息
     * @param orderVo 订单信息
     * @return com.medusa.gruul.payment.api.model.dto.PayResultDto
     */
    private PayResultDto userBalance(Long orderId, SecureUser secureUser, OrderVo orderVo) {
        //查询用户的会员情况
        MemberCardInfoCenterDto memberInfo =
                remoteMiniAccountService.getMemberInfo(secureUser.getId());

        //查询余额返利设置
        RebateSet rebateSet = remoteGoodsService.getRebateSet(ISecurity.userMust().getShopId());
        if (ObjectUtil.isNotNull(rebateSet)) {
            if (StrUtil.isBlank(rebateSet.getBalanceUseCondition())) {
                throw new ServiceException("余额支付已被禁用");
            }
            if (!rebateSet.getBalanceUseCondition().contains("0") && ObjectUtil.isNull(memberInfo)) {
                throw new ServiceException("普通用户的余额支付已被禁用");
            }
        }
        MemberBalanceUpdateDto memberBalanceUpdateDto = new MemberBalanceUpdateDto();
        if (ObjectUtil.isNotNull(memberInfo) && memberInfo.getId() != null ) {
            memberBalanceUpdateDto.setMemberInfoId(memberInfo.getId());
        } else {
            memberBalanceUpdateDto.setShopUserId(secureUser.getId());
        }
        memberBalanceUpdateDto.setUseBalance(orderVo.getPayAmount());
        //变化类型 0-添加 1-减少
        memberBalanceUpdateDto.setChangeType(1);
        memberBalanceUpdateDto.setDetailName(StrUtil.format("购买{}件商品",
                orderVo.getOrderItemList().stream().mapToInt(OrderItem::getProductQuantity).sum()));
        //交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功
        memberBalanceUpdateDto.setDealType(0);
        //订单来源：1-会员，2-商品，3-积分
        memberBalanceUpdateDto.setSource(2);
        memberBalanceUpdateDto.setOutOrderId(orderId);
        Boolean succ = remoteMiniAccountService.memberBalanceModify(memberBalanceUpdateDto);
        if (succ) {
            Order order = baseMapper.selectById(orderId);
            order.setStatus(OrderStatusEnum.WAIT_FOR_SEND);
            order.setPayType(PayTypeEnum.BALANCE);
            order.setPayTime(LocalDateTime.now());
            baseMapper.updateById(order);
            OrderVo vo = baseMapper.selectOrderVoById(order.getId());
            sender.sendPayedOrderMessage(vo);
            return null;
        } else {
            log.info("余额支付失败,入参为: {}", memberBalanceUpdateDto);
            log.info("余额支付失败,出参为: {}", succ);
            throw new ServiceException("余额使用失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paymentNotify(Long orderId) {

        ISecurity.userMust().setShopId(null);
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getId, orderId).last("limit " +
                "1"));
        ISecurity.userMust().setShopId(order.getShopId());
        order.setStatus(OrderStatusEnum.WAIT_FOR_SEND);
        order.setPayTime(LocalDateTime.now());
        baseMapper.updateById(order);
        OrderVo vo = baseMapper.selectOrderVoById(order.getId());
        sender.sendPayedOrderMessage(vo);
    }

    /**
     * 用户确认收货
     *
     * @param orderId  订单ID
     * @param isSystem 是否系统操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiptOrder(Long orderId, boolean isSystem) {
        Order order = baseMapper.selectById(orderId);
        OrderSetting orderSetting = orderSettingMapper.selectOne(null);
        if (ObjectUtil.isNull(order)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        if (!isSystem) {
            if (!order.getUserId().equals(ISecurity.userMust().getId())) {
                throw new ServiceException("您无此操作的权限");
            }
        }
        if (!OrderStatusEnum.canReceipt(order.getStatus())) {
            throw new ServiceException("当前状态不能确认此订单");
        }
        List<AfsOrder> afsOrderList = afsOrderMapper.selectProgressByOrderId(orderId);
        for (AfsOrder afsOrder : afsOrderList) {
            if (!afsOrder.getStatus().equals(AfsOrderStatusEnum.WAIT_FOR_BUSINESS_APPROVED) && !isSystem) {
                throw new ServiceException("当前订单有正在进行中的售后订单");
            }
        }
        order.setStatus(OrderStatusEnum.WAIT_FOR_COMMENT);
        baseMapper.updateById(order);
        OrderDelivery orderDelivery = orderDeliveryMapper.selectById(orderId);
        orderDelivery.setReceived(true);
        orderDelivery.setReceiveTime(LocalDateTime.now());
        orderDeliveryMapper.updateById(orderDelivery);
        //到期未评价自动完成订单
        BaseOrderMessage message =
                new BaseOrderMessage().setOrderId(orderId).setShopId(order.getShopId());
        sender.sendAutoCompletedOrderMessage(message, orderSetting.getFinishOvertime() * TimeConstants.ONE_DAY);
        OrderVo vo = orderInfo(order.getId());
        sender.sendReceiptOrderMessage(vo);
    }

    /**
     * 检查活动是否结束
     *
     * @param dto 订单查询参数
     * @return java.lang.Integer integer
     */
    @Override
    public Integer checkOrder(GroupOrderResultDto dto) {
        //先查询已经失败的订单里面有没有
        String failMsg = RedisUtil.getCacheObject(RedisUtil.key(Keys.ORDER_FAILED, dto.getOrderId()));
        if (StrUtil.isNotBlank(failMsg)) {
            throw new ServiceException(failMsg);
        }
        //再查询库存是否已经售罄
        Collection<SkuStock> skuStockList = remoteGoodsService.findSkuStockListByIds(dto.getSkuIdSet());
        Map<Long, SkuStock> skuStockMap = null;
        if (CollUtil.isNotEmpty(skuStockList)) {
            skuStockMap = skuStockList.stream().collect(Collectors.toMap(SkuStock::getId, v -> v));
        }
        for (ItemDto itemDto : dto.getItems()) {
            assert skuStockMap != null;
            SkuStock skuStock = skuStockMap.get(itemDto.getSkuId());
            if (ObjectUtil.isNotNull(skuStock)) {
                boolean hasStock = skuStock.getStock() > itemDto.getNumber();
                if (!hasStock) {
                    return -1;
                }
            } else {
                return -1;

            }
        }
        return 0;
    }

    /**
     * 用户订单列表
     *
     * @param dto 小程序端订单查询条件
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     */
    @Override
    public PageUtils<ApiOrderVo> searchOrder(ApiSearchOrderDto dto) {
        List<Integer> orderStatusList = new ArrayList<>(4);
        boolean searchAfterOrder = false;
        //订单状态 -1：所有订单, 0.待付款（待买家付款）, 1.待发货（买家已付款）, 2.配送中（卖家已发货）, 3.待提货（物流订单已发货）, 4.已完成（用户已经签收）, 5.待评价, 6
        // .售后订单, 7.已关闭
        switch (dto.getOrderStatus()) {
            case -1:
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
                orderStatusList.add(OrderStatusEnum.COMPLETE.getCode());
                break;
            case 5:
                orderStatusList.add(OrderStatusEnum.WAIT_FOR_COMMENT.getCode());
                break;
            case 6:
                searchAfterOrder = true;
                break;
            case 7:
                orderStatusList.add(OrderStatusEnum.REFUNDED.getCode());
                orderStatusList.add(OrderStatusEnum.PART_REFUNDED.getCode());
                orderStatusList.add(OrderStatusEnum.BUYER_PAY_TIMEOUT_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.BUYER_CANCEL_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.SELLER_CANCEL_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.EXCHANGE_SUCCESS_CLOSE.getCode());
                orderStatusList.add(OrderStatusEnum.EXCHANGE_CANCEL_CLOSE.getCode());
                break;
            default:
                break;
        }

        Page<ApiOrderVo> page = baseMapper.searchApiOrderVoPage(new Page(dto.getCurrent(), dto.getSize()),
                orderStatusList, searchAfterOrder, ISecurity.userMust().getId());
        for (ApiOrderVo record : page.getRecords()) {
            if (ObjectUtil.isNull(record.getExpireTime())) {
                record.setExpireTime(record.getCreateTime().plusSeconds(getExTime(record.getType()) / 1000));
            }
            if (record.getType().equals(OrderTypeEnum.EXCHANGE)) {
                Long originalOrderId = afsOrderMapper.selectOriginalOrderIdByOrderId(record.getOrderId());
                record.setOriginalOrderId(originalOrderId);
            }
        }
        return new PageUtils<>(page);
    }

    /**
     * 用户评价订单
     *
     * @param dto 订单评价信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateOrder(ApiOrderEvaluateDto dto) {
        OrderVo orderVo = baseMapper.selectOrderVoById(dto.getOrderId());
        if (ObjectUtil.isNull(orderVo)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        if (!orderVo.getUserId().equals(ISecurity.userMust().getId())) {
            throw new ServiceException("您无此操作的权限");
        }
        if (!OrderStatusEnum.canEvaluate(orderVo.getStatus())) {
            throw new ServiceException("当前状态不能评价此订单");
        }
        AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(ISecurity.userMust().getId()
                , Arrays.asList(2, 3));
        if (CollUtil.isNotEmpty(accountInfoDto.getRestrictTypes()) && accountInfoDto.getRestrictTypes().contains(BlacklistEnum.REJECT_COMMENT.getType())) {
            throw new ServiceException("账号异常请联系客服");
        }
        String defaultComment = "此用户没有填写评价";
        OrderEvaluate orderEvaluate = new OrderEvaluate();
        orderEvaluate.setOrderId(orderVo.getId());
        orderEvaluate.setUserId(orderVo.getUserId());
        orderEvaluate.setUserName(orderVo.getUserName());
        orderEvaluate.setUserAvatarUrl(orderVo.getUserAvatarUrl());
        orderEvaluate.setShopRate(dto.getShopRate());
        orderEvaluateMapper.insert(orderEvaluate);
        Map<Long, ApiOrderProductEvaluateDto> productEvaluateMap =
                dto.getProductEvaluateDtoList().stream().collect(Collectors.toMap(ApiOrderProductEvaluateDto::getProductSkuId, v -> v));
        for (OrderItemVo orderItem : orderVo.getOrderItemList()) {
            if (ObjectUtil.isNotEmpty(orderItem.getAfs())) {
                if (orderItem.getAfs().getStatus() == AfsOrderStatusEnum.SUCCESS) {
                    //售后成功订单商品不进行评价
                    break;
                }
            }
            ApiOrderProductEvaluateDto productEvaluateDto = productEvaluateMap.get(orderItem.getProductSkuId());
            OrderProductEvaluate productEvaluate = new OrderProductEvaluate();
            productEvaluate.setOrderId(orderVo.getId());
            productEvaluate.setProductId(orderItem.getProductId());
            productEvaluate.setProductSkuId(orderItem.getProductSkuId());
            productEvaluate.setProductPic(orderItem.getProductPic());
            productEvaluate.setProductQuantity(orderItem.getProductQuantity());
            productEvaluate.setProductPrice(orderItem.getProductPrice());
            productEvaluate.setProductName(orderItem.getProductName());
            productEvaluate.setSpecs(orderItem.getSpecs());
            if (StrUtil.isNotBlank(productEvaluateDto.getComment())) {
                productEvaluate.setComment(productEvaluateDto.getComment());
            } else {
                productEvaluate.setComment(defaultComment);
            }
            productEvaluate.setPicture(productEvaluateDto.getPicture());
            productEvaluate.setRate(productEvaluateDto.getRate());
            productEvaluateMapper.insert(productEvaluate);
        }
        completeOrder(orderVo.getId());
    }

    /**
     * 系统自动评价订单
     *
     * @param orderId 订单ID
     */
    @Override
    public void evaluateOrder(Long orderId) {
        OrderVo orderVo = baseMapper.selectOrderVoById(orderId);
        OrderEvaluate orderEvaluate =
                orderEvaluateMapper.selectOne(new LambdaQueryWrapper<OrderEvaluate>().eq(OrderEvaluate::getOrderId,
                        orderVo.getId()));
        if (ObjectUtil.isNull(orderEvaluate)) {
            String defaultComment = "系统自动好评";
            orderEvaluate = new OrderEvaluate();
            orderEvaluate.setOrderId(orderVo.getId());
            orderEvaluate.setUserId(orderVo.getUserId());
            orderEvaluate.setUserName(orderVo.getUserName());
            orderEvaluate.setUserAvatarUrl(orderVo.getUserAvatarUrl());
            orderEvaluate.setShopRate(5);
            orderEvaluateMapper.insert(orderEvaluate);
            for (OrderItem orderItem : orderVo.getOrderItemList()) {
                OrderProductEvaluate productEvaluate = new OrderProductEvaluate();
                productEvaluate.setOrderId(orderVo.getId());
                productEvaluate.setProductId(orderItem.getProductId());
                productEvaluate.setProductSkuId(orderItem.getProductSkuId());
                productEvaluate.setProductPic(orderItem.getProductPic());
                productEvaluate.setProductQuantity(orderItem.getProductQuantity());
                productEvaluate.setProductPrice(orderItem.getProductPrice());
                productEvaluate.setProductName(orderItem.getProductName());
                productEvaluate.setSpecs(orderItem.getSpecs());
                productEvaluate.setComment(defaultComment);
                productEvaluate.setRate(CommonConstants.NUMBER_FIVE);
                productEvaluateMapper.insert(productEvaluate);
            }
            completeOrder(orderVo.getId());
        }

    }

    /**
     * 订单详情
     *
     * @param orderId 订单id
     * @return com.medusa.gruul.order.model.OrderVo order vo
     */
    @Override
    public OrderVo orderInfo(Long orderId) {
        OrderVo vo = baseMapper.selectOrderVoById(orderId);
        if (vo.getStatus().equals(OrderStatusEnum.WAIT_FOR_PAY) && ObjectUtil.isNull(vo.getExpireTime())) {
            vo.setExpireTime(vo.getCreateTime().plusSeconds(getExTime(vo.getType()) / 1000));
        }
        vo.setProductTotalQuantity(vo.getOrderItemList().stream().mapToInt(OrderItem::getProductQuantity).sum());

        return vo;
    }

    /**
     * 用户查看评价列表
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2023/12/4 20:20
     */
    @Override
    public PageUtils<ManageEvaluateVo> searchOrderEvaluate(ApiSearchEvaluateDto dto) {
        Page<ManageEvaluateVo> page = orderEvaluateMapper.userSearchOrderEvaluate(new Page<>(dto.getCurrent(),
                dto.getSize()), ISecurity.userMust().getId());
        return new PageUtils<>(page);
    }

    @Override
    public void completeOrder(Long orderId) {
        Order order = baseMapper.selectById(orderId);
        if (ObjectUtil.isNull(order)) {
            return;
        }
        if (!OrderStatusEnum.canCompleted(order.getStatus())) {
            return;
        }
        order.setStatus(OrderStatusEnum.COMPLETE);
        order.setCompleteTime(LocalDateTime.now());
        baseMapper.updateById(order);
        sender.sendCompletedOrderMessage(orderInfo(orderId));

    }

    /**
     * 重新获取运费
     *
     * @param dto the dto
     * @return java.math.BigDecimal freight amount
     */
    @Override
    public CountCostDto getFreightAmount(GetCostDto dto) {
        log.info("GetCostDto is {}", ObjectUtil.isNotNull(dto) ? dto.toString() : "");
        CountCostDto dtoResult = new CountCostDto();
        TimeInterval timer = DateUtil.timer();
        if (CommonConstants.NUMBER_ONE.equals(dto.getType())) {
            List<LogisticsFreightDto> freightDtoList = new ArrayList<>();
            List<ItemVo> itemVos = remoteGoodsService.findItemVoByIds(dto.getItemSkuIds());
            Map<Long, Integer> numberMap = dto.getItems().stream().collect(Collectors.toMap(ItemDto::getSkuId,
                    ItemDto::getNumber));
            for (ItemVo itemVo : itemVos) {
                // 商品包邮 跳出当前add追加
                if (itemVo.getFreightTemplateId() == 0) {
                    continue;
                }
                LogisticsFreightDto freightDto = new LogisticsFreightDto();
                freightDto.setFreightTemplateId(itemVo.getFreightTemplateId());
                freightDto.setPrice(itemVo.getProductPrice());
                freightDto.setWeight(itemVo.getWeight());
                freightDto.setNum(numberMap.get(itemVo.getProductSkuId()));

                freightDtoList.add(freightDto);
            }

            log.info("getLogisticsFreightCalculation param is {} {}", JSONUtil.toJsonStr(freightDtoList),
                    dto.getRegion());
            BigDecimal cost = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(freightDtoList)) {
                cost = remoteLogisticsFeginService.getLogisticsFreightCalculation(JSONUtil.toJsonStr(freightDtoList),
                        dto.getRegion());
            }

            log.info("getLogisticsFreightCalculation result is {}", cost);
            log.info("查询快递运费耗时{}ms", timer.intervalRestart());
            dtoResult = new CountCostDto();
            dtoResult.setCost(cost);
        }
        return dtoResult;

    }

    /**
     * 我的订单概况
     *
     * @return com.medusa.gruul.order.api.model.OrderOverviewVo order overview vo
     */
    @Override
    public OrderOverviewVo orderOverview() {
        List<OrderStatusEnum> orderStatusEnumList = Arrays.asList(OrderStatusEnum.WAIT_FOR_PAY,
                OrderStatusEnum.SHIPPED, OrderStatusEnum.WAIT_FOR_PICKUP);
        List<Order> orderList = baseMapper.selectList(new LambdaQueryWrapper<Order>()
                .in(Order::getStatus, orderStatusEnumList)
                .eq(Order::getUserId, ISecurity.userMust().getId())
        );
        OrderOverviewVo vo = new OrderOverviewVo();
        vo.setWaitForPay(orderList.stream().filter(o -> o.getStatus() == OrderStatusEnum.WAIT_FOR_PAY).count());
        vo.setShipped(orderList.stream().filter(o -> o.getStatus() == OrderStatusEnum.SHIPPED).count());
        vo.setWaitForPickup(orderList.stream().filter(o -> o.getStatus() == OrderStatusEnum.WAIT_FOR_PICKUP).count());
        return vo;
    }

    /**
     * 小程序端我的订单概况
     *
     * @param userId 用户ID
     * @return com.medusa.gruul.order.api.model.OrderOverviewVo order overview vo
     */
    @Override
    public OrderOverviewVo orderOverview(String userId) {
        List<OrderStatusEnum> orderStatusEnumList = Arrays.asList(OrderStatusEnum.WAIT_FOR_PAY,
                OrderStatusEnum.SHIPPED, OrderStatusEnum.WAIT_FOR_PICKUP, OrderStatusEnum.WAIT_FOR_SEND);
        List<Order> orderList = baseMapper.selectList(new LambdaQueryWrapper<Order>()
                .in(Order::getStatus, orderStatusEnumList)
                .eq(Order::getUserId, userId)
                .notIn(Order::getType, OrderTypeEnum.REPLENISH)
        );
        OrderOverviewVo vo = new OrderOverviewVo();
        List<AfsOrder> afsOrders = afsOrderMapper.selectList(new LambdaQueryWrapper<AfsOrder>()
                .eq(AfsOrder::getUserId, userId));
        long afsOrdersNumber = 0;
        if (CollectionUtil.isNotEmpty(afsOrders)) {
            //计算售后订单未处理个数
            afsOrdersNumber = afsOrders.stream().filter(afsOrder -> afsOrder.getStatus() != AfsOrderStatusEnum.SUCCESS && afsOrder.getStatus() != AfsOrderStatusEnum.CLOSE).count();
        }
        vo.setWaitForPay(orderList.stream().filter(o -> o.getStatus() == OrderStatusEnum.WAIT_FOR_PAY).count());
        vo.setShipped(orderList.stream().filter(o -> o.getStatus() == OrderStatusEnum.SHIPPED).count());
        vo.setWaitForPickup(orderList.stream().filter(o -> o.getStatus() == OrderStatusEnum.WAIT_FOR_PICKUP).count());
        vo.setWithDelivery(orderList.stream().filter(o -> o.getStatus() == OrderStatusEnum.WAIT_FOR_SEND).count());
        vo.setAfsOrder(afsOrdersNumber);
        return vo;
    }

    /**
     * 小程序查询晒单内容
     *
     * @param orderId 订单id
     * @return OrderShareInfo
     */
    @Override
    public OrderShareInfo orderShareInfo(Long orderId) {
        OrderShareSetting setting = orderShareSettingService.getSetting();
        OrderDelivery orderDelivery = orderDeliveryMapper.selectByOrderId(orderId);
        List<SimpleOrderItemVo> orderItemVos = orderItemMapper.selectSimpleOrderItemVoByOrderId(orderId);
        OrderShareInfo orderShareInfo = new OrderShareInfo();
        if (ObjectUtil.isNull(setting)) {
            throw new ServiceException("获取分享设置失败，请联系商家检查分享设置");
        }
        orderShareInfo.setTitle(setting.getTitle().replace("{sname}", orderDelivery.getReceiverName()));
        orderShareInfo.setBackground(setting.getBackground());
        List<ShareItemVo> shareItemVos = new ArrayList<>();
        for (SimpleOrderItemVo orderItemVo : orderItemVos) {
            ShareItemVo shareItemVo = new ShareItemVo();
            shareItemVo.setProductName(orderItemVo.getProductName());
            shareItemVo.setProductPic(orderItemVo.getProductPic());
            shareItemVo.setProductQuantity(orderItemVo.getProductQuantity());
            shareItemVos.add(shareItemVo);
        }
        orderShareInfo.setItemList(shareItemVos);
        return orderShareInfo;
    }

    /**
     * 获取接龙活动最近的买家
     *
     * @param solitaireActivityId 接龙活动id
     * @return List<LatelyBuyerVo>
     */
    @Override
    public List<LatelyBuyerVo> solitaireLatelyBuyer(Integer solitaireActivityId) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getSolitaireActivityId, solitaireActivityId)
                .isNotNull(Order::getPayTime));
        List<LatelyBuyerVo> vos = baseMapper.getSolitaireLatelyBuyer(solitaireActivityId);
        for (LatelyBuyerVo vo : vos) {
            vo.setSn(count);
            count--;
        }
        return vos;
    }

    /**
     * 获取接龙活动买家
     *
     * @param dto 接龙买家信息
     * @return the page utils
     */
    @Override
    public PageUtils<SolitaireBuyerVo> solitaireBuyerPage(SolitaireBuyerDto dto) {

        Page<SolitaireBuyerVo> vos = baseMapper.getSolitaireBuyerPage(new Page(dto.getCurrent(), dto.getSize()),
                dto.getSolitaireActivityId());

        if (CollUtil.isEmpty(vos.getRecords())) {
            return new PageUtils<>(vos);
        }
        long count = vos.getTotal();
        count = count - (long) (dto.getCurrent() - 1) * dto.getSize();
        for (SolitaireBuyerVo record : vos.getRecords()) {
            record.setSn(Math.toIntExact(count));
            count--;
        }
        return new PageUtils<>(vos);
    }

    /**
     * 获取我的接龙活动订单
     *
     * @param solitaireActivityId the solitaire activity id
     * @return the list
     */
    @Override
    public List<SolitaireBuyerVo> mySolitaireOrder(Integer solitaireActivityId) {
        List<Long> orderIdList = baseMapper.selectList(new LambdaQueryWrapper<Order>()
                .select(Order::getId)
                .eq(Order::getSolitaireActivityId, solitaireActivityId)
                .isNotNull(Order::getPayTime)
                .orderByDesc(Order::getPayTime)).stream().map(Order::getId).toList();
        List<SolitaireBuyerVo> vos = baseMapper.getMySolitaireOrder(solitaireActivityId,
                ISecurity.userMust().getId());
        if (CollUtil.isEmpty(vos)) {
            return vos;
        }
        for (SolitaireBuyerVo vo : vos) {
            if (CollUtil.isNotEmpty(orderIdList) && orderIdList.contains(vo.getOrderId())) {
                vo.setSn(orderIdList.indexOf(vo.getOrderId()) + 1);
            }
        }

        return vos;
    }

    /**
     * 订单退款回调
     *
     * @param message the message
     */
    @Override
    public void refundNotify(RefundNotifyResultDto message) {
        log.info(message.toString());
        Order order = baseMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getTransactionId, message.getOutTradeNo()));
        order.setRefundTransactionId(message.getRefundId());
        baseMapper.updateById(order);
        OrderVo vo = baseMapper.selectOrderVoById(order.getId());
        sender.sendRefundSuccess(vo);
    }


}
