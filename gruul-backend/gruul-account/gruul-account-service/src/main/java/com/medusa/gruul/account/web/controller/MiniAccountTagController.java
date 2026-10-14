package com.medusa.gruul.account.web.controller;


import com.medusa.gruul.account.model.dto.UserTagDto;
import com.medusa.gruul.account.model.vo.TagVo;
import com.medusa.gruul.account.service.IMiniAccountTagService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户标签表 前端控制器
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@RestController
@RequestMapping("/mini-account-tag")
@Tag(name = "标签接口")
public class MiniAccountTagController {


    @Autowired
    private IMiniAccountTagService miniAccountTagService;

    /**
     * 查询所有标签
     *
     * @return Result<List<TagVo>>
     */
    @GetMapping
    @Operation(summary = "查询所有标签")
    public Result<List<TagVo>> listAll() {
        List<TagVo> tagVos = miniAccountTagService.listAll();
        return Result.ok(tagVos);
    }

    /**
     * 设置用户标签(添加|删除|设置)
     *
     * @param userTagDto 用户标签信息
     * @return Result<Void>
     */
    @PostMapping
    @Operation(summary = "设置用户标签(添加|删除|设置)")
    public Result<Void> settingUserTag(@RequestBody UserTagDto userTagDto) {
        miniAccountTagService.settingUserTag(userTagDto);
        return Result.ok();
    }

}
