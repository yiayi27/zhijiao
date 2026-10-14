package com.medusa.gruul.account.web.controller;


import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.model.dto.DeleteMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.InsertMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.MemberCardPrivilegeStatusDto;
import com.medusa.gruul.account.model.dto.UpdateMemberCardPrivilegeDto;
import com.medusa.gruul.account.service.IMemberCardPrivilegeService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 会员卡权益表 前端控制器
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-24
 */
@RestController
@RequestMapping("/manager")
public class MemberCardPrivilegeController {

    @Autowired
    private IMemberCardPrivilegeService memberCardPrivilegeService;

    /**
     * @Description pc端获取会员卡权益信息
     *
     * @return Result<List<MemberCardPrivilege>>
     **/
    @GetMapping("/getMemberCardPrivilege")
    @Operation(summary = "pc端获取会员卡权益信息")
    public Result<List<MemberCardPrivilege>> getMemberCardPrivilege() {
        List<MemberCardPrivilege> memberCardPrivileges = memberCardPrivilegeService.getMemberCardPrivilege();
        return Result.ok(memberCardPrivileges);
    }

    /**
     * 后台更新权益参数
     *
     * @param updateMemberCardPrivilegeDto 权益参数
     * @return Result<Void>
     */
    @PostMapping("/updateMemberCardPrivilege")
    @Operation(summary = "后台更新权益参数")
    public Result<Void> updateMemberCardPrivilege(@RequestBody @Validated UpdateMemberCardPrivilegeDto updateMemberCardPrivilegeDto) {
        memberCardPrivilegeService.updateMemberCardPrivilege(updateMemberCardPrivilegeDto);
        return Result.ok();
    }

    /**
     * 后台开启关闭会员卡权益
     *
     * @param memberCardPrivilegeStatusDto 权益状态信息
     * @return Result<Void>
     */
    @PostMapping("/updateMemberCardPrivilegeStatus")
    @Operation(summary = "后台开启关闭会员卡权益")
    public Result<Void> updateMemberCardPrivilegeStatus(@RequestBody @Validated MemberCardPrivilegeStatusDto memberCardPrivilegeStatusDto) {
        memberCardPrivilegeService.updateMemberCardPrivilegeStatus(memberCardPrivilegeStatusDto);
        return Result.ok();
    }

    /**
     * 后台删除自定义会员卡权益
     *
     * @param deleteMemberCardPrivilegeDto 删除权益信息
     * @return Result<Void>
     */
    @PostMapping("/deleteMemberCardPrivilege")
    @Operation(summary = "后台删除自定义会员卡权益")
    public Result<Void> deleteMemberCardPrivilege(@RequestBody @Validated DeleteMemberCardPrivilegeDto deleteMemberCardPrivilegeDto) {
        memberCardPrivilegeService.deleteMemberCardPrivilege(deleteMemberCardPrivilegeDto);
        return Result.ok();
    }

    /**
     * 后台插入自定义权益参数
     *
     * @param insertMemberCardPrivilegeDto 插入权益参数
     * @return Result<Void>
     */
    @PostMapping("/insertMemberCardPrivilege")
    @Operation(summary = "后台插入自定义权益参数")
    public Result<Void> insertMemberCardPrivilege(@RequestBody @Validated InsertMemberCardPrivilegeDto insertMemberCardPrivilegeDto) {
        memberCardPrivilegeService.insertMemberCardPrivilege(insertMemberCardPrivilegeDto);
        return Result.ok();
    }

}
