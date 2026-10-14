package com.medusa.gruul.platform.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author 张治保
 * @since 2025/4/10
 */
public enum ShopAdminAvailable {

    /**
     * 需要密码
     */
    @Schema(title = "需要密码")
    PASSWORD,

    /**
     * 账号已在系统中存在 不需要密码
     */
    @Schema(title = "账号已在系统中存在 不需要密码")
    NO_PASSWORD,

    /**
     * 已经绑定过管理员，无法再次绑定
     */
    @Schema(title = "已经绑定过管理员，无法再次绑定")
    BOUNDED,


}
