package com.medusa.gruul.common.security.resource.model;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public final class SecureUserAuthentication implements Authentication {
    /**
     * token id
     */
    private String tokenId;

    /**
     * 用户信息
     */
    private SecureUser<?> user;

    /**
     * 创建一个已认证对象
     *
     * @param tokenId token id
     * @param user    用户信息
     * @return 已认证对象
     */
    public static SecureUserAuthentication of(String tokenId, SecureUser<?> user) {
        return new SecureUserAuthentication(tokenId, user);
    }

    public String tokenId() {
        return tokenId;
    }

    public SecureUser<?> user() {
        return user;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    @JsonIgnore
    public Object getCredentials() {
        return StrUtil.EMPTY;
    }

    @Override
    @JsonIgnore
    public Object getDetails() {
        return StrUtil.EMPTY;
    }

    @Override
    @JsonIgnore
    public Object getPrincipal() {
        return StrUtil.EMPTY;
    }

    @Override
    @JsonIgnore
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    @JsonIgnore
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    @JsonIgnore
    public String getName() {
        return StrUtil.EMPTY;
    }
}
