package com.medusa.gruul.account.web.controller;


import com.medusa.gruul.account.api.entity.MemberPayRule;
import com.medusa.gruul.account.service.IMemberPayRuleService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author zhaokw
 * @Date 22:39 2024\6\12 0013
 **/
@RestController
@RequestMapping("/manager")
public class MemberPayRuleController {

    @Autowired
    private IMemberPayRuleService memberPayRuleService;

    /**
     * pc端获取会员储值规则信息
     *
     * @return Result<List<MemberPayRule>>
     */
    @GetMapping("/getMemberPayRule")
    @Operation(summary = "pc端获取会员储值规则信息")
    public Result<List<MemberPayRule>> getMemberPayRule() {
        List<MemberPayRule> memberPayRules = memberPayRuleService.getMemberPayRule();
        return Result.ok(memberPayRules);
    }

    /**
     * 后台保存会员储值规则
     * @param memberPayRuleList 会员储值规则信息
     * @return Result<Void>
     */
    @PostMapping("/saveMemberPayRule")
    @Operation(summary = "后台保存储值规则")
    public Result<Void> saveMemberPayRule(@RequestBody List<@Valid MemberPayRule> memberPayRuleList) {
        memberPayRuleService.saveMemberPayRule(memberPayRuleList);
        return Result.ok();
    }


}
