package com.medusa.gruul.common.core.param;

import com.medusa.gruul.common.core.constant.CommonConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 查询参数
 * @Author: alan
 * @Date: 2023/8/29 21:44
 */
@Data
@Schema(title = "查询参数对象")
public abstract class AbstractQueryParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -3263921252635611410L;

    @Schema(title = "页码,默认为1")
    private Integer current = CommonConstants.DEFAULT_PAGE_INDEX;
    @Schema(title = "页大小,默认为10")
    private long size = CommonConstants.DEFAULT_PAGE_SIZE;
    @Schema(title = "搜索字符串")
    private String keyword;

    public void setCurrent(Integer current) {
        if (current == null || current <= 0) {
            this.current = CommonConstants.DEFAULT_PAGE_INDEX;
        } else {
            this.current = current;
        }
    }

    public void setSize(Long size) {
        if (size == null || size <= 0) {
            this.size = CommonConstants.DEFAULT_PAGE_SIZE;
        } else {
            this.size = size;
        }
    }


}