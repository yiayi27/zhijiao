package com.medusa.gruul.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.finance.api.entity.FinanceAccount;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import com.medusa.gruul.finance.model.ApiFinanceAccountVo;

/**
 * <p>
 * 资金账户 服务类
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
public interface IFinanceAccountService extends IService<FinanceAccount> {
    /**
     * 获取小程序端个人佣金详情
     *
     * @param accountType the account type
     * @param userId      the user id
     * @return com.medusa.gruul.finance.model.ApiFinanceAccountVo finance account vo
     * @author alan
     * @date 2024/2/22 11:32
     */
    ApiFinanceAccountVo getFinanceAccountVo(AccountTypeEnum accountType, String userId);
}
