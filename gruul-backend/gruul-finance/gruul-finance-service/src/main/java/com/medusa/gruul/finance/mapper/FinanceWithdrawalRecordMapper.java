package com.medusa.gruul.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.finance.api.entity.FinanceWithdrawalRecord;
import com.medusa.gruul.finance.model.ApiWithdrawalRecordDto;
import com.medusa.gruul.finance.model.SearchWithdrawalRecordByAreaAssDto;
import com.medusa.gruul.finance.model.SearchWithdrawalRecordDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 提现记录 Mapper 接口
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
public interface FinanceWithdrawalRecordMapper extends BaseMapper<FinanceWithdrawalRecord> {

    /**
     * Gets withdrawal record page.
     *
     * @param page the page
     * @param dto  the dto
     * @return the withdrawal record page
     */
    IPage<FinanceWithdrawalRecord> getWithdrawalRecordPage(Page page, @Param(value = "dto") ApiWithdrawalRecordDto dto);

    /**
     * Search withdrawal record page page.
     *
     * @param page the page
     * @param dto  the dto
     * @return the page
     */
    IPage<FinanceWithdrawalRecord> searchWithdrawalRecordPage(Page page,
                                                              @Param(value = "dto") SearchWithdrawalRecordDto dto);

    /**
     * Search withdrawal record page by area ass page.
     *
     * @param page the page
     * @param dto  the dto
     * @return the page
     */
    IPage<FinanceWithdrawalRecord> searchWithdrawalRecordPageByAreaAss(Page page,
                                                                       @Param(value = "dto") SearchWithdrawalRecordByAreaAssDto dto);
}
