package com.medusa.gruul.platform.web.controller;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.platform.api.entity.PlatformShopInfo;
import com.medusa.gruul.platform.model.dto.ConsoleUpdateDto;
import com.medusa.gruul.platform.model.dto.ShopInfoConsoleCreateDto;
import com.medusa.gruul.platform.model.vo.ShopInfoVo;
import com.medusa.gruul.platform.service.IPlatformShopInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 店铺信息表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2024-03-06
 */
@RestController
@RequestMapping("/shop")
@Tag(name = "商户后台店铺相关接口")
public class PlatformShopController {

    @Autowired
    private IPlatformShopInfoService platformShopInfoService;

    /**
     * 商家控制台创建店铺
     *
     * @param dto com.medusa.gruul.platform.model.dto.ShopInfoConsoleCreateDto
     * @return 平台店铺id
     */
    @PostMapping(value = "/console/create")
    @Operation(summary = "控制台创建店铺,返回平台店铺id")
    @PreAuthorize("@S.role(@R.ADMIN).match()")
    public Result<String> amdinList(@RequestBody @Validated ShopInfoConsoleCreateDto dto) {
        String platformShopId = platformShopInfoService.consoleCreate(dto);
        return Result.ok(platformShopId);
    }

    /**
     * 控制台查询所属用户的所有店铺
     *
     * @param page 分页对象
     * @return com.medusa.gruul.platform.model.vo.ShopViewListVo
     */
    @GetMapping(value = "/console/list")
    @Operation(summary = "查询控制台拥有的店铺")
    @PreAuthorize("@S.anyRole(@R.ADMIN,@R.SUB_ADMIN).match()")
    public Result<PageUtils<PlatformShopInfo>> consoleList(
            @Parameter(description = "指定页数", required = true) @RequestParam Integer page,
            @Parameter(description = "数据条数", required = true) @RequestParam Integer size,
            @Parameter(description = "排序方式 1-升级 2-降序 默认降序", required = true) @RequestParam(defaultValue = "2") Integer orderBy

    ) {
        Page<PlatformShopInfo> objectPage = new Page<PlatformShopInfo>(page, size)
                .addOrder(
                        orderBy.equals(2) ?
                                OrderItem.desc("create_time") :
                                OrderItem.asc("create_time")
                );
        PageUtils<PlatformShopInfo> pageUtils = platformShopInfoService.consoleList(
                objectPage);
        return Result.ok(pageUtils);
    }

    /**
     * 商家自己删除店铺
     *
     * @param shopId 店铺id
     */
    @DeleteMapping(value = "/console/del/{shopId}")
    @Operation(summary = "控制台删除店铺")
    @PreAuthorize("@S.role(@R.ADMIN).match()")
    public Result<Void> consoleDel(@Parameter(description = "店铺id", required = true) @PathVariable String shopId) {
        platformShopInfoService.consoleDel(shopId);
        return Result.ok();
    }

    /**
     * 店铺设置
     *
     * @param dto    com.medusa.gruul.platform.model.dto.ConsoleUpdateDto
     * @param shopId 店铺id
     */
    @PutMapping(value = "/console/update/{shopId}")
    @Operation(summary = "店铺设置(修改店铺信息)")
    @PreAuthorize("@S.role(@R.ADMIN).match()")
    public Result<Void> consoleUpdate(@RequestBody @Validated ConsoleUpdateDto dto, @Parameter(description = "店铺id", required = true) @PathVariable String shopId) {
        platformShopInfoService.consoleUpdate(dto, shopId);
        return Result.ok();
    }

    /**
     * 获取店铺基本信息
     *
     * @return com.medusa.gruul.platform.model.vo.ShopInfoVo
     */
    @GetMapping("/info")
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取店铺基本信息")
    public Result<ShopInfoVo> shopInfo() {
        ShopInfoVo infoVo = platformShopInfoService.shopInfo(ISystem.shopId());
        return Result.ok(infoVo);
    }

    /**
     * 重新生成店所有默认值
     */
    @PostMapping("/default/repair")
    @Operation(summary = "重新生成店铺所有默认值")
    @PreAuthorize("denyAll()")
    public Result<Void> defaultRepair() {
        platformShopInfoService.defaultRepair(ISystem.shopId());
        return Result.ok();
    }

}
