package com.medusa.gruul.solitaire.web.controller.api;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.entity.InformationTopic;
import com.medusa.gruul.solitaire.api.model.dto.api.ApiInformationMessageDto;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationInfoParam;
import com.medusa.gruul.solitaire.api.model.param.api.ApiInformationMessageParam;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationInfoVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationMessageVo;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiInformationSetVo;
import com.medusa.gruul.solitaire.service.api.IApiInformationInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 小程序资讯信息信息 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2024-05-26
 */
@RestController
@RequestMapping("/api/information/info")
public class ApiInformationInfoController {

    @Autowired
    private IApiInformationInfoService iApiInformationInfoService;

    /**
     * 获取资讯设置信息
     */
    @GetMapping("/get")
    @Operation(summary = "获取资讯设置信息")
    public Result<ApiInformationSetVo> getInformationSet() {
        ApiInformationSetVo apiInformationSetVo = iApiInformationInfoService.getInformationSet();
        return Result.ok(apiInformationSetVo);
    }

    /**
     * 获取资讯专题信息
     */
    @GetMapping("/topic/list")
    @Operation(summary = "获取资讯专题信息")
    public Result<List<InformationTopic>> topicList() {
        return Result.ok(iApiInformationInfoService.topicList());
    }

    /**
     * 获取资讯列表信息
     */
    @GetMapping("/list")
    @Operation(summary = "获取资讯列表信息")
    public Result<PageUtils<ApiInformationInfoVo>> list(ApiInformationInfoParam apiInformationInfoParam) {
        PageUtils<ApiInformationInfoVo> pageUtils = iApiInformationInfoService.list(apiInformationInfoParam);
        return Result.ok(pageUtils);
    }

    /**
     * 获取单条资讯信息
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "获取单条资讯信息")
    public Result<ApiInformationInfoVo> get(@Parameter(description = "资讯信息id", required = true) @PathVariable(name = "id") Long id) {
        ApiInformationInfoVo apiInformationInfoVo = iApiInformationInfoService.get(id);
        return Result.ok(apiInformationInfoVo);
    }

    /**
     * 资讯阅读量增加
     */
    @PutMapping("/set/read/{id}")
    @Operation(summary = "资讯阅读量增加")
    public Result<Void> setRead(@Parameter(description = "资讯信息id", required = true) @PathVariable(name = "id") Long id) {
        iApiInformationInfoService.setRead(id);
        return Result.ok();
    }

    /**
     * 获取资讯留言列表信息
     */
    @GetMapping("/get/message/list")
    @Operation(summary = "获取资讯留言列表信息")
    public Result<PageUtils<ApiInformationMessageVo>> getMessageList(ApiInformationMessageParam apiInformationMessageParam) {
        PageUtils<ApiInformationMessageVo> pageUtils = iApiInformationInfoService.getMessageList(apiInformationMessageParam);
        return Result.ok(pageUtils);
    }

    /**
     * 资讯信息留言
     */
    @PostMapping("/leave")
    @Operation(summary = "资讯留言)")
    public Result<Void> leave(@RequestBody @Validated ApiInformationMessageDto apiInformationMessageDto) {
        iApiInformationInfoService.leave(apiInformationMessageDto);
        return Result.ok();
    }

    /**
     * 资讯信息点赞
     */
    @PostMapping("/like/{type}/{infoId}")
    @Operation(summary = "资讯信息点赞)")
    public Result<Void> like(@Parameter(description = "操作类型 0-点赞 1-取消点赞", required = true) @PathVariable(name = "type") Integer type,
                       @Parameter(description = "资讯id", required = true) @PathVariable(name = "infoId") Long infoId) {
        iApiInformationInfoService.like(type, infoId);
        return Result.ok();
    }

    /**
     * 资讯留言点赞
     */
    @PostMapping("/message/like/{type}/{messageId}")
    @Operation(summary = "资讯留言点赞)")
    public Result<Void> messageLike(@Parameter(description = "操作类型 0-点赞 1-取消点赞", required = true) @PathVariable(name = "type") Integer type,
                              @Parameter(description = "资讯留言id", required = true) @PathVariable(name = "messageId") Long messageId) {
        iApiInformationInfoService.messageLike(type, messageId);
        return Result.ok();
    }

    /**
     * 资讯留言删除
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "资讯留言删除")
    public Result<Void> deleteActivity(@Parameter(description = "资讯留言id", required = true) @PathVariable(name = "id") Long id) {
        iApiInformationInfoService.delete(id);
        return Result.ok();
    }
}
