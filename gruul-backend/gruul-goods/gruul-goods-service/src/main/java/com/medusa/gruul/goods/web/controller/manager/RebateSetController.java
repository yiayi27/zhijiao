package com.medusa.gruul.goods.web.controller.manager;



import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.model.dto.manager.RebateSetDto;
import com.medusa.gruul.goods.service.manager.IRebateSetService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 店铺返利信息设置
 * </p>
 *
 * @author lcysike
 * @since 2024-05-21
 */
@RestController
@RequestMapping("/manager/rebate/set")
public class RebateSetController {

    @Autowired
    private IRebateSetService rebateSetService;

    /**
     * 获取店铺返利信息设置
     */
    @GetMapping("/get")
    @Operation(summary = "获取店铺返利信息设置")
    @PreAuthorize("permitAll()")
    public Result<RebateSet> selectRebateSet() {
        RebateSet rebateSet = rebateSetService.selectRebateSet();
        return Result.ok(rebateSet);
    }

    /**
     * 新增/修改店铺返利信息设置
     */
    @PutMapping("/save")
    @Operation(summary = "新增/修改店铺返利信息设置")
    public Result<Void> saveRebateSet(@RequestBody @Validated RebateSetDto rebateSetDto) {
        rebateSetService.saveRebateSet(rebateSetDto);
        return Result.ok();
    }

}
