package com.medusa.gruul.finance.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.AccountInfoDto;
import com.medusa.gruul.account.api.model.MemberBalanceUpdateDto;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.RegexConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.LuhnUtil;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.finance.api.entity.FinanceTradeDetail;
import com.medusa.gruul.finance.api.entity.FinanceWithdrawalRecord;
import com.medusa.gruul.finance.api.enums.*;
import com.medusa.gruul.finance.mapper.FinanceTradeDetailMapper;
import com.medusa.gruul.finance.mapper.FinanceWithdrawalRecordMapper;
import com.medusa.gruul.finance.model.ApiWithdrawalBrokerageDto;
import com.medusa.gruul.finance.model.ApiWithdrawalRecordDto;
import com.medusa.gruul.finance.model.SearchWithdrawalRecordDto;
import com.medusa.gruul.finance.service.IFinanceWithdrawalRecordService;
import com.medusa.gruul.finance.util.GenSerialNumberUtil;
import com.medusa.gruul.payment.api.entity.EntPay;
import com.medusa.gruul.payment.api.enums.CheckNameEnum;
import com.medusa.gruul.payment.api.enums.PayChannelEnum;
import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.payment.api.model.param.EntPayReQuestParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 提现记录 服务实现类
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
@Slf4j
@Service
public class FinanceWithdrawalRecordServiceImpl extends ServiceImpl<FinanceWithdrawalRecordMapper,
        FinanceWithdrawalRecord> implements IFinanceWithdrawalRecordService {
    @Autowired
    private FinanceTradeDetailMapper financeTradeDetailMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RemotePaymentService remotePaymentService;
    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;

    /**
     * 申请提现
     *
     * @param dto 提现记录信息
     */
    @Override
    public void withdrawal(ApiWithdrawalBrokerageDto dto) {
        String openId = "";
        String userName = "";
        String userAvatarUrl = "";
        String phone = "";
        SecureUser<?> curUser = ISecurity.userMust();
        openId = curUser.getOpenid();
        userName = curUser.getNickname();
        userAvatarUrl = curUser.getAvatar();
        phone = dto.getPhone();
        //判断入参
        if (dto.getChannel() != ChannelTypeEnum.WECAHT) {
            if (StrUtil.isBlank(dto.getAccount())) {
                throw new ServiceException("账号不能为空");
            }
            if (StrUtil.isBlank(dto.getName())) {
                throw new ServiceException("姓名不能为空");
            }
        }
        if (dto.getChannel() == ChannelTypeEnum.BANK) {
            if (StrUtil.isBlank(dto.getBankName())) {
                throw new ServiceException("开户行不能为空");
            }
            if (!LuhnUtil.check(dto.getAccount())) {
                throw new ServiceException("银行卡账号格式错误");
            }
        }
        if (dto.getChannel() == ChannelTypeEnum.ALIPAY) {
            if (!ReUtil.isMatch(RegexConstants.REGEX_MOBILE_SIMPLE, dto.getAccount()) && !ReUtil.isMatch(RegexConstants.REGEX_EMAIL, dto.getAccount())) {
                throw new ServiceException("支付宝账号格式错误");
            }
        }
        if (NumberUtil.isLess(dto.getAmount(), new BigDecimal("0.3"))) {
            throw new ServiceException("单次最少满0.3元才可提现");
        }

        //判断是否有待审批的记录
        long audits = baseMapper.selectCount(new LambdaQueryWrapper<FinanceWithdrawalRecord>()
                .eq(FinanceWithdrawalRecord::getAccountType, dto.getAccountType())
                .eq(FinanceWithdrawalRecord::getUserId, dto.getUserId())
                .in(FinanceWithdrawalRecord::getAuditStatus, Arrays.asList(0, 1))
        );
        if (audits > 0) {
            throw new ServiceException("当前已经有待审批的提现申请，不能重复申请");
        }
        List<Integer> list = new ArrayList<>(CommonConstants.NUMBER_ONE);
        list.add(CommonConstants.NUMBER_TWO);
        AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(dto.getUserId(), list);
        //判断账户是否有足够的余额
        if (NumberUtil.isGreater(dto.getAmount(), accountInfoDto.getMiniAccountExtends().getSupplyBonus())) {
            throw new ServiceException("申请提现金额已经超过可提现余额");
        }
        //写入记录
        FinanceWithdrawalRecord record = new FinanceWithdrawalRecord();
        record.setUserId(dto.getUserId());
        record.setOpenId(openId);
        record.setUserName(userName);
        record.setUserAvatarUrl(userAvatarUrl);
        record.setPhone(phone);
        record.setAccountType(dto.getAccountType());
        record.setAmount(dto.getAmount());
        record.setChannel(dto.getChannel());
        if (dto.getChannel().equals(ChannelTypeEnum.WECAHT)) {
            record.setName(userName);
        } else {
            record.setName(dto.getName());
        }
        record.setAccount(dto.getAccount());
        record.setBankName(dto.getBankName());
        record.setAuditStatus(AuditStatusEnum.PENDING_MAIN);
        record.setCommission(BigDecimal.ZERO);
        record.setSn(
                GenSerialNumberUtil.initGenSerialNumber(GenSerialNumberUtil.WITHDRAWAL, ISystem.shopId())
                        .nextId()
        );
        baseMapper.insert(record);
    }

    /**
     * 分页查询提现记录
     *
     * @param dto 提现申请查询信息
     * @return 提现记录分页信息
     */
    @Override
    public PageUtils<FinanceWithdrawalRecord> searchWithdrawalRecordPage(SearchWithdrawalRecordDto dto) {
        if (StringUtils.isBlank(dto.getStartTime())) {
            dto.setStartTime(DateUtil.beginOfMonth(new DateTime()).toString());
        }
        if (StringUtils.isBlank(dto.getEndTime())) {
            dto.setEndTime(DateUtil.endOfMonth(new DateTime()).toString());
        } else {
            dto.setEndTime(dto.getEndTime() + " 23:59:59");
        }
        IPage<FinanceWithdrawalRecord> page = baseMapper.searchWithdrawalRecordPage(new Page<FinanceWithdrawalRecord>(dto.getCurrent(),
                dto.getSize()), dto);
        return new PageUtils<>(page);
    }

    /**
     * 通过提现审批
     *
     * @param id      提现记录id
     * @param tradeNo 交易号码
     * @param channel 提现渠道
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passWithdrawal(Long id, String tradeNo, Integer channel) {
        FinanceWithdrawalRecord record = baseMapper.selectById(id);
        // 增加分布式锁
        RLock rLock = redissonClient.getLock(id.toString());
        rLock.lock();
        try {
            if (record.getAuditStatus() == AuditStatusEnum.PENDING_MAIN) {
                if (ChannelTypeEnum.WECAHT.getCode() == channel) {
                    EntPayReQuestParam param = new EntPayReQuestParam();
                    param.setPayChannel(PayChannelEnum.WX);
                    param.setAmount(NumberUtil.mul(record.getAmount(), 100).intValue());
                    param.setDescription("佣金提现");
                    param.setOpenid(record.getOpenId());
                    param.setCheckName(CheckNameEnum.NO_CHECK);
                    param.setReUserName(record.getUserName());
                    param.setSpbillCreateIp(NetUtil.getLocalhostStr());
                    EntPay entPay;
                    try {
                        log.warn(String.format("调用企业付款接口,入参[%s]", param.md5().toString()));
                        entPay = remotePaymentService.payRequest(param.md5());
                        log.warn(String.format("调用企业付款接口,返回值[%s]", entPay.toString()));
                    } catch (Exception e) {
                        throw new ServiceException("微信付款调用失败,请稍后再试");
                    }
                    if (ObjectUtil.isNotNull(entPay) && CommonConstants.STRING_TWO.equalsIgnoreCase(entPay.getTradeStatus())) {
                        tradeNo = entPay.getTransactionId();
                    } else {
                        throw new ServiceException("微信付款失败,请人工查单后再试");
                    }
                } else {
                    record.setChannel(ChannelTypeEnum.OFFLINE);
                }
                record.setAuditStatus(AuditStatusEnum.PASSED);
                record.setArriveTime(LocalDateTime.now());
                record.setTradeNo(tradeNo);
                baseMapper.updateById(record);
                //新增用户余额提现记录
                FinanceTradeDetail financeTradeDetail = new FinanceTradeDetail();
                financeTradeDetail.setTradeNo(tradeNo);
                financeTradeDetail.setUserId(record.getUserId());
                financeTradeDetail.setUserName(record.getUserName());
                financeTradeDetail.setUserAvatarUrl(record.getUserAvatarUrl());
                financeTradeDetail.setType(TradeTypeEnum.DISTRIBUTOR_FOR_USER);
                financeTradeDetail.setAmount(record.getAmount());
                financeTradeDetail.setSourceId(record.getId());
                financeTradeDetail.setCapitalFlows(FlowTypeEnum.OUT);
                financeTradeDetail.setSn(record.getSn());
                int insert = financeTradeDetailMapper.insert(financeTradeDetail);
                if (insert == 0) {
                    throw new ServiceException("对账单提现记录新增失败!", SystemCode.DATA_ADD_FAILED.getCode());

                }
                //更新用户余额
                MemberBalanceUpdateDto memberBalanceUpdateDto = new MemberBalanceUpdateDto();
                memberBalanceUpdateDto
                        .setChangeType(CommonConstants.NUMBER_ONE)
                        .setDealType(CommonConstants.NUMBER_FIVE)
                        .setDetailName("提现")
                        .setShopUserId(record.getUserId())
                        .setSource(CommonConstants.NUMBER_ONE)
                        .setUseBalance(record.getAmount());
                Boolean changeRecord = remoteMiniAccountService.memberBalanceModify(memberBalanceUpdateDto);
                if (!changeRecord) {
                    throw new ServiceException("用户余额更新失败！");
                }
            }
        } finally {
            //释放锁
            rLock.unlock();
        }
    }

    /**
     * 拒绝提现审批
     *
     * @param id     提现记录id
     * @param reason 原因
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectWithdrawal(Long id, String reason) {
        FinanceWithdrawalRecord record = baseMapper.selectById(id);
        if (record.getAuditStatus() == AuditStatusEnum.PENDING_MAIN) {
            record.setAuditStatus(AuditStatusEnum.REJECTED);
            record.setReason(reason);
            baseMapper.updateById(record);
        }
    }

    /**
     * 备注提现审批
     *
     * @param ids    提现信息id集合
     * @param note   备注信息
     * @param isOver 是否完结
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void noteWithdrawal(List<Long> ids, String note, Boolean isOver) {
        List<FinanceWithdrawalRecord> records = baseMapper.selectByIds(ids);
        for (FinanceWithdrawalRecord record : records) {
            if (StrUtil.isBlank(note)) {
                note = StrUtil.EMPTY;
            }
            if (!isOver && StrUtil.isNotBlank(record.getNote())) {
                record.setNote(record.getNote() + StrUtil.CRLF + note);
            } else {
                record.setNote(note);
            }
            baseMapper.updateById(record);
        }
    }


    /**
     * 分页获取提现记录
     *
     * @param dto 提现记录查询信息
     * @return 提现记录分页信息
     */
    @Override
    public PageUtils<FinanceWithdrawalRecord> getWithdrawalRecord(ApiWithdrawalRecordDto dto) {
        IPage<FinanceWithdrawalRecord> page = baseMapper.getWithdrawalRecordPage(new Page<FinanceWithdrawalRecord>(dto.getCurrent(),
                dto.getSize()), dto);
        return new PageUtils<>(page);
    }

    /**
     * 获取上一次的提现申请
     *
     * @param accountType 账户类型
     * @param userId      用户id
     * @return 提现记录
     */
    @Override
    public FinanceWithdrawalRecord getLastWithdrawal(AccountTypeEnum accountType, String userId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<FinanceWithdrawalRecord>()
                .eq(FinanceWithdrawalRecord::getAccountType, accountType)
                .eq(FinanceWithdrawalRecord::getUserId, userId)
                .orderByDesc(FinanceWithdrawalRecord::getId)
                .last(CommonConstants.SQL_LIMIT_1)
        );
    }


}
