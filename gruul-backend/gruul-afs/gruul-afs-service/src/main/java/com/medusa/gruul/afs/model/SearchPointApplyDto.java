package com.medusa.gruul.afs.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Search point apply dto.
 *
 * @author alan
 */
@Data
public class SearchPointApplyDto extends AbstractQueryParam {

    @Schema(title = "售后编号，模糊搜索")
    private String no;

}
