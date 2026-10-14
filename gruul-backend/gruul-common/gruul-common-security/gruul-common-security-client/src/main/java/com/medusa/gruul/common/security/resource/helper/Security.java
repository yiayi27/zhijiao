package com.medusa.gruul.common.security.resource.helper;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Role;
import com.medusa.gruul.common.security.resource.extension.DelegateUserRolePermMatcher;
import com.medusa.gruul.common.security.resource.extension.RolePermConsumer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;
import java.util.function.Function;

/**
 * request 作用域 ，不是线程安全的, 避免在多线程下使用同一个bean
 *
 * @author 张治保
 * date 2022/2/25
 */
@Component("S")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public final class Security extends DelegateUserRolePermMatcher {
    /**
     * 获取用户角色集合 func
     */
    public static final Function<SecureUser<?>, Set<Role>> ROLES = SecureUser::getRoles;
    /**
     * 获取用户副角色集合 func
     */
    public static final Function<SecureUser<?>, Set<Role>> SUB_ROLES = SecureUser::getSubRoles;
    /**
     * 获取用户权限集合 func
     */
    public static final Function<SecureUser<?>, Set<String>> PERMS = SecureUser::getPerms;

    /**
     * 构造初始化 ,从上下文中获取用户权限信息
     */
    public Security() {
        super(ISecurity.matcher());
    }

    /**
     * IRolePermMatcher 消费器 用于给 springEl表达式使用
     *
     * @return RolePermConsumer 消费器
     */
    public RolePermConsumer link() {
        return new RolePermConsumer();
    }

    /**
     * 是否已认证
     */
    public boolean isAuthenticated() {
        return ISecurity.isAuthenticated();
    }

    /**
     * 管理员直接放行  自定义管理员有权限放行
     *
     * @param perms 权限列表
     * @return 是否有访问权限
     */
    public boolean hasPerm(String... perms) {
        return delegate.role(Role.ADMIN)
                .or(matcher -> matcher.role(Role.SUB_ADMIN).anyPerm(perms))
                .match();
    }

    /**
     * 管理员或消费者直接放行  自定义管理员有权限放行
     *
     * @param perms 权限列表
     * @return 是否有访问权限
     */
    public boolean permOrConsumer(String... perms) {
        return delegate.anyRole(Role.ADMIN, Role.CONSUMER)
                .or(matcher -> matcher.role(Role.SUB_ADMIN).anyPerm(perms))
                .match();
    }

    /**
     * 是否是消费者角色
     *
     * @return 是否是消费者角色
     */
    public boolean isConsumer() {
        return delegate.role(Role.CONSUMER).match();
    }


}
