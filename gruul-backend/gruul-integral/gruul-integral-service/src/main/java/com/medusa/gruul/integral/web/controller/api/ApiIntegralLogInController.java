package com.medusa.gruul.integral.web.controller.api;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.service.IIntegralEnterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * <p>
 * 积分登入表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-12-31
 */

@RestController
@RequestMapping("enter")
@Tag(name = "用户登入相关接口")
public class ApiIntegralLogInController {

    @Autowired
    private IIntegralEnterService integralEnterService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 用户进行登入 修改登入状态并生成积分明细记录
     *
     * @return 用户登入获得的积分
     */
    @Operation(summary = "用户登入,获取积分")
    @PostMapping("updateStatus")
    @PreAuthorize("@S.consumer")
    public Result<BigDecimal> updateRunningDays() {
        SecureUser<?> feignCurUser = ISecurity.userMust();

        String userId = feignCurUser.getId();
        // 增加分布式锁
        RLock rLock = redissonClient.getLock(userId);
        rLock.lock();
        BigDecimal bigDecimal;
        try {
            bigDecimal = integralEnterService.userLogin(userId);
        } finally {
            //释放锁
            rLock.unlock();
        }
        return Result.ok(bigDecimal);
    }
}
