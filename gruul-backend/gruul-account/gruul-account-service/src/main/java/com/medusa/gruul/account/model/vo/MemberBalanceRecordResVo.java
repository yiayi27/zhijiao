package com.medusa.gruul.account.model.vo;

import com.medusa.gruul.common.core.util.PageUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 会员余额明细返回集合
 * @Author zhaokw
 * @Date 00:42 2024\7\4 0007
 **/
@Data
@Schema(title = "会员余额明细返回集合")
@Accessors(chain = true)
public class MemberBalanceRecordResVo extends PageUtils<MemberBalanceRecordVo> {

    /**
     * 支出
     */
    @Schema(title = "支出")
    private BigDecimal outBalance;

    /**
     * 收入
     */
    @Schema(title = "收入")
    private BigDecimal inBalance;

    public MemberBalanceRecordResVo(List<MemberBalanceRecordVo> list, int total, int size, int current, BigDecimal outBalance, BigDecimal inBalance) {
        //显示调用父类构造器
        super(list, total, size, current);
        this.outBalance = outBalance;
        this.inBalance = inBalance;
    }
}
