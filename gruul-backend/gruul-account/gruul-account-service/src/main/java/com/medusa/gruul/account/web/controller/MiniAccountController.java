package com.medusa.gruul.account.web.controller;


import com.medusa.gruul.account.model.dto.DecodePhoneInfo;
import com.medusa.gruul.account.model.dto.SetBlacklistDto;
import com.medusa.gruul.account.model.dto.UpdateUserBaseInfoDto;
import com.medusa.gruul.account.model.param.ApiUserDistributionParam;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.account.service.IMiniAccountRestrictService;
import com.medusa.gruul.account.service.IMiniAccountService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 小程序用户表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@RestController
@RequestMapping("/mini-account")
@Tag(name = "用户相关接口")
public class MiniAccountController {

    @Autowired
    private IMiniAccountService miniAccountService;
    @Autowired
    private IMiniAccountRestrictService miniAccountRestrictService;


    /**
     * 解密获取用户手机号
     *
     * @param decodePhoneInfo 手机号码解密信息
     * @return Result<String>
     */
    @PostMapping("/decode/phone/info")
    @Operation(summary = "解密获取用户手机号")
    public Result<String> decodePhoneInfo(@RequestBody @Validated DecodePhoneInfo decodePhoneInfo) {
        String phone = miniAccountService.decodePhoneInfo(decodePhoneInfo);
        return Result.ok(phone);
    }

    /**
     * 更新用户基础信息
     *
     * @param updateUserBaseInfoDto 用户基础信息
     * @return Result<String>
     */
    @PostMapping
    @Operation(summary = "更新用户基础信息")
    public Result<Void> updateUserBaseInfo(@RequestBody @Validated UpdateUserBaseInfoDto updateUserBaseInfoDto) {
        miniAccountService.updateUserBaseInfo(updateUserBaseInfoDto);
        return Result.ok();
    }

    /**
     * 根据token获取指定用户基本信息
     *
     * @param infoLevel 数据级别 1-基础信息 2-用户扩展数据 3-用户限制数据 未传默认未1
     * @return Result<UserInfoVo>
     */
    @GetMapping
    @Operation(summary = "根据token获取指定用户基本信息", description = "小程序接口")
    public Result<UserInfoVo> userInfo(@Parameter(description = "数据级别 1-基础信息 2-用户扩展数据 3-用户限制数据 未传默认未1", required = true) @RequestParam(value = "infoLevel", required = false, defaultValue = "1") Integer infoLevel) {
        UserInfoVo vo = miniAccountService.getUserInfo(infoLevel);
        return Result.ok(vo);
    }

    /**
     * 获取当前用户的分销二维码
     *
     * @return Result<String>
     */
    @GetMapping(value = "/qr_code")
    @Operation(summary = "获取当前用户二维码")
    public Result<String> qrCode() {
        String qrCode = miniAccountService.qrCode();
        return Result.ok(qrCode);
    }

    /**
     * 获取当前用户id
     *
     * @return Result<String>
     */
    @GetMapping(value = "/getUserId")
    @Operation(summary = "获取当前用户id")
    public Result<String> getUserId() {
        String qrCode = miniAccountService.getUserId();
        return Result.ok(qrCode);
    }

    /**
     * 获取当前用户的分销信息
     *
     * @param userId 用户id
     * @return Result<UserDistributionVo>
     */
    @PostMapping(value = "/set/distribution/info/{userId}")
    @Operation(summary = "绑定当前用户的分销信息")
    public Result<String> setUserDistributionInfo(@Parameter(description = "绑定用户id", required = true) @PathVariable(value = "userId") String userId) {
        return Result.ok(miniAccountService.setUserDistributionInfo(userId));
    }

    /**
     * 获取当前用户的分销信息
     *
     * @return Result<UserDistributionVo>
     */
    @GetMapping(value = "/get/user/distribution/index")
    @Operation(summary = "获取分销中心首页信息")
    public Result<UserDistributionVo> getUserDistributionIndex() {
        UserDistributionVo userDistributionVo = miniAccountService.getUserDistributionIndex();
        return Result.ok(userDistributionVo);
    }

    /**
     * 获取当前用户的分销明细信息
     *
     * @param apiUserDistributionParam 分销明细查询参数
     * @return Result<UserDistributionVo>
     */
    @GetMapping(value = "/get/user/distribution/detail")
    @Operation(summary = "获取分销中心明细信息")
    public Result<PageUtils<UserDistributionDetailVo>> getUserDistributionList(ApiUserDistributionParam apiUserDistributionParam) {
        PageUtils<UserDistributionDetailVo> pageUtils = new PageUtils<>(miniAccountService.getUserDistributionList(apiUserDistributionParam));
        return Result.ok(pageUtils);
    }

    /**
     * 重新调用获取指定用户基本信息
     *
     * @param shopId 店铺id
     * @return Result<String>
     */
    @PutMapping(value = "switch/shops/{shopId}")
    @Operation(summary = "切换店铺,调用之后会返回新的token,同时需重新调用获取指定用户基本信息", description = "店铺相关接口")
    public Result<String> switchShops(@Parameter(description = "店铺id", required = true) @PathVariable(value = "shopId") String shopId) {
        String token = miniAccountService.switchShops(shopId);
        return Result.ok(token);
    }

    /**
     * pc端获取用户列表
     *
     * @param page 指定页数
     * @param size 数据条数
     * @param nikeName 微信昵称
     * @param becomeMemberStartTime 成为会员开始时间
     * @param becomeMemberEndTime 成为会员结束时间
     * @param tagId 标签id
     * @param orderSuccessStartTime 成交开始时间
     * @param orderSuccessEndTime 成交结束时间
     * @param rankCode 会员等级代码
     * @param memberNumber 会员编号
     * @param sort 排序类型 1-交易总额降序 2-交易总额升序 3-会员时间降序 4-会员时间升序
     * @return Result<PageUtils<List<UserListVo>>>
     */
    @GetMapping("list")
    @Operation(summary = "pc端获取用户列表")
    public Result<PageUtils<List<UserListVo>>> userList(
            @Parameter(description = "指定页数", required = true) @RequestParam Integer page,
            @Parameter(description = "数据条数", required = true) @RequestParam Integer size,
            @Parameter(name = "nikeName", description = "微信昵称") @RequestParam(required = false) String nikeName,
            @Parameter(name = "becomeMemberStartTime", description = "成为会员开始时间 2023-11-11 11:23:23") @RequestParam(required = false) String becomeMemberStartTime,
            @Parameter(name = "becomeMemberEndTime", description = "成为会员结束时间 2023-11-11 11:23:23") @RequestParam(required = false) String becomeMemberEndTime,
            @Parameter(name = "tagId", description = "标签id") @RequestParam(required = false) Long tagId,
            @Parameter(name = "orderSuccessStartTime", description = "成交开始时间 2023-11-11 11:23:23") @RequestParam(required = false) String orderSuccessStartTime,
            @Parameter(name = "orderSuccessEndTime", description = "成交结束时间 2023-11-11 11:23:23") @RequestParam(required = false) String orderSuccessEndTime,
            @Parameter(name = "rankCode", description = "会员等级代码") @RequestParam(required = false) String rankCode,
            @Parameter(name = "memberNumber", description = "会员编号") @RequestParam(required = false) String memberNumber,
            @Parameter(name = "sort", description = "排序类型 1-交易总额降序 2-交易总额升序 3-会员时间降序 4-会员时间升序") @RequestParam(required = false) Integer sort
    ) {
        PageUtils<List<UserListVo>> voPageUtils = miniAccountService.userList(nikeName, becomeMemberStartTime, becomeMemberEndTime,
                tagId, orderSuccessStartTime, orderSuccessEndTime, rankCode, memberNumber, page, size, sort);
        return Result.ok(voPageUtils);
    }

    /**
     * 获取黑名单用户列表
     *
     * @param page 指定页数
     * @param size 数据条数
     * @param fuzzy 手机号或微信昵称模糊搜索
     * @param permission 权限 1-下单 2-评论
     * @return Result<PageUtils<List<BlacklistUserVo>>>
     */
    @GetMapping("/blacklist")
    @Operation(summary = "获取黑名单用户列表")
    public Result<PageUtils<List<BlacklistUserVo>>> blacklist(
            @Parameter(description = "指定页数", required = true) @RequestParam Integer page,
            @Parameter(description = "数据条数", required = true) @RequestParam Integer size,
            @Parameter(description = "权限 1-下单 2-评论") @RequestParam(value = "permission", required = false) Integer permission,
            @Parameter(description = "手机号或微信昵称模糊搜索") @RequestParam(value = "fuzzy", required = false) String fuzzy) {
        PageUtils<List<BlacklistUserVo>> pageUtils = miniAccountService.blacklist(page, size, permission, fuzzy);
        return Result.ok(pageUtils);
    }

    /**
     * 批量设置黑名单用户
     *
     * @param dto 黑名单用户信息
     * @return Result<Void>
     */
    @PutMapping("/set/blacklist")
    @Operation(summary = "批量设置黑名单用户")
    public Result<Void> setBlacklist(@RequestBody SetBlacklistDto dto) {
        miniAccountRestrictService.setBlacklist(dto);
        return Result.ok();
    }

}
