package com.medusa.gruul.discount.web.controller.manager;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.dto.manager.FullScaleDto;
import com.medusa.gruul.discount.api.model.param.manager.FullScaleParam;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleSimpleVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.discount.service.manager.IFullScaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 满减活动 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@RestController
@RequestMapping("/manager/full/scale")
public class FullScaleController {

    @Autowired
    private IFullScaleService fullScaleService;

    /**
     * 满减活动信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "满减活动信息列表")
    public Result<PageUtils<FullScaleVo>> getFullScaleList(FullScaleParam fullScaleParam) {
        PageUtils<FullScaleVo> pageUtils = new PageUtils<>(fullScaleService.getFullScaleList(fullScaleParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取单张满减活动信息
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "单条满减活动信息")
    public Result<FullScaleSimpleVo> getSimpleFullScale(@Parameter(description = "满减活动id", required = true) @PathVariable("id") Long id) {
        FullScaleSimpleVo fullScaleSimpleVo = fullScaleService.getSimpleFullScale(id);
        return Result.ok(fullScaleSimpleVo);
    }

    /**
     * 满减活动新增
     */
    @PostMapping("/add")
    @Operation(summary = "满减活动新增")
    public Result<Void> addFullScale(@RequestBody @Validated FullScaleDto fullScaleDto) {
        fullScaleService.addFullScale(fullScaleDto);
        return Result.ok();
    }

    /**
     * 满减活动修改
     */
    @PutMapping("/update")
    @Operation(summary = "满减活动修改")
    public Result<Void> updateFullScale(@RequestBody @Validated FullScaleDto fullScaleDto) {
        fullScaleService.updateFullScale(fullScaleDto);
        return Result.ok();
    }

    /**
     * 关闭/启用满减活动
     */
    @PutMapping("/changeStatus/{status}")
    @Operation(summary = "关闭/启用满减活动")
    public Result<Void> changeFullScaleStatus(@Parameter(description = "满减活动ids", required = true) @RequestBody Long[] ids,
                                        @Parameter(description = "满减活动状态值", required = true) @PathVariable("status") Integer status) {
        fullScaleService.changeFullScaleStatus(ids, status);
        return Result.ok();
    }

    /**
     * 满减活动删除
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "满减活动删除(单个与批量共用)")
    public Result<Void> deleteFullScaleList(@Parameter(description = "满减活动ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        fullScaleService.deleteFullScaleList(ids);
        return Result.ok();
    }

}
