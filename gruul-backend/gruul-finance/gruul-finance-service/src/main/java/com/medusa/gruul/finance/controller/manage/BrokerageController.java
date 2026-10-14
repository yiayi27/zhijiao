package com.medusa.gruul.finance.controller.manage;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.finance.api.entity.FinanceTradeDetail;
import com.medusa.gruul.finance.api.entity.FinanceWithdrawalRecord;
import com.medusa.gruul.finance.model.SearchTradeDetailDto;
import com.medusa.gruul.finance.model.SearchWithdrawalRecordDto;
import com.medusa.gruul.finance.model.ShopOverviewVo;
import com.medusa.gruul.finance.model.TradeOverviewVo;
import com.medusa.gruul.finance.service.IFinanceTradeDetailService;
import com.medusa.gruul.finance.service.IFinanceWithdrawalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * PC端佣金规则设置 前端控制器
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Slf4j
@RestController
@RequestMapping("/manage")
@Tag(name = "PC端佣金")
public class BrokerageController {

    @Resource
    private IFinanceTradeDetailService financeTradeDetailService;
    @Resource
    private IFinanceWithdrawalRecordService financeWithdrawalRecordService;

    /**
     * 店铺实时概况
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the overview
     */
    @Operation(summary = "店铺实时概况")
    @GetMapping("/overview")
    public Result<ShopOverviewVo> getOverview(
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        ShopOverviewVo vo = financeTradeDetailService.getOverview(startTime, endTime);
        return Result.ok(vo);
    }

    /**
     * 对账单概览
     *
     * @param dto the dto
     * @return the trade overview
     */
    @Operation(summary = "对账单概览")
    @GetMapping("/trade")
    public Result<TradeOverviewVo> getTradeOverview(SearchTradeDetailDto dto) {
        TradeOverviewVo vo = financeTradeDetailService.getTradeOverview(dto);
        return Result.ok(vo);
    }


    /**
     * 对账单流水记录
     *
     * @param dto the dto
     * @return the trade detail
     */
    @Operation(summary = "对账单流水记录")
    @GetMapping("/trade/detail")
    public Result<PageUtils<FinanceTradeDetail>> getTradeDetail(SearchTradeDetailDto dto) {
        PageUtils<FinanceTradeDetail> pageUtils = financeTradeDetailService.getTradeDetailPage(dto);
        return Result.ok(pageUtils);
    }


//=============================== 佣金=======================================================

    /**
     * 申请提现列表
     *
     * @param dto the dto
     * @return the withdrawal record page
     */
    @Operation(summary = "申请提现列表")
    @GetMapping("/withdrawal")
    public Result<PageUtils<FinanceWithdrawalRecord>> getWithdrawalRecordPage(SearchWithdrawalRecordDto dto) {
        PageUtils<FinanceWithdrawalRecord> pageUtils = financeWithdrawalRecordService.searchWithdrawalRecordPage(dto);
        return Result.ok(pageUtils);
    }


    /**
     * 单条提现详情
     *
     * @param id the id
     * @return the trade detail info
     */
    @Operation(summary = "单条提现详情")
    @GetMapping("/withdrawal/info/{id}")
    public Result<FinanceWithdrawalRecord> getTradeDetailInfo(@PathVariable(value = "id") @NotNull Long id) {
        FinanceWithdrawalRecord withdrawalRecord = financeWithdrawalRecordService.getById(id);
        return Result.ok(withdrawalRecord);
    }

    /**
     * 通过提现申请
     *
     * @param id      the id
     * @param tradeNo the trade no
     * @return the result
     */
    @Operation(summary = "通过提现申请")
    @PutMapping("/withdrawal/pass")
    public Result<Void> passWithdrawal(@Parameter(name = "id", description = "工单ID", example = "0")
                                 @RequestParam(value = "id") Long id,
                                 @Parameter(name = "tradeNo", description = "交易流水号，不是微信渠道时必填", example = "0")
                                 @RequestParam(value = "tradeNo", required = false) String tradeNo,
                                 @Parameter(name = "channel", description = "结算类型 3->线下结算 只有当线下结算是参数才需要传递", example = "3")
                                 @RequestParam(value = "channel", required = false) Integer channel) {
        financeWithdrawalRecordService.passWithdrawal(id, tradeNo, channel);
        return Result.ok();
    }


    /**
     * 拒绝提现申请
     *
     * @param id     the id
     * @param reason the reason
     * @return the result
     */
    @Operation(summary = "拒绝提现申请")
    @PutMapping("/withdrawal/reject")
    public Result<Void> rejectWithdrawal(@Parameter(name = "id", description = "工单ID", example = "0") @RequestParam(value = "id") Long id,
                                   @Parameter(name = "reason", description = "拒绝说明", example = "账户异常") @RequestParam(value =
                                           "reason") String reason) {
        financeWithdrawalRecordService.rejectWithdrawal(id, reason);
        return Result.ok();
    }


    /**
     * 批量备注订单
     *
     * @param ids  the ids
     * @param over the over
     * @param note the note
     * @return the result
     */
    @Operation(summary = "批量备注订单")
    @PutMapping("/withdrawal/note")
    public Result<Void> noteWithdrawal(@RequestBody Long[] ids, @RequestParam(value = "over") Boolean over,
                                 @RequestParam(value = "note") String note) {
        financeWithdrawalRecordService.noteWithdrawal(Arrays.asList(ids), note, over);
        return Result.ok();
    }


}
