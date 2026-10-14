package com.medusa.gruul.recycling.web.controller;



import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.recycling.model.dto.CouponModifyDto;
import com.medusa.gruul.recycling.model.dto.FundModifyDto;
import com.medusa.gruul.recycling.model.dto.PopupWindowDto;
import com.medusa.gruul.recycling.model.vo.RecyclingSettingVo;
import com.medusa.gruul.recycling.service.IRecyclingSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 回收设置表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@RestController
@Tag(name = "PC端-相关接口")
@RequestMapping("/recycling-setting")
public class RecyclingSettingController {

    @Autowired
    private IRecyclingSettingService recyclingSettingService;

    /**
     * 获取当前店铺信息
     *
     * @return com.medusa.gruul.recycling.model.vo.RecyclingSettingVo
     */
    @GetMapping("info")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取当前店铺回收相关配置查询", tags = "小程序端与pc端共用接口")
    public Result<RecyclingSettingVo> fundInfo() {
        RecyclingSettingVo vos = recyclingSettingService.getSetting();
        return Result.ok(vos);
    }

    /**
     * 商品基金设置修改
     *
     * @param settingDto com.medusa.gruul.recycling.model.dto.FundModifyDto
     */
    @PutMapping("fund/modify")
    @Operation(summary = "商品基金设置修改")
    public Result<Void> fundModify(@RequestBody @Validated FundModifyDto settingDto) {
        recyclingSettingService.fundModify(settingDto);
        return Result.ok();
    }

    /**
     * 回收卡券设置修改
     *
     * @param couponModifyDto com.medusa.gruul.recycling.model.dto.CouponModifyDto
     */
    @PutMapping("coupon/modify")
    @Operation(summary = "回收卡券设置修改")
    public Result<Void> couponModify(@RequestBody @Validated CouponModifyDto couponModifyDto) {
        recyclingSettingService.couponModify(couponModifyDto);
        return Result.ok();
    }

    /**
     * 弹窗设置
     *
     * @param popupWindowDto com.medusa.gruul.recycling.model.dto.PopupWindowDto
     */
    @PutMapping("popup/window")
    @Operation(summary = "弹窗设置")
    public Result<Void> popupWindow(@RequestBody @Validated PopupWindowDto popupWindowDto) {
        recyclingSettingService.popupWindow(popupWindowDto);
        return Result.ok();
    }
}
