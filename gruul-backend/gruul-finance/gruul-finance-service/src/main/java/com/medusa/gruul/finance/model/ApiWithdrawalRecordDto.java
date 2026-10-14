package com.medusa.gruul.finance.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Api withdrawal record dto.
 *
 * @description: PC端佣金提现记录查询
 * @author: alan
 * @date: 2023/11/5 21:29
 */
@Data
@Schema(title = "PC端佣金提现记录查询")
public class ApiWithdrawalRecordDto extends AbstractQueryParam {
    private static final long serialVersionUID = 1L;


    /**
     * 结果类型：0->全部记录；1->到账记录
     */
    @Schema(title = "结果类型：0->全部记录；1->到账记录", example = "0")
    private Integer type;

    /**
     * 对应类型的用户ID
     */
    @Schema(title = "对应类型的用户ID")
    private String userId;


}
