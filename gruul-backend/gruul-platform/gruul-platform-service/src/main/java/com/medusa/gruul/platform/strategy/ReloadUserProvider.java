package com.medusa.gruul.platform.strategy;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Role;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.server.provider.IReloadUserProvider;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.common.system.model.Systems;
import com.medusa.gruul.platform.api.entity.AccountInfo;
import com.medusa.gruul.platform.service.IAccountInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author 张治保
 * @since 2023/11/16
 */
@Component
@RequiredArgsConstructor
public class ReloadUserProvider implements IReloadUserProvider {

    private final IAccountInfoService accountInfoService;

    /**
     * 根据用户id加载用户权限信息
     *
     * @param preUser 加载之前的用户认证资料
     * @return 用户信息
     */
    @Override
    public SecureUser<?> loadUser(SecureUser<?> preUser) {
        return loadUser(
                ISystem.clientType(),
                ISystem.shopId(),
                query -> query.eq(AccountInfo::getMobile, preUser.getMobile()),
                _ -> true
        );
    }

    /**
     * 加载用户资料信息
     *
     * @param clientType 客户端类型
     * @param shopId     店铺id
     * @param query      查询条件组装器
     * @param matcher    匹配器
     * @return 用户资料信息
     */
    public SecureUser<?> loadUser(
            ClientType clientType,
            String shopId,
            Consumer<LambdaQueryChainWrapper<AccountInfo>> query,
            Predicate<AccountInfo> matcher
    ) {
        //是否是消费者端登录
        boolean isConsumer = ClientType.CONSUMER == clientType;
        LambdaQueryChainWrapper<AccountInfo> queryWrapper = accountInfoService.lambdaQuery();
        query.accept(queryWrapper);
        AccountInfo user;
        //是否是默认的店铺id 表示目前未指定登录店铺
        boolean isDefaultShopId = Systems.DEFAULT_SHOP_ID.equals(shopId);
        if (isConsumer) {
            //消费者端登录 店铺id不能是 默认值
            if (isDefaultShopId) {
                throw SecureCodes.REQUEST_INVALID.exception();
            }
            user = queryWrapper.eq(AccountInfo::getShopId, shopId)
                    .one();
        } else {
            //非消费者端登录 找出一条为 管理员或子管理员的记录即可
            user = queryWrapper.isNotNull(AccountInfo::getRole)
                    .eq(!isDefaultShopId, AccountInfo::getShopId, shopId)
                    .last(CommonConstants.SQL_LIMIT_1)
                    .one();
        }
        if (user == null || !matcher.test(user)) {
            throw SecureCodes.USERNAME_PASSWORD_INVALID.exception();
        }
        SecureUser<Object> secureUser = new SecureUser<>()
                .setId(user.getId())
                .setClientType(clientType)
                .setShopId(shopId)
                .setMobile(user.getMobile())
                .setRoles(Set.of(isConsumer ? Role.CONSUMER : user.getRole()));
        if (isConsumer) {
            secureUser.setNickname(user.getNickname())
                    .setNickname(user.getNickname())
                    .setOpenid(user.getOpenid())
                    .setAvatar(user.getAvatar());
        } else {
            secureUser.setPerms(user.getPerms())
                    .setNickname(Role.ADMIN == user.getRole() ? Role.ADMIN.getDesc() : user.getAdminNickname());
        }
        secureUser.setOpen(
                Map.of(
                        "role", secureUser.getRoles().iterator().next(),
                        "perms", CollUtil.emptyIfNull(secureUser.getPerms())
                )
        );
        return secureUser;

    }

}
