package com.medusa.gruul.platform.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.web.parameter.BodyParam;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.model.dto.admin.ShopAdminAddDTO;
import com.medusa.gruul.platform.model.dto.admin.ShopAdminUpdateDTO;
import com.medusa.gruul.platform.model.enums.ShopAdminAvailable;
import com.medusa.gruul.platform.model.vo.AdminAccountVO;
import com.medusa.gruul.platform.service.ShopAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺管理员权限控制器
 *
 * @author 张治保
 * @since 2025/4/9
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/admin")
@PreAuthorize("@S.hasPerm(@P.MALL_SETTING.PERMISSION)")
public class ShopAdminController {

    private final ShopAdminService shopAdminService;

    /**
     * 分页查询管理员
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询管理员")
    public Result<PageUtils<AdminAccountVO>> page(@RequestBody Page<AccountInfo> param) {
        return Result.ok(
                shopAdminService.page(param)
        );
    }

    /**
     * 查询该手机号是否可绑定为本店管理员
     */
    @PostMapping("/available")
    @Operation(summary = "查询该手机号是否可绑定为本店管理员")
    public Result<ShopAdminAvailable> available(@BodyParam @Schema(title = "手机号码") String mobile) {
        return Result.ok(
                shopAdminService.available(mobile)
        );
    }

    /**
     * 新增管理员
     */
    @PostMapping("/add")
    @Operation(summary = "新增管理员")
    public Result<Void> add(@RequestBody @Valid ShopAdminAddDTO param) {
        shopAdminService.add(param);
        return Result.ok();
    }

    /**
     * 更新管理员
     */
    @PostMapping("/update")
    @Operation(summary = "更新管理员")
    public Result<Void> update(@RequestBody @Valid ShopAdminUpdateDTO param) {
        shopAdminService.update(param);
        return Result.ok();
    }

    /**
     * 删除管理员
     */
    @PostMapping("/delete")
    @Operation(summary = "删除管理员")
    public Result<Void> delete(@BodyParam String mobile) {
        shopAdminService.delete(mobile);
        return Result.ok();
    }
}
