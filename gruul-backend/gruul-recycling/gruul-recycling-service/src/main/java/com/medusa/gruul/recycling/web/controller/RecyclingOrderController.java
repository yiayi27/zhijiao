package com.medusa.gruul.recycling.web.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.recycling.model.dto.RecyclingOrderNewDto;
import com.medusa.gruul.recycling.model.dto.RecyclingOrderSendDto;
import com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo;
import com.medusa.gruul.recycling.model.vo.RecyclingPcOrderListVo;
import com.medusa.gruul.recycling.service.IRecyclingOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 回收订单表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/recycling-order")
public class RecyclingOrderController {

    @Autowired
    private IRecyclingOrderService recyclingOrderService;

    /**
     * PC端商户获取相关订单
     *
     * @param page     页数
     * @param size     条数
     * @param nickName 昵称
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    @GetMapping("pc/list")
    @Operation(summary = "PC端商户获取相关订单", tags = "pc端-回收订单详情")
    public ResultPageList<RecyclingPcOrderListVo> recyclingPcOrderList(
            @Parameter(description = "页数") @RequestParam Integer page,
            @Parameter(description = "条数") @RequestParam Integer size,
            @Parameter(description = "昵称") @RequestParam(required = false) String nickName,
            @Parameter(description = "id") @RequestParam(required = false) Long id,
            @Parameter(description = "物流单号") @RequestParam(required = false) String shipmentNumber
    ) {
        return recyclingOrderService.recyclingPcOrderList(page, size, nickName, id, shipmentNumber);
    }

    /**
     * 判断是否存在，需要寄出的回收订单
     *
     * @return true 存在，false 不存在
     */
    @GetMapping("exit/wait/send")
    @Operation(summary = "判断是否存在，需要寄出的回收订单,true 存在，false 不存在", tags = "小程序端-回收订单相关接口")
    public Result<Boolean> exitWaitSendOrder() {
        Boolean flag = recyclingOrderService.exitWaitSendOrder();
        return Result.ok(flag);
    }

    /**
     * 新增回收订单,返回当前订单获得的卡券
     *
     * @param dto com.medusa.gruul.recycling.model.dto.RecyclingOrderNewDto
     * @return
     */
    @PostMapping("new")
    @Operation(summary = "新增回收订单,返回当前订单获得的卡券", tags = "小程序端-回收订单相关接口")
    public Result<String> recyclingOrderNew(@RequestBody @Validated RecyclingOrderNewDto dto) {
        String img = recyclingOrderService.recyclingOrderNew(dto);
        return Result.ok(img);
    }

    /**
     * 用户获取我的回收订单
     *
     * @param orderStatus 查询订单  0-全部  1-待寄出  2-已寄出  3-已取消
     * @param page        页数
     * @param size        条数
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    @GetMapping("list")
    @Operation(summary = "用户获取我的回收订单", tags = "小程序端-回收订单相关接口")
    public ResultPageList<RecyclingOrderListVo> recyclingOrderList(
            @Parameter(description = "查询订单  0-全部  1-待寄出  2-已寄出  3-已取消") @RequestParam Integer orderStatus,
            @Parameter(description = "页数") @RequestParam Integer page,
            @Parameter(description = "条数") @RequestParam Integer size
    ) {
        return recyclingOrderService.recyclingOrderList(orderStatus, page, size);
    }

    /**
     * 用户取消新增的回收订单
     *
     * @param id 订单id
     */
    @PutMapping("cancel/{id}")
    @Operation(summary = "用户取消新增的回收订单", tags = "小程序端-回收订单相关接口")
    public Result<Void> recyclingOrderCancel(
            @Parameter(description = "订单id") @PathVariable(name = "id") Long id
    ) {
        recyclingOrderService.recyclingOrderCancel(id);
        return Result.ok();
    }

    /**
     * 用户寄出回收订单
     *
     * @param dto com.medusa.gruul.recycling.model.dto.RecyclingOrderSendDto
     */
    @PutMapping("send")
    @Operation(summary = "用户寄出回收订单", tags = "小程序端-回收订单相关接口")
    public Result<Void> recyclingOrderSend(@RequestBody @Validated RecyclingOrderSendDto dto) {
        recyclingOrderService.recyclingOrderSend(dto);
        return Result.ok();
    }

    /**
     * 用户删除回收订单
     *
     * @param id 订单id
     */
    @DeleteMapping("del/{id}")
    @Operation(summary = "用户删除回收订单", tags = "小程序端-回收订单相关接口")
    public Result<Void> recyclingOrderDel(
            @Parameter(description = "订单id") @PathVariable(name = "id") Long id
    ) {
        recyclingOrderService.recyclingOrderDel(id);
        return Result.ok();
    }
}
