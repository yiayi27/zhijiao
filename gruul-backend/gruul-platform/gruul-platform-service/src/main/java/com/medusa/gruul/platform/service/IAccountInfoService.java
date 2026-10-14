package com.medusa.gruul.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.model.dto.*;
import com.medusa.gruul.platform.model.vo.AccountInfoVo;
import jakarta.validation.Valid;

/**
 * <p>
 * 平台与店铺平台用户表 服务类
 * </p>
 *
 * @author whh
 * @since 2023-09-07
 */
public interface IAccountInfoService extends IService<AccountInfo> {


    /**
     * 校验是否存在该平台账号
     *
     * @param phone 手机号
     * @param type  type=1 校验账号存在  type=2校验账号不存在 默认使用1
     */
    void checkoutAccount(String phone, Integer type);


    /**
     * 根据请求token获取当前用户信息
     *
     * @return com.medusa.gruul.platform.model.vo.AccountInfoVo
     */
    AccountInfoVo info();


    /**
     * 账号手机换绑
     *
     * @param phoneChangeTieDto com.medusa.gruul.platform.model.dto.PhoneChangeTieDto
     */
    void phoneChangeTie(PhoneChangeTieDto phoneChangeTieDto);

    /**
     * 账号修改密码
     *
     * @param passChangeTieDto com.medusa.gruul.platform.model.dto.PassChangeTieDto
     */
    void passChangeTie(PassChangeTieDto passChangeTieDto);

    /**
     * 忘记密码
     *
     * @param passwordRetrieveDto com.medusa.gruul.platform.model.dto.PasswordRetrieveDto
     */
    void passwordRetrieve(PasswordRetrieveDto passwordRetrieveDto);


    /**
     * 修改用户电子发票邮箱接口
     *
     * @param emailChangeDto com.medusa.gruul.platform.model.dto.EmailChangeDto
     */
    void emailChange(EmailChangeDto emailChangeDto);

    /**
     * 校验当前用户相关信息是否正确
     *
     * @param verifyDataDto com.medusa.gruul.platform.model.dto.VerifyDataDto
     * @return 正确返回true  不正确返回false
     */
    Boolean verifyData(VerifyDataDto verifyDataDto);

    /**
     * 保存店铺账户信息
     *
     * @param account 店铺账户
     */
    void saveShopAccount(AccountInfo account);


    /**
     * 更新用户昵称和头像
     *
     * @param param 更新参数
     */
    void updateAccountData(@Valid AccountDataUpdateDTO param);
}
