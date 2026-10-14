package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description 更新储值订单备注
 * @Author zhaokw
 * @Date 00:42 2024\8\1 0007
 **/
@Data
@Schema(title = "更新储值订单备注")
@Accessors(chain = true)
public class UpdateOrderNoteVo {

    @Schema(title = "id")
    private Long id;

    /**
     * 备注
     */
    @Schema(title = "备注")
    private String note;

    /**
     * 是否覆盖 0:不覆盖；1：覆盖
     */
    @Schema(title = "是否覆盖 0:不覆盖；1：覆盖")
    private Integer state;


}
