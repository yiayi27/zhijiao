package com.medusa.gruul.solitaire.web.controller.manager;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireActivityDto;
import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivitySimpleVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;
import com.medusa.gruul.solitaire.service.manager.ISolitaireActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 接龙活动信息 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@RestController
@RequestMapping("/manager/activity")
public class SolitaireActivityController {

    @Autowired
    private ISolitaireActivityService solitaireActivityService;

    /**
     * 接龙活动信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "接龙活动信息列表")
    public Result<PageUtils<SolitaireActivityVo>> getActivityList(SolitaireActivityParam solitaireActivityParam) {
        PageUtils<SolitaireActivityVo> pageUtils = new PageUtils<>(solitaireActivityService.getActivityList(solitaireActivityParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取接龙活动信息明细
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "获取活动信息明细")
    public Result<SolitaireActivitySimpleVo> getActivityById(@Parameter(description = "接龙活动id", required = true) @PathVariable("id") Long id) {
        SolitaireActivitySimpleVo solitaireActivitySimpleVo = solitaireActivityService.getActivityById(id);
        return Result.ok(solitaireActivitySimpleVo);
    }

    /**
     * 接龙活动新增
     */
    @PostMapping("/add")
    @Operation(summary = "接龙活动新增")
    public Result<Void> addActivity(@RequestBody @Validated SolitaireActivityDto solitaireActivityDto) {
        solitaireActivityService.addActivity(solitaireActivityDto);
        return Result.ok();
    }

    /**
     * 接龙活动修改
     */
    @PutMapping("/update")
    @Operation(summary = "接龙活动修改")
    public Result<Void> updateActivity(@RequestBody @Validated SolitaireActivityDto solitaireActivityDto) {
        solitaireActivityService.updateActivity(solitaireActivityDto);
        return Result.ok();
    }

    /**
     * 接龙活动关闭
     */
    @PutMapping("/changeStatus/{id}")
    @Operation(summary = "接龙活动关闭")
    public Result<Void> changeActivityStatus(@Parameter(description = "接龙活动id", required = true) @PathVariable(name = "id") Long id) {
        solitaireActivityService.changeActivityStatus(id);
        return Result.ok();
    }

    /**
     * 接龙活动删除
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "接龙活动删除")
    public Result<Void> deleteActivity(@Parameter(description = "接龙活动id", required = true) @PathVariable(name = "id") Long id) {
        solitaireActivityService.deleteActivity(id);
        return Result.ok();
    }
}
