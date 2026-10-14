package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2023/12/6
 */
@Data
public class UserBlacklistAstrictVo {

    @Schema(title = "是否限制下单  1-未限制 2-已限制")
    private Boolean rejectOrder;

    @Schema(title = "是否限制评论  1-未限制 2-已限制")
    private Boolean rejectComment;

}
