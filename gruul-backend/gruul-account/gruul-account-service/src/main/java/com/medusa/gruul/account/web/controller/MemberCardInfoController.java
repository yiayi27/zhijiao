package com.medusa.gruul.account.web.controller;


import com.medusa.gruul.account.api.entity.MemberSwitch;
import com.medusa.gruul.account.model.dto.MemberCardInfoRankStatusDto;
import com.medusa.gruul.account.model.dto.MemberCardInfoStatusDto;
import com.medusa.gruul.account.model.vo.MemberCardInfoListVo;
import com.medusa.gruul.account.service.IMemberCardInfoService;

import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author zhaokw
 * @Date 22:39 2024\5\13 0013
 **/
@RestController
@RequestMapping("/manager")
public class MemberCardInfoController {

    @Autowired
    private IMemberCardInfoService memberCardInfoService;

    /**
     * 显示打开关闭会员功能
     *
     * @return Result<List<MemberSwitch>>
     */
    @GetMapping("/getMemberCardInfoSwitch")
    @Operation(summary = "显示打开关闭会员功能")
    public Result<List<MemberSwitch>> getMemberCardInfoSwitch() {
        List<MemberSwitch> memberCardInfoSwitchList = memberCardInfoService.getMemberCardInfoSwitch();
        return Result.ok(memberCardInfoSwitchList);
    }

    /**
     * 后台关闭打开会员卡功能
     *
     * @param memberCardInfoStatusDto 会员卡功能开关信息
     * @return Result<Void>
     */
    @PostMapping("/setMemberCardInfoStatus")
    @Operation(summary = "后台关闭打开会员卡功能")
    public Result<Void> setMemberCardInfoStatus(@RequestBody @Validated MemberCardInfoStatusDto memberCardInfoStatusDto) {
        memberCardInfoService.setMemberCardInfoStatus(memberCardInfoStatusDto);
        return Result.ok();
    }

    /**
     * 后台停用开启会员卡各个等级
     *
     * @param memberCardInfoRankStatusDtoList 会员卡等级信息
     * @return Result<Void>
     */
    @PostMapping("/updateMemberCardInfoStatus")
    @Operation(summary = "后台停用开启会员卡各个等级")
    public Result<Void> updateMemberCardInfoStatus(@RequestBody @Validated List<MemberCardInfoRankStatusDto> memberCardInfoRankStatusDtoList) {
        memberCardInfoService.updateMemberCardInfoStatus(memberCardInfoRankStatusDtoList);
        return Result.ok();
    }

    /**
     * pc端获取会员卡信息
     *
     * @return Result<MemberCardInfoListVo>
     */
    @GetMapping("/getMemberCardInfo")
    @Operation(summary = "pc端获取会员卡信息")
    public Result<MemberCardInfoListVo> getMemberCardInfo() {
        MemberCardInfoListVo memberCardInfo = memberCardInfoService.getMemberCardInfo();
        return Result.ok(memberCardInfo);
    }

    /**
     * 后台保存会员卡信息
     *
     * @param memberCardInfo 会员卡信息
     * @return Result<Void>
     */
    @PostMapping("/saveMemberCardInfo")
    @Operation(summary = "后台保存会员卡信息")
    public Result<Void> saveMemberCardInfo(@RequestBody @Validated MemberCardInfoListVo memberCardInfo) {
        memberCardInfoService.saveMemberCardInfo(memberCardInfo);
        return Result.ok();
    }

}
