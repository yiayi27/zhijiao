package com.medusa.gruul.recycling.web.controller;


import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.recycling.model.vo.MeRecyclingAssOrderListVo;
import com.medusa.gruul.recycling.service.IRecyclingAssOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 基金所得订单关联表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/recycling-ass-order")
public class RecyclingAssOrderController {

    @Autowired
    private IRecyclingAssOrderService recyclingAssOrderService;

    /**
     * 获取用户基金订单
     *
     * @param page 页数
     * @param size 条数
     * @return com.medusa.gruul.recycling.model.vo.MeRecyclingAssOrderList
     */
    @GetMapping("list")
    @Operation(summary = "获取用户基金订单", tags = "小程序端-订单关联基金")
    public ResultPageList<MeRecyclingAssOrderListVo> recyclingAssOrderList(
            @Parameter(description = "页数") @RequestParam Integer page,
            @Parameter(description = "条数") @RequestParam Integer size
    ) {
        return recyclingAssOrderService.recyclingAssOrderAccountList(page, size);
    }

}
