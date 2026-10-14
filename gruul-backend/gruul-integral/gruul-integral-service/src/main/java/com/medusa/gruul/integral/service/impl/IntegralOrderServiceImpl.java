package com.medusa.gruul.integral.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.*;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.IPUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.constant.IntegralConstant;
import com.medusa.gruul.integral.api.constant.IntegralOrderConstent;
import com.medusa.gruul.integral.api.constant.IntegralQueueEnum;
import com.medusa.gruul.integral.api.constant.IntegralQueueNameConstant;
import com.medusa.gruul.integral.api.entity.IntegralDetail;
import com.medusa.gruul.integral.api.entity.IntegralGoods;
import com.medusa.gruul.integral.api.entity.IntegralOrder;
import com.medusa.gruul.integral.api.model.IntegralOrderDTO;
import com.medusa.gruul.integral.api.model.IntegralOrderDeliveryDto;
import com.medusa.gruul.integral.api.model.IntegralOrderVo;
import com.medusa.gruul.integral.mapper.IntegralDetailMapper;
import com.medusa.gruul.integral.mapper.IntegralGoodsMapper;
import com.medusa.gruul.integral.mapper.IntegralOrderMapper;
import com.medusa.gruul.integral.mq.Sender;
import com.medusa.gruul.integral.service.IIntegralOrderService;
import com.medusa.gruul.integral.web.enums.IntegralGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralOperationTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralOrderStatusEnum;
import com.medusa.gruul.integral.web.enums.IntegralPayTypeEnum;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralOrderDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralReceivingInfoDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralDeliveryFiltrateParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsOrderParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodsOrderVo;
import com.medusa.gruul.logistics.api.feign.RemoteLogisticsFeginService;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsFreightDto;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.payment.api.model.dto.PayRequestDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.platform.api.constant.ExchangeConstant;
import com.medusa.gruul.platform.api.constant.QueueNameConstant;
import com.medusa.gruul.platform.api.model.dto.SubscribeMsgSendDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 积分订单表 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Slf4j
@Service
public class IntegralOrderServiceImpl extends ServiceImpl<IntegralOrderMapper, IntegralOrder>
        implements IIntegralOrderService {

    @Autowired
    private IntegralOrderMapper integralOrderMapper;

    @Autowired
    private RemotePaymentService remotePaymentService;

    @Autowired
    private IntegralGoodsMapper integralGoodsMapper;

    @Autowired
    private RemoteMiniAccountService remoteMiniAccountService;

    @Autowired
    private IntegralDetailMapper integralDetailMapper;

    @Autowired
    private RemoteMiniAccountService accountService;


    @Autowired
    private RemoteLogisticsFeginService remoteLogisticsFeginService;

    @Autowired
    private Sender sender;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 获取积分商品订单分页对象
     *
     * @param integralGoodsOrderParam 查询条件
     * @return 积分商品订单分页对象
     */
    @Override
    public IPage<IntegralGoodsOrderVo> searchOrders(IntegralGoodsOrderParam integralGoodsOrderParam) {
        //订单状态 : -1所有订单 0.待发货 1.确认发货 2.已完成
        switch (integralGoodsOrderParam.getPrizeOrderStatus()) {
            case -1:
                integralGoodsOrderParam.setPrizeOrderStatus(IntegralOrderStatusEnum.ALL.getStatus());
                break;
            case 0:
                integralGoodsOrderParam.setPrizeOrderStatus(IntegralOrderStatusEnum.WAIT_FOR_SEND.getStatus());
                break;
            case 1:
                integralGoodsOrderParam.setPrizeOrderStatus(IntegralOrderStatusEnum.SHIPPED.getStatus());
                break;
            case 2:
                integralGoodsOrderParam.setPrizeOrderStatus(IntegralOrderStatusEnum.COMPLETE.getStatus());
            default:
        }
        IPage<IntegralGoodsOrderVo> page = new Page<>(integralGoodsOrderParam.getCurrent(),
                integralGoodsOrderParam.getSize());
        return page.setRecords(this.baseMapper.getGoodsOrderList(page, integralGoodsOrderParam));
    }


    /**
     * 获取单个积分订单信息
     *
     * @param orderId 订单id
     * @return IntegralGoodsOrderVo
     */
    @Override
    public IntegralGoodsOrderVo getIntegralOrderInfo(Long orderId) {
        //根据orderId 获取单个积分订单信息
        return this.baseMapper.getGoodsOrderInfo(orderId);
    }


    /**
     * 商家pc端 积分订单备注
     *
     * @param integralOrders 订单ids
     * @param backRemark     备注内容
     * @param isCoverage  是否覆盖
     */
    @Override
    public void remarkIntegralOrder(List<Long> integralOrders, String backRemark, Boolean isCoverage) {
        List<IntegralOrder> iOrders = baseMapper.selectByIds(integralOrders);
        if (CollectionUtil.isNotEmpty(iOrders)) {
            for (IntegralOrder integralOrder : iOrders) {
                StringBuffer sb = new StringBuffer();
                if (StrUtil.isBlank(backRemark)) {
                    backRemark = "";
                }
                //是否进行覆盖 true 进行覆盖 false不覆盖
                if (!isCoverage) {
                    integralOrder.setBackRemark(
                            sb.append(integralOrder.getBackRemark()).append(StrUtil.CRLF).append(backRemark)
                                    .toString());
                } else {
                    integralOrder.setBackRemark(backRemark);
                }
            }
            boolean update = this.updateBatchById(iOrders);
            if (!update) {
                throw new ServiceException("批量备注失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
    }


    /**
     * 生成积分订单
     *
     * @param integralOrderDto
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public String addIntegralOrder(IntegralOrderDto integralOrderDto) {

        List<Integer> longs = new ArrayList<Integer>(CommonConstants.NUMBER_THREE);
        longs.add(CommonConstants.NUMBER_ONE);
        longs.add(CommonConstants.NUMBER_TWO);
        long prizeOrderId;
        //获取用户扩展数据信息
        AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(integralOrderDto.getUserId(), longs);
        IntegralGoods integralGoods = integralGoodsMapper.selectById(integralOrderDto.getPrizeId());
        //得到用户总积分值
        BigDecimal integral = accountInfoDto.getMiniAccountExtends().getIntegral();
        //获取用户状态
        Integer isBlacklist = accountInfoDto.getMiniAccountExtends().getIsBlacklist();
        //用户不在商家黑名单中
        if (isBlacklist != 1) {
            //商品库存大于0
            if (integralGoods.getInventory() > 0) {
                //用户总积分大于等于 本次兑换商品实付积分
                if (integral.compareTo(integralOrderDto.getPrice()) > -1) {
                    do {
                        //生成一个14bit的积分订单号
                        prizeOrderId = RandomUtil
                                .randomLong(IntegralOrderConstent.MIN_ID, IntegralOrderConstent.MAX_ID);
                        IntegralOrder integralOrder = this.baseMapper
                                .selectOne(new LambdaQueryWrapper<IntegralOrder>().eq(IntegralOrder::getPrizeOrderId, prizeOrderId));
                        if (BeanUtil.isEmpty(integralOrder)) {
                            break;
                        }
                    } while (true);
                    IntegralOrder integralOrder = integralOrderDto.coverIntegralOrder();
                    //set 订单号
                    integralOrder.setOpenId(ISecurity.userMust().getOpenid());
                    integralOrder.setPrizeOrderId(prizeOrderId);
                    integralOrder.setMemberAvatarUrl(accountInfoDto.getMiniAccountunt().getAvatarUrl());
                    integralOrder.setWxuserName(accountInfoDto.getMiniAccountunt().getNikeName());
                    integralOrder.setPayFee(integralOrderDto.getShippingFee());
                    //set订单类型 0为积分商品订单 1为积分优惠券订单
                    integralOrder.setOrderType(integralOrderDto.getOrderType());
                    integralOrder.setPrizeOrderStatus(IntegralOrderStatusEnum.UNPAID.getStatus());
                    //set 配送方式 0为物流配送 展示只有物流配送
                    integralOrder.setDistriMode(0);
                    integralOrder.setIntegralGoodName(integralGoods.getIntegralGoodName());
					/*//拿到邮费 判断是否要进行金额支付
					if (integralOrderDto.getShippingFee().compareTo(BigDecimal.ZERO) > 0) {
                        integralOrder.setPrizeOrderStatus(IntegralOrderStatusEnum.UNPAID.getStatus());
					}*/
                    //判断是否是余额支付
                    if (integralOrderDto.getPayType() == IntegralPayTypeEnum.BALANCE_PAY.getStatus()) {
                        boolean isMemberBalancePay = memberBalancePay(integralOrderDto, prizeOrderId);
                        integralOrder.setPrizeOrderStatus(IntegralOrderStatusEnum.WAIT_FOR_SEND.getStatus());
                        if (!isMemberBalancePay) {
                            throw new ServiceException("余额扣除异常,请稍后重试!");
                        }
                    }
                    int insert = integralOrderMapper.insert(integralOrder);
                    //插入失败
                    if (insert < 1) {
                        throw new ServiceException("订单生成失败", SystemCode.DATA_ADD_FAILED.getCode());
                    }
                    //修改积分商品库存
                    integralGoods.setInventory(integralGoods.getInventory() - 1);
                    integralGoods.setSales(integralGoods.getSales() + 1);
                    integralGoodsMapper.updateById(integralGoods);

                    //生成 积分明细表 插入数据
                    saveIntegralDetail(integralOrder);
                    //2. 通知 用户修改积分
                    sendIntegralMessage(integralOrderDto.getPrice(), integralOrderDto.getUserId(), IntegralOperationTypeEnum.REDUCE.getStatus(), integralOrder.getShopId());
                    /*
                     * 非余额支付,发送延迟队列
                     */
                    if (integralOrderDto.getPayType() != IntegralPayTypeEnum.BALANCE_PAY.getStatus()) {
                        sender.delayIntegralOrder(new IntegralOrderDTO().setOrderId(prizeOrderId), 5 * 60);
                    }
                    return prizeOrderId + "";
                }
                throw new ServiceException("积分不足");
            }
            throw new ServiceException("库存不足", SystemCode.ITEM_SOLD_OUT.getCode());
        }
        throw new ServiceException("账号异常请联系客服");
    }

    /**
     * mq通知用户共方法
     */
    public void sendIntegralMessage(BigDecimal integral, String userId, Integer integralType, String shopId) {
        IntegraChangelDto integraChangelDto = new IntegraChangelDto();
        integraChangelDto.setValue(integral);
        integraChangelDto.setUserId(userId);
        integraChangelDto.setOption(integralType);
        integraChangelDto.setShopId(shopId);
        sender.sendIntegralDeductionMessage(integraChangelDto);
    }

    /**
     * 会员余额支付
     *
     * @param integralOrderDto 积分订单dto
     * @param prizeOrderId     积分订单id
     * @return 扣除会员用户余额是否成功
     */
    private boolean memberBalancePay(IntegralOrderDto integralOrderDto, Long prizeOrderId) {
        MemberCardInfoCenterDto memberInfo = remoteMiniAccountService
                .getMemberInfo(integralOrderDto.getUserId());
        log.info(STR."会员卡信息\{memberInfo}");
        //会员扣除余额dto
        MemberBalanceUpdateDto memberBalanceUpdateDto = new MemberBalanceUpdateDto();
        memberBalanceUpdateDto.setUseBalance(integralOrderDto.getShippingFee());
        // 用戶不存在会员卡 or 会员卡已到期
        if (ObjectUtil.isNotNull(memberInfo)) {
            memberBalanceUpdateDto.setMemberInfoId(memberInfo.getId());
        } else {
            memberBalanceUpdateDto.setShopUserId(integralOrderDto.getUserId());
        }
        memberBalanceUpdateDto.setDetailName("购买积分商品");
        //变化类型 0-添加 1-减少
        memberBalanceUpdateDto.setChangeType(1);
        //交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功
        memberBalanceUpdateDto.setDealType(0);
        //订单来源：1-会员，2-商品，3-积分
        memberBalanceUpdateDto.setSource(3);
        memberBalanceUpdateDto.setOutOrderId(prizeOrderId);
        Boolean flag = remoteMiniAccountService.memberBalanceModify(memberBalanceUpdateDto);
        if (flag) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    private void saveIntegralDetail(IntegralOrder integralOrder) {
        String shopId = ISecurity.userMust().getShopId();
        //兑换积分商品直接生成积分明细
        IntegralDetail integralDetail = new IntegralDetail();
        integralDetail.setShopId(shopId);
        //取反 积分商品积分值
        integralDetail.setIntegral(integralOrder.getPrice().negate());
        BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, integralOrder.getUserId());
        //现有积分
        integralDetail.setNowTheIntegral(bigDecimal.subtract(integralOrder.getPrice()));
        integralDetail.setUserId(integralOrder.getUserId());
        integralDetail.setGainIntegralType(IntegralGainTypeEnum.EXCHANGE_INTEGRAL_GOODS.getStatus());
        integralDetail.setPrizeOrderId(integralOrder.getPrizeOrderId());
        integralDetailMapper.insert(integralDetail);
    }


    /**
     * 商家批量确认收货功能
     *
     * @param orderIds 订单ids
     */
    @Override
    public void receive(List<Long> orderIds) {
        List<IntegralOrder> integralOrders = baseMapper.selectByIds(orderIds);
        if (CollectionUtil.isNotEmpty(integralOrders)) {
            for (IntegralOrder integralOrder : integralOrders) {
                //设置收货时间
                integralOrder.setReceiveTime(LocalDateTime.now());
                //设置订单为已完成状态
                integralOrder.setPrizeOrderStatus(IntegralOrderStatusEnum.COMPLETE.getStatus());
            }
            boolean b = this.updateBatchById(integralOrders);
            if (!b) {
                throw new ServiceException("收货失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
    }

    @Override
    public PayResultDto payIntegralOrder(Long prizeOrderId, HttpServletRequest request) {
        IntegralOrder integralOrder = baseMapper.selectById(prizeOrderId);
        SecureUser secureUser = ISecurity.userMust();
        if (ObjectUtil.isNull(integralOrder)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        if (integralOrder.getShippingFee().doubleValue() < IntegralConstant.MIN_PAY_FEE) {
            throw new ServiceException("支付金额有误");
        }
        PayRequestDto dto = new PayRequestDto();
        dto.setShopId(ISecurity.userMust().getShopId());
        dto.setPayChannel(CommonConstants.NUMBER_ONE);
        dto.setTradeType(CommonConstants.NUMBER_ONE);
        dto.setOutTradeNo(String.valueOf(prizeOrderId));
        dto.setRouteKey(IntegralQueueEnum.INTEGRAL_QUEUE_ORDER_PAYMENT_NOTIFY.getRouteKey());
        dto.setOpenId(secureUser.getOpenid());
        //支付回调
        dto.setTotalFee(integralOrder.getPayFee());
        dto.setTerminalIp(IPUtils.getIpAddr(request));
        dto.setFeeType("CNY");
        dto.setTimeoutExpress("1d");
        dto.setBody("小程序购物");
        dto.setSubject("");
        PayResultDto payResultDto = remotePaymentService.payRequest(dto);
        if (ObjectUtil.isNull(payResultDto) || ObjectUtil.isNull(payResultDto.getWxResult()) || ObjectUtil
                .isNull(payResultDto.getWxResult().getTransactionId())) {
            log.error("支付创建失败,入参为: {}", dto);
            log.error("支付创建失败,出参为: {}", payResultDto.toString());
        }
        log.info("pay result is {}", payResultDto);
        return payResultDto;
    }


    /**
     * 支付成功  修改订单状态
     *
     * @param prizeOrderId
     */
    @Override
    public void integralOrderDone(Long prizeOrderId) {
        IntegralOrder integralOrder = baseMapper.selectById(prizeOrderId);
        if (ObjectUtil.isNull(integralOrder)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        integralOrder.setPrizeOrderStatus(IntegralOrderStatusEnum.WAIT_FOR_SEND.getStatus());
        this.baseMapper.updateById(integralOrder);
        //封装 MiniAccountExtendsUpdateDto 更新用户展示信息 消费金额及次数等
        MiniAccountExtendsUpdateDto miniAccountExtendsUpdateDto = new MiniAccountExtendsUpdateDto();
        miniAccountExtendsUpdateDto.setOptionType(1);
        miniAccountExtendsUpdateDto.setCumsumType(1);
        miniAccountExtendsUpdateDto.setBusinessId(integralOrder.getPrizeOrderId().toString());
        miniAccountExtendsUpdateDto.setCumsumLimit(integralOrder.getPayFee());
        miniAccountExtendsUpdateDto.setLastDealTime(integralOrder.getPayTime());
        Boolean flag = accountService.portionAttributeModify(integralOrder.getUserId(), miniAccountExtendsUpdateDto);
        if (!flag) {
            throw new ServiceException("金额累加失败");
        }
    }


    /**
     * 支付失败 删除订单 归还用户积分 归还商品信息
     *
     * @param prizeOrderId
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void delIntegralOrder(Long prizeOrderId) {
        IntegralOrder integralOrder = baseMapper.selectById(prizeOrderId);
        if (ObjectUtil.isNull(integralOrder) || IntegralOrderStatusEnum.UNPAID.getStatus() != integralOrder.getPrizeOrderStatus()) {
            return;
        }

        //生成明细记录
        IntegralDetail integralDetail = new IntegralDetail();
        integralDetail.setIntegral(integralOrder.getPrice());
        BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(integralOrder.getShopId(), integralOrder.getUserId());
        //现有积分
        integralDetail.setNowTheIntegral(bigDecimal.add(integralOrder.getPrice()));
        integralDetail.setUserId(integralOrder.getUserId());
        integralDetail.setGainIntegralType(IntegralGainTypeEnum.GIVE_BACK.getStatus());
        integralDetail.setPrizeOrderId(integralOrder.getPrizeOrderId());
        int insert = integralDetailMapper.insert(integralDetail);
        if (insert < 1) {
            throw new ServiceException("积分明细插入失败", SystemCode.DATA_ADD_FAILED.getCode());
        }

        //通知用户增加积分
        sendIntegralMessage(integralOrder.getPrice(), integralOrder.getUserId(), IntegralOperationTypeEnum.INCREASE.getStatus(), integralOrder.getShopId());

        //商品库存 还库操作
        IntegralGoods integralGoods = integralGoodsMapper.selectById(integralOrder.getPrizeId());
        integralGoods.setInventory(integralGoods.getInventory() + 1);
        integralGoods.setSales(integralGoods.getSales() - 1);
        //该商品已增加销量不进行扣除
        int update = integralGoodsMapper.updateById(integralGoods);
        if (update < 1) {
            throw new ServiceException("商品信息修改失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        //删除订单 使用物理删除
        this.baseMapper.deleteById(prizeOrderId);
    }


    /**
     * 积分商品订单 批量发货
     *
     * @param integralGoodsOrderVos
     */
    @Override
    public void shipments(List<IntegralGoodsOrderVo> integralGoodsOrderVos) {
        IntegralOrder integralOrder;
        for (IntegralGoodsOrderVo integralGoodsOrderVo : integralGoodsOrderVos) {
            IntegralOrder integralOrderSearch = this.baseMapper.selectById(integralGoodsOrderVo.getPrizeOrderId());
            // 简单对运单号进行限制
            String deliverySn = integralGoodsOrderVo.getDeliverySn();
            //对订单号进行简单的验证
            boolean sf = deliverySn.contains("SF");
            if (sf) {
                try {
                    String substring = deliverySn.substring(2);
                    Long.valueOf(substring);
                } catch (Exception e) {
                    throw new ServiceException("请输入正确订单号");
                }
            } else {
                try {
                    Long.valueOf(integralGoodsOrderVo.getDeliverySn());
                } catch (Exception e) {
                    log.error("", e);
                    throw new ServiceException("请输入正确订单号");
                }
            }
            integralGoodsOrderVo.setPrizeOrderStatus(IntegralOrderStatusEnum.SHIPPED.getStatus());
            integralGoodsOrderVo.setDeliverySn(integralGoodsOrderVo.getDeliverySn());

            //物流公司转换
            String deliveryCompany = logisticsCompanyConvertTo(integralGoodsOrderVo.getDeliveryCompany());
            integralGoodsOrderVo.setDeliveryCompany(deliveryCompany);
            integralOrder = integralGoodsOrderVo.coverIntegralOrder();
            integralOrder.setSendTime(LocalDateTime.now());
            int i = this.baseMapper.updateById(integralOrder);
            if (i < 1) {
                throw new ServiceException("积分商品订单发货失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            //进行消息订阅
            if ((!"-1".equals(integralGoodsOrderVo.getDeliveryTemplateId()))
                    && integralGoodsOrderVo.getDeliveryTemplateId() != null) {
                SubscribeMsgSendDto dto = new SubscribeMsgSendDto();
                dto.setTemplateId(integralGoodsOrderVo.getDeliveryTemplateId());
                dto.setShopId(integralOrder.getShopId());
                dto.setOpenId(integralOrderSearch.getOpenId());
                dto.setToPath(StrUtil.EMPTY);
                LinkedList<String> templateDetail = new LinkedList<>();
                templateDetail.add(integralGoodsOrderVo.getPrizeOrderId().toString());
                templateDetail.add(DateTime.now().toString("yyyy年MM月dd日 HH:mm"));
                //限制商品详情 长度
                if (integralGoodsOrderVo.getIntegralGoodName().length() < 16) {
                    templateDetail.add(integralGoodsOrderVo.getIntegralGoodName());
                } else {
                    templateDetail.add(integralGoodsOrderVo.getIntegralGoodName().substring(0, 13).concat("..."));
                }
                templateDetail.add(integralGoodsOrderVo.getDeliverySn());
                templateDetail.add(integralGoodsOrderVo.getReceiverName());
                dto.setSendDatas(templateDetail);
                rabbitTemplate.convertAndSend(ExchangeConstant.PLATFORM_EXCHANGE,
                        QueueNameConstant.PLATFORM_SUBSCRIBE_MSG_SEND, dto);
            }
        }
    }

    /**
     * 物流公司 转换 例 sf = 顺丰快递 使用了魔法值  延期修改
     *
     * @param deliveryCompany 物流公司
     * @return
     */
    private String logisticsCompanyConvertTo(String deliveryCompany) {
        if (IntegralConstant.SF_CODE.equals(deliveryCompany)) {
            deliveryCompany = IntegralConstant.SF_NAME;
        }
        if (IntegralConstant.ZT_CODE.equals(deliveryCompany)) {
            deliveryCompany = IntegralConstant.ZT_NAME;
        }
        if (IntegralConstant.YT_CODE.equals(deliveryCompany)) {
            deliveryCompany = IntegralConstant.YT_NAME;
        }
        if (IntegralConstant.YD_CODE.equals(deliveryCompany)) {
            deliveryCompany = IntegralConstant.YD_NAME;
        }
        if (IntegralConstant.HT_CODE.equals(deliveryCompany)) {
            deliveryCompany = IntegralConstant.HT_NAME;
        }
        return deliveryCompany;
    }


    /**
     * 获取 该用户兑换该积分商品 所需要的运费金额
     *
     * @param regionCode        收货地址code
     * @param freightTemplateId 运费模板id
     * @param weight            重量
     * @return 运费金额
     */
    @Override
    public BigDecimal freight(String regionCode, Long freightTemplateId, BigDecimal weight) {
        List<LogisticsFreightDto> freightDtoList = new ArrayList<>();
        TimeInterval timer = DateUtil.timer();
        BigDecimal price = new BigDecimal("0.01");
        LogisticsFreightDto freightDto = new LogisticsFreightDto();
        freightDto.setFreightTemplateId(freightTemplateId);
        freightDto.setPrice(price);
        freightDto.setWeight(weight);
        freightDto.setNum(1);
        freightDtoList.add(freightDto);
        //查询运费
        BigDecimal cost = remoteLogisticsFeginService
                .getLogisticsFreightCalculation(JSONUtil.toJsonStr(freightDtoList), regionCode);
        log.info("查询快递运费耗时{}ms", timer.intervalRestart());
        return cost;
    }

    /**
     * 获取符合条件 的批量发货订单信息List
     *
     * @param integralDeliveryFiltrateParam 查询条件
     * @return
     */
    @Override
    public List<IntegralGoodsOrderVo> filtrate(IntegralDeliveryFiltrateParam integralDeliveryFiltrateParam) {
        List<IntegralGoodsOrderVo> filtrateGoodsOrderList = this.baseMapper
                .getFiltrateGoodsOrderList(integralDeliveryFiltrateParam);

        return filtrateGoodsOrderList;
    }

    /**
     * 积分订单打印发货 修改订单信息
     *
     * @param integralOrderDeliveryDtos 发货信息
     * @return
     */
    @Override
    public int doLogisticsIntegralOrderDelivery(List<IntegralOrderDeliveryDto> integralOrderDeliveryDtos) {
        AtomicInteger res = new AtomicInteger(0);
        integralOrderDeliveryDtos.forEach(integralOrderDeliveryDto -> {
            IntegralOrder integralOrder = baseMapper.selectById(integralOrderDeliveryDto.getIntegralOrderId());
            if (null == integralOrder) {
                throw new ServiceException("订单信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
            }
            integralOrder.setPrizeOrderStatus(IntegralOrderStatusEnum.SHIPPED.getStatus());
            integralOrder.setUpdateTime(LocalDateTime.now());
            integralOrder.setSendTime(LocalDateTime.now());
            integralOrder.setDeliveryCompany(integralOrderDeliveryDto.getDeliveryCompany());
            integralOrder.setDeliverySn(integralOrderDeliveryDto.getDeliverySn());
            integralOrder.setSortingCode(integralOrderDeliveryDto.getSortingCode());
            integralOrder.setPackageName(integralOrderDeliveryDto.getPackageName());
            integralOrder.setPackageCode(integralOrderDeliveryDto.getPackageCode());
            int i = baseMapper.updateById(integralOrder);
            res.set(i + i);
            //进行消息订阅 物流发货
            deliveryTemplate(integralOrder, IntegralConstant.LOGISTICS);

        });
        return res.get();
    }

    /**
     * 根据积分订单id获取 积分订单vo信息
     *
     * @param integralOrderId
     * @return
     */
    @Override
    public IntegralOrderVo integralOrderInfo(Long integralOrderId) {
        IntegralOrderVo integralOrderVo = baseMapper.selectIntegralOrderVoById(integralOrderId);
        return integralOrderVo;
    }


    /**
     * 修改订单状态 发货时间 订单状态
     *
     * @param longs 订单id s
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void updateIntegralOrderStatus(List<Long> longs) {
        List<IntegralOrder> integralOrders = baseMapper.selectByIds(longs);
        if (CollectionUtil.isNotEmpty(integralOrders)) {
            for (IntegralOrder integralOrder : integralOrders) {
                integralOrder.setSendTime(LocalDateTime.now());
                integralOrder.setPrizeOrderStatus(IntegralOrderStatusEnum.SHIPPED.getStatus());
                integralOrder.setDeliverySn("--");
                integralOrder.setDeliveryCompany("--");
                //进行消息订阅 无需物流发货
                deliveryTemplate(integralOrder, IntegralConstant.NO_LOGISTICS);
                boolean update = this.updateById(integralOrder);
                if (!update) {
                    throw new ServiceException("无需物流发货失败", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            }
        }
    }

    /**
     * 获取全部待发货订单信息
     *
     * @return
     */
    @Override
    public List<IntegralGoodsOrderVo> getAllWaitForSendIntegralOrder() {
        return integralOrderMapper.selectAllWaitForSendIntegralOrder(IntegralOrderStatusEnum.WAIT_FOR_SEND.getStatus());
    }

    /**
     * 修改积分订单收货人信息
     *
     * @param integralReceivingInfoDto 收货人信息 地址 姓名 电话号
     */
    @Override
    public void updateReceivingInfo(IntegralReceivingInfoDto integralReceivingInfoDto) {
        IntegralOrder integralOrderSearch = integralOrderMapper.selectById(integralReceivingInfoDto.getPrizeOrderId());
        if (BeanUtil.isEmpty(integralOrderSearch)) {
            throw new ServiceException("订单信息不存在 !", SystemCode.DATA_NOT_EXIST.getCode());
        }
        IntegralOrder integralOrder = integralReceivingInfoDto.coverIntegralOrder();
        int i = integralOrderMapper.updateById(integralOrder);
        if (i < 1) {
            throw new ServiceException("修改积分订单收货人信息失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        //清除页面缓存数据
    }

    /**
     * @param userId 用户id
     */
    @Override
    public Boolean memberInfo(String userId) {

        //获取用户信息
        MemberCardInfoCenterDto memberInfo = remoteMiniAccountService.getMemberInfo(userId);
        if (memberInfo != null) {
            log.info(STR."会员卡信息\{memberInfo}");
            List<MemberCardPrivilege> memberCardPrivileges = memberInfo.getMemberCardPrivileges();
            //是否存在会员权益
            if (memberCardPrivileges.size() > 0) {
                for (MemberCardPrivilege memberCardPrivilege : memberCardPrivileges) {
                    if (IntegralConstant.INTEGRAL_POSTAGE_PRIVILEGE_TYPE.equals(memberCardPrivilege.getPType())
                            & memberCardPrivilege.getOpen() == 1) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * H5支付
     *
     * @param prizeOrderId
     */
    @Override
    public Object payIntegralOrderPage(Long prizeOrderId) {
        IntegralOrder integralOrder = baseMapper.selectById(prizeOrderId);
        if (ObjectUtil.isNull(integralOrder)) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        if (integralOrder.getShippingFee().doubleValue() < IntegralConstant.MIN_PAY_FEE) {
            throw new ServiceException("支付金额有误");
        }
        PayParam payParam = new PayParam();
        //判断支付方式
        PayType wechat = PayType.ALIPAY;
        if (integralOrder.getPayType() == IntegralPayTypeEnum.WECHAT_PAY.getStatus()) {
            wechat = PayType.WECHAT;
        }
        payParam.setPayType(wechat)
                .setSubject("积分支付")
                .setBody("积分商品金额支付")
                .setOrderId(integralOrder.getPrizeOrderId().toString())
                .setRouteKey(IntegralQueueNameConstant.INTEGRAL_PAY)
                .setPrice(integralOrder.getPayFee())
                .setSeconds(15 * 60L);
        return remotePaymentService.toPayPage(payParam).successData().getData();
    }


    /**
     * 积分订单发货 发送消息模板 -1为未订阅 未使用常量
     *
     * @param integralOrder
     * @param code          1000 无需物流发货 10001 物流发货 区别打印物流单号的值
     */
    private void deliveryTemplate(IntegralOrder integralOrder, String code) {
        if ((!IntegralConstant.DEFAULT_STRING_ONE.equals(integralOrder.getDeliveryTemplateId())) && integralOrder.getDeliveryTemplateId() != null) {
            //进行消息订阅
            SubscribeMsgSendDto dto = new SubscribeMsgSendDto();
            dto.setTemplateId(integralOrder.getDeliveryTemplateId());
            dto.setShopId(integralOrder.getShopId());
            dto.setOpenId(integralOrder.getOpenId());
            dto.setToPath("");
            LinkedList<String> templateDetail = new LinkedList<>();
            templateDetail.add(integralOrder.getPrizeOrderId().toString());
            templateDetail.add(DateTime.now().toString("yyyy年MM月dd日 HH:mm"));
            if (integralOrder.getIntegralGoodName().length() > IntegralConstant.INTEGRAL_GOODS_NAME_LENGTH) {
                templateDetail.add(integralOrder.getIntegralGoodName().substring(0, 13).concat("..."));
            } else {
                templateDetail.add(integralOrder.getIntegralGoodName());
            }
            //判断是否是无需物流发货
            if (IntegralConstant.NO_LOGISTICS.equals(code)) {
                templateDetail.add("--");
            } else {
                templateDetail.add(integralOrder.getDeliverySn());
            }
            templateDetail.add(integralOrder.getReceiverName());
            dto.setSendDatas(templateDetail);
            rabbitTemplate.convertAndSend(ExchangeConstant.PLATFORM_EXCHANGE, QueueNameConstant.PLATFORM_SUBSCRIBE_MSG_SEND, dto);
        }
    }
}
