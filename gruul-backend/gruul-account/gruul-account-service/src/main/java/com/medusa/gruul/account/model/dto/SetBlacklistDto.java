package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/11/20
 */
@Data
public class SetBlacklistDto {

    @Schema(title = "用户id数组")
    @NotNull(message = "未选择用户")
    private List<String> userIds;

    @Schema(title = "操作类别 1-加入 2-移除 3-修改")
    @Range(min = 1, max = 3, message = "操作列表错误")
    private Integer option;

    @Schema(title = "限制类型 1-限制下单,2-限制评论")
    private List<Integer> rejectInteger;

}
