package com.medusa.gruul.account.api.feign;

import com.medusa.gruul.account.api.model.*;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author whh
 * @date 2023/11/06
 */
@FeignClient(value = "account")
public interface RemoteMiniAccountService {


    /**
     * 修改用户扩展字段部分数据
     *
     * @param userId                      用户id
     * @param miniAccountExtendsUpdateDto com.medusa.gruul.account.api.model.MiniAccountExtendsUpdateDto
     * @return java.lang.Boolean  修改成功->true   失败->false
     */
    @RequestMapping(value = "/portion/attribute/modify/{userId}", method = RequestMethod.POST)
    @Operation(summary = "修改用户扩展字段部分数据")
    Boolean portionAttributeModify(@PathVariable(value = "userId", required = true) @NotNull(message = "用户id不能为null") String userId,
                                   @RequestBody @NotNull(message = "修改数据不能为null") MiniAccountExtendsUpdateDto miniAccountExtendsUpdateDto);

    /**
     * 获取用户信息接口
     *
     * @param userId 用户id
     * @param infos  [1,2,3,4]  1,基本信息,2,扩展信息,3-地址信息,4-授权信息  需要哪些发哪些 list
     * @return com.medusa.gruul.account.api.model.AccountInfoDto
     */
    @RequestMapping(value = "/account/info/{userId}", method = RequestMethod.GET)
    @Operation(summary = "获取用户信息,注意判空")
    AccountInfoDto accountInfo(@PathVariable(value = "userId", required = true) @NotNull(message = "用户id不能为null") String userId,
                               @RequestParam(value = "infos", required = true) @NotNull(message = "获取数据数组不能为空") List<Integer> infos);

    /**
     * 批量获取指定用户id(用户店铺id)的用户基本信息
     *
     * @param shopUserId 用户id数组
     * @return com.medusa.gruul.account.api.model.AccountInfoDto
     */
    @RequestMapping(value = "/accounts/info", method = RequestMethod.GET)
    @Operation(summary = "批量获取指定用户id(用户店铺id)的用户基本信息")
    List<MiniAccountExtDto> accountsInfoList(@RequestParam(value = "shopUserId") @NotNull(message = "用户id不能为null") List<String> shopUserId);

    /**
     * 积分确认是否足够或直接确认减少接口
     *
     * @param userId   用户id
     * @param value    要减少的积分值
     * @param isDeduct 是否直接减少 true 直接减少  false不操作积分,只确认积分是否足够
     * @param shopId  店铺id
     * @return 情况1:  isDeduct == true && 减少积分足够 return true   直接扣减并且积分足够
     * 情况2:  isDeduct == false && 减少积分足够 return true  不扣减仅确认积分是否足够
     */
    @RequestMapping(value = "/integral/subtract/affirm/{userId}", method = RequestMethod.GET)
    @Operation(summary = "获取用户信息,注意判空")
    Boolean integralSubtractAffirm(@PathVariable(value = "userId") @NotNull(message = "用户id不能为null") String userId,
                                   @RequestParam(value = "value") @NotNull(message = "积分值不能为空") BigDecimal value,
                                   @RequestParam(value = "isDeduct") @NotNull(message = "是否直接扣除字段不能为null") Boolean isDeduct,
                                   @RequestParam(value = "shopId") @NotNull(message = "店铺id不能为null") String shopId);

    /**
     * 根据店铺id和用户id获取会员信息
     *
     * @param userId 用户id
     * @return com.medusa.gruul.account.api.model.MemberCardInfoCenterDto
     */
    @RequestMapping(value = "/remote/getMemberInfo", method = RequestMethod.GET)
    @Operation(summary = "用户id不能为null")
    MemberCardInfoCenterDto getMemberInfo(
            @RequestParam(value = "userId") @NotNull(message = "用户id不能为null") String userId);

    /**
     * 获取店铺id下最高等级的会员等级信息
     *
     * @return com.medusa.gruul.account.api.model.MemberCardInfoCenterDto
     */
    @RequestMapping(value = "/remote/getTopMemberCardInfo", method = RequestMethod.GET)
    @Operation(summary = "店铺id不能为null")
    MemberTopCardInfoDto getTopMemberCardInfo();

    /**
     * 扣除余额且插入余额明细表
     *
     * @param memberBalanceUpdateDto com.medusa.gruul.account.api.model.MemberBalanceUpdateDto
     * @return java.lang.Boolean  修改成功->true   失败->false
     */
    @RequestMapping(value = "/remote/memberBalanceModify", method = RequestMethod.POST)
    @Operation(summary = "扣除余额且插入余额明细表")
    Boolean memberBalanceModify(@RequestBody @NotNull(message = "修改数据不能为null") MemberBalanceUpdateDto memberBalanceUpdateDto);

    /**
     * 会员模块是否开启
     *
     * @param shopId 店铺id
     * @return java.lang.Integer  开关状态：0-关 1-开
     */
    @RequestMapping(value = "/remote/memberSwitch", method = RequestMethod.GET)
    @Operation(summary = "会员模块是否开启")
    Integer memberSwitch(@RequestParam String shopId);

    /**
     * 公众号授权登录,返回登录token
     *
     * @param wxMpUserDto com.medusa.gruul.platform.api.model.dto.WxMpUserDto
     * @return java.lang.String
     */
    @RequestMapping(value = "/mp/login", method = RequestMethod.POST)
    @Operation(summary = "公众号授权登录")
    Result mpLogin(WxMpUserDto wxMpUserDto);

    /**
     * 根据店铺id和用户id获取对应的分销员信息
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @return com.medusa.gruul.account.api.model.UserDistributionVo
     */
    @RequestMapping(value = "/remote/getDistributionInfo", method = RequestMethod.GET)
    @Operation(summary = "用户id不能为null")
    UserOrderDistributionVo getDistributionInfo(@RequestParam(value = "shopId") @NotNull(message = "店铺id不能为null") String shopId,
                                                @RequestParam(value = "userId") @NotNull(message = "用户id不能为null") String userId);


    /**
     * 更新用户拓展信息，如果不存在 则初始化用户扩展信息
     *
     * @param extData 用户扩展信息
     * @return void
     */
    @RequestMapping(value = "/remote/account/ext/init", method = RequestMethod.POST)
    Result<Void> initAccountExtends(@RequestBody AccountExtendsDto extData);
}
