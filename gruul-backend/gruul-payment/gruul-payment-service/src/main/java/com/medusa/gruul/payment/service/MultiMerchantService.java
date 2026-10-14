package com.medusa.gruul.payment.service;

import com.github.binarywang.wxpay.service.WxPayService;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.model.MerchantConfig;
import com.medusa.gruul.payment.common.model.MerchantDetailsDTO;
import com.medusa.gruul.payment.common.model.MerchantDetailsVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 张治保
 * date 2024/12/18
 */
public interface MultiMerchantService {

    /**
     * 新增或修改商户配置
     *
     * @param param 配置详情
     */
    void merchant(MerchantDetailsDTO param);

    /**
     * 获取店铺商户支付配置列表
     *
     * @param shopId 店铺id
     * @return 店铺商户支付配置列表
     */
    List<MerchantDetailsVO> merchants(String shopId);

    /**
     * 上传微信支付证书
     *
     * @param multipartFile 证书文件
     * @return 证书路径
     */
    String uploadCertificate(MultipartFile multipartFile);


    /**
     * 根据 店铺id 和支付类型获取支付配置id
     *
     * @param shopId  店铺id
     * @param payType 支付类型
     * @return 支付配置id
     */
    String detailsId(String shopId, PayType payType);


    /**
     * 根据 店铺id 和支付类型获取支付配置
     *
     * @param shopId  店铺id
     * @param payType 支付类型
     * @return 支付配置
     */
    MerchantConfig config(String shopId, PayType payType);


    /**
     * 获取微信支付服务
     *
     * @param shopId 店铺id
     * @return WxPayService
     */
    WxPayService getWxPayService(String shopId);

}
