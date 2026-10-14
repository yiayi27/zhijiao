package com.medusa.gruul.account.model.vo;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoq
 * @date 2024/2/28 12:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "AccountFootMarkParam对象", description = "分页查询参数")
public class AccountFootMarkParam extends AbstractQueryParam {


    private static final long serialVersionUID = 1L;

    private String userId;
}
