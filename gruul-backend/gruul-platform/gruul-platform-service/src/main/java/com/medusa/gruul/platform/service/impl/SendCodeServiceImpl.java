package com.medusa.gruul.platform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.constant.RegexConstants;
import com.medusa.gruul.common.core.constant.TimeConstants;
import com.medusa.gruul.common.core.constant.enums.AuthCodeEnum;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.dto.SendCodeVerifyDto;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.platform.constant.PlatformError;
import com.medusa.gruul.platform.constant.RedisConstant;
import com.medusa.gruul.platform.constant.SmsConstant;
import com.medusa.gruul.platform.model.dto.SendCodeDto;
import com.medusa.gruul.platform.properties.PlatformProperties;
import com.medusa.gruul.platform.service.ISendCodeService;
import com.medusa.gruul.sms.api.dto.SmsTemplateSendDto;
import com.medusa.gruul.sms.api.feign.RemoteSmsSendService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-10-07
 */
@Service
public class SendCodeServiceImpl implements ISendCodeService {

    @Resource
    private RemoteSmsSendService remoteSmsSendService;

    @Resource
    private PlatformProperties platformProperties;

    /**
     * 发送验证码
     *
     * @param sendCodeDto dto
     * @return 返回值用于模拟登录
     */
    @Override
    public String sendCode(SendCodeDto sendCodeDto) {
        String phone = sendCodeDto.getPhone();
        if (!ReUtil.isMatch(RegexConstants.REGEX_MOBILE_EXACT, phone)) {
            throw new ServiceException("手机号错误", SystemCode.DATA_NOT_EXIST.getCode());
        }
        Integer type = sendCodeDto.getType();
        if (!AuthCodeEnum.isExistValue(type)) {
            throw new ServiceException("非法校验", SystemCode.FAILURE.getCode());
        }
        String code = getCacheCode(phone, type);
        if (StrUtil.isNotEmpty(code)) {
            //如果是模拟发送 则直接返回验证码
            if (platformProperties.isSmsSimulation()) {
                return code;
            }
            return null;
        }
        code = RandomUtil.randomNumbers(6);
        //如果是模拟发送 则直接返回验证码
        if (platformProperties.isSmsSimulation()) {
            setCacheCode(phone, code, type);
            return code;
        }
        //验证码有效期未过则重新发送
        SmsTemplateSendDto smsTemplateSendDto = new SmsTemplateSendDto();
        smsTemplateSendDto.setMobile(sendCodeDto.getPhone());
        smsTemplateSendDto.setTemplateParams(code);
        smsTemplateSendDto.setTemplateCode(SmsConstant.TEMPLATE_ID);
        Result<Void> order = remoteSmsSendService.sendSms(smsTemplateSendDto);
        if (order.getCode() != CommonConstants.SUCCESS) {
            throw new ServiceException("短信发送失败,".concat(order.getMsg()));
        }
        setCacheCode(phone, code, type);
        return null;
    }

    /**
     * 校验指定类型验证码是否正确
     *
     * @param sendCodeVerifyDto dto
     * @return boolean false 不正确 true正确
     */
    @Override
    public String verifyCode(SendCodeVerifyDto sendCodeVerifyDto) {
        String redisKey = RedisConstant.PHONE_KEY.concat(sendCodeVerifyDto.getType().toString()).concat(":").concat(sendCodeVerifyDto.getPhone());
        String code = RedisUtil.getCacheObject(getCodeCacheKey(sendCodeVerifyDto.getPhone(), sendCodeVerifyDto.getType()));
        if (StrUtil.isNotEmpty(code) && code.equals(sendCodeVerifyDto.getCode())) {
            String codeRedisKey = redisKey.concat(":").concat(code);
            String certificate = RedisUtil.getCacheObject(RedisUtil.key(Keys.PLATFORM, codeRedisKey));
            if (StrUtil.isNotEmpty(certificate)) {
                return certificate;
            }
            //生成有效凭证
            certificate = SecureUtil.md5(sendCodeVerifyDto.getPhone().concat(code).concat(String.valueOf(System.currentTimeMillis())));
            String certificateKey = RedisConstant.PHONE_CERTIFICATE_KEY.concat(sendCodeVerifyDto.getType().toString()).concat(":").concat(certificate);
            RedisUtil.setCacheObject(RedisUtil.key(Keys.PLATFORM, certificateKey), sendCodeVerifyDto.getPhone(), TimeConstants.FIVE_MINUTES, TimeUnit.MILLISECONDS);
            RedisUtil.setCacheObject(RedisUtil.key(Keys.PLATFORM, codeRedisKey), certificate, TimeConstants.FIVE_MINUTES, TimeUnit.MILLISECONDS);
            return certificate;
        }
        throw new ServiceException("验证码不正确");
    }

    private String getCodeCacheKey(String phone, Integer type) {
        return RedisUtil.key(
                Keys.PLATFORM,
                RedisConstant.PHONE_KEY,
                type.toString(),
                phone
        );
    }

    /**
     * 设置验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @param type  校验类型
     */
    private void setCacheCode(String phone, String code, Integer type) {
        RedisUtil.setCacheObject(
                getCodeCacheKey(phone, type),
                code,
                TimeConstants.FIVE_MINUTES,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 获取验证码
     *
     * @param phone 手机号
     * @param type  校验类型
     * @return 验证码
     */
    private String getCacheCode(String phone, Integer type) {
        return RedisUtil.getCacheObject(
                getCodeCacheKey(phone, type)
        );
    }

    /**
     * 短信校验通过凭证是否存在,并且凭证对应接收短信时的手机号
     *
     * @param certificateCheck 凭证
     * @param phone       手机号
     * @param type        校验类型
     */
    @Override
    public void certificateCheck(String certificateCheck, String phone, Integer type) {
        String phoneCheck = getCacheCode(phone, type);
        if (StrUtil.isEmpty(phoneCheck)) {
            throw PlatformError.SMS_CODE_EXPIRED.exception();
        }
        if (!phoneCheck.equals(certificateCheck)) {
            throw PlatformError.SMS_CODE_ERROR.exception();
        }
        RedisUtil.delete(RedisUtil.key(Keys.PLATFORM, phone));
    }

}
