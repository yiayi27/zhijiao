package com.medusa.gruul.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 区域数据传输类
 * </p>
 *
 * @author alan
 * @since 2020/3/31
 */
@Data
@NoArgsConstructor
public class AreaDto implements Serializable {

    private String label;
    private String value;
    private String fatherid;
    private List<AreaDto> list;
}
