package com.medusa.gruul.solitaire.web.controller.manager;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationInfoDto;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationInfoParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationInfoVo;
import com.medusa.gruul.solitaire.service.manager.IInformationInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 资讯信息信息 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@RestController
@RequestMapping("/manager/information/info")
public class InformationInfoController {

    @Autowired
    private IInformationInfoService iInformationInfoService;

    /**
     * 获取资讯列表信息
     */
    @GetMapping("/list")
    @Operation(summary = "获取资讯列表信息")
    public Result<PageUtils<InformationInfoVo>> list(InformationInfoParam informationInfoParam) {
        PageUtils<InformationInfoVo> pageUtils = new PageUtils<>(iInformationInfoService.list(informationInfoParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取单条资讯信息
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "获取单条资讯信息")
    public Result<InformationInfoVo> get(@Parameter(description = "资讯信息id", required = true) @PathVariable(name = "id") Long id) {
        InformationInfoVo informationInfoVo = iInformationInfoService.get(id);
        return Result.ok(informationInfoVo);
    }

    /**
     * 资讯信息新增/修改
     */
    @PostMapping("/save")
    @Operation(summary = "资讯信息新增/修改")
    public Result<Void> save(@RequestBody @Validated InformationInfoDto informationInfoDto) {
        iInformationInfoService.save(informationInfoDto);
        return Result.ok();
    }


    /**
     * 资讯信息排序
     */
    @PutMapping("/sort")
    @Operation(summary = "资讯信息排序")
    public Result<Void> sort(@RequestBody List<InformationInfoDto> informationInfoDtos) {
        iInformationInfoService.sort(informationInfoDtos);
        return Result.ok();
    }

    /**
     * 资讯信息删除
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "资讯信息删除")
    public Result<Void> delete(@Parameter(description = "资讯专题ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        iInformationInfoService.delete(ids);
        return Result.ok();
    }
}
