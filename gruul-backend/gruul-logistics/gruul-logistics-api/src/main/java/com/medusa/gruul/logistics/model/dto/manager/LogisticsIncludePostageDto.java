package com.medusa.gruul.logistics.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.common.dto.AreaDto;
import com.medusa.gruul.logistics.api.entity.LogisticsIncludePostage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lcy
 */
@Data
@Schema(title = "新增或修改指定包邮运送方式信息DTO")
public class LogisticsIncludePostageDto {

    @Schema(title = "唯一 id, 新增不传,修改传原值 ")
    private Long id;

    @Schema(title = "模板 id,新增运费模板的时候可不传")
    private Long logisticsId;

    @Schema(title = "包邮条件类型: 0= 按件数包邮,1=按重量包邮,2=按金额包邮,3=件数+金额 4=重量+金额 包邮")
    private Integer type;

    @Schema(title = "包邮地区 格式: {\"provinceid\": [\"cityId\",\"cityId2\"]}")
    private List<AreaDto> region;

    @Schema(title = "包邮 件数")
    private Integer pieceNum;

    @Schema(title = "包邮重量 单位: 千克(kg) ")
    private BigDecimal weight;

    @Schema(title = "包邮金额 单位/元")
    private BigDecimal amountNum;

    public LogisticsIncludePostage coverBean() {
        LogisticsIncludePostage logisticsIncludePostage = new LogisticsIncludePostage();
        BeanUtil.copyProperties(this, logisticsIncludePostage);
        logisticsIncludePostage.setRegion(JSONUtil.toJsonStr(this.getRegion()));
        return logisticsIncludePostage;
    }

}
