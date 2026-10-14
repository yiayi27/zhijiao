package com.medusa.gruul.shops.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 商铺热搜词汇信息
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Schema(title = "ShopsSearchTermsParam 实体", description = "ShopsSearchTermsParam 实体")
public class ShopsSearchTermsParam extends AbstractQueryParam {


    @Schema(title = "id")
    private Long id;


    /**
     * 词语 多个以英文逗号分隔
     */
    @Schema(title = "热搜词语 多个以英文逗号分隔")
    private String terms;


    /**
     * 默认 搜索词语
     */
    @Schema(title = "默认 搜索词语")
    private String defTerms;


    /**
     * 是否显示 0否 1是
     */
    @Schema(title = " 是否显示 0否 1是")
    private String isShow;


    /**
     * 逻辑删除标识  0正常 1已删除
     */
    @Schema(title = "逻辑删除标识  0正常 1已删除")
    private String isDeleted;

}
