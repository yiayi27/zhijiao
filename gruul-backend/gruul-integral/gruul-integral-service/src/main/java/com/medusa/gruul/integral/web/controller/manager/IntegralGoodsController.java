package com.medusa.gruul.integral.web.controller.manager;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;

import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.service.IIntegralGoodsService;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralGoodsDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 积分商品表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("/manager")
@Tag(name = "积分商品接口")
public class IntegralGoodsController {
    @Autowired
    private IIntegralGoodsService integralGoodsService;

    /**
     * 积分商品新增 (发布商品)
     *
     * @param integralGoodsDto 积分商品数据
     * @return
     */
    @PostMapping("/issue/integralGoods")
    @Operation(summary = "积分商品发布")
    public Result<Void> issueIntegralGoods(@RequestBody @Validated IntegralGoodsDto integralGoodsDto) {
        String shopId = ISecurity.userMust().getShopId();
        integralGoodsService.addIntegralGoods(integralGoodsDto, shopId);
        return Result.ok();
    }


    /**
     * 根据积分商品id 删除积分商品 (逻辑删除)
     *
     * @param id 积分商品id
     * @return
     */
    @PreAuthorize("permitAll()")
    @DeleteMapping("/delete/integralGoods/{id}")
    @Operation(summary = "删除积分商品")
    public Result<Void> deleteIntegralGoods(@Parameter(description = "积分商品id", required = true) @PathVariable(name = "id") Long id) {
        integralGoodsService.deleteIntegralGoods(id);
        return Result.ok();
    }


    /**
     * 积分商品信息修改
     *
     * @param integralGoodsDto 积分商品信息
     * @return
     */
    @PutMapping("/update/integralGoods")
    @Operation(summary = "积分商品信息修改")
    public Result<Void> updateIntegralGoods(@RequestBody @Validated IntegralGoodsDto integralGoodsDto) {
        integralGoodsService.updateIntegralGoods(integralGoodsDto);
        return Result.ok();
    }


    /**
     * 获取积分商品的基础信息
     *
     * @param id 积分商品id
     * @return 积分商品基础信息
     */
    @GetMapping("/get/integralGoodsInfo")
    @Operation(summary = "单个积分商品基础信息")
    public Result<IntegralGoodVo> getIntegralGoodsInfo(@Parameter(description = "积分商品id", required = true) Long id) {
        IntegralGoodVo integralGoodsInfo = integralGoodsService.getIntegralGoodsInfo(id);
        return Result.ok(integralGoodsInfo);
    }


    /**
     * 积分商品上下架
     *
     * @param id     积分商品id
     * @param status 状态值
     * @return
     */
    @PutMapping("/update/integralGoods/status")
    @Operation(summary = "积分商品上下架")
    public Result<Void> updateIntegralGoodsStatus(@Parameter(description = "积分商品id", required = true) Long id,
                                            @Parameter(description = "积分商品状态") Integer status) {
        integralGoodsService.updateIntegralGoodsStatus(id, status);
        return Result.ok();
    }


    /**
     * 积分商品批量上下架
     *
     * @param ids    积分商品ids
     * @param status 状态值 (上架或下架)
     * @return
     */
    @PutMapping("/update/batchIntegralGoods/{status}")
    @Operation(summary = "积分商品批量上下架")
    public Result<Void> updateBatchIntegralGoodsStatus(@Parameter(description = "积分商品ids", required = true) @RequestBody Long[] ids,
                                                 @Parameter(description = "状态值", required = true) @PathVariable("status") Integer status) {
        integralGoodsService.updateBatchStatus(ids, status);
        return Result.ok();

    }


    /**
     * 批量删除积分商品(逻辑删除)
     *
     * @param ids 积分商品ids
     * @return
     */
    @DeleteMapping("/delete/batch/integralGoods/{ids}")
    @Operation(summary = "批量删除积分商品")
    public Result<Void> deleteBatchIntegralGoods(
            @Parameter(description = "积分商品ids", required = true) @RequestBody @PathVariable(name = "ids") Long[] ids) {
        integralGoodsService.deleteBatch(ids);
        return Result.ok();
    }


    /**
     * 积分商品信息列表
     *
     * @param integralGoodsParam 查询条件
     * @return 符合条件的积分商品数据
     */
    @GetMapping("get/integralGoods/List")
    @Operation(summary = "积分商品信息")
    @PreAuthorize("permitAll()")
    public Result<PageUtils<IntegralGoodVo>> getIntegralGoods(IntegralGoodsParam integralGoodsParam) {
        PageUtils<IntegralGoodVo> pageUtils = new PageUtils<>(integralGoodsService.selectIntegralGoodsList(integralGoodsParam));
        return Result.ok(pageUtils);
    }
}
