package com.medusa.gruul.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.AccountExtendsDto;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.enums.AuthCodeEnum;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Role;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.constant.MeConstant;
import com.medusa.gruul.platform.mapper.AccountInfoMapper;
import com.medusa.gruul.platform.model.dto.*;
import com.medusa.gruul.platform.model.vo.AccountInfoVo;
import com.medusa.gruul.platform.properties.PlatformProperties;
import com.medusa.gruul.platform.service.IAccountInfoService;
import com.medusa.gruul.platform.service.ISendCodeService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * <p>
 * 平台与店铺平台用户表 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-09-07
 */
@Service
@Log4j2
public class AccountInfoServiceImpl extends ServiceImpl<AccountInfoMapper, AccountInfo> implements IAccountInfoService {

    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;
    @Resource
    private ISendCodeService sendCodeService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private PlatformProperties platformProperties;

    /**
     * 校验是否存在该平台账号
     *
     * @param phone 手机号
     * @param type  type=1 校验账号存在  type=2校验账号不存在 默认使用1
     */
    @Override
    public void checkoutAccount(String phone, Integer type) {
        boolean exists = this.lambdaQuery()
                .eq(AccountInfo::getMobile, phone)
                .isNotNull(AccountInfo::getRole)
                .exists();
        //账号存在校验
        if (type.equals(CommonConstants.NUMBER_ONE)) {
            if (!exists) {
                throw new ServiceException("账号不存在", SystemCode.DATA_NOT_EXIST.getCode());
            }
            return;
        }
        //账号不存在校验
        if (type.equals(CommonConstants.NUMBER_TWO)) {
            if (exists) {
                throw new ServiceException("账号已存在");
            }
            return;
        }
        throw new ServiceException("非法操作");

    }

    /**
     * 根据请求token获取当前用户信息
     *
     * @return com.medusa.gruul.platform.model.vo.AccountInfoVo
     */
    @Override
    public AccountInfoVo info() {
        SecureUser<?> user = ISecurity.userMust();
        return new AccountInfoVo()
                .setNikeName(user.getNickname())
                .setAvatarUrl(user.getAvatar())
                .setPhone(user.getMobile());
    }

    /**
     * 账号手机换绑
     *
     * @param phoneChangeTieDto com.medusa.gruul.platform.model.dto.PhoneChangeTieDto
     */
    @Override
    @Redisson(name = MeConstant.ACCOUNT_LOCK_PREFIX, key = "@S.user.mobile")
    @Redisson(name = MeConstant.ACCOUNT_LOCK_PREFIX, key = "#phoneChangeTieDto.newPhone")
    public void phoneChangeTie(PhoneChangeTieDto phoneChangeTieDto) {
        SecureUser<?> secureUser = ISecurity.userMust();
        String shopId = secureUser.getShopId();
        String mobile = secureUser.getMobile();
        //校验原手机验证码是否正确
        sendCodeService.certificateCheck(phoneChangeTieDto.getOneCertificate(), mobile, AuthCodeEnum.ACCOUNT_PHONE_IN_TIE.getType());
        //校验新验证码是否正确
        sendCodeService.certificateCheck(phoneChangeTieDto.getTwoCertificate(), phoneChangeTieDto.getNewPhone(), AuthCodeEnum.ACCOUNT_PHONE_IN_TIE.getType());
        //校验新手机号是否已被使用
        boolean exists = this.lambdaQuery()
                .eq(AccountInfo::getMobile, phoneChangeTieDto.getNewPhone())
                .eq(AccountInfo::getShopId, shopId)
                .exists();
        if (exists) {
            throw new ServiceException("换绑手机账号已被使用");
        }
        //更新用户
        this.lambdaUpdate()
                .set(AccountInfo::getMobile, phoneChangeTieDto.getNewPhone())
                .eq(AccountInfo::getShopId, shopId)
                .eq(AccountInfo::getMobile, mobile)
                .update();
        //token 用户资料刷新
        ISecurity.refreshUsers(ISystem.clientType(), shopId, Set.of(secureUser.getId()));
    }

    /**
     * 账号修改密码
     *
     * @param passChangeTieDto com.medusa.gruul.platform.model.dto.PassChangeTieDto
     */
    @Override
    @Redisson(name = MeConstant.ACCOUNT_LOCK_PREFIX, key = "@S.user.mobile")
    public void passChangeTie(PassChangeTieDto passChangeTieDto) {
        SecureUser<?> secureUser = ISecurity.userMust();
        //校验验证码是否正确
        sendCodeService.certificateCheck(passChangeTieDto.getCertificate(), secureUser.getMobile(), AuthCodeEnum.ACCOUNT_PASSWORD_IN_TIE.getType());
        //更新新密码，更新密码无需刷新用户资料
        //同一账号的所有商户密码一致
        this.lambdaUpdate()
                .set(AccountInfo::getPassword, passwordEncoder.encode(passChangeTieDto.getPasswd()))
                .eq(AccountInfo::getMobile, secureUser.getMobile())
                .update();
    }

    /**
     * 忘记密码
     *
     * @param passwordRetrieveDto com.medusa.gruul.platform.model.dto.PasswordRetrieveDto
     */
    @Override
    @Redisson(name = MeConstant.ACCOUNT_LOCK_PREFIX, key = "@S.user.mobile")
    public void passwordRetrieve(PasswordRetrieveDto passwordRetrieveDto) {
        //校验验证码是否正确
        String mobile = passwordRetrieveDto.getPhone();
        sendCodeService.certificateCheck(passwordRetrieveDto.getCertificate(), mobile, AuthCodeEnum.ACCOUNT_PASSWORD_IN_TIE.getType());
        //
        if (!this.lambdaQuery()
                .eq(AccountInfo::getMobile, mobile)
                .exists()) {
            throw new ServiceException(SystemCode.PARAM_VALID_ERROR);
        }
        this.lambdaUpdate()
                .set(AccountInfo::getPassword, passwordEncoder.encode(passwordRetrieveDto.getPasswd()))
                .eq(AccountInfo::getMobile, mobile)
                .update();
    }

    /**
     * 修改用户电子发票邮箱接口
     *
     * @param emailChangeDto com.medusa.gruul.platform.model.dto.EmailChangeDto
     */
    @Override
    public void emailChange(EmailChangeDto emailChangeDto) {
        SecureUser<?> secureUser = ISecurity.userMust();
        this.lambdaUpdate()
                .set(AccountInfo::getEmail, emailChangeDto.getEmail())
                .eq(AccountInfo::getMobile, secureUser.getMobile())
                .eq(AccountInfo::getShopId, secureUser.getShopId())
                .update();
    }

    /**
     * 校验当前用户相关信息是否正确
     *
     * @param verifyDataDto com.medusa.gruul.platform.model.dto.VerifyDataDto
     * @return 正确返回true  不正确返回false
     */
    @Override
    public Boolean verifyData(VerifyDataDto verifyDataDto) {
        Boolean flag = Boolean.FALSE;
        if (CommonConstants.NUMBER_ONE.equals(verifyDataDto.getOption())) {
            flag = ISecurity.userMust().getMobile().equals(verifyDataDto.getPhone());
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveShopAccount(AccountInfo account) {
        account.setId(null)
                .setNickname(ISecurity.generateNickName())
                .setAvatar(this.platformProperties.getDefaultConsumerAvatar());
        if (Role.ADMIN != account.getRole()) {
            account.setEmail(null);
        }
        this.save(account);
        Result<Void> result = remoteMiniAccountService.initAccountExtends(
                new AccountExtendsDto()
                        .setShopId(account.getShopId())
                        .setUserId(account.getId())
                        .setMobile(account.getMobile())
                        .setNickname(account.getNickname())
                        .setAvatar(account.getAvatar())
        );
        if (!result.isSuccess()) {
            throw new ServiceException(result.getMsg(), result.getCode());
        }
    }

    /**
     * 更新用户昵称和头像
     *
     * @param param 更新参数
     */
    @Override
    public void updateAccountData(AccountDataUpdateDTO param) {
        SecureUser<?> user = ISecurity.userMust();
        String shopId = user.getShopId();
        this.lambdaUpdate()
                .set(AccountInfo::getNickname, param.getNickname())
                .set(AccountInfo::getAvatar, param.getAvatar())
                .eq(AccountInfo::getShopId, shopId)
                .eq(AccountInfo::getMobile, user.getMobile())
                .update();

        //更新用户资料
        remoteMiniAccountService.initAccountExtends(
                new AccountExtendsDto()
                        .setUserId(user.getId())
                        .setShopId(shopId)
                        .setNickname(param.getNickname())
                        .setAvatar(param.getAvatar())
        );
        //通知客户端刷新用户资料
        ISecurity.refreshUsers(ClientType.CONSUMER, shopId, Set.of(user.getId()));
    }
}
