package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 会员权益组关联字段
 * @Author zhaokw
 * @Date 21:19 2024\6\4 0015
 **/
@Data
@Schema(title = "会员权益组关联字段")
@Accessors(chain = true)
public class MemberCardPrivilegeGroupVo {

    @Schema(title = "权益组id")
    private Long id;

    @Schema(title = "权益id")
    private Long privilegeId;

    @Schema(title = "会员卡id")
    private Long memberCardId;

    @Schema(title = "计算值")
    private BigDecimal value;

    @Schema(title = "是否删除字段")
    private Integer isDeleted;

    @Schema(title = "更新时间")
    private LocalDateTime updateTime;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "店铺id")
    private String shopId;

}
