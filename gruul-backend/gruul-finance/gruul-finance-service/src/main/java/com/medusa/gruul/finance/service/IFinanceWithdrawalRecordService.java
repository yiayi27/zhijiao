package com.medusa.gruul.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.finance.api.entity.FinanceWithdrawalRecord;
import com.medusa.gruul.finance.api.enums.AccountTypeEnum;
import com.medusa.gruul.finance.model.ApiWithdrawalBrokerageDto;
import com.medusa.gruul.finance.model.ApiWithdrawalRecordDto;
import com.medusa.gruul.finance.model.SearchWithdrawalRecordDto;

import java.util.List;

/**
 * <p>
 * 提现记录 服务类
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
public interface IFinanceWithdrawalRecordService extends IService<FinanceWithdrawalRecord> {
    /**
     * 申请提现
     *
     * @param dto the dto
     * @return void
     * @author alan
     * @date 2024/2/22 11:43
     */
    void withdrawal(ApiWithdrawalBrokerageDto dto);

    /**
     * 分页获取提现记录
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils withdrawal record
     * @author alan
     * @date 2024/2/22 12:37
     */
    PageUtils<FinanceWithdrawalRecord> getWithdrawalRecord(ApiWithdrawalRecordDto dto);

    /**
     * 分页查询提现记录
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2024/2/22 22:25
     */
    PageUtils<FinanceWithdrawalRecord> searchWithdrawalRecordPage(SearchWithdrawalRecordDto dto);

    /**
     * 通过提现审批
     *
     * @param id      the id
     * @param tradeNo the trade no
     * @param channel
     * @return void
     * @author alan
     * @date 2024/2/22 23:27
     */
    void passWithdrawal(Long id, String tradeNo, Integer channel);

    /**
     * 拒绝提现审批
     *
     * @param id     the id
     * @param reason the reason
     * @return void
     * @author alan
     * @date 2024/2/22 23:28
     */
    void rejectWithdrawal(Long id, String reason);

    /**
     * 备注提现审批
     *
     * @param asList the as list
     * @param note   the note
     * @param over   the over
     * @return void
     * @author alan
     * @date 2024/2/23 10:23
     */
    void noteWithdrawal(List<Long> asList, String note, Boolean over);

    /**
     * 获取上一次的提现申请
     *
     * @param accountType the account type
     * @param userId      the user id
     * @return com.medusa.gruul.finance.api.entity.FinanceWithdrawalRecord last withdrawal
     * @Author alan
     * @Date 2024/3/2 20:18
     */
    FinanceWithdrawalRecord getLastWithdrawal(AccountTypeEnum accountType, String userId);


}
