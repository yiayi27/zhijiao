package com.medusa.gruul.solitaire.web.controller.manager;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationSetDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationSetVo;
import com.medusa.gruul.solitaire.service.manager.IInformationSetService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 资讯设置信息 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@RestController
@RequestMapping("/manager/information/set")
public class InformationSetController {

    @Autowired
    private IInformationSetService iInformationSetService;

    /**
     * 获取资讯设置信息
     */
    @GetMapping("/get")
    @Operation(summary = "获取资讯设置信息")
    public Result<InformationSetVo> getInformationSet() {
        InformationSetVo informationSetVo = iInformationSetService.get();
        return Result.ok(informationSetVo);
    }

    /**
     * 资讯设置新增/修改
     */
    @PostMapping("/save")
    @Operation(summary = "资讯设置新增/修改")
    public Result<Void> saveInfo(@RequestBody @Validated InformationSetDto informationSetDto) {
        iInformationSetService.save(informationSetDto);
        return Result.ok();
    }
}
