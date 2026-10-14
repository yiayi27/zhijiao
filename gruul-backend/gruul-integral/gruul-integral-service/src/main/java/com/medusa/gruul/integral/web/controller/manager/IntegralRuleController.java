package com.medusa.gruul.integral.web.controller.manager;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.service.IIntegralRuleService;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralRuleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 积分规则表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("/rule")
@Tag(name = "积分规则接口")
public class IntegralRuleController {
    @Autowired
    private IIntegralRuleService integralRuleService;

    /**
     * 获取积分商城规则
     *
     * @return 积分商城规则
     */
    @Operation(summary = "获取积分规则")
    @PreAuthorize("permitAll()")
    @GetMapping("/get")
    public Result<List<IntegralRuleDto>> getIntegralRule() {
        List<IntegralRuleDto> integralShopRule = integralRuleService.getIntegralShopRule(ISystem.shopId());
        return Result.ok(integralShopRule);
    }


    /**
     * 修改积分商城规则
     *
     * @param integralRuleDto 积分商城规则
     * @return
     */
    @Operation(summary = "积分规则修改")
    @PutMapping("/update")
    public Result<Void> updateIntegralRule(@RequestBody List<IntegralRuleDto> integralRuleDto) {
        integralRuleService.updateIntegralRule(integralRuleDto);
        return Result.ok();
    }

    /**
     * 无用
     *
     * @param integralRuleDto
     * @return
     */
    @Operation(summary = "默认积分规则插入 --- 未对接")
    @PostMapping("saveDefault")
    public Result<Void> saveIntegralRule(@RequestBody List<IntegralRuleDto> integralRuleDto) {
        integralRuleService.saveIntegralRule(integralRuleDto);
        return Result.ok();
    }


}
