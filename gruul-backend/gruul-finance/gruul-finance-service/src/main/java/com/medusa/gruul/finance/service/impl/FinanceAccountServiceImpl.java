package com.medusa.gruul.finance.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.finance.api.entity.FinanceAccount;
import com.medusa.gruul.finance.api.entity.FinanceWithdrawalRecord;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import com.medusa.gruul.finance.api.enums.AuditStatusEnum;
import com.medusa.gruul.finance.mapper.FinanceAccountMapper;
import com.medusa.gruul.finance.mapper.FinanceWithdrawalRecordMapper;
import com.medusa.gruul.finance.model.ApiFinanceAccountVo;
import com.medusa.gruul.finance.service.IFinanceAccountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 资金账户 服务实现类
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Slf4j
@Service
public class FinanceAccountServiceImpl extends ServiceImpl<FinanceAccountMapper, FinanceAccount> implements IFinanceAccountService {

    @Resource
    private FinanceWithdrawalRecordMapper financeWithdrawalRecordMapper;

    /**
     * 获取小程序端个人佣金详情
     *
     * @param accountType the account type
     * @param userId      the user id
     * @return com.medusa.gruul.finance.model.ApiFinanceAccountVo finance account vo
     * @author alan
     * @date 2024/2/22 11:32
     */
    @Override
    public ApiFinanceAccountVo getFinanceAccountVo(AccountTypeEnum accountType, String userId) {
        FinanceAccount account = baseMapper.getByAccount(accountType, userId);
        if (ObjectUtil.isNull(account)) {
            throw new ServiceException(StrUtil.format("当前用户类型{}，用户ID{}无对应的账户", accountType.getDesc(), userId));
        }

        List<FinanceWithdrawalRecord> withdrawalRecordList =
                financeWithdrawalRecordMapper.selectList(new LambdaQueryWrapper<FinanceWithdrawalRecord>()
                        .eq(FinanceWithdrawalRecord::getAccountType, accountType)
                        .eq(FinanceWithdrawalRecord::getUserId, userId)
                        .in(FinanceWithdrawalRecord::getAuditStatus, Arrays.asList(0, 1))
                        .last(CommonConstants.SQL_LIMIT_1));

        List<FinanceWithdrawalRecord> passRecordList =
                financeWithdrawalRecordMapper.selectList(new LambdaQueryWrapper<FinanceWithdrawalRecord>()
                        .eq(FinanceWithdrawalRecord::getAccountType, accountType)
                        .eq(FinanceWithdrawalRecord::getUserId, userId)
                        .eq(FinanceWithdrawalRecord::getAuditStatus, AuditStatusEnum.PASSED));

        ApiFinanceAccountVo vo = ApiFinanceAccountVo.toVo(account,
                passRecordList.stream().map(FinanceWithdrawalRecord::getAmount).reduce(BigDecimal.ZERO,
                        BigDecimal::add));
        if (withdrawalRecordList.isEmpty()) {
            vo.setWaitReviewedBrokerage(BigDecimal.ZERO);
        } else {
            vo.setWaitReviewedBrokerage(withdrawalRecordList.stream().map(FinanceWithdrawalRecord::getAmount).reduce(BigDecimal.ZERO,
                    BigDecimal::add));
        }
        return vo;
    }
}


