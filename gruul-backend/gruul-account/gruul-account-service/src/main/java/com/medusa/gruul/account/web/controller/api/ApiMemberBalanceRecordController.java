package com.medusa.gruul.account.web.controller.api;


import com.medusa.gruul.account.api.model.MemberBalanceUpdateDto;
import com.medusa.gruul.account.model.vo.MemberBalanceRecordResVo;
import com.medusa.gruul.account.service.IMemberBalanceRecordService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 会员结算信息控制层
 * @Author zhaokw
 * @Date 2024\7\4
 **/
@RestController
@RequestMapping("/api")
public class ApiMemberBalanceRecordController {

    @Autowired
    private IMemberBalanceRecordService memberBalanceRecordService;

    /**
     * 获取会员余额明细
     *
     * @param yearMonth    年月
     * @param userShopId 会员id
     * @param page         指定页数
     * @param size         数据条数
     * @param dealType     交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现
     * @return Result<MemberBalanceRecordResVo>
     */
    @GetMapping("/getMemberBalanceRecord")
    @Operation(summary = "小程序端获取会员余额明细信息")
    @PreAuthorize("permitAll()")
    public Result<MemberBalanceRecordResVo> getMemberBalanceRecord(
            @Parameter(description = "指定页数", required = true) @RequestParam Integer page,
            @Parameter(description = "数据条数", required = true) @RequestParam Integer size,
            @Parameter(name = "yearMonth", description = "年月") @RequestParam(required = false) String yearMonth,
            @Parameter(name = "memberInfoId", description = "会员id") @RequestParam(required = false) String userShopId,
            @Parameter(name = "dealType", description = "交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现") @RequestParam(required = false) Integer dealType
    ) {
        MemberBalanceRecordResVo memberBalanceRecords = memberBalanceRecordService.getMemberBalanceRecord(yearMonth, userShopId, page, size, dealType);
        return Result.ok(memberBalanceRecords);
    }

    /**
     * 用户删除指定余额明细id
     *
     * @param id 余额明细id
     * @return Result<Void>
     */
    @DeleteMapping("/memberBalanceRecord/{id}")
    @Operation(summary = "用户删除余额明细")
    @PreAuthorize("permitAll()")
    public Result<Void> removeMemberBalanceRecord(
            @Parameter(description = "用户余额明细id", required = true) @PathVariable(name = "id") Long id
    ) {
        memberBalanceRecordService.removeMemberBalanceRecord(id);
        return Result.ok();
    }

    /**
     * 会员余额变更
     *
     * @param memberBalanceUpdateDto 扣除余额与余额明细
     * @return Result<Boolean>
     */
    @PostMapping("/memberBalanceModify")
    @Operation(summary = "测试扣退款会员余额变更")
    @PreAuthorize("permitAll()")
    public Result<Boolean> memberBalanceModify(@RequestBody @Validated MemberBalanceUpdateDto memberBalanceUpdateDto) {
        Boolean flag = memberBalanceRecordService.memberBalanceModify(memberBalanceUpdateDto);
        return Result.ok(flag);
    }


}
