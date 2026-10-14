package com.medusa.gruul.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The type Order brokerage vo.
 * <p>
 * 团长待结算的佣金明细
 *
 * @author alan
 * @date 2023/11/13 20:20
 */
@Data
@Schema(title = "团长待结算的佣金明细")
public class OrderBrokerageVo extends SimpleOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预计到账时间
     */
    @Schema(title = "预计到账时间")
    private String estimateTime;
    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    private String userAvatarUrl;

    /**
     * 支付时间
     */
    @Schema(title = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime payTime;


}
