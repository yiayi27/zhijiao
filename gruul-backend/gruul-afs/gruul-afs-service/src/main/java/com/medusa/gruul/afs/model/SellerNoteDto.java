package com.medusa.gruul.afs.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * The type Seller note dto.
 *
 * @author alan
 * @description: SellerNoteDto.java
 * @date 2024/8/5 22:41
 */
@Data
@Schema(title = "商家备注的参数")
public class SellerNoteDto {

    @NotEmpty(message = "备注的订单不能为空")
    @Schema(title = "选择的售后ID")
    private List<Long> afsIds;

    @Schema(title = "是否覆盖")
    private Boolean over;

    @Schema(title = "备注")
    private String note;
}
