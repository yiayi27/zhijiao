package com.medusa.gruul.finance.controller.mini;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.finance.api.entity.FinanceWithdrawalRecord;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import com.medusa.gruul.finance.model.ApiFinanceAccountVo;
import com.medusa.gruul.finance.model.ApiWithdrawalBrokerageDto;
import com.medusa.gruul.finance.model.ApiWithdrawalRecordDto;
import com.medusa.gruul.finance.service.IFinanceAccountService;
import com.medusa.gruul.finance.service.IFinanceWithdrawalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 资金账户 前端控制器
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Slf4j
@RestController
@RequestMapping("/mini")
@Tag(name = "小程序端财务模块")
public class MiniFinanceController {
    @Resource
    private IFinanceAccountService financeAccountService;
    @Resource
    private IFinanceWithdrawalRecordService financeWithdrawalRecordService;


    /**
     * 用户概况
     *
     * @param accountType the account type
     * @param userId      the user id
     * @return the account info
     */
    @Operation(summary = "用户概况")
    @GetMapping("/account/info")
    public Result<ApiFinanceAccountVo> getAccountInfo(
            @Parameter(name = "accountType", description = "用户类型") @RequestParam(value = "accountType") AccountTypeEnum accountType,
            @Parameter(name = "userId", description = "对应的ID", example = "0") @RequestParam(value = "userId") String userId) {
        ApiFinanceAccountVo vo = financeAccountService.getFinanceAccountVo(accountType, userId);
        return Result.ok(vo);
    }


    /**
     * 获取最后一次申请数据
     *
     * @param accountType the account type
     * @param userId      the user id
     * @return the result
     */
    @Operation(summary = "获取最后一次申请的数据")
    @GetMapping("/withdrawal/last")
    public Result<FinanceWithdrawalRecord> lastWithdrawal(
            @Parameter(name = "accountType", description = "用户类型") @RequestParam(value = "accountType") AccountTypeEnum accountType,
            @Parameter(name = "userId", description = "对应的ID", example = "0") @RequestParam(value = "userId") String userId) {
        FinanceWithdrawalRecord record = financeWithdrawalRecordService.getLastWithdrawal(accountType, userId);
        return Result.ok(record);
    }

    /**
     * 申请提现
     *
     * @param dto the dto
     * @return the result
     */
    @Operation(summary = "申请提现")
    @PostMapping("/withdrawal")
    @PreAuthorize("@S.consumer")
    public Result<Void> withdrawal(@RequestBody @Validated ApiWithdrawalBrokerageDto dto) {
        financeWithdrawalRecordService.withdrawal(dto);
        return Result.ok();
    }

    /**
     * 申请提现列表
     *
     * @param dto the dto
     * @return the withdrawal record
     */
    @Operation(summary = "申请提现的列表")
    @GetMapping("/withdrawal")
    public Result<PageUtils<FinanceWithdrawalRecord>> getWithdrawalRecord(ApiWithdrawalRecordDto dto) {
        PageUtils<FinanceWithdrawalRecord> pageUtils = financeWithdrawalRecordService.getWithdrawalRecord(dto);
        return Result.ok(pageUtils);
    }


}
