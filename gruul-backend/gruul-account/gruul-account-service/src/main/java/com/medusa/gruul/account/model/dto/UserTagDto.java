package com.medusa.gruul.account.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author whh
 * @description
 * @date: 2023/11/20
 */
@Data
public class UserTagDto {

    @Schema(title = "添加标签数组")
    private List<TagDto> add;

    @Schema(title = "修改的标签数组")
    private List<TagDto> update;

    @Schema(title = "删除的标签数组")
    private List<TagDto> del;

    @Schema(title = "用户id数组")
    private List<String> userIds;
}
