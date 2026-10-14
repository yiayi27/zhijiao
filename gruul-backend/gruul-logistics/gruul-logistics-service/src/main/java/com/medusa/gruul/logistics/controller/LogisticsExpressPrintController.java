package com.medusa.gruul.logistics.controller;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsExpressPrintDto;
import com.medusa.gruul.logistics.model.param.LogisticsExpressPrintParam;
import com.medusa.gruul.logistics.model.vo.LogisticsExpressPrintVo;
import com.medusa.gruul.logistics.service.ILogisticsExpressPrintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 电子面单打印设置
 *
 * @author lcysike
 */
@RestController
public class LogisticsExpressPrintController {

    @Autowired
    private ILogisticsExpressPrintService logisticsExpressPrintService;

    /**
     * 电子面单打印设置列表查询
     *
     * @param logisticsExpressPrintParam 电子面单打印设置查询参数
     * @return Result<PageUtils>
     */
    @GetMapping(value = "/get/express/print/list")
    @Operation(summary = "电子面单打印设置列表查询")
    public Result<PageUtils<LogisticsExpressPrintVo>> getExpressPrintList(LogisticsExpressPrintParam logisticsExpressPrintParam) {
        PageUtils<LogisticsExpressPrintVo> pageUtils = new PageUtils<>(logisticsExpressPrintService.getExpressPrintList(logisticsExpressPrintParam));
        return Result.ok(pageUtils);
    }

    /**
     * 查询单条电子面单打印机信息
     *
     * @param id 电子面单打印机信息id
     * @return Result<LogisticsExpressPrintVo>
     */
    @PostMapping(value = "/get/express/print/info/{id}")
    @Operation(summary = "查询单条电子面单打印机信息")
    public Result<LogisticsExpressPrintVo> getExpressPrintInfo(@Parameter(description = "打印机id", required = true) @PathVariable("id") Long id) {
        LogisticsExpressPrintVo logisticsExpressPrintVo = this.logisticsExpressPrintService.getExpressPrintInfo(id);
        return Result.ok(logisticsExpressPrintVo);
    }


    /**
     * 新增/修改 电子面单打印设置
     *
     * @param logisticsExpressPrintDto 电子面单打印设置
     * @return Result<Void>
     */
    @PostMapping(value = "/set/express/print/info")
    @Operation(summary = "新增/修改电子面单打印设置")
    public Result<Void> setExpressPrintInfo(@RequestBody LogisticsExpressPrintDto logisticsExpressPrintDto) {
        this.logisticsExpressPrintService.setExpressPrintInfo(logisticsExpressPrintDto);
        return Result.ok();
    }

    /**
     * 启用/停用电子面单打印设置
     *
     * @param type 0-停用 1-启用
     * @param id 打印机id
     * @return Result<Void>
     */
    @PutMapping(value = "/set/express/print/status/{id}/{type}")
    @Operation(summary = "设置启用/停用打印机")
    public Result<Void> setExpressPrintStatus(@Parameter(description = "打印机id", required = true) @PathVariable("id") Long id,
                                        @Parameter(description = "收发货类型 0-停用 1-启用", required = true) @PathVariable("type") Integer type) {
        this.logisticsExpressPrintService.setExpressPrintStatus(type, id);
        return Result.ok();
    }


    /**
     * 删除电子面单打印设置
     *
     * @param id 打印机id
     * @return Result<Void>
     */
    @DeleteMapping(value = "/del/express/print/{id}")
    @Operation(summary = "删除电子面单打印机设置")
    public Result<Void> delExpressPrintInfo(@Parameter(description = "打印机id", required = true) @PathVariable("id") Long id) {
        this.logisticsExpressPrintService.delExpressPrintInfo(id);
        return Result.ok();
    }

}
