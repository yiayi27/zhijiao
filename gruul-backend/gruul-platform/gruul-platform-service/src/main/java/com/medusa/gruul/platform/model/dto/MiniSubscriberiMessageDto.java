package com.medusa.gruul.platform.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @description
 * @date: 2024/5/23
 */
@Data
public class MiniSubscriberiMessageDto {


    @Schema(title = "模板标题")
    private String tId;

    /**
     * 消息标题
     */
    @Schema(title = "消息标题")
    private String title;

    /**
     * 模板消息id
     */
    @Schema(title = "模板消息id")
    private String templateId;


    /**
     * 开启状态 0-未使用  1-使用
     */
    @Schema(title = "开启状态 0-未使用  1-使用")
    private Integer useStatus;

    /**
     * kid按顺序分割,例子1,3,5
     */
    @Schema(title = "kid按顺序分割,例子1,3,5")
    private String kIds;

    /**
     * 参数规则名跟kid排序相对应amount1,thing3,thing5
     */
    @Schema(title = "参数规则名跟kid排序相对应amount1,thing3,thing5")
    private String rules;

    /**
     * 提供预览值
     */
    @Schema(title = "提供预览值")
    private String exampleJson;

    /**
     * 二级类目名称
     */
    @Schema(title = "二级类目名称")
    private String secondClass;


    /**
     * 推送规则
     */
    @Schema(title = "推送规则 ")
    private String sendRule;

}
