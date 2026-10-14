package com.medusa.gruul.afs.controller;


import com.medusa.gruul.afs.api.entity.AfsNegotiateHistory;
import com.medusa.gruul.afs.model.NegotiateHistoryDto;
import com.medusa.gruul.afs.service.IAfsNegotiateHistoryService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 协商历史 前端控制器
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
@RestController
@Tag(name = "协商历史接口")
@RequestMapping("/negotiate")
public class AfsNegotiateHistoryController {
    @Resource
    private IAfsNegotiateHistoryService afsNegotiateHistoryService;

    /**
     * 获取订单的售后协商历史
     *
     * @param dto the dto
     * @return the result
     */
    @Operation(summary = "获取订单的售后协商历史")
    @GetMapping("/history")
    public Result<List<AfsNegotiateHistory>> negotiateHistoryList(NegotiateHistoryDto dto) {
        List<AfsNegotiateHistory> historyList = afsNegotiateHistoryService.negotiateHistoryList(dto);
        return Result.ok(historyList);

    }

}
