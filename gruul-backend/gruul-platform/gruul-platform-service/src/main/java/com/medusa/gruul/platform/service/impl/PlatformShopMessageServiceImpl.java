package com.medusa.gruul.platform.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.OrderKeyBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.ShippingListBean;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.WxMaOrderShippingInfoUploadRequest;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.order.api.entity.OrderItem;
import com.medusa.gruul.order.api.model.OrderDeliveryDto;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.platform.api.entity.PlatformShopMessage;
import com.medusa.gruul.platform.api.model.dto.SubscribeMsgSendDto;
import com.medusa.gruul.platform.api.model.vo.MiniMsgVo;
import com.medusa.gruul.platform.api.model.vo.ShopMessageDetailVo;
import com.medusa.gruul.platform.api.model.vo.ShopMessageVo;
import com.medusa.gruul.platform.mapper.PlatformShopMessageMapper;
import com.medusa.gruul.platform.model.dto.MiniSubscriberiMessageDto;
import com.medusa.gruul.platform.model.dto.MotifyMsgStateDto;
import com.medusa.gruul.platform.properties.PlatformProperties;
import com.medusa.gruul.platform.service.IPlatformShopMessageService;
import com.medusa.gruul.platform.service.IWechatPlatformService;
import com.medusa.gruul.platform.service.WechatMiniAppService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 店铺消息配置 服务实现类
 * </p>
 *
 * @author whh
 * @since 2024-05-22
 */
@Service
@Log4j2
public class PlatformShopMessageServiceImpl extends ServiceImpl<PlatformShopMessageMapper, PlatformShopMessage> implements IPlatformShopMessageService {

    @Autowired
    private WechatMiniAppService wechatMiniAppService;
    @Autowired
    private IWechatPlatformService wechatPlatformService;
    @Resource
    private PlatformProperties platformProperties;

    /**
     * 获取小程序订阅消息
     *
     * @return com.medusa.gruul.platform.api.model.vo.MiniMsgVo
     */
    @Override
    public List<MiniMsgVo> getCurrentMiniMsg() {
        String shopId = ISystem.shopId();
        if (StrUtil.isEmpty(shopId)) {
            throw new ServiceException("非法请求");
        }
        List<PlatformShopMessage> list = this.baseMapper.selectList(new LambdaQueryWrapper<PlatformShopMessage>()
                .eq(PlatformShopMessage::getUseType, CommonConstants.NUMBER_ONE));
        if (CollectionUtil.isEmpty(list)) {
            List<PlatformShopMessage> lastMiniMsg = getLastMiniMsg();
            if (CollectionUtil.isEmpty(lastMiniMsg)) {
                return new LinkedList<>();
            }
            return lastMiniMsg.stream().map(obj -> BeanUtil.toBean(obj, MiniMsgVo.class)).toList();
        }
        return list.stream().map(obj -> BeanUtil.toBean(obj, MiniMsgVo.class)).toList();
    }

    /**
     * 获取店铺消息
     *
     * @return com.medusa.gruul.platform.api.model.vo.ShopMessageVo
     */
    @Override
    public List<ShopMessageVo> msgAll() {
        String shopId = ISystem.shopId();
        if (StrUtil.isEmpty(shopId)) {
            throw new ServiceException("非法请求");
        }
        List<PlatformShopMessage> list = this.baseMapper.selectList(new LambdaQueryWrapper<PlatformShopMessage>()
                .eq(PlatformShopMessage::getUseType, CommonConstants.NUMBER_ONE));
        if (CollectionUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        Map<Integer, List<PlatformShopMessage>> listMap = list.stream().collect(Collectors.groupingBy(PlatformShopMessage::getMessageType));
        List<ShopMessageVo> vos = new LinkedList<>();
        //获取订单消息
        getVos(listMap.get(CommonConstants.NUMBER_ONE), vos, "订单消息");
        //获取用户消息
        getVos(listMap.get(CommonConstants.NUMBER_THREE), vos, "用户消息");
        return vos;
    }


    private List<PlatformShopMessage> getLastMiniMsg() {
        return this.baseMapper.getLastMiniMsg();
    }

    /**
     * 更新店铺订阅消息,在小程序审核通过之后才会去生成,因为消息最多25个
     *
     * @param shopId 店铺id
     */
    @Override
    public void upSubscriptionMsg(String shopId) {
        wechatPlatformService.addSubscribeMsg(shopId);
    }


    /**
     * 获取小程序订阅消息
     *
     * @return com.medusa.gruul.platform.api.model.vo.MiniMsgVo
     */
    @Override
    public List<PlatformShopMessage> getByVersionMes(Integer useType) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<PlatformShopMessage>()
                .eq(PlatformShopMessage::getUseType, useType));
    }

    /**
     * 封装数据
     *
     * @param platformShopMessages 指定类型数据
     * @param list                 数组
     * @param title                标题
     */
    private void getVos(List<PlatformShopMessage> platformShopMessages, List<ShopMessageVo> list, String title) {
        if (CollectionUtil.isEmpty(platformShopMessages)) {
            return;
        }
        ShopMessageVo vo = new ShopMessageVo();
        vo.setMsgTitle(title);
        List<ShopMessageDetailVo> vos = new LinkedList<>();
        for (PlatformShopMessage platformShopMessage : platformShopMessages) {
            ShopMessageDetailVo shopMessageDetailVo = BeanUtil.toBean(platformShopMessage, ShopMessageDetailVo.class);
            vos.add(shopMessageDetailVo);
        }
        vo.setShopMessageDetailVos(vos);
        list.add(vo);
    }


    /**
     * 发送订阅消息
     *
     * @param msgSendDto com.medusa.gruul.platform.model.dto.SubscribeMsgSendDto
     */
    @Override
    public void subscribeMsgSend(SubscribeMsgSendDto msgSendDto) {
        PlatformShopMessage miniSubscriberiMessage = this.getOne(new LambdaQueryWrapper<PlatformShopMessage>().eq(PlatformShopMessage::getMiniTemplateId, msgSendDto.getTemplateId()));
        if (miniSubscriberiMessage == null) {
            throw new ServiceException("无效templateId");
        }

        MiniSubscriberiMessageDto miniSubscriberiMessageDto = JSONUtil.toBean(miniSubscriberiMessage.getMiniMsg(), MiniSubscriberiMessageDto.class);
        String kIds = miniSubscriberiMessageDto.getKIds();
        String rules = miniSubscriberiMessageDto.getRules();
        String[] kIdArr = kIds.split(",");
        String[] rulesArr = rules.split(",");
        LinkedList<String> sendDatas = msgSendDto.getSendDatas();
        if (CollectionUtil.isEmpty(sendDatas)) {
            throw new ServiceException("sendDatas=null");
        }
        if (kIdArr.length != sendDatas.size()) {
            throw new ServiceException("消息内容数据错误,kIdArr.length=".concat(String.valueOf(kIdArr.length)).concat("sendDatas.size=").concat(String.valueOf(msgSendDto.getSendDatas().size())));
        }
        String miniprogramState = "";
        if (StrUtil.isNotEmpty(msgSendDto.getToPath())) {
            miniprogramState = WxMaConstants.MiniProgramState.DEVELOPER;
        }
        //判断发送那个环境
        List<WxMaSubscribeMessage.MsgData> datas = new ArrayList<>();
        for (int i = 0; i < rulesArr.length; i++) {
            WxMaSubscribeMessage.MsgData data = new WxMaSubscribeMessage.MsgData();
            data.setName(rulesArr[i] + kIdArr[i]);
            data.setValue(sendDatas.get(i));
            datas.add(data);
        }
        sendSubscribeMsg(msgSendDto.getShopId(), msgSendDto.getOpenId(), msgSendDto.getTemplateId(), msgSendDto.getToPath(), datas, miniprogramState);

    }

    /**
     * 修改消息状态
     *
     * @param msgStateDto com.medusa.gruul.platform.model.dto.MotifyMsgStateDto
     */
    @Override
    public void motifyState(MotifyMsgStateDto msgStateDto) {
        PlatformShopMessage shopMessage = this.getById(msgStateDto.getId());
        if (shopMessage == null) {
            throw new ServiceException("不存在指定消息");
        }
        if (msgStateDto.getMiniOpen() != null && msgStateDto.getMiniOpen() > 0) {
            String miniTemplateId = shopMessage.getMiniTemplateId();
            if (StrUtil.isEmpty(miniTemplateId)) {
                throw new ServiceException("请上传审核小程序之后再开启");
            }
        }
        PlatformShopMessage platformShopMessage = BeanUtil.toBean(msgStateDto, PlatformShopMessage.class);
        this.updateById(platformShopMessage);
    }

    /**
     * 发送订阅消息
     * @param shopId     店铺id
     * @param openId     openId
     * @param templateId 订阅消息模板id
     * @param toPath     跳转页面
     * @param data       发送数据
     */
    @SneakyThrows
    private void sendSubscribeMsg(String shopId, String openId, String templateId, String toPath, List<WxMaSubscribeMessage.MsgData> data, String miniprogramState) {
        WxMaService wxMaService = wechatMiniAppService.wxMaservice(shopId);
        WxMaMsgService msgService = wxMaService.getMsgService();
        WxMaSubscribeMessage.WxMaSubscribeMessageBuilder builder = WxMaSubscribeMessage.builder();
        builder.toUser(openId);
        builder.templateId(templateId);
        builder.page(toPath);
        builder.miniprogramState(miniprogramState);
        builder.data(data);
        WxMaSubscribeMessage build = builder.build();
        msgService.sendSubscribeMsg(build);
    }

    /**
     * 小程序发货信息录入
     *
     * @param vo               订单信息
     * @param orderDeliveryDto 发货单信息
     * @return Result<Boolean>
     */
    @Override
    public Result<Boolean> uploadShippingInfo(OrderVo vo, OrderDeliveryDto orderDeliveryDto) {
        try {
            if(platformProperties.getWechat().isOpenShipping()){
                log.error("当前小程序未开启发货管理功能");
                return Result.failed("当前小程序未开启发货管理功能!");
            }
            WxMaService wxMaService = wechatMiniAppService.wxMaservice(ISystem.shopId());
            WxMaOrderShippingInfoUploadRequest request = new WxMaOrderShippingInfoUploadRequest();
            // 构建订单关键信息
            OrderKeyBean orderKey = new OrderKeyBean();
            // 订单号类型（2-微信支付交易单号）
            orderKey.setOrderNumberType(CommonConstants.NUMBER_TWO);
            orderKey.setTransactionId(vo.getTransactionId());
            // 构建物流信息
            ShippingListBean shipping = new ShippingListBean();
            // 物流单号
            shipping.setTrackingNo(orderDeliveryDto.getDeliverySn());
            // 物流公司
            shipping.setExpressCompany(orderDeliveryDto.getDeliveryCompany());
            // 商品描述
            shipping.setItemDesc(vo.getOrderItemList().stream()
                    .map(OrderItem::getProductName)
                    .collect(Collectors.joining(",")));
            // 构建请求主体
            request.setOrderKey(orderKey);
            // 发货模式（1-普通发货）
            request.setDeliveryMode(CommonConstants.NUMBER_ONE);
            // 物流类型（4-快递）
            request.setLogisticsType(CommonConstants.NUMBER_FOUR);
            request.setShippingList(Collections.singletonList(shipping));
            request.setUploadTime(LocalDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            // 调用接口
            wxMaService.getWxMaOrderShippingService().upload(request);
            log.info("微信发货信息录入成功，订单ID: {}", vo.getId());
        } catch (WxErrorException e) {
            log.error("微信发货信息录入失败，订单ID: {}, 错误码: {}, 错误信息: {}",
                    vo.getId(), e.getError().getErrorCode(), e.getError().getErrorMsg());
            throw new ServiceException(STR."微信发货信息录入失败: \{e.getError().getErrorMsg()}");
        }
        return Result.ok(true);
    }


}
