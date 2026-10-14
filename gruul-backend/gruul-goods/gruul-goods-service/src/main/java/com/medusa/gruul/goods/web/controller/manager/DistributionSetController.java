package com.medusa.gruul.goods.web.controller.manager;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;
import com.medusa.gruul.goods.service.manager.IDistributionSetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 产品分销设置
 * </p>
 *
 * @author lcysike
 * @since 2023-10-01
 */
@RestController
@RequestMapping("/manager/distribution/set")
public class DistributionSetController {

    @Autowired
    private IDistributionSetService distributionSetService;

    /**
     * 根据产品id查询产品分销列表
     */
    @GetMapping("/list/{productId}")
    @Operation(summary = "产品分销列表")
    public Result<List<DistributionSetVo>> selectByProductId(@Parameter(description = "产品id", required = true) @PathVariable("productId") Long productId) {
        List<DistributionSetVo> list = distributionSetService.getByProductId(productId);
        return Result.ok(list);
    }

}
