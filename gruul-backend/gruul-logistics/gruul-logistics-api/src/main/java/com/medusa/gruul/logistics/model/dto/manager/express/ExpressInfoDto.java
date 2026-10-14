package com.medusa.gruul.logistics.model.dto.manager.express;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 快递接口数据传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-03-13
 */
@Data
public class ExpressInfoDto {

    /**
     * 订单编号，必须唯一
     */
    private String orderId;
    /**
     * 快递公司标识符，如：zt 。
     */
    private String shipperType;
    /**
     * 货品或者包裹名称
     */
    private String tradeName;
    /**
     * 收件人名称
     */
    private String name;
    /**
     * 收件人手机
     */
    private String mobile;
    /**
     * 收件人所在省
     */
    private String province;
    /**
     * 收件人所在市
     */
    private String city;
    /**
     * 收件人所在县/区
     */
    private String district;
    /**
     * 收件人详细地址
     */
    private String address;
    /**
     * 打印任务id
     */
    private String taskId;
    /**
     * 物流单号
     */
    private String deliverySn;
    /**
     * 打印机编号
     */
    private String printCode;

    /**
     * 快递100打印模版id
     */
    private String kdHundredTempId;


}
