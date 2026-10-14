package com.medusa.gruul.account.web.controller;


import com.medusa.gruul.account.model.vo.MemberOrderInfoResVo;
import com.medusa.gruul.account.model.vo.UpdateOrderNoteVo;
import com.medusa.gruul.account.service.IMemberOrderInfoService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author zhaokw
 * @Date 00:39 2024\6\13 0013
 **/
@RestController
@RequestMapping("/manager")
public class MemberOrderInfoController {

    @Autowired
    private IMemberOrderInfoService memberOrderInfoService;

    /**
     * pc端获取会员储值订单信息
     *
     * @param page         指定页数
     * @param size         数据条数
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param searchContent 搜索内容
     * @return Result<PageUtils<List<MemberOrderInfoResVo>>>
     */
    @GetMapping("/getMemberOrderInfo")
    @Operation(summary = "pc端获取会员储值订单信息")
    public Result<PageUtils<List<MemberOrderInfoResVo>>> getMemberOrderInfo(
            @Parameter(description = "指定页数", required = true) @RequestParam Integer page,
            @Parameter(description = "数据条数", required = true) @RequestParam Integer size,
            @Parameter(name = "startTime", description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(name = "endTime", description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(name = "searchContent", description = "搜索内容") @RequestParam(required = false) String searchContent
    ) {
        PageUtils<List<MemberOrderInfoResVo>> memberOrderInfoResVos = memberOrderInfoService.getMemberOrderInfo(startTime, endTime, page, size, searchContent);
        return Result.ok(memberOrderInfoResVos);
    }

    /**
     * 更新储值订单备注
     *
     * @param updateOrderNoteVo 储值订单备注信息
     * @return Result<Void>
     */
    @PostMapping("/note")
    @Operation(summary = "更新储值订单备注")
    public Result<Void> updateNote(@RequestBody @Validated UpdateOrderNoteVo updateOrderNoteVo) {
        memberOrderInfoService.updateNote(updateOrderNoteVo);
        return Result.ok();
    }


}
