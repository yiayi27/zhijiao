package com.medusa.gruul.live.controller.manager;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.live.model.param.manager.LiveParam;
import com.medusa.gruul.live.model.vo.LiveVo;
import com.medusa.gruul.live.service.LiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;


/**
 * <p>
 * 直播表 前端控制器
 * </p>
 *
 * @author zhaokw
 * @since 2024-04-01
 */
@RestController
@RequestMapping("/manager")
@Tag(name = "直播接口")
public class LiveController {
    @Autowired
    private LiveService liveService;

    /**
     * 直播信息列表
     *
     * @param liveParam 直播信息列表
     * @return Result<PageUtils<LiveVo>>
     */
    @GetMapping("/get/List")
    @Operation(summary = "直播信息")
    public Result<PageUtils<LiveVo>> getLiveList(LiveParam liveParam) {
        PageUtils<LiveVo> pageUtils = new PageUtils<>(liveService.selectLiveList(liveParam));
        return Result.ok(pageUtils);
    }

    /**
     * 从微信后台重新调取数据
     *
     * @param liveParam 直播信息列表
     * @return Result<Void>
     */
    @GetMapping("/get/weChatList")
    @Operation(summary = "直播信息")
    public Result<PageUtils<LiveVo>> getWeChatLiveList(LiveParam liveParam) {
        PageUtils<LiveVo> pageUtils = new PageUtils<>(liveService.getWeChatLiveList(liveParam));
        return Result.ok(pageUtils);
    }


    /**
     * 直播信息修改
     *
     * @param liveParam 直播修改信息
     * @return Result<Void>
     */
    @GetMapping("/update/launchArea")
    @Operation(summary = "直播信息修改")
    public Result<Void> updateLaunchArea(LiveParam liveParam) {
        liveService.updateLaunchArea(liveParam);
        return Result.ok();
    }


    /**
     * 分享小程序码获取
     *
     * @param liveParam 分享小程序码的获取
     * @return Result<Void>
     */
    @GetMapping("/get/shareCode")
    @Operation(summary = "小程序码获取")
    public Result<Void> shareCode(LiveParam liveParam) {
        liveService.shareCode();
        return Result.ok();
    }


    /**
     * 拖拽更新sort
     *
     * @param params 直播信息列表
     * @return Result<Boolean>
     */
    @PostMapping("/update/sort")
    @Operation(summary = "直播信息修改")
    public Result<Boolean> updateSort(@RequestBody LinkedList<LiveParam> params) {
        return Result.ok(liveService.updateSort(params));
    }

}
