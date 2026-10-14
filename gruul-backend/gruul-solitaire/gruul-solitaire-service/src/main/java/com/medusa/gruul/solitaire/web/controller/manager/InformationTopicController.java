package com.medusa.gruul.solitaire.web.controller.manager;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.model.dto.manager.InformationTopicDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.InformationTopicVo;
import com.medusa.gruul.solitaire.service.manager.IInformationTopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 资讯专题信息 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@RestController
@RequestMapping("/manager/information/topic")
public class InformationTopicController {

    @Autowired
    private IInformationTopicService iInformationTopicService;

    /**
     * 获取资讯专题信息
     */
    @GetMapping("/get")
    @Operation(summary = "获取资讯专题信息")
    public Result<List<InformationTopicVo>> get() {
        List<InformationTopicVo> informationTopics = iInformationTopicService.get();
        return Result.ok(informationTopics);
    }

    /**
     * 资讯专题新增/修改(支持批量)
     */
    @PostMapping("/save")
    @Operation(summary = "资讯专题新增/修改(支持批量)")
    public Result<Void> save(@RequestBody @Validated List<InformationTopicDto> informationTopicDtos) {
        iInformationTopicService.save(informationTopicDtos);
        return Result.ok();
    }


    /**
     * 资讯专题排序
     */
    @PutMapping("/sort")
    @Operation(summary = "资讯专题排序")
    public Result<Void> sort(@RequestBody List<InformationTopicDto> informationTopicDtos) {
        iInformationTopicService.sort(informationTopicDtos);
        return Result.ok();
    }

    /**
     * 资讯专题删除
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "资讯专题删除")
    public Result<Void> deleteActivity(@Parameter(description = "资讯专题id", required = true) @PathVariable(name = "id") Long id) {
        iInformationTopicService.delete(id);
        return Result.ok();
    }
}
