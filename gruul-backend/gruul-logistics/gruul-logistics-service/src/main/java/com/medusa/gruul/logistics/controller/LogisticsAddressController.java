package com.medusa.gruul.logistics.controller;


import com.kuaidi100.sdk.response.QueryTrackData;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsAddressDto;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsBatchDeliverDto;
import com.medusa.gruul.logistics.model.dto.manager.LogisticsPrintDeliverDto;
import com.medusa.gruul.logistics.model.param.LogisticsAddressParam;
import com.medusa.gruul.logistics.model.vo.LogisticsAddressVo;
import com.medusa.gruul.logistics.service.ILogisticsAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 收发货地址管理控制层
 *
 * @author lcysike
 */
@RestController
public class LogisticsAddressController {
    @Autowired
    private ILogisticsAddressService logisticsAddressService;

    /**
     * 物流-查看地址列表
     *
     * @return Result<PageUtils<LogisticsAddressVo>>
     */
    @GetMapping(value = "/get/address/list")
    @Operation(summary = "物流-查看地址列表")
    public Result<PageUtils<LogisticsAddressVo>> getAddressList(LogisticsAddressParam logisticsAddressParam) {
        PageUtils<LogisticsAddressVo> pageUtils = new PageUtils<>(logisticsAddressService.getAddressList(logisticsAddressParam));
        return Result.ok(pageUtils);
    }

    /**
     * 物流-查看所有地址
     *
     * @return Result<List<LogisticsAddressVo>>
     */
    @GetMapping(value = "/get/all/address")
    @Operation(summary = "物流-查看所有地址")
    public Result<List<LogisticsAddressVo>> getAllAddressList() {
        List<LogisticsAddressVo> logisticsAddressVos = logisticsAddressService.getAllAddressList();
        return Result.ok(logisticsAddressVos);
    }


    /**
     * 新增/修改 地址
     *
     * @return Result<Void>
     */
    @PostMapping(value = "/set/address")
    @Operation(summary = "新增/修改地址")
    public Result<Void> setAddress(@RequestBody LogisticsAddressDto logisticsAddressDto) {
        this.logisticsAddressService.setAddress(logisticsAddressDto);
        return Result.ok();
    }

    /**
     * 设置默认发货/退货地址
     *
     * @param type 1-发货地址 2-收货地址
     * @param id 收发货地址id
     * @return Result<Void>
     */
    @PutMapping(value = "/set/def/address/{id}/{type}")
    @Operation(summary = "设置默认发货/退货地址")
    public Result<Void> setDefAddress(@Parameter(description = "收发货地址id", required = true) @PathVariable("id") Long id,
                                @Parameter(description = "收发货类型 1-发货地址 2-收货地址", required = true) @PathVariable("type") Integer type) {
        logisticsAddressService.setDefAddress(type, id);
        return Result.ok();
    }


    /**
     * 删除地址
     *
     * @param id 收发货地址id
     * @return Result<Void>
     */
    @DeleteMapping(value = "/del/address/{id}")
    @Operation(summary = "删除地址")
    public Result<Void> delAddress(@Parameter(description = "收发货地址id", required = true) @PathVariable("id") Long id) {
        logisticsAddressService.delAddress(id);
        return Result.ok();
    }

    /**
     * 物流-获取默认发/收货地址
     *
     * @param type 1-发货地址 2-收货地址
     * @return LogisticsAddressVo
     */
    @GetMapping(value = "/get/default/address/{type}")
    @Operation(summary = "物流-获取默认发/收货地址")
    public LogisticsAddressVo getDefaultAddress(@Parameter(description = "收发货类型 1-发货地址 2-收货地址", required = true) @PathVariable("type") Integer type) {
        LogisticsAddressVo logisticsAddressVo = logisticsAddressService.getDefaultAddress(type);
        return logisticsAddressVo;
    }

    /**
     * 发货页面
     *
     * @return Result<Map<String, Object>>
     */
    @GetMapping(value = "/list/company")
    @Operation(summary = "发货页面")
    public Result<Map<String, Object>> listLogisticsCompany() {
        String shopId = ISystem.shopId();

        Map<String, Object> res = logisticsAddressService.listLogisticsCompany(shopId);
        return Result.ok(res);
    }


    /**
     * 设为默认发货物流公司
     *
     * @return
     */
    @GetMapping(value = "/set/default")
    @Operation(summary = "设为默认发货物流公司")
    public Result<Void> setCompanyDefault(Long logisticsCompanyId) {
        logisticsAddressService.setCompanyDefault(logisticsCompanyId);
        return Result.ok();
    }

    /**
     * 批量发货
     *
     * @return
     */
    @PostMapping(value = "/batch/deliver/goods")
    @Operation(summary = "批量发货")
    public Result<Void> doBatchDeliver(@RequestBody List<LogisticsBatchDeliverDto> logisticsBatchDeliverDtos) {
        String shopId = ISecurity.userMust().getShopId();
        logisticsAddressService.doBatchDeliver(logisticsBatchDeliverDtos, shopId);
        return Result.ok();
    }

    /**
     * 公共打印并发货
     *
     * @return
     */
    @PostMapping(value = "/normal/print/deliver")
    @Operation(summary = "公共打印并发货")
    @PreAuthorize("permitAll()")
    public Result<Void> normalDeliverOrder(@RequestBody @Valid LogisticsPrintDeliverDto logisticsPrintDeliverDto) {
        logisticsPrintDeliverDto.setShopId(ISecurity.userMust().getShopId());
        logisticsAddressService.normalPrintDeliverGoods(logisticsPrintDeliverDto);
        return Result.ok();
    }

    /**
     * 获取物流信息
     *
     * @return
     */
    @PostMapping(value = "/get/express/info")
    @Operation(summary = "获取物流信息")
    @PreAuthorize("permitAll()")
    public Result<List<QueryTrackData>> getExpressInfo(@RequestParam("waybillNo") String waybillNo, @RequestParam("shipperType") String shipperType, @RequestParam("mobile") String mobile) {
        return Result.ok(logisticsAddressService.getExpressInfo(waybillNo, shipperType, mobile));
    }
}

