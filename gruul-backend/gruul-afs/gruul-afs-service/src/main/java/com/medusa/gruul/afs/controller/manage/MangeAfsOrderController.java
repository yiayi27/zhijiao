package com.medusa.gruul.afs.controller.manage;


import com.medusa.gruul.afs.model.*;
import com.medusa.gruul.afs.service.IAfsOrderService;
import com.medusa.gruul.afs.service.IManageAfsOrderService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 售后工单 前端控制器
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
@RestController
@Tag(name = "PC端接口")
@RequestMapping("/manage")
public class MangeAfsOrderController {
    @Resource
    private IManageAfsOrderService manageAfsOrderService;
    @Resource
    private IAfsOrderService afsOrderService;

    /**
     * 商家售后列表查看
     *
     * @param dto 售后工单信息
     * @return Result<PageUtils<ManageAfsOrderVo>>
     */
    @Operation(summary = "商家售后列表查看")
    @GetMapping("/list")
    public Result<PageUtils<ManageAfsOrderVo>> list(SearchDto dto) {
        PageUtils<ManageAfsOrderVo> page = manageAfsOrderService.searchManageAfsOrderVoPage(dto);
        return Result.ok(page);
    }

    /**
     * 商家拒绝申请
     *
     * @param dto 售后工单信息
     * @return Result<Void>
     */
    @Operation(summary = "商家拒绝申请")
    @PostMapping("/seller/refusal")
    public Result<Void> sellerRefusal(@RequestBody @Validated SellerRefusalDto dto) {
        manageAfsOrderService.sellerRefuse(dto);
        return Result.ok();
    }

    /**
     * 商家同意申请
     *
     * @param afsId 售后工单id
     * @return Result<Void>
     */
    @Operation(summary = "商家同意申请")
    @PutMapping("/seller/approve")
    public Result<Void> sellerApprove(@RequestParam(value = "afsId") @NotNull Long afsId) {
        manageAfsOrderService.sellerApprove(afsId, false);
        return Result.ok();
    }

    /**
     * 商家批量备注订单
     *
     * @param dto 售后工单信息
     * @return Result<Void>
     */
    @Operation(summary = "商家批量备注订单", description = "商家批量备注订单")
    @PostMapping("/note")
    public Result<Void> note(@RequestBody @Validated SellerNoteDto dto) {
        manageAfsOrderService.note(dto);
        return Result.ok();
    }

    /**
     * 根据售后单ID获取售后详情
     *
     * @param afsId 售后工单id
     * @return Result<ApiAfsOrderVo>
     */
    @Operation(summary = "根据售后单ID获取售后详情")
    @GetMapping("/info")
    public Result<AfsOrderVo> getAfsOrderInfo(@RequestParam(value = "afsId") @NotNull Long afsId) {
        AfsOrderVo vo = afsOrderService.getAfsOrderInfo(afsId);
        return Result.ok(vo);
    }

}
