package com.medusa.gruul.account.web.controller.api;

import com.medusa.gruul.account.model.dto.AccountFootMarkDto;
import com.medusa.gruul.account.model.vo.AccountFootMarkParam;
import com.medusa.gruul.account.service.IApiMiniAccountFootMarkService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoq
 * 小程序用戶足迹 前端控制器
 * @date 2024/2/28 10:34
 */
@RestController
@RequestMapping("/api/FootMark")
public class ApiMiniAccountFootMarkController {

    @Autowired
    private IApiMiniAccountFootMarkService miniAccountFootMarkService;

    /**
     * 获取足迹信息
     *
     * @param accountFootMarkParam 分页数据
     * @return Result<List<String>> 足迹信息
     */
    @GetMapping("/getUserFootMarkInfo")
    @Operation(summary = "获取用户足迹信息")
    public Result<List<String>> accountFootMarkList(AccountFootMarkParam accountFootMarkParam) {
        List<String> accountFootMark = miniAccountFootMarkService.getAccountFootMark(accountFootMarkParam);
        return Result.ok(accountFootMark);
    }


    /**
     * 添加足迹信息
     *
     * @param accountFootMarkDto 足迹数据
     * @return Result<Void>
     */
    @PostMapping("/add/FootMarkInfo")
    @PreAuthorize("permitAll()")
    @Operation(summary = "添加用户足迹信息")
    public Result<Void> addAccountFootMark(@RequestBody @Validated AccountFootMarkDto accountFootMarkDto) {
        miniAccountFootMarkService.addAccountFootMark(accountFootMarkDto);
        return Result.ok();
    }


    /**
     * 删除足迹信息
     *
     * @param footmarkIds 足迹ids
     * @param type        :0-删除, 1-清空
     * @return Result<Void>
     */
    @PutMapping("/del/FootMarkInfo/{type}")
    @Operation(summary = "删除用户足迹信息")
    public Result<Void> deleteAccountFootMark(@RequestBody Long[] footmarkIds,
                                        @PathVariable(value = "type") @NotNull Integer type) {
        miniAccountFootMarkService.delAccountFootMark(Arrays.asList(footmarkIds), type);
        return Result.ok();
    }

    /**
     * 获取用户足迹数量
     *
     * @return Result<Integer> 足迹数量
     */
    @GetMapping("/getAccountFootMarkCount")
    @Operation(summary = "获取用户足迹数量")
    public Result<Integer> getAccountFootMarkCount() {
        return Result.ok(miniAccountFootMarkService.getAccountFootMarkCount());
    }
}
