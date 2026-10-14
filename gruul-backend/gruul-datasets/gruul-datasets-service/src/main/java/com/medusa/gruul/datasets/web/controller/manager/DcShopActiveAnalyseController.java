package com.medusa.gruul.datasets.web.controller.manager;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.datasets.service.IDcShopActiveAnalyseService;
import com.medusa.gruul.datasets.web.model.CallerInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 访客数据统计表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2024-05-06
 */
@RestController
@RequestMapping("/shopCaller")
public class DcShopActiveAnalyseController {

    @Autowired
    private IDcShopActiveAnalyseService dcShopActiveAnalyseService;

    /**
     * 新增访客数据
     *
     * @return Result<Void>
     */
    @PostMapping("add/info")
    @Operation(summary = "访客数据新增")
    @PreAuthorize("@S.consumer")
    public Result<Void> callerInfoAdd() {
        dcShopActiveAnalyseService.addCallerInfo();
        return Result.ok();
    }

    /**
     * 获取当前商铺的访客信息
     *
     * @return Result<CallerInfoVo>
     */
    @GetMapping("get/info")
    @Operation(summary = "获取商铺访客信息")
    public Result<CallerInfoVo> getCallerInfo() {
        CallerInfoVo shopCallerInfo = dcShopActiveAnalyseService.getShopCallerInfo();
        return Result.ok(shopCallerInfo);
    }

}
