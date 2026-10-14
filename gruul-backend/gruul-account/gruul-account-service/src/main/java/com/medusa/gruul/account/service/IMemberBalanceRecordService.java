package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberBalanceRecord;
import com.medusa.gruul.account.api.model.MemberBalanceUpdateDto;
import com.medusa.gruul.account.model.vo.MemberBalanceRecordResVo;
import com.medusa.gruul.order.api.model.OrderVo;

/**
 * <p>
 * 账户余额明细表 服务类
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-04
 */
public interface IMemberBalanceRecordService extends IService<MemberBalanceRecord> {

    /**
     * 获取会员余额明细
     *
     * @param yearMonth    年月
     * @param memberInfoId 会员id
     * @param page         指定页数
     * @param size         数据条数
     * @param dealType     交易类型 0-用户消费(购买商品) 1-微信充值 2-消费返利 3-退款成功 4-商品分销 5-提现
     * @return com.medusa.gruul.account.model.vo.MemberBalanceRecordResVo
     */
    MemberBalanceRecordResVo getMemberBalanceRecord(String yearMonth, String memberInfoId, Integer page, Integer size, Integer dealType);

    /**
     * 会员余额变更
     *
     * @param memberBalanceUpdateDto 扣除余额与余额明细
     * @return 是否成功
     */
    Boolean memberBalanceModify(MemberBalanceUpdateDto memberBalanceUpdateDto);

    /**
     * 订单交易成功，增加用户返利金额
     *
     * @param orderVo 订单信息
     */
    void memberRebatePayed(OrderVo orderVo);

    /**
     * 订单交易成功，结算对应分销员分销金额
     *
     * @param orderVo 订单信息
     */
    void userDistributionCompleted(OrderVo orderVo);

    /**
     * 用户删除指定余额明细id
     *
     * @param id 余额明细id
     */
    void removeMemberBalanceRecord(Long id);
}
