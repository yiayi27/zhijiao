package com.medusa.gruul.goods.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商查询参数
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "SupplierParam对象", description = "供应商查询参数")
public class SupplierParam extends AbstractQueryParam {
    private static final long serialVersionUID = 1;

    /**
     * 供应商识别号
     */
    @Schema(title = "供应商识别号")
    private String supplierSn;

    /**
     * 供应商名称
     */
    @Schema(title = "供应商名称")
    private String name;

    /**
     * 手机号码
     */
    @Schema(title = "手机号码")
    private String mobile;

    /**
     * 状态(0--已关闭，1--已审核，2--待审核，3--禁用中)
     */
    @Schema(title = "状态(0--已关闭，1--已审核，2--待审核，3--禁用中)")
    private Integer status;
}