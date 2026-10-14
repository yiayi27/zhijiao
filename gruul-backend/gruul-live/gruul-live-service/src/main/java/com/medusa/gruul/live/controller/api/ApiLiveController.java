package com.medusa.gruul.live.controller.api;



import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.live.model.param.api.ApiLiveParam;
import com.medusa.gruul.live.model.param.manager.LiveParam;
import com.medusa.gruul.live.model.vo.LiveVo;
import com.medusa.gruul.live.service.LiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 小程序端直播接口
 * @Author: zhaokw
 * @Date: 2024/4/5 9:47
 **/
@RestController
@RequestMapping("/api/")
@Tag(name = "小程序端直播接口")
public class ApiLiveController {
    @Autowired
    private LiveService liveService;

    /**
     * 获取小程序端的直播列表 进行分页
     *
     * @param apiLiveParam 分页对象
     * @return Result<PageUtils<LiveVo>>
     */
    @PreAuthorize("permitAll()")
    @GetMapping("get/list")
    @Operation(summary = "直播列表分页展示")
    public Result<PageUtils<LiveVo>> getApiLiveList(ApiLiveParam apiLiveParam) {
        PageUtils<LiveVo> pageUtils = new PageUtils<>(liveService.getPageList(apiLiveParam));
        return Result.ok(pageUtils);
    }

    /**
     * 更新小程序端观看量
     *
     * @param liveParam 直播间id
     * @return Result<Void>
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/update/viewCount")
    @Operation(summary = "更新直播间观看量")
    public Result<Void> updateViewCount(LiveParam liveParam) {
        liveService.updateViewCount(liveParam);
        return Result.ok();
    }

    /**
     * 更新小程序端点赞量
     *
     * @param liveParam 直播间id
     * @return Result<Void>
     */
    @GetMapping("/update/likeCount")
    @Operation(summary = "更新直播间点赞量")
    @PreAuthorize("permitAll()")
    public Result<Void> updateLikeCount(LiveParam liveParam) {
        liveService.updateLikeCount(liveParam);
        return Result.ok();
    }


}
