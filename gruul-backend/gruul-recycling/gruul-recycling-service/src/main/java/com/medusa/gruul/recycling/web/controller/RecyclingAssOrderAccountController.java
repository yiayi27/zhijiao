package com.medusa.gruul.recycling.web.controller;


import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.recycling.model.vo.RecyclingAssOrderAccountStatisticalVo;
import com.medusa.gruul.recycling.model.vo.RecyclingAssOrderListVo;
import com.medusa.gruul.recycling.service.IRecyclingAssOrderAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 基金所得订单用户关联表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2024-05-05
 */
@RestController
@RequestMapping("/recycling-ass-order-account")
public class RecyclingAssOrderAccountController {


    @Autowired
    private IRecyclingAssOrderAccountService recyclingAssOrderList;

    /**
     * 基金详情统计
     *
     * @return com.medusa.gruul.recycling.model.vo.RecyclingAssOrderAccountStatisticalVo
     */
    @GetMapping("statistical")
    @Operation(summary = "基金详情统计", tags = "pc端-基金详情")
    public Result<RecyclingAssOrderAccountStatisticalVo> recyclingAssOrderAccountStatistical(
    ) {
        RecyclingAssOrderAccountStatisticalVo vo = recyclingAssOrderList.recyclingAssOrderAccountStatistical();
        return Result.ok(vo);
    }

    /**
     * 获取店铺用户基金详情
     *
     * @param page     页数
     * @param size     条数
     * @param nickName 昵称
     * @param sortType 排序方式  1-基金值升序  2-基金值降序   3-按最近交易时间升序  4-按最近交易时间降序
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    @GetMapping("list")
    @Operation(summary = "获取店铺用户基金详情", tags = "pc端-基金详情")
    public ResultPageList<RecyclingAssOrderListVo> recyclingAssOrderAccountList(
            @Parameter(description = "排序方式  1-基金值升序  2-基金值降序   3-按最近交易时间升序  4-按最近交易时间降序") @RequestParam(defaultValue = "1") Integer sortType,
            @Parameter(description = "页数") @RequestParam Integer page,
            @Parameter(description = "条数") @RequestParam Integer size,
            @Parameter(description = "昵称") @RequestParam(required = false) String nickName
    ) {
        return recyclingAssOrderList.recyclingAssOrderAccountList(page, size, nickName, sortType);
    }

    /**
     * 基金详情统计
     *
     * @return com.medusa.gruul.recycling.model.vo.RecyclingAssOrderAccountStatisticalVo
     */
    @GetMapping("me/statistical")
    @Operation(summary = "获取用户自身基金统计", tags = "小程序-基金统计")
    public Result<RecyclingAssOrderAccountStatisticalVo> meRecyclingAssOrderAccountList() {
        RecyclingAssOrderAccountStatisticalVo vo = recyclingAssOrderList.recyclingAssOrderAccountStatistical();
        return Result.ok(vo);
    }
}
