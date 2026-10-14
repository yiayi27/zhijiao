package com.medusa.gruul.integral.web.controller.manager;


import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.service.IIntegralShopService;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralShopDto;
import com.medusa.gruul.integral.web.model.vo.IntegralShopVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 积分商城表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("/manager/shop")
@Tag(name = "积分商城接口")
public class IntegralShopController {
    @Autowired
    private IIntegralShopService integralShopService;


    /**
     * 修改店铺状态(关闭或开启积分商城)
     *
     * @param status 状态值
     * @return
     */
    @GetMapping("update/status")
    @Operation(summary = "积分商城开启关闭")
    public Result<Void> updateIntegralShopStatus(@Parameter(description = "状态值", required = true) Boolean status) {
        integralShopService.updateIntegralShopStatus(ISystem.shopId(), status);
        return Result.ok();
    }


    /**
     * 查看店铺信息 (店铺状态)
     *
     * @return 店铺信息
     */
    @GetMapping("get/shopId")
    @Operation(summary = "商城信息")
    public Result<IntegralShopVo> getIntegralShopInfo() {
        String shopId = ISecurity.userMust().getShopId();
        IntegralShopVo integralShopInfo = integralShopService.getIntegralShopInfo(shopId);
        if (BeanUtil.isEmpty(integralShopInfo)) {
            throw new ServiceException("商城信息不存在 !", SystemCode.DATA_NOT_EXIST.getCode());
        }
        return Result.ok(integralShopInfo);
    }


    /**
     * 默认新增商铺默认值
     *
     * @param integralShopDto
     * @return
     */
    @PostMapping("add")
    @Operation(summary = "新增商城默认值 --未对接")
    public Result<Void> saveShopInfo(@RequestBody IntegralShopDto integralShopDto) {
        integralShopService.addShopInfo(integralShopDto);
        return Result.ok();
    }


}
