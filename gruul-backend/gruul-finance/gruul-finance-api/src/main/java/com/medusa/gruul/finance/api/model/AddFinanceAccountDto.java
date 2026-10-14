package com.medusa.gruul.finance.api.model;

import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Add finance account dto.
 *
 * @Description: AddFinanceAccountDto.java
 * @Author: alan
 * @Date: 2024/3/1 19:40
 */
@NoArgsConstructor
@Data
public class AddFinanceAccountDto {
    @Schema(title = "商铺ID")
    private String shopId;
    @Schema(title = "微信UserID")
    private String userId;
    @Schema(title = "帐户类型")
    private AccountTypeEnum type;
    @Schema(title = "对应类型的ID")
    private String id;
}
