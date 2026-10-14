package com.medusa.gruul.account.web.controller;

import com.medusa.gruul.account.model.vo.AggregateVo;
import com.medusa.gruul.account.service.IApiMiniAccountCollectService;
import com.medusa.gruul.account.service.IApiMiniAccountFootMarkService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.integral.api.model.UserInfoVo;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import com.medusa.gruul.order.api.model.OrderOverviewVo;
import com.medusa.gruul.shops.api.feign.RemoteShopsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 聚合接口
 *
 * @author whh
 * @description
 * @date: 2024/5/13
 */
@RestController
@RequestMapping("/aggregate")
@Tag(name = "聚合接口")
@Log4j2
public class AggregateController {

    @Autowired
    private RemoteOrderService remoteOrderService;
    @Autowired
    private IApiMiniAccountCollectService miniAccountCollectService;
    @Autowired
    private IApiMiniAccountFootMarkService miniAccountFootMarkService;
    @Autowired
    private RemoteShopsService remoteShopsService;
    @Autowired
    private RemoteIntegralService remoteIntegralService;

    /**
     * 首页聚合接口
     *
     * @return Result<AggregateVo>
     */
    @GetMapping(value = "/more")
    @PreAuthorize("permitAll()")
    @Operation(summary = "首页聚合接口")
    public Result<AggregateVo> getCommanderInfo() {
        Optional<SecureUser<Object>> userOpt = ISecurity.userOpt();
        if (userOpt.isEmpty()) {
            return Result.ok(
                    new AggregateVo().setOrderOverviewVo(new OrderOverviewVo().setAfsOrder(CommonConstants.LONG_ZERO).setShipped(CommonConstants.LONG_ZERO).setWaitForPay(CommonConstants.LONG_ZERO).setWaitForPickup(CommonConstants.LONG_ZERO).setWithDelivery(CommonConstants.LONG_ZERO))
                            .setCollectCount(CommonConstants.NUMBER_ZERO)
                            .setAccountFootMarkCount(CommonConstants.NUMBER_ZERO)
                            .setAccountCenterVo(remoteShopsService.accountCenterSetting())
                            .setShopFunctionInfoVo(remoteIntegralService.getShoppingFunctionInfo(ISystem.shopId()))
                            .setUserInfoVo(
                                    new UserInfoVo()
                                            .setIntegral(BigDecimal.ZERO).
                                            setNickName(null).setAvatarUrl("https://tse1-mm.cn.bing.net/th/id/R-C.15c0b1d32d2fdc0eec1d79e8f3e0e419?rik=r9QOLHA3RWvMwA&riu=http%3a%2f%2fimg.duoziwang.com%2f2023%2f05%2f08050202206347.jpg&ehk=Z8x%2f2MI%2f89G2IGwzEzacYpz5aAagXwDRo5Ia0zBVKLs%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1")
                            )
            );
        }
        SecureUser<Object> httpCurUser = userOpt.get();
        AggregateVo vo = new AggregateVo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new ServiceException("数据异常");
        }
        ServletRequestAttributes att = (ServletRequestAttributes) requestAttributes;
        //将RequestAttributes对象设置为子线程共享
        RequestContextHolder.setRequestAttributes(att, true);
        //获取用户中心
        vo.setAccountCenterVo(remoteShopsService.accountCenterSetting());
        //用户订单数据聚合
        vo.setShopFunctionInfoVo(remoteIntegralService.getShoppingFunctionInfo(httpCurUser.getShopId()));
        vo.setOrderOverviewVo(remoteOrderService.orderOverview(httpCurUser.getId()));
        vo.setUserInfoVo(remoteIntegralService.getUserInfo(httpCurUser.getId())
                .setAvatarUrl(ISecurity.userMust().getAvatar())
                .setNickName(ISecurity.userMust().getNickname()));
        //获取用户 收藏数量
        vo.setCollectCount(miniAccountCollectService.getCollectCount());
        //获取用户足迹数量
        vo.setAccountFootMarkCount(miniAccountFootMarkService.getAccountFootMarkCount());
        return Result.ok(vo);

    }
}
