package com.medusa.gruul.solitaire.web.controller.api;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireActivityVo;
import com.medusa.gruul.solitaire.service.api.IApiSolitaireActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 接龙信息controller
 *
 * @author lcysike
 * @Date 2024-06-22
 */
@RestController
@RequestMapping("/api/weChat")
public class ApiSolitaireActivityController {

    @Autowired
    private IApiSolitaireActivityService apiSolitaireActivityService;

    @Operation(summary = "获取当前接龙活动信息")
    @GetMapping("/current/activity")
    @PreAuthorize("permitAll()")
    public Result<ApiSolitaireActivityVo> getCurrentActivity() {
        return apiSolitaireActivityService.getCurrentActivity();
    }

    @Operation(summary = "获取单场接龙活动详细信息")
    @GetMapping("/get/activity/{activityId}")
    @PreAuthorize("permitAll()")
    public Result<ApiSolitaireActivityVo> getCurrentActivityById(@Parameter(description = "接龙活动id", required = true) @PathVariable("activityId") Long activityId) {
        return apiSolitaireActivityService.getCurrentActivityById(activityId);
    }

    @Operation(summary = "h5获取单场接龙活动详细信息")
    @GetMapping("/h5/get/activity")
    @PreAuthorize("permitAll()")
    public Result<ApiSolitaireActivityVo> getH5CurrentActivityById(@Parameter(description = "接龙活动id", required = true) @RequestParam Long activityId,
                                                                   @Parameter(description = "店铺id", required = true) @RequestParam String shopId) {

        return apiSolitaireActivityService.getCurrentActivityById(activityId);
    }

    @Operation(summary = "转发当前接龙活动商品信息")
    @GetMapping("/current/activity/product/{activityId}")
    @PreAuthorize("permitAll()")
    public Result<Object> getCurrentActivityProduct(@Parameter(description = "接龙活动id", required = true) @PathVariable("activityId") Long activityId,
                                            @Parameter(description = "接龙活动跳转路径", required = true) @RequestParam("path") String path) {
        return apiSolitaireActivityService.getCurrentActivityProduct(activityId, path);
    }

    @Operation(summary = "转发当前接龙信息")
    @GetMapping("/current/activity/record/{activityId}")
    public Result<Object> getCurrentActivityRecord(@Parameter(description = "接龙活动id", required = true) @PathVariable("activityId") Long activityId,
                                           @Parameter(description = "接龙活动跳转路径", required = true) @RequestParam("path") String path) {
        return apiSolitaireActivityService.getCurrentActivityRecord(activityId, path);
    }

    @Operation(summary = "根据商品获取当前参与的最新一场接龙活动信息")
    @GetMapping("get/activity/by/product/{productId}")
    @PreAuthorize("permitAll()")
    public Result<Object> getActivityByProduct(@Parameter(description = "商品id", required = true) @PathVariable("productId") Long productId) {
        return apiSolitaireActivityService.getActivityByProduct(productId);
    }
}

