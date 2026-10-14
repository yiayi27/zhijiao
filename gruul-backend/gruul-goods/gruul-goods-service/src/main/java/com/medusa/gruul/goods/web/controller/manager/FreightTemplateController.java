package com.medusa.gruul.goods.web.controller.manager;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.service.manager.IFreightTemplateService;
import com.medusa.gruul.logistics.model.vo.LogisticsTemplateVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 运费模版 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2023-10-01
 */
@RestController
@RequestMapping("/manager/freight/template")
public class FreightTemplateController {

    @Autowired
    private IFreightTemplateService freightTemplateService;

    /**
     * 物流接口获取所有运费模版
     */
    @GetMapping("/list/by/logistics")
    @Operation(summary = "物流接口获取所有运费模版")
    public Result<List<LogisticsTemplateVo>> findFreightTemplateListByLogistics() {
        return freightTemplateService.findFreightTemplateListByLogistics();
    }

    /**
     * 物流接口获取单个产品运费模版信息
     */
    @GetMapping("/info/by/logistics/{freightTemplateId}")
    @Operation(summary = "物流接口获取单个产品运费模版信息")
    public Result<LogisticsTemplateVo> findFreightTemplateInfoByLogistics(@Parameter(description = "运费模版id", required = true) @PathVariable("freightTemplateId") Long freightTemplateId) {
        return freightTemplateService.findFreightTemplateInfoByLogistics(freightTemplateId);
    }

}
