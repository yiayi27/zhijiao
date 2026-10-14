package com.medusa.gruul.afs.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * The type Return address vo.
 *
 * @author alan
 */
@Data
@Schema(title = "退货地址信息")
public class ReturnAddressVo {

    /**
     * 联系人
     */
    @Schema(title = "联系人")
    private String name;

    /**
     * 手机
     */
    @Schema(title = "手机")
    @TableField("phone")
    private String phone;

    /**
     * 地址
     */
    @Schema(title = "地址")
    private String address;
}
