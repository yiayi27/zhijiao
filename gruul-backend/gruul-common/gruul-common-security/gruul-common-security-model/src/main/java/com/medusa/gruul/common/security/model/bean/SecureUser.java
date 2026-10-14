package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.security.model.enums.Role;
import com.medusa.gruul.common.system.model.ClientType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 安全用户 匿名用户 id为空
 *
 * @author 张治保
 * date 2022/2/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public final class SecureUser<T> implements Serializable {

    /**
     * 原始token
     * todo 目前用于在feign接口中传递token
     */
    private String token;

    /**
     * 用户id 匿名用户id为空
     */
    private String id;

    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * shopId
     */
    private String shopId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 微信 open id
     */
    private String openid;

    /**
     * 用户角色集合
     */
    private Set<Role> roles = Set.of();

    /**
     * 副角色
     */
    private Set<Role> subRoles = Set.of();

    /**
     * 用户权限集合
     */
    private Set<String> perms = Set.of();

    /**
     * 额外信息
     */
    private T extra;

    /**
     * 开放的信息 可用于展示给前端
     */
    private Map<String, Object> open = Map.of();

    /**
     * 是否是匿名用户
     *
     * @return true 是 false 不是
     */
    public boolean isAnonymous() {
        return id == null;
    }


    /**
     * 获取开放数据 并清空
     *
     * @return 开放数据
     */
    public Map<String, Object> open() {
        try {
            return open;
        } finally {
            this.setOpen(null);
        }
    }
}
