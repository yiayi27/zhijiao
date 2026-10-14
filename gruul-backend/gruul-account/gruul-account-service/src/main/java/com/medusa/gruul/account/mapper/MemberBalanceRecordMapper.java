package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.account.api.entity.MemberBalanceRecord;
import com.medusa.gruul.account.model.vo.MemberBalanceRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 账户余额明细表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-04
 */
public interface MemberBalanceRecordMapper extends BaseMapper<MemberBalanceRecord> {


    /**
     * 账户余额明细表
     *
     * @param memberBalanceRecordVoPage 分页数据
     * @param paramMap                  查询条件参数
     * @return com.medusa.gruul.account.model.entity.MemberBalanceRecord
     */
    IPage<MemberBalanceRecordVo> getMemberBalanceRecord(Page<MemberBalanceRecordVo> memberBalanceRecordVoPage, @Param("paramMap") Map<String, Object> paramMap);

}
