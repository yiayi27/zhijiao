package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.StepNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 返回步数及用户相关数据
 * @Author: xiaoq
 * @Date: 2023/12/17 17:58
 */
@Data
@Schema(title = "用户步数相关数据Dto")
public class StepNumberDto {

    @Schema(title = "用户头像")
    private String userUrl;

    @Schema(title = "用户微信名")
    private String userWxname;

    @Schema(title = "今日步数")
    private Integer todayStep;


    public StepNumber coverStepNumber() {
        StepNumber stepNumber = new StepNumber();
        BeanUtil.copyProperties(this, stepNumber);
        return stepNumber;
    }


}
