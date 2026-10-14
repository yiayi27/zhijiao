package com.medusa.gruul.logistics.controller;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsExpressDto;
import com.medusa.gruul.logistics.model.param.LogisticsExpressParam;
import com.medusa.gruul.logistics.model.vo.LogisticsExpressVo;
import com.medusa.gruul.logistics.service.ILogisticsExpressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 物流公司发货地址设置
 *
 * @author lcysike
 */
@RestController
public class LogisticsExpressController {

    @Autowired
    private ILogisticsExpressService logisticsExpressService;

    /**
     * 物流公司信息列表
     * @param logisticsExpressParam 物流公司信息查询参数
     * @return Result<List<LogisticsExpressVo>>
     */
    @GetMapping(value = "/get/express/list")
    @Operation(summary = "物流公司信息列表查询")
    public Result<List<LogisticsExpressVo>> getExpressList(LogisticsExpressParam logisticsExpressParam) {
        return Result.ok(logisticsExpressService.getExpressList(logisticsExpressParam));
    }

    /**
     * 查询单条物流公司信息
     * @param id 物流公司信息地址id
     * @return Result<LogisticsExpressVo>
     */
    @PostMapping(value = "/get/express/info/{id}")
    @Operation(summary = "查询单条物流公司信息")
    public Result<LogisticsExpressVo> getExpressInfo(@Parameter(description = "物流公司信息地址id", required = true) @PathVariable("id") Long id) {
        LogisticsExpressVo logisticsExpressVo = this.logisticsExpressService.getExpressInfo(id);
        return Result.ok(logisticsExpressVo);
    }


    /**
     * 新增/修改 物流公司信息
     * @param logisticsExpressDto 物流公司信息
     * @return Result<Void>
     */
    @PostMapping(value = "/set/express/info")
    @Operation(summary = "新增/修改物流公司信息")
    public Result<Void> setExpressInfo(@RequestBody LogisticsExpressDto logisticsExpressDto) {
        this.logisticsExpressService.setExpressInfo(logisticsExpressDto);
        return Result.ok();
    }


    /**
     * 删除物流公司信息
     *
     * @param id 物流公司信息id
     * @return Result<Void>
     */
    @DeleteMapping(value = "/del/express/{id}")
    @Operation(summary = "删除物流公司信息")
    public Result<Void> delExpressInfo(@Parameter(description = "物流公司信息地址id", required = true) @PathVariable("id") Long id) {
        logisticsExpressService.delExpressInfo(id);
        return Result.ok();
    }

}
