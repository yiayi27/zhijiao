package com.medusa.gruul.payment.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceApacheHttpImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.common.core.util.SpringUtils;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.MerchantHandler;
import com.medusa.gruul.payment.common.constant.PayConst;
import com.medusa.gruul.payment.common.helper.PayHelper;
import com.medusa.gruul.payment.common.helper.WechatHelper;
import com.medusa.gruul.payment.common.model.MerchantConfig;
import com.medusa.gruul.payment.common.model.MerchantDetailsDTO;
import com.medusa.gruul.payment.common.model.MerchantDetailsVO;
import com.medusa.gruul.payment.service.MultiMerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 张治保
 * date 2024/12/18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiMerchantServiceImpl implements MultiMerchantService {

    /**
     * 新增或修改商户配置
     *
     * @param param 配置详情
     */
    @Override
    public void merchant(MerchantDetailsDTO param) {
        Handler<Void> handler = SpringUtils.getBean(MerchantHandler.class, param.getPayType());
        if (handler == null) {
            throw new ServiceException("服务器内部错误,不支持的处理器");
        }
        handler.handle(param);
    }

    /**
     * 在支付配置列表查询时生成的默认配置
     *
     * @return 默认配置列表
     */

    private MerchantDetailsVO defaultConfig(String shopId, PayType payType) {
        return new MerchantDetailsVO()
                .setDetailsId(IdUtil.fastSimpleUUID())
                .setPayType(payType)
                .setAppid(PayType.WECHAT == payType ? WechatHelper.getAppid(shopId) : null);
    }

    /**
     * 获取店铺商户支付配置列表
     *
     * @param shopId 店铺id
     * @return 店铺商户支付配置列表
     */
    @Override
    public List<MerchantDetailsVO> merchants(String shopId) {
        Map<PayType, String> typeDetailsIdMap = RedisUtil.getCacheMap(
                RedisUtil.key(PayConst.PAY_TYPE_CONFIG_CACHE_KEY, shopId),
                new TypeReference<>() {
                }
        );
        //若没有配置,则返回默认配置
        if (CollUtil.isEmpty(typeDetailsIdMap)) {
            return List.of(
                    defaultConfig(shopId, PayType.WECHAT),
                    defaultConfig(shopId, PayType.ALIPAY)
            );
        }
        //获取商户配置详情
        Map<String, MerchantConfig> configMap = RedisUtil.getMultiCacheMapValue(
                        PayConst.PAY_CONFIG_CACHE_KEY,
                        typeDetailsIdMap.values(),
                        MerchantConfig.class
                ).stream()
                //根据配置详情id转成对应的map
                .collect(Collectors.toMap(MerchantConfig::getDetailsId, Function.identity()));
        return Stream.of(PayType.WECHAT, PayType.ALIPAY)
                .map(payType -> {
                    String detailsId = typeDetailsIdMap.get(payType);
                    //如果没有配置详情id,则返回默认配置
                    if (StrUtil.isEmpty(detailsId)) {
                        return defaultConfig(shopId, payType);
                    }
                    MerchantConfig config = configMap.get(detailsId);
                    //如果没有配置详情,则返回默认配置
                    if (config == null) {
                        return defaultConfig(shopId, payType);
                    }
                    return new MerchantDetailsVO()
                            .setDetailsId(config.getDetailsId())
                            .setPayType(config.getPayType())
                            .setAppid(config.getAppid())
                            .setKeyPrivate(config.getKeyPrivate())
                            .setKeyPublic(config.getKeyPublic())
                            .setMchId(config.getMchId())
                            .setKeyCert(config.getKeyCert());
                })
                .toList();
    }

    /**
     * 上传微信支付证书
     *
     * @param multipartFile 证书文件
     * @return 证书路径
     */
    @Override
    public String uploadCertificate(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new ServiceException("上传的文件内容为空");
        }
        String originalFilename = multipartFile.getOriginalFilename();

        String extName = FileUtil.getSuffix(originalFilename);
        if (!"p12".equals(extName) && !"crt".equals(extName)) {
            throw new ServiceException("文件格式不正确,请上传.p12或.crt证书");
        }

        String absolutePath = PayHelper.absolutePath(ISecurity.userMust().getShopId(), extName);
        log.info("证书文件路径:{}", absolutePath);
        try {
            multipartFile.transferTo(new File(absolutePath));
        } catch (IOException exception) {
            log.error("保存文件发生错误", exception);
            throw new ServiceException("保存文件时发生错误");
        }
        return absolutePath;
    }

    @Override
    public String detailsId(String shopId, PayType payType) {
        String detailsId = RedisUtil.getCacheMapValue(
                RedisUtil.key(PayConst.PAY_TYPE_CONFIG_CACHE_KEY, shopId),
                payType.name()
        );
        if (StrUtil.isEmpty(detailsId)) {
            throw new ServiceException(STR."\{payType}：商户配置不存在");
        }
        return detailsId;
    }

    @Override
    public MerchantConfig config(String shopId, PayType payType) {
        String detailsId = detailsId(shopId, payType);
        MerchantConfig config = RedisUtil.getCacheMapValue(
                PayConst.PAY_CONFIG_CACHE_KEY,
                detailsId,
                MerchantConfig.class);
        if (config == null) {
            throw new ServiceException(STR."\{payType}：商户配置不存在");
        }
        return config;
    }


    /**
     * 组装支付配置
     */
    @Override
    public WxPayService getWxPayService(String shopId) {
        MerchantConfig config = config(shopId, PayType.WECHAT);
        WxPayService wxPayService = new WxPayServiceApacheHttpImpl();
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(config.getAppid());
        wxPayConfig.setMchId(config.getMchId());
        // 新增APIv3密钥配置
        wxPayConfig.setApiV3Key(config.getKeyPrivate());

        //v2
        wxPayConfig.setMchKey(config.getKeyPrivate());
        wxPayConfig.setKeyPath(config.getKeyCert());
        String keyPublic = config.getKeyPublic();
        if (StrUtil.isNotBlank(keyPublic)) {
            String[] splits = keyPublic.split(StrPool.COMMA);
            wxPayConfig.setPublicKeyId(splits[0]);
            wxPayConfig.setPublicKeyString(splits[1]);
        }
        wxPayConfig.setNotifyUrl(config.getNotifyUrl());
        wxPayConfig.setTradeType(WxPayConstants.TradeType.JSAPI);
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }

}
