package com.medusa.gruul.solitaire.web.controller.manager;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationMessageDto;
import com.medusa.gruul.solitaire.api.model.param.manager.InformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationMessageVo;
import com.medusa.gruul.solitaire.service.manager.IInformationMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 资讯留言信息 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@RestController
@RequestMapping("/manager/information/message")
public class InformationMessageController {

    @Autowired
    private IInformationMessageService iInformationMessageService;

    /**
     * 获取资讯留言信息
     */
    @GetMapping("/list")
    @Operation(summary = "获取资讯留言信息")
    public Result<PageUtils<InformationMessageVo>> list(InformationMessageParam informationMessageParam) {
        PageUtils<InformationMessageVo> pageUtils = iInformationMessageService.list(informationMessageParam);
        return Result.ok(pageUtils);
    }

    /**
     * 资讯信息回复
     */
    @PostMapping("/reply")
    @Operation(summary = "资讯留言回复)")
    public Result<Void> reply(@RequestBody @Validated InformationMessageDto informationMessageDto) {
        iInformationMessageService.reply(informationMessageDto);
        return Result.ok();
    }

    /**
     * 资讯信息设为/取消星标
     */
    @PostMapping("/star")
    @Operation(summary = "资讯留言新增/修改(支持批量)")
    public Result<Void> star(@RequestBody @Validated InformationMessageDto informationMessageDto) {
        iInformationMessageService.star(informationMessageDto);
        return Result.ok();
    }


    /**
     * 资讯留言排序
     */
    @PutMapping("/sort")
    @Operation(summary = "资讯留言排序")
    public Result<Void> sort(@RequestBody List<InformationMessageDto> informationMessageDtos) {
        iInformationMessageService.sort(informationMessageDtos);
        return Result.ok();
    }

    /**
     * 资讯留言删除
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "资讯留言删除")
    public Result<Void> deleteActivity(@Parameter(description = "资讯留言id", required = true) @PathVariable(name = "id") Long id) {
        iInformationMessageService.delete(id);
        return Result.ok();
    }
}
