package com.medusa.gruul.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.finance.api.entity.FinanceAccount;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 资金账户 Mapper 接口
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
public interface FinanceAccountMapper extends BaseMapper<FinanceAccount> {

    /**
     * Gets by account.
     *
     * @param ass the ass
     * @param id  the id
     * @return the by account
     */
    FinanceAccount getByAccount(@Param(value = "type") AccountTypeEnum ass, @Param(value = "id") String id);
}
