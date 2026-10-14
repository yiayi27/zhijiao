package com.medusa.gruul.logistics.controller;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsTemplateDto;
import com.medusa.gruul.logistics.model.param.LogisticsTemplateParam;
import com.medusa.gruul.logistics.model.vo.LogisticsTemplateVo;
import com.medusa.gruul.logistics.service.ILogisticsTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 物流模版设置控制层
 *
 * @author lcysike
 */
@RestController
public class LogisticsTemplateController {
    @Autowired
    private ILogisticsTemplateService logisticsTemplateService;

    /**
     * 获取模板列表
     *
     * @param logisticsTemplateParam 模版查询参数
     * @return Result<PageUtils>
     */

    @GetMapping(value = "/get/template/list")
    @Operation(summary = "获取模板列表")
    public Result<PageUtils<LogisticsTemplateVo>> getTemplateList(LogisticsTemplateParam logisticsTemplateParam) {
        PageUtils<LogisticsTemplateVo> pageUtils = new PageUtils<>(logisticsTemplateService.getTemplateList(logisticsTemplateParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取单个模板详情
     *
     * @param id 模版id
     * @return Result<LogisticsTemplateVo>
     */

    @GetMapping(value = "/get/template/info/{id}")
    @Operation(summary = "获取单个模板详情")
    public Result<LogisticsTemplateVo> getTemplateInfo(@Parameter(description = "模版id", required = true) @PathVariable("id") Long id) {
        LogisticsTemplateVo logisticsTemplateVo = this.logisticsTemplateService.getTemplateInfo(id);
        return Result.ok(logisticsTemplateVo);
    }

    /**
     * 新增/修改单个模板模板
     *
     * @param logisticsTemplateDto 模版信息
     * @return Result<Void>
     */

    @PostMapping(value = "/set/template/info")
    @Operation(summary = "新增/修改单个模板信息")
    public Result<Void> setTemplateInfo(@RequestBody LogisticsTemplateDto logisticsTemplateDto) {
        this.logisticsTemplateService.addOrUpdateTemplate(logisticsTemplateDto);
        return Result.ok();
    }

    /**
     * 删除模板
     *
     * @param id 模版id
     * @return Result<Void>
     */
    @Operation(summary = "删除模板")
    @DeleteMapping(value = "/delete/template/info/{id}")
    public Result<Void> removeTemplateInfo(@Parameter(description = "模版id", required = true) @PathVariable("id") Long id) {
        logisticsTemplateService.removeTemplateById(id);
        return Result.ok();
    }
}
