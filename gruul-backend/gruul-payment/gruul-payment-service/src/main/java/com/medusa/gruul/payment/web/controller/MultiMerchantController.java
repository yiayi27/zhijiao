package com.medusa.gruul.payment.web.controller;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.log.annotation.SysLog;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.payment.common.model.MerchantDetailsDTO;
import com.medusa.gruul.payment.common.model.MerchantDetailsVO;
import com.medusa.gruul.payment.service.MultiMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 张治保
 * date 2024/12/18
 */
@RestController
@RequestMapping("/multi/merchant")
@RequiredArgsConstructor
@PreAuthorize("@S.hasPerm(@P.MALL_SETTING.COMMON_SETTING)")
public class MultiMerchantController {

    private final MultiMerchantService multiMerchantService;

    /**
     * 获取店铺商户支付配置列表
     *
     * @return 店铺商户支付配置列表
     */
    @SysLog("获取商户配置列表")
    @GetMapping
    public Result<List<MerchantDetailsVO>> details() {
        return Result.ok(
                multiMerchantService.merchants(ISystem.shopId())
        );
    }

    /**
     * 新增或修改商户配置
     *
     * @param param 配置详情
     */
    @SysLog("新增/修改商户配置")
    @PostMapping
    public Result<Void> merchant(@RequestBody @Validated MerchantDetailsDTO param) {
        param.validParam();
        multiMerchantService.merchant(param);
        return Result.ok();
    }

    /**
     * 上传微信支付证书
     *
     * @param file 证书文件
     * @return 证书路径
     */
    @SysLog("上传支付证书")
    @PostMapping("/wechat/upload/cert")
    public Result<String> uploadCertificate(@RequestParam("file") MultipartFile file) {
        return Result.ok(
                multiMerchantService.uploadCertificate(file)
        );
    }
}
