package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.UserMatch;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 委派模式的用户角色匹配器
 *
 * @author 张治保
 * @since 2025/1/15
 */
@RequiredArgsConstructor
public class DelegateUserRolePermMatcher implements IUserRolePermMatcher {

    /**
     * 委派实例
     */
    protected final IUserRolePermMatcher delegate;

    @Override
    public final <T> SecureUser<T> getUser() {
        return delegate.getUser();
    }

    @Override
    public final boolean match() {
        return delegate.match();
    }

    @Override
    public final UserMatch userMatch() {
        return delegate.userMatch();
    }

    @Override
    public final IUserRolePermMatcher anonymous() {
        return delegate.anonymous();
    }

    @Override
    public final <T> IUserRolePermMatcher neq(Function<SecureUser<?>, Set<T>> property, T condition) {
        return delegate.neq(property, condition);
    }

    @Override
    public final <T> IUserRolePermMatcher eq(Function<SecureUser<?>, Set<T>> property, T condition) {
        return delegate.eq(property, condition);
    }

    @SafeVarargs
    @Override
    public final <T> IUserRolePermMatcher any(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        return delegate.any(property, conditions);
    }

    @SafeVarargs
    @Override
    public final <T> IUserRolePermMatcher all(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        return delegate.all(property, conditions);
    }

    @Override
    public final IUserRolePermMatcher and(Consumer<IRolePermMatcher<?>> and) {
        return delegate.and(and);
    }

    @Override
    public final IUserRolePermMatcher or(Consumer<IRolePermMatcher<?>> or) {
        return delegate.or(or);
    }

    @Override
    public final IUserRolePermMatcher custom(Function<IRolePermMatcher<?>, Boolean> custom) {
        return delegate.custom(custom);
    }
}
