package com.medusa.gruul.common.security.model.constant;

/**
 * 常量
 *
 * @author 张治保
 * date 2022/4/19
 */
public interface SecureConst {

    /**
     * 默认的用具昵称前缀
     */
    String DEFAULT_USER_NICKNAME_PREFIX = "U";

    /**
     * 用户登录检查 最后一个参数为shopId 店铺id
     */
    String USER_LOGIN_STATE_CHECK = "gruul:mall:uaa:login:user";


}
