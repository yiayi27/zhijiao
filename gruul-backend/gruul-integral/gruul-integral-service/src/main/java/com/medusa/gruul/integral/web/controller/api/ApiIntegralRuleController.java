package com.medusa.gruul.integral.web.controller.api;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.service.IIntegralRuleService;
import com.medusa.gruul.integral.web.model.dto.manager.ApiIntegralRuleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @Description: 提供规则是否开启
 * @Author: xiaoq
 * @Date: 2023/12/21 16:12
 */
@RestController
@RequestMapping("/api/")
@Tag(name = "小程序端规则")
public class ApiIntegralRuleController {
    @Autowired
    private IIntegralRuleService integralRuleService;

    /**
     * 获取商家设置的积分规则信息 提供给小程序端进行显示使用
     *
     * @return 获取积分的规则是否开启
     */
    @GetMapping("get/integralRul")
    @Operation(summary = "获取商城详情")
    public Result<List<ApiIntegralRuleDto>> getIntegralRule() {
        List<ApiIntegralRuleDto> apiIntegralRule = integralRuleService.getApiIntegralRule();
        return Result.ok(apiIntegralRule);
    }
}
