package com.medusa.gruul.solitaire.web.controller.manager;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireInfoDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireInfoVo;
import com.medusa.gruul.solitaire.service.manager.ISolitaireInfoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/manager/info")
public class SolitaireInfoController {

    @Autowired
    private ISolitaireInfoService solitaireInfoService;

    /**
     * 获取接龙设置信息
     */
    @GetMapping("/get")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取接龙设置信息")
    public Result<SolitaireInfoVo> getInfo() {
        SolitaireInfoVo solitaireInfoVo = solitaireInfoService.getInfo();
        return Result.ok(solitaireInfoVo);
    }

    /**
     * 接龙信息设置新增/修改
     */
    @PostMapping("/save")
    @Operation(summary = "接龙信息设置新增/修改")
    public Result<Void> addActivity(@RequestBody @Validated SolitaireInfoDto solitaireInfoDto) {
        solitaireInfoService.saveInfo(solitaireInfoDto);
        return Result.ok();
    }
}
