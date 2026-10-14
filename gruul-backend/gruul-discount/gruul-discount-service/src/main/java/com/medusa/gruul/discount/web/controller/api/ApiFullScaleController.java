package com.medusa.gruul.discount.web.controller.api;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.vo.api.ApiFullScaleVo;
import com.medusa.gruul.discount.service.api.IApiFullScaleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 小程序满减活动 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@RestController
@RequestMapping("/api/full/scale")
public class ApiFullScaleController {

    @Autowired
    private IApiFullScaleService apiFullScaleService;

    /**
     * 满减活动信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "满减活动信息列表")
    public Result<List<ApiFullScaleVo>> getApiFullScaleList() {
        List<ApiFullScaleVo> apiFullScaleVos = apiFullScaleService.getApiFullScaleList();
        return Result.ok(apiFullScaleVos);
    }

}
