package com.medusa.gruul.finance.model;


import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Search withdrawal record by area ass dto.
 *
 * @description: SearchWithdrawalRecordDto.java
 * @author: alan
 * @date: 2024/2/22 20:53
 */
@Data
@Schema(title = "PC端查看提现申请")
public class SearchWithdrawalRecordByAreaAssDto extends AbstractQueryParam {


    @Schema(title = "开始时间：2024-02-22 00:00:00，为空默认为本月开始时间")
    private String startTime;


    @Schema(title = "结束时间：2024-02-22 23:59:59，为空默认为本月结束时间")
    private String endTime;


    @Schema(title = "审核状态:1->待审核；2->已通过；3->已拒绝")
    private Integer auditStatus;


    @Schema(title = "userId")
    private String userId;


}
